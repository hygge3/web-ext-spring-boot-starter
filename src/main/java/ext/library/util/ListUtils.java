package ext.library.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import ext.library.constant.MaxOrMinEnum;
import ext.library.constant.SortEnum;
import ext.library.convert.Convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * List 工具类
 */
public class ListUtils extends ListUtil {

    /**
     * 频率元素
     */
    static final String FREQUENCY = "frequency";

    /**
     * List 数组是否为空
     *
     * @param list list
     * @return 是否为空
     */
    public static boolean isEmpty(List<?> list) {
        return null == list || list.isEmpty();
    }

    /**
     * List 数组是否不为空
     *
     * @param list list
     * @return 是否不为空
     */
    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    /**
     * 获得 List 数组中对应类型的第一个值
     *
     * @param <T>   想要的类型
     * @param list  List 数组
     * @param clazz 想要的类型 Class
     * @return 找到的第一个值或 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(List<?> list, Class<T> clazz) {
        for (Object value : list) {
            if (value != null && value.getClass() == clazz) {
                return (T) value;
            }
        }

        return null;
    }

    /**
     * 数据分组
     * <p>将拥有相同的 key 值的 JSON 数据归为一组</p>
     *
     * @param list 要处理的集合
     * @param key  分组依据
     * @return 分组后的 list
     */
    public static <T> List<List<T>> grouping(List<T> list, String key) {
        List<List<T>> result = new ArrayList<>();
        toListAndDistinct(list, key).forEach(str -> {
            List<T> jsonList = new ArrayList<>();
            list.forEach(json -> {
                if (ObjectUtils.equals(str, BeanUtils.getFieldValue(json, key))) {
                    jsonList.add(json);
                }
            });
            result.add(jsonList);
        });
        return result;
    }

    /**
     * 保留相同值
     *
     * @param list  循环第一层
     * @param list2 循环第二层
     * @return 处理后的 list
     */
    public static List<String> keepSameValue(List<String> list, List<String> list2) {
        List<String> result = new ArrayList<>();
        list.forEach(str -> list2.forEach(str2 -> {
            if (StringUtils.equals(str, str2)) {
                result.add(str);
            }
        }));
        return result;
    }

    /**
     * 将 JSON 集合，合并到数组的 JSON 集合
     * <p>
     * 以条件 key 获得 ObjectNode 数组中每个对象的 value 作为 JSON 对象的 key，然后进行合并。<br>
     * JSON 对象 key 获得的值，应该是一个 ObjectNode 对象<br>
     * </p>
     * <blockquote>示例：
     * <pre>
     * 	List<Dict> list = [
     *        {
     * 			"id": 1,
     * 			"name": "name"
     *        }
     * 	]
     * 	Dict dict = {
     * 		1: {
     * 			"sex": "男",
     * 			"age": 18
     *        }
     *    }
     *
     * 	String key = "id";
     *
     * 	List<Dict> mergeResult = merge(list, dict, key);
     * 	System.out.println(mergeResult);
     * </pre>
     * 结果：
     * [{"id": 1, "name": "name", "sex": "男", "age": 18}]
     * </blockquote>
     *
     * @param list 数组
     * @param dict 对象
     * @param key  条件
     * @return 合并后的 ArrayNode
     */
    public static List<Dict> merge(List<Dict> list, Dict dict, String key) {
        list.forEach(item -> {
            Dict temp = Convert.toDict(item);
            String value = temp.getStr(key);
            temp.putAll(dict.getBean(value));
        });
        return list;
    }

    /**
     * List 合并
     * <p>将<b> list2 </b>合并到<b> list1 </b>里面
     *
     * @param list1 需要合并的列表
     * @param list2 被合并的列表
     * @param key1  list1 中对象所使用的 key
     * @param key2  list2 中对象所使用的 key
     */
    public static void merge(List<Dict> list1, List<Dict> list2, String key1, String key2) {
        list1.forEach(dict1 -> {
            Object value1 = dict1.get(key1);
            for (Dict dict2 : list2) {
                Object value2 = dict2.get(key2);
                if (ObjectUtils.equals(value1, value2)) {
                    dict2.remove(key2);
                    dict1.putAll(dict2);
                    break;
                }
            }
        });
    }

    /**
     * List 集合排序
     *
     * @param list     需要处理的集合
     * @param sortKey  排序依据（ObjectNode 的 key）
     * @param sortEnum 排序方式
     * @return 处理后的 List 集合
     */
    public static List<Dict> sort(List<Dict> list, String sortKey, SortEnum sortEnum) {
        list.sort((dict1, dict2) -> {
            var dict1value = dict1.get(sortKey);
            var dict2value = dict2.get(sortKey);
            if (sortEnum == SortEnum.ASC) {
                return CompareUtil.compare(dict1value, dict2value, false);
            } else {
                return CompareUtil.compare(dict2value, dict1value, false);
            }
        });

        return list;
    }

