package ext.library.config.exception;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import ext.library.constant.Constant;
import ext.library.convert.Convert;
import ext.library.exception.AuthorizeException;
import ext.library.exception.ParamDecryptException;
import ext.library.exception.ParamException;
import ext.library.exception.ParamVoidException;
import ext.library.exception.ResultException;
import ext.library.util.ExceptionUtils;
import ext.library.util.ServletUtils;
import ext.library.view.R;
import ext.library.view.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 全局统一异常处理
 */
@Slf4j
@ControllerAdvice
@ConditionalOnProperty(prefix = ResultExceptionHandler.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ResultExceptionHandler extends AbstractExceptionHandler {

    static final String PREFIX = Constant.PROJECT_PREFIX + ".exception-handler";


    // RESTful 异常拦截

    /**
     * 异常结果处理-synchronized
     *
     * @param e 结果异常
     * @return 结果
     */
    @Override
    @ResponseBody
    @ExceptionHandler(ResultException.class)
    public synchronized Result<?> resultExceptionHandler(ResultException e) {
        var result = e.getResult();
        log.error(result.toString());
        ExceptionUtils.printException(e);
        return result;
    }

    /**
     * 方法不允许（Method Not Allowed）-405
     * <p>客户端使用服务端不支持的 Http Request Method 进行接口调用
     *
     * @param e 方法不允许异常
     * @return 结果
     */
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        String uri = Objects.requireNonNull(ServletUtils.getRequest()).getRequestURI();
        Console.error("uri={}", uri);
        ExceptionUtils.printException(e);
        return R.methodNotAllowed(e.getMessage());
    }

    /**
     * 参数效验为空统一处理 -432
     *
     * @return 结果
     */
    @Override
    @ResponseBody
    @ExceptionHandler(ParamVoidException.class)
    public Result<?> paramVoidExceptionHandler() {
        return R.paramVoid();
    }

    /**
     * 参数效验未通过统一处理 -433
     *
     * @param e 参数校验未通过异常
     * @return 结果
     */
    @Override
    @ResponseBody
    @ExceptionHandler(ParamException.class)
    public Result<?> paramExceptionHandler(ParamException e) {
        ExceptionUtils.printException(e);
        return R.paramCheckNotPass(e.getMessage());
    }

    /**
     * {@linkplain Valid} 验证异常统一处理 -433
     *
     * @param e 验证异常
     * @return 结果
     */
    @Override
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public Result<?> bindExceptionHandler(BindException e) {
        String uri = Objects.requireNonNull(ServletUtils.getRequest()).getRequestURI();
        Console.error("uri={}", uri);
        List<ObjectError> errors = e.getAllErrors();
        JSONObject paramHint = new JSONObject();
        errors.forEach(error -> {
            String str = StrUtil.subAfter(Objects.requireNonNull(error.getArguments())[0].toString(), "[", true);
            String key = str.substring(0, str.length() - 1);
            String msg = error.getDefaultMessage();
            paramHint.put(key, msg);
            Console.error(key + " " + msg);
        });

        return R.paramCheckNotPass(paramHint);
    }

    /**
     * 验证异常统一处理 -433
     *
     * @param e 验证异常
     * @return 结果
     */
    @Override
    @ResponseBody
    @ExceptionHandler(ValidateException.class)
    public Result<?> validateExceptionHandler(ValidateException e) {
        ExceptionUtils.printException(e);
        try {
            return R.paramCheckNotPass(Convert.toJSONArray(e.getMessage()));
        } catch (Exception exception) {
            return R.paramCheckNotPass(e.getMessage());
        }
    }

    /**
     * 解密异常统一处理 -435
     *
     * @param e 解密异常
     * @return 结果
     */
    @Override
    @ResponseBody
    @ExceptionHandler(ParamDecryptException.class)
    public Result<?> paramDecryptExceptionHandler(ParamDecryptException e) {
        log.error("【解密错误】错误信息如下：{}", e.getMessage());
        ExceptionUtils.printException(e);
        return R.paramDecryptError();
    }

    // WEB 异常拦截

    /**
     * 拦截登录异常（Admin）-301
     *
     * @param e 认证异常
     * @throws IOException 重定向失败
     */
    @Override
    @ExceptionHandler(AuthorizeException.class)
    public void authorizeExceptionHandler(AuthorizeException e) throws IOException {
        ExceptionUtils.printException(e);
        Objects.requireNonNull(ServletUtils.getResponse()).sendRedirect("");
    }

}