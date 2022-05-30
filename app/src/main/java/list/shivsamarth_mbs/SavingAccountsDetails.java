package list.shivsamarth_mbs;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class SavingAccountsDetails extends Activity implements OnClickListener 
{
	//Editor e;
	//SavingAccountsDetails act;
	ImageView img_heading;
	SavingAccountsDetails act;
	TextView actype, branch, sch_acno, name, bal,txt_heading;
	ImageButton btn_home, back;
	EditText unclearbal,txt_vir_ac_no,txt_vir_upa_no;
	String actype_val, branch_val, sch_acno_val, name_val, bal_val;
	String str = "", spi_str = "";
	String balance;
	String retMess = "", retVal = "",custid="",accountNo="",respcode="",retval="",respdesc="",retvalweb="";
	int cnt = 0, flag = 0;
		DatabaseManagement dbms;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balance_report);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.savings);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act=this;
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		System.out.println("onCreateView() BalanceRep");
		Log.e("onCreateView()"," BalanceRep");
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
   
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				
		/*Bundle bnd = act.getIntent().getExtras();
		str = bnd.getString("str");
		spi_str = bnd.getString("spi_str");
		balance = bnd.getString("balance");*/
		System.out.println("str--------------" + str);
		System.out.println("balance--------------" + balance);
		System.out.println("Spinner str--------------" + spi_str);
		//Log.e("BalanceRep","onCreateView");
		actype = (TextView) findViewById(R.id.txt_acnt_type);
		branch = (TextView) findViewById(R.id.txt_branch);
		sch_acno = (TextView) findViewById(R.id.txt_sch_acno);
		name = (TextView) findViewById(R.id.txt_name);
		bal = (TextView)findViewById(R.id.txt_bal);
		unclearbal=(EditText)findViewById(R.id.unclearbal);
		back = (ImageButton) findViewById(R.id.btn_back);
		
		btn_home= (ImageButton) findViewById(R.id.btn_home);
		
		txt_heading=(TextView) findViewById(R.id.txt_heading);
		txt_vir_ac_no =(EditText) findViewById(R.id.txt_vir_ac_no);
		txt_vir_upa_no =(EditText) findViewById(R.id.txt_vir_upa_no);
		txt_heading.setText(getString(R.string.lbl_acc_details));
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		back.setImageResource(R.mipmap.backover);
		back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		Intent getdata=getIntent();
		Bundle b1=getdata.getExtras();
		if(b1!=null)
		{
			accountNo=b1.getString("accountnumber");
			Log.e("onCreateView","accountNo=="+accountNo);
			//Log.e("onCreateView","accountNo=="+accountNo);
		}

		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custid=c1.getString(2);
	        	Log.e("custId","......"+custid);
	        }
        }
		
		//setValues();
		
		flag = chkConnectivity();
		//flag=0;
		if (flag == 0) 
		{
			CallWebServiceGetOperativeAccDetails c=new CallWebServiceGetOperativeAccDetails();
			c.execute();
		}
        
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
    }
	
	
	public void setValues(String str) 
	{		
		String sterval=str.split("SUCCESS~")[1];
		String[] retValues=sterval.split("#");
		
		branch.setText(retValues[0]);
		
		
		if(retValues[1].equalsIgnoreCase("SB"))
		{
			actype.setText("Savings");
		}
		else if(retValues[1].equalsIgnoreCase("LO"))
		{
			actype.setText("Loan");
		}
		else if(retValues[1].equalsIgnoreCase("RP"))
		{
			actype.setText("Re-Investment Plan");
		}
		else if(retValues[1].equalsIgnoreCase("FD"))
		{
			actype.setText("Fixed Deposite");
		}
		else if(retValues[1].equalsIgnoreCase("CA"))
		{
			actype.setText("Current Account");
		}
		else if(retValues[1].equalsIgnoreCase("PG"))
		{
			actype.setText("Pigmi Account");
		}
		sch_acno.setText(accountNo);
		
		name.setText(retValues[2]);
		bal.setText(MBSUtils.amountFormat(retValues[3],true,act));
		/*if(accountNo.substring(15, 16).equalsIgnoreCase("0"))
			
		else if(accountNo.substring(15, 16).equalsIgnoreCase("3"))
			bal.setText(MBSUtils.amountFormat("12305",true,act));
		else if(accountNo.substring(15, 16).equalsIgnoreCase("8"))
			bal.setText(MBSUtils.amountFormat("3050",true,act));
		else if(accountNo.substring(15, 16).equalsIgnoreCase("1"))
			bal.setText(MBSUtils.amountFormat("7506",true,act));
		else
			bal.setText(MBSUtils.amountFormat("750",true,act));*/
		unclearbal.setText(retValues[4]);
		txt_vir_ac_no.setText(retValues[5]);
		txt_vir_upa_no.setText(retValues[6]);

	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_back:
				System.out.println("Clicked on back");
				/*Bundle bundle=new Bundle();
				Fragment fragment = new HomeFragment(act);
				bundle.putInt("CHECKACTTYPE", 1);
				fragment.setArguments(bundle);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();*/
				Intent in1=new Intent(act,SavingAccounts.class);
				in1.putExtra("var1", var1);
				   in1.putExtra("var3", var3);
				startActivity(in1);
				finish();
				break;
				
			case R.id.btn_home:
				Intent in=new Intent(act,DashboardActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
		}
	}
	
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
						//pb_wait.setVisibility(ProgressBar.VISIBLE);
						// locManager = (LocationManager)
						// getSystemService(Context.LOCATION_SERVICE);
						// netFlg = gpsFlg = 1;
						// Toast.makeText(this, ""+pref,
						// Toast.LENGTH_LONG).show();
						// if (pref.equals("G"))
						// new GpsTimer(timeout * 1000, 1000,
						// this);
						// else
						// new NetworkTimer(timeout * 1000,
						// 1000, this);
					}
					break;
				case DISCONNECTED:
					flag = 1;
					//retMess = "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					//retMess = "Network Unavailable. Please Try Again.";
					retMess=getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			//Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			//Log.i("mayuri", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
	
	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{@Override
			public void onClick(View v)
 
			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							setValues(retvalweb);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
							this.dismiss();
					  break;			
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.show();
	}
	
	
	class CallWebServiceGetOperativeAccDetails extends AsyncTask<Void, Void, Void> {// CallWebServiceGetOperativeAccDetails

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		//String[] xmlTags = { "CUSTID", "ACCNO", "IMEINO" };
		
	     JSONObject jsonObj = new JSONObject();

		

		@Override
		protected void onPreExecute() 
		{
			try{
			loadProBarObj.show();
			
			//accountNo=accNo.getText().toString().split(":")[1].trim();
			  System.out.println("custId:"+custid);
			  System.out.println("accountNo:"+accountNo);			  
			  
			 
              jsonObj.put("CUSTID", custid);
              jsonObj.put("ACCNO", accountNo);
              jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(act));
              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              jsonObj.put("METHODCODE","33");
			 /* valuesToEncrypt[0] = custid; 
			  valuesToEncrypt[1] = accountNo;
			  valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
			 */
			}
			  catch (JSONException je) {
	                je.printStackTrace();
	            }
		   

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			System.out
					.println("============= inside doInBackground =================");
			 String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "callWebservice";

				try 
				{
					String keyStr=CryptoClass.Function2();
					var2=CryptoClass.getKey(keyStr);
					SoapObject request = new SoapObject(value4, value7);
					request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
					request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
					request.addProperty("value3", var3);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);

					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1,var5.length() - 3);
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
		
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
   				jsonObj = new JSONObject(str.trim());
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE"))
				{
					respcode = jsonObj.getString("RESPCODE");
				}
				else
				{
					respcode="-1";
				}
				if (jsonObj.has("RETVAL"))
				{
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdesc = "";
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			if(respdesc.length()>0)
			{
				showAlert(respdesc);
			}
			else{
		try{
			Log.e("Debug@retvalweb-------", retvalweb);
			if (retvalweb.indexOf("FAILED") > -1) 
			{
				post_success(retvalweb);
			} 		
			else 
			{
				setValues(retvalweb);
			}
	}
		catch (Exception je) {
            je.printStackTrace();
        }}
		}// end onPostExecute

	}// end CallWebServiceGetOperativeAccDetails
	
	public 	void post_success(String retvalweb)
	{
		respcode="";
		
		respdesc="";
		retMess = getString(R.string.alert_092);
		showAlert(retMess);
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
