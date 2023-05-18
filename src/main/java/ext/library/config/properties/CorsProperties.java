package ext.library.config.properties;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * 跨域自动配置属性
 */
@Data
@ConfigurationProperties(CorsProperties.PREFIX)
public class CorsProperties {

    static final String PREFIX = Constant.PROJECT_PREFIX + ".cors";


    /**
     * 是否允许 <code style="color:red">跨域</code>
     * <p>
     * 默认：true
     */
    private boolean allow = true;

    /**
     * response 允许暴露的 Headers
     * <p>
     * 默认：{@linkplain CorsConfiguration#setExposedHeaders(List)}
     * ({@code Cache-Control}, {@code Content-Language}, {@code Content-Type},
     * {@code Expires}, {@code Last-Modified}, or {@code Pragma})
     * 此外在此基础之上还额外的增加了一个名为 <b>token</b> 的 Headers
     */
    private List<String> exposedHeaders;

}