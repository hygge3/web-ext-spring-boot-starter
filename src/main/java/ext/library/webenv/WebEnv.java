package ext.library.webenv;

import com.alibaba.fastjson.JSONObject;
import ext.library.util.ParamUtils;
import ext.library.view.Result;

/**
 * <b>Web 环境实现</b>
 * <p>如：WebMvc、WebFlux
 */
public interface WebEnv {

    // Result

    /**
     * {@link Result#response()}
     */
	void resultResponse(Result<?> result);

    // ParamUtils

    /**
     * {@link ParamUtils#getParam()}
     *
     * @return JSON 对象
     */
	JSONObject getParam();

    /**
     * {@link ParamUtils#getParam(Class)}
     *
     * @param <T>   泛型
     * @param clazz 想要获取的参数类型
     * @return 想要的对象实例
     */
	<T> T getParam(Class<T> clazz);

}