package ext.library.util;

import cn.hutool.core.util.IdUtil;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Id 工具类
 */
public class IdUtils extends IdUtil {

    /**
     * 获得小写的 32 位 UUID（去掉中划线）
     *
     * @return 小写 32 位 UUID
     */
    public static String getSimpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获得大写的 32 位 UUID（去掉中划线）
     *
     * @return 大写 32 位 UUID
     */
    public static String getSimpleUUIDToUpperCase() {
        return getSimpleUUID().toUpperCase();
    }

    /**
     * 获得 20 位有序时间单号（可读的时间有序、纯数字、20 位）
     *
     * @return 20 位有序时间单号
     */
    public static String getDateOrderNo() {
        return getDateOrderNo(null);
    }

    /**
     * 获得有序时间单号（可读的时间有序、纯数字、默认 20 位）
     *
     * @param length 时间单号长度（可以为 null，最小 20 位，最长 36 位）
     * @return 有序时间单号
     */
    public static String getDateOrderNo(@Nullable Integer length) {
        StringBuilder orderNo = new StringBuilder();
        orderNo.append(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()));
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        int subLength = snowflakeNextIdStr.length() - 3;
        if (length != null && length > 20) {
            subLength = subLength - (length - 20);
        }

        return orderNo.append(snowflakeNextIdStr.substring(subLength)).toString();
    }

    /**
     * 获得随机生成 n 位数字编码（纯数字）
     *
     * @param length 长度
     * @return 对应长度的数字编码（纯数字）
     */
    public static String getRandomNumberCode(int length) {
        // 字符源，可以根据需要删减
        String randomCodeSource = "0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 循环随机获得当次字符
            code.append(randomCodeSource.charAt((int) Math.floor(Math.random() * randomCodeSource.length())));
        }

        return code.toString();
    }

    /**
     * 获得随机生成 n 位字符编码（数字 + 字母）
     *
     * @param length 长度
     * @return 对应长度的字符编码（数字 + 字母）
     */
    public static String getRandomCode(int length) {
        // 字符源，可以根据需要删减
        String randomCodeSource = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 循环随机获得当次字符
            code.append(randomCodeSource.charAt((int) Math.floor(Math.random() * randomCodeSource.length())));
        }

        return code.toString();
    }

    /**
     * 获得随机生成 n 位大写字符编码（数字 + 大写字母）
     *
     * @param length 长度
     * @return 对应长度的大写字符编码（数字 + 大写字母）
     */
    public static String getRandomCodeToUpperCase(int length) {
        // 字符源，可以根据需要删减
        String randomCodeSource = "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 循环随机获得当次字符
            code.append(randomCodeSource.charAt((int) Math.floor(Math.random() * randomCodeSource.length())));
        }

        return code.toString();
    }

}