package ext.library.mybatis.entity;

import ext.library.convert.Convert;
import ext.library.util.SpringUtils;
import io.github.linpeilie.Converter;

import java.io.Serializable;

/**
 * <h2>RESTful 驼峰命名法基础实体</h2>
 */
public class BaseEntity implements Serializable {

    static final long serialVersionUID = 2241197545628586478L;

    /**
     * 转换
     *
     * @param clazz clazz
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