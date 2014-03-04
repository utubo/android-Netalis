package utb.dip.jp.netalis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.MessageFormat;
import java.util.List;

public class ConvertJSONActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_json);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.convert_json, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_export) : {
                EditText jsonEditText = (EditText) findViewById(R.id.json_editText);
                MainActivity.dbAdapter.open();
                try {
                    jsonEditText.setText(Task.toJSON(MainActivity.dbAdapter.selectAllTasks()));
                } catch (Exception e) {
                    jsonEditText.setText(e.getMessage());
                } finally {
                    MainActivity.dbAdapter.close();
                }
                return true;
            }
            case (R.id.action_import) : {
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                alertDlg.setTitle("");
                alertDlg.setMessage("import ?");
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
        EditText jsonEditText = (EditText) findViewById(R.id.json_editText);
        try {
            List<Task> list = Task.fromJSON(jsonEditText.getText().toString());
            MainActivity.dbAdapter.open();
            try {
                int addCount = 0;
                int updateCount = 0;
                int skipCount = 0;
                for (Task task : list) {
                    switch (MainActivity.dbAdapter.saveTaskWithoutLastupdateTimestamp(task)) {
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
