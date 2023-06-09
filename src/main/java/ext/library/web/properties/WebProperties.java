package ext.library.web.properties;

import ext.library.constant.Constant;
import ext.library.web.repeatedly.RepeatedlyReadServletRequestFilter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Web 自动配置属性
 */
@Data
@ConfigurationProperties(WebProperties.PREFIX)
public class WebProperties {

    /**
     * Prefix of {@link WebProperties}.
     */
    public static final String PREFIX = Constant.CONFIG_PREFIX + ".web";

    /**
     * 启用输入流可反复读取的 HttpServletRequest
     * <p>默认：true
     */
    private boolean enabledRepeatedlyReadServletRequest = true;

    /**
     * {@link RepeatedlyReadServletRequestFilter} 优先级
     * <p>值越小优先级越高
     * <p>默认：-999，比常规过滤器更高的优先级，防止输入流被更早读取
     */
    private int repeatedlyReadServletRequestFilterOrder = -999;

}
