package ext.library.mybatis.config;

import com.github.pagehelper.PageInterceptor;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.logicdelete.impl.DateTimeLogicDeleteProcessor;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({MybatisProperties.class})
public class MybatisConfig implements MyBatisFlexCustomizer {
    final MybatisProperties mybatisProperties;

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        //我们可以在这里进行一些列的初始化配置
        if (mybatisProperties.isLog()) {
            // 开启审计功能
            AuditManager.setAuditEnable(true);
            // 设置 SQL 审计收集器
            MessageCollector collector = new ConsoleMessageCollector();
            AuditManager.setMessageCollector(collector);
        }
        log.info("【Mybatis-Flex】配置项：{}, 已初始化完毕。", MybatisProperties.PREFIX);
    }

    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        log.info("【Mybatis-Flex】逻辑删除处理已初始化完毕。");
        return new DateTimeLogicDeleteProcessor();
    }

    /**
     * 页面拦截器
     * 使用自动配置无效，
     * 需要手动注入 bean，以让 com.mybatisflex.spring.boot.MybatisFlexAutoConfiguration#MybatisFlexAutoConfiguration 正常获取到 com.github.pagehelper.PageInterceptor
     *
     * @return {@link PageInterceptor}
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        log.info("【Mybatis-Flex】PageHelper 处理已初始化完毕。");
        return new PageInterceptor();
    }
}
