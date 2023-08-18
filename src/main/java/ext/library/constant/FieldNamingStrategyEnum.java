package ext.library.constant;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
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
    CAMEL_CASE(PropertyNamingStrategies.LOWER_CAMEL_CASE),

    /**
     * 小驼峰命名法
     * <p>{@link #CAMEL_CASE}
     */
    LOWER_CAMEL_CASE(PropertyNamingStrategies.LOWER_CAMEL_CASE),

    /**
     * 大驼峰命名法
     * <p>{@link #PASCAL_CASE}
     */
    UPPER_CAMEL_CASE(PropertyNamingStrategies.UPPER_CAMEL_CASE),

    /**
     * 帕斯卡命名法，即：大驼峰命名法
     * <p>PASCAL_CASE 策略，Java 对象属性：personId，序列化后属性：PersonId
     */
    PASCAL_CASE(PropertyNamingStrategies.UPPER_CAMEL_CASE),

    /**
     * 下划线命名法
     * <p>SNAKE_CASE 策略，Java 对象属性：personId，序列化后属性：person_id
     */
    SNAKE_CASE(PropertyNamingStrategies.SNAKE_CASE),

    /**
     * 中划线命名法
     * <p>KEBAB_CASE 策略，Java 对象属性：personId，序列化后属性：person-id
     */
    KEBAB_CASE(PropertyNamingStrategies.KEBAB_CASE);

    final PropertyNamingStrategy propertyNamingStrategy;

}