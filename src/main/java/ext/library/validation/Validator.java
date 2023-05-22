package ext.library.validation;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import ext.library.util.I18nUtils;
import ext.library.util.SpringUtils;
import ext.library.util.StringUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

/**
 * <b>参数校验器</b>
 * <p>全面简单的校验框架，更适合国内校验场景，支持多种校验方式，配合全局异常处理，只为更好的 RESTful
 */
@Slf4j
@NoArgsConstructor
public class Validator {

    // 提示
    static final String NOT_NULL_HINT_MSG = "参数 {} 必须不为 null";
    static final String NOT_EMPTY_HINT_MSG = "参数 {} 必须不为 empty(null 或 \"\")";
    static final String ASSERT_TRUE_HINT_MSG = "参数 {} 必须为 true";
    static final String ASSERT_FALSE_HINT_MSG = "参数 {} 必须为 false";
    static final String DIGITS_HINT_MSG = "参数 {} 必须是一个数字，其值必须在 {} - {} 之间（包含）";
    static final String MAX_HINT_MSG = "参数 {} 不能超过最大值：{}";
    static final String MIN_HINT_MSG = "参数 {} 不能低于最小值：{}";
    static final String LENGTH_HINT_MSG = "参数 {} 长度必须在 {} - {} 之间（包含）";
    static final String CHINESE_HINT_MSG = "参数 {} 中文校验不通过";
    static final String ENGLISH_HINT_MSG = "参数 {} 英文校验不通过";
    static final String CELLPHONE_HINT_MSG = "参数 {} 不是一个合法的手机号码";
    static final String EMAIL_HINT_MSG = "参数 {} 不是一个合法的邮箱格式";
    static final String ID_CARD_HINT_MSG = "参数 {} 不是一个合法的身份证号码";
    static final String UUID_HINT_MSG = "参数 {} 不是一个合法的 UUID";
    static final String URL_HINT_MSG = "参数 {} 不是一个合法的 URL";
    static final String IPV4_HINT_MSG = "参数 {} 不是一个合法的 IPV4 地址";
    static final String IPV6_HINT_MSG = "参数 {} 不是一个合法的 IPV6 地址";
    static final String MAC_ADDRESS_HINT_MSG = "参数 {} 不是一个合法的 MAC 地址";
    static final String ZIP_CODE_HINT_MSG = "参数 {} 不是一个合法的邮政编码（中国）";
    static final String REGEX_HINT_MSG = "参数 {} 不满足正则表达式：{}";
    static final String USERNAME_HINT_MSG = "参数 {} 不是一个合法的用户名";
    Object param;

    /**
     * 获得参数校验器并设置校验对象
     *
     * @param param 校验对象
     * @return Validator
     */
    public static Validator getValidatorAndSetParam(Object param) {
        return SpringUtils.getBean(Validator.class).param(param);
    }

    /**
     * 切换校验对象
     *
     * @param param 校验对象
     * @return Validator
     */
    public Validator param(Object param) {
        this.param = param;
        return this;
    }

    /**
     * 必须不为 {@code null}
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator notNull(String paramName) {
        cn.hutool.core.lang.Validator.validateNotNull(param, StrUtil.format(NOT_NULL_HINT_MSG, paramName));
        return this;
    }

    /**
     * 必须不为 empty(null 或 "")
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator notEmpty(String paramName) {
        cn.hutool.core.lang.Validator.validateNotEmpty(param, StrUtil.format(NOT_EMPTY_HINT_MSG, paramName));
        return this;
    }

    /**
     * 必须为 true
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator assertTrue(String paramName) {
        cn.hutool.core.lang.Validator.validateTrue((boolean) param, StrUtil.format(ASSERT_TRUE_HINT_MSG, paramName));
        return this;
    }

    /**
     * 必须为 false
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator assertFalse(String paramName) {
        cn.hutool.core.lang.Validator.validateFalse((boolean) param, StrUtil.format(ASSERT_FALSE_HINT_MSG, paramName));
        return this;
    }

    /**
     * 必须是一个数字，其值必须在可接受的范围内（包含）
     *
     * @param min       最小值
     * @param max       最大值
     * @param paramName 参数名
     * @return Validator
     */
    public Validator digits(Number min, Number max, String paramName) {
        cn.hutool.core.lang.Validator.validateBetween((Number) param, min, max, StrUtil.format(DIGITS_HINT_MSG, paramName, min, max));
        return this;
    }

    /**
     * 最大值校验
     *
     * @param max       最大值
     * @param paramName 参数名
     * @return Validator
     */
    public Validator max(Number max, String paramName) {
        BigDecimal bigNum1 = NumberUtil.toBigDecimal((Number) param);
        BigDecimal bigNum2 = NumberUtil.toBigDecimal(max);

        if (!NumberUtil.isLessOrEqual(bigNum1, bigNum2)) {
            throw new ValidateException(StrUtil.format(MAX_HINT_MSG, paramName, max));
        }
        return this;
    }

    /**
     * 最小值校验
     *
     * @param min       最小值
     * @param paramName 参数名
     * @return Validator
     */
    public Validator min(Number min, String paramName) {
        BigDecimal bigNum1 = NumberUtil.toBigDecimal((Number) param);
        BigDecimal bigNum2 = NumberUtil.toBigDecimal(min);

        if (!NumberUtil.isGreaterOrEqual(bigNum1, bigNum2)) {
            throw new ValidateException(StrUtil.format(MIN_HINT_MSG, paramName, min));
        }
        return this;
    }

