package ext.library.web.properties;

import ext.library.constant.FieldNamingStrategyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jackson HTTP 消息转换器配置
 */
@Data
@ConfigurationProperties(JacksonHttpMessageConverterProperties.PREFIX)
public class JacksonHttpMessageConverterProperties {

    /**
     * Prefix of {@link JacksonHttpMessageConverterProperties}.
     */
    public static final String PREFIX = WebProperties.PREFIX + ".http-message-converter";

    /**
     * 启用 ext-library 对 Jackson 进行增强配置
     * <p>Jackson 是 SpringBoot 默认的 Json 解析器
     * <p>默认：false
     */
    boolean enabled = true;

    /**
     * 字段命名策略
     */
    FieldNamingStrategyEnum fieldNamingStrategy;

    /**
     * 输出 Null 值为空字符串
     * <p>默认：false
     */
    boolean writeNullAsStringEmpty = false;

    /**
     * Null String 输出为空字符串
     * <p>默认：true
     */
    boolean writeNullStringAsEmpty = true;

    /**
     * 输出 Null Map 为 {}
     * <p>默认：true
     */
    boolean writeNullMapAsEmpty = true;

    /**
     * Null List 输出为 []
     * <p>默认：true
     */
    boolean writeNullListAsEmpty = true;

    /**
     * 输出 Null Array 为 []
     * <p>默认：true
     */
    boolean writeNullArrayAsEmpty = true;

    /**
     * Null Boolean 输出为 false
     * <p>默认：true
     */
    boolean writeNullBooleanAsFalse = true;

    /**
     * Null Number 输出为 0
     * <p>默认：false
     */
    boolean writeNullNumberAsZero = false;

}
