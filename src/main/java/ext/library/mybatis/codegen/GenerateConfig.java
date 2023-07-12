package ext.library.mybatis.codegen;

import ext.library.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class GenerateConfig {
    /**
     * jdbc url
     */
    final String jdbcUrl;
    /**
     * 用户名
     */
    final String username;
    /**
     * 密码
     */
    final String password;
    // 代码生成目录
    String sourceDir = "src/main/java";
    // 根包名
    final String basePackage;
    // entity 类的父类，可以自定义一些 BaseEntity 类
    Class<?> entitySupperClass = BaseEntity.class;
    // 数据库表前缀，多个前缀用英文逗号（,）隔开
    String tablePrefix;
    // 大字段字段名称列表
    Set<String> largeColumns;
    /**
     * 租户列表
     */
    String tenantColumn;
    /**
     * 是否添加缓存示例代码
     */
    boolean addCacheExample = false;
    // 是否生成 controller 类
    boolean controllerGenerateEnable = true;
    // 生成那些表，白名单
    final Set<String> generateTables;
    // 不生成那些表，黑名单
    private Set<String> unGenerateTables;
}
