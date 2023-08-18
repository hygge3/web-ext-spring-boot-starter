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
    static final String PREFIX = Constant.CONFIG_PREFIX + ".mybatis";

    /**
     * 是否打开 SQL 执行日志
     * <p>
     * 默认：false
     */
    boolean sqlLog = false;

}