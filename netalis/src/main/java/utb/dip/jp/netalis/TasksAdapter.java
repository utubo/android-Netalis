package utb.dip.jp.netalis;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
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
        View view = Utils.nvl(convertView, _inflater.inflate(R.layout.task_list_item, null));
        if (view == null) {
            return null;
        }
        Task task = getItem(position);
        // テキスト
        String[] lines = task.task.split("\n");
        ((TextView) view.findViewById(R.id.title_textView)).setText(lines[0]);
        ((TextView) view.findViewById(R.id.sub_textView)).setText(1 < lines.length ? lines[1] : "");
        // 色
        Utils.TaskColor c = Utils.taskColor(task.color);
        SurfaceView sur = (SurfaceView) view.findViewById(R.id.task_color_surfaceView);
        sur.setBackgroundColor(c.taskColor);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.task_list_item_container);
        container.setBackgroundColor(c.background);

        return view;
    }

    public Task find(String uuid) {
        if (Utils.isEmpty(uuid)) {
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
    }

    public static Task toTask(Intent intent) {
        Task task = new Task();
        task.uuid = intent.getStringExtra("uuid");
        task.task = intent.getStringExtra("task");
        task.status = intent.getIntExtra("status", Utils.STATUS.TODO.intValue);
        task.color = intent.getStringExtra("color");
        return task;
    }

}
