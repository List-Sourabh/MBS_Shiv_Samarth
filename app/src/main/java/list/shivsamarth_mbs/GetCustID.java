package list.shivsamarth_mbs;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.ConnectionDetector;
import mbLib.CryptoClass;
import mbLib.MBSUtils;
import mbLib.MyThread;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GetCustID extends Activity implements OnClickListener {

	MainActivity act;
	GetCustID getcustID;
	Button btn_proceed;
	EditText txt_accno, txt_mobileno;
	ImageView img_heading;
	String accno = "", mobno = "", retMess = "",respcode="",retvalweb="",getcustidrespdesc="",sendotprespdesc="";
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	private static String NAMESPACE = "";
	private static String URL = ""; //
	private static String SOAP_ACTION = ""; 
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	private static String responseJSON = "NULL";
	Intent mainIntent;
	TextView txt_heading;
	String[] presidents;
	static String imeiNo;
	ImageButton btn_back;
	String custid = "";
	int flag = 0;
	String retVal = "";
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	// private Context _context;
	TelephonyManager telephonyManager;

	public GetCustID() {
	}

	public GetCustID(MainActivity a) {
		System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		getcustID = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");

		setContentView(R.layout.customerid);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.register);
		txt_accno = (EditText) findViewById(R.id.txt_accno);
		txt_mobileno = (EditText) findViewById(R.id.txt_mobileno);
		btn_proceed = (Button) findViewById(R.id.btn_proceed);
		btn_proceed.setOnClickListener(this);
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_021));
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);		
		btn_back.setOnClickListener(this);
		cd = new ConnectionDetector(getApplicationContext());
		presidents = getResources().getStringArray(R.array.Errorinwebservice);

		imeiNo = MBSUtils.getImeiNumber(GetCustID.this);
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btn_proceed:
			Log.e("=====", "111111111");
			accno = txt_accno.getText().toString().trim();
			mobno = txt_mobileno.getText().toString().trim();
			if (accno.length() == 0) {
				showAlert(getString(R.string.alert_008));
			}
			if (accno.length() < 16) {
				showAlert(getString(R.string.alert_009));
			} else if (mobno.length() == 0) {
				showAlert(getString(R.string.alert_010));
			} else if (mobno.length() > 15 || mobno.length() < 10) {
				// showAlert(getString(R.string.alert_mobileno_len_min10_max15));
				showAlert(getString(R.string.alert_011));
			} else {
				Log.e("=====", "22222222222222222");
				flag = chkConnectivity();
				if (flag == 0)
				{
						Log.e("Customer", "Before WS Call");
						CallWebServiceGetCustID C = new CallWebServiceGetCustID();
						C.execute();
						Log.e("Customer", "After WS Call");
				}
				else
				{
						
						//showAlert(getString(R.string.alert_000));
				}
			}
			break;
		case R.id.btn_back:
			Intent in = new Intent(this, Register.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		default:
			break;
		}

	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(GetCustID.this, "" + str)
		{@Override
			public void onClick(View v)

			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(getcustidrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successCallWebServiceGetCustID(retvalweb);
						}
						else if((str.equalsIgnoreCase(getcustidrespdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(sendotprespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successCallWebServiceSendOTP(retvalweb);
						}
						else if((str.equalsIgnoreCase(sendotprespdesc)) && (respcode.equalsIgnoreCase("1")))
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
		mainIntent = new Intent(GetCustID.this, Register.class);
		mainIntent.putExtra("var1", var1);
		mainIntent.putExtra("var3", var3);
		startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
		finish();
	}

	public class CallWebServiceGetCustID extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(GetCustID.this);
		JSONObject jsonobj = new JSONObject();

		@Override
		protected void onPreExecute() {
			Log.e("CallWebServiceGetCustID ","onPreExecute ");
			loadProBarObj.show();
			
			
		
			try {
				jsonobj.put("ACCNO", accno);
				jsonobj.put("MOBNO", mobno);
				jsonobj.put("IMEI", imeiNo);
				jsonobj.put("SIMNO", MBSUtils.getSimNumber(GetCustID.this));
				jsonobj.put("METHODCODE","50"); 
				Log.e("onPreExecute= ","accno=="+accno);
				Log.e("onPreExecute= ","mobno=="+mobno);
				Log.e("onPreExecute= ","imeiNo=="+imeiNo);
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.e("CallWebServiceGetCustID ","doInBackground ");
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";

			try 
			{
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1", CryptoClass.Function5(jsonobj.toString(), var2));
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
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.e("Registration ", "in onPostExecute() responseJSON :"
					+ responseJSON);

			// intent = new Intent(thisObj, ChooseMpin.class);
   
			loadProBarObj.dismiss();

			
					
					
					JSONObject jsonObj;
					try
					{
		
						String str=CryptoClass.Function6(var5,var2);
						Log.e("str getcustid", "==========="+str.trim());
						jsonObj = new JSONObject(str.trim());
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
							getcustidrespdesc = jsonObj.getString("RESPDESC");
						}
						else
						{	
							getcustidrespdesc = "";
						}
					} catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(getcustidrespdesc.length()>0)
					{
						showAlert(getcustidrespdesc);
					}
					else
					{
						if (respcode.equals("0")) 
						{
							post_successCallWebServiceGetCustID(retvalweb);
					} else if (!respcode.equals("0")) {
						showAlert(getString(R.string.alert_unable_to_getcustid));
					} else {
						if (!retvalweb.equalsIgnoreCase("NULL")) {
							String RESPREASON = getcustidrespdesc;//json.getString("RETVAL");
							int pos = Integer.parseInt(respcode);
							String errmsg = presidents[pos];
							Log.e("IN Choose MPin", errmsg);
							showAlert("" + errmsg);
						} else {
							showAlert(getString(R.string.alert_network_problem_pease_try_again));
						}
					}
					}

		}// onPostExecute

	}
	
	public 	void post_successCallWebServiceGetCustID(String retvalweb)
	{
		respcode="";getcustidrespdesc="";
		custid = retvalweb;//json.getString("CUSTID");
		Log.e("onPostExecute========", "CUSTID== " + custid);
		CallWebServiceSendOTP c = new CallWebServiceSendOTP();
		c.execute();
	}

	class CallWebServiceSendOTP extends AsyncTask<Void, Void, Void>
	{// CallWebService_resend_otp
		LoadProgressBar loadProBarObj = new LoadProgressBar(GetCustID.this);
		int cnt = 0, flag = 0;
		
	
	      
	     JSONObject jsonObj = new JSONObject();

	
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			try
			{
				Log.e("CallWebServiceSendOTP", "onPreExecute Customer ID" + custid);
				loadProBarObj.show();
		
				jsonObj.put("CUSTID", custid);
				jsonObj.put("REQSTATUS", "O");
				jsonObj.put("REQFROM", "MBSREG");
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(GetCustID.this));
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(GetCustID.this));
				jsonObj.put("METHODCODE","26");  
	
			}  catch (JSONException je) {
                je.printStackTrace();
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
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			Log.e("CallWebServiceSendOTP", "onPostExecute");
			if (isWSCalled) {
			//	String[] xmlTags = { "STATUS" };
	
				JSONObject jsonObj;
				try
				{
	
					String str=CryptoClass.Function6(var5,var2);
					Log.e("strgetcust","------------"+str.trim());
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
						sendotprespdesc = jsonObj.getString("RESPDESC");
					}
					else
					{	
						sendotprespdesc = "";
					}
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(sendotprespdesc.length()>0)
				{
					showAlert(sendotprespdesc);
				}
				else{
				
				
				if (retvalweb.split("~")[0].indexOf("SUCCESS") > -1) {
				
					post_successCallWebServiceSendOTP(retvalweb);
					
				} else {
					// System.out.println("in else ***************************************");
					retMess = getString(R.string.alert_094);
					showAlert(retMess);
				}
				}
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp
	
	public 	void post_successCallWebServiceSendOTP(String retvalweb)
	{
		respcode="";
		sendotprespdesc="";
		String decryptedAccounts = retvalweb.split("~")[1];
		Bundle bObj = new Bundle();
		Intent in = new Intent(GetCustID.this, OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID", custid);
		// bObj.putString("MOBNO",strMobNo);
		bObj.putString("FROMACT", "GetCustID");
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
		in.putExtras(bObj);
		startActivity(in);
		finish();
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// System.out.println("============= inside chkConnectivity 1 ================== ");
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("BalanceEnquiry	in chkConnectivity () state1 ---------"
							+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

					}
					break;
				case DISCONNECTED:
					flag = 1;
					// ////////retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					
					break;
				default:
					flag = 1;
					// //////retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);

					break;
				}
			} else {
				flag = 1;
				// ////retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				showAlert(retMess);

			}
		} catch (NullPointerException ne) {

			Log.i("BalanceEnquiry    mayuri", "NullPointerException Exception"
					+ ne);
			flag = 1;
			// ///////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			// ///////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

			
		}
		System.out
				.println("========================= end chkConnectivity ==================");
		return flag;
	}// end chkConnectivity
	
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
