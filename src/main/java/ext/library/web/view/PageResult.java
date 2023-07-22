package ext.library.web.view;

import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.paginate.Page;
import ext.library.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 分页结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 当前页
     */
    Long pageNum;
    /**
     * 每页显示条数
     */
    Integer pageSize;
    /**
     * 总条数
     */
    Long totalRows;
    /**
     * 总页数
     */
    Long totalPages;
    /**
     * 结果集
     */
    List<T> records;

    /**
     * ORM 分页对象转应用分页对象
     *
     * @param page ORM 分页对象
     * @return {@link PageResult}<{@link T}>
     */
    public static <T> PageResult<T> of(@NonNull Page<T> page) {
        PageResult<T> pageResult = new PageResult<>();
        if (page.getTotalRow() == 0) {
            return pageResult.empty();
        }
        pageResult.setPageNum((long) page.getPageNumber());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setTotalRows(page.getTotalRow());
        pageResult.setTotalPages(page.getTotalPage());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }

    /**
     * 应用分页对象转换 ORM 分页对象
     *
     * @return {@link Page}<{@link T}>
     */
    public Page<T> toPage() {
        if (Objects.nonNull(this.getTotalRows())) {
            return Page.of(Math.toIntExact(this.getPageNum()), this.getPageSize()
                    .byteValue(), this.getTotalRows());
        }
        return Page.of(Math.toIntExact(this.getPageNum()), this.getPageSize());
    }

    /**
     * 空结果
     *
     * @return {@link PageResult}<{@link T}>
     */
    public PageResult<T> empty() {
        totalRows = 0L;
        totalPages = 0L;
        records = Collections.emptyList();
        return this;
    }

    /**
     * 结果集转换为指定类型
     *
     * @param clazz clazz
     * @return {@link PageResult}<{@link T}>
     */
    public <E> PageResult<E> convert(Class<E> clazz) {
        return replace(Convert.toList(clazz, records));
    }

    /**
     * 结果集替换为指定结果集
     *
     * @param results 结果
     * @return {@link PageResult}<{@link E}>
     */
    public <E> PageResult<E> replace(List<E> results) {
        PageResult<E> pageResult = new PageResult<>();
        pageResult.setPageNum(this.getPageNum());
        pageResult.setPageSize(this.getPageSize());
        pageResult.setTotalRows(this.getTotalRows());
        pageResult.setTotalPages(this.getTotalPages());
        if (CollUtil.isEmpty(results)) {
            pageResult.setRecords(Collections.emptyList());
        } else {
            pageResult.setRecords(results);
        }
        return pageResult;
    }
}