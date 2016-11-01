package com.qianmi.tda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.qianmi.tda.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * TestCaseGenerator
 * Created by aqlu on 2016/10/28.
 */
@SuppressWarnings({"FieldCanBeLocal", "SpringFacetCodeInspection", "ConstantConditions"})
@SpringBootApplication
public class TestCaseGenerator implements CommandLineRunner {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static Logger logger = LoggerFactory.getLogger(TestCaseGenerator.class);

    private static String esHost = "eslog.dev.qianmi.com";

    private static String indexName = "logstash-*";

    private static String typeName = "INTF_LOG";

    private static String appName = "qm-pc";

    private static String beginTime = Tools.formatDate(Date.from(Instant.now().plus(-6, ChronoUnit.DAYS))) + " 00:00:00";

    private static String endTime = Tools.formatDate(Date.from(Instant.now())) + " 23:59:59";

    private static boolean isOverrideTestCase = false;

    private static String[] intfs = new String[]{
            "com.qianmi.pc.api.app.AppProductProvider:1.0.0@addFromOwner",
            "com.qianmi.pc.api.app.AppProductProvider:1.0.0@copyProduct",
            "com.qianmi.pc.api.app.AppProductProvider:1.0.0@getGoodsStockList",
            "com.qianmi.pc.api.app.product.AppProductImportProvider:1.0.0@batchImport",
            "com.qianmi.pc.api.app.stock.AppStockBillProvider:1.0.0@add",
            "com.qianmi.pc.api.app.stock.AppStockBillProvider:1.0.0@importStock",
            "com.qianmi.pc.api.app.stock.AppStockBillQueryProvider:1.0.0@getBillIdByOrderNo",
            "com.qianmi.pc.api.app.stock.AppStockBillQueryProvider:1.0.0@queryStockBillDetail",
            "com.qianmi.pc.api.app.stock.AppStockBillQueryProvider:1.0.0@queryStockBillDetailByGoodsId",
            "com.qianmi.pc.api.app.stock.AppStockBillQueryProvider:1.0.0@queryStockBillList",
            "com.qianmi.pc.api.cs.item.CsGoodsQueryProvider:1.0.0@query",
            "com.qianmi.pc.api.cs.item.CsProductProvider:1.0.0@modifyCustom",
            "com.qianmi.pc.api.cs.item.CsProductProvider:1.0.0@recyleDelete",
            "com.qianmi.pc.api.d2c.item.D2cGoodsQueryProvider:1.0.0@condition",
            "com.qianmi.pc.api.d2c.item.D2cGoodsQueryProvider:1.0.0@getDetail",
            "com.qianmi.pc.api.d2c.item.D2cGoodsQueryProvider:1.0.0@listByIds",
            "com.qianmi.pc.api.d2c.item.D2cGoodsQueryProvider:1.0.0@listByProductId",
            "com.qianmi.pc.api.d2c.item.D2cGoodsQueryProvider:1.0.0@listGoodsOnShelf",
            "com.qianmi.pc.api.d2c.item.D2cGoodsQueryProvider:1.0.0@listNoLevelByIds",
            "com.qianmi.pc.api.d2c.item.D2cProductQueryProvider:1.0.0@getDetailById",
            "com.qianmi.pc.api.d2c.item.D2cProductQueryProvider:1.0.0@getPriceInfo",
            "com.qianmi.pc.api.d2c.item.D2cProductQueryProvider:1.0.0@query",
            "com.qianmi.pc.api.d2p.item.D2pGoodsQueryProvider:1.0.0@condition",
            "com.qianmi.pc.api.d2p.item.D2pGoodsQueryProvider:1.0.0@listByIds",
            "com.qianmi.pc.api.d2p.item.D2pGoodsQueryProvider:1.0.0@listByProductId",
            "com.qianmi.pc.api.d2p.item.D2pGoodsQueryProvider:1.0.0@listGoodsOnShelf",
            "com.qianmi.pc.api.d2p.item.D2pGoodsQueryProvider:1.0.0@listNoLevelByIds",
            "com.qianmi.pc.api.d2p.item.D2pGoodsQueryProvider:1.0.0@queryGoodsByProductIds",
            "com.qianmi.pc.api.d2p.item.D2pProductProvider:1.0.0@setProductStockLabel",
            "com.qianmi.pc.api.d2p.item.D2pProductQueryProvider:1.0.0@countByChainMasterId",
            "com.qianmi.pc.api.d2p.item.D2pProductQueryProvider:1.0.0@getDetailById",
            "com.qianmi.pc.api.d2p.item.D2pProductQueryProvider:1.0.0@getProductSettingInfo",
            "com.qianmi.pc.api.d2p.item.D2pProductQueryProvider:1.0.0@query",
            "com.qianmi.pc.api.d2p.item.D2pProductQueryProvider:1.0.0@queryByName",
            "com.qianmi.pc.api.d2p.item.D2pProductQueryProvider:1.0.0@queryPriceStep",
            "com.qianmi.pc.api.es.item.EsGoodsQueryProvider:1.0.0@listByIdString",
            "com.qianmi.pc.api.es.item.EsGoodsQueryProvider:1.0.0@query",
            "com.qianmi.pc.api.es.item.EsProductQueryProvider:1.0.0@listByIdString",
            "com.qianmi.pc.api.es.item.EsProductQueryProvider:1.0.0@query",
            "com.qianmi.pc.api.g2d.item.G2dGoodsProvider:1.0.0@batchModifyGoodsStock",
            "com.qianmi.pc.api.g2d.item.G2dGoodsProvider:1.0.0@listByCmidPidGoodsId",
            "com.qianmi.pc.api.g2d.item.G2dGoodsProvider:1.0.0@listLineBySuplier",
            "com.qianmi.pc.api.g2d.item.G2dGoodsQueryProvider:1.0.0@listByGoodsIds",
            "com.qianmi.pc.api.g2d.item.G2dGoodsQueryProvider:1.0.0@listByGoodsIdsInShoppintCart",
            "com.qianmi.pc.api.g2d.item.G2dProductProvider:1.0.0@add",
            "com.qianmi.pc.api.g2d.item.G2dProductProvider:1.0.0@addFromOwner",
            "com.qianmi.pc.api.g2d.item.G2dProductProvider:1.0.0@modifyCustom",
            "com.qianmi.pc.api.g2d.item.G2dProductProvider:1.0.0@myAuditPass",
            "com.qianmi.pc.api.g2d.item.G2dProductProvider:1.0.0@recycleDelete",
            "com.qianmi.pc.api.g2d.item.G2dProductQueryProvider:1.0.0@query",
            "com.qianmi.pc.api.g2d.item.G2dProductQueryProvider:1.0.0@querySimilarSupplyProductList",
            "com.qianmi.pc.api.g2d.item.G2dProductQueryProvider:1.0.0@querySupplyProduct",
            "com.qianmi.pc.api.item.CloudShopProductEFALSEtQueryProvider:1.0.0@queryCloudShopProductEFALSEts",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@cancelDelivery",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@delivery",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@lockStock",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@newModifyBillStock",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@newReleaseStock",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@returnGoods",
            "com.qianmi.pc.api.item.GoodsProvider:1.0.0@saveStock",
            "com.qianmi.pc.api.item.GoodsQueryProvider:1.0.0@listByCmidPidGoodsId",
            "com.qianmi.pc.api.item.GoodsQueryProvider:1.0.0@listByIds",
            "com.qianmi.pc.api.item.GoodsQueryProvider:1.0.0@listByProductId",
            "com.qianmi.pc.api.item.GoodsQueryProvider:1.0.0@listNoLevelByIds",
            "com.qianmi.pc.api.item.ItemBatchProvider:1.0.0@addOrModifyBatch",
            "com.qianmi.pc.api.item.ItemBatchQueryProvider:1.0.0@getTypeRelDataGetById",
            "com.qianmi.pc.api.item.ItemProvider:1.0.0@add",
            "com.qianmi.pc.api.item.ItemProvider:1.0.0@addCustom",
            "com.qianmi.pc.api.item.ItemProvider:1.0.0@addFromStdProduct",
            "com.qianmi.pc.api.item.ItemProvider:1.0.0@modify",
            "com.qianmi.pc.api.item.ProductEFALSEtQueryProvider:1.0.0@getProductEFALSEtBasic",
            "com.qianmi.pc.api.item.ProductEFALSEtQueryProvider:1.0.0@queryProductEFALSEt",
            "com.qianmi.pc.api.item.ProductProvider:1.0.0@deleteProductInRecycler",
            "com.qianmi.pc.api.item.ProductQueryProvider:1.0.0@countByChainMasterId",
            "com.qianmi.pc.api.item.ProductQueryProvider:1.0.0@getBasic",
            "com.qianmi.pc.api.item.ProductQueryProvider:1.0.0@getProductAndGoodsListById",
            "com.qianmi.pc.api.item.SupProductEFALSEtQueryProvider:1.0.0@queryGoods",
            "com.qianmi.pc.api.ms.item.MsGoodsQueryProvider:1.0.0@eFALSEportGoods",
            "com.qianmi.pc.api.ms.item.MsProductProvider:1.0.0@clearByChainMasterId",
            "com.qianmi.pc.api.ms.item.MsProductProvider:1.0.0@clearProductInTrashLastN",
            "com.qianmi.pc.api.ms.item.MsProductProvider:1.0.0@deleteOrRecoveryProduct",
            "com.qianmi.pc.api.ms.item.MsProductProvider:1.0.0@forceDeleteProductByStdId",
            "com.qianmi.pc.api.ms.item.MsProductProvider:1.0.0@forceDeleteProducts",
            "com.qianmi.pc.api.ms.item.MsProductQueryProvider:1.0.0@queryProductEFALSEt",
            "com.qianmi.pc.api.op.item.OpGoodsProvider:1.0.0@batchModifyGoodsStock",
            "com.qianmi.pc.api.op.item.OpGoodsProvider:1.0.0@batchModifyStock",
            "com.qianmi.pc.api.op.item.OpGoodsProvider:1.0.0@modifyById",
            "com.qianmi.pc.api.op.item.OpGoodsProvider:1.0.0@modifyCloudGoodsStock",
            "com.qianmi.pc.api.op.item.OpGoodsQueryProvider:1.0.0@queryByBn",
            "com.qianmi.pc.api.op.item.OpGoodsQueryProvider:1.0.0@queryByIds",
            "com.qianmi.pc.api.op.item.OpProductProvider:1.0.0@addCustom",
            "com.qianmi.pc.api.op.item.OpProductProvider:1.0.0@deleteById",
            "com.qianmi.pc.api.op.item.OpProductQueryProvider:1.0.0@getDetailById",
            "com.qianmi.pc.api.op.item.OpProductQueryProvider:1.0.0@queryByBn",
            "com.qianmi.pc.api.op.item.OpProductQueryProvider:1.0.0@queryByIds",
            "com.qianmi.pc.api.op.item.OpProductQueryProvider:1.0.0@queryCsProductEFALSEts",
            "com.qianmi.pc.api.op.item.OpProductQueryProvider:1.0.0@queryProductAndGoodsByIds",
            "com.qianmi.pc.api.op.item.OpStdProductQueryProvider:1.0.0@listProductByCatIds",
            "com.qianmi.pc.api.product.ProductImportProvider:1.0.0@addSpecificProduct",
            "com.qianmi.pc.api.ssm.es.EsSsmProductQueryProvider:1.0.0@listSsmProductByIds",
            "com.qianmi.pc.api.ssm.item.SsmAdminProductLineProvider:1.0.0@addSsmWareModelProductToPointUser",
            "com.qianmi.pc.api.ssm.item.SsmAdminProductLineQueryProvider:1.0.0@querySsmCustProduct",
            "com.qianmi.pc.api.ssm.item.SsmAdminProductLineQueryProvider:1.0.0@querySsmWareAvailableModelProduct",
            "com.qianmi.pc.api.ssm.item.SsmAdminProductLineQueryProvider:1.0.0@querySsmWareModelProduct",
            "com.qianmi.pc.api.ssm.item.SsmAdminProductLineQueryProvider:1.0.0@querySsmWareProduct",
            "com.qianmi.pc.api.ssm.item.SsmAdminProductLineQueryProvider:1.0.0@queryYFALSEdShopProduct",
            "com.qianmi.pc.api.ssm.item.SsmCashRegisteQueryProvider:1.0.0@queryOfflineInfos",
            "com.qianmi.pc.api.ssm.item.SsmCashRegisteQueryProvider:1.0.0@queryOnSaleSsmGoods",
            "com.qianmi.pc.api.ssm.item.SsmCashRegisteQueryProvider:1.0.0@querySsmGoods",
            "com.qianmi.pc.api.ssm.item.SsmDpcItemVerifyQueryProvider:1.0.0@querySsmDpcVerifyItem",
            "com.qianmi.pc.api.ssm.item.SsmItemProvider:1.0.0@addDistributorItem",
            "com.qianmi.pc.api.ssm.item.SsmItemProvider:1.0.0@addItem",
            "com.qianmi.pc.api.ssm.item.SsmItemProvider:1.0.0@copySsmAdminItems",
            "com.qianmi.pc.api.ssm.item.SsmItemProvider:1.0.0@deleteItem",
            "com.qianmi.pc.api.ssm.item.SsmItemProvider:1.0.0@moifySsmProduct",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@getDistributorItemDetail",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@getSsmItemCdetailById",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@getSsmProductPdetailById",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@listPssmGoodsByBarCode",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@listPurchaseGoodsById",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@listSsmAdminGoodsToShopById",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@listSsmGoodsByBarCode",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@listSsmGoodsById",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@queryDistributorItems",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@querySsmItemsByParam",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@queryYFALSEdProduct",
            "com.qianmi.pc.api.ssm.item.SsmItemQueryProvider:1.0.0@queryYFALSEdProductByChainMasterCreate",
            "com.qianmi.pc.api.ssm.item.SsmProductQueryProvider:1.0.0@getProFALSEyProductById",
            "com.qianmi.pc.api.ssm.item.SsmProductQueryProvider:1.0.0@queryCellProductListByCondition",
            "com.qianmi.pc.api.ssm.item.SsmProFALSEyProductQueryProvider:1.0.0@listProFALSEyGoodsById",
            "com.qianmi.pc.api.ssm.item.SsmProFALSEyProductQueryProvider:1.0.0@querySsmProFALSEyGoodsListByStock",
            "com.qianmi.pc.api.ssm.item.SsmProFALSEyProductQueryProvider:1.0.0@querySSMProFALSEyProductList",
            "com.qianmi.pc.api.ssm.item.YFALSEdPcItemProvider:1.0.0@deleteItemById",
            "com.qianmi.pc.api.ssm.item.YFALSEdPcItemQueryProvider:1.0.0@queryYFALSEdProduct",
            "com.qianmi.pc.api.ssm.stock.SsmStockProvider:1.0.0@addPandian",
            "com.qianmi.pc.api.ssm.stock.SsmStockProvider:1.0.0@addStockBill",
            "com.qianmi.pc.api.ssm.stock.SsmStockProvider:1.0.0@batchAddAllotoutGoodsStock",
            "com.qianmi.pc.api.sup.item.SupGoodsQueryProvider:1.0.0@listByIds",
            "com.qianmi.pc.api.sup.SupProductQueryProvider:1.0.0@getSupProductAndGoodsDetailById"};

