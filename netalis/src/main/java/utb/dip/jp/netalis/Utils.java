package utb.dip.jp.netalis;

import android.graphics.Color;

import java.util.UUID;

/**
 * ユーティリティ
 */
public class Utils {

    public static class Config {
        public static int EXPIRE_DAYS = 30;
    }

    /** タスクの色を表す構造体 */
    public static class TaskColor {
        public String colorDBValue;
        public int taskColor;
        public int background;
        public TaskColor(String taskColor, String background) {
            this.colorDBValue = taskColor;
            this.taskColor = Color.parseColor(taskColor);
            this.background = Color.parseColor(background);
        }
    }

    /** タスクの色 */
    public static TaskColor[] taskColors = new TaskColor[] {
        new TaskColor("#cccccc", "#ffffff"),
        new TaskColor("#ff6666", "#ffcccc"),
        new TaskColor("#ff9966", "#ffccaa"),
        new TaskColor("#ffcc66", "#ffffcc"),
        new TaskColor("#66aa66", "#aaccaa"),
        new TaskColor("#66aaff", "#aaccff"),
        new TaskColor("#6666ff", "#ccccff"),
        new TaskColor("#aa66ff", "#ccaaff"),
        new TaskColor("#333333", "#aaaaaa"),
    };

    /**
     * タスクの色を返す。
     * @param color 色を表す文字列（#rrggbb)
     * @return TaskColorクラス
     */
    public static TaskColor taskColor(String color) {
        for (TaskColor c : taskColors) {
            if (c.colorDBValue.equals(color)) {
                return c;
            }
        }
        return taskColors[0];
    }


    /** STATUS列挙体 */
    public static enum STATUS {
        TODO(1), DONE(2), CANCEL(3), OTHER(0);
        public final int intValue;
        public final int position;
        public final String dbValue;
        STATUS(int i) {
            intValue = i;
            position = i -1;
            dbValue = String.valueOf(i);
        }
        public static STATUS valueOf(int i) {
            for (STATUS s : STATUS.values()) {
                if (s.intValue == i) {
                    return s;
                }
            }
            return STATUS.OTHER;
        }
    }

    /**
     * valueがnullならotherwiseを返す。
     * @param value value
     * @param otherwise valueがnullの時の値
     * @param <T> 任意の型
     * @return valueかotherwise
     */
    public static <T> T nvl(T value, T otherwise) {
        return (value != null) ? value : otherwise;
    }

    /**
     * nullを考慮したequals。
     * @param a 比較対象
     * @param b 比較対象
     * @return 等しければtrue。
     */
    public static boolean eq(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    /**
     * 文字列がnullか空ならtrueを返す。
     * @param s 対象の文字列
     * @return nullか空ならtrue。
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0 || s.trim().length() == 0);
    }

    /**
     * JSONの文字値を作成する。（「"」だけエスケープして「"」で囲う。）<br/>
     * 「abc"efg"xyz」→「"abc\"efg\"xyz"」<br/>
     * null→「null」
     * @param s 対象の文字列
     * @return JSONの文字値。
     */
    public static String ezJsonStr(String s) {
        if (s == null) {
            return "null";
        }
        s = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + s + "\"";
    }

    /**
     * UUIDの文字列を返す。引数がUUIDの形式出ない場合はnullを返す。
     * @param uuid UUID
     * @return UUIDの文字列かnull。
     */
    public static String uuidOrNull(String uuid) {
        if (isEmpty(uuid)) {
            return null;
        }
        try {
            return UUID.fromString(uuid).toString();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
