package ext.library.config;

import ext.library.api.version.ApiVersionProperties;
import ext.library.api.version.ApiVersionRequestMappingHandlerMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * WebMvcRegistrations
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ApiVersionProperties.class)
public class WebMvcRegistrationsConfig implements WebMvcRegistrations {

    final ApiVersionProperties apiVersionProperties;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        if (!apiVersionProperties.isEnabled()) {
            return WebMvcRegistrations.super.getRequestMappingHandlerMapping();
        }

        log.info("【初始化配置-ApiVersionRequestMappingHandlerMapping】默认配置为 true，当前环境为 true：RESTful API 接口版本控制，执行初始化 ...");
        return new ApiVersionRequestMappingHandlerMapping(apiVersionProperties);
    }

}