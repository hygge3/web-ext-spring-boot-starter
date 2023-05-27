package ext.library.web.config;

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
        if (apiVersionProperties.isEnabled()) {
            log.info("【RESTful API 接口版本控制】配置项：{}。执行初始化 ...", ApiVersionProperties.PREFIX);
            return new ApiVersionRequestMappingHandlerMapping(apiVersionProperties);
        }
        return WebMvcRegistrations.super.getRequestMappingHandlerMapping();
    }

}