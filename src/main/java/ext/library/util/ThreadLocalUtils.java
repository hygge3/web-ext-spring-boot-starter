package ext.library.util;

import cn.hutool.core.lang.Dict;
import ext.library.constant.HttpAttribute;

/**
 * 当前线程工具
 */
public class ThreadLocalUtils {
    static final InheritableThreadLocal<Dict> THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 清空
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }

    /**
     * 放入数据
     *
     * @param key   键
     * @param value 值
     */
    protected static void put(String key, String value) {
        Dict dict = get();
        dict.put(key, value);
    }

    /**
     * 获取已有的值
     *
     * @return {@link Dict}
     */
    private static Dict get() {
        Dict dict = THREAD_LOCAL.get();
        if (ObjectUtils.isNull(dict)) {
            dict = Dict.create();
            THREAD_LOCAL.set(dict);
        }
        return dict;
    }

    /**
     * 得到 str
     *
     * @param key 关键
     * @return {@link String}
     */
    protected static String getStr(String key) {
        return THREAD_LOCAL.get().getStr(key);
    }

    public static String getIp() {
        return getStr(HttpAttribute.IP);
    }

    public static void setIp(String ip) {
        put(HttpAttribute.IP, ip);
    }

}