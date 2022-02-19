package mbLib;

import java.util.ArrayList;

import list.shivsamarth_mbs.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapterForRechargePlan extends ArrayAdapter<RechargePlanBean>
{
    private ArrayList<RechargePlanBean> rechargeBeans;
    Activity context;
    String rechargeType;

    public CustomAdapterForRechargePlan(Activity context, ArrayList<RechargePlanBean> rechargeBeans,String rechargeType){
        super(context, R.layout.customelist_rech_transaction,rechargeBeans);
        this.context = context;
        this.rechargeBeans=rechargeBeans;
        this.rechargeType=rechargeType; 
    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.customelist_rech_transaction ,null, true);

        TextView tran_mrp = (TextView) rowView.findViewById(R.id.tran_mrp);
        TextView tran_validity = (TextView) rowView.findViewById(R.id.tran_validity);
        TextView tran_desc = (TextView) rowView.findViewById(R.id.tran_desc);
        try
        {
        	tran_mrp.setText("Mrp Rs. "+rechargeBeans.get(position).getMrp());
            tran_validity.setText(rechargeBeans.get(position).getValidity());
            tran_desc.setText(rechargeBeans.get(position).getDescription());	
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return rowView;
    }
} 