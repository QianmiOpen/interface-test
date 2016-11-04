package com.qianmi.tda.exec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.qianmi.tda.bean.AggTestResult;
import com.qianmi.tda.bean.TestCase;
import com.qianmi.tda.bean.TestResult;
import com.qianmi.tda.bean.TestSuit;
import com.qianmi.tda.util.EvalUtil;
import com.qianmi.tda.util.Tools;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TestRunner
 * Created by aqlu on 2016/10/29.
 */
@Component
@Slf4j
public class DubboTestRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${default-test-server-url:http://127.0.0.1:8080/executeTest.do}")
    @Setter
    private String defaultTestServerUrl;

    public AggTestResult run(TestSuit testSuit) {
        String methodName = testSuit.getIntfName();
        String dubboServiceUrl = testSuit.getDubboServiceURL();
        String testServerURL = StringUtils.hasText(testSuit.getTestServerURL()) ? testSuit.getTestServerURL() : defaultTestServerUrl;

        List<TestResult> testResults = testSuit.getTestCases().stream()
                .map(testCase -> run(testCase, testServerURL, methodName, dubboServiceUrl))
                .collect(Collectors.toList());

        AggTestResult aggTestResult = new AggTestResult(methodName, Tools.formatDateTimeMills(new Date()));
        aggTestResult.addTestCases(testResults);

        return aggTestResult;
    }

    private TestResult run(TestCase testCase, String testServerUrl, String methodName, String dubboServiceUrl) {
        log.debug("开始执行:{}", methodName);
        long begin = System.currentTimeMillis();
        TestResult testResult = TestResult.builder().name(testCase.getName()).date(Tools.formatDateTimeMills(new Date())).build();

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();

        paramMap.add("methodName", methodName);
        if (StringUtils.hasText(dubboServiceUrl)) {
            paramMap.add("serviceUrl", dubboServiceUrl);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(paramMap, headers);
            paramMap.add("params", objectMapper.writeValueAsString(testCase.getParams()));

            ResponseEntity<String> responseEntity = restTemplate.exchange(testServerUrl, HttpMethod.POST, requestEntity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String response = responseEntity.getBody();
                if (StringUtils.hasText(response)) {
                    DocumentContext json = JsonPath.parse(response);
                    Boolean success = json.read("$.success", Boolean.class);
                    String msg = json.read("$.msg", String.class);

                    if (success) {
                        testResult.setOriginResult(msg);

                        List<TestResult.FailMsg> failMsgs = testCase.getExpects().stream().map(expect -> {
                            String path = expect.getPath();
                            String operator = expect.getOperator();
                            Object expectValue = expect.getValue();
                            Object actualObj = null;
                            String actualValue = json.read(path.replaceFirst("\\$", "\\$.msg"), String.class);
                            try {
                                actualObj = objectMapper.readValue(actualValue, Object.class);
                            } catch (IOException e) {
                                log.debug("转换接口执行结果失败, exMsg:{}", e.getMessage());
                                actualObj = actualValue;
                            }

                            if (EvalUtil.eval(expectValue, actualObj, operator)) {
                                return null;
                            } else {
                                try {
                                    return new TestResult.FailMsg(actualValue == null ? "null" : objectMapper.writeValueAsString(actualValue),
                                            expectValue == null ? "null" : objectMapper.writeValueAsString(expectValue), operator);
                                } catch (JsonProcessingException e) {
                                    log.debug("json转换异常，actualValue:{}, expectValue:{}", actualValue, expectValue);
                                    return new TestResult.FailMsg(String.valueOf(objectMapper), String.valueOf(expectValue), operator);
                                }
                            }
                        }).filter(failMsg -> failMsg != null).collect(Collectors.toList());

                        if (failMsgs.isEmpty()) {
                            testResult.setStatus(TestResult.PASSED);
                        } else {
                            testResult.setStatus(TestResult.FAILED);
                            testResult.addFailMsgs(failMsgs);
                        }

                    } else {
                        testResult.setStatus(TestResult.ERROR);
                        testResult.setException(msg);
                    }

                } else {
                    testResult.setStatus(TestResult.ERROR);
                    testResult.setException("未获取到接口执行数据");
                }
            } else {
                testResult.setStatus(TestResult.ERROR);
                testResult.setException("未获取到接口执行数据");
            }

        } catch (MappingException ex) {
            testResult.setStatus(TestResult.ERROR);
            testResult.setException("根据'{}'无法到获取值");
        } catch (JsonProcessingException e) {
            log.info("解析参数失败. testSuit: {}, testCase:{}", methodName, testCase, e);
            testResult.setStatus(TestResult.ERROR);
            testResult.setException(Tools.getStackTrace(e));
        } catch (Exception e) {
            log.info("执行用例失败. testSuit: {}, testCase:{}", methodName, testCase);
            testResult.setStatus(TestResult.ERROR);
            testResult.setException(Tools.getStackTrace(e));
        } finally {
            testResult.setCostMills(System.currentTimeMillis() - begin);
        }

        log.debug("执行结束，methodName:{}，testResult:{}", methodName, testResult);
        return testResult;
    }
}
