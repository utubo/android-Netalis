package utb.dip.jp.netalis;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class MyListView extends ListView {

    public static class Config {
        public static int blinkDuration = 1500;
    }

    @SuppressWarnings("unused")
    public MyListView(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("unused")
    public MyListView(Context context, AttributeSet attrs) {
       super(context, attrs);
    }

    /**
     * 画面の中心に表示するようにスクロールする。
     * @param position 表示対象
     */
    public void setSelectionCenter(int position) {
        if (position < 0 || getAdapter().getCount() < position) {
            return;
        }
        View firstView = getChildAt(0);
        int offset = (getHeight() - (firstView == null ? 0 : firstView.getHeight())) / 2;
        setSelectionFromTop(position, offset);
    }

    /**
     * リストアイテムを点滅表示する。
     * @param position 点滅対象
     */
    public void blink(final int position) {
        post(new Runnable() {
            @Override
            public void run() {
                View v = getChildAt(position - getFirstVisiblePosition());
                if (v != null) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f, 0f, 1f);
                    oa.setDuration(Config.blinkDuration);
                    oa.start();
                }
            }
        });
    }
}
