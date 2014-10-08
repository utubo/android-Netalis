package utb.dip.jp.netalis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.text.MessageFormat;
import java.util.List;

/** エクスポート・インポート画面 */
public class ConvertJSONActivity extends BaseActivity {

    int offset = 0;
    int count = 0;
    int maxCount = 0;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_json);
        MainActivity.dbAdapter.open();
        try {
            maxCount = (int) MainActivity.dbAdapter.selectCountAllTasks();
        } finally {
            MainActivity.dbAdapter.close();
        }
        count = Math.min(999, maxCount);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.convert_json, menu);
        return true;
    }


    // OFFSET、COUNT入力ダイアログ
    public static class MainFragmentDialog extends DialogFragment {
        public ConvertJSONActivity owner;
        public int id;
        public String title;
        public Drawable icon;
        private NumberPicker findNumberPicker(int value, int max, View view, int id) {
            NumberPicker np = (NumberPicker) view.findViewById(id);
            np.setMinValue(0);
            np.setMaxValue(Math.max(0, max));
            np.setValue(value);
            return np;
        }
        private Activity a() {
            Activity a = getActivity();
            if (a == null) throw new RuntimeException("activity is null");
            return a;
        }
        /** {@inheritDoc} */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final LayoutInflater inflater = a().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_convert_json_export_dialog, null, false);
            final NumberPicker offsetNumberPicker = findNumberPicker(owner.offset, owner.maxCount - 1, view, R.id.export_dialog_offset);
            final NumberPicker countNumberPicker  = findNumberPicker(owner.count, owner.maxCount, view, R.id.export_dialog_count);
            AlertDialog.Builder builder = new AlertDialog.Builder(a());
            // OKクリック時の処理
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText jsonEditText = (EditText) owner.findViewById(R.id.json_editText);
                    MainActivity.dbAdapter.open();
                    try {
                        owner.offset = offsetNumberPicker.getValue();
                        owner.count = countNumberPicker.getValue();
                        jsonEditText.setText(Task.toJSON(MainActivity.dbAdapter.selectAllTasks(owner.offset, owner.count)));
                    } catch (Exception e) {
                        jsonEditText.setText(e.getMessage());
                    } finally {
                        MainActivity.dbAdapter.close();
                    }
                }
            });
            // CANCELクリック時の処理
            builder.setNegativeButton("Cancel", null);
            // タイトル
            builder.setTitle(title);
            builder.setIcon(icon);
            builder.setView(view);
            return builder.create();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        final String title = item.getTitle().toString();
        final Drawable icon = item.getIcon();
        switch (id) {
            case (R.id.action_export) : {
                final ConvertJSONActivity owner = this;
                // ダイアログの表示
                MainFragmentDialog dialog = new MainFragmentDialog();
                dialog.owner = owner;
                dialog.icon = icon;
                dialog.title = title;
                dialog.id = id;
                dialog.show(getFragmentManager(), "convert_json_export_dialog");
                return true;
            }
            case (R.id.action_import) : {
                // Y/Nダイアログ
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                alertDlg.setTitle(title);
                alertDlg.setIcon(icon);
                alertDlg.setMessage(getString(R.string.info_import));
                alertDlg.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            importJson();
                        }
                    }
                );
                alertDlg.setNegativeButton(
                        "CANCEL",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }
                );
                alertDlg.create().show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * JSONからインポートする。
     */
    protected void importJson() {
        EditText jsonEditText = find(R.id.json_editText);
        try {
            List<Task> list = Task.fromJSON(String.valueOf(jsonEditText.getText()));
            MainActivity.dbAdapter.open();
            try {
                int addCount = 0;
                int updateCount = 0;
                int skipCount = 0;
                for (Task task : list) {
                    switch (MainActivity.dbAdapter.saveTask(task, DBAdapter.QUERY_OPTION.WITHOUT_UPDATE_LASTUPDATE)) {
                        case INSERTED:
                            addCount ++;
                            break;
                        case UPDATED:
                            updateCount ++;
                            break;
                        default:
                            skipCount ++;
                    }
                }
                jsonEditText.setText(MessageFormat.format(
                        "{0} tasks added.\n" +
                        "{1} tasks updated.\n" +
                        "{2} tasks skipped.\n",
                        addCount, updateCount, skipCount
                ));
                MainActivity.refreshTaskAdapters();
            } finally {
                MainActivity.dbAdapter.close();
            }
        } catch (Exception e) {
            jsonEditText.setText(e.getMessage());
        }
    }

}
