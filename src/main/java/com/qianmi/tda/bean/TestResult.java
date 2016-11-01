package com.qianmi.tda.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TestCase
 * Created by aqlu on 2016/10/27.
 */
@Data
public class TestResult {
    public static String PASSED = "passed";
    public static String FAILED = "failed";
    public static String ERROR = "error";

    private String name;

    private String status; //Success or Failed or Error

    private long costMills;

    private String date;

    private String exception;

    private String originResult;

    @Builder
    public TestResult(String name, String status, long costMills, String date, String exception){
        this.name = name;
        this.status = status;
        this.costMills = costMills;
        this.date = date;
        this.exception = exception;
    }

    private List<FailMsg> failMsgList = new ArrayList<>();

    public TestResult addFailMsgs(List<FailMsg>failMsgs){
        this.failMsgList.addAll(failMsgs);
        return this;
    }

    @Data
    @AllArgsConstructor
    public static class FailMsg{
        private Object actual;

        private Object expect;

        private String operator;
    }
}
