package mbLib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CusFntTextView extends TextView 
{
	//Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	public CusFntTextView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	}
	
	public CusFntTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public CusFntTextView(Context context) {
	    super(context);
	    init();
	}
	
	private void init() 
	{
	    if (!isInEditMode()) 
	    {        	    
	    	//setTypeface(FontCache.get("fonts/roboto.medium-italic.ttf", getContext()));
	    	setTypeface(FontCache.get("fonts/Poppins-Regular.otf", getContext()));
	    	//setTextColor(Color.parseColor("#340C6F"));
	    }
	}
}