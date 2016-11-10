package com.qianmi.tda.exec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import com.qianmi.tda.bean.*;
import com.qianmi.tda.util.EvalUtil;
import com.qianmi.tda.util.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TestRunner
 * Created by aqlu on 2016/10/29.
 */
@Component
@Slf4j
public class DubboTestRunner {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DubboExecutor dubboExecutor;

    public AggTestResult run(TestSuit testSuit) {
        String intfName = testSuit.getIntfName();
        String dubboServiceUrl = testSuit.getDubboServiceURL();


        List<TestResult> testResults = testSuit.getTestCases().stream()
                .map(testCase -> run(testCase, testSuit.getTestServerURL(), intfName, dubboServiceUrl))
                .collect(Collectors.toList());

        AggTestResult aggTestResult = new AggTestResult(intfName, Tools.formatDateTimeMills(new Date()));
        aggTestResult.addTestCases(testResults);

        return aggTestResult;
    }

    private TestResult run(TestCase testCase, String testServerUrl, String intfName, String dubboServiceUrl) {
        log.debug("开始执行:{}", intfName);
        long begin = System.currentTimeMillis();
        TestResult testResult = TestResult.builder().name(testCase.getName()).date(Tools.formatDateTimeMills(new Date())).build();

        try {
            DubboRequest dubboRequest = DubboRequest.builder().intfName(intfName)
                    .dubboServiceURL(dubboServiceUrl)
                    .testServerURL(testServerUrl)
                    .params(testCase.getParams())
                    .build();

            String msg = dubboExecutor.exec(dubboRequest);
            testResult.setOriginResult(msg);

            List<TestResult.FailMsg> failMsgs = testCase.getExpects().stream().map(expect -> {
                String path = expect.getPath();
                String operator = expect.getOperator();
                Object expectValue = expect.getValue();
                Object actualObj = null;

                DocumentContext result = null;
                try {
                    result = JsonPath.parse(msg);
                    actualObj = result.read(path, Object.class);
                } catch (InvalidJsonException e) {
                    // 如果解析失败，说明返回结果不是一个合法的json对象
                    actualObj = msg;
                } catch (Exception e) {
                    log.debug("转换接口执行结果失败, exMsg:{}", e.getMessage());
                    try {
                        actualObj = objectMapper.readValue(msg, Object.class);
                    } catch (IOException e1) {
                        actualObj = msg;
                    }
                }

                if (EvalUtil.eval(expectValue, actualObj, operator)) {
                    return null;
                } else {
                    try {
                        return new TestResult.FailMsg(actualObj == null ? "null" : objectMapper.writeValueAsString(actualObj),
                                expectValue == null ? "null" : objectMapper.writeValueAsString(expectValue), operator);
                    } catch (JsonProcessingException e) {
                        log.debug("json转换异常，actualValue:{}, expectValue:{}", actualObj, expectValue);
                        return new TestResult.FailMsg(String.valueOf(objectMapper), String.valueOf(expectValue), operator);
                    }
                }
            }).filter(failMsg -> failMsg != null).collect(Collectors.toList());

            if (failMsgs.isEmpty()) {
                // 所有断言都验证通过
                testResult.setStatus(TestResult.PASSED);
            } else {
                testResult.setStatus(TestResult.FAILED);
                testResult.addFailMsgs(failMsgs);
            }
        } catch (Exception e) {
            log.info("执行用例失败. testSuit: {}, testCase:{}", intfName, testCase, e);
            testResult.setStatus(TestResult.ERROR);
            testResult.setException(Tools.getStackTrace(e));
        } finally {
            testResult.setCostMills(System.currentTimeMillis() - begin);
        }

        log.debug("执行结束，methodName:{}，testResult:{}", intfName, testResult);
        return testResult;
    }
}
