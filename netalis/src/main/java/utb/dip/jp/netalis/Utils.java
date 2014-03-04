package utb.dip.jp.netalis;

import android.graphics.Color;

/**
 * Created by utb on 14/03/02.
 */
public class Utils {

    public static class Config {
        public static int EXPIRE_DAYS = 30;
    }

    /** colors */
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

    public static TaskColor taskColor(String color) {
        for (TaskColor c : taskColors) {
            if (c.colorDBValue.equals(color)) {
                return c;
            }
        }
        return taskColors[0];
    }


    /** Status */
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
        public static STATUS atPosition(int p) {
            for (STATUS s : STATUS.values()) {
                if (s.position == p) {
                    return s;
                }
            }
            return STATUS.TODO;
        }
    }

    public static <T> T nvl(T value, T otherwise) {
        return (value != null) ? value : otherwise;
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0 || s.trim().length() == 0);
    }

    public static boolean eq(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
}
