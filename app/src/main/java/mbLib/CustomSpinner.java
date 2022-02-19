package mbLib;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomSpinner extends ArrayAdapter<String>
{
	public CustomSpinner(Context context, int resource, String[] items) {
        super(context, resource, items);
    }

    // Affects default (closed) state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(FontCache.get("fonts/Poppins-Regular.otf", getContext()));
        view.setTextColor(Color.parseColor("#FFFFFF"));
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        //view.setTypeface(FontCache.get("fonts/roboto.medium-italic.ttf", getContext()));
        view.setTypeface(FontCache.get("fonts/Poppins-Regular.otf", getContext()));
        view.setTextColor(Color.parseColor("#FFFFFF"));
        return view;
    }
}
