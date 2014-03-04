package utb.dip.jp.netalis;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 日付関連ユーティリティ
 * Created by utb on 14/03/02.
 */
public class Task {
    public long _id = -1;
    public String task = "";
    public int status = Utils.STATUS.TODO.intValue;
    public String color = null;
    public String lastupdate = null;
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\t_id:").append(_id).append(",\n");
        sb.append("\tlastupdate:\"").append(lastupdate).append("\",\n");
        sb.append("\tstatus:\"").append(Utils.STATUS.valueOf(status).name()).append("\",\n");
        sb.append("\tcolor:\"").append(color).append("\",\n");
        sb.append("\ttask:\"").append(Utils.easyEscapeJSON(task)).append("\"\n");
        sb.append("}\n");
        return sb.toString();
    }

    public static String toJSON(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ tasks: [\n");
        String dlm = "";
        for (Task task : tasks) {
            sb.append(dlm);
            sb.append(task.toJSON());
            dlm = ",\n";
        }
        sb.append("]}");
        return sb.toString();
    }

    public static List<Task> fromJSON(String s) {
        List<Task> list = new ArrayList<Task>();
        if (Utils.isEmpty(s)) {
            return list;
        }
        try {
            JSONObject jobj = new JSONObject(s);
            JSONArray ary = jobj.getJSONArray("tasks");
            for (int i = 0; i < ary.length(); i ++) {
                JSONObject jTask = ary.getJSONObject(i);
                Task task = new Task();
                task._id = Utils.toLong(jTask.getString("_id"), -1);
                task.color = jTask.getString("color");
                task.status = Utils.STATUS.valueOf(jTask.getString("status")).intValue;
                task.lastupdate = jTask.getString("lastupdate");
                if (!task.lastupdate.matches("[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}")) {
                    task.lastupdate = MyDate.now().format();
                }
                task.task = jTask.getString("task");
                list.add(task);
            }
        } catch (JSONException e) {
            Log.e(null, "import error.", e);
        }
        return list;
    }
}
