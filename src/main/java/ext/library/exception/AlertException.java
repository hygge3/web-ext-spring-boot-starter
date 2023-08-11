package ext.library.exception;

import cn.hutool.core.util.StrUtil;
import ext.library.web.view.R;
import ext.library.web.view.Result;
import ext.library.web.view.ResultPrompt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@linkplain Result} 需要弹窗提示定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AlertException extends RuntimeException {

    Result<?> result;

    /**
     * <b>弹窗提示定义消息构造</b>
     * {@linkplain R#alertErrorPrompt(String)} 的便捷方式
     */
    public AlertException(String msg) {
        super(msg);
        this.result = R.alertErrorPrompt(msg);
    }

    /**
     * <b>弹窗提示定义消息构造</b>
     * {@linkplain R#alertPromptFormat(String, Object...)} 的便捷方式
     *
     * @param msg    提示消息（如：{@value ResultPrompt#USERNAME_OR_PASSWORD_ERROR}）
     * @param values 业务处理数据
     */
    public AlertException(String msg, Object... values) {
        super(StrUtil.format(msg, values));
        this.result = R.alertPromptFormat(msg, values);
    }

    public AlertException(Result<?> result) {
        super(result.getMsg());
        this.result = result;
    }

}
