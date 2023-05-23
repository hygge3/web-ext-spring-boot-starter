package ext.library.mybatis.config;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mybatis 自动配置属性
 */
@Data
@ConfigurationProperties(MybatisProperties.PREFIX)
public class MybatisProperties {
    static final String PREFIX = Constant.PROJECT_PREFIX + ".mybatis";

    /**
     * 是否打印 banner
     * <p>
     * 默认：true
     */
    boolean banner = true;

    /**
     * 是否启用执行日志
     * <p>
     * 默认：true
     */
    boolean log = true;

}