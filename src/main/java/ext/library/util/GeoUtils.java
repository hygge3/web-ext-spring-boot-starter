package ext.library.util;

import ext.library.constant.Url;
import ext.library.ipo.GeoIPO;

public class GeoUtils {


    public static GeoIPO getGeoByIp(String ip) {
        return HttpUtils.get(Url.IP_GEO + "/" + ip, GeoIPO.class);

    }

    public static GeoIPO getCurrentGeo() {
        return HttpUtils.get(Url.IP_GEO, GeoIPO.class);
    }
}