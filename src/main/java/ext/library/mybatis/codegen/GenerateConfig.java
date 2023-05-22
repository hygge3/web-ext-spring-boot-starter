package ext.library.mybatis.codegen;

import ext.library.mybatis.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
public class GenerateConfig {
    /** jdbc url */
    @NotBlank String jdbcUrl;
    /** 用户名 */
    @NotBlank String username;
    /** 密码 */
    @NotBlank String password;
    // 代码生成目录
    @NotBlank String sourceDir = "src/main/java";
    // 根包名
    @NotBlank String basePackage;
    // entity 类的父类，可以自定义一些 BaseEntity 类
    Class<?> entitySupperClass = BaseEntity.class;
    // 数据库表前缀，多个前缀用英文逗号（,）隔开
    String tablePrefix;
    // 大字段字段名称列表
    Set<String> largeColumns;
    // 是否生成 mapper 类
    boolean mapperGenerateEnable = true;
    // 是否生成 service 类
    boolean serviceGenerateEnable = true;
    // 是否生成 serviceImpl 类
    boolean serviceImplGenerateEnable = true;
    // 是否生成 controller 类
    boolean controllerGenerateEnable = true;
    // 生成那些表，白名单
    @NotBlank Set<String> generateTables;
    // 不生成那些表，黑名单
    private Set<String> unGenerateTables;
}