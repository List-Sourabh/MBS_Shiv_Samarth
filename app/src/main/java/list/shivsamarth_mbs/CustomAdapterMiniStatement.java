package list.shivsamarth_mbs;

import java.util.ArrayList;

import mbLib.MiniStatementBean;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapterMiniStatement extends ArrayAdapter<MiniStatementBean>
{
	Activity context;
	ArrayList<MiniStatementBean> MiniStmntBeanArray;
	
	public CustomAdapterMiniStatement(Activity mainActivity, ArrayList<MiniStatementBean> MiniStmntBeanArray) 
	{
		super(mainActivity, R.layout.mini_stmt_row,MiniStmntBeanArray);
		this.context=mainActivity;
		this.MiniStmntBeanArray=MiniStmntBeanArray;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		Log.e("CUSTOM", "size======"+MiniStmntBeanArray.size());
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.mini_stmt_row ,null, true);
		

        TextView item1 = (TextView) rowView.findViewById(R.id.item2);
        TextView item2 = (TextView) rowView.findViewById(R.id.item3);
        TextView item3 = (TextView) rowView.findViewById(R.id.item4);
        TextView item4 = (TextView) rowView.findViewById(R.id.item5);
        try
        {
        	item1.setText(MiniStmntBeanArray.get(position).getDate());
        	item2.setText(MiniStmntBeanArray.get(position).getDescr());
        	if(MiniStmntBeanArray.get(position).getDrCr().equalsIgnoreCase("CR"))
        	{
        		item3.setTextColor(context.getResources().getColor(R.color.green));
        		item4.setTextColor(context.getResources().getColor(R.color.green));
        	}
        	else
        	{
        		item3.setTextColor(context.getResources().getColor(R.color.red_color));
        		item4.setTextColor(context.getResources().getColor(R.color.red_color));
        	}
        	
        	item3.setText(MiniStmntBeanArray.get(position).getAmount());
        	item4.setText(MiniStmntBeanArray.get(position).getDrCr());
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
			
        /*ViewGroup.LayoutParams params = rowView.getLayoutParams();
        params.height = 20;
        rowView.setLayoutParams(params);*/
		
		return rowView;
	}

}
