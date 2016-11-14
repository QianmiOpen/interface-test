package com.qianmi.tda.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * TestSuit
 * Created by aqlu on 2016/10/28.
 */
@Data
public class TestSuit implements Comparable<TestSuit> {

    private String name;

    private String intfName;

    private String testServerURL;

    private String dubboServiceURL;

    private Long execOrder = 1L;

    private List<TestCase> testCases = new ArrayList<>();

    @Override
    public int compareTo(TestSuit testSuit) {
        if (this.execOrder.compareTo(testSuit.getExecOrder()) == 0 && this.intfName != null && testSuit.getIntfName() != null) {
            return this.intfName.compareTo(testSuit.getIntfName());
        } else {
            return this.execOrder.compareTo(testSuit.getExecOrder());
        }
    }
}
