package ext.library.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class JsonUtils {
    /**
     * 建立 Json 操作中的日期格式
     */
    static final String STANDARD_FORMAT = DateUtils.DATE_TIME_FORMAT;

    /**
     * 建立 Jackson 的 ObjectMapper 对象
     */
    static final ObjectMapper MAPPER = new ObjectMapper();

    /*
     * 设置一些通用的属性
     */
    static {
        MAPPER.registerModule(new JavaTimeModule()).registerModule(new Jdk8Module()).registerModule(new ParameterNamesModule()).registerModules(ObjectMapper.findModules());
        // 是否允许使用注释
        // MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //字段允许去除引号
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //允许单引号
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //允许转义字符
        // MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //严格重复检测
        // MAPPER.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
        // 不输出空值字段
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //取消默认转换 timestamps 形式
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //时间输出为毫秒而非纳秒
        MAPPER.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        //忽略空 Bean 转 json 的错误
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为以下的样式，即 yyyy-MM-dd HH:mm:ss
        MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        // 反序列化
        //在 json 字符串中存在，但是在 java 对象中不存在对应属性的情况。防止错误
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //不检测失败字段映射
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //时间读取为毫秒而非纳秒
        MAPPER.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        //不输出空值字段
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

//        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

    }

    /**
     * json 副本
     * <br/>
     * 简单地直接用 json 复制或者转换 (Cloneable)
     *
     * @param obj    源对象
     * @param tClass 目标类型
     * @return {@link T}
     */
    public static <T> T jsonCopy(Object obj, Class<T> tClass) {
        return obj != null ? parseObject(toString(obj), tClass) : null;
    }

    /**
     * 对象序列化
     *
     * @param obj 源对象
     * @return {@link String}
     */
    public static <T> String toString(T obj) {
        return toString(obj, () -> "", false);
    }

    /**
     * 对象序列化，但是字符串会保证一定的结构性（提高可读性，增加字符串大小）
     *
     * @param obj 源对象
     * @return {@link String}
     */
    public static <T> String toStringPretty(T obj) {
        return toString(obj, () -> "", true);
    }

    /**
     * 对象序列化
     *
     * @param obj             源对象
     * @param defaultSupplier 提供默认结果的逻辑
     * @param pretty          格式美化，提高可读性
     * @return {@link String}
     */
    public static <T> String toString(T obj, Supplier<String> defaultSupplier, boolean pretty) {
        try {
            if (obj == null) {
                return defaultSupplier.get();
            }
            if (obj instanceof String str) {
                return str;
            }
            if (obj instanceof Number num) {
                return num.toString();
            }
            if (pretty) {
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Object to Json String error :{}", obj != null ? obj.toString() : "null", e);
        }
        return defaultSupplier.get();
    }

    /**
     * 对象反序列化
     *
     * @param jsonStr json 源字符串
     * @param tClass  目标对象的 Class
     * @return {@link T}
     */
    public static <T> T parseObject(String jsonStr, Class<T> tClass) {
        return parseObject(jsonStr, tClass, () -> null);
    }

    /**
     * 对象反序列化
     *
     * @param tClass 目标对象的 Class
     * @param obj    源对象
     * @return {@link T}
     */
    public static <T> T parseObject(Object obj, Class<T> tClass) {
        if (ObjectUtils.isNull(obj)) {
            return null;
        } else {
            return parseObject(toString(obj), tClass, () -> null);
        }
    }

    /**
     * 对象反序列化
     *
     * @param tClass          目标对象的 Class
     * @param defaultSupplier 提供默认结果的逻辑
     * @param jsonStr         json 源字符串
     * @return {@link T}
     */
    public static <T> T parseObject(String jsonStr, Class<T> tClass, Supplier<T> defaultSupplier) {
        try {
            if (StringUtils.isBlank(jsonStr)) {
                return defaultSupplier.get();
            }
            return MAPPER.readValue(jsonStr, tClass);
        } catch (Exception e) {
            log.error("Parse String to Object error : \n{}\n{}", jsonStr, tClass, e);
        }
        return defaultSupplier.get();
    }

    /**
     * List 类型反序列化
     *
     * @param jsonStr      json 源字符串
     * @param elementClass 集合中的元素类型
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> elementClass) {
        return parseList(jsonStr, elementClass, CollUtil::newArrayList);
    }

    /**
     * List 类型反序列化
     *
     * @param obj          源对象
     * @param elementClass 集合中的元素类型
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> parseList(Object obj, Class<T> elementClass) {
        if (ObjectUtils.isNull(obj)) {
            return CollUtil.newArrayList();
        } else {
            return parseList(toString(obj), elementClass, CollUtil::newArrayList);
        }
    }

    /**
     * List 类型反序列化
     *
     * @param jsonStr         json 源字符串
     * @param elementClass    集合中的元素类型
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> elementClass, Supplier<List<T>> defaultSupplier) {
        try {
            if (StringUtils.isBlank(jsonStr)) {
                return defaultSupplier.get();
            }
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, elementClass);
            return MAPPER.readValue(jsonStr, javaType);
        } catch (Throwable e) {
            log.error("Parse String to List error :\n{}\n{}", jsonStr, elementClass, e);
        }
        return defaultSupplier.get();
    }

    /**
     * Map 反序列化
     *
     * @param jsonStr json 源字符串
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> parseMap(String jsonStr) {
        return StringUtils.isNotBlank(jsonStr) ? parseMap(jsonStr, () -> MapUtils.createMap(HashMap.class)) : MapUtils.createMap(HashMap.class);
    }

    /**
     * Map 反序列化
     *
     * @param obj 源对象
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> parseMap(Object obj) {
        return parseMap(obj, () -> MapUtils.createMap(HashMap.class));
    }

    /**
     * Map 反序列化
     *
     * @param obj             源对象
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> parseMap(Object obj, Supplier<Map<String, Object>> defaultSupplier) {
        if (obj == null) {
            return defaultSupplier.get();
        }
        try {
            if (obj instanceof Map map) {
                return (Map<String, Object>) map;
            }
        } catch (Exception e) {
            log.info("fail to convert" + toString(obj), e);
        }
        return parseMap(toString(obj), defaultSupplier);
    }

    /**
     * Map 反序列化
     *
     * @param jsonStr         json 源字符串
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> parseMap(String jsonStr, Supplier<Map<String, Object>> defaultSupplier) {
        if (StringUtils.isBlank(jsonStr)) {
            return defaultSupplier.get();
        }
        try {
            return parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            log.error("Parse String to Map error :\n{}", jsonStr, e);
        }
        return defaultSupplier.get();
    }


    /**
     * Dict 反序列化
     *
     * @param jsonStr json 源字符串
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Dict parseDict(String jsonStr) {
        return StringUtils.isNotBlank(jsonStr) ? parseDict(jsonStr, Dict::create) : Dict.create();
    }

    /**
     * Dict 反序列化
     *
     * @param obj 源对象
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Dict parseDict(Object obj) {
        return parseDict(obj, Dict::create);
    }

    /**
     * Dict 反序列化
     *
     * @param obj             源对象
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Dict parseDict(Object obj, Supplier<Dict> defaultSupplier) {
        if (obj == null) {
            return defaultSupplier.get();
        }
        try {
            if (obj instanceof Dict dict) {
                return dict;
            }
        } catch (Exception e) {
            log.info("fail to convert" + toString(obj), e);
        }
        return parseDict(toString(obj), defaultSupplier);
    }

    /**
     * Dict 反序列化
     *
     * @param jsonStr         json 源字符串
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Dict parseDict(String jsonStr, Supplier<Dict> defaultSupplier) {
        if (StringUtils.isBlank(jsonStr)) {
            return defaultSupplier.get();
        }
        try {
            return parseObject(jsonStr, Dict.class);
        } catch (Exception e) {
            log.error("Parse String to Dict error :\n{}", jsonStr, e);
        }
        return defaultSupplier.get();
    }

    /**
     * Dict 集合反序列化
     *
     * @param jsonStr json 源字符串
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static List<Dict> parseDictList(String jsonStr) {
        return StringUtils.isNotBlank(jsonStr) ? parseDictList(jsonStr, CollUtil::newArrayList) : CollUtil.newArrayList();
    }

    /**
     * Dict 反序列化
     *
     * @param obj 源对象
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static List<Dict> parseDictList(Object obj) {
        return parseDictList(obj, CollUtil::newArrayList);
    }

    /**
     * Dict 反序列化
     *
     * @param obj             源对象
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static List<Dict> parseDictList(Object obj, Supplier<List<Dict>> defaultSupplier) {
        if (obj == null) {
            return defaultSupplier.get();
        }
        return parseDictList(toString(obj), defaultSupplier);
    }

    /**
     * Dict 反序列化
     *
     * @param jsonStr         json 源字符串
     * @param defaultSupplier 提供默认结果的逻辑
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static List<Dict> parseDictList(String jsonStr, Supplier<List<Dict>> defaultSupplier) {
        if (StringUtils.isBlank(jsonStr)) {
            return defaultSupplier.get();
        }
        try {
            return parseList(jsonStr, Dict.class);
        } catch (Exception e) {
            log.error("Parse String to Dict error :\n{}", jsonStr, e);
        }
        return defaultSupplier.get();
    }

}