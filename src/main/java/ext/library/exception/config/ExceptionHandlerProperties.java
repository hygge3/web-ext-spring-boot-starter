package ext.library.exception.config;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局统一异常处理自动配置属性
 */
@Data
@ConfigurationProperties(ExceptionHandlerProperties.PREFIX)
public class ExceptionHandlerProperties {
    public static final String PREFIX = Constant.CONFIG_PREFIX + ".exception-handler";

    /**
     * 是否启用 <code style="color:red">全局统一异常处理</code> 自动配置
     * <p>
     * 默认：true
     */
    boolean enabled = true;

    /**
     * 重定向位置 URL（可以是绝对的或相对的）
     * the redirect location URL (maybe absolute or relative)
     */
    String location = "";

}
