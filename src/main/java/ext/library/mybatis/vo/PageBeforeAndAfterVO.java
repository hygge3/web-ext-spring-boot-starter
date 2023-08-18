package ext.library.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页上下条数据 VO
 * <p>
 * T - 主键类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageBeforeAndAfterVO<T> {

    /**
     * 上一条数据 ID
     */
    T beforeId;
    /**
     * 下一条数据 ID
     */
    T afterId;

}