package utb.dip.jp.netalis;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/* Task編集画面 */
public class EditActivity extends BaseActivity {

    private Task task = new Task();

    private List<ImageView> stars = new ArrayList<ImageView>();
    private EditText editText = null;
    private LinearLayout priorityButtons = null;
    private LinearLayout colorButtons = null;
    private boolean isUiInitilized = false;

    /**
     * アクティビティ表示
     * @param savedInstanceState なんだろこれ
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        task = TasksAdapter.fromExtra(intent);
        editText = (EditText) findViewById(R.id.editText);
        priorityButtons = (LinearLayout) findViewById(R.id.priority_buttons_linerLayout);
        colorButtons = (LinearLayout) findViewById(R.id.color_buttons_linerLayout);

        editText.setText(task.task);
        editText.setSelection(task.task.length()); // カーソルを末尾に移動
        setupTaskColor(task.color);

        if (task.uuid == null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    /**
     * 画面レイアウト計算後のイベント<br/>
     * ボタンを設置する。
     * @param hasFocus フォーカス
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (isUiInitilized) {
            return;
        }
        isUiInitilized = true;

        // 星ボタン
        for (int i = 0; i <= U.Config.PRIORITY_MAX; i ++) {
            ImageView button = new ImageView(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            final int priority = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setupPriority(priority);
                }
            });
            button.setImageResource(i == 0 ? android.R.drawable.ic_menu_close_clear_cancel : starId(i));
            stars.add(button);
            priorityButtons.addView(button);
        }

        // 色選択ボタン
        for (U.TaskColor c : U.taskColorHashMap.values()) {
            if (c.isParseError) {
                continue;
            }
            Button button = new Button(this, null, R.style.ColorButtonStyle);
            U.applyBackground(button, R.drawable.shape_circle, c.taskColor);
            button.setWidth(colorButtons.getHeight());
            button.setHeight(colorButtons.getHeight());
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

    /**
     * 優先度以下の★ボタンのidを取得する。
     * @param priority 優先度
     * @return ボタンid
     */
    private int starId(int priority) {
        return priority <= task.priority ? android.R.drawable.star_on : android.R.drawable.star_off;
    }

    /**
     * 優先度（★）の設定
     * @param priority 優先度
     */
    public void setupPriority(int priority) {
        task.priority = priority;
        for (int i = 1; i < stars.size(); i ++) {
            stars.get(i).setImageResource(starId(i));
        }
    }

    /**
     * 色設定。TextEditの色も変更する。
     * @param color 色
     */
    public void setupTaskColor(String color) {
        task.color = color;
        U.TaskColor c = U.taskColor(color);
        editText.setTextColor(c.textColor);
        U.applyBackground(editText, R.drawable.shape_task, c.taskColor);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    /** {@inheritDoc} */
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

    /**
     * 戻るボタン。変更内容を呼び出し元へ返却してアクティビティを閉じる。
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // intentの作成
        Intent intent = new Intent();

        // intentへ添え字付で値を保持させる
        if (editText != null && editText.getText() != null) {
            task.task = editText.getText().toString();
            TasksAdapter.putExtra(intent, task);
        }

        // 返却したい結果ステータスをセットする
        setResult( RESULT_OK, intent );

        // アクティビティを終了させる
        finish();
    }
}
