package ext.library.util;

import org.hibernate.validator.internal.util.CollectionHelper;

import java.util.Map;

/**
 * 线程本地工具
 *
 * @author Hygge
 * @since 1.0.0
 */
public class ThreadLocalUtils {

    /**
     * 注意右边 new 的不是原生的 ThreadLocal，而是 TransmittableThreadLocal，
     *
     * @see InheritableThreadLocal
     */
    private final static ThreadLocal<Map<String, Object>> THREAD_CONTEXT = new InheritableThreadLocal<>() {
        @Override
        protected Map<String, Object> initialValue() {
            return CollectionHelper.newHashMap();
        }
    };

    /**
     * 根据 key 获取 value
     * 比如 key 为 USER_INFO，则返回"{'name':'bravo', 'age':18}"
     * {
     * ...THREAD_CONTEXT: {
     * ........."USER_INFO":"{'name':'bravo', 'age':18}",
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     *
     * @param key 键
     * @return {@link Object}
     */
    public static Object get(String key) {
        // getContextMap() 表示要先获取 THREAD_CONTEXT 的 value，也就是 Map<String, Object>。
        // 然后再从 Map<String, Object>中根据 key 获取
        return getContextMap().get(key);
    }

    /**
     * 根据 key 获取 value
     * 比如 key 为 USER_INFO，则返回"{'name':'bravo', 'age':18}"
     * {
     * ...THREAD_CONTEXT: {
     * ........."USER_INFO":"{'name':'bravo', 'age':18}",
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     *
     * @param key 键
     * @return {@link Object}
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        // getContextMap() 表示要先获取 THREAD_CONTEXT 的 value，也就是 Map<String, Object>。
        // 然后再从 Map<String, Object>中根据 key 获取
        return (T) getContextMap().get(key);
    }

    /**
     * put 操作，原理同上
     *
     * @param key 键
     * @param value 值
     */
    public static void put(String key, Object value) {
        getContextMap().put(key, value);
    }

    /**
     * 清除 map 里的某个值
     * 比如把
     * {
     * ...THREAD_CONTEXT: {
     * ........."USER_INFO":"{'name':'bravo', 'age':18}",
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     * 变成
     * {
     * ...THREAD_CONTEXT: {
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     *
     * @param key 键
     * @return 值
     */
    public static Object remove(String key) {
        return getContextMap().remove(key);
    }

    /**
     * 清除整个 Map<String, Object>
     * 比如把
     * {
     * ...THREAD_CONTEXT: {
     * ........."USER_INFO":"{'name':'bravo', 'age':18}",
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     * 变成
     * {
     * ...THREAD_CONTEXT: {}
     * }
     */
    public static void remove() {
        getContextMap().clear();
    }

    /**
     * 从 ThreadLocalMap 中清除当前 ThreadLocal 存储的内容
     * 比如把
     * {
     * ...THREAD_CONTEXT: {
     * ........."USER_INFO":"{'name':'bravo', 'age':18}",
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     * 变成
     * {
     * }
     */
    public static void clear() {
        THREAD_CONTEXT.remove();
    }

    /**
     * 从 ThreadLocalMap
     * {
     * ...THREAD_CONTEXT: {
     * ........."USER_INFO":"{'name':'bravo', 'age':18}",
     * ........."SCORE":"{'Math':99, 'English': 97}"
     * ......}
     * }
     * 中获取 Map<String, Object>
     * {
     * ..."USER_INFO":"{'name':'bravo', 'age':18}",
     * ..."SCORE":"{'Math':99, 'English': 97}"
     * }
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    private static Map<String, Object> getContextMap() {
        return THREAD_CONTEXT.get();
    }

}