    /**
     * List-T 集合排序
     *
     * @param <T>       泛型
     * @param list      需要处理的集合
     * @param sortField 排序字段
     * @param sortEnum  排序方式
     * @return 处理后的 List 集合
     */
    public static <T> List<T> sortT(List<T> list, String sortField, SortEnum sortEnum) {
        list.sort((o1, o2) -> {
            Dict dict1 = Convert.toDict(o1);
            Dict dict2 = Convert.toDict(o2);
            var dict1value = dict1.get(sortField);
            var dict2value = dict2.get(sortField);
            if (sortEnum == SortEnum.ASC) {
                return CompareUtil.compare(dict1value, dict2value, false);
            } else {
                return CompareUtil.compare(dict2value, dict1value, false);
            }
        });

        return list;
    }

    /**
     * 反转集合
     *
     * @param <T>   泛型
     * @param list  需要处理的集合
     * @param clazz 集合元素类型
     * @return 反转后的 List 集合
     */
    public static <T> List<T> reverse(List<T> list, Class<T> clazz) {
        return ListUtils.toList(ArrayUtil.reverse(ArrayUtil.toArray(list, clazz)));
    }

    /**
     * HashSet 去重
     *
     * @param <T>  泛型
     * @param list 需要去重的 list
     */
    public static <T> List<T> distinct(List<T> list) {
        HashSet<T> h = new HashSet<>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * {@linkplain List} - {@linkplain T} value 去重
     * <p>根据参数 distinctKey 去重。
     *
     * @param list        需要处理的集合
     * @param distinctKey 去重的依据（JSON 的 key）
     * @return 处理后的 List 集合
     */
    public static <T> List<T> distinct(List<T> list, String distinctKey) {
        for (int i = 0; i < list.size(); i++) {
            T itemi = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                T itemj = list.get(j);
                if (Objects.equals(BeanUtils.getFieldValue(itemi, distinctKey), BeanUtils.getFieldValue(itemj, distinctKey))) {
                    list.remove(j);
                }
            }
        }

        return list;
    }

    /**
     * {@linkplain List}-{@linkplain Dict}集合去重统计与排序
     * <p>根据参数 distinctKey（Dict 的 key）计算元素重复次数，并为每个 ObjectNode 添加一个 <b>frequency</b>（频率元素），value 的值是从整数 1 开始计数。
     * <p>示例：<code>json.put("frequency", frequency)</code>
     * <p><b>根据 frequency（重复频率）排序</b>
     *
     * @param list        需要处理的集合
     * @param distinctKey 去重的依据（Dict 的 key）
     * @param sortEnum    排序方式
     * @return 处理后的 List 集合
     */
    public static List<Dict> distinctCount(List<Dict> list, String distinctKey, SortEnum sortEnum) {
        for (int i = 0; i < list.size(); i++) {
            int frequency = 1;
            Dict dicti = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                Dict dictj = list.get(j);
                if (Objects.equals(dicti.get(distinctKey), dictj.get(distinctKey))) {
                    list.remove(j);
                    frequency++;
                }
            }
            dicti.put(FREQUENCY, frequency);
        }

