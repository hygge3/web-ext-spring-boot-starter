package ext.library.web.view;

import cn.hutool.core.util.StrUtil;
import ext.library.util.I18nUtils;
import ext.library.util.JsonUtils;
import ext.library.util.ServletUtils;
import ext.library.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.LocalDateTime;

/**
 * 便捷返回 {@linkplain ProblemDetail}，构建 {@code RESTful} 风格 API 结果
 */
@Slf4j
public class R {

    // 3xx Redirection 资源、重定向、定位等提示

    /**
     * 300 资源已失效
     *
     * @return 问题细节
     */
    public static ProblemDetail resourceAlreadyInvalid() {
        return error(ResultEnum.MULTIPLE_CHOICE);
    }

    /**
     * 301 永久重定向
     *
     * @return 问题细节
     */
    public static ProblemDetail movedPermanently() {
        return error(ResultEnum.MOVED_PERMANENTLY);
    }

    // 4xx Client Error 客户端错误

    /**
     * 401 未登录或登录已失效
     *
     * @return 问题细节
     */
    public static ProblemDetail unauthorized() {
        return error(ResultEnum.UNAUTHORIZED);
    }

    /**
     * 403 无权限
     *
     * @return 问题细节
     */
    public static ProblemDetail forbidden() {
        return error(ResultEnum.FORBIDDEN);
    }

    /**
     * 404 无法找到所请求的资源
     *
     * @return 问题细节
     */
    public static ProblemDetail notFound() {
        return error(ResultEnum.NOT_FOUND);
    }

    /**
     * 405 方法不允许（Method Not Allowed
     * <p>客户端使用服务端不支持的 Http Request Method 进行接口调用
     *
     * @param data {@link ProblemDetail#setDetail(String)} 更详细的异常提示信息
     * @return 问题细节
     */
    public static <T> ProblemDetail methodNotAllowed(T data) {
        return error(ResultEnum.METHOD_NOT_ALLOWED, data);
    }

    /**
     * 410 API 接口版本弃用
     *
     * @return 问题细节
     */
    public static ProblemDetail gone() {
        return error(ResultEnum.GONE);
    }

    /**
     * 429 频繁请求限流
     *
     * @return 问题细节
     */
    public static ProblemDetail tooManyRequests() {
        return error(ResultEnum.TOO_MANY_REQUESTS);
    }

