package ext.library.mybatis.codegen;

import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.JdbcTypeMapping;
import com.mybatisflex.spring.service.impl.CacheableServiceImpl;
import com.zaxxer.hikari.HikariDataSource;
import ext.library.mybatis.constant.DbConstant;
import ext.library.util.Assert;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 代码生成
 */
public class Codegen {

    /**
     * 生成
     *
     * @param config 配置
     */
    public static void generate(GenerateConfig config) {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(config.getJdbcUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());

        // 创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // 注释配置
        // 作者
        globalConfig.setAuthor("Mybatis-Flex");

        // 包配置
        // 文件输出目录
        globalConfig.setSourceDir(config.getSourceDir());
        // 根包名
        Assert.notBlank(config.getBasePackage(), "必须指定根包名");
        globalConfig.setBasePackage(config.getBasePackage());

        // 策略模式
        // 设置表前缀，多个前缀用英文逗号（,）隔开
        globalConfig.setTablePrefix(config.getTablePrefix());
        // 设置某个列的全局配置
        // 创建时间
        ColumnConfig createTimeConfig = new ColumnConfig();
        createTimeConfig.setColumnName(DbConstant.DB_FIELD_CREATE_TIME);
        createTimeConfig.setOnInsertValue("now()");
        globalConfig.setColumnConfig(createTimeConfig);
        // 更新时间
        ColumnConfig updateTimeConfig = new ColumnConfig();
        updateTimeConfig.setColumnName(DbConstant.DB_FIELD_UPDATE_TIME);
        updateTimeConfig.setOnUpdateValue("now()");
        globalConfig.setColumnConfig(updateTimeConfig);
        // 删除标识
        globalConfig.setLogicDeleteColumn(DbConstant.DB_FIELD_DEFINITION_DELETE);
        // 大字段批量设置
        Set<String> largeColumns = config.getLargeColumns();
        if (CollUtil.isNotEmpty(largeColumns)) {
            for (String largeColumn : largeColumns) {
                ColumnConfig largeColumnConfig = new ColumnConfig();
                largeColumnConfig.setColumnName(largeColumn);
                largeColumnConfig.setLarge(true);
                globalConfig.setColumnConfig(largeColumnConfig);
            }
        }
        Assert.notEmpty(config.getGenerateTables(), "必须指定需要生成代码的表");
        // 生成哪些表，白名单
        globalConfig.setGenerateTables(config.getGenerateTables());
        // 不生成哪些表，黑名单
        globalConfig.setUnGenerateTables(config.getUnGenerateTables());

        // 模板配置 使用默认模板

        // Entity 生成配置
        // 是否覆盖之前生成的文件
        globalConfig.setEntityOverwriteEnable(true);
        // Entity 类的后缀
        globalConfig.setEntityClassSuffix("DO");
        // Entity 默认实现的接口
        globalConfig.setEntityInterfaces(new Class<?>[0]);
        // Entity 类的父类，可以自定义一些 BaseEntity 类
        globalConfig.setEntitySupperClass(config.getEntitySupperClass());

        // 是否生成 Mapper
        globalConfig.setMapperGenerateEnable(config.isMapperGenerateEnable());

        // 是否生成 Service
        globalConfig.setServiceGenerateEnable(config.isServiceGenerateEnable());
        globalConfig.setServiceOverwriteEnable(false);

        // 是否生成 ServiceImpl
        globalConfig.setServiceImplGenerateEnable(config.isServiceImplGenerateEnable());
        globalConfig.setServiceImplOverwriteEnable(false);
        if (config.isServiceCacheEnable()) {
            globalConfig.setServiceImplSupperClass(CacheableServiceImpl.class);
        }

        // 是否生成 Controller
        globalConfig.setControllerGenerateEnable(config.isControllerGenerateEnable());
        globalConfig.setControllerOverwriteEnable(false);

        // TableDef 设置
        globalConfig.setTableDefGenerateEnable(true);

        // MapperXml 生成配置
        globalConfig.setMapperXmlGenerateEnable(config.isMapperXmlGenerateEnable());

        // 默认类型映射
        JdbcTypeMapping.registerMapping(Timestamp.class, LocalDateTime.class);
        JdbcTypeMapping.registerMapping(BigInteger.class, Long.class);

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }
}