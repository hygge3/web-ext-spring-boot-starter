package ext.library.web.properties;

import ext.library.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.Cookie;


/**
 * cookie 自动配置属性
 */
@Data
@ConfigurationProperties(CookieProperties.PREFIX)
public class CookieProperties {

    static final String PREFIX = Constant.PROJECT_PREFIX + ".cookie";

    /**
     * 指定哪些主机可以接受 Cookie。如果不指定，该属性默认为同一 host 设置 cookie，不包含子域名
     * 例如，如果设置 Domain=mozilla.org，则 Cookie 也包含在子域名中（如 developer.mozilla.org）
     */
    String domain;

    /**
     * SameSite 属性允许服务器指定是否/何时通过跨站点请求发送（其中站点由注册的域和方案定义：http 或 https）。
     * 这提供了一些针对跨站点请求伪造攻击（CSRF）的保护。它采用三个可能的值：Strict、Lax 和 None。
     * someSize:None: 浏览器在同站请求、跨站请求下都会发送 Cookies
     * someSize:Strict: 浏览器只会在相同站点下发送 Cookies
     * someSize:Lax: 与 strict 类似，不同的是它可以从外站通过链接导航到该站
     */
    Cookie.SameSite sameSite = Cookie.SameSite.NONE;
    /** 标记为 secure 的 Cookie 只应通过被 Https 协议加密过的请求发送给服务端。SameSite=None 的 cookie 还必须指定 Secure 属性 */
    Boolean secure = true;
    /** 设置是否可以通过 javascript 访问 Cookie */
    Boolean httpOnly = true;


}