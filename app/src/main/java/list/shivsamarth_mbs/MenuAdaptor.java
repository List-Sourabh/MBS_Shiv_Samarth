package list.shivsamarth_mbs;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuAdaptor extends ArrayAdapter<MenuIcon>{

    Context context; 
    int layoutResourceId;    
    MenuIcon data[] = null;
    
    public MenuAdaptor(Context context, int layoutResourceId, MenuIcon[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

  //  @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MenuHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new MenuHolder();           
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.listViewLayout = (LinearLayout)row.findViewById(R.id.listViewLayout);
            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder)row.getTag();
        }
        
        MenuIcon item = data[position];
        holder.txtTitle.setText(item.title);
        holder.imgIcon.setImageResource(item.icon);
        
        holder.listViewLayout.setBackgroundColor(Color.WHITE);
		holder.txtTitle.setTextColor(Color.BLACK);
       /* if(position%2==0)
		{	
			holder.imgIcon.setImageResource(R.mipmap.saving);
		}
		else
		{
			holder.imgIcon.setImageResource(R.mipmap.arrowover);
		}*/
        /*holder.listViewLayout.setBackgroundColor(Color.WHITE);
		holder.txtTitle.setTextColor(Color.BLACK);
		holder.imgIcon.setImageResource(R.mipmap.next_d);*/
        
        return row;
    }
    
    static class MenuHolder
    {
        
        TextView txtTitle;
        ImageView imgIcon;
        LinearLayout listViewLayout;
    }
}