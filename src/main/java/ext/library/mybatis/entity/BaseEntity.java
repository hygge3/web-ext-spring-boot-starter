package ext.library.mybatis.entity;

import ext.library.convert.Convert;
import ext.library.util.SpringUtils;
import io.github.linpeilie.Converter;

import java.io.Serializable;

/**
 * <h2>RESTful 驼峰命名法基础实体</h2>
 * <p>
 * 继承该类必须配置 mapstruct-plus
 */
public class BaseEntity implements Serializable {

    /**
     * 转换为目标类型
     *
     * @param clazz 目标类型
     * @return {@link T}
     */
    public <T> T convert(Class<T> clazz) {
        try {
            Converter converter = SpringUtils.getBean(Converter.class);
            return converter.convert(this, clazz);
        } catch (Exception e) {
            return Convert.convert(this, clazz);
        }
    }
}