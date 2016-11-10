package com.qianmi.tda.bean;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DubboRequest
 * Created by aqlu on 2016/11/10.
 */
@SuppressWarnings("unused")
@Data
@Getter
@Setter
public class DubboRequest {

    private String intfName;

    private String testServerURL;

    private String dubboServiceURL;

    private List<Object> params = new ArrayList<>();

    public DubboRequest(){

    }

    @Builder
    public DubboRequest(String intfName, String testServerURL, String dubboServiceURL, List<Object> params){
        this.intfName = intfName;
        this.testServerURL = testServerURL;
        this.dubboServiceURL = dubboServiceURL;
        this.params = params;
    }
}
