package ext.library.util;

/**
 * 线程信息获取工具类
 */
public class CurrentLineInfo {

    private static final int ORIGIN_STACK_INDEX = 2;

    public static String getFileName() {
        return Thread.currentThread().getStackTrace()[ORIGIN_STACK_INDEX].getFileName();
    }

    /**
     * 得到当前线程所在的类名称
     *
     * @return 类名称
     */
    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[ORIGIN_STACK_INDEX].getClassName();
    }

    /**
     * 得到当前线程所在的方法名称
     *
     * @return 方法名称
     */
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[ORIGIN_STACK_INDEX].getMethodName();
    }

    /**
     * 得到当前线程在第几行
     *
     * @return 第几行
     */
    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[ORIGIN_STACK_INDEX].getLineNumber();
    }

}