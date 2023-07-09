package ext.library.util;

import cn.hutool.core.net.NetUtil;

/**
 * 网络相关工具
 */
public class NetUtils extends NetUtil {

    final static String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 判定是否为内网 IP<br>
     * 私有 IP：A 类 10.0.0.0-10.255.255.255 B 类 172.16.0.0-172.31.255.255 C 类 192.168.0.0-192.168.255.255 当然，还有 127 这个网段是环回地址
     *
     * @return 是否为内网 IP
     */
    public static Boolean isInnerIp() {
        // 1. 获取客户端 IP 地址，考虑反向代理的问题
        String ip = ServletUtils.getClientIP(ServletUtils.getRequest());

        // 2. 确认是否为内网 IP
        if (ip.equals(LOCAL_IPV6)) {
            return true;
        }
        return isInnerIP(ip);
    }

}