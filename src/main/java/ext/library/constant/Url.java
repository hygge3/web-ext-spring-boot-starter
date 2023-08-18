package ext.library.constant;

/**
 * url
 */
public interface Url {
    /**
     * get.geojs.io 基本 url
     */
    String IP_BASE_URL = "https://get.geojs.io/v1";
    /**
     * 获取地理信息
     */
    String IP_GEO = IP_BASE_URL + "/ip/geo.json";
    /**
     * 获取国家信息
     */
    String IP_COUNTRY = IP_BASE_URL + "/ip/country.json";
    /**
     * 获取 ip
     */
    String IP = IP_BASE_URL + "/ip";

}