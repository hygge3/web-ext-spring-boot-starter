package ext.library.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用上下文工具类，用于在普通类中获取 Spring IOC 容器中的 bean 对象，包括：
 *
 * <pre>
 *     1、Spring IOC 容器中的 bean 对象获取
 *     2、注册和注销 Bean
 * </pre>
 */
@Slf4j
public class SpringUtils extends SpringUtil {}