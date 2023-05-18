package ext.library.util;

import cn.hutool.core.util.StrUtil;

/**
 * String 工具类
 */
public class StringUtils extends StrUtil {

    /**
     * 判断 String 数组是否为空<br>
     * <p>弱判断，只确定数组中第一个元素是否为空</p>
     *
     * @param array 要判断的 String[] 数组
     * @return String 数组长度==0 或者第一个元素为空（true）
     */
    public static boolean isEmptys(String[] array) {
        return null == array || array.length == 0 || isEmpty(array[0]);
    }

    /**
     * 确认 String 数组不为空<br>
     * <p>弱判断，只确定数组中第一个元素是否为空</p>
     *
     * @param array 要判断的 String[] 数组
     * @return String 数组长度==0 或者第一个元素为空（true）
     */
    public static boolean isNotEmptys(String[] array) {
        return !isEmptys(array);
    }

    /**
     * 替换字符串 - 根据索引
     *
     * @param str         原始字符串
     * @param replacedStr 替换字符串
     * @param start       开始索引，包括此索引
     * @param end         结束索引，不包括此索引（结束索引==开始索引：将在开始索引处插入替换字符串）
     * @return 替换后的字符串
     */
    public static String replace(String str, String replacedStr, int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.replace(start, end, replacedStr);
        return sb.toString();
    }

    /**
     * 删除开始相等的字符串
     *
     * @param str    需要处理的字符串
     * @param prefix 条件
     * @return 删除后的 StringBuffer
     */
    public static String deleteFirstEqualString(CharSequence str, CharSequence prefix) {
        return removePrefix(str, prefix);
    }

    /**
     * 删除尾部相等的字符串
     *
     * @param str    需要处理的字符串
     * @param suffix 条件
     * @return 删除后的字符串
     */
    public static String deleteLastEqualString(CharSequence str, CharSequence suffix) {
        StringBuilder sb = new StringBuilder(str);
        int end = sb.length();
        int start = end - suffix.length();
        String condition = sb.substring(start, end);
        if (suffix.equals(condition)) {
            return sb.delete(start, end).toString();
        }

        return sb.toString();
    }

    /**
     * 删除前后相等字符串
     *
     * @param str      需要处理的字符串
     * @param firstStr 开始字符串
     * @param lastStr  末尾字符串
     * @return 删除后的字符串
     */
    public static String deleteFirstLastEqualString(String str, String firstStr, String lastStr) {
        str = deleteFirstEqualString(str, firstStr);
        str = deleteLastEqualString(str, lastStr);
        return str;
    }

    /**
     * 删除前后字符串
     *
     * @param str    需要处理的字符串
     * @param length 删除长度
     * @return 删除后的字符串
     */
    public static String deleteFirstLastString(String str, int length) {
        return str.substring(length, str.length() - length);
    }

}