package ext.library.web.config;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import ext.library.idempotent.IdempotentInterceptorRegistry;
import ext.library.util.ClassUtils;
import ext.library.util.ListUtils;
import ext.library.util.StringUtils;
import ext.library.web.log.LogInterceptorRegistry;
import ext.library.web.properties.JacksonHttpMessageConverterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
@Import({IdempotentInterceptorRegistry.class, LogInterceptorRegistry.class})
public class WebMvcAutoConfig implements WebMvcConfigurer {
    final JacksonHttpMessageConverterProperties jacksonProperties;
    final IdempotentInterceptorRegistry idempotentInterceptorRegistry;
    final LogInterceptorRegistry logInterceptorRegistry;

    /**
     * 扩展 HTTP 消息转换器做 Json 解析处理
     */
    @Override
    public void extendMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        if (jacksonProperties.isEnabled()) {
            // 启用 ext-library 对 Jackson 进行增强配置
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = ListUtils.get(converters, MappingJackson2HttpMessageConverter.class);
            mappingJackson2HttpMessageConverterConfig(Objects.requireNonNull(mappingJackson2HttpMessageConverter));
            log.info("【消息转换器】配置项：{}，执行初始化 ...", JacksonHttpMessageConverterProperties.PREFIX);
        }
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
                    gen.writeString(StringUtils.EMPTY);
                    return;
                }

                gen.writeNull();
            }
        };

        objectMapper.getSerializerProvider().setNullValueSerializer(jsonSerializer);
    }

    /**
     * 添加自定义拦截器
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 添加日志拦截器
        if (Objects.nonNull(logInterceptorRegistry)) {
            logInterceptorRegistry.registry(registry);
        }
        // 添加幂等性拦截器
        if (Objects.nonNull(idempotentInterceptorRegistry)) {
            idempotentInterceptorRegistry.registry(registry);
        }
    }

}
