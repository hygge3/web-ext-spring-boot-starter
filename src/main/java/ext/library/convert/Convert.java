package ext.library.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import ext.library.util.DateUtils;
import ext.library.util.JsonUtils;
import ext.library.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * <b>类型转换器</b>
 * <p>提供简单全面的类型转换，适合更多的业务场景，内置 hutool、fastjson、ext 三种类型转换器，判断精确性能强大，未知类型兼容性更强
 */
@Slf4j
public class Convert extends cn.hutool.core.convert.Convert {

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
                return (T) DateUtils.parseLocalDateTime(str, DateUtils.DATE_FORMAT);
            } else if (clazz == LocalDateTime.class) {
                return (T) DateUtils.parseLocalDateTime(str, DateUtils.DATE_TIME_FORMAT);
            }
        }

        // 采用 json 转换
        try {
            return JsonUtils.parseObject(value, clazz);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("【Convert】采用 json 类型转换器转换失败，正尝试 hutool 类型转换器转换。", e);
            }
        }

        // 采用 hutool 默认转换能力 + ext-library 扩展能力进行转换
        return cn.hutool.core.convert.Convert.convert(clazz, value);
    }

    /**
     * <h2 style="color:red">转换值为指定 POJO 类型</h2>
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

        // 采用 json 转换
        try {
            return JsonUtils.parseObject(value, clazz);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("【Convert】采用 fastjson 类型转换器转换失败，正尝试 hutool 类型转换器转换。", e);
            }
        }

        // 采用 hutool 默认转换能力 + ext-library 扩展能力进行转换
        return BeanUtil.toBean(value, clazz);
    }

    /**
     * 转换为 JSONString
     *
     * @param value 被转换的值
     * @return JSON 字符串
     */
    public static String toJSONString(Object value) {
        return JsonUtils.toString(value);
    }

    /**
     * 转换为 {@linkplain Dict}
     *
     * @param value 被转换的值
     * @return Dict 对象
     */
    public static Dict toDict(Object value) {
        if (value instanceof Dict dict) {
            return dict;
        }

        if (value instanceof String str) {
            return JsonUtils.parseDict(str);
        }

        return Dict.parse(value);
    }

    /**
     * 转换为 {@linkplain List<Dict>}
     *
     * @param value 被转换的值
     * @return Dict 数组
     */
    public static List<Dict> toDictList(Object value) {
        return JsonUtils.parseDictList(value);
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
     * {@linkplain List<Dict>} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>          泛型
     * @param list         需要转换的 List
     * @param elementClass 集合中的元素类型
     * @return 转换后的 List
     */
    public static <T> List<T> toList(List<Dict> list, Class<T> elementClass) {
        List<T> result = CollUtil.newArrayList();
        for (Dict dict : list) {
            result.add(dict.toBean(elementClass));
        }
        return result;
    }

    /**
     * {@linkplain List}-{@linkplain Dict} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     * @return 转换后的 List
     * @see ListUtils#toList(List, String, Class)
     */
    public static <T> List<T> toList(List<Dict> list, String keepKey, Class<T> clazz) {
        return ListUtils.toList(list, keepKey, clazz);
    }

    /**
     * {@linkplain List} - {@linkplain Dict} 转 {@linkplain List} - {@linkplain String}并去除重复元素
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @return 处理后的 List
     * @see ListUtils#toListAndDistinct(List, String)
     */
    public static List<String> toListAndDistinct(List<Dict> list, String keepKey) {
        return ListUtils.toListAndDistinct(list, keepKey);
    }

    /**
     * {@linkplain List}-{@linkplain Dict} 转 {@linkplain List}-{@linkplain Class}并去除重复元素
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     * @return 处理后的 List
     * @see ListUtils#toListAndDistinct(List, String, Class)
     */
    public static <T> List<T> toListAndDistinct(List<Dict> list, String keepKey, Class<T> clazz) {
        return ListUtils.toListAndDistinct(list, keepKey, clazz);
    }

    /**
     * {@linkplain List} - {@linkplain Map} 转 {@linkplain List} - {@linkplain Dict}
     * <p>
     * <b><i>性能测试说明：</i></b><br>
     * <i>测试 CPU：</i>i7-4710MQ<br>
     * <i>测试结果：</i>百万级数据平均 200ms（毫秒）<br>
     * </p>
     *
     * @param list 需要转换的 List
     * @return 转换后的 List
     * @see ListUtils#toDictList(List)
     */
    public static List<Dict> toDictList(List<Map<String, Object>> list) {
        return ListUtils.toDictList(list);
    }

    /**
     * {@linkplain List} - {@linkplain Class} 转 {@linkplain List} - {@linkplain Dict}
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param <T>  泛型
     * @param list 需要转换的 List
     * @return 转换后的 DictList
     * @see ListUtils#toDictListT(List)
     */
    public static <T> List<Dict> toDictListT(List<T> list) {
        return ListUtils.toDictListT(list);
    }

    /**
     * {@linkplain List} - {@linkplain Class} 转 {@linkplain Dict}[]
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param <T>  泛型
     * @param list 需要转换的 List
     * @return 转换后的 Dicts
     * @see ListUtils#toDictsT(List)
     */
    public static <T> Dict[] toDictsT(List<T> list) {
        return ListUtils.toDictsT(list);
    }

    /**
     * {@linkplain List} - {@linkplain Class} 转 {@linkplain Dict}[] 并移除空对象
     * <p>
     * <b><i>性能测试报告：</i></b><br>
     * <i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param <T>  泛型
     * @param list 需要转换的 List
     * @return 转换后的 Dicts
     * @see ListUtils#toDictsTAndRemoveEmpty(List)
     */
    public static <T> Dict[] toDictsTAndRemoveEmpty(List<T> list) {
        return ListUtils.toDictsTAndRemoveEmpty(list);
    }

    /**
     * {@linkplain String} 转 {@linkplain Dict}[]
     *
     * @param jsonString 需要转换的 JSON 字符串
     * @return Dict 数组
     * @see ListUtils#toDicts(String)
     */
    public static Dict[] toDicts(String jsonString) {
        return ListUtils.toDicts(jsonString);
    }

    /**
     * {@linkplain String} 转 {@linkplain Dict}[]
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
     * @see ListUtils#toDicts(String, String, String)
     */
    public static Dict[] toDicts(String text, String regex, String key) {
        return ListUtils.toDicts(text, regex, key);
    }

}