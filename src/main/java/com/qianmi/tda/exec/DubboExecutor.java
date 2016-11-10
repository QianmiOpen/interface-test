package com.qianmi.tda.exec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.qianmi.tda.bean.DubboRequest;
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

/**
 * DubboExecutor
 * Created by aqlu on 2016/11/10.
 */
@Component
@Slf4j
public class DubboExecutor {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${default-test-server-url:http://127.0.0.1:8080/executeTest.do}")
    @Setter
    private String defaultTestServerUrl;


    public String exec(String request) throws IOException {
        return exec(objectMapper.readValue(request, DubboRequest.class));
    }


    public String exec(DubboRequest request) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();

        paramMap.add("methodName", request.getIntfName());
        if (StringUtils.hasText(request.getDubboServiceURL())) {
            paramMap.add("serviceUrl", request.getDubboServiceURL());
        }

        String testServerURL = StringUtils.hasText(request.getTestServerURL()) ? request.getTestServerURL() : defaultTestServerUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(paramMap, headers);
        try {
            paramMap.add("params", objectMapper.writeValueAsString(request.getParams()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("参数解析失败，格式不合法");
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(testServerURL, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String response = responseEntity.getBody();

            if (!StringUtils.hasText(response)) {
                throw new RuntimeException("未获取到接口执行数据");
            }

            DocumentContext json = JsonPath.parse(response);
            Boolean success = json.read("$.success", Boolean.class);
            String msg = json.read("$.msg", String.class);

            if (!success) {
                throw new RuntimeException(msg);
            }

            return msg;
        }else{
            throw new RuntimeException("执行请求失败， statusCode:" + responseEntity.getStatusCode());
        }
    }
}