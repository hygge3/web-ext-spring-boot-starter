package ext.library.repeatedly.read;

import ext.library.constant.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * RepeatedlyReadServletRequestWrapper 过滤器
 * <p>传递输入流可反复读取的 HttpServletRequest
 * <p>OncePerRequestFilter 是在一次外部请求中只过滤一次。对于服务器内部之间的 forward 等请求，不会再次执行过滤方法。
 */
@Slf4j
public class RepeatedlyReadServletRequestFilter extends OncePerRequestFilter {

    private static final String PARAM_TRANSMIT = Constant.PREFIX + "Param-Transmit";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("传递输入流可反复读取的 HttpServletRequest ...");

        /*
         * 在 request 被包装之前，先进行一次参数解析处理，避免后续出现使用未包装的 request 导致：FileUploadException: Stream closed.
         * 如：调用 request#getParts() 将使用已解析过的结果（parse multipart content and store the parts）.
         */
        request.getParameter(PARAM_TRANSMIT);

        ServletRequest requestWrapper = new RepeatedlyReadServletRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
    }

}