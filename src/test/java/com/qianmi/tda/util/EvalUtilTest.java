package com.qianmi.tda.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * EvalUtilTest
 * Created by aqlu on 2016/10/31.
 */
public class EvalUtilTest {

    @Test
    public void testEval() throws IOException {
        assertTrue(EvalUtil.eval(1, 1, "="));
        assertTrue(EvalUtil.eval(2, 1, "!="));
        assertTrue(EvalUtil.eval(2, 1, "<"));
        assertTrue(EvalUtil.eval(2, 1, "<="));
        assertTrue(EvalUtil.eval(2, 2, "<="));
        assertTrue(EvalUtil.eval(1, 2, ">"));
        assertTrue(EvalUtil.eval(1, 1, ">="));
        assertTrue(EvalUtil.eval(1, 2, ">="));
        assertTrue(EvalUtil.eval(1, 1, "="));

        assertTrue(EvalUtil.eval("Hello", "Hello", "="));
        assertTrue(EvalUtil.eval("Hello", "Hello,A", "!="));
        assertTrue(EvalUtil.eval("Hello", "Hello", ">="));
        assertTrue(EvalUtil.eval("Hello", "Hello,A", ">"));
        assertTrue(EvalUtil.eval("Hello", "Hello", "<="));
        assertTrue(EvalUtil.eval("Hello", "Hdllo", "<"));
        assertTrue(EvalUtil.eval("Hello", "Hello, A", "contains"));
        assertTrue(EvalUtil.eval("Hello", "Helle, A", "!contains"));
        assertTrue(EvalUtil.eval(".*\\.json$", "Hello.json", "match"));
        assertTrue(EvalUtil.eval(".*\\.json$", "Hello.js", "!match"));

        assertTrue(EvalUtil.eval(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c"), "="));
        assertTrue(EvalUtil.eval(Arrays.asList("a", "b", "c"), Arrays.asList("a", "c", "b"), "!="));
        assertTrue(EvalUtil.eval(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "b"), "!="));
        assertTrue(EvalUtil.eval("a", Arrays.asList("a", "b", "c"), "contains"));
        assertTrue(EvalUtil.eval("e", Arrays.asList("a", "b", "c"), "!contains"));
        assertTrue(EvalUtil.eval(Arrays.asList("a", "b"), Arrays.asList("a", "b", "c"), "contains"));
        assertTrue(EvalUtil.eval(Arrays.asList("a", "c"), Arrays.asList("a", "b", "c"), "contains"));
        assertTrue(EvalUtil.eval(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c"), "contains"));
        assertTrue(EvalUtil.eval(Arrays.asList("d", "e"), Arrays.asList("a", "b", "c"), "!contains"));

        assertTrue(EvalUtil.eval(Arrays.asList(1, 2), Arrays.asList("1", "2"), "!="));
        assertTrue(EvalUtil.eval(Arrays.asList(1, 2), Arrays.asList("1", "2", "3"), "!contains"));

        assertTrue(EvalUtil.eval(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3), "="));
        assertTrue(EvalUtil.eval(Arrays.asList(1, 2, 3), Arrays.asList(1, 3, 2), "!="));
        assertTrue(EvalUtil.eval(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 2), "!="));
        assertTrue(EvalUtil.eval(1, Arrays.asList(1, 2, 3), "contains"));
        assertTrue(EvalUtil.eval(4, Arrays.asList(1, 2, 3), "!contains"));
        assertTrue(EvalUtil.eval(Arrays.asList(1, 2), Arrays.asList(1, 2, 3), "contains"));
        assertTrue(EvalUtil.eval(Arrays.asList(1, 3), Arrays.asList(1, 2, 3), "contains"));
        assertTrue(EvalUtil.eval(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3), "contains"));
        assertTrue(EvalUtil.eval(Arrays.asList(4, 5), Arrays.asList(1, 2, 3), "!contains"));

        Map<String, String> map1 = new HashMap<>();
        map1.put("a", "1");
        map1.put("b", "2");
        Map<String, String> map2 = new HashMap<>();
        map2.put("a", "1");
        map2.put("b", "2");

        assertTrue(EvalUtil.eval(map1, map2, "="));
        assertTrue(EvalUtil.eval(map1, map2, "contains"));
        map2.put("c", "3");
        assertTrue(EvalUtil.eval(map1, map2, "!="));
        assertTrue(EvalUtil.eval(map1, map2, "contains"));

        ObjectMapper objectMapper = new ObjectMapper();
        List json1 = objectMapper.readValue(testJson, List.class);
        List json2 = objectMapper.readValue(testJson, List.class);
        assertTrue(EvalUtil.eval(json1, json2, "="));

        assertTrue(EvalUtil.eval(Collections.singletonList(json1), Arrays.asList(json1, json2), "contains"));

    }

    String testJson = "[\n" +
            "        {\n" +
            "          \"operator\": \"=\",\n" +
            "          \"path\": \"$\",\n" +
            "          \"value\": {\n" +
            "            \"supplierId\": \"A1451442\",\n" +
            "            \"sourceChainMasterId\": \"A1301402\",\n" +
            "            \"productId\": \"1454504\",\n" +
            "            \"newProductId\": \"1454527\",\n" +
            "            \"goodsIds\": [\n" +
            "              \"g3069886\",\n" +
            "              \"g3069885\",\n" +
            "              \"g3069888\",\n" +
            "              \"g3069887\",\n" +
            "              \"g3069890\",\n" +
            "              \"g3069889\",\n" +
            "              \"g3069892\",\n" +
            "              \"g3069891\",\n" +
            "              \"g3069904\",\n" +
            "              \"g3069903\",\n" +
            "              \"g3069906\",\n" +
            "              \"g3069905\",\n" +
            "              \"g3069883\",\n" +
            "              \"g3069884\",\n" +
            "              \"g3069881\",\n" +
            "              \"g3069880\",\n" +
            "              \"g3069902\",\n" +
            "              \"g3069900\",\n" +
            "              \"g3069882\",\n" +
            "              \"g3069901\",\n" +
            "              \"g3069898\",\n" +
            "              \"g3069899\",\n" +
            "              \"g3069896\",\n" +
            "              \"g3069897\",\n" +
            "              \"g3069895\",\n" +
            "              \"g3069894\",\n" +
            "              \"g3069893\"\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]";

}