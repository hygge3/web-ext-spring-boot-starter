package ext.library.web.properties;

import com.alibaba.fastjson2.JSONWriter;
import ext.library.constant.FieldNamingStrategyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FastJson HTTP 消息转换器配置
 */
@Data
@ConfigurationProperties(FastJsonHttpMessageConverterProperties.PREFIX)
public class FastJsonHttpMessageConverterProperties {

    /**
     * Prefix of {@link FastJsonHttpMessageConverterProperties}.
     */
    static final String PREFIX = HttpMessageConverterProperties.PREFIX + ".fastjson";

    /**
     * 启用 FastJson 优先于默认的 Jackson 做 json 解析
     * <p>默认：false
     */
    boolean enabled = true;

    /**
     * 字段命名策略
     */
    FieldNamingStrategyEnum fieldNamingStrategy;

    /**
     * 自定义序列化特性
     * <p>HTTP 序列化时对 null 值进行初始化处理，默认做如下配置：
     */
    JSONWriter.Feature[] serializerFeatures = {JSONWriter.Feature.WriteNulls // 序列化输出空值字段
            , JSONWriter.Feature.BrowserCompatible // 在大范围超过 JavaScript 支持的整数，输出为字符串格式
            , JSONWriter.Feature.WriteEnumUsingToString // 序列化 enum 使用 toString 方法
            , JSONWriter.Feature.IgnoreErrorGetter // 忽略 setter 方法的错误
            , JSONWriter.Feature.WriteBigDecimalAsPlain // 序列化 BigDecimal 使用 toPlainString，避免科学计数法
            , JSONWriter.Feature.MapSortField // 对 Map 中的 KeyValue 按照 Key 做排序后再输出。在有些验签的场景需要使用这个 Feature
            , JSONWriter.Feature.WriteNonStringKeyAsString // 将 Map 中的非 String 类型的 Key 当做 String 类型输出
            , JSONWriter.Feature.WriteNullStringAsEmpty // 将 String 类型字段的空值序列化输出为空字符串""
            , JSONWriter.Feature.WriteNullListAsEmpty // 将 List 类型字段的空值序列化输出为空数组"[]"
            , JSONWriter.Feature.LargeObject // 这个是一个保护措施，是为了防止序列化有循环引用对象消耗过大资源的保护措施
            , JSONWriter.Feature.WriteMapNullValue};


    /**
     * 输出 Null 值为空字符串
     * <p>默认：false
     */
    boolean writeNullAsStringEmpty = false;

    /**
     * 输出 Null Map 为 {}
     * <p>默认：true
     */
    boolean writeNullMapAsEmpty = true;

}