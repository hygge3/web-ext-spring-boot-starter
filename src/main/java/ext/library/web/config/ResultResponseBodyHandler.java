package ext.library.web.config;

import com.alibaba.fastjson2.JSON;
import ext.library.annotation.UnWrapper;
import ext.library.constant.HttpAttribute;
import ext.library.util.I18nUtils;
import ext.library.web.view.R;
import ext.library.web.view.Result;
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
public class ResultResponseBodyHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 不需要包装
        if (returnType.hasMethodAnnotation(UnWrapper.class) ||
            returnType.getContainingClass().isAnnotationPresent(UnWrapper.class)) {
            log.debug("UnWrapper");
            return false;
        }
        return Objects.nonNull(returnType.getMethod());
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        Result<?> result;
        // 1. 处理参数
        if (body == null) {
            result = R.success();
        } else if (body instanceof Result<?>) {
            result = (Result<?>) body;
        } else if (body instanceof String) {
            // String 特殊处理
            result = R.success(body);
            result.setMsg(I18nUtils.getExt(result.getMsg()));
            result.setTraceId(MDC.get(HttpAttribute.TRACE_ID));
            return JSON.toJSONString(result);
        } else {
            result = R.success(body);
        }

        // 2. 设置 i18n msg
        result.setMsg(I18nUtils.getExt(result.getMsg()));

        // 3. 设置链路 ID
        result.setTraceId(MDC.get(HttpAttribute.TRACE_ID));

        // 4. 响应结果
        return result;
    }

}
