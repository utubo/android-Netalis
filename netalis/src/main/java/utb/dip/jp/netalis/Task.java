package utb.dip.jp.netalis;

/**
 * 日付関連ユーティリティ
 * Created by utb on 14/03/02.
 */
public class Task {
    public long _id = -1;
    public String task = "";
    public int status = Utils.STATUS.TODO.intValue;
    public String color = null;
    public String lastupdate = null;
}
