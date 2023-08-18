package ext.library.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import ext.library.convert.Convert;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Map 工具类
 */
@Slf4j
public class MapUtils extends MapUtil {

    /**
     * 不可变的空 Json 常量
     */
    final static Dict FINAL_EMPTY_JSON = Dict.create();

    /**
     * 判断 Map 数据结构 key 的一致性
     *
     * @param paramMap        参数
     * @param mustContainKeys 必须包含的 key（必传）
     * @param canContainKeys  可包含的 key（非必传）
     * @return 是否满足条件
     */
    public static boolean isKeys(Map<String, Object> paramMap, String[] mustContainKeys, String... canContainKeys) {
        // 1. 必传参数校验
        for (String key : mustContainKeys) {
            if (!paramMap.containsKey(key)) {
                return false;
            }
        }

        // 2. 无可选参数
        if (StringUtils.isEmptys(canContainKeys)) {
            return true;
        }

        // 3. 可选参数校验 - 确认 paramMap 大小
        int keySize = mustContainKeys.length + canContainKeys.length;
        if (paramMap.size() > keySize) {
            return false;
        }

        // 4. 获得 paramMap 中包含可包含 key 的大小
        int paramMapCanContainKeysLength = 0;
        for (String key : canContainKeys) {
            if (paramMap.containsKey(key)) {
                paramMapCanContainKeysLength++;
            }
        }

        // 5. 确认 paramMap 中包含的可包含 key 大小 + 必须包含 key 大小 是否等于 paramMap 大小
        return paramMapCanContainKeysLength + mustContainKeys.length == paramMap.size();

        // 6. 通过所有校验，返回最终结果
    }

