package ext.library.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import ext.library.mybatis.entity.BaseEntity;
import io.github.linpeilie.Converter;

import java.util.List;

/**
 * Bean 解析工具类
 */
public class BeanUtils extends BeanUtil {

    static final String GET_METHOD_NAME_FORMAT = "get%s";
    static final String SET_METHOD_NAME_FORMAT = "set%s";

    /**
     * 基于 mapstruct 的集合转换
     *
     * @param source     来源
     * @param targetType 目标类型
     * @return {@link List}<{@link T}>
     */
    public static <T extends BaseEntity> List<T> convert(List<? extends BaseEntity> source, Class<T> targetType) {
        try {
            Converter converter = SpringUtils.getBean(Converter.class);
            return converter.convert(source, targetType);
        } catch (Exception e) {
            return copyToList(source, targetType);
        }
    }

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
