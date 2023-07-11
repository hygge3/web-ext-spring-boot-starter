package ext.library.web.log;

import ext.library.web.properties.WebProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局统一异常处理自动配置属性
 */
@Data
@ConfigurationProperties(LogProperties.PREFIX)
public class LogProperties {

    /**
     * Prefix of {@link LogProperties}.
     */
    public static final String PREFIX = WebProperties.PREFIX + ".log";

    /**
     * 是否启用辅助日志
     */
    boolean enabled = false;

    /**
     * 是否启用请求日志
     * <p>
     * 默认：true
     */
    boolean request = false;

    /**
     * 是否启用链路追踪
     * <p>
     * 默认：true
     */
    boolean trace = false;

}