    private static String generateFilePath = System.getProperty("user.dir") + FILE_SEPARATOR + "testcase";

    private ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private RestTemplate defaultRestTemplate;


    public static void main(String[] args) {
        SpringApplication.run(TestCaseGenerator.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        long begin = System.currentTimeMillis();
        String url = String.format("http://%s/%s/%s/_search", esHost, indexName, typeName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        int succesCount = 0;
        int skipCount = 0;
        int notFountCount = 0;
        int excepitonCount = 0;

        for (String intf : intfs) {
            String fullClassName = intf.split(":")[0];
            String packageName = fullClassName.substring(0, fullClassName.lastIndexOf("."));
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String version = intf.split(":")[1].split("@")[0];
            String methodName = intf.split(":")[1].split("@")[1];


            @SuppressWarnings("ReplaceAllDot")
            String dirPath = generateFilePath + FILE_SEPARATOR + packageName.replaceAll("\\.", FILE_SEPARATOR);
            String fileName = dirPath + FILE_SEPARATOR + className + ":" + version + "@" + methodName + ".ts.json";

            // 如果文件已存在，并且没开启覆盖则忽略
            if (!isOverrideTestCase && new File(fileName).exists()) {
                logger.info("文件已存在，跳过生成。{}", fileName);
                skipCount++;
                continue;
            }


            String dsl = String.format(DSL_Template,
                    appName,
                    fullClassName + "." + methodName,
                    Tools.parse(beginTime).getTime(),
                    Tools.parse(endTime).getTime());
            HttpEntity<String> requestEntity = new HttpEntity<>(dsl, headers);

            ResponseEntity<String> esResponse;
            try {
                esResponse = this.defaultRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            } catch (RestClientException e) {
                excepitonCount++;
                logger.warn("获取接口日志异常，接口名：{}", intf, e);
                continue;
            }

            if (esResponse.getStatusCode().is2xxSuccessful()) {
                String json = esResponse.getBody();
                DocumentContext doc = JsonPath.parse(json);
                try {
                    if (doc.read("$.hits.total", long.class) > 0) {
                        String resultValue = doc.read("$.hits.hits[0].fields.resultValue[0]");
                        String paramValues = doc.read("$.hits.hits[0].fields.paramValues[0]");

                        Object resultMap = objectMapper.readValue(resultValue, Object.class);
                        TreeMap[] paramsMap = objectMapper.readValue(paramValues, TreeMap[].class);

                        Map<String, Object> expectsMap = new TreeMap<>();
                        expectsMap.put("path", "$");
                        expectsMap.put("operator", "=");
                        expectsMap.put("value", resultMap);

                        Map<String, Object> caseMap = new HashMap<>();
                        caseMap.put("name", "case1");
                        caseMap.put("params", paramsMap);
                        caseMap.put("expects", Collections.singletonList(expectsMap));

                        Map<String, Object> suitMap = new TreeMap<>();
                        suitMap.put("execOrder", 1);
                        suitMap.put("testServerURL", null);
                        suitMap.put("dubboServiceURL", "dubbo://172.19.65.199:20880");
                        suitMap.put("execOrder", 1);
                        suitMap.put("testCases", Collections.singletonList(caseMap));


                        String prettyContent = Tools.formatJson(objectMapper.writeValueAsString(suitMap));

                        // mkdir
                        //noinspection ResultOfMethodCallIgnored
                        new File(dirPath).mkdirs();
                        try (FileWriter fileWriter = new FileWriter(fileName)) {
                            fileWriter.write(prettyContent);
                            succesCount++;
                            logger.debug("生成数据模板完成， 接口名：{}", intf);
                        } catch (IOException ex) {
                            excepitonCount++;
                            logger.warn("生成数据模板失败，接口名：{}", intf, ex);
                        }
                    } else {
                        notFountCount++;
                        logger.warn("未查询到查询到接口日志：接口名{}", intf);
                    }
                } catch (PathNotFoundException ex) {
                    excepitonCount++;
                    logger.warn("获取接口日志paramValues与resultValue失败，接口名：{}，响应结果：{}", intf, esResponse);
                } catch (Exception ex) {
                    excepitonCount++;
                    logger.warn("未知异常, 接口名：{}", intf, ex);
                }
            } else {
                logger.warn("获取接口日志失败，接口名：{}，响应结果：{}", intf, esResponse);
                excepitonCount++;
            }
        }

        logger.info("************************");
        logger.info("*      生成模板：{}", succesCount);
        logger.info("* 跳过已存在模板：{}", skipCount);
        logger.info("*    未找到日志：{}", notFountCount);
        logger.info("*      发生异常：{}", excepitonCount);
        logger.info("*   Cost times：{}ms", System.currentTimeMillis() - begin);
        logger.info("************************");

    }


    //@formatter:off
    private static String DSL_Template = "{\n" +
            "    \"query\": {\n" +
            "        \"bool\": {\n" +
            "            \"must\": [\n" +
            "                {\n" +
            "                    \"term\": {\n" +
            "                        \"appName.raw\": \"%s\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"term\": {\n" +
            "                        \"methodName.raw\": \"%s\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"must_not\": {\n" +
            "                \"exists\": {\n" +
            "                    \"field\": \"exceptionMsg\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"filter\": {\n" +
            "                \"range\": {\n" +
            "                    \"@timestamp\": {\n" +
            "                        \"gte\": %s,\n" +
            "                        \"lte\": %s,\n" +
            "                        \"format\": \"epoch_millis\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"size\": 10,\n" +
            "    \"fields\": [\n" +
            "        \"methodName\",\n" +
            "        \"paramValues\",\n" +
            "        \"resultValue\"\n" +
            "    ]\n" +
            "}";
    //@formatter:on
}
