package utb.dip.jp.netalis;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EditActivity extends ActionBarActivity {

    private Task task = new Task();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        task = TasksAdapter.toTask(intent);
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(task.task);
        setupTaskColor(task.color);

        if (task._id < 0) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LinearLayout colorButtons = (LinearLayout) findViewById(R.id.color_buttons_linerLayout);
        for (Utils.TaskColor c : Utils.taskColors) {
            Button button = new Button(this, null, R.style.ColorButtonStyle);
            button.setWidth(colorButtons.getHeight());
            button.setHeight(colorButtons.getHeight());
            button.setBackgroundColor(c.taskColor);
            button.setTag(c.colorDBValue);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setupTaskColor((String) view.getTag());
                }
            });
            colorButtons.addView(button);
        }
    }

    public void setupTaskColor(String color) {
        task.color = color;
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setBackgroundColor(Utils.taskColor(color).background);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // intentの作成
        Intent intent = new Intent();

        // intentへ添え字付で値を保持させる
        task.task = ((EditText) findViewById(R.id.editText)).getText().toString();
        TasksAdapter.putExtra(intent, task);

        // 返却したい結果ステータスをセットする
        setResult( RESULT_OK, intent );

        // アクティビティを終了させる
        finish();
    }
}
