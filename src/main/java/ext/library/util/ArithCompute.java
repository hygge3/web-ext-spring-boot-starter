package ext.library.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <b>数学计算</b>
 * <p>
 * 由于 Java 的简单类型不能够精确的对浮点数进行运算，这个工具类提供精确的浮点数运算，包括加减乘除和四舍五入。
 */
public class ArithCompute {

    private static final int DEF_DIV_SCALE = 10;

    /**
     * 提供精确的加法运算。
     *
     * @param augend  被加数
     * @param addends 加数
     * @return 参数的和
     */
    public static double add(double augend, double... addends) {
        BigDecimal bdAugend = new BigDecimal(Double.toString(augend));
        for (double addend : addends) {
            BigDecimal bdAddend = new BigDecimal(Double.toString(addend));
            bdAugend = bdAugend.add(bdAddend);
        }

        return bdAugend.doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param minuend     被减数
     * @param subtrahends 减数
     * @return 参数的差
     */
    public static double sub(double minuend, double... subtrahends) {
        BigDecimal bdMinuend = new BigDecimal(Double.toString(minuend));
        for (double subtrahend : subtrahends) {
            BigDecimal bdSubtrahend = new BigDecimal(Double.toString(subtrahend));
            bdMinuend = bdMinuend.subtract(bdSubtrahend);
        }
        return bdMinuend.doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点后 10 位，其余的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由 scale 参数指定精度，其余的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数（除数不能为零）
     * @param scale 表示表示需要精确到小数点以后几位（如果精确范围小于 0，将抛出异常信息）
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精确度不能小于 0");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位 <b>四舍五入</b> 处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        return roundingMode(v, scale, RoundingMode.HALF_UP);
    }

    /**
     * <b>舍入模式</b>
     *
     * @param v            需要舍入的数字
     * @param scale        小数点后保留几位
     * @param roundingMode 舍入模式
     * @return 舍入后的结果
     */
    public static double roundingMode(double v, int scale, RoundingMode roundingMode) {
        if (scale < 0) {
            throw new IllegalArgumentException("精确度不能小于 0");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, roundingMode).doubleValue();
    }

    /**
     * 分转元
     *
     * @param moneySumCent 金额（单位：分）
     * @return 金额（单位：元）
     */
    public static double centToYuan(int moneySumCent) {
        return div(moneySumCent, 100);
    }

    /**
     * 元转分
     *
     * @param moneySum 金额（单位：元）
     * @return 金额（单位：分）
     */
    public static int yuanToCent(double moneySum) {
        return Integer.parseInt(Double.toString(mul(moneySum, 100)));
    }

}