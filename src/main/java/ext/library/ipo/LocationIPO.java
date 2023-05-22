package ext.library.ipo;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 位置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationIPO {

    /**
     * 经度
     */
    double lng;
    /**
     * 纬度
     */
    double lat;

    /**
     * 将经纬度参数转换为位置对象
     * <p>
     * {@linkplain JSONObject} 转 {@linkplain LocationIPO}
     *
     * @param location 标准的经纬度 JSON 对象，包含的 key 有 ("lng", "lat")
     * @return 经纬度对象
     */
    public static LocationIPO toLocationIPO(JSONObject location) {
        double lng = location.getDouble("lng");
        double lat = location.getDouble("lat");
        return LocationIPO.builder().lng(lng).lat(lat).build();
    }

}