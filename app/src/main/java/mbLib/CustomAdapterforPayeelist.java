package mbLib;

import java.util.ArrayList;

import list.shivsamarth_mbs.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomAdapterforPayeelist extends ArrayAdapter<PayeeBean>
{
    private ArrayList<PayeeBean> payeeBeans;
    Activity context;
    String category;

    public CustomAdapterforPayeelist(Activity context, ArrayList<PayeeBean> payeeBeans,String categor){
        super(context, R.layout.listviewpayee,payeeBeans);
        this.context = context;
        this.payeeBeans=payeeBeans;
        category=categor;
        
    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listviewpayee ,null, true);

        TextView item1 = (TextView) rowView.findViewById(R.id.item1);
        TextView item2 = (TextView) rowView.findViewById(R.id.item2);
        try
        {
        	if(category.equalsIgnoreCase("PREPAID MOBILE"))
        	{
        	Log.e("date","date"+payeeBeans.get(position).getAccname());
            String accnam =payeeBeans.get(position).getAccname();
            item1.setText(accnam);
            Log.e("date","date"+payeeBeans.get(position).getConsumerno());
            String mobno =payeeBeans.get(position).getConsumerno();
            item2.setText(mobno);
            }
        	else if(category.equalsIgnoreCase("PREPAID DTH"))
        	{
        	Log.e("date","date"+payeeBeans.get(position).getAccname());
            String accnam =payeeBeans.get(position).getAccname();
            item1.setText(accnam);
            String cunsno =payeeBeans.get(position).getConsumerno();
            item2.setText(cunsno);
        	}
        	
        	else if(category.equalsIgnoreCase("DATACARD"))
        	{
        	Log.e("date","date"+payeeBeans.get(position).getAccname());
            String accnam =payeeBeans.get(position).getAccname();
            item1.setText(accnam);
            String mobno =payeeBeans.get(position).getMobileno();
            item2.setText(mobno);
        	}
        	else
        	{
            	Log.e("date","date"+payeeBeans.get(position).getAccname());
                String accnam =payeeBeans.get(position).getAccname();
                item1.setText(accnam);
                String Billername =payeeBeans.get(position).getBillername();
                item2.setText(Billername);
        	}
          
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