        return sort(list, FREQUENCY, sortEnum);
    }

    /**
     * {@linkplain List}-{@linkplain Dict}集合——去重、统计、排序与元素选择性保留
     * <p>根据参数 distinctKey（ObjectNode 的 key），计算元素重复次数。并为每个 ObjectNode 添加一个<b>frequency</b>（频率元素），value 的值是从整数 1 开始计数。
     * <p>示例：<code>json.put("frequency", frequency)</code>
     * <p><b>根据 frequency（重复频率）排序</b>
     *
     * @param list         需要处理的集合
     * @param distinctKey  去重的依据（Dict 的 key）
     * @param sortEnum     排序方式
     * @param keepKey      需要保留的重复元素（此参数必须为可判断的 Number 类型：根据 maxOrMinEnum 选择保留最大值 <i>或</i> 最小值）<b><i>如：</i></b>根据 id 去重，保留 age 为最大或最小的 ObjectNode
     * @param maxOrMinEnum 保留的值：最大值 <i>或</i> 最小值
     * @return 处理后的 List 集合
     */
    public static List<Dict> distinctCountSortSelectKeep(List<Dict> list, String distinctKey, SortEnum sortEnum, String keepKey, MaxOrMinEnum maxOrMinEnum) {
        for (int i = 0; i < list.size(); i++) {
            int frequency = 1;
            Dict dicti = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                Dict dictj = list.get(j);
                if (Objects.equals(dicti.get(distinctKey), dictj.get(distinctKey))) {
                    // i > j
                    if (CompareUtil.compare(dicti.get(keepKey), dictj.get(keepKey), false) > 0) {
                        if (maxOrMinEnum == MaxOrMinEnum.MIN) {
                            dicti.replace(keepKey, dictj.get(keepKey));
                        }
                    } else {
                        if (maxOrMinEnum == MaxOrMinEnum.MAX) {
                            dicti.replace(keepKey, dictj.get(keepKey));
                        }
                    }
                    list.remove(j);
                    frequency++;
                }
            }
            dicti.put(FREQUENCY, frequency);
        }

        return sort(list, FREQUENCY, sortEnum);
    }

    /**
     * 数组转 List
     * <p>此方法为 {@linkplain Arrays#asList(Object...)} 的安全实现</p>
     *
     * @param <T>   数组中的对象类
     * @param array 将被转换的数组
     */
    public static <T> ArrayList<T> toList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * {@linkplain List}-{@linkplain Dict} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>   泛型
     * @param list  需要转换的 List
     * @param clazz json 转换的 POJO 类型
     */
    public static <T> List<T> toList(List<Dict> list, Class<T> clazz) {
        List<T> toList = new ArrayList<>();
        for (Dict dict : list) {
            toList.add(dict.toBean(clazz));
        }
        return toList;
    }

    /**
     * {@linkplain List}-{@linkplain Dict} 转 {@linkplain List}-{@linkplain String}
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     */
    public static List<String> toList(List<Dict> list, String keepKey) {
        List<String> toList = new ArrayList<>();
        for (Dict dict : list) {
            String value = dict.getStr(keepKey);
            toList.add(value);
        }

        return toList;
    }

    /**
     * {@linkplain List}-{@linkplain Dict} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     */
    public static <T> List<T> toList(List<Dict> list, String keepKey, Class<T> clazz) {
        List<T> toList = new ArrayList<>();
        for (Dict dict : list) {
            toList.add(Convert.toObject(dict.get(keepKey), clazz));
        }

        return toList;
    }

    /**
     * {@linkplain List} - {@linkplain Dict} 转 {@linkplain List} - {@linkplain String} 并去除重复元素
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     */
    public static <T> List<String> toListAndDistinct(List<T> list, String keepKey) {
        List<String> toList = new ArrayList<>();
        for (T item : list) {
            Dict dict = Convert.toDict(item);
            String value = dict.getStr(keepKey);
            toList.add(value);
        }

        return distinct(toList);
    }

    /**
     * {@linkplain List}-{@linkplain Dict} 转 {@linkplain List}-{@linkplain Class} 并去除重复元素
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     */
    public static <T> List<T> toListAndDistinct(List<Dict> list, String keepKey, Class<T> clazz) {
        return distinct(toList(list, keepKey, clazz));
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
     */
    public static List<Dict> toDictList(List<Map<String, Object>> list) {
        List<Dict> dicts = new ArrayList<>();
        for (Map<String, Object> map : list) {
            dicts.add(new Dict(map));
        }

        return dicts;
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
     * @return 转换后的 List<Dict>
     */
    public static <T> List<Dict> toDictListT(List<T> list) {
        List<Dict> dicts = new ArrayList<>();
        for (T obj : list) {
            dicts.add(Convert.toDict(obj));
        }

        return dicts;
    }

    /**
     * {@linkplain List<Dict>} 转 {@linkplain Dict}[]
     * <p>对象引用转换，内存指针依旧指向元数据
     *
     * @param list 需要转换的 List<Dict>
     */
    public static Dict[] toDicts(List<Dict> list) {
        int size = list.size();
        Dict[] dicts = new Dict[size];
        for (int i = 0; i < size; i++) {
            dicts[i] = list.get(i);
        }

        return dicts;
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
     * @return 转换后的 jsons
     */
    public static <T> Dict[] toDictsT(List<T> list) {
        Dict[] dicts = new Dict[list.size()];
        int index = 0;
        for (T obj : list) {
            dicts[index] = Convert.toDict(obj);
            index++;
        }

        return dicts;
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
     * @return 转换后的 jsons
     */
    public static <T> Dict[] toDictsTAndRemoveEmpty(List<T> list) {
        Dict[] dicts = new Dict[list.size()];
        int index = 0;
        for (T obj : list) {
            Map<String, Object> map = Convert.toMap(String.class, Object.class, obj);
            MapUtils.removeEmpty(map);
            dicts[index] = Convert.toDict(map);
            index++;
        }

        return dicts;
    }

    /**
     * {@linkplain String} 转 {@linkplain Dict}[]
     *
     * @param jsonString 需要转换的 JSON 字符串
     * @return Dict 数组
     */
    public static Dict[] toDicts(String jsonString) {
        return toDicts(JsonUtils.parseDictList(jsonString));
    }

    /**
     * {@linkplain String} 转 {@linkplain Dict}[]
     * <blockquote>示例：
     * <pre>
     *    {@code
     * 		String text = "1,3,5,9";
     * 		Dict[] jsons = toDicts(text, ",", "id");
     * 		System.out.println(Arrays.toString(jsons));
     * }
     * </pre>
     * 结果：
     * [{"id":"1"}, {"id":"3"}, {"id":"5"}, {"id":"9"}]
     * </blockquote>
     *
     * @param text  需要转换的文本
     * @param regex 文本分割表达式，同{@linkplain String}类的 split() 方法
     * @param key   key 名称
     */
    public static Dict[] toDicts(String text, String regex, String key) {
        String[] texts = text.split(regex);
        Dict[] dicts = new Dict[texts.length];
        for (int i = 0; i < texts.length; i++) {
            Dict paramDict = Dict.create();
            paramDict.put(key, texts[i]);
            dicts[i] = paramDict;
        }

        return dicts;
    }

}