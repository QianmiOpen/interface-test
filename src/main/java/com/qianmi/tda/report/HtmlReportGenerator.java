package com.qianmi.tda.report;

import com.qianmi.tda.bean.AggTestResult;
import com.qianmi.tda.util.Tools;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HtmlReportGenerator
 * Created by aqlu on 2016/11/1.
 */
@Component
@Slf4j
public class HtmlReportGenerator {

    @Value("${test-reports-home:${user.dir}/reports}")
    @Setter
    private File testReportsHome;

    @Autowired
    private Template htmlTemplate;

    public String generate(String reportTitle, List<AggTestResult> aggTestResultList) throws IOException {
        long totalCases = aggTestResultList.stream().mapToLong(AggTestResult::getTotal).sum();
        long passedCases = aggTestResultList.stream().mapToLong(AggTestResult::getPassed).sum();
        long errorCases = aggTestResultList.stream().mapToLong(AggTestResult::getErrors).sum();
        long failedCases = aggTestResultList.stream().mapToLong(AggTestResult::getFailures).sum();
        long totalCost = aggTestResultList.stream().mapToLong(AggTestResult::getTotalTime).sum();

        Map<String, Object> params = new HashMap<>();
        params.put("reportTitle", reportTitle);
        params.put("reportTotalCost", totalCost);
        params.put("reportTotalCases", totalCases);
        params.put("reportPassedCases", passedCases);
        params.put("reportFailedCases", failedCases);
        params.put("reportErrorCases", errorCases);
        params.put("reportDate", Tools.formatDateTimeMills(new Date()));
        params.put("aggTestResultList", aggTestResultList);

        log.debug("生成报告参数:{}", params);

        if (!testReportsHome.exists()) {
            Files.createDirectories(Paths.get(testReportsHome.getPath()));
        }

        String htmlReportPath = String.format("%s/html-report-%s.html", testReportsHome.getPath(), Tools.formatDateToFileSuffix(new Date()));
        Writer htmlWriter = new FileWriter(htmlReportPath);
        try {
            htmlTemplate.process(params, htmlWriter);
        } catch (TemplateException e) {
            log.error("生成html报告失败, 模板处理异常", e);
        }
        return htmlReportPath;
    }
}
