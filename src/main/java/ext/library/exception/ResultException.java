package ext.library.exception;

import cn.hutool.core.util.StrUtil;
import ext.library.web.view.R;
import ext.library.web.view.Result;
import ext.library.web.view.ResultPrompt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@linkplain Result} 结果异常定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResultException extends RuntimeException {

    static final long serialVersionUID = -4332073495864145387L;

    /**
     * 业务定位标识
     * <p>用于上层捕获异常（如：多重 try_catch）时，方便业务定位
     */
    int businessId;
    Result<?> result;

    /**
     * <b>异常消息构造</b>
     * {@linkplain R#errorPrompt(String)} 的便捷方式
     */
    public ResultException(String msg) {
        super(msg);
        this.result = R.errorPrompt(msg);
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
        this.result = R.errorPromptFormat(msg, values);
    }

    public ResultException(Result<?> result) {
        super(result.getMsg());
        this.result = result;
    }

    /**
     * <b>异常消息构造</b>
     * {@linkplain R#errorPrompt(String)} 的便捷方式
     */
    public ResultException(int businessId, String msg) {
        super(msg);
        this.businessId = businessId;
        this.result = R.errorPrompt(msg);
    }

    public ResultException(int businessId, Result<?> result) {
        super(result.getMsg());
        this.businessId = businessId;
        this.result = result;
    }

}