    /**
     * 433 参数为空
     *
     * @return 问题细节
     */
    public static ProblemDetail paramVoid() {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS);
    }

    /**
     * 433 参数校验未通过，请参照 API 核对后重试
     *
     * @return 问题细节
     */
    public static ProblemDetail paramCheckNotPass() {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS);
    }

    /**
     * 433 参数校验未通过，请参照 API 核对后重试
     *
     * @param msg 提示信息
     * @return 问题细节
     */
    public static <T> ProblemDetail paramCheckNotPass(String msg) {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS, msg);
    }

    /**
     * 433 参数校验未通过，请参照 API 核对后重试
     *
     * @param data {@link ProblemDetail#setDetail(String)} 提示信息
     * @return 问题细节
     */
    public static <T> ProblemDetail paramCheckNotPass(T data) {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS, data);
    }

    /**
     * 433 参数校验未通过，请参照 API 核对后重试
     *
     * @param data {@link ProblemDetail#setDetail(String)} 提示信息
     * @param msg  提示信息
     * @return 问题细节
     */
    public static <T> ProblemDetail paramCheckNotPass(String msg, T data) {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS, StringUtils.isBlank(msg) ? ResultEnum.PARAM_CHECK_NOT_PASS.getMsg() : msg, data);
    }

    /**
     * 433 参数校验未通过，无效的 value
     *
     * @return 问题细节
     */
    public static ProblemDetail paramValueInvalid() {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS);
    }

    /**
     * 433 参数校验未通过，无效的 value
     *
     * @param data {@link ProblemDetail#setDetail(String)} 提示信息
     * @return 问题细节
     */
    public static <T> ProblemDetail paramValueInvalid(T data) {
        return error(ResultEnum.PARAM_CHECK_NOT_PASS);
    }

    /**
     * 434 参数解密错误
     *
     * @return 问题细节
     */
    public static ProblemDetail paramDecryptError() {
        return error(ResultEnum.PARAM_DECRYPT_ERROR);
    }

    // 500 - 服务器错误

    /**
     * 500 服务器内部错误
     *
     * @return 问题细节
     */
    public static ProblemDetail internalServerError() {
        return error(ResultEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 500 服务器内部错误
     *
     * @param <T> 泛型
     * @param msg 异常标题
     * @return 问题细节
     */
    public static <T> ProblemDetail internalServerError(String msg) {
        return error(ResultEnum.INTERNAL_SERVER_ERROR, msg);
    }

    /**
     * 500 服务器内部错误
     *
     * @param <T>  泛型
     * @param data 异常数据
     * @return 问题细节
     */
    public static <T> ProblemDetail internalServerError(T data) {
        return error(ResultEnum.INTERNAL_SERVER_ERROR, data);
    }

    /**
     * 503 服务不可用
     * <p>服务目前无法使用（由于超载或停机维护）</p>
     *
     * @return 问题细节
     */
    public static ProblemDetail serviceUnavailable() {
        return error(ResultEnum.SERVICE_UNAVAILABLE);
    }

    /**
     * 503 服务不可用（停机维护）
     *
     * @param restoreTime 预计恢复时间（如：2020-12-31 08:00:00）
     * @return 问题细节
     */
    public static ProblemDetail serviceUnavailable(LocalDateTime restoreTime) {
        return error(ResultEnum.SERVICE_UNAVAILABLE, ResultPrompt.serviceUnavailable(restoreTime));
    }

    /**
     * 503 服务不可用
     * <p>服务目前无法使用（由于超载或停机维护）</p>
     *
     * @param <T>  泛型
     * @param data 服务不可用的具体原因，参考：{@link ResultPrompt#serviceUnavailable(int)}
     * @return 问题细节
     */
    public static <T> ProblemDetail serviceUnavailable(T data) {
        return error(ResultEnum.SERVICE_UNAVAILABLE, data);
    }

    // 600 - 自定义错误提示

    /**
     * <b>601 错误提示 </b>
     * <p>适用于用户操作提示、业务消息提示、友好的错误提示等场景。
     * <p>可优先使用 {@linkplain ResultPrompt} 预定义的提示信息，如：{@linkplain ResultPrompt#USERNAME_OR_PASSWORD_ERROR}
     *
     * @param msg 提示消息（如：{@value ResultPrompt#USERNAME_OR_PASSWORD_ERROR}）
     * @return 问题细节
     */
    public static ProblemDetail errorPrompt(String msg) {
        return error(ResultEnum.ERROR_PROMPT, msg);
    }

    /**
     * <b>601 错误提示 </b>
     * <p>适用于用户操作提示、业务消息提示、友好的错误提示等场景。
     * <p>可优先使用 {@linkplain ResultPrompt} 预定义的提示信息，如：{@linkplain ResultPrompt#USERNAME_OR_PASSWORD_ERROR}
     * <p>msg 支持文本模板格式化，{} 表示占位符
     * <pre class="code">例：("this is {} for {}", "a", "b") = this is a for b</pre>
     *
     * @param msg    文本模板，被替换的部分用 {} 表示
     * @param values 文本模板中占位符被替换的值
     * @return 问题细节
     */
    public static ProblemDetail errorPromptFormat(String msg, Object... values) {
        return errorPrompt(StrUtil.format(msg, values));
    }

    /**
     * <b>601 错误提示 </b>
     * <p>适用于用户操作提示、业务消息提示、友好的错误提示等场景。
     * <p>可优先使用 {@linkplain ResultPrompt} 预定义的提示信息，如：{@linkplain ResultPrompt#USERNAME_OR_PASSWORD_ERROR}
     *
     * @param msg  提示消息（如：{@value ResultPrompt#USERNAME_OR_PASSWORD_ERROR}）
     * @param data 业务处理数据
     * @return 问题细节
     */
    public static <T> ProblemDetail errorPrompt(String msg, T data) {
        return error(ResultEnum.ERROR_PROMPT.getCode(), msg, data);
    }

    /**
     * <b>601 错误提示 </b>
     * <p>适用于 i18n 资源包定义（messages.properties），遵循 SpringBoot 默认值规范</p>
     * <p>msg 支持文本模板格式化，{} 表示占位符
     * <pre class="code">例：("this is {} for {}", "a", "b") = this is a for b</pre>
     *
     * @param msgKey messages.properties 中定义的 key，被替换的部分用 {} 表示
     * @param values messages.properties 中占位符被替换的值
     * @return 问题细节
     */
    public static ProblemDetail errorPromptI18n(String msgKey, Object... values) {
        return errorPrompt(I18nUtils.get(msgKey, values));
    }

    /**
     * <b>601 错误提示 </b>
     * <p>适用于 i18n 资源包定义（messages.properties），遵循 SpringBoot 默认值规范</p>
     *
     * @param msgKey messages.properties 中定义的 key，被替换的部分用 {} 表示
     * @param data   业务处理数据
     * @return 问题细节
     */
    public static <T> ProblemDetail errorPromptI18n(String msgKey, T data) {
        return errorPrompt(I18nUtils.get(msgKey), data);
    }

    /**
     * <b>602 弹窗错误</b>
     * <p>适用于弹窗告知用户操作提示、业务消息提示、友好的错误提示等场景。
     *
     * @param msg 提示消息
     * @return 问题细节
     */
    public static ProblemDetail errorAlert(String msg) {
        return error(ResultEnum.ERROR_ALERT, msg);
    }

    /**
     * <b>602 弹窗错误</b>
     * <p>适用于弹窗告知用户操作提示、业务消息提示、友好的错误提示等场景。
     * <p>msg 支持文本模板格式化，{} 表示占位符
     * <pre class="code">例：("this is {} for {}", "a", "b") = this is a for b</pre>
     *
     * @param msg    文本模板，被替换的部分用 {} 表示
     * @param values 文本模板中占位符被替换的值
     * @return 问题细节
     */
    public static ProblemDetail errorAlertFormat(String msg, Object... values) {
        return errorAlert(StrUtil.format(msg, values));
    }

    // ------ ProblemDetail builder ------

    public static <T> ProblemDetail error(HttpStatus status, String title, T data) {
        return error(status.value(), title, data);
    }

    public static ProblemDetail error(HttpStatus status, String title) {
        return error(status.value(), title, null);
    }

    public static ProblemDetail error(ResultEnum resultEnum) {
        return error(resultEnum.getCode(), resultEnum.getMsg(), null);
    }

    public static ProblemDetail error(ResultEnum resultEnum, String title) {
        return error(resultEnum.getCode(), title, null);
    }

    public static <T> ProblemDetail error(ResultEnum resultEnum, T data) {
        return error(resultEnum.getCode(), resultEnum.getMsg(), data);
    }

    public static <T> ProblemDetail error(ResultEnum resultEnum, String title, T data) {
        return error(resultEnum.getCode(), resultEnum.getMsg(), data);
    }

    public static <T> ProblemDetail error(int status, String title, T data) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        HttpServletRequest request = ServletUtils.getRequest();
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setTitle(title);
        problemDetail.setDetail(JsonUtils.toString(data));
        return problemDetail;
    }

    public static ProblemDetail error(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }

}