package ext.library.exception;

import ext.library.web.view.R;
import ext.library.web.view.Result;
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