    /**
     * 长度校验
     *
     * @param min       最小长度
     * @param max       最大长度
     * @param paramName 参数名
     * @return Validator
     */
    public Validator length(int min, int max, String paramName) {
        int length = ObjectUtil.length(param);
        if (!(length >= min && length <= max)) {
            throw new ValidateException(StrUtil.format(LENGTH_HINT_MSG, paramName, min, max));
        }

        return this;
    }

    /**
     * 中文校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator chinese(String paramName) {
        cn.hutool.core.lang.Validator.validateChinese((CharSequence) param, StrUtil.format(CHINESE_HINT_MSG, paramName));
        return this;
    }

    /**
     * 英文校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator english(String paramName) {
        cn.hutool.core.lang.Validator.validateWord((CharSequence) param, StrUtil.format(ENGLISH_HINT_MSG, paramName));
        return this;
    }

    /**
     * 手机号校验
     */
    public Validator cellphone() {
        return cellphone("cellphone");
    }

    /**
     * 手机号校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator cellphone(String paramName) {
        cn.hutool.core.lang.Validator.validateMobile((CharSequence) param, StrUtil.format(CELLPHONE_HINT_MSG, paramName));
        return this;
    }

    /**
     * 邮箱校验
     */
    public Validator email() {
        return email("email");
    }

    /**
     * 邮箱校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator email(String paramName) {
        cn.hutool.core.lang.Validator.validateEmail((CharSequence) param, StrUtil.format(EMAIL_HINT_MSG, paramName));
        return this;
    }


    /**
     * 身份证校验
     */
    public Validator idCard() {
        return idCard("idCard");
    }

    /**
     * 身份证校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator idCard(String paramName) {
        cn.hutool.core.lang.Validator.validateCitizenIdNumber((CharSequence) param, StrUtil.format(ID_CARD_HINT_MSG, paramName));
        return this;
    }

    /**
     * UUID 校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator uuid(String paramName) {
        cn.hutool.core.lang.Validator.validateUUID((CharSequence) param, StrUtil.format(UUID_HINT_MSG, paramName));
        return this;
    }

    /**
     * URL 校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator url(String paramName) {
        cn.hutool.core.lang.Validator.validateUrl((CharSequence) param, StrUtil.format(URL_HINT_MSG, paramName));
        return this;
    }

    /**
     * IPV4 地址校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator ipv4(String paramName) {
        cn.hutool.core.lang.Validator.validateIpv4((CharSequence) param, StrUtil.format(IPV4_HINT_MSG, paramName));
        return this;
    }

    /**
     * IPV6 地址校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator ipv6(String paramName) {
        cn.hutool.core.lang.Validator.validateIpv6((CharSequence) param, StrUtil.format(IPV6_HINT_MSG, paramName));
        return this;
    }

    /**
     * MAC 地址校验
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator macAddress(String paramName) {
        cn.hutool.core.lang.Validator.validateMac((CharSequence) param, StrUtil.format(MAC_ADDRESS_HINT_MSG, paramName));
        return this;
    }

    /**
     * 验证是否为邮政编码（中国）
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator zipCode(String paramName) {
        cn.hutool.core.lang.Validator.validateZipCode((CharSequence) param, StrUtil.format(ZIP_CODE_HINT_MSG, paramName));
        return this;
    }

    /**
     * 用户名校验
     */
    public Validator username() {
        return username("username");
    }

    /**
     * 用户名校验
     *
     * <ul>
     *     <li>长度必须大于等于 5</li>
     *     <li>不能包含@符号，避免是邮箱</li>
     *     <li>不能是手机号</li>
     * </ul>
     *
     * @param paramName 参数名
     * @return Validator
     */
    public Validator username(String paramName) {
        String value = (String) param;
        boolean isValid = true;
        if (StringUtils.isNotBlank(value)) {
            if (value.length() < 5) {
                isValid = false;
            }
            if (value.contains("@")) {
                isValid = false;
            }
            if (cn.hutool.core.lang.Validator.isMobile(value)) {
                isValid = false;
            }
        } else {
            isValid = false;
        }

        if (!isValid) {
            throw new ValidateException(USERNAME_HINT_MSG, paramName);
        }

        return this;
    }

    /**
     * 正则校验
     *
     * @param regex     正则表达式
     * @param paramName 参数名
     * @return Validator
     */
    public Validator regex(String regex, String paramName) {
        cn.hutool.core.lang.Validator.validateMatchRegex(regex, (CharSequence) param, StrUtil.format(REGEX_HINT_MSG, paramName, regex));
        return this;
    }

    /**
     * POJO 对象校验（通过注解）
     *
     * @param param  校验对象
     * @param groups 用于验证的组或组列表 (默认为 Default)
     * @return Validator
     */
    public Validator valid(Object param, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violations = SpringUtils.getBean(jakarta.validation.Validator.class)
                .validate(param, groups);
        if (violations.size() > 0) {
            log.warn("{} violations.", violations.size());
            Console.log("校验对象：{}", param);
            JSONArray errorHints = new JSONArray();
            violations.forEach(violation -> {
                String errorKey = violation.getPropertyPath().toString();
                Object errorValue = violation.getInvalidValue();
                String errorHintMsg = I18nUtils.getExt(violation.getMessage());
                JSONObject errorHint = new JSONObject();
                errorHint.put("errorKey", errorKey);
                errorHint.put("errorValue", errorValue);
                errorHint.put("errorHintMsg", errorHintMsg);
                errorHints.add(errorHint);
                System.out.println(errorHint.toString(JSONWriter.Feature.WriteMapNullValue));
            });

            throw new ValidateException(errorHints.toString(JSONWriter.Feature.WriteMapNullValue));
        }

        return this;
    }

}