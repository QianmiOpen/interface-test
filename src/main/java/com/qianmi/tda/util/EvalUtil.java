package com.qianmi.tda.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * EvalUtil
 * Created by aqlu on 2016/10/31.
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class EvalUtil {
    public static final String CMP_OP_EQUALS = "=";
    public static final String CMP_OP_NOT_EQUALS = "!=";
    public static final String CMP_OP_LESS_THAN = "<";
    public static final String CMP_OP_GREATER_THAN = ">";
    public static final String CMP_OP_LESS_THAN_EQUALS = "<=";
    public static final String CMP_OP_GREATER_THAN_EQUALS = ">=";
    public static final String CMP_OP_CONTAINS = "contains";
    public static final String CMP_OP_NOT_CONTAINS = "!contains";
    public static final String CMP_OP_MATCH = "match";
    public static final String CMP_OP_NOT_MATCH = "!match";

    public static final String[] OPERATORS = new String[]{CMP_OP_EQUALS, CMP_OP_NOT_EQUALS, CMP_OP_LESS_THAN, CMP_OP_GREATER_THAN, CMP_OP_LESS_THAN_EQUALS, CMP_OP_GREATER_THAN_EQUALS, CMP_OP_CONTAINS, CMP_OP_NOT_CONTAINS, CMP_OP_MATCH, CMP_OP_NOT_MATCH};

    public static final List<String> OPERATOR_LIST = Arrays.asList(OPERATORS);


    /**
     * 比较两个对象
     *
     * @param expect   期待值
     * @param actual   实际值
     * @param operator 比较操作符
     * @return true:符合，false:不符合
     */
    public static boolean eval(Object expect, Object actual, String operator) {
        //校验运算符
        if (!StringUtils.hasText(operator)) {
            throw new IllegalArgumentException("operator参数不能为空");
        } else if (!OPERATOR_LIST.contains(operator)) {
            throw new IllegalArgumentException("不支持【" + operator + "】运算符， 仅支持：" + OPERATOR_LIST);
        }

        if (expect == null || actual == null) {
            // 期待值与实际值只要有一个为null时，只做"="与"!="比较
            switch (operator) {
                case CMP_OP_EQUALS:
                    return expect == actual;
                case CMP_OP_NOT_EQUALS:
                    return expect != actual;
                default:
                    return false;
            }
        } else if (expect instanceof Number) { //数字比较
            if (actual instanceof Number) {
                return evalNumber((Number) expect, (Number) actual, operator);
            } else //noinspection SimplifiableIfStatement
                if (actual instanceof List) {
                    return evalObjectAndList(expect, (List) actual, operator);
                } else {
                    return false;
                }
        } else if (expect instanceof String) { // 字符串比较
            if (actual instanceof String) {
                return evalString((String) expect, (String) actual, operator);
            } else  //noinspection SimplifiableIfStatement
                if (actual instanceof List) {
                    return evalObjectAndList(expect, (List) actual, operator);
                } else {
                    return false;
                }
        } else if (expect instanceof List) { // List比较
            return actual instanceof List && evalList((List) expect, (List) actual, operator, false);
        } else if (expect instanceof Map) { // Map比较
            return actual instanceof Map && evalMap((Map) expect, (Map) actual, operator);
        } else if (expect instanceof Comparable) { //Comparable比较
            return actual instanceof Comparable && evalComparable((Comparable) expect, (Comparable) actual, operator);
        } else { // 其它比较
            switch (operator) {
                case CMP_OP_EQUALS:
                    return expect.equals(actual);
                case CMP_OP_NOT_EQUALS:
                    return !expect.equals(actual);
                case CMP_OP_LESS_THAN:
                case CMP_OP_GREATER_THAN:
                case CMP_OP_LESS_THAN_EQUALS:
                case CMP_OP_GREATER_THAN_EQUALS:
                case CMP_OP_CONTAINS:
                case CMP_OP_NOT_CONTAINS:
                case CMP_OP_MATCH:
                case CMP_OP_NOT_MATCH:
                default:
                    throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
            }
        }
    }

    /**
     * 字符串比较
     */
    private static boolean evalString(String expect, String actual, String operator) {
        switch (operator) {
            case CMP_OP_EQUALS:
                return expect.equals(actual);
            case CMP_OP_NOT_EQUALS:
                return !expect.equals(actual);
            case CMP_OP_LESS_THAN:
                return actual.compareTo(expect) < 0;
            case CMP_OP_GREATER_THAN:
                return actual.compareTo(expect) > 0;
            case CMP_OP_LESS_THAN_EQUALS:
                return actual.compareTo(expect) <= 0;
            case CMP_OP_GREATER_THAN_EQUALS:
                return actual.compareTo(expect) >= 0;
            case CMP_OP_CONTAINS:
                return actual.contains(expect);
            case CMP_OP_NOT_CONTAINS:
                return !actual.contains(expect);
            case CMP_OP_MATCH:
                return actual.matches(expect);
            case CMP_OP_NOT_MATCH:
                return !actual.matches(expect);
            default:
                throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
        }
    }

    /**
     * 对象与列表比较
     */
    private static boolean evalObjectAndList(Object expect, List actual, String operator) {
        switch (operator) {
            case CMP_OP_CONTAINS:
                return actual.contains(expect);
            case CMP_OP_NOT_CONTAINS:
                return !actual.contains(expect);
            case CMP_OP_EQUALS:
            case CMP_OP_NOT_EQUALS:
            case CMP_OP_LESS_THAN:
            case CMP_OP_GREATER_THAN:
            case CMP_OP_LESS_THAN_EQUALS:
            case CMP_OP_GREATER_THAN_EQUALS:
            case CMP_OP_MATCH:
            case CMP_OP_NOT_MATCH:
            default:
                throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
        }
    }

    /**
     * 数字比较
     */
    private static boolean evalNumber(Number expect, Number actual, String operator) {
        switch (operator) {
            case CMP_OP_EQUALS:
                return compareNumbers(expect, actual) == 0;
            case CMP_OP_NOT_EQUALS:
                return compareNumbers(expect, actual) != 0;
            case CMP_OP_LESS_THAN:
                return compareNumbers(actual, expect) < 0;
            case CMP_OP_GREATER_THAN:
                return compareNumbers(actual, expect) > 0;
            case CMP_OP_LESS_THAN_EQUALS:
                return compareNumbers(actual, expect) <= 0;
            case CMP_OP_GREATER_THAN_EQUALS:
                return compareNumbers(actual, expect) >= 0;
            case CMP_OP_CONTAINS:
            case CMP_OP_NOT_CONTAINS:
            case CMP_OP_MATCH:
            case CMP_OP_NOT_MATCH:
            default:
                throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
        }
    }

    /**
     * 列表比较
     */
    private static boolean evalList(List expect, List actual, String operator, boolean ignoreOrder) {

        // 尝试对List进行排序
        if(ignoreOrder) {
            try {
                Collections.sort(expect);
                Collections.sort(actual);
            } catch (Exception e) { //忽略排序失败
                log.debug("排序失败, expect:{}, actual:{}", expect, actual, e);
            }
        }

        switch (operator) {
            case CMP_OP_EQUALS:
                return expect.equals(actual);
            case CMP_OP_NOT_EQUALS:
                return !expect.equals(actual);
            case CMP_OP_CONTAINS:
                for (Object expectElement : expect) {
                    if (!actual.contains(expectElement)) {
                        return false;
                    }
                }
                return true;
            case CMP_OP_NOT_CONTAINS:
                for (Object expectElement : expect) {
                    if (actual.contains(expectElement)) {
                        return false;
                    }
                }
                return true;
            case CMP_OP_LESS_THAN:
            case CMP_OP_GREATER_THAN:
            case CMP_OP_LESS_THAN_EQUALS:
            case CMP_OP_GREATER_THAN_EQUALS:
            case CMP_OP_MATCH:
            case CMP_OP_NOT_MATCH:
            default:
                throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
        }
    }

    /**
     * Map比较
     */
    private static boolean evalMap(Map expect, Map actual, String operator) {
        switch (operator) {
            case CMP_OP_EQUALS:
                return expect.equals(actual);
            case CMP_OP_NOT_EQUALS:
                return !expect.equals(actual);
            case CMP_OP_CONTAINS:
                for (Object expectKey : expect.keySet()) {
                    Object actualValue = actual.get(expectKey);
                    Object expectValue = expect.get(expectKey);

                    if (actualValue == null && expectValue == null) {
                        return true;
                    } else if (actualValue != null && expectValue != null && actualValue.equals(expectValue)) {
                        return true;
                    }
                }
                return false;
            case CMP_OP_NOT_CONTAINS:
            case CMP_OP_LESS_THAN:
            case CMP_OP_GREATER_THAN:
            case CMP_OP_LESS_THAN_EQUALS:
            case CMP_OP_GREATER_THAN_EQUALS:
            case CMP_OP_MATCH:
            case CMP_OP_NOT_MATCH:
            default:
                throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
        }
    }

    /**
     * Comparable比较
     */
    @SuppressWarnings("unchecked")
    private static boolean evalComparable(Comparable expect, Comparable actual, String operator) {
        switch (operator) {
            case CMP_OP_EQUALS:
                return expect.equals(actual);
            case CMP_OP_NOT_EQUALS:
                return !expect.equals(actual);
            case CMP_OP_LESS_THAN:
                return actual.compareTo(expect) < 0;
            case CMP_OP_GREATER_THAN:
                return actual.compareTo(expect) > 0;
            case CMP_OP_LESS_THAN_EQUALS:
                return actual.compareTo(expect) <= 0;
            case CMP_OP_GREATER_THAN_EQUALS:
                return actual.compareTo(expect) >= 0;
            case CMP_OP_CONTAINS:
            case CMP_OP_NOT_CONTAINS:
            case CMP_OP_MATCH:
            case CMP_OP_NOT_MATCH:
            default:
                throw new IllegalArgumentException(String.format("【%s】与【%s】不支持进行【%s】比较", expect, actual, operator));
        }
    }

    /**
     * @return -1 for negative, 0 for zero, 1 for positive.
     * @throws ArithmeticException if the number is NaN
     */
    @SuppressWarnings("Duplicates")
    public static int getSignum(Number num) throws ArithmeticException {
        if (num instanceof Integer) {
            int n = num.intValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        } else if (num instanceof BigDecimal) {
            BigDecimal n = (BigDecimal) num;
            return n.signum();
        } else if (num instanceof Double) {
            double n = num.doubleValue();
            if (n > 0) return 1;
            else if (n == 0) return 0;
            else if (n < 0) return -1;
            else throw new ArithmeticException("The signum of " + n + " is not defined.");  // NaN
        } else if (num instanceof Float) {
            float n = num.floatValue();
            if (n > 0) return 1;
            else if (n == 0) return 0;
            else if (n < 0) return -1;
            else throw new ArithmeticException("The signum of " + n + " is not defined.");  // NaN
        } else if (num instanceof Long) {
            long n = num.longValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        } else if (num instanceof Short) {
            short n = num.shortValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        } else if (num instanceof Byte) {
            byte n = num.byteValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        } else if (num instanceof BigInteger) {
            BigInteger n = (BigInteger) num;
            return n.signum();
        } else {
            throw new IllegalArgumentException("不支持此数字，number:" + num);
        }
    }

    private static BigDecimal toBigDecimal(Number num) {
        try {
            return num instanceof BigDecimal ? (BigDecimal) num : new BigDecimal(num.toString());
        } catch (NumberFormatException e) {
            // The exception message is useless, so we add a new one:
            throw new NumberFormatException("Can't parse this as BigDecimal number: " + num);
        }
    }

    /**
     * 比较两个数字，first>second返回1, first=second返回0, first<second返回-1
     */
    public static int compareNumbers(Number first, Number second) {
        // We try to find the result based on the sign (+/-/0) first, because:
        // - It's much faster than converting to BigDecial, and comparing to 0 is the most common comparison.
        // - It doesn't require any type conversions, and thus things like "Infinity > 0" won't fail.
        int firstSignum = EvalUtil.getSignum(first);
        int secondSignum = EvalUtil.getSignum(second);
        if (firstSignum != secondSignum) {
            return firstSignum < secondSignum ? -1 : (firstSignum > secondSignum ? 1 : 0);
        } else if (firstSignum == 0) {
            return 0;
        } else {
            BigDecimal left = toBigDecimal(first);
            BigDecimal right = toBigDecimal(second);
            return left.compareTo(right);
        }
    }
}
