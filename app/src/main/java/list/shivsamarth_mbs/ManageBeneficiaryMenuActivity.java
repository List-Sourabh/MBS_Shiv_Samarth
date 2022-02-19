package list.shivsamarth_mbs;


import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import mbLib.DialogBox;
import mbLib.MyThread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ManageBeneficiaryMenuActivity extends Activity implements View.OnClickListener
{
	  ImageButton btn_home, btn_back;
	  Button but_exit;
	  DialogBox dbs;
	  ManageBeneficiaryMenuActivity act = this;
	  ListView lst_dpt;
	  TextView txt_heading;//,list_benf,remove_benf,add_same_benf,add_other_benf;
	  LinearLayout list_benf,remove_benf,add_same_benf,add_other_benf;
	  ImageView img_heading;
	  Intent in ;
	  private MyThread t1;
		int timeOutInSecs=300;
		PrivateKey var1 = null;
		String var5 = "", var3 = "";
		SecretKeySpec var2 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mngben_submenu);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
	list_benf=(LinearLayout) findViewById(R.id.list_benf);
	remove_benf=(LinearLayout) findViewById(R.id.remove_benf);
	add_other_benf=(LinearLayout) findViewById(R.id.add_other_benf);
	add_same_benf=(LinearLayout) findViewById(R.id.add_same_benf);
	
	txt_heading=(TextView)findViewById(R.id.txt_heading);
	img_heading=(ImageView)findViewById(R.id.img_heading);
	
	btn_home=(ImageButton)findViewById(R.id.btn_home);
	btn_back=(ImageButton)findViewById(R.id.btn_back);
	
	//btn_home.setImageResource(R.mipmap.ic_home_d);
	btn_back.setImageResource(R.mipmap.backover);
	btn_back.setOnClickListener(this);
	btn_home.setOnClickListener(this);
	txt_heading.setText(getString(R.string.lbl_manage_beneficiary));
	img_heading.setBackgroundResource(R.mipmap.list_beneficiary);

	list_benf.setOnClickListener(this);
	remove_benf.setOnClickListener(this);
	add_other_benf.setOnClickListener(this);
	add_same_benf.setOnClickListener(this);
	
	t1 = new MyThread(timeOutInSecs,this,var1,var3);
	t1.start();
	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		 switch(v.getId())
		 {
		 	case R.id.btn_home:	
		 	case R.id.btn_back:	
				Intent in=new Intent(this,DashboardActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
		 		break;
			case R.id.list_benf:
				in = new Intent(act,ListBeneficiary.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				act.startActivity(in);
				act.finish();
				
				break;
			case R.id.add_same_benf:
				in = new Intent(act,AddSameBankBeneficiary.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				act.startActivity(in);
				act.finish();
				break;
			case R.id.add_other_benf:
				in = new Intent(act,AddOtherBankBeneficiary.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				act.startActivity(in);
				act.finish();
				break;
			case R.id.remove_benf:
				
				in = new Intent(act,RemoveBeneficiary.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				act.startActivity(in);
				act.finish();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
	 // Simply Do noting!
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		t1.sec=-1;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		t1.sec = timeOutInSecs;
		Log.e("sec11= ","sec11=="+t1.sec);
		return super.onTouchEvent(event);
	}
}
