package mbLib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.TextView;
public class CusFntRadioButton extends RadioButton{

	public CusFntRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CusFntRadioButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public CusFntRadioButton(Context context) {
	    super(context);
	    init();
	}
	private void init() 
	{
	    if (!isInEditMode()) 
	    {        
	    	//setTypeface(FontCache.get("fonts/roboto.medium-italic.ttf", getContext()));
	    	setTypeface(FontCache.get("fonts/Poppins-Regular.otf", getContext()));
	    	setTextColor(Color.parseColor("#340C6F"));
	    }
	}
}
