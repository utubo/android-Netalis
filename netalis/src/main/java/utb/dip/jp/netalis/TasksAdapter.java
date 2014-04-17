package utb.dip.jp.netalis;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * タスクリストと画面をつなげるアダプター
 */
public class TasksAdapter extends ArrayAdapter<Task> {

    private LayoutInflater _inflater;

    public TasksAdapter(Context context) {
        super(context, R.layout.task_list_item, R.id.title_textView);
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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
        U.TaskColor c = U.taskColor(task.color);
        U.applyBackground(container, R.drawable.shape_task, c.taskColor);
        titleTextView.setTextColor(c.textColor);
        subTextView.setTextColor(c.textColor);

        return view;
    }

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

    public static void putExtra(Intent intent, Task task) {
        intent.putExtra("uuid", task.uuid);
        intent.putExtra("task", task.task);
        intent.putExtra("status", task.status);
        intent.putExtra("color", task.color);
        intent.putExtra("priority", task.priority);
    }

    public static Task fromExtra(Intent intent) {
        Task task = new Task();
        task.uuid = intent.getStringExtra("uuid");
        task.task = intent.getStringExtra("task");
        task.status = intent.getIntExtra("status", U.STATUS.TODO.intValue);
        task.color = intent.getStringExtra("color");
        task.priority = intent.getIntExtra("priority", 0);
        return task;
    }

}
