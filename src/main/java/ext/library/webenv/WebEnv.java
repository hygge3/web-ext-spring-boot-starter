package ext.library.webenv;

import com.alibaba.fastjson2.JSONObject;
import ext.library.util.ParamUtils;
import ext.library.util.ServletUtils;
import ext.library.view.Result;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * <b>Web 环境实现</b>
 * <p>如：WebMvc、WebFlux
 */
@Component
public interface WebEnv {

    // Result

    /**
     * {@link Result#response()}
     */
    default void resultResponse(Result<?> result) {
        HttpServletResponse response = ServletUtils.getResponse();
        Objects.requireNonNull(response).setStatus(result.getCode());
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer;
        try {
            writer = response.getWriter();
            writer.print(JSONObject.toJSONString(result));
            writer.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ParamUtils

    /**
     * {@link ParamUtils#getParam()}
     *
     * @return JSON 对象
     */
    default JSONObject getParam() {
        return ServletUtils.getParamToJson();

    }

    /**
     * {@link ParamUtils#getParam(Class)}
     *
     * @param <T>   泛型
     * @param clazz 想要获取的参数类型
     * @return 想要的对象实例
     */
    default <T> T getParam(Class<T> clazz) {
        return ServletUtils.getParamToJavaBean(clazz);
    }

}