package ext.library.web.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.BeanContext;
import com.alibaba.fastjson2.filter.ContextValueFilter;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.filter.NameFilter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import ext.library.argument.resolver.ArrayArgumentResolver;
import ext.library.argument.resolver.CustomRequestParamMethodArgumentResolver;
import ext.library.argument.resolver.JavaBeanArgumentResolver;
import ext.library.constant.FieldNamingStrategyEnum;
import ext.library.idempotent.IdempotentInterceptorRegistry;
import ext.library.util.ClassUtils;
import ext.library.util.DateUtils;
import ext.library.util.ListUtils;
import ext.library.web.properties.FastJsonHttpMessageConverterProperties;
import ext.library.web.properties.JacksonHttpMessageConverterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WebMvc 配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Import(IdempotentInterceptorRegistry.class)
public class WebMvcConfig implements WebMvcConfigurer {

    final FastJsonHttpMessageConverterProperties fastJsonProperties;
    final JacksonHttpMessageConverterProperties jacksonProperties;
    @Autowired(required = false)
    IdempotentInterceptorRegistry idempotentInterceptorRegistry;

    /**
     * 扩展 HTTP 消息转换器做 Json 解析处理
     */
    @Override
    public void extendMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        if (fastJsonProperties.isEnabled()) {
            // 使用 FastJson 优先于默认的 Jackson 做 json 解析
            // https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md
            fastJsonHttpMessageConverterConfig(converters);
        } else if (jacksonProperties.isEnabled()) {
            // 启用 ext-library 对 Jackson 进行增强配置
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = ListUtils.get(converters, MappingJackson2HttpMessageConverter.class);
            mappingJackson2HttpMessageConverterConfig(Objects.requireNonNull(mappingJackson2HttpMessageConverter));
        }
    }

    private void fastJsonHttpMessageConverterConfig(List<HttpMessageConverter<?>> converters) {
        // 1. 创建 FastJsonHttpMessageConverter
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setSupportedMediaTypes(CollUtil.newArrayList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json")));
        FastJsonConfig config = new FastJsonConfig();
        config.setDateFormat(DateUtils.DATE_TIME_FORMAT);
        config.setReaderFeatures(JSONReader.Feature.FieldBased // 基于字段反序列化，如果不配置，会默认基于 public 的 field 和 getter 方法序列化。配置后，会基于非 static 的 field（包括 private）做反序列化。在 fieldbase 配置下会更安全
                , JSONReader.Feature.SupportArrayToBean // 支持数据映射的方式
                , JSONReader.Feature.SupportSmartMatch // 默认下是 camel case 精确匹配，打开这个后，能够智能识别 camel/upper/pascal/snake/Kebab 五中 case
                , JSONReader.Feature.IgnoreSetNullValue // 忽略输入为 null 的字段
                , JSONReader.Feature.DuplicateKeyValueAsArray// 重复 Key 的 Value 不是替换而是组合成数组
        );
        config.setWriterFeatures(fastJsonProperties.getSerializerFeatures());

        // 2. 配置 FastJsonHttpMessageConverter 规则
        ContextValueFilter contextValueFilter = (BeanContext context, Object object, String name, Object value) -> {
            if (context == null) {
                if (fastJsonProperties.isWriteNullAsStringEmpty()) {
                    return StrUtil.EMPTY;
                }
                return value;
            }
            Class<?> fieldClass = context.getFieldClass();
            if (value != null || ClassUtils.isBasicType(fieldClass) || Collection.class.isAssignableFrom(fieldClass)) {
                return value;
            }
            if (fastJsonProperties.isWriteNullMapAsEmpty() && Map.class.isAssignableFrom(fieldClass)) {
                return new JSONObject();
            }
            return null;
        };

        // 3. 配置 FastJsonHttpMessageConverter
        List<Filter> filters = CollUtil.newArrayList();
        if (fastJsonProperties.isWriteNullAsStringEmpty() || fastJsonProperties.isWriteNullMapAsEmpty()) {
            filters.add(contextValueFilter);
        }
        FieldNamingStrategyEnum fieldNamingStrategy = fastJsonProperties.getFieldNamingStrategy();
        if (fieldNamingStrategy != null) {
            filters.add(NameFilter.of(fieldNamingStrategy.getPropertyNamingStrategy()));
        }
        config.setWriterFilters(ArrayUtil.toArray(filters, Filter.class));
        converter.setFastJsonConfig(config);
        converters.add(0, converter);
        log.info("【初始化配置-FastJsonHttpMessageConverter】默认配置为 false，当前环境为 true：使用 FastJson 优先于默认的 Jackson 做 json 解析 ... 已初始化完毕。");
    }

    private void mappingJackson2HttpMessageConverterConfig(MappingJackson2HttpMessageConverter converter) {
        ObjectMapper objectMapper = converter.getObjectMapper();
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                // 1. 确认 currentValue 是否有值
                JsonStreamContext outputContext = gen.getOutputContext();
                Object currentValue = outputContext.getCurrentValue();
                if (currentValue == null) {
                    writeNullOrEmpty(gen);
                    return;
                }

                // 2. 确认当前字段
                String currentName = outputContext.getCurrentName();
                Field field = null;
                try {
                    field = ReflectUtil.getField(currentValue.getClass(), currentName);
                } catch (Exception e) {
                    // 不做处理
                }
                if (field == null) {
                    writeNullOrEmpty(gen);
                    return;
                }

                // 3. 处理初始化类型
                Class<?> fieldClass = field.getType();
                if (jacksonProperties.isWriteNullStringAsEmpty() && CharSequence.class.isAssignableFrom(fieldClass)) {
                    gen.writeString(StrUtil.EMPTY);
                    return;
                } else if (jacksonProperties.isWriteNullNumberAsZero() && Number.class.isAssignableFrom(fieldClass)) {
                    gen.writeNumber(0);
                    return;
                } else if (jacksonProperties.isWriteNullBooleanAsFalse() && Boolean.class.isAssignableFrom(fieldClass)) {
                    gen.writeBoolean(false);
                    return;
                } else if (jacksonProperties.isWriteNullMapAsEmpty() && Map.class.isAssignableFrom(fieldClass)) {
                    gen.writeStartObject();
                    gen.writeEndObject();
                    return;
                } else if (jacksonProperties.isWriteNullArrayAsEmpty() && (fieldClass.isArray() || Collection.class.isAssignableFrom(fieldClass))) {
                    gen.writeStartArray();
                    gen.writeEndArray();
                    return;
                } else if (ClassUtils.isBasicType(fieldClass)) {
                    gen.writeNull();
                    return;
                }

                // 4. 其它类型处理
                writeNullOrEmpty(gen);
            }

            private void writeNullOrEmpty(JsonGenerator gen) throws IOException {
                if (jacksonProperties.isWriteNullAsStringEmpty()) {
                    gen.writeString(StrUtil.EMPTY);
                    return;
                }

                gen.writeNull();
            }
        };

        objectMapper.getSerializerProvider().setNullValueSerializer(jsonSerializer);
    }

    /**
     * 添加自定义方法参数解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JavaBeanArgumentResolver());
        resolvers.add(new ArrayArgumentResolver(true));
        resolvers.add(new CustomRequestParamMethodArgumentResolver(true));
    }

    /**
     * 添加自定义拦截器
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 添加幂等性拦截器
        if (idempotentInterceptorRegistry != null) {
            idempotentInterceptorRegistry.registry(registry);
        }
    }

    /**
     * 添加资源处理程序
     * <p>
     * 解决 knife4j 无法访问 doc.html 的问题
     *
     * @param registry 注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjar/**").addResourceLocations("classpath:/META-INF/resources/webjar/");
    }

}