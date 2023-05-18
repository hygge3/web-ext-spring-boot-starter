package ext.library.mybatis.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <h2>RESTful 驼峰命名法基础实体</h2><br>
 *
 * <b><code style="color:red">注意子类使用 Lombok 重写 toString() 与 equals() 和 hashCode() 方法时，callSuper 属性需为 true，如下：</code></b>
 * <blockquote>
 * <p>&#064;ToString(callSuper = true)
 * <p>&#064;EqualsAndHashCode(callSuper = true)
 * </blockquote><br>
 *
 * <b><code style="color:red">注意子类使用 Lombok 生成 builder() 方法时，需使用@SuperBuilder 注解，而非@Builder 注解，如下：</code></b>
 * <blockquote>
 * <p>&#064;NoArgsConstructor
 * <p>&#064;AllArgsConstructor
 * <p>&#064;SuperBuilder(toBuilder = true)
 * </blockquote><br>
 *
 * <a href="https://ylyue.cn/#/data/jdbc/DO 基类">👉点击查看关于 DO 基类的详细使用介绍</a>
 */
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseEntity implements Serializable {

    static final long serialVersionUID = 2241197545628586478L;

    /**
     * 有序主键：单表时数据库自增、分布式时雪花自增
     */
    @Id(keyType = KeyType.Auto)
    protected Long id;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    protected LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onUpdateValue = "now()")
    protected LocalDateTime updateTime;

    /**
     * 删除时间：默认 0（未删除）
     * <p>一般不作查询展示
     */
    @Column(isLogicDelete = true)
    protected Long deleteTime;

}