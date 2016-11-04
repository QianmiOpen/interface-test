package com.qianmi.tda.config;

import lombok.Setter;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * HttpClientConfiguration
 * Created by aqlu on 2016/10/27.
 */
@Configuration
public class HttpClientConfiguration {
    /**
     * 整个连接池的并发最大数
     */
    @Value("${http.client.connection-manager-max-total:1000}")
    @Setter
    private int connectionManagerMaxTotal = 1000;

    /**
     * 每个主机的并发最大数
     */
    @Value("${http.client.connection-manager-default-max-per-route:1000}")
    @Setter
    private int connectionManagerDefaultMaxPerRoute = 1000;

    /**
     * Request连接超时时间，单位毫秒；
     */
    @Setter
    @Value("${http.client.request-connect-timeout:2000}")
    private int requestConnectTimeout = 2000;

    /**
     * Request读取响应结果超时时间，单位毫秒
     */
    @Value("${http.client.request-read-timeout:10000}")
    @Setter
    private int requestReadTimeout = 10000;

    @Bean(name = "defaultHttpClientConnectionManager")
    @ConditionalOnMissingBean
    public HttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager();
        clientConnectionManager.setMaxTotal(connectionManagerMaxTotal); // 整个连接池的并发
        clientConnectionManager.setDefaultMaxPerRoute(connectionManagerDefaultMaxPerRoute); // 每个主机的并发
        return clientConnectionManager;
    }

    @Bean(name = "defaultHttpClient")
    @ConditionalOnMissingBean
    public HttpClient httpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(httpClientConnectionManager());

        return builder.build();
    }

    @Bean(name = "defaultClientHttpRequestFactory")
    @ConditionalOnMissingBean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                httpClient());
        clientHttpRequestFactory.setConnectTimeout(requestConnectTimeout);
        clientHttpRequestFactory.setReadTimeout(requestReadTimeout);
        return clientHttpRequestFactory;
    }

    @Bean(name = "defaultRestTemplate")
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }
}
