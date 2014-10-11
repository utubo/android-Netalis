package utb.dip.jp.netalis.model;

import android.graphics.Color;

import java.util.LinkedHashMap;
import java.util.Map;

/** タスクの色を表す構造体 */
public class TaskColor {

    /** 色のキャッシュ */
    public static Map<String, TaskColor> taskColorHashMap = new LinkedHashMap<String, TaskColor>();
    /** デフォルトの色リストをキャッシュ */
    static {
        TaskColor.taskColor("#cccccc");
        TaskColor.taskColor("#ff8888");
        TaskColor.taskColor("#ff6666");
        TaskColor.taskColor("#ff9966");
        TaskColor.taskColor("#ffcc66");
        TaskColor.taskColor("#00dd66");
        TaskColor.taskColor("#66aaff");
        TaskColor.taskColor("#6666ff");
        TaskColor.taskColor("#aa66ff");
        TaskColor.taskColor("#333333");
    }

    public String colorDBValue;
    public int taskColor;
    public int textColor;
    public boolean isParseError = false;

    /**
     * コンストラクタ
     * @param color 色
     */
    public TaskColor(String color) {
        this.colorDBValue = color;
        try {
            this.taskColor = Color.parseColor(color);
        } catch (Exception e) {
            isParseError = true;
            this.taskColor = Color.parseColor("#cccccc");
        }
        this.textColor = Color.parseColor(isBrightColor(taskColor) ? "#555555" : "#ffffff");
    }

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

    /**
     * 明るい色ならtrue。
     * @param color 判定する色
     * @return 明るい色ならtrue。
     */
    private boolean isBrightColor(int color) {
        return Color.blue(color) + Color.green(color) + Color.red(color) > 12 * 16 * 3;
    }

}
