package ext.library.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * <b>I18n（国际化）</b>
 * <p>资源包定义示例：</p>
 * <pre class="code">
 *     messages.properties
 *     messages_zh_CN.properties
 *     messages_en.properties
 * </pre>
 */
@Slf4j
@Component
public class I18nUtils {

    static MessageSource messageSource;
    static ResourceBundleMessageSource messageSourceExt;

    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
        I18nUtils.messageSourceExt = new ResourceBundleMessageSource();
        I18nUtils.messageSourceExt.setBasenames("ExtMessages", "messages");
        I18nUtils.messageSourceExt.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    /**
     * 获取单个国际化翻译值
     *
     * @param code 资源包中定义的 key
     * @param args 资源包中定义的{value}占位符参数，顺序匹配（可空参数）
     * @return 动态匹配资源包中定义的翻译 value
     */
    public static String get(String code, @Nullable Object... args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return code;
        }
    }

    /**
     * 获取 ext-library 内置的单个国际化翻译值
     */
    public static String getExt(String msgKey) {
        try {
            return messageSourceExt.getMessage(msgKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return msgKey;
        }
    }

}