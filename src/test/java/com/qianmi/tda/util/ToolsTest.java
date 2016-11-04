package com.qianmi.tda.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ToolsTest
 * Created by aqlu on 2016/11/3.
 */
public class ToolsTest {
    @Test
    public void formatJson() throws Exception {
        String json = "{\"exec\nOrder\":1,\"testCases\":[{\"intfName\":\"case1\",\"params\":[{\"brandName\":\"茶类\",\"cateId\":\"1008\",\"chainMasterId\":\"A1246490\",\"goodsModifyRequest\":[{\"optUserCode\":null,\"optUserName\":null,\"goodsId\":\"g3071818\",\"price\":20,\"stock\":1000,\"spec\":null,\"barCode\":null,\"cost\":0}],\"imagesRequest\":[],\"optUserCode\":null,\"optUserName\":null,\"pointUserId\":\"A1452239\",\"productDesc\":\"<p><span style=\\\"color: rgb(51, 51, 51); font-family: Tahoma, Arial, &#39;Hiragino Sans GB&#39;, 冬青黑, &#39;Microsoft YaHei&#39;, 微软雅黑, SimSun, 宋体, Heiti, 黑体, sans-serif; line-height: 34px; text-indent: 28px; background-color: rgb(255, 255, 255);\\\">&nbsp; &nbsp; &nbsp; 新疆昆仑雪菊是野生草本植物,性味苦、辛、韦寒、归肺、肝经。是新疆惟一与雪莲齐名的稀有高寒植物，《本草汇言》称其可“破血疏肝，解疔散毒。是一种人们非常喜欢的珍贵食材，虽然很多人都还无缘尝得。那么，雪菊泡水喝的功效有什么?主要对高血压，高血脂，糖尿病有着特殊的药食疗效。同时还可以平肝明目，散风清热，抗菌消炎。用于防止上呼吸道感染，失眠多梦，改善睡眠，热性肠炎及前列腺炎，消除口臭，改善眼睛的各种不适，对治疗眼睛疲劳，视力模糊有很好的疗效。</span></p>\",\"productId\":\"1455153\",\"productName\":\"新疆昆仑雪菊盒装50克\",\"productPlace\":null,\"supplier\":null,\"unit\":\"盒\"}],\"expects\":[{\"path\":\"$\",\"value\":null,\"operator\":\"=\"}]}]}";
        System.out.println(Tools.formatJson(json));
    }

}