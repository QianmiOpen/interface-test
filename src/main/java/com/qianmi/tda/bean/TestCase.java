package com.qianmi.tda.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * TestCase
 * Created by aqlu on 2016/10/28.
 */
@Data
public class TestCase {
    private String name;

    private List<Object> params = new ArrayList<>();

    private List<Expect> expects = new ArrayList<>();

    @SuppressWarnings("WeakerAccess")
    @Data
    public static class Expect {
        private String path;

        private Object value;

        /**
         * 运算符；equals、gt、gte、lt、lte、contains、match
         */
        private String operator;
    }
}
