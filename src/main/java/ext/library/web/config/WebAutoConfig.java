package ext.library.web.config;

import ext.library.api.version.ApiVersionProperties;
import ext.library.argument.resolver.CustomArgumentResolversConfig;
import ext.library.argument.resolver.RepeatedlyReadServletRequestFilter;
import ext.library.log.LogProperties;
import ext.library.thread.pool.AsyncProperties;
import ext.library.thread.pool.ContextDecorator;
import ext.library.web.exception.ExceptionHandlerProperties;
import ext.library.web.properties.CookieProperties;
import ext.library.web.properties.CorsProperties;
import ext.library.web.properties.FastJsonHttpMessageConverterProperties;
import ext.library.web.properties.JacksonHttpMessageConverterProperties;
import ext.library.web.properties.WebProperties;
import ext.library.web.webenv.WebEnv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * web bean 自动配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Import({WebMvcConfig.class, WebMvcRegistrationsConfig.class, CustomArgumentResolversConfig.class, WebEnv.class})
@EnableConfigurationProperties({ApiVersionProperties.class, ExceptionHandlerProperties.class, CorsProperties.class, CookieProperties.class, LogProperties.class, WebProperties.class, JacksonHttpMessageConverterProperties.class, FastJsonHttpMessageConverterProperties.class})
public class WebAutoConfig {

    final WebProperties webProperties;

    // CorsConfig-跨域

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CorsProperties.PREFIX, name = "allow", havingValue = "true", matchIfMissing = true)
    public CorsFilter corsFilter(CorsProperties corsProperties) {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedOriginPatterns(List.of("*"));
        config.setMaxAge(3600L);

        // 设置 response 允许暴露的 Headers
        List<String> exposedHeaders = corsProperties.getExposedHeaders();
        if (exposedHeaders != null) {
            config.setExposedHeaders(exposedHeaders);
        } else {
            config.addExposedHeader("token");
        }

        source.registerCorsConfiguration("/**", config);

        log.info("【跨域】配置项：{}，初始化任何情况下都允许跨域访问...", CorsProperties.PREFIX);
        return new CorsFilter(source);
    }

    // 注册 Filter 并配置执行顺序

    /**
     * 配置输入流可反复读取的 HttpServletRequest
     */
    @Bean
    @ConditionalOnProperty(prefix = WebProperties.PREFIX, name = "enabled-repeatedly-read-servlet-request", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<RepeatedlyReadServletRequestFilter> registerRepeatedlyReadRequestFilter() {
        FilterRegistrationBean<RepeatedlyReadServletRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        // 设置比常规过滤器更高的优先级，防止输入流被更早读取
        filterRegistrationBean.setOrder(webProperties.getRepeatedlyReadServletRequestFilterOrder());
        filterRegistrationBean.setFilter(new RepeatedlyReadServletRequestFilter());
        log.info("【Body 反复读取】配置项：{}，初始化启用输入流可反复读取的 HttpServletRequest...", WebProperties.PREFIX + ".enabled-repeatedly-read-servlet-request");
        return filterRegistrationBean;
    }

    // 线程装饰器

    /**
     * Servlet 子线程上下文装饰器
     */
    @Bean
    @ConditionalOnMissingBean({TaskDecorator.class, ContextDecorator.class})
    @ConditionalOnBean(AsyncProperties.class)
    public TaskDecorator taskDecorator(AsyncProperties asyncProperties) {
        return new ContextDecorator(asyncProperties);
    }

}