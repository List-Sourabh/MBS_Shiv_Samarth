package list.shivsamarth_mbs;

import java.util.ArrayList;

import mbLib.Accountbean;
import mbLib.FontCache;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class Customlist_radioadt extends ArrayAdapter<Accountbean> {

	private ArrayList<Accountbean> Accountbean_arr;
	Activity context;
	RadioButton r;
	/*Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
			"fonts/Kozuka-Gothic-Pro-M_26793.ttf");*/

	public Customlist_radioadt(Activity context,
			ArrayList<Accountbean> Accountbean_arr) {
		super(context, R.layout.account_list, Accountbean_arr);
		this.context = context;
		this.Accountbean_arr = Accountbean_arr;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.custom_list_withradio, null,
				true);
		TextView txt_account1 = (TextView) rowView
				.findViewById(R.id.account_info);
		//txt_account1.setTypeface(tf);
		txt_account1.setTypeface(FontCache.get("fonts/futura-book-bt.ttf", getContext()));
		r = (RadioButton) rowView.findViewById(R.id.radio);

		LinearLayout listLayout = (LinearLayout) rowView
				.findViewById(R.id.stmtListLayout);
		/*if (position % 2 == 0) {
			listLayout.setBackgroundColor(Color.rgb(251, 225, 121));// (249,242,214));
			// txt_account1.setTextColor(Color.WHITE);
		} else*/ {
			listLayout.setBackgroundColor(Color.WHITE);
			// txt_account1.setTextColor(Color.rgb(219, 103, 21));
		}
		// listLayout.setBackgroundColor(Color.WHITE);
		txt_account1.setTextColor(context.getResources().getColor(R.color.samarth_text_color));
		// r.setOnCheckedListener();

		/*
		 * r.setChecked(position == selectedPosition); r.setTag(position);
		 * r.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { selectedPosition =
		 * (Integer)view.getTag(); notifyDataSetChanged(); } });
		 */

		// Log.e("111",Accountbean_arr.get(position).getAccountinfo());
		try {
			txt_account1
					.setText(Accountbean_arr.get(position).getAccountinfo());
		} catch (Exception e) {
			e.printStackTrace();

		}
		return rowView;
	}

}
