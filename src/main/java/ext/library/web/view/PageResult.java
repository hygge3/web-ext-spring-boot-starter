package ext.library.web.view;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.IPage;
import com.github.pagehelper.PageInfo;
import com.mybatisflex.core.paginate.Page;
import ext.library.convert.Convert;
import ext.library.mybatis.entity.BaseEntity;
import ext.library.util.ListUtils;
import ext.library.util.SpringUtils;
import io.github.linpeilie.Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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
public class PageResult<T> implements IPage {
    /**
     * 当前页
     */
    Integer pageNum;
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
    Integer totalPages;
    /**
     * 排序字段
     */
    String orderBy;
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
        pageResult.setPageNum(page.getPageNumber());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setTotalRows(page.getTotalRow());
        pageResult.setTotalPages(Math.toIntExact(page.getTotalPage()));
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }

    /**
     * PageHelper 分页对象转应用分页对象
     *
     * @param pageInfo 页面信息
     * @return {@link PageResult}<{@link T}>
     */
    public static <T> PageResult<T> of(@NonNull PageInfo<T> pageInfo) {
        PageResult<T> pageResult = new PageResult<>();
        if (pageInfo.getTotal() == 0) {
            return pageResult.empty();
        }
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setTotalRows(pageInfo.getTotal());
        pageResult.setTotalPages(pageInfo.getPages());
        pageResult.setRecords(pageInfo.getList());
        return pageResult;
    }

    /**
     * 应用分页对象转换 ORM 分页对象
     *
     * @return {@link Page}<{@link T}>
     */
    @SuppressWarnings("rawtypes")
    public Page toPage() {
        Page<T> page = new Page<>();
        if (Objects.nonNull(pageNum)) {
            page.setPageNumber(pageNum);
        }
        if (Objects.nonNull(pageSize)) {
            page.setPageNumber(pageSize);
        }
        if (Objects.nonNull(totalRows)) {
            page.setTotalRow(totalRows);
        }
        return page;
    }

    /**
     * 空结果
     *
     * @return {@link PageResult}<{@link T}>
     */
    public PageResult<T> empty() {
        totalRows = 0L;
        totalPages = 0;
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
        List<E> list = CollUtil.newArrayList();
        if (ListUtils.isEmpty(records)) {
            return replace(list);

        }
        try {
            for (T record : records) {
                if (BaseEntity.class.isAssignableFrom(clazz)) {
                    Converter converter = SpringUtils.getBean(Converter.class);
                    list.add(converter.convert(record, clazz));
                } else {
                    list.add(Convert.convert(record, clazz));
                }
            }
        } catch (Exception e) {
            list = Convert.toList(clazz, records);
        }
        return replace(list);
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
