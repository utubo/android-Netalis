package utb.dip.jp.netalis;

import android.support.v4.app.FragmentActivity;
import android.view.View;

/** 画面の基底クラス */
abstract public class BaseActivity extends FragmentActivity {
    public <V extends View> V find(int id) {
        return U.find(this, id);
    }
}
