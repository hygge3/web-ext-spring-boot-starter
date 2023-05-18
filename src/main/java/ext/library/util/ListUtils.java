package ext.library.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
    public static List<List<JSONObject>> grouping(List<JSONObject> list, String key) {
        List<List<JSONObject>> result = new ArrayList<>();
        toListAndDistinct(list, key).forEach(str -> {
            List<JSONObject> jsonList = new ArrayList<>();
            list.forEach(json -> {
                if (Objects.equals(str, json.getString(key))) {
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
            if (Objects.equals(str, str2)) {
                result.add(str);
            }
        }));
        return result;
    }

    /**
     * 将 JSON 集合，合并到数组的 JSON 集合
     * <p>
     * 以条件 key 获得 JSONObject 数组中每个对象的 value 作为 JSON 对象的 key，然后进行合并。<br>
     * JSON 对象 key 获得的值，应该是一个 JSONObject 对象<br>
     * </p>
     * <blockquote>示例：
     * <pre>
     * 	JSONArray array = [
     *        {
     * 			"id": 1,
     * 			"name": "name"
     *        }
     * 	]
     * 	JSONObject json = {
     * 		1: {
     * 			"sex": "男",
     * 			"age": 18
     *        }
     *    }
     *
     * 	String key = "id";
     *
     * 	JSONArray mergeResult = merge(array, json, key);
     * 	System.out.println(mergeResult);
     * </pre>
     * 结果：
     * [{"id": 1, "name": "name", "sex": "男", "age": 18}]
     * </blockquote>
     *
     * @param array JSONObject 数组
     * @param json  JSON 对象
     * @param key   条件
     * @return 合并后的 JSONArray
     */
    public static JSONArray merge(JSONArray array, JSONObject json, String key) {
        array.forEach(arrayObj -> {
            JSONObject temp = Convert.toJSONObject(arrayObj);
            String value = temp.getString(key);
            temp.putAll(json.getJSONObject(value));
        });

        return array;
    }

    /**
     * List 合并
     * <p>将<b> list2 </b>合并到<b> list1 </b>里面
     *
     * @param list1 需要合并的列表
     * @param list2 被合并的列表
     * @param key1  list1 中 JSON 所使用的 key
     * @param key2  list2 中 JSON 所使用的 key
     */
    public static void merge(List<JSONObject> list1, List<JSONObject> list2, String key1, String key2) {
        list1.forEach(json1 -> {
            Object value1 = json1.get(key1);
            for (JSONObject json2 : list2) {
                Object value2 = json2.get(key2);
                if (ObjectUtils.equals(value1, value2)) {
                    json2.remove(key2);
                    json1.putAll(json2);
                    break;
                }
            }
        });
    }

    /**
     * List-JSONObject 集合排序
     *
     * @param list     需要处理的集合
     * @param sortKey  排序依据（JSONObject 的 key）
     * @param sortEnum 排序方式
     * @return 处理后的 List 集合
     */
    public static List<JSONObject> sort(List<JSONObject> list, String sortKey, SortEnum sortEnum) {
        list.sort((json1, json2) -> {
            var json1value = json1.get(sortKey);
            var json2value = json2.get(sortKey);
            if (sortEnum == SortEnum.ASC) {
                return CompareUtil.compare(json1value, json2value, false);
            } else {
                return CompareUtil.compare(json2value, json1value, false);
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
            JSONObject json1 = Convert.toJSONObject(o1);
            JSONObject json2 = Convert.toJSONObject(o2);
            var json1value = json1.get(sortField);
            var json2value = json2.get(sortField);
            if (sortEnum == SortEnum.ASC) {
                return CompareUtil.compare(json1value, json2value, false);
            } else {
                return CompareUtil.compare(json2value, json1value, false);
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
     * {@linkplain List} - {@linkplain JSONObject} value 去重
     * <p>根据参数 distinctKey 去重。
     *
     * @param list        需要处理的集合
     * @param distinctKey 去重的依据（JSON 的 key）
     * @return 处理后的 List 集合
     */
    public static List<JSONObject> distinct(List<JSONObject> list, String distinctKey) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsoni = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                JSONObject jsonj = list.get(j);
                if (Objects.equals(jsoni.get(distinctKey), jsonj.get(distinctKey))) {
                    list.remove(j);
                }
            }
        }

        return list;
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject}集合去重统计与排序
     * <p>根据参数 distinctKey（JSONObject 的 key）计算元素重复次数，并为每个 JSONObject 添加一个 <b>frequency</b>（频率元素），value 的值是从整数 1 开始计数。
     * <p>示例：<code>json.put("frequency", frequency)</code>
     * <p><b>根据 frequency（重复频率）排序</b>
     *
     * @param list        需要处理的集合
     * @param distinctKey 去重的依据（JSONObject 的 key）
     * @param sortEnum    排序方式
     * @return 处理后的 List 集合
     */
    public static List<JSONObject> distinctCount(List<JSONObject> list, String distinctKey, SortEnum sortEnum) {
        for (int i = 0; i < list.size(); i++) {
            int frequency = 1;
            JSONObject jsoni = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                JSONObject jsonj = list.get(j);
                if (Objects.equals(jsoni.get(distinctKey), jsonj.get(distinctKey))) {
                    list.remove(j);
                    frequency++;
                }
            }
            jsoni.put("frequency", frequency);
        }

        return sort(list, "frequency", sortEnum);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject}集合——去重、统计、排序与元素选择性保留
     * <p>根据参数 distinctKey（JSONObject 的 key），计算元素重复次数。并为每个 JSONObject 添加一个<b>frequency</b>（频率元素），value 的值是从整数 1 开始计数。
     * <p>示例：<code>json.put("frequency", frequency)</code>
     * <p><b>根据 frequency（重复频率）排序</b>
     *
     * @param list         需要处理的集合
     * @param distinctKey  去重的依据（JSONObject 的 key）
     * @param sortEnum     排序方式
     * @param keepKey      需要保留的重复元素（此参数必须为可判断的 Number 类型：根据 maxOrMinEnum 选择保留最大值 <i>或</i> 最小值）<b><i>如：</i></b>根据 id 去重，保留 age 为最大或最小的 JSONObject
     * @param maxOrMinEnum 保留的值：最大值 <i>或</i> 最小值
     * @return 处理后的 List 集合
     */
    public static List<JSONObject> distinctCountSortSelectKeep(List<JSONObject> list, String distinctKey, SortEnum sortEnum, String keepKey, MaxOrMinEnum maxOrMinEnum) {
        for (int i = 0; i < list.size(); i++) {
            int frequency = 1;
            JSONObject jsoni = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                JSONObject jsonj = list.get(j);
                if (Objects.equals(jsoni.get(distinctKey), jsonj.get(distinctKey))) {
                    // i > j
                    if (CompareUtil.compare(jsoni.get(keepKey), jsonj.get(keepKey), false) > 0) {
                        if (maxOrMinEnum == MaxOrMinEnum.MIN) {
                            jsoni.replace(keepKey, jsonj.get(keepKey));
                        }
                    } else {
                        if (maxOrMinEnum == MaxOrMinEnum.MAX) {
                            jsoni.replace(keepKey, jsonj.get(keepKey));
                        }
                    }
                    list.remove(j);
                    frequency++;
                }
            }
            jsoni.put("frequency", frequency);
        }

        return sort(list, "frequency", sortEnum);
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
     * {@linkplain JSONArray} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>       泛型
     * @param jsonArray 需要转换的 JSONArray
     * @param clazz     json 转换的 POJO 类型
     * @return 转换后的 List
     */
    public static <T> List<T> toList(JSONArray jsonArray, Class<T> clazz) {
        return jsonArray.toJavaList(clazz);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>   泛型
     * @param list  需要转换的 List
     * @param clazz json 转换的 POJO 类型
     */
    public static <T> List<T> toList(List<JSONObject> list, Class<T> clazz) {
        List<T> toList = new ArrayList<>();
        for (JSONObject json : list) {
            toList.add(Convert.toJavaBean(json, clazz));
        }

        return toList;
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain String}
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     */
    public static List<String> toList(List<JSONObject> list, String keepKey) {
        List<String> toList = new ArrayList<>();
        for (JSONObject json : list) {
            String value = json.getString(keepKey);
            toList.add(value);
        }

        return toList;
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     */
    public static <T> List<T> toList(List<JSONObject> list, String keepKey, Class<T> clazz) {
        List<T> toList = new ArrayList<>();
        for (JSONObject json : list) {
            toList.add(Convert.toObject(json.get(keepKey), clazz));
        }

        return toList;
    }

    /**
     * {@linkplain List} - {@linkplain JSONObject} 转 {@linkplain List} - {@linkplain String} 并去除重复元素
     *
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     */
    public static List<String> toListAndDistinct(List<JSONObject> list, String keepKey) {
        List<String> toList = new ArrayList<>();
        for (JSONObject json : list) {
            String value = json.getString(keepKey);
            toList.add(value);
        }

        return distinct(toList);
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class} 并去除重复元素
     *
     * @param <T>     泛型
     * @param list    需要转换的 List
     * @param keepKey 保留值的 key
     * @param clazz   类型
     */
    public static <T> List<T> toListAndDistinct(List<JSONObject> list, String keepKey, Class<T> clazz) {
        return distinct(toList(list, keepKey, clazz));
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
     */
    public static List<JSONObject> toJsonList(List<Map<String, Object>> list) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            jsonList.add(new JSONObject(map));
        }

        return jsonList;
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
     */
    public static List<JSONObject> toJsonList(JSONArray jsonArray) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonList.add(jsonArray.getJSONObject(i));
        }

        return jsonList;
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
     */
    public static <T> List<JSONObject> toJsonListT(List<T> list) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (T obj : list) {
            jsonList.add(Convert.toJSONObject(obj));
        }

        return jsonList;
    }

    /**
     * {@linkplain JSONArray} 转 {@linkplain JSONObject}[]
     * <p>对象引用转换，内存指针依旧指向元数据
     *
     * @param jsonArray 需要转换的 JSONArray
     */
    public static JSONObject[] toJsons(JSONArray jsonArray) {
        int size = jsonArray.size();
        JSONObject[] jsons = new JSONObject[size];
        for (int i = 0; i < size; i++) {
            jsons[i] = jsonArray.getJSONObject(i);
        }

        return jsons;
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain JSONObject}[]
     * <p>对象引用转换，内存指针依旧指向元数据
     *
     * @param list 需要转换的 List
     */
    public static JSONObject[] toJsons(List<JSONObject> list) {
        JSONObject[] jsons = new JSONObject[list.size()];
        int index = 0;
        for (JSONObject json : list) {
            jsons[index] = json;
            index++;
        }
        return jsons;
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
     */
    public static <T> JSONObject[] toJsonsT(List<T> list) {
        JSONObject[] jsons = new JSONObject[list.size()];
        int index = 0;
        for (T obj : list) {
            jsons[index] = Convert.toJSONObject(obj);
            index++;
        }

        return jsons;
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
     */
    public static <T> JSONObject[] toJsonsTAndRemoveEmpty(List<T> list) {
        JSONObject[] jsons = new JSONObject[list.size()];
        int index = 0;
        for (T obj : list) {
            JSONObject json = Convert.toJSONObject(obj);
            MapUtils.removeEmpty(json);
            jsons[index] = json;
            index++;
        }

        return jsons;
    }

    /**
     * {@linkplain String} 转 {@linkplain JSONObject}[]
     *
     * @param jsonString 需要转换的 JSON 字符串
     * @return JSON 数组
     */
    public static JSONObject[] toJsons(String jsonString) {
        return toJsons(JSONArray.parseArray(jsonString));
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
     */
    public static JSONObject[] toJsons(String text, String regex, String key) {
        String[] texts = text.split(regex);
        JSONObject[] jsons = new JSONObject[texts.length];
        for (int i = 0; i < texts.length; i++) {
            JSONObject paramJson = new JSONObject();
            paramJson.put(key, texts[i]);
            jsons[i] = paramJson;
        }

        return jsons;
    }

}