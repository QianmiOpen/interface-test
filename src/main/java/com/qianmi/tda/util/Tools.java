package com.qianmi.tda.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tools
 * Created by aqlu on 2016/10/28.
 */
public final class Tools {

    private static String DATE_PATTERN = "yyyy-MM-dd";

    private static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static String DATE_TIME_MILLS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 日期格式化成字符串
     */
    public static String formatDate(Date date) {
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }

    /**
     * 日期格式化成字符串
     */
    public static String formatDateTime(Date date) {
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(date);
    }

    /**
     * 日期格式化成字符串
     */
    public static String formatDateToFileSuffix(Date date) {
        return new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(date);
    }

    /**
     * 日期格式化成字符串
     */
    public static String formatDateTimeMills(Date date) {
        return new SimpleDateFormat(DATE_TIME_MILLS_PATTERN).format(date);
    }

    /**
     * 字符串转换为日期
     */
    public static Date parse(String dateStr) {
        try {
            switch (dateStr.length()) {
                case 10:
                    return new SimpleDateFormat(DATE_PATTERN).parse(dateStr);
                case 19:
                    return new SimpleDateFormat(DATE_TIME_PATTERN).parse(dateStr);
                case 23:
                    return new SimpleDateFormat(DATE_TIME_MILLS_PATTERN).parse(dateStr);
                default:
                    throw new IllegalArgumentException("Argument of ['dateStr'] format wrong. dateStr: " + dateStr);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Argument of ['dateStr'] format wrong. dateStr: " + dateStr);
        }
    }


    /**
     * 将Throwable转换为字符串描述的堆栈信息
     *
     * @param throwable 异常信息
     */
    public static String getStackTrace(Throwable throwable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * JSON字符串格式化
     *
     * @param jsonStr json字符串
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last;
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMark = false;
        int quotationMarks = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);

            if (last != '\\' && current == '\"') {
                quotationMarks++;
                isInQuotationMark = quotationMarks % 2 == 1;
            }

            switch (current) {
                case '\n':
                    if (!isInQuotationMark) {
                        sb.append("\\n");
                    } else {
                        sb.append(current);
                    }
                    break;
                case '\r':
                    if (!isInQuotationMark) {
                        sb.append("\\r");
                    } else {
                        sb.append(current);
                    }
                    break;
                case '\t':
                    if (!isInQuotationMark) {
                        sb.append("\\t");
                    } else {
                        sb.append(current);
                    }
                    break;
                case '\f':
                    if (!isInQuotationMark) {
                        sb.append("\\f");
                    } else {
                        sb.append(current);
                    }
                    break;
                case ':':
                    sb.append(current);
                    if (!isInQuotationMark) {
                        sb.append(" ");
                    }
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (!isInQuotationMark) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }

    public static void main(String[] args) {
        String json = "{\"exec\nOrder\":1,\"testCases\":[{\"intfName\":\"case1\",\"params\":[{\"brandName\":\"茶类\",\"cateId\":\"1008\",\"chainMasterId\":\"A1246490\",\"goodsModifyRequest\":[{\"optUserCode\":null,\"optUserName\":null,\"goodsId\":\"g3071818\",\"price\":20,\"stock\":1000,\"spec\":null,\"barCode\":null,\"cost\":0}],\"imagesRequest\":[],\"optUserCode\":null,\"optUserName\":null,\"pointUserId\":\"A1452239\",\"productDesc\":\"<p><span style=\\\"color: rgb(51, 51, 51); font-family: Tahoma, Arial, &#39;Hiragino Sans GB&#39;, 冬青黑, &#39;Microsoft YaHei&#39;, 微软雅黑, SimSun, 宋体, Heiti, 黑体, sans-serif; line-height: 34px; text-indent: 28px; background-color: rgb(255, 255, 255);\\\">&nbsp; &nbsp; &nbsp; 新疆昆仑雪菊是野生草本植物,性味苦、辛、韦寒、归肺、肝经。是新疆惟一与雪莲齐名的稀有高寒植物，《本草汇言》称其可“破血疏肝，解疔散毒。是一种人们非常喜欢的珍贵食材，虽然很多人都还无缘尝得。那么，雪菊泡水喝的功效有什么?主要对高血压，高血脂，糖尿病有着特殊的药食疗效。同时还可以平肝明目，散风清热，抗菌消炎。用于防止上呼吸道感染，失眠多梦，改善睡眠，热性肠炎及前列腺炎，消除口臭，改善眼睛的各种不适，对治疗眼睛疲劳，视力模糊有很好的疗效。</span></p>\",\"productId\":\"1455153\",\"productName\":\"新疆昆仑雪菊盒装50克\",\"productPlace\":null,\"supplier\":null,\"unit\":\"盒\"}],\"expects\":[{\"path\":\"$\",\"value\":null,\"operator\":\"=\"}]}]}";
        System.out.println(formatJson(json));
    }
}
