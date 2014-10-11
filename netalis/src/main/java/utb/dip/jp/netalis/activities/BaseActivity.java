package utb.dip.jp.netalis.activities;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import utb.dip.jp.netalis.util.U;

/** 画面の基底クラス */
abstract public class BaseActivity extends FragmentActivity {
    public <V extends View> V find(int id) {
        return U.find(this, id);
    }
}
