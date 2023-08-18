package ext.library.ipo;

public class GeoIPO {
    /**
     * IP 地址可能所在的指定位置周围的半径（以公里为单位）
     */
    private int accuracy;
    /**
     * 区号
     */
    private String areaCode;
    /**
     * IP 注册到的组织（注意：当此字段未知时返回）Unknown
     */
    private String continentCode;
    /**
     * 英文国家名
     */
    private String country;
    /**
     * 两个字母的国家代码
     */
    private String countryCode;
    /**
     * 三个字母的国家代码
     */
    private String countryCode3;
    /**
     * 请求的 IP
     */
    private String ip;
    /**
     * IP 纬度（注意：由于历史原因，这是一个字符串）
     */
    private String latitude;
    /**
     * IP 经度（注意：由于历史原因，这是一个字符串）
     */
    private String longitude;

}