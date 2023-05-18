package ext.library.config.exception;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局统一异常处理自动配置属性
 */
@Data
@ConfigurationProperties(ExceptionHandlerProperties.PREFIX)
public class ExceptionHandlerProperties {
    static final String PREFIX = Constant.PROJECT_PREFIX + ".exception-handler";

    /**
     * 是否启用 <code style="color:red">全局统一异常处理</code> 自动配置
     * <p>
     * 默认：true
     */
    private boolean enabled = true;

}