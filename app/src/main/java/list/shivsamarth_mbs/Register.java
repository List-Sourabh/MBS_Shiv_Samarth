package list.shivsamarth_mbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import mbLib.CryptoClass;
import mbLib.DeviceUtils;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class Register extends Activity implements OnClickListener
{
	TextView txt_heading;
	Register regAct = this;
	ImageButton btn_home,btn_back;
	Button btn_Validate,btn_validateOTP;
	EditText txt_CustId,txt_MobileNo,txt_RefId,txt_OTP;
	String CustId,MobileNo,RefId,OTPVal;
	LinearLayout layout_ref_id,layout_OTP,layout_btn;
	ImageView img_heading;
	String retMess="";
	TextView custid;
	int flag = 0;
	DialogBox dbs;
	int netFlg, gpsFlg;
	int timeout = 5;
	String pref = "G";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	String respcode="",retval="",respdesc="";
	boolean termflg=false;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		if (!new DeviceUtils().isEmulator()) {
			MBSUtils.ifGooglePlayServicesValid(Register.this);
		} else {
			MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup),Register.this);
		}

		txt_CustId=(EditText)findViewById(R.id.txt_CustId);
		txt_MobileNo=(EditText)findViewById(R.id.txt_MobileNo);
		btn_Validate=(Button)findViewById(R.id.btn_Validate);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_012));
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		custid=(TextView)findViewById(R.id.custid);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.register);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		btn_Validate.setOnClickListener(this);
		custid.setOnClickListener(new View.OnClickListener()
		{  
			public void onClick(View v)
			{
				Log.e("onCreate==== ","onClick of custID");
		    	Intent mainIntent = new Intent(Register.this, GetCustID.class);
		    	mainIntent.putExtra("var1", var1);
				   mainIntent.putExtra("var3", var3);
		    	startActivity(mainIntent);
		    	finish();
	       }
		});
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.btn_back:			
		case R.id.btn_home:
			Intent in=new Intent(this,LoginActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		case R.id.btn_Validate:
			//Log.e("REGISTER","btn_Validate Clicked");
			//txtCustId=txt_CustId.getText().toString();
			//txtMobileNo=txt_MobileNo.getText().toString();
			boolean flag1=validateForm();
			//Log.e("REGISTER","===flag=="+flag1);
			if(flag1)
			{
				flag = chkConnectivity();
				if (flag == 0)
				{
					Log.e("Register", "Before WS Call");
					CallWebServiceValidateCustId c=new CallWebServiceValidateCustId();
					c.execute();
					Log.e("Register", "After WS Call");
				}
			}
			
			break;
			//else if(v.getId() == R.id.custid) 
		case R.id.custid:
			{
				custid.performClick();
			}
		}
	}

	public boolean validateForm()
	{
		CustId=txt_CustId.getText().toString().trim();
		MobileNo=txt_MobileNo.getText().toString().trim();
		if(CustId.length()!=10)
		{
			showAlert(getString(R.string.alert_001));
			return false;
		}
		else if(MobileNo.length()!=10)
		{
			showAlert(getString(R.string.alert_002));
			return false;
		}
	
		return true;
	}
	
	public void showAlert(final String str)
	{
		ErrorDialogClass alert = new ErrorDialogClass(this,""+str)
		{
			@Override
			public void onClick(View v)
 
			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success();
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if(str.equalsIgnoreCase(getString(R.string.login_alert_009)))
						{
							Intent in = new Intent(Register.this,ValidateSecQueActivity.class);
							Bundle b = new Bundle();
							b.putString("custId", CustId);
							b.putString("mpin", "");
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							in.putExtras(b);
							regAct.startActivity(in);
							regAct.finish();
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
			ok = (Button)findViewById(R.id.btn_ok);
			txt_message=(TextView)findViewById(R.id.txt_dia);
			txt_message.setText(textMessage);
			ok.setOnClickListener(this);		
			CustId=txt_CustId.getText().toString().trim();
		}//end onCreate

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
			
				case R.id.btn_ok:
					
					Log.e("btn_ok", "msg=="+textMessage.trim());
					Log.e("btn_ok", "msg11=="+getString(R.string.alert_007).trim());
					
					if(textMessage.trim().equalsIgnoreCase(getString(R.string.alert_007).trim()))
					{
						Log.e("in if","in if ok");
						//Intent in = new Intent(regAct,ValidateSecQueActivity.class);
						Bundle b = new Bundle();
						b.putString("custId", CustId);
						b.putString("mpin", "");
					//	in.putExtras(b);
					//	regAct.startActivity(in);
						regAct.finish();
					}
					else
					{
					this.dismiss();
					}
				  break;			
				default:
				  break;
			}
			dismiss();
		}
	}//end class
	
	public void onBackPressed() {
		DialogBox dbs = new DialogBox(this);
		dbs.get_adb().setMessage(getString(R.string.lbl_007));
		dbs.get_adb().setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//startActivity(lang_activity);
						finish();
						System.exit(0);
					}
				});
		dbs.get_adb().setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
		dbs.get_adb().show();
	}

	class CallWebServiceValidateCustId extends AsyncTask<Void, Void, Void> 
	{
		
		String CustId="",MobileNo="",retMess="",retVal="";
		int cnt=0;
		LoadProgressBar loadProBarObj=new LoadProgressBar(Register.this);
		
		String apptyp=getString(R.string.lbl_apptype);
		boolean isWSCalled = false;
		
	     
	     JSONObject jsonObj = new JSONObject();


		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			CustId=txt_CustId.getText().toString().trim();
			MobileNo=txt_MobileNo.getText().toString().trim();
			
			try 
			{					
				jsonObj.put("CUSTID", CustId);
				jsonObj.put("MOBILENO",MobileNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(Register.this));
				jsonObj.put("VERSION", "2");
				jsonObj.put("APPTYPE",apptyp);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(Register.this));
				jsonObj.put("METHODCODE","19"); 
			} 
			catch (JSONException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
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
					e.printStackTrace();
				}
				if(respdesc.length()>0)
				{
					showAlert(respdesc);
				}
				else
				{
				
				Log.e("retval=","retval="+retval);
				Log.e("retval=","XML[0]="+retval);
				
				if(retval.split("~")[0].indexOf("SUCCESS")>-1)
				{
					post_success();
				} 
				else
				{
					
					if(retval.indexOf("FAILED~")>-1)
					{
						String retVal= retval.split("FAILED~")[1];
						if(retVal.equalsIgnoreCase("DUPLICATE"))
						{
							showAlert(getString(R.string.alert_091));
						}
						else if(retVal.equalsIgnoreCase("INVALIDCUSTOMER") || retVal.equalsIgnoreCase("INVALIDMOBILENO"))
						{
							showAlert(getString(R.string.alert_regi_fail));
						}
						else if (retVal.indexOf("INVALID USER") >= 0) 
						{
							showAlert(getString(R.string.alert_regi_fail));
						}
						else if (retVal.indexOf("UNSUBSCRIBED") >= 0) 
						{
							showAlert(getString(R.string.alert_regi_fail_sub));
						}
	                    else if (retVal.indexOf("INVALIDTYPE") >= 0) 
						{
							showAlert(getString(R.string.alert_regi_fail_type));
						}
	                    else if (retVal.indexOf("DIFFIMEI") >= 0)
						{
							retMess=getString(R.string.login_alert_009);
							showAlert(getString(R.string.login_alert_009));
							
							/*if(retMess == getString(R.string.login_alert_009))
							{
								Intent in = new Intent(Register.this,ValidateSecQueActivity.class);
								Bundle b = new Bundle();
								b.putString("custId", CustId);
								b.putString("mpin", "");
								in.putExtras(b);
								regAct.startActivity(in);
								regAct.finish();
							}*/
						}
						else
						{
							showAlert(getString(R.string.alert_err));
						}
					}
					else
					{
						showAlert(getString(R.string.alert_err));
					}					
				}
			} 
			}
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}
	
	public 	void post_success()
	{
		String respcode="",respdesc="";
		Bundle bObj=new Bundle();
		Intent in=new Intent(Register.this,GetTermsCondition.class);
		//bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",CustId);
		bObj.putString("MOBNO",MobileNo);
		bObj.putString("FROMACT", "REGISTER");
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		in.putExtras(bObj);
		startActivity(in);
		finish();
	}
	
	public void registration()
	{
		
		
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
	
	/*class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {

		String[] xmlTags = { "CUSTID","OTPVAL","REFID","IMEINO" };
		String[] valuesToEncrypt = new String[4];
		// LoadProgressBar loadProBarObj=new LoadProgressBar(this);
		
		LoadProgressBar loadProBarObj = new LoadProgressBar(Register.this);
		String generatedXML = "";
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();

			// p_wait.setVisibility(ProgressBar.VISIBLE);
			CustId=txt_CustId.getText().toString();
			RefId=txt_RefId.getText().toString();
			OTPVal=txt_OTP.getText().toString();
			
			//Log.i("IN onPreExecute()", "CustId :" + CustId);
			//Log.i("IN onPreExecute()", "MobileNo :" + MobileNo);
			//Log.i("IN onPreExecute()", "imeiNo :" + imeiNo);

			valuesToEncrypt[0] = CustId;
			valuesToEncrypt[1] = OTPVal;
			valuesToEncrypt[2] = RefId;
			valuesToEncrypt[3] = MBSUtils.getImeiNumber(Register.this);

			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);

			Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			NAMESPACE=getString(R.string.namespace);
			URL=getString(R.string.url);
			SOAP_ACTION=getString(R.string.soap_action);
			// p_wait.setVisibility(ProgressBar.VISIBLE);

			int i = 0;
			Log.i("Shrikant", "in doInBackground()");
			try {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				request.addProperty("para_value", generatedXML);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				String status = "";
				try 
				{
					Log.e("Shrikant", "111");
					//androidHttpTransport.call(SOAP_ACTION, envelope);
					Log.e("Shrikant", "222");
					//System.out.println(envelope.bodyIn.toString());
					Log.e("Shrikant", "333");
					//status = envelope.bodyIn.toString().trim();
					Log.i("Shrikant status", status);
					//retVal = status;
					retVal="SUCCESS~";
					Log.e("Return value IN BACKGROUND () FUNCTION:", retVal);
					//int pos = envelope.bodyIn.toString().trim().indexOf("=");
					//if(pos>-1)
					{
						//status = status.substring(pos + 1, status.length() - 3);
						//retVal = status;
						isWSCalled = true;
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Log.e("In Login", "----------" + e);
					// retMess = "Problem in login. Some error occured";
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
					// return "FAILED";
				}
			} 
			catch (Exception e) 
			{
				// retMess = "Error Getting IMEI NO";
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
				// return "FAILED";
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			if (isWSCalled) 
			{
				//String[] xmlTags = { "ACCOUNTS" };

				int start =  retVal.indexOf("SUCCESS");
				System.out.println("start:" + start);

				Intent intent = null;

				if (start >= 0) 
				{

					//String[] xml_data = CryptoUtil.readXML(retVal.split("#")[1], xmlTags);
					//System.out.println("xml_data.len :" + xml_data.length);
					//String decryptedAccounts = xml_data[0];					
					
					if(retVal.split("~")[0].indexOf("SUCCESS")>-1)
					{
						showAlert(getString(R.string.alert_077));
						Intent in =new Intent(Register.this, SecurityQuestion.class);
						startActivity(in);
						finish();
						String decryptedAccounts = retVal.split("~")[1];
						Log.e("In Login", "decryptedAccounts,,,,, :"+ decryptedAccounts);
						
						String val[]=decryptedAccounts.split("!!");
						Log.e("REGISTER","val[0]==="+val[0]);
						Log.e("REGISTER","val[1]==="+val[1]);
						Log.e("REGISTER","val[2]==="+val[2]);
						layout_ref_id.setVisibility(layout_ref_id.VISIBLE);
						layout_OTP.setVisibility(layout_OTP.VISIBLE);
						layout_btn.setVisibility(layout_btn.VISIBLE);
						//String str="OTP has sent to your registered mobile with reference id :"+val[1];
						showAlert(getString(R.string.alert_074)+val[1]);
						txt_RefId.setText(""+val[1]);
						
						//txt_OTP.setText("");
					}
					else
					{
						showAlert(getString(R.string.alert_076));
					}

					if (!decryptedAccounts.equals("FAILED#")) 
					{
						Bundle b = new Bundle();
						// add data to bundle

						System.out.println("in if ***************************************");
						System.out.println("decryptedAccounts :"
								+ decryptedAccounts);

						String splitstr[] = decryptedAccounts.split(",");
						System.out.println("splitstr.len :" + splitstr.length);
						//String accounts = decryptedAccounts.split("~")[1];
						String accounts = splitstr[0];
						
						//System.out.println("accounts :" + accounts);
						Log.e("accounts :", accounts);
						String mobno =  splitstr[1];

						System.out.println("mobno :" + mobno);
					}
					else
					{
						retMess = getString(R.string.alert_prblm_login);
						showAlert(retMess);
					}
					
				} 
				else 
				{
					System.out.println("in else ***************************************");
					if (retVal.indexOf("INVALID USER") >= 0) 
					{
						retMess = getString(R.string.alert_login_fail);
					}
					else
					{
						retMess = getString(R.string.alert_prblm_login);
					}
					showAlert(retMess);
				}
			} 
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}

	}*/

}

