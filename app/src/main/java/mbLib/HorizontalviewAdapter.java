package mbLib;

import java.util.ArrayList;

import list.shivsamarth_mbs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HorizontalviewAdapter extends BaseAdapter 
{

	static Context context;
	LayoutInflater inflater;
	int GroupPosition,ListPosition;
	ArrayList<String> tagNames;


	public HorizontalviewAdapter(Context Context, ArrayList<String> tagNames)
	{
		context = Context;
		this.tagNames=tagNames;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() 
	{
		return tagNames.size();
	}
	
	@Override
	public Object getItem(int position) 
	{
		// TODO Auto-generated method stub
		return tagNames.size();
	}
	
	@Override
	public long getItemId(int position) 
	{
		// TODO Auto-generated method stub
		return position;
	}
	
	public static class ViewHolder 
	{
		//ImageView imgIndicator,image;
		TextView plan_id;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		ViewHolder holder = null;
		
		if (rowView == null)
		{
			holder = new ViewHolder();
			rowView = inflater.inflate(R.layout.horizontal,parent, false);
			holder.plan_id=(TextView)rowView.findViewById(R.id.plan_id);
			rowView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder)rowView.getTag();
		}
		holder.plan_id.setText(tagNames.get(position));
		
		return rowView;
	}
}