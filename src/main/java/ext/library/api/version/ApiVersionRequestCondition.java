package ext.library.api.version;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import ext.library.exception.ApiVersionDeprecatedException;
import ext.library.util.Assert;
import ext.library.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;


/**
 * 优雅的接口版本控制，URL 匹配器
 */
@Data
@AllArgsConstructor
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    ApiVersion apiVersion;
    ApiVersionProperties apiVersionProperties;
    /**
     * {@link RequestMapping} 版本占位符索引
     */
    Integer versionPlaceholderIndex;

    @Override
    @NonNull
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition apiVersionRequestCondition) {
        // 最近优先原则：在方法上的 {@link ApiVersion} 可覆盖在类上面的 {@link ApiVersion}
        return new ApiVersionRequestCondition(apiVersionRequestCondition.getApiVersion(), apiVersionRequestCondition.getApiVersionProperties(), apiVersionRequestCondition.getVersionPlaceholderIndex());
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        // 校验请求 url 中是否包含版本信息
        String requestUri = request.getRequestURI();
        String[] versionPaths = StringUtils.splitToArray(requestUri, "/");
        double pathVersion = Double.parseDouble(versionPaths[versionPlaceholderIndex].substring(1));

        // pathVersion 的值大于等于 apiVersionValue 皆可匹配，除非 ApiVersion 的 deprecated 值已被标注为 true
        double apiVersionValue = this.getApiVersion().value();
        if (pathVersion >= apiVersionValue) {
            double minimumVersion = apiVersionProperties.getMinimumVersion();
            Assert.isFalse(
                    (this.getApiVersion().deprecated() || minimumVersion > pathVersion) && NumberUtil.equals(pathVersion, apiVersionValue),
                    () -> new ApiVersionDeprecatedException(StrUtil.format("客户端调用弃用版本 API 接口，requestURI：{}", requestUri)));
            if (this.getApiVersion().deprecated()) {
                // 继续匹配
                return null;
            }

            // 匹配成功
            return this;
        }

        // 继续匹配
        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition apiVersionRequestCondition, @NonNull HttpServletRequest request) {
        // 当出现多个符合匹配条件的 ApiVersionCondition，优先匹配版本号较大的
        return NumberUtil.compare(apiVersionRequestCondition.getApiVersion().value(), getApiVersion().value());
    }

}