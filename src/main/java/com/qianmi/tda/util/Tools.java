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
}
