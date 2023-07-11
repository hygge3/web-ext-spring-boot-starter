package ext.library.web.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import ext.library.util.DateUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * RequestBody 日期时间反序列化配置
 */
@Configuration
public class DateTimeFormatConfig {

    /**
     * 关于日期时间反序列化，只有在使用 {@link RequestBody} 时有效
     *
     * @return 自定义序列化器
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateUtils.DATE_TIME_FORMATTER))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DateUtils.DATE_FORMATTER))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(DateUtils.TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateUtils.DATE_TIME_FORMATTER))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateUtils.DATE_FORMATTER))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateUtils.TIME_FORMATTER));
    }

    // ~ 没有使用 {@link RequestBody} 反序列化时生效
    // ================================================================================================

    /**
     * 日期参数接收转换器，将 json 字符串转为日期类型
     *
     * @return MVC LocalDateTime 参数接收转换器
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(@NonNull String source) {
                return LocalDateTime.parse(source, DateUtils.DATE_TIME_FORMATTER);
            }
        };
    }

    /**
     * 日期参数接收转换器，将 json 字符串转为日期类型
     *
     * @return MVC LocalDate 参数接收转换器
     */
    @Bean
    public Converter<String, LocalDate> localDateConvert() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(@NonNull String source) {
                return LocalDate.parse(source, DateUtils.DATE_FORMATTER);
            }
        };
    }

    /**
     * 日期参数接收转换器，将 json 字符串转为日期类型
     *
     * @return MVC LocalTime 参数接收转换器
     */
    @Bean
    public Converter<String, LocalTime> localTimeConvert() {
        return new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(@NonNull String source) {
                return LocalTime.parse(source, DateUtils.TIME_FORMATTER);
            }
        };
    }

}
