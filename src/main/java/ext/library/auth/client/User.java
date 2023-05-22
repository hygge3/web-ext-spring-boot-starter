package ext.library.auth.client;

import com.alibaba.fastjson2.JSONObject;
import ext.library.auth.config.properties.AuthProperties;
import ext.library.exception.LoginException;
import ext.library.exception.ResultException;
import ext.library.redis.client.Redis;
import ext.library.util.IdUtils;
import ext.library.util.ServletUtils;
import ext.library.util.StringUtils;
import ext.library.web.view.R;
import ext.library.web.view.Result;
import ext.library.web.view.ResultPrompt;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * <b>User 客户端</b>
 * <p>登录登出、第三方登录、token 自动解析获取用户信息、分布式验证码
 */
@NoArgsConstructor
public class User {
    @Autowired
    protected Redis redis;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected AuthProperties authProperties;
    @Autowired
    private HttpServletResponse response;

    /**
     * 获得请求 token
     */
    public String getRequestToken() {
        Cookie cookie = ServletUtils.getCookie(request, authProperties.getCookieTokenKey());
        String token;
        if (cookie != null) {
            token = cookie.getValue();
        } else {
            token = request.getHeader(authProperties.getCookieTokenKey());
        }
        return token;
    }

    /**
     * 获得用户 ID
     * <p><code style="color:red"><b>注意：若 userId == null，请先确认 auth-service 模块的 login(Object) 方法是否存入 {@linkplain AuthProperties#getUserKey()} 字段，此处可以传 JSON 与 POJO 对象</b></code>
     *
     * @return userId
     */
    public Long getUserId() {
        try {
            // 1. 获得请求 token
            String token = getRequestToken();

            // 2. 确认 token
            if (StringUtils.isEmpty(token)) {
                throw new LoginException("token is null");
            }

            // 3. 查询 Redis 中 token 的值
            String tokenValue = redis.get(authProperties.getRedisTokenPrefix() + token);

            // 4. 返回 userId
            return JSONObject.parseObject(tokenValue).getLong(authProperties.getUserKey());
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }

    /**
     * 获得用户相关信息
     *
     * @param <T>   泛型
     * @param clazz 泛型类型
     * @return POJO 对象
     */
    public <T> T getUser(Class<T> clazz) {
        try {
            // 1. 获得请求 token
            String token = getRequestToken();

            // 2. 确认 token
            if (StringUtils.isEmpty(token)) {
                throw new LoginException("token == null");
            }

            // 3. 查询 Redis 中 token 的值
            String tokenValue = redis.get(authProperties.getRedisTokenPrefix() + token);

            // 4. 返回 POJO
            T t = JSONObject.parseObject(tokenValue, clazz);
            if (t == null) {
                throw new LoginException(null);
            }
            return t;
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }

    /**
     * 验证 - 验证码（基于 redis 解决分布式验证的问题）
     * <p>验证码错误会抛出一个{@linkplain ResultException}异常，作为结果提示...<br>
     *
     * @param captcha 验证码
     * @throws ResultException 验证码错误
     */
    public void captchaValidate(String captcha) {
        String captchaRedisKey = String.format(authProperties.getRedisCaptchaPrefix(), captcha);
        String randCaptcha = redis.get(captchaRedisKey);
        if (StringUtils.isEmpty(randCaptcha) || !randCaptcha.equalsIgnoreCase(captcha)) {
            throw new ResultException(R.errorPrompt(ResultPrompt.CAPTCHA_ERROR));
        }

        redis.del(captchaRedisKey);
    }

    /**
     * 生成验证码（基于 redis 解决分布式验证的问题）
     */
    public void putCaptcha() {
        // 1. 生成验证码
        String captcha = IdUtils.getRandomCodeToUpperCase(6);
        // 2. 设置验证码到 Redis
        String captchaRedisKey = String.format(authProperties.getRedisCaptchaPrefix(), captcha);
        redis.set(captchaRedisKey, captcha, authProperties.getCaptchaTimeout());
    }

    /**
     * 登录
     * <p>登录成功 - 设置 token 至 Cookie
     * <p>登录成功 - 设置 token 至 Header
     * <p><code style="color:red"><b>注意：登录之后的所有相关操作，都是基于请求报文中所携带的 token，若 Cookie 与 Header 皆没有 token 或 Redis 中匹配不到值，将视为未登录状态
     * </b></code>
     *
     * @param userInfo 用户信息（必须包含：<code style="color:red">{@linkplain AuthProperties#getUserKey()}，默认：userId，可通过配置文件进行配置</code>）
     * @return <b>token</b> 身份认证令牌 <code style="color:red"><b>（不建议使用，最好是忽略这个返回值，哪怕你只是将他放在响应体里面，也不推荐这样做）</b></code>
     * <p>支持 Cookie：建议使用默认的机制即可
     * <p>不支持 Cookie：建议从响应 Header 中获取 token，之后的请求都将 token 放入请求 Header 中即可
     */
    public String login(Object userInfo) {
        // 1. 获得请求 token
        String token = getRequestToken();

        // 2. 注销会话
        String redisTokenKey;
        if (StringUtils.isNotEmpty(token)) {
            redisTokenKey = authProperties.getRedisTokenPrefix() + token;
            String tokenValue = redis.get(redisTokenKey);
            if (StringUtils.isNotEmpty(tokenValue)) {
                redis.del(redisTokenKey);
            }
        }

        // 3. 生成新的 token
        token = UUID.randomUUID().toString();
        redisTokenKey = authProperties.getRedisTokenPrefix() + token;

        // 4. 登录成功 - 设置 token 至 Redis
        Integer tokenTimeout = authProperties.getTokenTimeout();
        redis.set(redisTokenKey, JSONObject.toJSONString(userInfo), tokenTimeout);

        // 5. 登录成功 - 设置 token 至 Cookie
        ServletUtils.addCookie(response, authProperties.getCookieTokenKey(), token, tokenTimeout);

        // 6. 登录成功 - 设置 token 至 Header
        response.setHeader(authProperties.getCookieTokenKey(), token);

        // 7. 登录成功 - 返回 token
        return token;
    }

    /**
     * 登出
     * <p>清除 Redis-token
     * <p>清除 Cookie-token
     *
     * @return 成功
     */
    public Result<?> logout() {
        // 1. 获得请求 token
        String token = getRequestToken();

        // 2. 确认 token
        if (StringUtils.isEmpty(token)) {
            return R.unauthorized();
        }

        // 3. 清除 Redis-token
        redis.del(authProperties.getRedisTokenPrefix() + token);

        // 4. 清除 Cookie-token
        ServletUtils.addCookie(response, authProperties.getCookieTokenKey(), null, 0);

        // 5. 返回结果
        return R.success();
    }

}