package mbLib;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class CustomButton extends Button 
{
	//Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public CustomButton(Context context) {
	    super(context);
	    init();
	}
	
	private void init() {
	    //if (!isInEditMode()) 
	    {
	        //setTypeface(FontCache.get("fonts/roboto.medium-italic.ttf", getContext()));
	    	setTypeface(FontCache.get("fonts/Poppins-Regular.otf", getContext()));
	    	setTextColor(Color.parseColor("#FFFFFF"));
	    	
	    }
	}
	protected void finalize()
	{
		System.gc();
	}
	/*private void destroy()
	{
		tf=null;
	}*/
}