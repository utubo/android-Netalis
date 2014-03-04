package utb.dip.jp.netalis;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import utb.dip.jp.netalis.Utils.STATUS;

public class DBAdapter {

    static final String DATABASE_NAME = "netalis.db";
    static final int DATABASE_VERSION = 5;

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    //
    // SQLiteOpenHelper
    //

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                "CREATE TABLE tasks (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "task TEXT NOT NULL," +
                    "status INTEGER NOT NULL," +
                    "color TEXT, " +
                    "lastupdate TEXT NOT NULL);"
            );
            // swipe right: move to 'DONE' tab.
            // swipe left: move to 'CANCEL' tab.
            // swipe left: move to 'TO-DO' tab.
            // swipe right: move to 'CANCEL' tab.
            // 'CANCEL' tab keep tasks only 30 days.
            // swipe right: remove task.
            // swipe left: move to 'DONE' tab.
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tasks");
            onCreate(db);
        }

    }

    //
    // Adapter Methods
    //

    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }


    public void close(){
        dbHelper.close();
    }


    //
    // App Methods
    //

    /**
     * タスク削除
     * @param task  タスク
     * @return 削除件数
     */
    public int deleteTask(Task task) {
        return db.delete("tasks", "_id=?", strings(task._id));
    }

    /**
     * キャンセルにある30日以上のタスクを削除。
     */
    public int deleteCanceledTask() {
        return db.delete(
                "tasks",
                "status = ? " +
                "and lastupdate < ? ",
                new String[] {
                    STATUS.CANCEL.dbValue,
                    MyDate.now().addDays(- Utils.Config.EXPIRE_DAYS).format()
                }
        );
    }

    String[] TASKS_COLUMNS = new String[]{"_id", "task", "status", "color", "lastupdate"};

    /**
     * 全タスクリスト取得
     * @return Taskのリスト
     */
    public List<Task> selectAllTasks() {
        Cursor cursor = db.query(
                "tasks",
                TASKS_COLUMNS,
                null,
                null,
                null, // group
                null, // having
                "status, lastupdate desc" // oder by
        );
        return selectTasks(cursor);
    }

    /**
     * タスクリスト取得
     * @param status ステータス
     * @return Taskのリスト
     */
    public List<Task> selectTasks(STATUS status) {
        Cursor cursor = db.query(
                "tasks",
                TASKS_COLUMNS,
                "status=?",
                new String[]{status.dbValue},
                null, // group
                null, // having
                "lastupdate desc" // oder by
        );
        return selectTasks(cursor);
    }

    /**
     * タスクリスト取得
     * @param cursor DBカーソル
     * @return Taskのリスト
     */
    public List<Task> selectTasks(Cursor cursor) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task._id = cursor.getInt(0);
                task.task = cursor.getString(1);
                task.status = cursor.getInt(2);
                task.color = cursor.getString(3);
                task.lastupdate = cursor.getString(4);
                tasks.add(task);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return tasks;
    }

    public boolean saveTask(Task task) {
        task.lastupdate = MyDate.now().format();
        return saveTaskWithoutLastupdateTimestamp(task);
    }
    /**
     * タスクの追加/更新
     * @param task タスク
     */
    public boolean saveTaskWithoutLastupdateTimestamp(Task task) {
        ContentValues values = new ContentValues();
        values.put("task", task.task);
        values.put("status", task.status);
        values.put("color", task.color);
        values.put("lastupdate", task.lastupdate);
        if (task._id < 0) {
            db.insertOrThrow("tasks", null, values);
            Cursor cursor = db.query(
                    "sqlite_sequence",
                    new String[]{"seq"},
                    "name = ?",
                    new String[]{"tasks"},
                    null,
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                task._id = cursor.getLong(cursor.getColumnIndex("seq"));
                return true;
            }
        } else {
            return db.update("tasks", values, "_id=?", strings(task._id)) > 0;
        }
        return false;
   }

    /**
     * ObjectのリストをStringの配列にして返す。
     * @param values 値
     * @return Stringの配列
     */
    protected String[] strings(Object... values) {
        List<String> list = new ArrayList<String>();
        for (Object value : values) {
            list.add(String.valueOf(value));
        }
        return list.toArray(new String[list.size()]);
    }

}