    /**
     * 判断 Map 数据结构所有的 key 是否与数组完全匹配
     *
     * @param paramMap 需要确认的 Map
     * @param keys     条件
     * @return 匹配所有的 key 且大小一致（true）
     */
    public static boolean isKeysEqual(Map<String, Object> paramMap, String[] keys) {
        if (paramMap.size() != keys.length) {
            return false;
        }
        for (String key : keys) {
            if (!paramMap.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断 Map 数据结构是否包含 <b>keys</b> 之一
     *
     * @param paramMap 需要确认的 Map
     * @param keys     条件
     * @return 只要包含一个 key（true）
     */
    public static boolean isContainsOneOfKey(Map<String, Object> paramMap, String[] keys) {
        for (String key : keys) {
            if (paramMap.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 Map 数组第一个元素，是否包含所有的 key<br>
     * <p>弱比较，只判断数组中第一个元素是否包含所有的 key</p>
     *
     * @param paramMaps 需要确认的 Map 数组
     * @param keys      条件数组
     * @return Map 数组元素 0 包含所有的 key（true）
     */
    public static boolean isMapsKeys(Map<String, Object>[] paramMaps, String[] keys) {
        return isKeys(paramMaps[0], keys);
    }

    /**
     * 判断 Map 数组是否为空<br>
     * <p>弱判断，只确定数组中第一个元素是否为空</p>
     *
     * @param paramMaps 要判断的 Map[] 数组
     * @return Map 数组==null 或长度==0 或第一个元素为空（true）
     */
    public static boolean isEmptys(Map<String, Object>[] paramMaps) {
        return null == paramMaps || paramMaps.length == 0 || paramMaps[0].isEmpty();
    }

    /**
     * 判断 Map 是否为空，或者 Map 中 String 类型的 value 值是否为空<br>
     *
     * @param paramMap 要判断的 Map
     * @return value 值是否为空
     */
    public static boolean isStringValueEmpty(Map<String, Object> paramMap) {
        if (paramMap.isEmpty()) {
            return true;
        }
        for (Object value : paramMap.values()) {
            if (null == value || "".equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除 Value 字符串前后空格
     *
     * @param paramMap 需要处理的 map
     */
    public static void trimStringValues(Map<String, Object> paramMap) {
        for (String key : paramMap.keySet()) {
            String str = getString(paramMap, key);
            String value = str.trim();
            if (!str.equals(value)) {
                paramMap.replace(key, value);
            }
        }
    }

    /**
     * 批量移除
     *
     * @param paramMap 要操作的 Map
     * @param keys     被移除的 key 数组
     */
    public static void remove(Map<String, Object> paramMap, String[] keys) {
        for (String key : keys) {
            paramMap.remove(key);
        }
    }

    /**
     * 移除空对象
     *
     * @param paramMap 要操作的 Map
     */
    public static void removeEmpty(Map<String, Object> paramMap) {
        Iterator<Entry<String, Object>> iter = paramMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Object> entry = iter.next();
            Object value = entry.getValue();
            if (ObjectUtils.isNull(value)) {
                iter.remove();
            }
        }
    }

    /**
     * 移除空白字符串
     * <p>空白的定义如下： <br>
     * 1、为 null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param paramMap 要操作的 Map
     */
    public static void removeBlankStr(Map<String, Object> paramMap) {
        Iterator<Entry<String, Object>> iter = paramMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Object> entry = iter.next();
            Object value = entry.getValue();
            if (StrUtil.isBlankIfStr(value)) {
                iter.remove();
            }
        }
    }

    /**
     * 替换 key
     *
     * @param paramMap   要操作的 Map
     * @param key        被替换的 key
     * @param replaceKey 替换的 key
     */
    public static void replaceKey(Map<String, Object> paramMap, String key, String replaceKey) {
        var value = paramMap.get(key);
        paramMap.put(replaceKey, value);
        paramMap.remove(key);
    }

    /**
     * 获取所有的 key
     *
     * @param paramMap 需要获取 keys 的 map
     * @return keyList
     */
    public static List<String> keyList(Map<String, Object> paramMap) {
        return new ArrayList<>(paramMap.keySet());
    }

    /**
     * 以安全的方式从 Map 中获取一组数据，组合成一个新的 Dict
     *
     * @param paramMap 需要从中获取数据的 map
     * @param keys     获取的 keys
     * @return 结果
     */
    public static Dict getDict(Map<String, Object> paramMap, String... keys) {
        Dict paramJson = new Dict(paramMap);
        if (!isContainsOneOfKey(paramJson, keys)) {
            return null;
        }

        Dict resultJson = new Dict();
        for (String key : keys) {
            Object value = paramJson.get(key);
            if (value != null) {
                resultJson.put(key, value);
            }
        }

        return resultJson;
    }

    /**
     * 以安全的方式从 Map 中获取对象
     *
     * @param <T>      泛型
     * @param paramMap 参数 map
     * @param key      key
     * @param clazz    泛型类型
     * @return 结果
     */
    public static <T> T getObject(final Map<?, ?> paramMap, final Object key, Class<T> clazz) {
        if (paramMap != null) {
            Object answer = paramMap.get(key);
            if (answer != null) {
                return Convert.toObject(answer, clazz);
            }
        }

        return null;
    }

    /**
     * 以安全的方式从 Map 中获取 Number
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static Number getNumber(final Map<?, ?> paramMap, final Object key) {
        if (paramMap != null) {
            Object answer = paramMap.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;

                } else if (answer instanceof String) {
                    try {
                        String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);

                    } catch (ParseException e) {
                        // failure means null is returned
                    }
                }
            }
        }
        return null;
    }

    /**
     * 以安全的方式从 Map 中获取字符串
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static String getString(final Map<?, ?> paramMap, final Object key) {
        if (paramMap != null) {
            Object answer = paramMap.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }

    /**
     * 以安全的方式从 Map 中获取 Boolean
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static Boolean getBoolean(final Map<?, ?> paramMap, final Object key) {
        if (paramMap != null) {
            Object answer = paramMap.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;

                } else if (answer instanceof String) {
                    return Boolean.valueOf((String) answer);

                } else if (answer instanceof Number n) {
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    /**
     * 以安全的方式从 Map 中获取 Integer
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static Integer getInteger(final Map<?, ?> paramMap, final Object key) {
        Number answer = getNumber(paramMap, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return answer.intValue();
    }

    /**
     * 以安全的方式从 Map 中获取 Long
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static Long getLong(final Map<?, ?> paramMap, final Object key) {
        Number answer = getNumber(paramMap, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return answer.longValue();
    }

    /**
     * 以安全的方式从 Map 中获取 Double
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static Double getDouble(final Map<?, ?> paramMap, final Object key) {
        Number answer = getNumber(paramMap, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Double) {
            return (Double) answer;
        }
        return answer.doubleValue();
    }

    /**
     * 以安全的方式从 Map 中获取 BigDecimal
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static BigDecimal getBigDecimal(final Map<?, ?> paramMap, final Object key) {
        Number answer = getNumber(paramMap, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Double) {
            return BigDecimal.valueOf((Double) answer);
        }
        return BigDecimal.valueOf(answer.doubleValue());
    }

    /**
     * 以安全的方式从 Map 中获取 Dict
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static Dict getDict(final Map<?, ?> paramMap, String key) {
        Object value = paramMap.get(key);
        return Convert.toDict(value);
    }

    /**
     * 以安全的方式从 Map 中获取 List
     *
     * @param paramMap 参数 map
     * @param key      key
     * @return 结果
     */
    public static List<Dict> getDictList(final Map<?, ?> paramMap, String key) {
        Object value = paramMap.get(key);
        return Convert.toDictList(value);
    }

    /**
     * <b>将指定值提取出来作为 map key,map 的值为相同 key 值的 list<b>
     * <p>
     * 例：一个用户集合中的对象有 key、name、sex
     * <p>
     * 数据 1：key：1，name：张三，sex：man
     * <p>
     * 数据 2：key：2，name：李四，sex:woman
     * <p>
     * 数据 3：key：3，name：王五，sex：man
     * <p>
     * 方法调用：ListPOJOExtractKeyToList(list,"sex");
     * <p>
     * 处理后返回结果为一个 map，值为一个 list,json 表示为：
     * <p>
     * {"man":[{"key":"1","name":"张三","sex":"man"},{"key":"3","name":"王五","sex":"man"}],
     * <p>
     * "woman":[{"key":"2","name":"李四","sex":"woman"}]}
     *
     * @param objectList 对象 list
     * @return key 为 map key 的键值对
     */
    public static <T> Map<String, List<T>> listPOJOExtractKeyToList(List<T> objectList, String key) {
        // 声明一个返回的 map 集合
        Map<String, List<T>> map = new LinkedHashMap<>();
        // 如果需要转换的值是空的，直接返回一个空的集合
        if (ListUtils.isEmpty(objectList)) {
            return map;
        }
        // 循环集合，转换为 map
        for (T item : objectList) {
            // 声明一个 object 对象接收 key 的值
            Object valueKey = null;
            try {
                // 通过对象和属性值获取对应的值
                valueKey = getValue(item, key);
            } catch (Exception e) {
                // 未找到方法值时不处理，采用默认的 null
                log.error("未找到方法值", e);
            }
            // 获取需要返回的 map 中是否已有该值的集合
            // 如果没有该值的集合，创建一个新的集合插入 map 中
            List<T> list = map.computeIfAbsent(valueKey == null ? null : valueKey.toString(), k -> new ArrayList<>());
            // 将该对象插入对应的集合中去
            list.add(item);
        }

        return map;
    }

    /**
     * <p>
     * 将 list 对象中数据提取为单个 map 键值对
     * <p>
     * 注：如果有相同的 key 时，后面的值会覆盖第一次出现的 key 对应的值
     * <p>
     * 例：一个用户集合中的对象有 key、name、sex
     * <p>
     * 数据 1：key：1，name：张三，sex：man
     * <p>
     * 数据 2：key：2，name：李四，sex:woman
     * <p>
     * 数据 3：key：3，name：王五，sex：man
     * <p>
     * 方法调用：ListPOJOExtractKeyToList(list,"key");
     * <p>
     * 处理后返回结果为一个 map，值为一个对象，json 表示为：
     * <p>
     * {"1":{"key":"1","name":"张三","sex":"man"},"2":{"key":"2","name":"李四","sex":"woman"},"3":{"key":"3","name":"王五","sex":"man"}}
     *
     * @param objectList list 数据
     * @param key        需要提取的 key
     * @param <T>        泛型对象
     * @return Map&lt;String, T&gt;
     */
    public static <T> Map<String, T> listPOJOExtractKeyToPOJO(List<T> objectList, String key) {
        // 声明一个返回的 map 集合
        Map<String, T> map = new LinkedHashMap<>();
        // 如果需要转换的值是空的，直接返回一个空的集合
        if (objectList == null || objectList.isEmpty()) {
            return map;
        }
        // 循环集合，转换为 map
        for (T item : objectList) {
            // 声明一个 object 对象接收 key 的值
            Object mapKey = null;
            try {
                // 通过对象和属性值获取对应的值
                mapKey = getValue(item, key);
            } catch (Exception e) {
                // 未找到方法值时不处理，采用默认的 null
                log.error("未找到方法值", e);
            }
            // 将取到的值作为 key，当前对象作为值，插入 map 中，如果有相同的 key 会覆盖之前的值
            map.put(mapKey == null ? null : mapKey.toString(), item);
        }

        return map;
    }

    /**
     * 获取
     */
    private static Object getValue(Object obj, String name) {
        // 判断是否键值对
        if (obj instanceof Dict dict) {
            return dict.get(name);
        } else if (obj instanceof Map map) {
            return map.get(name);
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());
        } catch (IntrospectionException e) {
            log.info("获取实体信息错误", e);
            return null;
        }
        // 获取所有属性
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            // 获取 get 方法
            Method readMethod = descriptor.getReadMethod();
            // 判断是否是需要的属性的 get 方法
            if (!StringUtils.equals(name, descriptor.getName())) {
                continue;
            }
            try {
                // 执行 get 方法拿到值
                return readMethod.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.info("获取值发生错误", e);
                return null;
            }
        }
        return null;
    }

}