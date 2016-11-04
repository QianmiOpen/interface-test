package com.qianmi.tda.exec;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.qianmi.tda.bean.TestSuit;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TestCaseLoader
 * Created by aqlu on 2016/10/28.
 */
@Component
@Slf4j
public class TestCaseLoader {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    @Value("${test-case-file-extension:.ts.json}")
    @Setter
    private String testCaseFileExtension;

    @Value("${test-suit-home:${user.dir}/testcase}")
    @Setter
    private File testSuitHome;

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
        try {
            DocumentContext doc = JsonPath.parse(testSuitFile);

            TestSuit testSuit = doc.read("$", TestSuit.class);

            if (!StringUtils.hasText(testSuit.getIntfName())) {
                String testSuitHomePath = testSuitHome.getPath();
                String testSuitFilePath = testSuitFile.getPath();
                String defaultSuitName = testSuitFilePath.substring(0, testSuitFilePath.lastIndexOf(testCaseFileExtension))
                        .replaceFirst(testSuitHomePath + FILE_SEPARATOR, "")
                        .replaceAll(FILE_SEPARATOR, ".");

                testSuit.setIntfName(defaultSuitName);
            }

            return testSuit;
        } catch (Exception ex) {
            throw new RuntimeException("加载文件失败，file:" + testSuitFile, ex);
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
                } else if (file.isFile() && file.getName().endsWith(testCaseFileExtension)) {
                    testSuitsFiles.add(file);
                }
            }
        }
    }
}
