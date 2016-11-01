package com.qianmi.tda.report;

import com.qianmi.tda.bean.AggTestResult;
import com.qianmi.tda.bean.TestResult;
import com.qianmi.tda.util.Tools;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * HtmlReportGeneratorTest
 * Created by aqlu on 2016/11/1.
 */
public class HtmlReportGeneratorTest {
    @Test
    public void generate() throws Exception {

        List<AggTestResult> aggTestResultList = new ArrayList<>();

        TestResult testResult1 = TestResult.builder().name("case1").costMills(10).status(TestResult.PASSED).date(Tools.formatDateTimeMills(new Date())).build();
        TestResult testResult2 = TestResult.builder().name("case2").costMills(8).status(TestResult.PASSED).date(Tools.formatDateTimeMills(new Date())).build();

        TestResult testResult3 = TestResult.builder().name("case3").costMills(5).status(TestResult.FAILED).date(Tools.formatDateTimeMills(new Date())).build().addFailMsgs(Collections.singletonList(new TestResult.FailMsg(2, 1, "equals")));
        TestResult testResult4 = TestResult.builder().name("case4").costMills(12).status(TestResult.ERROR).date(Tools.formatDateTimeMills(new Date())).exception(Tools.getStackTrace(new RuntimeException("人为触发异常")).replaceAll("\n", "<br/>")).build();
        AggTestResult aggTestResult1 = AggTestResult.builder().name("com.qianmi.pc.api.app.product.ProductCountQueryProvider:1.0.0@getStockCount").date(Tools.formatDateTimeMills(new Date())).build().addTestCases(Arrays.asList(testResult1, testResult2, testResult3, testResult4));
        AggTestResult aggTestResult2 = AggTestResult.builder().name("com.qianmi.pc.api.op.item.OpGoodsProvider:1.0.0@batchModifyStock").date(Tools.formatDateTimeMills(new Date())).build().addTestCases(Arrays.asList(testResult1, testResult3));

        aggTestResultList.add(aggTestResult1);
        aggTestResultList.add(aggTestResult2);
    }

}