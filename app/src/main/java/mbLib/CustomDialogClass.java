package mbLib;


import list.shivsamarth_mbs.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;



	public class CustomDialogClass extends Dialog implements OnClickListener 
	{

		private Context activity;
		private Dialog d;
		private Button ok,sms;
		private TextView txt_message; 
		private String textMessage;
		public CustomDialogClass(Context activity,String textMessage) 
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
			
			setContentView(R.layout.custom_dialog_box);		
			ok = (Button)findViewById(R.id.btn_ok);
			sms = (Button)findViewById(R.id.btn_cancel);
			txt_message=(TextView)findViewById(R.id.txt_dia);
			txt_message.setText(textMessage);
			ok.setOnClickListener(this);
			sms.setOnClickListener(this);
		}//end onCreate

		@Override
		public void onClick(View v)  
		{
			switch (v.getId()) 
			{
				case R.id.btn_ok:
					
					//this.finish();
					//this.dismiss();
				  break;	
				  
				case R.id.btn_cancel:

					//this.dismiss();
					break;
				default:
				  break;
			}
			dismiss();
		}
	}//end class

