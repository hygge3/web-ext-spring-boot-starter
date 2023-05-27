package ext.library.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import ext.library.convert.Convert;
import ext.library.exception.ParamException;
import ext.library.exception.ParamVoidException;
import ext.library.exception.ResultException;
import ext.library.ipo.ParamFormatIPO;
import ext.library.validation.Validator;
import ext.library.web.view.R;
import ext.library.web.webenv.WebEnv;

import java.util.Arrays;
import java.util.List;

/**
 * 参数处理工具类
 * <p>1. 用于获取 Request 中的请求参数
 * <p>2. 用于参数确认与类型美化
 * <p>参数校验与类型转换参考：{@linkplain Convert}、{@linkplain Validator}
 */
public class ParamUtils {

    // Validate

    /** 必传参数 */
    static final String PARAM_PREFIX_MUST = "【必传参数】：";
    /** 可选参数 */
    static final String PARAM_PREFIX_CAN = "【可选参数】：";
    /** 收到传参 */
    static final String PARAM_PREFIX_RECEIVED = "【收到传参】：";

    // RequestParam

    /**
     * 获取 Request 中的请求参数
     * <p>不区分 Query 或 Body 传参，只要传参便可获取到
     * <p>Query Body 1 + 1，参数整合接收，从根源去除 SpringMVC 固定方式传参取参带来的烦恼
     * <p>此方法逻辑具体由当前 {@link WebEnv} 环境实现
     *
     * @return JSON 对象
     */
    public static JSONObject getParam() {
        WebEnv webEnv = SpringUtils.getBean(WebEnv.class);
        return webEnv.getParam();
    }

    /**
     * 获取 Request 中的请求参数
     * <p>不区分 Query 或 Body 传参，只要传参便可获取到
     * <p>Query Body 1 + 1，参数整合接收，从根源去除 SpringMVC 固定方式传参取参带来的烦恼
     * <p>此方法逻辑具体由当前 {@link WebEnv} 环境实现
     *
     * @param <T>   泛型
     * @param clazz 想要的参数类型
     * @return 想要的对象实例
     */
    public static <T> T getParam(Class<T> clazz) {
        WebEnv webEnv = SpringUtils.getBean(WebEnv.class);
        return webEnv.getParam(clazz);
    }

    // Format

    /**
     * 参数美化-Boolean 强类型转换
     *
     * @param paramJson 需要向强类型转换的参数
     * @param keys      可多个 boolean 值的 key
     */
    public static void paramFormatBoolean(JSONObject paramJson, String... keys) {
        for (String key : keys) {
            paramJson.replace(key, paramJson.getBoolean(key));
        }
    }

    /**
     * 参数美化-BigDecimal 强类型转换
     *
     * @param paramJson 需要向强类型转换的参数
     * @param keys      可多个 BigDecimal 值的 key
     */
    public static void paramFormatBigDecimal(JSONObject paramJson, String... keys) {
        for (String key : keys) {
            paramJson.replace(key, paramJson.getBigDecimal(key));
        }
    }

    /**
     * 参数美化-JSONObject 强类型转换
     *
     * @param paramJson 需要向强类型转换的参数
     * @param keys      可多个 JSONObject 值的 key
     */
    public static void paramFormatJsonObject(JSONObject paramJson, String... keys) {
        for (String key : keys) {
            paramJson.replace(key, paramJson.getJSONObject(key));
        }
    }

    /**
     * 参数美化-JSONArray 强类型转换
     *
     * @param paramJson 需要向强类型转换的参数
     * @param keys      可多个 JSONArray 值的 key
     */
    public static void paramFormatJsonArray(JSONObject paramJson, String... keys) {
        for (String key : keys) {
            paramJson.replace(key, paramJson.getJSONArray(key));
        }
    }

    /**
     * 参数美化-Object 强类型转换
     *
     * @param paramJson       需要向强类型转换的参数
     * @param paramFormatList 多个参数美化 IPO
     */
    public static void paramFormatObject(JSONObject paramJson, List<ParamFormatIPO> paramFormatList) {
        for (ParamFormatIPO paramFormat : paramFormatList) {
            String key = paramFormat.getKey();
            Class<?> clazz = paramFormat.getClazz();
            paramJson.replace(key, paramJson.getObject(key, clazz));
        }
    }

