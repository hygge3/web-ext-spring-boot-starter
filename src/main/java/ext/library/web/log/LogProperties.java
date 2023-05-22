package ext.library.web.log;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局统一异常处理自动配置属性
 */
@Data
@ConfigurationProperties(LogProperties.PREFIX)
public class LogProperties {
    public static final String PREFIX = Constant.PROJECT_PREFIX + ".log";

    /** 是否启用辅助日志 */
    boolean enabled = true;
    /**
     * 是否启用请求日志
     * <p>
     * 默认：true
     */
    boolean request = true;

    /**
     * 是否启用链路追踪
     * <p>
     * 默认：true
     */
    boolean trace = true;

}