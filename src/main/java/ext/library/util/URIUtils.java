package ext.library.util;

import org.springframework.web.util.UriUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * URI 处理
 */
public class URIUtils {

    /**
     * The default encoding for URI encode/decode: <kbd>UTF-8</kbd>.
     */
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * URI 前缀或后缀通配符匹配（只能同时存在一种通配）
     *
     * @param array 包含通配符的 URI 的数组
     * @param uri   实际的 URI
     * @return 是否匹配
     */
    public static boolean isUriArraySuffixOrPrefixWildcard(String[] array, String uri) {
        for (String url : array) {
            // 1. 前通配
            if (!url.endsWith("**")) {
                if (uri.startsWith("/")) {
                    url = url.substring(2);
                } else {
                    url = url.substring(3);
                }
                if (uri.endsWith(url)) {
                    return true;
                }
            } else {
                // 2. 后通配
                url = url.substring(0, url.length() - 3);
                if (uri.startsWith(url)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * URI 编码
     *
     * @param source 要编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String source) {
        return UriUtils.encode(source, DEFAULT_ENCODING);
    }

    /**
     * URI 解码
     *
     * @param source 要解码的字符串
     * @return 解码后的字符串
     */
    public static String decode(String source) {
        return UriUtils.decode(source, DEFAULT_ENCODING);
    }

}