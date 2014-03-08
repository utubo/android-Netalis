package utb.dip.jp.netalis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconTextAdapter extends ArrayAdapter<IconTextAdapter.IconText> {

    /**
     * アイコンとテキスト
     */
    public static class IconText {
        String text;
        int icon;

        public IconText(String text, int icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    /**
     * テキストとアイコンのホルダ
     */
    private class IconTextView {
        TextView textView;
        ImageView iconView;
    }

    private Context aplContext;

    /**
     * コンストラクタ
     */
    public IconTextAdapter(Context context) {
        super(context, R.layout.list_item_icon_text);
        aplContext = context.getApplicationContext();
    }

    public IconTextAdapter add(CharSequence charSequence, int icon) {
        add(new IconText(charSequence.toString(), icon));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    /** {@inheritDoc} */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        IconTextView holder;
        if (view == null) {
            view = LayoutInflater.from(aplContext).inflate(
                    R.layout.list_item_icon_text, null);

            holder = new IconTextView();
            holder.textView = (TextView) view.findViewById(R.id.title);
            holder.iconView = (ImageView) view.findViewById(R.id.icon);
            view.setTag(holder);
        } else {
            holder = (IconTextView) view.getTag();
        }
        final IconText item = getItem(position);
        holder.textView.setText(item.text);
        holder.iconView.setImageResource(item.icon);

        return view;
    }
}