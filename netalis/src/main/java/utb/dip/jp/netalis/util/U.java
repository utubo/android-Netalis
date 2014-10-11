package utb.dip.jp.netalis.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * ユーティリティ
 */
public class U {

    public static Random rnd = new Random();

    /**
     * valueがnullならotherwiseを返す。
     * @param value value
     * @param otherwise valueがnullの時の値
     * @param <T> 任意の型
     * @return valueかotherwise
     */
    public static <T> T nvl(T value, T otherwise) {
        return (value != null) ? value : otherwise;
    }

    /**
     * nullを考慮したequals。
     * @param a 比較対象
     * @param b 比較対象
     * @return 等しければtrue。
     */
    public static boolean eq(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    /**
     * 文字列がnullか空ならtrueを返す。
     * @param s 対象の文字列
     * @return nullか空ならtrue。
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0 || s.trim().length() == 0);
    }

    /**
     * JSONの文字値を作成する。（「"」だけエスケープして「"」で囲う。）<br/>
     * 「abc"efg"xyz」→「"abc\"efg\"xyz"」<br/>
     * null→「null」
     * @param s 対象の文字列
     * @return JSONの文字値。
     */
    public static String ezJsonStr(String s) {
        if (s == null) {
            return "null";
        }
        s = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + s + "\"";
    }

    /**
     * UUIDの文字列を返す。引数がUUIDの形式出ない場合はnullを返す。
     * @param uuid UUID
     * @return UUIDの文字列かnull。
     */
    public static String uuidOrNull(String uuid) {
        if (isEmpty(uuid)) {
            return null;
        }
        try {
            return UUID.fromString(uuid).toString();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> int indexOf(T item, T[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (eq(item, array[i])) {
                    return i;
                }

            }
        }
        return -1;
    }

    /**
     * List#get(int)のIndexOutOfBounds出ない版
     * @param list list
     * @param index index
     * @param <T> any type
     * @return list.get(index)。indexが範囲外ならnull。
     */
    public static <T> T find(List<T> list, int index) {
        return list.size() <= index ? null : list.get(index);
    }

    /**
     * バックグラウンドの色を指定して形をあてがう
     * @param view 適用するView
     * @param id 形
     * @param color 色
     */
    public static void applyBackground(View view, int id, int color) {
        Resources res = view.getResources();
        if (res == null) {
            return;
        }
        GradientDrawable drawable = (GradientDrawable) res.getDrawable(id);
        if (drawable == null) {
            return;
        }
        drawable.setColor(color);
        view.setBackgroundDrawable(drawable);
    }

    /**
     * 「@SuppressWarnings("ConstantConditions")」を書くのが面倒なときに…
     * @param v value
     * @param <V> valueのクラス
     * @return value
     */
    public static <V> V notNull(V v) {
        if (v == null) throw new RuntimeException("is null.");
        return v;
    }

    /**
     * Viewの検索
     * @param activity 親
     * @param id 検索するid
     * @param <V> クラス
     * @return view。nullの場合やキャスト例外はとりあえずRuntimeException。
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> V find(Activity activity, int id) {
        try {
            return (V) notNull(activity.findViewById(id));
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Viewの検索
     * @param view 親
     * @param id 検索するid
     * @param <V> クラス
     * @return view。nullの場合はとりあえずRuntimeException。
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> V find(View view, int id) {
        try {
            return (V) notNull(view.findViewById(id));
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

}
