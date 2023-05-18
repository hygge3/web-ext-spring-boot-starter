package ext.library.convert.converter;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSONObject;
import ext.library.convert.Convert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JSONList 类型转换器
 */
public class JSONListConverter extends AbstractConverter<List<JSONObject>> {

    static final long serialVersionUID = -4664287699033731805L;

    List<JSONObject> registryType;
    ArrayList<JSONObject> registryType2;

    public static List<Type> getRegistryTypes() {
        Type registryType = ReflectUtil.getField(JSONListConverter.class, "registryType").getGenericType();
        Type registryType2 = ReflectUtil.getField(JSONListConverter.class, "registryType2").getGenericType();
        List<Type> registryTypes = new ArrayList<>();
        registryTypes.add(registryType);
        registryTypes.add(registryType2);
        return registryTypes;
    }

    @Override
    protected List<JSONObject> convertInternal(Object value) {
        return Convert.toJsonList(Convert.toJSONArray(value));
    }

}