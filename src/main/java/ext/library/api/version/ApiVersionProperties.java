package ext.library.api.version;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * RESTful API 接口版本定义自动配置属性
 */
@Data
@ConfigurationProperties(ApiVersionProperties.PREFIX)
public class ApiVersionProperties {
    static final String PREFIX = Constant.PROJECT_PREFIX + ".api-version";

    /**
     * 是否启用 <code style="color:red">RESTful API 接口版本控制</code>
     * <p>
     * 默认：true
     */
    boolean enabled = true;

    /**
     * 最小版本号，小于该版本号返回版本过时。
     */
    double minimumVersion;

    /**
     * {@link RequestMapping} 版本占位符，如下所示：
     * <p>/{version}/user
     * <p>/user/{version}
     */
    String versionPlaceholder = "{version}";

}