package ext.library.constant;

/**
 * http 属性
 *
 * @author Huy Cheung
 */
public interface HttpAttribute {

    // 请求头
    /**
     * 链路 ID
     */
    String TRACE_ID = Constant.PREFIX + "Trace-Id";
    /** 请求时间 */
    String REQUEST_TIME = Constant.PREFIX + "Request-Time";

    /** 时区 */
    String TIME_ZONE = Constant.PREFIX + "Time-Zone";

    // 请求属性

    /** 幂等性版本号请求 key */
    String API_IDEMPOTENT_VERSION = Constant.PREFIX + "Api-Idempotent-Version";

    /**
     * ServletAsyncContext 阻塞超时时长 setAttribute 时所使用的固定变量名
     */
    String SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS = Constant.PREFIX + "Servlet-Async-Context-Timeout-Millis";


}