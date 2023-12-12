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

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({MybatisProperties.class})
public class MybatisAutoConfig implements MyBatisFlexCustomizer {
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
        log.info("【Mybatis-Flex】配置项：{}。执行初始化 ...", MybatisProperties.PREFIX);
    }

    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        log.info("【Mybatis-Flex】逻辑删除处理器。执行初始化 ...");
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
        log.info("【Mybatis-Flex】集成 PageHelper。执行初始化 ...");
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 分页合理化参数，默认值为 false。当该参数设置为 true 时，pageNum<=0 时会查询第一页，pageNum>pages（超过总数时），会查询最后一页。默认 false 时，直接根据参数进行查询。
        properties.setProperty("reasonable", "true");
        // 支持通过 Mapper 接口参数来传递分页参数，默认值 false，分页插件会从查询方法的参数值中，自动根据上面 params 配置的字段中取值，查找到合适的值时就会自动分页。
        properties.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(properties);
        return new PageInterceptor();
    }
}
