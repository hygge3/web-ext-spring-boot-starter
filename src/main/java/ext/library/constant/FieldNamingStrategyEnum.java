package ext.library.constant;

import com.alibaba.fastjson2.PropertyNamingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段命名策略
 */
@Getter
@AllArgsConstructor
public enum FieldNamingStrategyEnum {

    /**
     * 驼峰命名法，即：小驼峰命名法
     * <p>CAMEL_CASE 策略，Java 对象属性：personId，序列化后属性：personId
     */
    CAMEL_CASE(PropertyNamingStrategy.CamelCase),

    /**
     * 小驼峰命名法
     * <p>{@link #CAMEL_CASE}
     */
    LOWER_CAMEL_CASE(PropertyNamingStrategy.CamelCase),

    /**
     * 大驼峰命名法
     * <p>{@link #PASCAL_CASE}
     */
    UPPER_CAMEL_CASE(PropertyNamingStrategy.PascalCase),

    /**
     * 帕斯卡命名法，即：大驼峰命名法
     * <p>PASCAL_CASE 策略，Java 对象属性：personId，序列化后属性：PersonId
     */
    PASCAL_CASE(PropertyNamingStrategy.PascalCase),

    /**
     * 下划线命名法
     * <p>SNAKE_CASE 策略，Java 对象属性：personId，序列化后属性：person_id
     */
    SNAKE_CASE(PropertyNamingStrategy.SnakeCase),

    /**
     * 中划线命名法
     * <p>KEBAB_CASE 策略，Java 对象属性：personId，序列化后属性：person-id
     */
    KEBAB_CASE(PropertyNamingStrategy.KebabCase);

    final PropertyNamingStrategy propertyNamingStrategy;

}