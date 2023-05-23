package ext.library.auth.config.properties;

import ext.library.auth.client.UserClient;
import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Auth 可配置属性，适用于 auth 所有模块的通用可配置属性
 */
@Data
@ConfigurationProperties(AuthProperties.PREFIX)
public class AuthProperties {

    /**
     * Prefix of {@link AuthProperties}.
     */
    public static final String PREFIX = Constant.PROJECT_PREFIX + ".auth";
    public static final String PREFIX_REDIS = Constant.PROJECT_PREFIX + ":auth:";
    /** 验证码前缀 */
    String redisCaptchaPrefix = "captcha_%s";
    /**
     * Cookie Token Key
     * <p>默认：token
     */
    String cookieTokenKey = "token";
    /**
     * Redis Token 前缀（自定义值，请保留“<code style="color:red">:</code>”部分）
     * <p>默认：token:
     */
    String redisTokenPrefix = PREFIX_REDIS + "token:";
    /**
     * Redis Token Value 序列化后的 key，反序列化时需使用，如：{@linkplain UserClient#getUserId()}
     * <p>默认：userId
     */
    String userKey = "userId";

    /**
     * IP 前缀（自定义值，请保留“<code style="color:red">_%s</code>”部分）
     * <p>默认：ip_%s
     */
    String ipPrefix = "ip_%s";

    /**
     * Token 超时时间（单位：秒）
     * <p>默认：36000（10 小时）
     */
    Integer tokenTimeout = 36000;

    /**
     * 验证码超时时间（单位：秒）
     * <p>默认：360（6 分钟）
     */
    Integer captchaTimeout = 360;


}