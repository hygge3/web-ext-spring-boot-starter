package ext.library.web.webenv;

import cn.hutool.core.lang.Dict;
import ext.library.util.ParamUtils;
import ext.library.util.ServletUtils;
import org.springframework.stereotype.Component;

/**
 * <b>Web 环境实现</b>
 * <p>如：WebMvc、WebFlux
 */
@Component
public class WebEnv {

    // ParamUtils

    /**
     * {@link ParamUtils#getParam()}
     *
     * @return JSON 对象
     */
    public Dict getParam() {
        return ServletUtils.getParamToJson();

    }

    /**
     * {@link ParamUtils#getParam(Class)}
     *
     * @param <T>   泛型
     * @param clazz 想要获取的参数类型
     * @return 想要的对象实例
     */
    public <T> T getParam(Class<T> clazz) {
        return ServletUtils.getParamToJavaBean(clazz);
    }

}