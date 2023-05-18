package ext.library.convert.converter;

import cn.hutool.core.convert.AbstractConverter;
import com.alibaba.fastjson2.JSONObject;
import ext.library.convert.Convert;

/**
 * JSONObject 类型转换器
 */
public class JSONObjectConverter extends AbstractConverter<JSONObject> {

    static final long serialVersionUID = 1L;

    @Override
    protected JSONObject convertInternal(Object value) {
        return Convert.toJSONObject(value);
    }

}