package ext.library.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.multipart.MultipartFormData;
import cn.hutool.core.net.multipart.UploadSetting;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import ext.library.convert.Convert;
import ext.library.web.properties.CookieProperties;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Servlet 相关工具类封装<br>
 * 源自 hutool-extra 增强
 */
public class ServletUtils {

    static final String METHOD_DELETE = "DELETE";
    static final String METHOD_HEAD = "HEAD";
    static final String METHOD_GET = "GET";
    static final String METHOD_OPTIONS = "OPTIONS";
    static final String METHOD_POST = "POST";
    static final String METHOD_PUT = "PUT";
    static final String METHOD_TRACE = "TRACE";
    static final String HTTP_TCP_NAME = "http://";
    static final String HTTPS_TCP_NAME = "https://";
    static final String BEARER_TYPE = "Bearer ";
    static final String ACCESS_TOKEN = "access_token";


    /**
     * 获得当前请求上下文中的{@linkplain ServletRequestAttributes}
     *
     * @return ServletRequestAttributes
     */
    public static ServletRequestAttributes getRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    }

    /**
     * 获得当前请求上下文中的{@linkplain HttpServletRequest}
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获得当前请求上下文中的{@linkplain HttpServletResponse}
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getResponse();
    }

    /**
     * 获得当前请求{@linkplain HttpSession}
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        return Objects.requireNonNull(getRequest()).getSession();
    }

    /**
     * 获得请求中的 OAuth2 Token
     *
     * @return token
     */
    public static String getAuthToken() {
        return getAuthToken(Objects.requireNonNull(getRequest()));
    }

    /**
     * 获得请求中的 OAuth2 Token
     *
     * @param request 请求对象{@link ServletRequest}
     * @return token
     */
    public static String getAuthToken(HttpServletRequest request) {
        String authToken = StrUtil.subAfter(request.getHeader(HttpHeaders.AUTHORIZATION), BEARER_TYPE, false);
        if (StrUtil.isBlank(authToken)) {
            authToken = request.getParameter(ACCESS_TOKEN);
        }

        return authToken;
    }

    /**
     * 获得请求 Json
     *
     * @return 解析的 Json
     */
    public static Dict getParamToJson() {
        return getParamToJson(getRequest());
    }

    /**
     * 获得请求 Json
     *
     * @param request 请求对象{@link ServletRequest}
     * @return 解析的 Json
     */
    public static Dict getParamToJson(HttpServletRequest request) {
        // 获得所有请求参数
        Map<String, String> paramMap = ServletUtils.getParamMap(request);
        Dict paramJson = Dict.parse(paramMap);

        // 获取请求体：遵守一种行业默认的行为规范，对 GET 请求默认不处理 body，避免一些难以解释的复杂问题
        boolean getMethod = ServletUtils.isGetMethod(request);
        if (!getMethod) {
            String body = ServletUtils.getBody(request);
            if (StrUtil.isNotEmpty(body)) {
                Dict jsonBody = Convert.toDict(body);
                paramJson.putAll(jsonBody);
            }
        }

        // 返回解析 Json
        return paramJson;
    }

    /**
     * 获得请求参数并转换为 JavaBean
     *
     * @return 转换的 JavaBean
     */
    public static <T> T getParamToJavaBean(Class<T> clazz) {
        return getParamToJavaBean(getRequest(), clazz);
    }

    /**
     * 获得请求参数并转换为 JavaBean
     *
     * @param request 请求对象{@link ServletRequest}
     * @return 转换的 JavaBean
     */
    public static <T> T getParamToJavaBean(HttpServletRequest request, Class<T> clazz) {
        return Convert.toJavaBean(getParamToJson(request), clazz);
    }

    /**
     * 获得当前请求的服务器的 URL 地址
     * <p>
     * 示例一：http://localhost:8080<br>
     * 示例二：http://localhost:8080/projectName<br>
     *
     * @return 当前请求的服务器的 URL 地址
     */
    public static String getServerURL() {
        HttpServletRequest request = getRequest();
        // 服务器地址
        String serverName = Objects.requireNonNull(request).getServerName();
        // 端口号
        int serverPort = request.getServerPort();
        // 项目名称
        String contextPath = request.getContextPath();
        return HTTP_TCP_NAME + serverName + ":" + serverPort + contextPath;
    }

    /**
     * 打印请求报文
     * <p>
     * 注意：打印不包括：异步请求内容、数据流
     */
    public static void printRequest() {
        // 1. 打印请求信息
        HttpServletRequest request = getRequest();
        Console.error("========开始 - 打印请求报文========");
        Console.log();
        Console.log("打印请求信息：");
        Console.log("RemoteAddr:{}", Objects.requireNonNull(request).getRemoteAddr());
        Console.log("Method:{}", request.getMethod());
        Console.log("AuthType:{}", request.getAuthType());

        // 2. 打印服务器信息
        Console.log();
        Console.log("打印服务器信息：");
        Console.log("ServerURL:{}", getServerURL());
        Console.log("RequestURL:{}", request.getRequestURL());
        Console.log("RequestedSessionId:{}", request.getRequestedSessionId());

        // 3. 打印请求参数
        Console.log();
        Console.log("打印请求参数：");
        Console.log("QueryString:{}", request.getQueryString());
        Console.log("ParameterMap:{}", JsonUtils.toString(request.getParameterMap()));

        // 4. 打印请求头
        Console.log();
        Console.log("打印请求头：");
        Console.log("Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            StringBuilder headerValues = new StringBuilder();
            request.getHeaders(headerName).asIterator().forEachRemaining(headerValues::append);
            Console.log("  {}：{}", headerName, headerValues);
        });

        // 5. 打印 Cookie
        Console.log();
        Console.log("Cookies:");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                Console.log("  {}：{}", cookie.getName(), cookie.getValue());
            }
        }
        Console.error("========结束 - 打印请求报文========");
    }

    // --------------------------------------------------------- getParam start

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), ArrayUtil.join(entry.getValue(), StrUtil.COMMA));
        }
        return params;
    }

    /**
     * 获取请求体<br>
     * 调用该方法后，getParam 方法将失效
     *
     * @param request {@link ServletRequest}
     * @return 获得请求体
     * @since 4.0.2
     */
    public static String getBody(ServletRequest request) {
        try (final BufferedReader reader = request.getReader()) {
            return IoUtil.read(reader);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获取请求体 byte[]<br>
     * 调用该方法后，getParam 方法将失效
     *
     * @param request {@link ServletRequest}
     * @return 获得请求体 byte[]
     * @since 4.0.2
     */
    public static byte[] getBodyBytes(ServletRequest request) {
        try {
            return IoUtil.readBytes(request.getInputStream());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
    // --------------------------------------------------------- getParam end

    // --------------------------------------------------------- fillBean start

    /**
     * ServletRequest 参数转 Bean
     *
     * @param <T>         Bean 类型
     * @param request     ServletRequest
     * @param bean        Bean
     * @param copyOptions 注入时的设置
     * @return Bean
     * @since 3.0.4
     */
    public static <T> T fillBean(final ServletRequest request, T bean, CopyOptions copyOptions) {
        final String beanName = StrUtil.lowerFirst(bean.getClass().getSimpleName());
        return BeanUtil.fillBean(bean, new ValueProvider<>() {
            @Override
            public Object value(String key, Type valueType) {
                String[] values = request.getParameterValues(key);
                if (ArrayUtil.isEmpty(values)) {
                    values = request.getParameterValues(beanName + StrUtil.DOT + key);
                    if (ArrayUtil.isEmpty(values)) {
                        return null;
                    }
                }

                if (1 == values.length) {
                    // 单值表单直接返回这个值
                    return values[0];
                } else {
                    // 多值表单返回数组
                    return values;
                }
            }

            @Override
            public boolean containsKey(String key) {
                // 对于 Servlet 来说，返回值 null 意味着无此参数
                return (null != request.getParameter(key)) || (null != request.getParameter(beanName + StrUtil.DOT + key));
            }
        }, copyOptions);
    }

    /**
     * ServletRequest 参数转 Bean
     *
     * @param <T>           Bean 类型
     * @param request       {@link ServletRequest}
     * @param bean          Bean
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     */
    public static <T> T fillBean(ServletRequest request, T bean, boolean isIgnoreError) {
        return fillBean(request, bean, CopyOptions.create().setIgnoreError(isIgnoreError));
    }

    /**
     * ServletRequest 参数转 Bean
     *
     * @param <T>           Bean 类型
     * @param request       ServletRequest
     * @param beanClass     Bean Class
     * @param isIgnoreError 是否忽略注入错误
     * @return Bean
     */
    public static <T> T toBean(ServletRequest request, Class<T> beanClass, boolean isIgnoreError) {
        return fillBean(request, ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
    }
    // --------------------------------------------------------- fillBean end

    /**
     * 获取客户端 IP
     *
     * <p>
     * 默认检测的 Header:
     *
     * <pre>
     * 1、X-Forwarded-For
     * 2、X-Real-IP
     * 3、Proxy-Client-IP
     * 4、WL-Proxy-Client-IP
     * </pre>
     *
     * <p>
     * otherHeaderNames 参数用于自定义检测的 Header<br>
     * 需要注意的是，使用此方法获取的客户 IP 地址必须在 Http 服务器（例如 Nginx）中配置头信息，否则容易造成 IP 伪造。
     * </p>
     *
     * @param otherHeaderNames 其他自定义头文件，通常在 Http 服务器（例如 Nginx）中配置
     * @return IP 地址
     */
    public static String getClientIP(String... otherHeaderNames) {
        return getClientIP(ServletUtils.getRequest(), otherHeaderNames);
    }

    /**
     * 获取客户端 IP
     *
     * <p>
     * 默认检测的 Header:
     *
     * <pre>
     * 1、X-Forwarded-For
     * 2、X-Real-IP
     * 3、Proxy-Client-IP
     * 4、WL-Proxy-Client-IP
     * </pre>
     *
     * <p>
     * otherHeaderNames 参数用于自定义检测的 Header<br>
     * 需要注意的是，使用此方法获取的客户 IP 地址必须在 Http 服务器（例如 Nginx）中配置头信息，否则容易造成 IP 伪造。
     * </p>
     *
     * @param request          请求对象{@link HttpServletRequest}
     * @param otherHeaderNames 其他自定义头文件，通常在 Http 服务器（例如 Nginx）中配置
     * @return IP 地址
     */
    public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtil.isNotEmpty(otherHeaderNames)) {
            headers = ArrayUtil.addAll(headers, otherHeaderNames);
        }

        return getClientIPByHeader(request, headers);
    }

    /**
     * 获取客户端 IP
     *
     * <p>
     * headerNames 参数用于自定义检测的 Header<br>
     * 需要注意的是，使用此方法获取的客户 IP 地址必须在 Http 服务器（例如 Nginx）中配置头信息，否则容易造成 IP 伪造。
     * </p>
     *
     * @param request     请求对象{@link HttpServletRequest}
     * @param headerNames 自定义头，通常在 Http 服务器（例如 Nginx）中配置
     * @return IP 地址
     * @since 4.4.1
     */
    public static String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
        String ip;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (!NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return NetUtil.getMultistageReverseProxyIp(ip);
    }

    /**
     * 获得 MultiPart 表单内容，多用于获得上传的文件 在同一次请求中，此方法只能被执行一次！
     *
     * @param request {@link ServletRequest}
     * @return MultipartFormData
     * @throws IORuntimeException IO 异常
     * @since 4.0.2
     */
    public static MultipartFormData getMultipart(ServletRequest request) throws IORuntimeException {
        return getMultipart(request, new UploadSetting());
    }

    /**
     * 获得multipart/form-data 表单内容<br>
     * 包括文件和普通表单数据<br>
     * 在同一次请求中，此方法只能被执行一次！
     *
     * @param request       {@link ServletRequest}
     * @param uploadSetting 上传文件的设定，包括最大文件大小、保存在内存的边界大小、临时目录、扩展名限定等
     * @return MultiPart 表单
     * @throws IORuntimeException IO 异常
     * @since 4.0.2
     */
    public static MultipartFormData getMultipart(ServletRequest request, UploadSetting uploadSetting) throws IORuntimeException {
        final MultipartFormData formData = new MultipartFormData(uploadSetting);
        try {
            formData.parseRequestStream(request.getInputStream(), CharsetUtil.charset(request.getCharacterEncoding()));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        return formData;
    }

    // --------------------------------------------------------- Header start

    /**
     * 获取请求所有的头（header）信息
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return header 值
     * @since 4.6.2
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        final Map<String, String> headerMap = new HashMap<>();

        final Enumeration<String> names = request.getHeaderNames();
        String name;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            headerMap.put(name, request.getHeader(name));
        }

        return headerMap;
    }


    /**
     * 忽略大小写获得请求 header 中的信息
     *
     * @param request        请求对象{@link HttpServletRequest}
     * @param nameIgnoreCase 忽略大小写头信息的 KEY
     * @return header 值
     */
    public static String getHeaderIgnoreCase(HttpServletRequest request, String nameIgnoreCase) {
        final Enumeration<String> names = request.getHeaderNames();
        String name;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (name != null && name.equalsIgnoreCase(nameIgnoreCase)) {
                return request.getHeader(name);
            }
        }

        return null;
    }

    /**
     * 获得请求 header 中的信息
     *
     * @param request     请求对象{@link HttpServletRequest}
     * @param name        头信息的 KEY
     * @param charsetName 字符集
     * @return header 值
     */
    public static String getHeader(HttpServletRequest request, String name, String charsetName) {
        return getHeader(request, name, CharsetUtil.charset(charsetName));
    }

    /**
     * 获得请求 header 中的信息
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @param name    头信息的 KEY
     * @param charset 字符集
     * @return header 值
     * @since 4.6.2
     */
    public static String getHeader(HttpServletRequest request, String name, Charset charset) {
        final String header = request.getHeader(name);
        if (null != header) {
            return CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset);
        }
        return null;
    }

    /**
     * 客户浏览器是否为 IE
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 客户浏览器是否为 IE
     */
    public static boolean isIE(HttpServletRequest request) {
        String userAgent = getHeaderIgnoreCase(request, "User-Agent");
        if (StrUtil.isNotBlank(userAgent)) {
            // noinspection ConstantConditions
            userAgent = userAgent.toUpperCase();
            return userAgent.contains("MSIE") || userAgent.contains("TRIDENT");
        }
        return false;
    }

    /**
     * 是否为 GET 请求
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为 GET 请求
     */
    public static boolean isGetMethod(HttpServletRequest request) {
        return METHOD_GET.equalsIgnoreCase(request.getMethod());
    }

    /**
     * 是否为 POST 请求
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为 POST 请求
     */
    public static boolean isPostMethod(HttpServletRequest request) {
        return METHOD_POST.equalsIgnoreCase(request.getMethod());
    }

    /**
     * 具有 body 方法
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为 POST 请求
     */
    public static boolean hasBodyMethod(HttpServletRequest request) {
        return StringUtils.equalsAnyIgnoreCase(request.getMethod(), METHOD_POST, METHOD_PUT, METHOD_POST, METHOD_DELETE);
    }

    /**
     * 是否为 Multipart 类型表单，此类型表单用于文件上传
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为 Multipart 类型表单，此类型表单用于文件上传
     */
    public static boolean isMultipart(HttpServletRequest request) {
        if (!isPostMethod(request)) {
            return false;
        }

        String contentType = request.getContentType();
        if (StrUtil.isBlank(contentType)) {
            return false;
        }
        return contentType.toLowerCase().startsWith("multipart/");
    }
    // --------------------------------------------------------- Header end

    // --------------------------------------------------------- Cookie start

    /**
     * 获得指定的 Cookie
     *
     * @param httpServletRequest {@link HttpServletRequest}
     * @param name               cookie 名
     * @return Cookie 对象
     */
    public static Cookie getCookie(HttpServletRequest httpServletRequest, String name) {
        return readCookieMap(httpServletRequest).get(name);
    }

    /**
     * 将 cookie 封装到 Map 里面
     *
     * @param httpServletRequest {@link HttpServletRequest}
     * @return Cookie map
     */
    public static Map<String, Cookie> readCookieMap(HttpServletRequest httpServletRequest) {
        final Cookie[] cookies = httpServletRequest.getCookies();
        if (ArrayUtil.isEmpty(cookies)) {
            return MapUtil.empty();
        }

        return IterUtil.toMap(new ArrayIter<>(httpServletRequest.getCookies()), new CaseInsensitiveMap<>(), Cookie::getName);
    }

    /**
     * 设定返回给客户端的 Cookie
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param cookie   Servlet Cookie 对象
     */
    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    /**
     * 设定返回给客户端的 Cookie
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param name     Cookie 名
     * @param value    Cookie 值
     */
    public static void addCookie(HttpServletResponse response, String name, String value) {
        response.addCookie(new Cookie(name, value));
    }

    /**
     * 设定返回给客户端的 Cookie
     *
     * @param response        响应对象{@link HttpServletResponse}
     * @param name            cookie 名
     * @param value           cookie 值
     * @param maxAgeInSeconds -1: 关闭浏览器清除 Cookie. 0: 立即清除 Cookie. &gt;0 : Cookie 存在的秒数。
     * @param path            Cookie 的有效路径
     * @param domain          the domain name within which this cookie is visible; form is according to RFC 2109
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds, String path, String domain) {
        Cookie cookie = new Cookie(name, value);
        // 获取 cookie 配置
        CookieProperties cookieProperties = SpringUtils.getBean(CookieProperties.class);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        } else if (StringUtils.isNotBlank(cookieProperties.getDomain())) {
            cookie.setDomain(cookieProperties.getDomain());
        }
        if (Objects.nonNull(cookieProperties.getSameSite())) {
            cookie.setAttribute("SameSite", cookieProperties.getSameSite().attributeValue());
        }
        if (Objects.nonNull(cookieProperties.getSecure())) {
            cookie.setSecure(cookieProperties.getSecure());
        }
        if (Objects.nonNull(cookieProperties.getHttpOnly())) {
            cookie.setHttpOnly(cookieProperties.getHttpOnly());
        }
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        addCookie(response, cookie);
    }

    /**
     * 设定返回给客户端的 Cookie<br>
     * Path: "/"<br>
     * No Domain
     *
     * @param response        响应对象{@link HttpServletResponse}
     * @param name            cookie 名
     * @param value           cookie 值
     * @param maxAgeInSeconds -1: 关闭浏览器清除 Cookie. 0: 立即清除 Cookie. &gt;0 : Cookie 存在的秒数。
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        addCookie(response, name, value, maxAgeInSeconds, "/", null);
    }

    // --------------------------------------------------------- Cookie end
    // --------------------------------------------------------- Response start

    /**
     * 获得 PrintWriter
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @return 获得 PrintWriter
     * @throws IORuntimeException IO 异常
     */
    public static PrintWriter getWriter(HttpServletResponse response) throws IORuntimeException {
        try {
            return response.getWriter();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response    响应对象{@link HttpServletResponse}
     * @param text        返回的内容
     * @param contentType 返回的类型
     */
    public static void write(HttpServletResponse response, String text, String contentType) {
        response.setContentType(contentType);
        Writer writer = null;
        try {
            writer = response.getWriter();
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new UtilException(e);
        } finally {
            IoUtil.close(writer);
        }
    }

    /**
     * 返回文件给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param file     写出的文件对象
     * @since 4.1.15
     */
    public static void write(HttpServletResponse response, File file) {
        final String fileName = file.getName();
        final String contentType = ObjectUtil.defaultIfNull(FileUtil.getMimeType(fileName), "application/octet-stream");
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            write(response, in, contentType, fileName);
        } finally {
            IoUtil.close(in);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response    响应对象{@link HttpServletResponse}
     * @param in          需要返回客户端的内容
     * @param contentType 返回的类型，可以使用{@link FileUtil#getMimeType(String)}获取对应扩展名的 MIME 信息
     *                    <ul>
     *                      <li>application/pdf</li>
     *                      <li>application/vnd.ms-excel</li>
     *                      <li>application/msword</li>
     *                      <li>application/vnd.ms-powerpoint</li>
     *                    </ul>
     *                    docx、xlsx 这种 office 2007 格式 设置 MIME;网页里面 docx 文件是没问题，但是下载下来了之后就变成 doc 格式了
     *                    参考：https://my.oschina.net/shixiaobao17145/blog/32489
     *                    <ul>
     *                      <li>MIME_EXCELX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";</li>
     *                      <li>MIME_PPTX_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";</li>
     *                      <li>MIME_WORDX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";</li>
     *                      <li>MIME_STREAM_TYPE = "application/octet-stream;charset=utf-8"; #原始字节流</li>
     *                    </ul>
     * @param fileName    文件名，自动添加双引号
     * @since 4.1.15
     */
    public static void write(HttpServletResponse response, InputStream in, String contentType, String fileName) {
        final String charset = ObjectUtil.defaultIfNull(response.getCharacterEncoding(), CharsetUtil.UTF_8);
        response.setHeader("Content-Disposition", StrUtil.format("attachment;filename=\"{}\"", URLUtil.encode(fileName, CharsetUtil.charset(charset))));
        response.setContentType(contentType);
        write(response, in);
    }

    /**
     * 返回数据给客户端
     *
     * @param response    响应对象{@link HttpServletResponse}
     * @param in          需要返回客户端的内容
     * @param contentType 返回的类型
     */
    public static void write(HttpServletResponse response, InputStream in, String contentType) {
        response.setContentType(contentType);
        write(response, in);
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param in       需要返回客户端的内容
     */
    public static void write(HttpServletResponse response, InputStream in) {
        write(response, in, IoUtil.DEFAULT_BUFFER_SIZE);
    }

    /**
     * 返回数据给客户端
     *
     * @param response   响应对象{@link HttpServletResponse}
     * @param in         需要返回客户端的内容
     * @param bufferSize 缓存大小
     */
    public static void write(HttpServletResponse response, InputStream in, int bufferSize) {
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            IoUtil.copy(in, out, bufferSize);
        } catch (IOException e) {
            throw new UtilException(e);
        } finally {
            IoUtil.close(out);
            IoUtil.close(in);
        }
    }

    /**
     * 设置响应的 Header
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param name     名
     * @param value    值，可以是 String，Date，int
     */
    public static void setHeader(HttpServletResponse response, String name, Object value) {
        if (value instanceof String) {
            response.setHeader(name, (String) value);
        } else if (Date.class.isAssignableFrom(value.getClass())) {
            response.setDateHeader(name, ((Date) value).getTime());
        } else if (value instanceof Integer || "int".equalsIgnoreCase(value.getClass().getSimpleName())) {
            response.setIntHeader(name, (int) value);
        } else {
            response.setHeader(name, value.toString());
        }
    }
    // --------------------------------------------------------- Response end

}