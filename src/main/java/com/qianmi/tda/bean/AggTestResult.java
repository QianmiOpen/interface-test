package com.qianmi.tda.bean;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AggTestCase
 * Created by aqlu on 2016/10/27.
 */
@Data
public class AggTestResult {

    private String name;

    private String date;

    @Builder
    public AggTestResult(String name, String date) {
        this.name = name;
        this.date = date;
    }

    @Getter
    private List<TestResult> testResultList = new ArrayList<>();

    public AggTestResult addTestCases(List<TestResult> testResults) {
        this.testResultList.addAll(testResults);
        return this;
    }

    public long getFailures() {
        return this.testResultList.stream().filter(testResultResult -> testResultResult.getStatus().equals(TestResult.FAILED)).count();
    }

    public long getPassed() {
        return this.testResultList.stream().filter(testResultResult -> testResultResult.getStatus().equals(TestResult.PASSED)).count();
    }

    public long getErrors() {
        return this.testResultList.stream().filter(testResultResult -> testResultResult.getStatus().equals(TestResult.ERROR)).count();
    }

    public long getTotal() {
        return this.testResultList.size();
    }

    public long getTotalTime() {
        return this.testResultList.stream().mapToLong(TestResult::getCostMills).sum();
    }

    public String getStatus() {
        if (getErrors() > 0) {
            return TestResult.ERROR;
        } else if (getFailures() > 0) {
            return TestResult.FAILED;
        } else {
            return TestResult.PASSED;
        }
    }
}
