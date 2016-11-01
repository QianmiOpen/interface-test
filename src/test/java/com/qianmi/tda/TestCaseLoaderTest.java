package com.qianmi.tda;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.qianmi.tda.bean.TestSuit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * TestCaseLoaderTest
 * Created by aqlu on 2016/10/29.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TestCaseLoaderTest {

//    @Autowired
//    ApplicationContext ctx;

    @Before
    public void setUp() throws Exception {
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

    @Test
    public void load() throws Exception {

        File testSuitHome = new File(System.getProperty("user.dir") + "/testcase");

        TestCaseLoader testCaseLoader = new TestCaseLoader();
        testCaseLoader.setTestCaseFileExtension(".ts.json");
        testCaseLoader.setTestSuitHome(testSuitHome);

        List<TestSuit> testSuits = testCaseLoader.load();
        System.out.println(testSuits);
    }

}