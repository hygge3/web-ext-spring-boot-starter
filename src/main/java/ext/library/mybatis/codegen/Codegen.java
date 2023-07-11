package ext.library.mybatis.codegen;

import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.JdbcTypeMapping;
import com.zaxxer.hikari.HikariDataSource;
import ext.library.mybatis.constant.DbConstant;
import ext.library.util.Assert;
import ext.library.util.StringUtils;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        GlobalConfig globalConfig = createGlobalConfig(config);

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);
        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfig(GenerateConfig config) {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // 注释配置
        globalConfig.getJavadocConfig()
                // 作者
                .setAuthor("Mybatis-Flex Codegen");

        // 包配置
        Assert.notBlank(config.getBasePackage(), "必须指定根包名");
        globalConfig.getPackageConfig()
                // 根包名
                .setBasePackage(config.getBasePackage());
        if (StringUtils.isNotBlank(config.getSourceDir())) {
            globalConfig.setSourceDir(config.getSourceDir());
        }

        // 策略配置
        globalConfig.getStrategyConfig()
                // 数据库表前缀，多个前缀用英文逗号（,）隔开
                .setTablePrefix(config.getTablePrefix())
                // 逻辑删除的默认字段名称
                .setLogicDeleteColumn(DbConstant.DB_FIELD_DELETE_TIME)
                // 生成哪些表，白名单
                .setGenerateTables(config.getGenerateTables())
                // 不生成哪些表，黑名单
                .setUnGenerateTables(config.getUnGenerateTables());
        // 某个列的全局配置
        // 创建时间
        ColumnConfig createTime = new ColumnConfig();
        createTime.setColumnName(DbConstant.DB_FIELD_CREATE_TIME);
        createTime.setOnInsertValue("NOW()");
        globalConfig.setColumnConfig(createTime);
        // 更新时间
        ColumnConfig updateTime = new ColumnConfig();
        updateTime.setColumnName(DbConstant.DB_FIELD_UPDATE_TIME);
        updateTime.setOnUpdateValue("NOW()");
        globalConfig.setColumnConfig(updateTime);
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

        // Entity 生成配置
        globalConfig.enableEntity()
                // Entity 类的父类，可以自定义一些 BaseEntity 类
                .setSupperClass(config.getEntitySupperClass())
                // 是否覆盖之前生成的文件
                .setOverwriteEnable(false)
                // Entity 默认实现的接口
                .setImplInterfaces()
                // Entity 是否使用 Lombok 注解
                .setWithLombok(true);

        // Mapper 生成配置
        globalConfig.enableMapper()
                // 是否添加 @Mapper 注解
                .setMapperAnnotation(true)
                // 是否覆盖之前生成的文件
                .setOverwriteEnable(true);

        // Service 生成配置
        globalConfig.enableService()
                // 是否覆盖之前生成的文件
                .setOverwriteEnable(false);

        // ServiceImpl 生成配置
        globalConfig.enableServiceImpl()
                // 是否覆盖之前生成的文件
                .setOverwriteEnable(false)
                // 是否添加缓存示例代码
                .setCacheExample(config.isAddCacheExample());

        // Controller 生成配置
        globalConfig.enableController()
                // 是否覆盖之前生成的文件
                .setOverwriteEnable(false);

        // TableDef 生成配置
        globalConfig.enableTableDef()
                // 是否覆盖之前生成的文件
                .setOverwriteEnable(true);


        // 默认类型映射
        JdbcTypeMapping.registerMapping(Timestamp.class, LocalDateTime.class);
        JdbcTypeMapping.registerMapping(Date.class, LocalDate.class);
        JdbcTypeMapping.registerMapping(Time.class, LocalTime.class);
        JdbcTypeMapping.registerMapping(BigInteger.class, Long.class);
        return globalConfig;
    }
}
