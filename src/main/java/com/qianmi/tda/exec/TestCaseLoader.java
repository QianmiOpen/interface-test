package com.qianmi.tda.exec;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.qianmi.tda.bean.TestSuit;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TestCaseLoader
 * Created by aqlu on 2016/10/28.
 */
@Component
@Slf4j
public class TestCaseLoader {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    @Value("${test-case-file-extensions:.ts.json,.ts.js}")
    @Setter
    private String[] testCaseFileExtensions;

    @Value("${test-suit-home:${user.dir}/testcase}")
    @Setter
    private File testSuitHome;

    @Value("${test-suit-variable-name-in-js:testSuit}")
    private String testSuitVariableNameInJs;

    /**
     * 加载测试
     */
    public List<TestSuit> load() {

        if (testSuitHome == null || !testSuitHome.exists() || !testSuitHome.isDirectory()) {
            log.warn("'{}'不存在或不是一个目录", testSuitHome);
            return Collections.emptyList();
        }
        List<File> testSuitsFiles = new ArrayList<>();
        scanTestSuitFile(testSuitHome, testSuitsFiles);

        return testSuitsFiles.stream().map(this::loadTestSuit).sorted().collect(Collectors.toList());
    }


    private TestSuit loadTestSuit(File testSuitFile) {
        TestSuit testSuit = null;
        Exception cause = null;
        try {
            // 默认按json格式文件处理
            DocumentContext doc = JsonPath.parse(testSuitFile);
            testSuit = doc.read("$", TestSuit.class);
        } catch (Exception ex) {
            cause = ex;

            try {
                // 按json格式处理失败后，用JS引擎加载
                ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
                nashorn.eval(new FileReader(testSuitFile)); //ScriptException->ParserException
                String jsonStr = (String) nashorn.eval("JSON.stringify(" + testSuitVariableNameInJs + ")");
                testSuit = JsonPath.parse(jsonStr).read("$", TestSuit.class);
            } catch (Exception e) {
                cause = e;
            }
        }

        if(testSuit != null) {
            if (!StringUtils.hasText(testSuit.getIntfName())) {
                // 没有配置接口名时，根据文件存放路径与文件名生成接口名
                String testSuitHomePath = testSuitHome.getPath();
                String testSuitFilePath = testSuitFile.getPath();
                String extension = getTestSuitFileExtension(testSuitFile);
                String defaultSuitName = testSuitFilePath.substring(0, testSuitFilePath.lastIndexOf(extension))
                        .replaceFirst(testSuitHomePath + FILE_SEPARATOR, "")
                        .replaceAll("#", ":")
                        .replaceAll(FILE_SEPARATOR, ".");

                testSuit.setIntfName(defaultSuitName);
            }
            return testSuit;
        }else {
            throw new RuntimeException("加载文件失败，file:" + testSuitFile, cause);
        }
    }

    /**
     * 扫描所有.ts.json文件
     */
    private void scanTestSuitFile(File dir, List<File> testSuitsFiles) {

        if (dir == null) {
            return;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanTestSuitFile(file, testSuitsFiles);
                } else if (isTestSuitFile(file)) {
                    testSuitsFiles.add(file);
                }
            }
        }
    }

    /**
     * 判断是否为测试套文件
     */
    private boolean isTestSuitFile(File file) {
        return file.isFile()
                && Arrays.stream(testCaseFileExtensions).anyMatch(extension -> file.getName().endsWith(extension));
    }

    /**
     * 获取测试套文件的扩展名
     */
    private String getTestSuitFileExtension(File file) {
        Optional<String> optional = Arrays.stream(testCaseFileExtensions).filter(extension -> file.getName().endsWith(extension)).findFirst();
        return optional.isPresent() ? optional.get() : "";
    }
}