    /**
     * 参数美化--弱类型转强类型
     *
     * @param paramJson      需要向强类型转换的参数
     * @param booleanKeys    多个 boolean 值的 key（可以为 null）
     * @param decimalKeys    多个 BigDecimal 值的 key（可以为 null）
     * @param jsonObjectKeys 多个 JSONObject 值的 key（可以为 null）
     * @param jsonArrayKeys  多个 JSONArray 值的 key（可以为 null）
     */
    public static void paramFormat(JSONObject paramJson, String[] booleanKeys, String[] decimalKeys, String[] jsonObjectKeys, String[] jsonArrayKeys) {
        if (!StringUtils.isEmptys(booleanKeys)) {
            paramFormatBoolean(paramJson, booleanKeys);
        }

        if (!StringUtils.isEmptys(decimalKeys)) {
            paramFormatBigDecimal(paramJson, decimalKeys);
        }

        if (!StringUtils.isEmptys(jsonObjectKeys)) {
            paramFormatJsonObject(paramJson, jsonObjectKeys);
        }

        if (!StringUtils.isEmptys(jsonArrayKeys)) {
            paramFormatJsonArray(paramJson, jsonArrayKeys);
        }
    }

    // Validate

    /**
     * 空对象校验
     *
     * @param objects 对象数组
     * @throws ResultException 有空对象将抛出异常
     */
    public static void paramValidate(Object... objects) {
        for (Object object : objects) {
            if (ObjectUtils.isNull(object)) {
                throw new ResultException(R.paramCheckNotPass());
            }
        }
    }

    /**
     * param 参数校验
     * <p>1. 判断 Map 数据结构 key 的一致性
     * <p>2. 必传参数是否为空字符串
     *
     * @param paramJson       参数
     * @param mustContainKeys 必须包含的 key（必传）
     * @param canContainKeys  可包含的 key（非必传）
     * @throws ParamException 不满足条件抛出此异常及其提示信息
     */
    public static void paramValidate(JSONObject paramJson, String[] mustContainKeys, String... canContainKeys) {
        // 1. 判断 Map 数据结构 key 的一致性
        boolean isHint = false;
        String hintMsg = "";
        if (!MapUtils.isKeys(paramJson, mustContainKeys, canContainKeys)) {
            isHint = true;
            hintMsg = "【错误提示】：要求的参数 key 不一致，";
        }

        // 2. 必传参数是否为空字符串
        if (!isHint) {
            for (String key : mustContainKeys) {
                if (StringUtils.isEmptyIfStr(paramJson.get(key))) {
                    isHint = true;
                    hintMsg = StrUtil.format("【错误提示】：必传参数 {} 的值为空，", key);
                    break;
                }
            }
        }

        // 3. 提示
        if (isHint) {
            String paramHint = hintMsg + PARAM_PREFIX_MUST + Arrays.toString(mustContainKeys) + "，" + PARAM_PREFIX_CAN + Arrays.toString(canContainKeys) + "，" + PARAM_PREFIX_RECEIVED + paramJson.keySet();
            throw new ParamException(paramHint);
        }
    }

    /**
     * param 参数校验
     * <p>1. 判断 Map 数组数据结构 key 的一致性
     * <p>2. 必传参数是否为空字符串
     *
     * @param paramList       参数数组
     * @param mustContainKeys 必须包含的 key（必传）
     * @param canContainKeys  可包含的 key（非必传）
     * @throws ParamVoidException 参数是否为空抛出此异常
     * @throws ParamException     不满足条件抛出此异常及其提示信息
     */
    public static void paramValidate(List<JSONObject> paramList, String[] mustContainKeys, String... canContainKeys) {
        // 1. 校验参数是否为空
        Assert.notEmpty(paramList, ParamVoidException::new);

        // 2. 确认参数 key
        for (JSONObject paramJson : paramList) {
            paramValidate(paramJson, mustContainKeys, canContainKeys);
        }
    }

    /**
     * 11 位手机号码隐藏加密
     *
     * @param cellphone 手机号
     * @return 隐藏加密后的手机号
     */
    public static String cellphoneEncrypt(String cellphone) {
        if (cellphone.length() == 11) {
            return cellphone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        if (cellphone.length() > 4) {
            return cellphone.replaceAll("(\\d{2})\\d+(\\d{2})", "$1****$2");
        }
        return cellphone.replaceAll("(\\d{1})\\d+(\\d{1})", "$1**$2");
    }

}