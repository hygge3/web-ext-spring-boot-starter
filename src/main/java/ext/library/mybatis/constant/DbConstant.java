package ext.library.mybatis.constant;

/**
 * Db 常量定义
 */
public interface DbConstant {

    // ====================== 类字段名定义 ======================

    /** 主键 */
    String CLASS_FIELD_ID = "id";
    /** 创建时间 */
    String CLASS_FIELD_CREATE_TIME = "createTime";
    /** 更新时间 */
    String CLASS_FIELD_UPDATE_TIME = "updateTime";
    /** 删除时间：逻辑删除 */
    String CLASS_FIELD_DEFINITION_DELETE = "isDeleted";

    // ====================== 数据库字段名定义 ======================

    /** 主键 */
    String DB_FIELD_ID = "id";
    /** 创建时间 */
    String DB_FIELD_CREATE_TIME = "create_time";
    /** 更新时间 */
    String DB_FIELD_UPDATE_TIME = "update-time";
    /** 删除时间：逻辑删除 */
    String DB_FIELD_DEFINITION_DELETE = "is_deleted";

}