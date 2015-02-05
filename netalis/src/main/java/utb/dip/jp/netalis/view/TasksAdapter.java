package utb.dip.jp.netalis.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import utb.dip.jp.netalis.R;
import utb.dip.jp.netalis.model.Task;
import utb.dip.jp.netalis.model.TaskStatus;
import utb.dip.jp.netalis.util.Config;
import utb.dip.jp.netalis.util.DBAdapter;
import utb.dip.jp.netalis.model.TaskColor;
import utb.dip.jp.netalis.util.U;

/**
 * タスクリストと画面をつなげるアダプター
 */
public class TasksAdapter extends ArrayAdapter<Task> {

    private LayoutInflater _inflater;

    /** U.STATUS列挙体 */
    public TaskStatus status;

    /** 続きがあるか */
    public boolean hasMore = true;

    /**
     * コンストラクタ
     * @param context 表示するcontext
     * @param stauts U.STATUS列挙体
     */
    public TasksAdapter(Context context, TaskStatus stauts) {
        super(context, R.layout.task_list_item, R.id.title_textView);
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.status = stauts;
    }

    /**
     * 続きを読み込む
     * @param openedDBAdapter Open済みのDBアダプタ
     */
    public void loadMore(DBAdapter openedDBAdapter) {
        List<Task> tasks = openedDBAdapter.selectLimitedTasks(status, getCount());
        for (Task task : tasks) {
            add(task);
        }
        hasMore = tasks.size() == DBAdapter.LIMIT_COUNT;
    }

    /** {@inheritDoc} */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (_inflater == null)
            return null;
        View view = U.nvl(convertView, _inflater.inflate(R.layout.task_list_item, null));
        if (view == null) {
            return null;
        }
        // UI
        LinearLayout container =  U.find(view, R.id.task_list_item_container);
        TextView titleTextView =  U.find(view, R.id.title_textView);
        TextView subTextView = U.find(view, R.id.sub_textView);
        // アイテム
        Task task = getItem(position);
        // テキスト
        String[] lines = task.task.split("\n");
        titleTextView.setText(lines[0]);
        subTextView.setText(1 < lines.length ? lines[1] : "");
        // 色
        TaskColor c = TaskColor.taskColor(task.color);
        U.applyBackground(container, R.drawable.shape_task, c.taskColor, Config.TASK_ALPHA);
        titleTextView.setTextColor(c.textColor);
        subTextView.setTextColor(c.textColor);

        return view;
    }

    /**
     * このアダプターに登録されているTaskを検索する。
     * @param uuid Taskのuuid
     * @return Task見つからなければnull。
     */
    public Task find(String uuid) {
        if (U.isEmpty(uuid)) {
            return null;
        }
        int count = getCount();
        for (int i = 0; i < count; i ++) {
            Task task = getItem(i);
            if (uuid.equals(task.uuid)) {
                return task;
            }
        }
        return null;
    }

    /**
     * 他Activityへ渡すためのデータをセットする。
     * @param intent intent
     * @param task task
     */
    public static void putExtra(Intent intent, Task task) {
        intent.putExtra("task", Task.toJSON(Arrays.asList(task)));
    }

    /**
     * 他Activityから受け取ったデータからTaskを作成する。
     * @param intent intent
     * @return task
     */
    public static Task fromExtra(Intent intent) {
        return U.find(Task.fromJSON(intent.getStringExtra("task")), 0);
    }

}
