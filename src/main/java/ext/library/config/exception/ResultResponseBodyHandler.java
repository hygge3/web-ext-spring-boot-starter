package ext.library.config.exception;

import ext.library.constant.Constant;
import ext.library.util.I18nUtils;
import ext.library.view.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 响应结果处理器
 * <p>标准 HTTP 状态码
 */
@Slf4j
@ControllerAdvice
public class ResultResponseBodyHandler implements ResponseBodyAdvice<Result> {

    @Override
    public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return Objects.requireNonNull(returnType.getMethod()).getReturnType() == Result.class;
    }

    @Override
    public Result<?> beforeBodyWrite(@Nullable Result body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        // 1. 处理参数
        if (body == null) {
            return null;
        }

        // 2. 设置 i18n msg
        body.setMsg(I18nUtils.getExt(body.getMsg()));

        // 3. 设置链路 ID
        body.setTraceId(MDC.get(Constant.TRACE_ID));

        // 4. 响应结果
        return body;
    }

}