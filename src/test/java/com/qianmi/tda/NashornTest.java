package com.qianmi.tda;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Set;

/**
 * NashornTest
 * Created by aqlu on 2016/11/9.
 */
public class NashornTest {

    @Test
    public void test() throws ScriptException {
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
//        DocumentContext doc = JsonPath.parse(this.getClass().getClassLoader().getResourceAsStream("nashorn.js")); //InvalidJsonException
//        TestSuit testSuit = doc.read("$", TestSuit.class);

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");

        nashorn.eval(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("nashorn.js")));//ScriptException->ParserException

        Object obj = nashorn.eval("JSON.stringify(testSuit)");

        System.out.println(obj);


    }
}
