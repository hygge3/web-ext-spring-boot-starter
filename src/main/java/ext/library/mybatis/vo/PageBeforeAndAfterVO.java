package ext.library.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页上下条数据 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageBeforeAndAfterVO {

    /** 上一条数据 ID */
    Long beforeId;
    /** 下一条数据 ID */
    Long afterId;

}