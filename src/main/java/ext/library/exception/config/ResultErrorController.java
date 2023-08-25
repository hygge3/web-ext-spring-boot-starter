package ext.library.exception.config;

import ext.library.web.view.R;
import ext.library.web.view.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 异常响应控制器
 */
@Slf4j
public class ResultErrorController extends BasicErrorController {

    final ErrorAttributes errorAttributes;

    public ResultErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
        this.errorAttributes = errorAttributes;
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Throwable error = errorAttributes.getError(new ServletWebRequest(request));
        Result<?> result = R.getResult(error);
        if (error != null) {
            log.info("【异常响应控制器】拦截到异常类型：{}，异常信息：{}，处理后响应内容：{}", error.getClass()
                    .getName(), error.getMessage(), result);
        } else {
            log.info("【异常响应控制器】拦截到异常类型：{}，异常信息：{}，处理后响应内容：{}", 404, result.getMsg(), result);
        }

        return ResponseEntity.status(result.getCode()).body(result.castToJSONObject());
    }

}
