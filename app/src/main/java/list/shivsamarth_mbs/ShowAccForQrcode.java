package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ShowAccForQrcode extends Activity implements OnClickListener,
android.view.View.OnKeyListener
{
	MainActivity act;
	ShowAccForQrcode showAccForQrcodeAct;
	private static final String MY_SESSION = "my_session";
	
	ListView acnt_listView;
	Button btn_get_stmt;
	TextView txt_heading;
	ImageView img_heading;
	ImageButton btn_home, btn_back;
	DatabaseManagement dbms;
	String stringValue="", str2="", accountNo="", acnt_inf="";
	protected String accName;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mini_statement);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		acnt_listView = (ListView) findViewById(R.id.acnt_listView);
		btn_get_stmt = (Button) findViewById(R.id.btnGetStmt);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		img_heading.setBackgroundResource(R.mipmap.transfer);
		btn_back.setImageResource(R.mipmap.backover);
		txt_heading.setText(getString(R.string.lbl_qr_receive));
		btn_get_stmt.setText("Generate QR Code");
		btn_get_stmt.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
	        	Log.e("retValStr","......"+stringValue);
	        
	        }
        }
        //stringValue = "2#101#SB#25430#KULKARNI SHASHIKANT  RAJARAM##0020001010025430#O#9999999#15433163#Y~2#101#SB#25584#KULKARNI SHASHIKANT  RAJARAM##0020001010025584#O#NA#618.8#Y~2#101#SB#25635#KULKARNI SHASHIKANT  RAJARAM##0020001010025635#O#NA#25471.2#Y~2#101#SB#25636#KULKARNI SHASHIKANT  RAJARAM##0020001010025636#O#NA#17848.2#Y~2#101#SB#25637#KULKARNI SHASHIKANT  RAJARAM##0020001010025637#O#NA#37783.53#Y~2#1011#SB#1#KULKARNI SHASHIKANT  RAJARAM##0020010110000001#O#NA#0#Y~2#301#LO#58#KULKARNI SHASHIKANT  RAJARAM##0020003010000058#I#NA#13100.3#Y~2#301#LO#131#KULKARNI SHASHIKANT  RAJARAM##0020003010000131#I#NA#452922.87#Y~2#337#LO#8345#KULKARNI SHASHIKANT  RAJARAM##0020003370008345#I#NA#142105#Y~2#TEST2#LO#6#KULKARNI SHASHIKANT  RAJARAM##0020TEST20000006#I#NA#140000#Y~2#901#PG#10020209#KULKARNI SHASHIKANT  RAJARAM##0020009011002020#O##0#N~";//stringValue;

		addAccounts(stringValue);
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}	
	
	public void addAccounts(String str) 
	{
		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			int noOfAccounts = allstr.length;
			ArrayList<Accountbean> Accountbean_arr = new ArrayList<Accountbean>();
			final ArrayList<String> Account_arrTemp = new ArrayList<String>();
			Accounts acArray[] = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) 
			{
				str2 = allstr[i];
				Log.e("ShowAccForQrcode","str====="+str2);
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				if (str2.indexOf("FD") == -1 && str2.indexOf("RP") == -1 && str2.indexOf("PG") == -1) 
				{
					Accountbean Accountbeanobj = new Accountbean();
					Accountbean_arr.add(Accountbeanobj);
					Account_arrTemp.add(str2);
					String acctype=str2.split("-")[2];
					str2 = MBSUtils.get16digitsAccNo(str2);
					Accountbeanobj.setAccountinfo(str2+" ("+MBSUtils.getAccTypeDesc(acctype)+")");
					Accountbeanobj.setAccountNumber(str2);
				}	
			}
		
			Customlist_radioadt adapter = new Customlist_radioadt(this,	Accountbean_arr);
			acnt_listView.setAdapter(adapter);
			acnt_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			acnt_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> adapterView,	View view, int i, long l) 
				{
					btn_get_stmt.setEnabled(true);
					Accountbean dataModel = (Accountbean) adapterView.getItemAtPosition(i);
					accountNo=dataModel.getAccountNumber();
					acnt_inf = Account_arrTemp.get(i);
					accName=acnt_inf.split("-")[4];
					Log.e("ACC","acnt_inf==="+acnt_inf);
					for (int i1 = 0; i1 < adapterView.getCount(); i1++) 
					{
						try 
						{
							View v = adapterView.getChildAt(i1);
							RadioButton radio = (RadioButton) v.findViewById(R.id.radio);
							radio.setChecked(false);
						} 
						catch (Exception e) 
						{
							Log.e("radio button", "radio");
						}

					}
					try 
					{
						RadioButton radio = (RadioButton) view.findViewById(R.id.radio);
						radio.setChecked(true);
					} 
					catch (Exception e) 
					{
						Log.e("radio button", "radio");
					}

				}
			});
		} 
		catch (Exception e) 
		{
			System.out.println("" + e);
		}
	}
	
	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) 
	{
		return false;
	}

	@Override
	public void onClick(View v) 
	{
		Intent in;
		
		switch (v.getId()) 
		{
			case R.id.btnGetStmt:
				Bundle bundle = new Bundle();
				bundle.putString("ACCNO", accountNo);
				bundle.putString("ACCNM", accName);
				Log.e("ShowAccForQRCode","accountNo==="+accountNo);
				in = new Intent(this, QrcodeRcvActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				in.putExtras(bundle);
				startActivity(in);
				finish();
				break;
			case R.id.btn_back:
				in = new Intent(this, FundTransferMenuActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case R.id.btn_home:	
				 in = new Intent(this, DashboardActivity.class);
					in.putExtra("var1", var1);
					   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
				
			default:
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		t1.sec=-1;
		System.gc();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		t1.sec = timeOutInSecs;
		Log.e("sec11= ","sec11=="+t1.sec);
		return super.onTouchEvent(event);
	}
}
