package list.shivsamarth_mbs;

import list.shivsamarth_mbs.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ErrorDialogClass extends Dialog implements OnClickListener 
{

	private Context activity;
	private Dialog d;
	private Button ok;
	private TextView txt_message; 
	public  String textMessage;
	
	public ErrorDialogClass(Context activity,String textMessage) 
	{
		super(activity);		
		this.textMessage=textMessage;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.custom_dialog);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		ok = (Button)findViewById(R.id.btn_ok);
		txt_message=(TextView)findViewById(R.id.txt_dia);
		txt_message.setText(textMessage);
		ok.setOnClickListener(this);		
	}//end onCreate

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_ok:
				
				this.dismiss();
				
			  break;			
			default:
			  break;
		}
		dismiss();
	}
}//end class