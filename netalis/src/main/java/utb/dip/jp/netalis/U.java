package utb.dip.jp.netalis;

import android.graphics.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * ユーティリティ
 */
public class U {

    public static Random rnd = new Random();

    public static class Config {
        public static int EXPIRE_DAYS = 30;
    }

    /** タスクの色を表す構造体 */
    public static class TaskColor {
        public String colorDBValue;
        public int taskColor;
        public int textColor;
        public boolean isParseError = false;
        public TaskColor(String color) {
            this.colorDBValue = color;
            try {
                this.taskColor = Color.parseColor(color);
            } catch (Exception e) {
                isParseError = true;
                this.taskColor = Color.parseColor("#cccccc");
            }
            this.textColor = Color.parseColor(isBrightColor(taskColor) ? "#333333" : "#ffffff");
        }
    }

    /**
     * 明るい色ならtrue。
     * @param color 判定する色
     * @return 明るい色ならtrue。
     */
    private static boolean isBrightColor(int color) {
        return Color.blue(color) + Color.green(color) + Color.red(color) > 12 * 16 * 3;
    }

    /** 色のキャッシュ */
    public static Map<String, TaskColor> taskColorHashMap = new LinkedHashMap<String, TaskColor>();

    /**
     * タスクの色を返す。
     * @param color 色を表す文字列（#rrggbb)
     * @return TaskColorクラス
     */
    public static TaskColor taskColor(String color) {
        TaskColor c = taskColorHashMap.get(color);
        if (c == null) {
            c = new TaskColor(color);
            taskColorHashMap.put(color, c);
        }
        return c;
    }

    /** デフォルトタスクの色 */
    static {
        taskColor("#cccccc");
        taskColor("#ff8888");
        taskColor("#ff6666");
        taskColor("#ff9966");
        taskColor("#ffcc66");
        taskColor("#66aa66");
        taskColor("#66aaff");
        taskColor("#6666ff");
        taskColor("#aa66ff");
        taskColor("#333333");
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

    public static <T> int indexOf(T item, T[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (eq(item, array[i])) {
                    return i;
                }

            }
        }
        return -1;
    }
}
