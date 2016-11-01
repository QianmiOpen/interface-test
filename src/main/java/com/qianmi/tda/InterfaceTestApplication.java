package com.qianmi.tda;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.qianmi.tda.bean.AggTestResult;
import com.qianmi.tda.bean.TestCase;
import com.qianmi.tda.bean.TestSuit;
import com.qianmi.tda.exec.DubboTestRunner;
import com.qianmi.tda.report.HtmlReportGenerator;
import com.qianmi.tda.util.Tools;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * InterfaceTestApplication
 * Created by aqlu on 2016/10/28.
 */
@SpringBootApplication
public class InterfaceTestApplication implements InitializingBean, CommandLineRunner{

    @Autowired
    private DubboTestRunner dubboTestRunner;

    @Autowired
    private HtmlReportGenerator htmlReportGenerator;

    @Autowired
    private TestCaseLoader testCaseLoader;

    @Bean
    public Template htmlTemplate() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        cfg.setClassForTemplateLoading(InterfaceTestApplication.class, "/ftl/");
        return cfg.getTemplate("html-report.ftl");
    }

    @SuppressWarnings("WeakerAccess")
    public void init(){
        // jsonpath配置初始化
        com.jayway.jsonpath.Configuration.setDefaults(new com.jayway.jsonpath.Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(InterfaceTestApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public void run(String... args) throws Exception {
        String reportTitle = "接口测试报告——" + Tools.formatDateTimeMills(new Date());

        // 加载测试套
        List<TestSuit> testSuits = testCaseLoader.load();
        Collections.sort(testSuits); // 排序

        List<AggTestResult> aggTestResults =  testSuits.stream().map(dubboTestRunner::run).collect(Collectors.toList());

        htmlReportGenerator.generate(reportTitle, aggTestResults);

    }
}
