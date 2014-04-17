package utb.dip.jp.netalis;

import android.support.v4.app.FragmentActivity;
import android.view.View;

abstract public class BaseActivity extends FragmentActivity {
    public <V extends View> V find(int id) {
        return U.find(this, id);
    }
}
