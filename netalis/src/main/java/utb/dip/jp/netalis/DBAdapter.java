package utb.dip.jp.netalis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import utb.dip.jp.netalis.U.STATUS;

public class DBAdapter {

    static final String DATABASE_NAME = "netalis.db";
    static final int DATABASE_VERSION = 6;

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
                    "uuid TEXT NOT NULL, " +
                    "task TEXT NOT NULL," +
                    "status INTEGER NOT NULL," +
                    "color TEXT, " +
                    "lastupdate TEXT NOT NULL);"
            );
            db.execSQL("CREATE INDEX idx_tasks ON tasks(uuid);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 5 : {
                    db.execSQL("ALTER TABLE tasks ADD COLUMN uuid;");
                    db.execSQL("CREATE INDEX idx_tasks ON tasks(uuid);");
                    Cursor cursor = db.rawQuery("SELECT _id from tasks", null);
                    while(cursor.moveToNext()) {
                        long id = cursor.getLong(0);
                        db.execSQL(
                            "UPDATE tasks SET uuid = ? WHERE _id = ?",
                            new String[] {UUID.randomUUID().toString(), String.valueOf(id)}
                        );
                    }
                    break;
                }
            }
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
        return db.delete("tasks", "uuid=?", strings(task.uuid));
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
                    MyDate.now().addDays(- U.Config.EXPIRE_DAYS).format()
                }
        );
    }

    String[] TASKS_COLUMNS = new String[]{"uuid", "task", "status", "color", "lastupdate"};

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
     * @param uuid UUID
     * @return Taskのリスト
     */
    public Task selectTask(String uuid) {
        Cursor cursor = db.query(
                "tasks",
                TASKS_COLUMNS,
                "uuid=?",
                new String[]{uuid},
                null, // group
                null, // having
                null // oder by
        );
        List<Task> list = selectTasks(cursor);
        return list.size() == 0 ? null : list.get(0);
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
                task.uuid = cursor.getString(0);
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

    /** 実行結果 */
    public enum RESULT {
        INSERTED, UPDATED, SKIPPED
    }

    public enum QUERY_OPTION {
        FORCE_UPDATE,
        WITHOUT_UPDATE_LASTUPDATE
    }
    /**
     * タスクの追加/更新
     * @param task タスク
     * @param
     */
    public RESULT saveTask(Task task, QUERY_OPTION... options) {
        if (U.indexOf(QUERY_OPTION.WITHOUT_UPDATE_LASTUPDATE, options) < 0) {
            task.lastupdate = MyDate.now().format();
        }
        ContentValues values = new ContentValues();
        values.put("task", task.task);
        values.put("status", task.status);
        values.put("color", task.color);
        values.put("lastupdate", task.lastupdate);

        ////////////////////
        // UPDATE
        if (task.uuid != null) {
            String where;
            String[] params;
            if (U.indexOf(QUERY_OPTION.FORCE_UPDATE, options) < 0) {
                where = "uuid = ? and lastupdate < ?";
                params = strings(task.uuid, task.lastupdate);
            } else {
                where = "uuid = ?";
                params = strings(task.uuid);
            }
            int updateCount = db.update("tasks", values, where, params);
            if (updateCount != 0) {
                return RESULT.UPDATED;
            }
            Cursor cursor = db.rawQuery("select count(*) from tasks where uuid = ? ", strings(task.uuid));
            if (cursor.moveToLast() && cursor.getInt(0) > 0) {
                return RESULT.SKIPPED;
            }
        }

        ////////////////////
        // INSERT
        if (task.uuid == null) {
            task.uuid = UUID.randomUUID().toString();
        }
        values.put("uuid", task.uuid);
        db.insertOrThrow("tasks", null, values);
        return RESULT.INSERTED;
    }

    //public long getLastInsertPrimaryKey() {
    //    Cursor cursor = db.query(
    //            "sqlite_sequence",
    //            new String[]{"seq"},
    //            "name = ?",
    //            new String[]{"tasks"},
    //            null,
    //            null,
    //            null,
    //            null
    //    );
    //    if (cursor.moveToFirst()) {
    //        return cursor.getLong(cursor.getColumnIndex("seq"));
    //    }
    //    return 0;
    //}

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