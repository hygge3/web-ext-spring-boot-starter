package ext.library.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import ext.library.convert.Convert;

/**
 * 对象工具类，包括对象比较与转换等问题
 */
public class ObjectUtils extends ObjectUtil {

    /**
     * 转换值为指定类型
     *
     * @param <T>   泛型
     * @param value 被转换的值
     * @param clazz 泛型类型
     * @return 转换后的对象
     * @see Convert#toObject(Object, Class)
     */
    public static <T> T toObject(Object value, Class<T> clazz) {
        return Convert.toObject(value, clazz);
    }

    /**
     * 转换值为指定 POJO 类型
     *
     * @param <T>   泛型
     * @param value 被转换的值
     * @param clazz 泛型类型
     * @return 转换后的 POJO
     * @see Convert#toJavaBean(Object, Class)
     */
    public static <T> T toJavaBean(Object value, Class<T> clazz) {
        return Convert.toJavaBean(value, clazz);
    }

    /**
     * 转换为 {@linkplain JSONObject}
     *
     * @param value 被转换的值
     * @return JSON
     * @see Convert#toJSONObject(Object)
     */
    public static JSONObject toJSONObject(Object value) {
        return Convert.toJSONObject(value);
    }

    /**
     * 转换为 {@linkplain JSONArray}
     *
     * @param value 被转换的值
     * @return JSON 数组
     * @see Convert#toJSONArray(Object)
     */
    public static JSONArray toJSONArray(Object value) {
        return Convert.toJSONArray(value);
    }

}