package com.qianmi.tda.util;

import com.qianmi.tda.exec.DubboExecutor;

import java.io.IOException;

/**
 * DubboExecutorUtil
 * Created by aqlu on 2016/11/10.
 */
@SuppressWarnings("unused")
public class DubboExecutorUtil {

    public static String exec(String request) throws IOException {
        DubboExecutor dubboExecutor = SpringContextHolder.getBean(DubboExecutor.class);
        return dubboExecutor.exec(request);
    }
}
