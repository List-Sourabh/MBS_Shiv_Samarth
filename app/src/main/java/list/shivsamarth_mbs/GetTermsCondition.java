package list.shivsamarth_mbs;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DialogBox;
import mbLib.MBSUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GetTermsCondition extends Activity implements OnClickListener 
{
	MainActivity act;
	GetTermsCondition getcustID;
	Button btn_otp;
	EditText txt_accno, txt_mobileno;
	String accno = "", mobno = "", retMess = "",version,respcode="",retval="",respdesc="";
	Boolean isInternetPresent = false;
	private static String NAMESPACE = "";
	private static String URL = ""; 
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = ""; 
	private static String METHOD_NAME1 = "";
	private static String responseJSON = "NULL";
	Intent mainIntent;
	TextView txt_heading,txt_heading1,txt_heading2,txt_heading3,txt_heading4,txt_heading5,txt_heading6,txt_heading7,txt_heading8,term_textView;
	CheckBox term_checkBox;
	String[] presidents;
	ImageView img_heading;
	static String imeiNo;
	ImageButton btn_back;
	String custid = "";
	int flag = 0;
	int netFlg, gpsFlg;
	DialogBox dbs;
	String retVal = "",strCustId="",strFromAct="",strMobNo="",strRetVal="";
	// private Context _context;
	TelephonyManager telephonyManager;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	public GetTermsCondition() {
	}

	public GetTermsCondition(MainActivity a) {
		System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		getcustID = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		// dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");

		setContentView(R.layout.terms_condition);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_terms_cond));	
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.ministatement);
		txt_heading1 = (TextView)findViewById(R.id.txt_heading1);
		txt_heading2= (TextView)findViewById(R.id.txt_heading2);
		txt_heading3 = (TextView)findViewById(R.id.txt_heading3);
		txt_heading4 = (TextView)findViewById(R.id.txt_heading4);
		txt_heading5= (TextView)findViewById(R.id.txt_heading5);
		txt_heading6 = (TextView)findViewById(R.id.txt_heading6);
		txt_heading7 = (TextView)findViewById(R.id.txt_heading7);
		txt_heading8 = (TextView)findViewById(R.id.txt_heading8);
		term_checkBox=(CheckBox)findViewById(R.id.checkBox1);
		term_textView=(TextView)findViewById(R.id.textView1);
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);		
		btn_back.setOnClickListener(this);
		
		btn_otp=(Button)findViewById(R.id.btn_otp);
		btn_otp.setOnClickListener(this);
	
		imeiNo = MBSUtils.getImeiNumber(GetTermsCondition.this);
		
		Bundle bObj = getIntent().getExtras();
		if (bObj != null) {
			strCustId = bObj.getString("CUSTID");
			strFromAct = bObj.getString("FROMACT");
			strMobNo = bObj.getString("MOBNO");
			//strRetVal = bObj.getString("RETVAL");
		}
		
		String val[] = strRetVal.split("!!");
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btn_otp:
			
			if(!term_checkBox.isChecked())
			{
				showAlert(getString(R.string.alert_15009));
			}
			else
			{
				flag = chkConnectivity();
				if (flag == 0)
				{
					CallWebService callWebService=new CallWebService();
					callWebService.execute();
				}	
			}
			break;
		
		case R.id.btn_back:
			Intent in = new Intent(this, Register.class);
			in.putExtra("termflg", true);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		default:
			break;
		}

	}
	
	public void showAlert(final String str)
	{	
		ErrorDialogClass alert = new ErrorDialogClass(this,""+str)
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retval);
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


	public void onBackPressed() { 
		Intent mainIntent;
		mainIntent = new Intent(this, Register.class);
		mainIntent.putExtra("termflg", true);
		mainIntent.putExtra("var1", var1);
		mainIntent.putExtra("var3", var3);
		startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
		finish();
	}

	class CallWebService extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(GetTermsCondition.this);
	
		JSONObject jsonObj = new JSONObject();
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			
			try
			{
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("REQSTATUS","R");
				jsonObj.put("REQFROM", "MBSREG");
				jsonObj.put("MOBNO", strMobNo);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(GetTermsCondition.this));
				jsonObj.put("IMEINO",MBSUtils.getImeiNumber(GetTermsCondition.this));
				jsonObj.put("METHODCODE","27"); 
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			 String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "webServiceOne";

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
					isWSCalled=true;
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;


		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			if (isWSCalled) 
			{
			
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
   						retval = jsonObj.getString("RETVAL");
   					}
   					else
   					{
   						retval = "";
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
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}
				//int start =  xml_data[0].indexOf("SUCCESS");
   				if(respdesc.length()>0)
   				{
   					showAlert(respdesc);
   				}
   				else
   				{
					if(retval.split("~")[0].indexOf("SUCCESS")>-1)
					{
						
						post_success(retval);
					} 
					else 
					{
						//System.out.println("in else ***************************************");
						retMess = getString(R.string.alert_094);
						showAlert(retMess);
					}
				}
			} 
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp
	
	public 	void post_success(String retval)
	{
		respcode="";respdesc="";
		String decryptedAccounts = retval.split("~")[1];
		
		Bundle bObj=new Bundle();
		Intent in=new Intent(GetTermsCondition.this,OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",strCustId);
		bObj.putString("MOBNO",strMobNo);
		bObj.putString("FROMACT", strFromAct);
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		in.putExtras(bObj);
		startActivity(in);
		finish();
	}
	
	public int chkConnectivity() {
		//Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		//Log.i("2222", "2222");
		try {
			State state = ni.getState();
			//Log.i("3333", "3333");
			boolean state1 = ni.isAvailable();
			//Log.i("4444", "4444");
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:

					//Log.i("5555", "5555");
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						gpsFlg = 1;
						flag = 0;

					}
					break;
				case DISCONNECTED:
				//	Log.i("6666", "6666");
					flag = 1;
					// retMess = "Network Disconnected. Please Try Again.";
					retMess = getString(R.string.alert_000);
					dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
								}
							});
					dbs.get_adb().show();
					break;
				default:
				//	Log.i("7777", "7777");
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();

					dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
									Intent in = null;
									in = new Intent(getApplicationContext(),
											LoginActivity.class);
									in.putExtra("var1", var1);
									   in.putExtra("var3", var3);
									startActivity(in);
									finish();
								}
							});
					dbs.get_adb().show();
					break;
				}
			} else {
			//	Log.i("8888", "8888");
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();

				dbs = new DialogBox(this);
				dbs.get_adb().setMessage(retMess);
				dbs.get_adb().setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
								Intent in = null;
								in = new Intent(getApplicationContext(),
										LoginActivity.class);
								in.putExtra("var1", var1);
								   in.putExtra("var3", var3);
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
		}
		return flag;
	}
	
	
}
