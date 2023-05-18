package ext.library.convert.converter;

import cn.hutool.core.convert.AbstractConverter;
import com.alibaba.fastjson.JSONArray;
import ext.library.convert.Convert;

/**
 * JSONArray 类型转换器
 */
public class JSONArrayConverter extends AbstractConverter<JSONArray> {

    static final long serialVersionUID = 1L;

    @Override
    protected JSONArray convertInternal(Object value) {
        return Convert.toJSONArray(value);
    }

}