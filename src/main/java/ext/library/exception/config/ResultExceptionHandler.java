package ext.library.exception.config;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import ext.library.convert.Convert;
import ext.library.exception.ApiVersionDeprecatedException;
import ext.library.exception.ForbiddenException;
import ext.library.exception.LoginException;
import ext.library.exception.ParamDecryptException;
import ext.library.exception.ParamException;
import ext.library.exception.ParamVoidException;
import ext.library.exception.ResultException;
import ext.library.util.ExceptionUtils;
import ext.library.web.view.R;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    public synchronized ProblemDetail resultExceptionHandler(ResultException e) {
        ExceptionUtils.printException(e);
        if (e.isAlert()) {
            return R.errorAlert(e.getMessage());
        } else {
            return R.errorPrompt(e.getMessage());
        }
    }

    /**
     * 401 拦截登录异常（User）
     *
     * @param e 登录异常
     * @return 结果
     */
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail loginExceptionHandler(LoginException e) {
        ExceptionUtils.printException(e);
        return R.unauthorized();
    }

    /**
     * 403 无权限异常访问处理
     *
     * @param e 无权限异常
     * @return 结果
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail forbiddenExceptionHandler(ForbiddenException e) {
        ExceptionUtils.printException(e);
        return R.forbidden();
    }

    /**
     * 410 拦截 API 接口版本弃用异常
     *
     * @param e API 接口版本弃用异常
     * @return 结果
     */
    @ExceptionHandler(ApiVersionDeprecatedException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ProblemDetail apiVersionDeprecatedExceptionHandler(ApiVersionDeprecatedException e) {
        ExceptionUtils.printException(e);
        return R.gone();
    }

    /**
     * 433 参数效验未通过统一处理
     *
     * @param e 参数校验未通过异常
     * @return 结果
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, ParamVoidException.class, ParamException.class, BindException.class, ConstraintViolationException.class, ValidateException.class})
    public ProblemDetail missingServletRequestParameterExceptionHandler(Exception e, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Console.error("uri={}", uri);
        Object data = null;
        String message = null;
        Dict paramHint = Dict.create();
        ExceptionUtils.printException(e);
        if (e instanceof BindException bindException) {
            List<ObjectError> errors = bindException.getAllErrors();
            errors.forEach(error -> {
                String str = StrUtil.subAfter(Objects.requireNonNull(error.getArguments())[0].toString(), "[", true);
                String key = str.substring(0, str.length() - 1);
                String msg = error.getDefaultMessage();
                paramHint.put(key, msg);
                Console.error(key + " " + msg);
            });
            data = paramHint;
        } else if (e instanceof ConstraintViolationException constraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
                String key = constraintViolation.getPropertyPath().toString();
                String msg = constraintViolation.getMessage();
                paramHint.put(key, msg);
                Console.error(key + " " + msg);
            }
            data = paramHint;
        } else if (e instanceof ValidateException validateException) {
            try {
                data = Convert.toDictList(validateException.getMessage());
            } catch (Exception exception) {
                message = validateException.getMessage();
            }
        }
        return R.paramCheckNotPass(message, data);
    }

    /**
     * 解密异常统一处理 -435
     *
     * @param e 解密异常
     * @return 结果
     */
    @ExceptionHandler(ParamDecryptException.class)
    public ProblemDetail paramDecryptExceptionHandler(ParamDecryptException e) {
        log.error("【解密错误】错误信息如下：{}", e.getMessage());
        ExceptionUtils.printException(e);
        return R.paramDecryptError();
    }

    /**
     * 非法参数（断言）异常统一处理
     * <p>
     * 用于业务异常
     *
     * @param e 解密异常
     * @return 结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("【执行存在不合理】错误信息如下：{}", e.getMessage());
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
    public ProblemDetail exceptionHandler(Exception e) {
        ExceptionUtils.printException(e);
        return R.internalServerError(e.getMessage());
    }

}