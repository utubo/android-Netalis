package utb.dip.jp.netalis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日時ユーティリティ
 */
public class MyDate extends Date {
    public static MyDate dummyDate = null;
    public static MyDate now() {
        return U.nvl(dummyDate, new MyDate());
    }

    /** "yyyy/MM/dd HH:mm:ss"でフォーマット */
    public String format() {
        return format("yyyy/MM/dd HH:mm:ss");
    }

    /**
     * SimpleDateFormatのエイリアス
     * @param format フォーマット
     * @return フォーマットされた値
     */
    public String format(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(this);
    }

    public MyDate addDays(int days) {
        MyDate ret = new MyDate();
        ret.setTime(this.getTime() + days * 24 * 60 * 60 * 10000);
        return ret;
    }
}
