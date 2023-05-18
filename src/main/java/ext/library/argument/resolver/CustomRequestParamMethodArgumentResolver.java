package ext.library.argument.resolver;

import ext.library.util.ServletUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import jakarta.servlet.http.HttpServletRequest;


/**
 * {@linkplain RequestParamMethodArgumentResolver} 增强
 * <p>添加对 body 参数的解析
 */
public class CustomRequestParamMethodArgumentResolver extends RequestParamMethodArgumentResolver {

    public CustomRequestParamMethodArgumentResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    public CustomRequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory, useDefaultResolution);
    }

    @Override
    @Nullable
    protected Object resolveName(@NonNull String name, @NonNull MethodParameter parameter, NativeWebRequest request) throws Exception {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        // 保留原始逻辑
        Object arg = super.resolveName(name, parameter, request);

        // ext-library
        if (arg == null) {
            arg = ServletUtils.getParamToJson(servletRequest).get(name);
        }
        return arg;
    }

}