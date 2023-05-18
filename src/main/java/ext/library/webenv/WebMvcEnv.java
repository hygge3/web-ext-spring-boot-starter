package ext.library.webenv;

import com.alibaba.fastjson2.JSONObject;
import ext.library.util.ServletUtils;
import ext.library.view.Result;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 *
 */
@Component
public class WebMvcEnv implements WebEnv {

    @Override
    public void resultResponse(Result<?> result) {
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

    @Override
    public JSONObject getParam() {
        return ServletUtils.getParamToJson();
    }

    @Override
    public <T> T getParam(Class<T> clazz) {
        return ServletUtils.getParamToJavaBean(clazz);
    }

}