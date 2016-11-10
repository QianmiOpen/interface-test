package com.qianmi.tda.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取spring上下文工具类
 * Created by aqlu on 15/12/2.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Bean名称获取实例
     *
     * @return bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T)applicationContext.getBean(name);
    }

    /**
     * 根据类型获取实例
     *
     * @param type 类型
     * @return bean实例
     */
    public static <T> T getBean(Class<T> type) throws BeansException {
        return applicationContext.getBean(type);
    }
}
