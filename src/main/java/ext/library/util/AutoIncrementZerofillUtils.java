package ext.library.util;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import ext.library.exception.ParamException;

/**
 * 自动递增填充零
 */
public class AutoIncrementZerofillUtils {

    /**
     * 获得初始化值，自动填充零
     *
     * @param length 初始化长度
     * @return 如：0001
     */
    public static String getInitValue(int length) {
        return StrUtil.padPre("1", length, '0');
    }

    /**
     * 字符串尾部值自动递增
     *
     * @param str 尾部值是 {@linkplain Integer} 类型
     * @return 自动递增后的值
     * @throws ParamException 如：("999", "str999")
     */
    public static String autoIncrement(String str) {
        int maxIndex = str.length() - 1;
        int autoIncrementValue = Integer.parseInt(CharUtil.toString(str.charAt(maxIndex))) + 1;
        if (autoIncrementValue == 10) {
            int cycleIndex = 0;
            int i = maxIndex - 1;
            while (i >= 0) {
                int autoIncrementValueI = Integer.parseInt(CharUtil.toString(str.charAt(i))) + 1;
                cycleIndex++;
                if (autoIncrementValueI != 10) {
                    String pad = StrUtil.padPre("0", cycleIndex, '0');
                    String replaceValue = autoIncrementValueI + pad;
                    return StringUtils.replace(str, replaceValue, i, i + 1 + replaceValue.length());
                }
                i--;
            }

            throw new ParamException("无法自动递增，此参数已是最大值：" + str);
        }

        return str.substring(0, maxIndex) + autoIncrementValue;
    }

    /**
     * 字符串尾部值自动递减
     *
     * @param str 尾部值是 {@linkplain Integer} 类型
     * @return 自动递减后的值
     */
    public static String autoDecr(String str) {
        int maxIndex = str.length() - 1;
        int autoDecrValue = Integer.parseInt(CharUtil.toString(str.charAt(maxIndex))) - 1;
        if (autoDecrValue == -1) {
            int cycleIndex = 0;
            int i = maxIndex - 1;
            while (i >= 0) {
                int autoDecrValueI = Integer.parseInt(CharUtil.toString(str.charAt(i))) - 1;
                cycleIndex++;
                if (autoDecrValueI != -1) {
                    String pad = StrUtil.padPre("9", cycleIndex, '9');
                    String replaceValue = autoDecrValueI + pad;
                    return StringUtils.replace(str, replaceValue, i, i + 1 + replaceValue.length());
                }
                i--;
            }

            throw new ParamException("无法自动递减，此参数已是最小值：" + str);
        }

        return str.substring(0, maxIndex) + autoDecrValue;
    }

}