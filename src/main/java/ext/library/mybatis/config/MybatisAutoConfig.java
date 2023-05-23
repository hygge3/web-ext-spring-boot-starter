package ext.library.mybatis.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({MybatisProperties.class})
public class MybatisAutoConfig {
    final MybatisProperties mybatisProperties;

    @PostConstruct
    public void init() {
        FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();
        globalConfig.setPrintBanner(mybatisProperties.isBanner());
        if (mybatisProperties.isLog()) {
            // 开启审计功能
            AuditManager.setAuditEnable(true);
            // 设置 SQL 审计收集器
            MessageCollector collector = new ConsoleMessageCollector();
            AuditManager.setMessageCollector(collector);
        }
        log.info("【初始化配置 - Mybatis】配置项：{}... 已初始化完毕。", MybatisProperties.PREFIX);
    }

}