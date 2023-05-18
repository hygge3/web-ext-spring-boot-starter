package ext.library.util;

import cn.hutool.core.util.ObjectUtil;

/**
 * Sql 拼接
 */
public class Sql {

    private final StringBuffer sql = new StringBuffer();

    private Sql(CharSequence initialSql) {
        sql.append(initialSql);
    }

    /**
     * Sql 拼接
     *
     * @return Sql 拼接
     */
    public static Sql sql() {
        return new Sql("");
    }

    /**
     * Sql 拼接
     *
     * @param appendSql 需要拼接的 Sql 字符串
     * @return Sql 拼接
     */
    public static Sql sql(CharSequence appendSql) {
        return new Sql(appendSql);
    }

    /**
     * 拼接 Sql
     *
     * @param originalSql 原始 Sql
     * @param appendSql   需要追加的 Sql 字符串
     * @return 拼接的 Sql
     */
    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql) {
        return originalSql.append(appendSql);
    }

    /**
     * 拼接 Sql
     *
     * @param originalSql 原始 Sql
     * @param appendSql   需要追加的 Sql 字符串
     * @param expression  条件表达式（true 拼接，false 不拼接）
     * @return 拼接的 Sql
     */
    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql, boolean expression) {
        if (expression) {
            return originalSql.append(appendSql);
        }

        return originalSql;
    }

    /**
     * 拼接 Sql
     *
     * @param originalSql 原始 Sql
     * @param appendSql   需要追加的 Sql 字符串
     * @param isNotEmpty  判空对象，支持：CharSequence、Map、Iterable、Iterator、Array
     * @return 拼接的 Sql
     */
    public static StringBuffer append(StringBuffer originalSql, CharSequence appendSql, Object isNotEmpty) {
        return append(originalSql, appendSql, ObjectUtil.isNotEmpty(isNotEmpty));
    }

    /**
     * 拼接 Sql
     *
     * @param appendSql 需要追加的 Sql 字符串
     * @return Sql 拼接
     */
    public Sql append(CharSequence appendSql) {
        sql.append(appendSql);
        return this;
    }

    /**
     * 拼接 Sql
     *
     * @param appendSql  需要追加的 Sql 字符串
     * @param expression 条件表达式（true 拼接，false 不拼接）
     * @return Sql 拼接
     */
    public Sql append(CharSequence appendSql, boolean expression) {
        if (expression) {
            return append(appendSql);
        }

        return this;
    }

    /**
     * 拼接 Sql
     *
     * @param appendSql  需要追加的 Sql 字符串
     * @param isNotEmpty 判空对象，支持：CharSequence、Map、Iterable、Iterator、Array
     * @return Sql 拼接
     */
    public Sql append(CharSequence appendSql, Object isNotEmpty) {
        return append(appendSql, ObjectUtil.isNotEmpty(isNotEmpty));
    }

    /**
     * 获得 Sql
     */
    public StringBuffer getSql() {
        return sql;
    }

    /**
     * 获得 Sql 字符串
     */
    public String getSqlString() {
        return sql.toString();
    }

    /**
     * 获得 Sql 字符串
     */
    @Override
    public String toString() {
        return getSqlString();
    }

}