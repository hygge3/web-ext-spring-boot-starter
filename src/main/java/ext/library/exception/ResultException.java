package ext.library.exception;

import cn.hutool.core.util.StrUtil;
import ext.library.web.view.R;
import ext.library.web.view.ResultPrompt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 结果异常定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResultException extends RuntimeException {

    /**
     * 业务定位标识
     * <p>用于上层捕获异常（如：多重 try_catch）时，方便业务定位
     */
    int businessId;
    /**
     * 是否是弹窗警告
     */
    boolean alert;
    /**
     * 数据
     */
    Object data;

    /**
     * <b>异常提示消息构造</b>
     * {@linkplain R#errorPrompt(String)} 的便捷方式
     */
    public ResultException(String msg) {
        super(msg);
    }

    /**
     * <b>异常提示消息构造</b>
     * {@linkplain R#errorPrompt(String)} 的便捷方式
     */
    public ResultException(String msg, Object data) {
        super(msg);
        this.data = data;
    }

    /**
     * <b>弹窗异常消息构造</b>
     * {@linkplain R#errorPrompt(String)} 的便捷方式
     *
     * @param alert 弹窗异常
     * @param msg   消息
     */
    public ResultException(boolean alert, String msg) {
        super(msg);
        this.alert = alert;
    }

    /**
     * <b>异常消息构造</b>
     * {@linkplain R#errorPromptFormat(String, Object...)} 的便捷方式
     *
     * @param msg    提示消息（如：{@value ResultPrompt#USERNAME_OR_PASSWORD_ERROR}）
     * @param values 业务处理数据
     */
    public ResultException(String msg, Object... values) {
        super(StrUtil.format(msg, values));
    }

}