package ext.library.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import ext.library.convert.Convert;

import java.util.List;

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
     * 转换为 {@linkplain Dict}
     *
     * @param value 被转换的值
     * @return Dict
     * @see Convert#toDict(Object)
     */
    public static Dict toDict(Object value) {
        return Convert.toDict(value);
    }

    /**
     * 转换为 {@linkplain List<Dict>}
     *
     * @param value 被转换的值
     * @return Dict 数组
     * @see Convert#toDictList(Object)
     */
    public static List<Dict> toDictList(Object value) {
        return Convert.toDictList(value);
    }

}