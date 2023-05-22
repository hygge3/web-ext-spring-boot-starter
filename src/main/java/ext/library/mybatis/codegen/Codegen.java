package ext.library.mybatis.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.JdbcTypeMapping;
import com.zaxxer.hikari.HikariDataSource;
import ext.library.mybatis.constant.DbConstant;
import ext.library.util.Assert;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
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

        Assert.notEmpty(config.getGenerateTables(), "必须指定需要生成代码的表");

        // 设置只生成哪些表
        globalConfig.setGenerateTables(config.getGenerateTables());
        globalConfig.setUnGenerateTables(config.getUnGenerateTables());

        // 设置代码生成目录
        Assert.notBlank(config.getSourceDir(), "必须指定代码生成目录");
        globalConfig.setSourceDir(config.getSourceDir());

        // 设置基础包名
        Assert.notBlank(config.getBasePackage(), "必须指定代码基础包名");
        globalConfig.setBasePackage(config.getBasePackage());

        // entity 配置
        globalConfig.setEntityWithLombok(false);
        globalConfig.setEntityInterfaces(new Class<?>[0]);
        if (Objects.nonNull(config.getEntitySupperClass())) {
            globalConfig.setEntitySupperClass(config.getEntitySupperClass());
        }

        // 设置表前缀
        globalConfig.setTablePrefix(config.getTablePrefix());

        // tableDef 设置
        globalConfig.setTableDefGenerateEnable(true);

        // 是否生成 mapper 类
        if (config.isMapperGenerateEnable()) {
            globalConfig.setMapperGenerateEnable(true);
            globalConfig.setMapperOverwriteEnable(true);
        }
        // 是否生成 service 接口
        if (config.isServiceGenerateEnable()) {
            globalConfig.setServiceGenerateEnable(true);
            globalConfig.setServiceOverwriteEnable(false);
        }
        // 是否生成 service 实现类
        if (config.isServiceImplGenerateEnable()) {
            globalConfig.setServiceImplGenerateEnable(true);
            globalConfig.setServiceImplOverwriteEnable(false);
        }
        // 是否生成 controller 类
        if (config.isControllerGenerateEnable()) {
            globalConfig.setControllerGenerateEnable(true);
            globalConfig.setControllerOverwriteEnable(false);
        }

        // 单独配置某个列
        // 创建时间
        ColumnConfig createTimeConfig = new ColumnConfig();
        createTimeConfig.setColumnName(DbConstant.DB_FIELD_CREATE_TIME);
        createTimeConfig.setOnInsertValue("now()");
        globalConfig.addColumnConfig(createTimeConfig);
        // 更新时间
        ColumnConfig updateTimeConfig = new ColumnConfig();
        updateTimeConfig.setColumnName(DbConstant.DB_FIELD_UPDATE_TIME);
        updateTimeConfig.setOnUpdateValue("now()");
        globalConfig.addColumnConfig(updateTimeConfig);
        // 删除时间
        globalConfig.setLogicDeleteColumn(DbConstant.DB_FIELD_DEFINITION_DELETE_TIME);
        // 大字段批量设置
        Set<String> largeColumns = config.getLargeColumns();
        for (String largeColumn : largeColumns) {
            ColumnConfig largeColumnConfig = new ColumnConfig();
            largeColumnConfig.setColumnName(largeColumn);
            largeColumnConfig.setLarge(true);
            globalConfig.addColumnConfig(largeColumnConfig);
        }

        // 默认类型映射
        JdbcTypeMapping.registerMapping(Timestamp.class, LocalDateTime.class);
        JdbcTypeMapping.registerMapping(BigInteger.class, Long.class);

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }
}