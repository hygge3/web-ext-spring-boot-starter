package ext.library.web.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * Result HTTP 状态码枚举
 * <p>参考{@linkplain HttpStatus}
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    // 2xx Success

    /**
     * {@link HttpStatus#OK}
     * <p>{@code 200 OK}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.1">HTTP/1.1: Semantics and Content, section 6.3.1</a>
     */
    SUCCESS(200, "successful.success"),

    // 3xx Redirection 资源、重定向、定位等提示

    /**
     * {@link HttpStatus#MULTIPLE_CHOICES}
     * <p>{@code 300 Multiple Choices 重定向}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.1">HTTP/1.1: Semantics and Content, section 6.4.1</a>
     */
    MULTIPLE_CHOICE(300, "redirection.multiple_choice"),

    /**
     * {@link HttpStatus#MOVED_PERMANENTLY}
     * <p>{@code 301 Moved Permanently 永久重定向，此状态码会禁止更改请求方法}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.2">HTTP/1.1: Semantics and Content, section 6.4.2</a>
     */
    MOVED_PERMANENTLY(301, "redirection.moved_permanently"),

    /**
     * {@link HttpStatus#TEMPORARY_REDIRECT}
     * <p>{@code 307 Temporary Redirect 暂时重定向}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.7">HTTP/1.1: Semantics and Content, section 6.4.7</a>
     */
    TEMPORARY_REDIRECT(307, "redirection.temporary_redirect"),

    /**
     * {@link HttpStatus#PERMANENT_REDIRECT}
     * <p>{@code 308 Permanent Redirect 永久重定向}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7238">RFC 7238</a>
     */
    PERMANENT_REDIRECT(308, "redirection.permanent_redirect"),

    // 4xx Client Error 客户端错误

    /**
     * {@link HttpStatus#BAD_REQUEST}
     * <p>{@code 400 Bad Request 客户端错误}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.1">HTTP/1.1: Semantics and Content, section 6.5.1</a>
     */
    BAD_REQUEST(400, "client_error.bad_request"),

    /**
     * {@link HttpStatus#UNAUTHORIZED}
     * <p>{@code 401 Unauthorized 未登录或登录已失效}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-3.1">HTTP/1.1: Authentication, section 3.1</a>
     */
    UNAUTHORIZED(401, "client_error.unauthorized"),
    /**
     * {@link HttpStatus#PAYMENT_REQUIRED}
     * <p>{@code 402 Payment Required 需要付费}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.2">HTTP/1.1: Semantics and Content, section 6.5.2</a>
     */
    PAYMENT_REQUIRED(402, "client_error.payment_required"),
    /**
     * {@link HttpStatus#FORBIDDEN}
     * <p>{@code 403 Forbidden 无权限}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.3">HTTP/1.1: Semantics and Content, section 6.5.3</a>
     */
    FORBIDDEN(403, "client_error.forbidden"),

    /**
     * {@link HttpStatus#NOT_FOUND}
     * <p>{@code 404 Not Found 无法找到所请求的资源}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.4">HTTP/1.1: Semantics and Content, section 6.5.4</a>
     */
    NOT_FOUND(404, "client_error.not_found"),

    /**
     * {@link HttpStatus#METHOD_NOT_ALLOWED}
     * <p>{@code 405 Method Not Allowed 禁止使用当前 HTTP 方法的请求}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.5">HTTP/1.1: Semantics and Content, section 6.5.5</a>
     */
    METHOD_NOT_ALLOWED(405, "client_error.method_not_allowed"),

    /**
     * {@link HttpStatus#REQUEST_TIMEOUT}
     * <p>{@code 408 Request Timeout 请求超时}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.7">HTTP/1.1: Semantics and Content, section 6.5.7</a>
     */
    REQUEST_TIMEOUT(408, "client_error.request_timeout"),
    /**
     * {@link HttpStatus#CONFLICT}
     * <p>{{@code 409 Conflict 请求与服务器端目标资源的当前状态相冲突}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.8">HTTP/1.1: Semantics and Content, section 6.5.8</a>
     */
    CONFLICT(409, "client_error.conflict"),
    /**
     * {@link HttpStatus#GONE}
     * <p>{@code 410 Gone 资源永久性丢失}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.9">
     * HTTP/1.1: Semantics and Content, section 6.5.9</a>
     */
    GONE(410, "client_error.gone"),
    /**
     * {@link HttpStatus#PAYLOAD_TOO_LARGE}
     * <p>{@code 413 Payload Too Large 请求大小超过服务器限度}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.11">
     * HTTP/1.1: Semantics and Content, section 6.5.11</a>
     * @since 4.1
     */
    PAYLOAD_TOO_LARGE(413, "client_error.payload_too_large"),
    /**
     * {@link HttpStatus#UNSUPPORTED_MEDIA_TYPE}
     * <p>{@code 415 Unsupported Media Type 不支持的格式}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.13">
     * HTTP/1.1: Semantics and Content, section 6.5.13</a>
     */
    UNSUPPORTED_MEDIA_TYPE(415, "client_error.unsupported_media_type"),

    /**
     * {@link HttpStatus#TOO_MANY_REQUESTS}
     * <p>{@code 429 Too Many Requests 频繁请求限流}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-4">Additional HTTP Status Codes</a>
     */
    TOO_MANY_REQUESTS(429, "client_error.too_many_requests"),
    /**
     * 433 自定义错误码 参数检查不通过
     */
    PARAM_CHECK_NOT_PASS(433, "client_error.param_check_not_pass"),
    /**
     * 434 自定义错误码 参数解密错误
     */
    PARAM_DECRYPT_ERROR(434, "client_error.param_decrypt_error"),

    // 5xx Server Error 服务器错误

    /**
     * {@link HttpStatus#INTERNAL_SERVER_ERROR}
     * <p>{@code 500 Internal Server Error 服务端错误}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.1">HTTP/1.1: Semantics and Content, section 6.6.1</a>
     */
    INTERNAL_SERVER_ERROR(500, "server_error.internal_server_error"),

    /**
     * {@link HttpStatus#SERVICE_UNAVAILABLE}
     * <p>{@code 503 Service Unavailable 服务器停机维护或者已超载}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.4">HTTP/1.1: Semantics and Content, section 6.6.4</a>
     */
    SERVICE_UNAVAILABLE(503, "server_error.service_unavailable"),

    // 600 Business Error 业务自定义错误提示

    /**
     * 错误提示，请使用具体的错误提示信息覆盖此 {@link #msg}
     */
    BUSINESS_ERROR(600, "业务错误"),
    /**
     * 提示错误，请使用具体的错误提示信息覆盖此 {@link #msg}
     */
    ERROR_PROMPT(601, "错误提示，请使用具体的错误提示信息覆盖此 msg"),
    /**
     * 弹窗错误，请使用具体的错误提示信息覆盖此 {@link #msg}
     */
    ERROR_ALERT(602, "弹窗错误，请使用具体的错误提示信息覆盖此 msg");

    final Integer code;
    final String msg;

    /**
     * 如果可能，将给定的状态代码解析为 {@code ResultEnum}
     *
     * @param code HTTP 状态码 (可能是非标准的)
     * @return 对应的 {@code ResultEnum}，如果没有找到，则为 {@code null}
     */
    @Nullable
    public static ResultEnum valueOf(int code) {
        for (ResultEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }

        return null;
    }

}