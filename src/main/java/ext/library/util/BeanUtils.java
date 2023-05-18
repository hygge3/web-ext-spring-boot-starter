package ext.library.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Bean 解析工具类
 */
public class BeanUtils extends BeanUtil {

    public static final String GET_METHOD_NAME_FORMAT = "get%s";
    public static final String SET_METHOD_NAME_FORMAT = "set%s";

    /**
     * 获得 Java Bean get 方法名
     *
     * @param fieldName 字段名
     * @return get 方法名
     */
    public static String getGetMethodName(String fieldName) {
        return String.format(GET_METHOD_NAME_FORMAT, StrUtil.upperFirst(fieldName));
    }

    /**
     * 获得 Java Bean set 方法名
     *
     * @param fieldName 字段名
     * @return set 方法名
     */
    public static String getSetMethodName(String fieldName) {
        return String.format(SET_METHOD_NAME_FORMAT, StrUtil.upperFirst(fieldName));
    }

}