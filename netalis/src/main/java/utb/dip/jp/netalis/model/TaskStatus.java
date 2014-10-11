package utb.dip.jp.netalis.model;

import android.app.Activity;

import utb.dip.jp.netalis.R;

/** STATUS列挙体 */
public enum TaskStatus {
    TODO(   1, R.string.title_section1, R.string.info_section1),
    DONE(   2, R.string.title_section2,  R.string.info_section2),
    CANCEL( 3, R.string.title_section3,  R.string.info_section3),
    REMOVE(  0, R.string.empty_string,   R.string.empty_string);
    public final int intValue;
    public final int position;
    public final int titleId;
    public final int infoId;
    public final String dbValue;
    TaskStatus(int i, int title, int info) {
        intValue = i;
        position = i - 1;
        titleId = title;
        infoId = info;
        dbValue = String.valueOf(i);
    }
    public static TaskStatus valueOf(int i) {
        for (TaskStatus s : TaskStatus.values()) {
            if (s.intValue == i) {
                return s;
            }
        }
        return TaskStatus.REMOVE;
    }

    public static TaskStatus positionOf(int position) {
        for (TaskStatus s : TaskStatus.values()) {
            if (s.position == position) {
                return s;
            }
        }
        return TaskStatus.REMOVE;
    }

    public int getIcon(Activity a) {
        switch (this) {
            case TODO: return a.getResources().getIdentifier("@*android:drawable/ic_menu_home", null, a.getPackageName());
            case DONE: return a.getResources().getIdentifier("@*android:drawable/ic_menu_mark", null, a.getPackageName());
            case CANCEL: return android.R.drawable.ic_menu_delete;
            case REMOVE: return android.R.drawable.ic_delete;
            default: return 0;
        }
    }

    public TaskStatus next() {
        switch (this) {
            case TODO: return DONE;
            case DONE: return CANCEL;
            case CANCEL: return REMOVE;
            default: return TODO;
        }
    }

    public TaskStatus prev() {
        switch (this) {
            case TODO: return CANCEL;
            case DONE: return TODO;
            case CANCEL: return DONE;
            default: return TODO;
        }
    }
}
