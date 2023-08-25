package ext.library.exception.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import ext.library.convert.Convert;
import ext.library.exception.*;
import ext.library.util.ExceptionUtils;
import ext.library.util.ServletUtils;
import ext.library.web.view.R;
import ext.library.web.view.Result;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 全局统一异常处理
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnProperty(prefix = ExceptionHandlerProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ResultExceptionHandler {

    @PostConstruct
    private void init() {
        log.info("【全局统一异常处理】配置项:{}。初始化拦截所有异常 ...", ExceptionHandlerProperties.PREFIX);
    }


    // RESTful 异常拦截

    /**
     * 异常结果处理-synchronized
     *
     * @param e 结果异常
     * @return 结果
     */
    @ExceptionHandler(ResultException.class)
    public synchronized Result<?> resultExceptionHandler(ResultException e) {
        var result = e.getResult();
        log.error(result.toString());
        ExceptionUtils.printException(e);
        return result;
    }

    /**
     * 需要弹窗提示结果处理
     *
     * @param e 结果异常
     * @return 结果
     */
    @ExceptionHandler(AlertException.class)
    public synchronized Result<?> alertExceptionHandler(AlertException e) {
        var result = e.getResult();
        log.warn(result.toString());
        return result;
    }

    /**
     * 拦截登录异常（User）-401
     *
     * @param e 登录异常
     * @return 结果
     */
    @ExceptionHandler(LoginException.class)
    public Result<?> loginExceptionHandler(LoginException e) {
        ExceptionUtils.printException(e);
        return R.unauthorized();
    }

    /**
     * Sa-Token 拦截登录异常（User）-401
     *
     * @param e 登录异常
     * @return 结果
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> saTokenExceptionHandler(NotLoginException e) {
        ExceptionUtils.printException(e);
        return R.unauthorized();
    }

    /**
     * 非法请求异常拦截 -402
     *
     * @param e 非法请求异常
     * @return 结果
     */
    @ExceptionHandler(AttackException.class)
    public Result<?> attackExceptionHandler(AttackException e) {
        ExceptionUtils.printException(e);
        return R.attack(e.getMessage());
    }


    /**
     * 无权限异常访问处理 -403
     *
     * @param e 无权限异常
     * @return 结果
     */
    @ExceptionHandler(ForbiddenException.class)
    public Result<?> forbiddenExceptionHandler(ForbiddenException e) {
        ExceptionUtils.printException(e);
        return R.forbidden();
    }

    /**
     * Sa-Token 无权限异常访问处理 -403
     *
     * @param e 无权限异常
     * @return 结果
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> notPermissionExceptionHandler(NotPermissionException e) {
        ExceptionUtils.printException(e);
        return R.forbidden();
    }

    /**
     * Sa-Token 无角色异常访问处理 -403
     *
     * @param e 无权限异常
     * @return 结果
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<?> notRoleExceptionHandler(NotRoleException e) {
        ExceptionUtils.printException(e);
        return R.forbidden();
    }

    /**
     * 接口未找到（Not Found） -404
     *
     * @param e 接口未找到异常
     * @return 结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> noHandlerFoundExceptionHandler(NoHandlerFoundException e) {
        ExceptionUtils.printException(e);
        return R.notFound();
    }

    /**
     * 方法不允许（Method Not Allowed）-405
     * <p>客户端使用服务端不支持的 Http Request Method 进行接口调用
     *
     * @param e 方法不允许异常
     * @return 结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        String uri = Objects.requireNonNull(ServletUtils.getRequest()).getRequestURI();
        Console.error("uri={}", uri);
        ExceptionUtils.printException(e);
        return R.methodNotAllowed(e.getMessage());
    }

    /**
     * 拦截 API 接口版本弃用异常 -410
     *
     * @param e API 接口版本弃用异常
     * @return 结果
     */
    @ExceptionHandler(ApiVersionDeprecatedException.class)
    public Result<?> apiVersionDeprecatedExceptionHandler(ApiVersionDeprecatedException e) {
        ExceptionUtils.printException(e);
        return R.gone();
    }

    /**
     * 参数效验为空统一处理 -432
     *
     * @return 结果
     */
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
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        ExceptionUtils.printException(e);
        return R.paramCheckNotPass(e.getMessage());
    }

    /**
     * 参数效验未通过统一处理 -433
     *
     * @param e 参数校验未通过异常
     * @return 结果
     */
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
     * 违反约束异常统一处理 -433
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String uri = Objects.requireNonNull(ServletUtils.getRequest()).getRequestURI();
        Console.error("uri={}", uri);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        JSONObject paramHint = new JSONObject();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            String key = constraintViolation.getPropertyPath().toString();
            String msg = constraintViolation.getMessage();
            paramHint.put(key, msg);
            Console.error(key + " " + msg);
        }

        return R.paramCheckNotPass(paramHint);
    }


    /**
     * Http 请求消息序列化异常
     *
     * @param e e
     * @return {@link Result}<{@link ?}>
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Result<?> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        ExceptionUtils.printException(e);
        return R.paramCheckNotPass();

    }


    /**
     * 验证异常统一处理 -433
     *
     * @param e 验证异常
     * @return 结果
     */
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
    @ExceptionHandler(ParamDecryptException.class)
    public Result<?> paramDecryptExceptionHandler(ParamDecryptException e) {
        log.error("【解密错误】错误信息如下：{}", e.getMessage());
        ExceptionUtils.printException(e);
        return R.paramDecryptError();
    }

    /**
     * 非法参数（断言）异常统一处理 -435
     *
     * @param e 解密异常
     * @return 结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("【参数错误】：{}", e.getMessage());
        ExceptionUtils.printException(e);
        return R.errorPrompt(e.getMessage());
    }

    // WEB 异常拦截

    /**
     * 拦截所有未处理异常 -500
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        return R.getResult(e);
    }

    /**
     * 服务降级 -507
     *
     * @param e 服务降级异常
     * @return 结果
     */
    @ExceptionHandler(ClientFallbackException.class)
    public Result<?> clientFallbackExceptionHandler(ClientFallbackException e) {
        ExceptionUtils.printException(e);
        return R.clientFallback();
    }

    /**
     * 类型转换异常统一处理 -509
     *
     * @param e 转换异常
     * @return 结果
     */
    @ExceptionHandler(ConvertException.class)
    public Result<?> convertExceptionHandler(ConvertException e) {
        log.error("【类型转换异常】转换类型失败，错误信息如下:{}", e.getMessage());
        ExceptionUtils.printException(e);
        return R.typeConvertError(e.getMessage());
    }


}
