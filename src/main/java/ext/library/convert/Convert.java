package ext.library.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import ext.library.convert.converter.JSONArrayConverter;
import ext.library.convert.converter.JSONListConverter;
import ext.library.convert.converter.JSONObjectConverter;
import ext.library.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.toJSON;
import static com.alibaba.fastjson.util.TypeUtils.cast;
import static com.alibaba.fastjson.util.TypeUtils.castToJavaBean;

/**
 * <b>类型转换器</b>
 * <p>提供简单全面的类型转换，适合更多的业务场景，内置 hutool、fastjson、ext 三种类型转换器，判断精确性能强大，未知类型兼容性更强
 */
@Slf4j
public class Convert extends cn.hutool.core.convert.Convert {

    // 注册自定义转换器
    static {
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        converterRegistry.putCustom(JSONObject.class, JSONObjectConverter.class)
                .putCustom(JSONArray.class, JSONArrayConverter.class);
        List<Type> registryTypes = JSONListConverter.getRegistryTypes();
        for (Type registryType : registryTypes) {
            converterRegistry.putCustom(registryType, JSONListConverter.class);
        }
    }

    // --------------------------------------- 覆盖 hutool 转换方法，防止直接调用父类静态方法，导致因为本类未加载，从而自定义转换器未注册

    /**
     * 转换值为指定类型，类型采用字符串表示
     *
     * @param <T>       目标类型
     * @param className 类的字符串表示
     * @param value     值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convertByClassName(String className, Object value) throws ConvertException {
        return cn.hutool.core.convert.Convert.convertByClassName(className, value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     * @deprecated 请使用 {@linkplain #convert(Object, Class)}
     */
    @Deprecated
    public static <T> T convert(Class<T> type, Object value) throws ConvertException {
        return cn.hutool.core.convert.Convert.convert(type, value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>       目标类型
     * @param reference 类型参考，用于持有转换后的泛型类型
     * @param value     值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(TypeReference<T> reference, Object value) throws ConvertException {
        return cn.hutool.core.convert.Convert.convert(reference, value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Type type, Object value) throws ConvertException {
        return cn.hutool.core.convert.Convert.convert(type, value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Class<T> type, Object value, T defaultValue) throws ConvertException {
        return cn.hutool.core.convert.Convert.convert(type, value, defaultValue);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Type type, Object value, T defaultValue) throws ConvertException {
        return cn.hutool.core.convert.Convert.convert(type, value, defaultValue);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回{@code null}
     *
     * @param <T>   目标类型
     * @param type  目标类型
     * @param value 值
     * @return 转换后的值，转换失败返回 null
     */
    public static <T> T convertQuietly(Type type, Object value) {
        return cn.hutool.core.convert.Convert.convertQuietly(type, value);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param type         目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
        return cn.hutool.core.convert.Convert.convertQuietly(type, value, defaultValue);
    }

    // ----------------------------------------------------------------------- 推荐转换方法

    /**
     * 转换值为指定类型 <code style="color:red">（推荐）</code>
     *
     * @param <T>   泛型
     * @param value 被转换的值
     * @param clazz 泛型类型
     * @return 转换后的对象
     * @see #toObject(Object, Class)
     * @since Greenwich.SR1.2
     */
    public static <T> T convert(Object value, Class<T> clazz) {
        return toObject(value, clazz);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>   泛型
     * @param value 被转换的值
     * @param clazz 泛型类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(Object value, Class<T> clazz) {
        // 不用转换
        if (value != null && clazz != null && (clazz == value.getClass() || clazz.isInstance(value))) {
            return (T) value;
        }

        // JDK8 日期时间转换
        if (value instanceof String str) {
            if (clazz == LocalDate.class) {
                return (T) LocalDate.parse(str);
            } else if (clazz == LocalDateTime.class) {
                return (T) LocalDateTime.parse(str);
            }
        }

        // JSONObject 转换
        if (clazz == JSONObject.class) {
            return (T) toJSONObject(value);
        }

        // JSONArray 转换
        if (clazz == JSONArray.class) {
            return (T) toJSONArray(value);
        }

        // 采用 fastjson 转换
        try {
            return cast(value, clazz, ParserConfig.getGlobalInstance());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("【Convert】采用 fastjson 类型转换器转换失败，正尝试 hutool 类型转换器转换。");
                e.printStackTrace();
            }
        }

        // 采用 hutool 默认转换能力 + yue-library 扩展能力进行转换
        return cn.hutool.core.convert.Convert.convert(clazz, value);
    }

    /**
     * <h2 style="color:red">转换值为指定 POJO 类型</h2>
     * <p>
     * <b><i>性能测试对比如下：</i></b><br>
     * <i>1、Spring BeanUtils：</i>性能伯仲，兼容性远超<br>
     * <i>2、Cglib BeanCopier：</i>性能伯仲，兼容性远超<br>
     * <i>3、Apache BeanUtils：</i>秒杀<br>
     * <i>4、Apache PropertyUtils：</i>秒杀<br>
     * <i>5、Dozer：</i>秒杀<br>
     * </p>
     *
     * @param <T>   泛型
     * @param value 被转换的值
     * @param clazz 泛型类型
     * @return 转换后的 POJO
     */
    @SuppressWarnings("unchecked")
    public static <T> T toJavaBean(Object value, Class<T> clazz) {
        // 不用转换
        if (value == null) {
            return null;
        }
        if (clazz != null && (clazz == value.getClass() || clazz.isInstance(value))) {
            return (T) value;
        }

        // 采用 fastjson 转换
        try {
            if (value instanceof String) {
                return JSONObject.parseObject((String) value, clazz);
            }

            return castToJavaBean(toJSONObject(value), clazz, ParserConfig.getGlobalInstance());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("【Convert】采用 fastjson 类型转换器转换失败，正尝试 hutool 类型转换器转换。");
                e.printStackTrace();
            }
        }

        // 采用 hutool 默认转换能力 + yue-library 扩展能力进行转换
        return BeanUtil.toBean(value, clazz);
    }

    /**
     * 转换为 JSONString
     *
     * @param value 被转换的值
     * @return JSON 字符串
     */
    public static String toJSONString(Object value) {
        return JSONObject.toJSONString(value);
    }

    /**
     * 转换为 {@linkplain JSONObject}
     *
     * @param value 被转换的值
     * @return JSON 对象
     */
    @SuppressWarnings("unchecked")
    public static JSONObject toJSONObject(Object value) {
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        if (value instanceof Map) {
            return new JSONObject((Map<String, Object>) value);
        }

        if (value instanceof String) {
            return JSONObject.parseObject((String) value);
        }

        return JSONObject.parseObject(JSONObject.toJSONString(value));
    }

    /**
     * 转换为 {@linkplain JSONArray}
     *
     * @param value 被转换的值
     * @return JSON 数组
     */
    @SuppressWarnings("unchecked")
    public static JSONArray toJSONArray(Object value) {
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        if (value instanceof List) {
            return new JSONArray((List<Object>) value);
        }

        if (value instanceof String) {
            return JSONArray.parseArray((String) value);
        }

        return (JSONArray) toJSON(value);
    }

    // ----------------------------------------------------------------------- List 转换方法

    /**
     * 数组转 List
     * <p>此方法为 {@linkplain Arrays#asList(Object...)} 的安全实现</p>
     *
     * @param <T>   数组中的对象类
     * @param array 将被转换的数组
     * @return 被转换数组的列表视图
     * @see ListUtils#toList(Object[])
     */
    public static <T> ArrayList<T> toList(T[] array) {
        return ListUtils.toList(array);
    }

    /**
     * {@linkplain JSONArray} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>       泛型
     * @param jsonArray 需要转换的 JSONArray
     * @param clazz     json 转换的 POJO 类型
     * @return 转换后的 List
     * @see ListUtils#toList(JSONArray, Class)
     */
    public static <T> List<T> toList(JSONArray jsonArray, Class<T> clazz) {
        return ListUtils.toList(jsonArray, clazz);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>   泛型
     * @param list  需要转换的 List
     * @param clazz json 转换的 POJO 类型
     * @return 转换后的 List
     * @see ListUtils#toList(List, Class)
     */
    public static <T> List<T> toList(List<JSONObject> list, Class<T> clazz) {
        return ListUtils.toList(list, clazz);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain String}
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @return 转换后的 List
     * @see ListUtils#toList(List, String)
     */
    public static List<String> toList(List<JSONObject> list, String keepKey) {
        return ListUtils.toList(list, keepKey);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     * @return 转换后的 List
     * @see ListUtils#toList(List, String, Class)
     */
    public static <T> List<T> toList(List<JSONObject> list, String keepKey, Class<T> clazz) {
        return ListUtils.toList(list, keepKey, clazz);
    }

    /**
     * {@linkplain List} - {@linkplain JSONObject} 转 {@linkplain List} - {@linkplain String}并去除重复元素
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @return 处理后的 List
     * @see ListUtils#toListAndDistinct(List, String)
     */
    public static List<String> toListAndDistinct(List<JSONObject> list, String keepKey) {
        return ListUtils.toListAndDistinct(list, keepKey);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class}并去除重复元素
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     * @return 处理后的 List
     * @see ListUtils#toListAndDistinct(List, String, Class)
     */
    public static <T> List<T> toListAndDistinct(List<JSONObject> list, String keepKey, Class<T> clazz) {
        return ListUtils.toListAndDistinct(list, keepKey, clazz);
    }

    /**
     * {@linkplain List} - {@linkplain Map} 转 {@linkplain List} - {@linkplain JSONObject}
     * <p>
     * <b><i>性能测试说明：</i></b><br>
     * <i>测试 CPU：</i>i7-4710MQ<br>
     * <i>测试结果：</i>百万级数据平均 200ms（毫秒）<br>
     * </p>
     *
     * @param list 需要转换的 List
     * @return 转换后的 List
     * @see ListUtils#toJsonList(List)
     */
    public static List<JSONObject> toJsonList(List<Map<String, Object>> list) {
        return ListUtils.toJsonList(list);
    }

    /**
     * {@linkplain JSONArray} 转 {@linkplain List} - {@linkplain JSONObject}
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>无类型转换（类型推断）：</i>见 {@linkplain #toJsonList(List)}<br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param jsonArray 需要转换的 JSONArray
     * @return 转换后的 jsonList
     * @see ListUtils#toJsonList(JSONArray)
     */
    public static List<JSONObject> toJsonList(JSONArray jsonArray) {
        return ListUtils.toJsonList(jsonArray);
    }

    /**
     * {@linkplain List} - {@linkplain Class} 转 {@linkplain List} - {@linkplain JSONObject}
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param <T>  泛型
     * @param list 需要转换的 List
     * @return 转换后的 jsonList
     * @see ListUtils#toJsonListT(List)
     */
    public static <T> List<JSONObject> toJsonListT(List<T> list) {
        return ListUtils.toJsonListT(list);
    }

    /**
     * {@linkplain JSONArray} 转 {@linkplain JSONObject}[]
     * <p>对象引用转换，内存指针依旧指向元数据
     *
     * @param jsonArray 需要转换的 JSONArray
     * @return 转换后的 jsons
     * @see ListUtils#toJsons(JSONArray)
     */
    public static JSONObject[] toJsons(JSONArray jsonArray) {
        return ListUtils.toJsons(jsonArray);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain JSONObject}[]
     * <p>对象引用转换，内存指针依旧指向元数据
     *
     * @param list 需要转换的 List
     * @return 转换后的 jsons
     * @see ListUtils#toJsons(List)
     */
    public static JSONObject[] toJsons(List<JSONObject> list) {
        return ListUtils.toJsons(list);
    }

    /**
     * {@linkplain List} - {@linkplain Class} 转 {@linkplain JSONObject}[]
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param <T>  泛型
     * @param list 需要转换的 List
     * @return 转换后的 jsons
     * @see ListUtils#toJsonsT(List)
     */
    public static <T> JSONObject[] toJsonsT(List<T> list) {
        return ListUtils.toJsonsT(list);
    }

    /**
     * {@linkplain List} - {@linkplain Class} 转 {@linkplain JSONObject}[] 并移除空对象
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param <T>  泛型
     * @param list 需要转换的 List
     * @return 转换后的 jsons
     * @see ListUtils#toJsonsTAndRemoveEmpty(List)
     */
    public static <T> JSONObject[] toJsonsTAndRemoveEmpty(List<T> list) {
        return ListUtils.toJsonsTAndRemoveEmpty(list);
    }

    /**
     * {@linkplain String} 转 {@linkplain JSONObject}[]
     *
     * @param jsonString 需要转换的 JSON 字符串
     * @return JSON 数组
     * @see ListUtils#toJsons(String)
     */
    public static JSONObject[] toJsons(String jsonString) {
        return ListUtils.toJsons(jsonString);
    }

    /**
     * {@linkplain String} 转 {@linkplain JSONObject}[]
     * <blockquote>示例：
     * <pre>
     *    {@code
     * 		String text = "1,3,5,9";
     * 		JSONObject[] jsons = toJsons(text, ",", "id");
     * 		System.out.println(Arrays.toString(jsons));
     * }
     * </pre>
     * 结果：
     * [{"id":"1"}, {"id":"3"}, {"id":"5"}, {"id":"9"}]
     * </blockquote>
     *
     * @param text  需要转换的文本
     * @param regex 文本分割表达式，同{@linkplain String}类的 split() 方法
     * @param key   JSON 的 key 名称
     * @return 转换后的 jsons
     * @see ListUtils#toJsons(String, String, String)
     */
    public static JSONObject[] toJsons(String text, String regex, String key) {
        return ListUtils.toJsons(text, regex, key);
    }

}