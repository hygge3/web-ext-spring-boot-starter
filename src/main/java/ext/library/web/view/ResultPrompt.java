package ext.library.web.view;


import ext.library.util.DateUtils;

import java.time.LocalDateTime;

/**
 * Result String 类型异常提示信息预定义
 */
public interface ResultPrompt {

    //---------------------错误提示预定义---------------------

    String USER_EXIST = "用户已存在";
    String USER_NO_EXIST = "用户不存在";
    String USER_STOP = "用户已停用";
    String USER_INVALID = "无效的用户";
    String USERNAME_OR_PASSWORD_ERROR = "用户名或密码错误";
    String ORIGINAL_PASSWORD_ERROR = "原密码错误";
    String PHONE_EXIST = "手机号已被注册";
    String PASSWORD_INVALID = "无效的口令";
    String CAPTCHA_ERROR = "验证码错误";
    String INVITE_CODE_INVALID = "邀请码无效";
    String ACCOUNT_EXIST_BINDING = "此帐户已被绑定";
    String ACCOUNT_EXIST_BUSINESS_NOT_ALLOW_CHANGE = "帐户存在业务，暂时不允许改变状态";
    String ACCOUNT_BALANCE_NOT_ENOUGH = "帐户余额不足";

    //---------------------异常提示预定义---------------------

    /**
     * 超出最大 limit 限制
     */
    String MAX_LIMIT = "超出最大 limit 限制";

    /**
     * 分布式锁
     */
    String DISTRIBUTED_LOCK = "分布式锁...";

    /**
     * 多行插入错误
     */
    String INSERT_BATCH_ERROR = "执行多行插入命令失败，可能原因是：数据结构异常或无 ID 主键。请立即检查数据的一致性、唯一性。";

    /**
     * 单行删除错误
     */
    String DELETE_ERROR = "执行单行删除命令失败，可能原因是：数据结构异常或无 ID 主键。请立即检查数据的一致性、唯一性。";

    /**
     * 批次删除错误
     */
    String DELETE_BATCH_ERROR = "执行批次删除命令失败，可能原因是：数据结构异常或无 ID 主键。请立即检查数据的一致性、唯一性。";

    /**
     * 批次更新错误
     */
    String UPDATE_BATCH_ERROR = "执行批次更新命令失败，可能原因是：数据结构异常或无 ID 主键。请立即检查数据的一致性、唯一性。";

    //---------------------枚举提示引用预定义---------------------

    /**
     * 数据结构异常 - 不正确的结果
     *
     * @param expected 预期内容
     * @param actual   实际内容
     * @return 提示信息
     */
    static String dataStructure(Object expected, Object actual) {
        return ResultEnum.DATA_STRUCTURE.getMsg() + " Incorrect result size: expected " + expected + ", actual " + actual;
    }

    /**
     * 服务不可用 - 停机维护
     *
     * @param restoreTime 预计恢复时间（如：2020-12-31 08:00:00）
     * @return 提示信息
     */
    static String serviceUnavailable(LocalDateTime restoreTime) {
        return ResultEnum.SERVICE_UNAVAILABLE.getMsg() + "，我们正对服务进行停机维护，预计将在 " + DateUtils.toDateTimeFormatter(restoreTime) + " 后恢复，给你带来的不便，敬请谅解！";
    }

    /**
     * 服务不可用 - 停机维护
     *
     * @param restoreMinutes 预计恢复时间（单位：分钟）
     * @return 提示信息
     */
    static String serviceUnavailable(int restoreMinutes) {
        return ResultEnum.SERVICE_UNAVAILABLE.getMsg() + "，我们正对服务进行停机维护，预计将在 " + restoreMinutes + " 分钟后恢复，给你带来的不便，敬请谅解！";
    }

}
