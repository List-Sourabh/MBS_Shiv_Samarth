package list.shivsamarth_mbs;



import mbLib.FontCache;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomeSpinnerAdapter extends ArrayAdapter<String>
{
	private Activity ctx;
	private String[] spinnerValues;
	LayoutInflater inflater;
	View mySpinner;
	public CustomeSpinnerAdapter(Activity ctx, int txtViewResourceId, String[] spinnerValues) 
	{
		super(ctx, txtViewResourceId, spinnerValues); 
		this.ctx=ctx;
		this.spinnerValues=spinnerValues;
		inflater =ctx.getLayoutInflater();
	}
	
	@Override
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) 
	{ 
		return getCustomView(position, cnvtView, prnt);
	}
	
	@Override 
	public View getView(int pos, View cnvtView, ViewGroup prnt) 
	{ 
		return getCustomView(pos, cnvtView, prnt); 
	} 
	
	public View getCustomView(int position, View convertView, ViewGroup parent)
	{ 
		mySpinner = inflater.inflate(R.layout.spinner_layout, null, true);
		//RadioButton main_text = (RadioButton) mySpinner.findViewById(R.id.text_main_seen); 
		TextView main_text = (TextView) mySpinner.findViewById(R.id.text_main_seen); 
		main_text.setText(spinnerValues[position]); 
	
		main_text.setTypeface(FontCache.get("fonts/futura-book-bt.ttf", getContext()));
		return mySpinner; 
	}
	
}	
