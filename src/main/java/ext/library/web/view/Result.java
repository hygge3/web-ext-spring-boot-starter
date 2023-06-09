package ext.library.web.view;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import ext.library.convert.Convert;
import ext.library.exception.ResultException;
import ext.library.util.I18nUtils;
import ext.library.util.ListUtils;
import ext.library.util.SpringUtils;
import ext.library.web.webenv.WebEnv;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static com.alibaba.fastjson2.util.TypeUtils.toBigDecimal;
import static com.alibaba.fastjson2.util.TypeUtils.toBigInteger;
import static com.alibaba.fastjson2.util.TypeUtils.toBoolean;
import static com.alibaba.fastjson2.util.TypeUtils.toDate;
import static com.alibaba.fastjson2.util.TypeUtils.toDouble;
import static com.alibaba.fastjson2.util.TypeUtils.toInteger;
import static com.alibaba.fastjson2.util.TypeUtils.toLong;

/**
 * HTTP 请求最外层响应对象，更适应 RESTful 风格 API
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3830508963654505583L;

    /** 响应状态码 */
    @JSONField(ordinal = 1)
    private Integer code;
    /** 响应提示 */
    @JSONField(ordinal = 2)
    private String msg;
    /** 响应状态 */
    @JSONField(ordinal = 3)
    private boolean flag;
    /** 链路追踪码 */
    @JSONField(ordinal = 4)
    private String traceId;
    /** 业务数据 */
    @JSONField(ordinal = 5)
    private T data;

    /**
     * <b>成功校验</b>
     * <p>如果此处获得的 Result 是一个错误提示结果，那么便会抛出一个 {@linkplain ResultException} 异常，以便于数据回滚并进行异常统一处理。
     *
     * @throws ResultException 返回的请求异常结果
     */
    public void successValidate() {
        if (!flag) {
            throw new ResultException(this);
        }
    }

    // -------- getData --------

    public <D> D getData(Class<D> clazz) {
        return Convert.convert(data, clazz);
    }

    public <D> D dataToObject(Class<D> clazz) {
        return getData(clazz);
    }

    public <D> D dataToJavaBean(Class<D> clazz) {
        return Convert.toJavaBean(data, clazz);
    }

    public JSONObject dataToJSONObject() {
        return Convert.toJSONObject(data);
    }

    public JSONArray dataToJSONArray() {
        return Convert.toJSONArray(data);
    }

    public <D> List<D> dataToList(Class<D> clazz) {
        return Convert.toList(clazz, data);
    }

    @SuppressWarnings("unchecked")
    public List<JSONObject> dataToJsonList() {
        if (data instanceof List<?> dataTemp) {
            if (ListUtils.isNotEmpty(dataTemp)) {
                if (dataTemp.get(0) instanceof JSONObject) {
                    return (List<JSONObject>) data;
                }
            }
        }

        return ListUtils.toJsonList(dataToJSONArray());
    }

    public Boolean dataToBoolean() {
        if (data == null) {
            return null;
        }
        return toBoolean(data);
    }

    public Integer dataToInteger() {
        return toInteger(data);
    }

    public Long dataToLong() {
        return toLong(data);
    }

    public Double dataToDouble() {
        return toDouble(data);
    }

    public BigDecimal dataToBigDecimal() {
        return toBigDecimal(data);
    }

    public BigInteger dataToBigInteger() {
        return toBigInteger(data);
    }

    public String dataToString() {

        if (data == null) {
            return null;
        }

        return data.toString();
    }

    public String dataToJSONString() {

        if (data == null) {
            return null;
        }

        return Convert.toJSONString(data);
    }

    public Date dataToDate() {

        return toDate(data);
    }

    // -------- result convert --------

    public ResponseEntity<Result<?>> castToResponseEntity() {
        return ResponseEntity.status(getCode()).body(this);
    }

    public String castToJSONString() {
        this.setMsg(I18nUtils.getExt(this.getMsg()));
        return Convert.toJSONString(this);
    }

    public JSONObject castToJSONObject() {
        this.setMsg(I18nUtils.getExt(this.getMsg()));
        return Convert.toJSONObject(this);
    }

    /**
     * 将 Result 写入当前请求上下文的响应结果中，如：HttpServletResponse 等。具体由当前 {@link WebEnv} 环境实现
     */
    public void response() {
        WebEnv webEnv = SpringUtils.getBean(WebEnv.class);
        webEnv.resultResponse(this);
    }

}