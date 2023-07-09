package ext.library.ipo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数美化 IPO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamFormatIPO {

    /**
     * 键
     */
    String key;
    /**
     * 类型
     */
    Class<?> clazz;

}