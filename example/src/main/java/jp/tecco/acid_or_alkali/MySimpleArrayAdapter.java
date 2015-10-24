package jp.tecco.acid_or_alkali;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by makotonishimoto on 2015/05/29.
 */
public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public MySimpleArrayAdapter(Context context, String[] values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        // Change the icon for USA and JAPAN
        String s = values[position];
        if (s.endsWith("1")) {
            imageView.setImageResource(R.drawable.japan);
        } else {
            imageView.setImageResource(R.drawable.usa);
        }

        String hoge = values[position].substring(0, s.length() - 1);
        textView.setText(hoge);

        return rowView;
    }
}