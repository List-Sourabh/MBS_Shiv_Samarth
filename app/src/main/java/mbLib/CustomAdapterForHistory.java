package mbLib;

import java.util.ArrayList;

import list.shivsamarth_mbs.R;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapterForHistory  extends ArrayAdapter<HistoryBean>
{

	private ArrayList<HistoryBean> historyBeans;
    Activity context;
    String category;

    public CustomAdapterForHistory(Activity context, ArrayList<HistoryBean> historyBeans)
    {
        super(context, R.layout.history_list,historyBeans);
        this.context = context;
        this.historyBeans=historyBeans;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.history_list ,null, true);

        TextView payee = (TextView) rowView.findViewById(R.id.payee);
        TextView amount = (TextView) rowView.findViewById(R.id.amount);
        TextView biller = (TextView) rowView.findViewById(R.id.biller);
        TextView status = (TextView) rowView.findViewById(R.id.status);
        try
        {
        	payee.setText(historyBeans.get(position).getPayeenm());
        	amount.setText(historyBeans.get(position).getAmount());
        	biller.setText(historyBeans.get(position).getBiller());
        	status.setText(historyBeans.get(position).getStatus());
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return rowView;
    }
}
