package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * <h3>Db 异常</h3><br>
 */
@NoArgsConstructor
public class DbException extends RuntimeException {

    static final long serialVersionUID = 5869945193750586067L;

    /**
     * 统一异常处理后是否显示异常提示
     */
    boolean showMsg = false;

    public DbException(String msg) {
        super(msg);
    }

    /**
     * DB 异常
     *
     * @param msg     异常提示
     * @param showMsg 统一异常处理后是否显示异常提示
     */
    public DbException(String msg, boolean showMsg) {
        super(msg);
        this.showMsg = showMsg;
    }

    /**
     * 统一异常处理后是否显示异常提示
     *
     * @return 是否显示
     */
    public boolean isShowMsg() {
        return showMsg;
    }

    /**
     * 设置统一异常处理后是否显示异常提示
     *
     * @param showMsg 是否显示
     */
    public void setShowMsg(boolean showMsg) {
        this.showMsg = showMsg;
    }

}