package list.shivsamarth_mbs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DeviceUtils;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class LoginActivity extends CustomWindow implements OnClickListener,LocationListener 
{
	Button btnLogin;
	EditText et_custid, et_mpin;
	LoginActivity loginAct = this;
	ImageButton cntus,locus;
	String imeiNo = "", tmpXMLString = "", retMess = "",respdescvalidate="",strOTP="",newMpin="",strMobNo="",retvalotp="",respdescresend="",strRefId="",retvalvalidate="";
	private String userid,custname,retvalstr;
	TelephonyManager telephonyManager;
	TextView txt_register,txt_forgot_pass;
	EditText txt_mpin1,txt_mpin2,txt_mpin3,txt_mpin4,txt_mpin5,txt_mpin6;
	int cnt = 0, flag = 0;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	String respdesc="",respcode="",retvalweb="";
	String retVal = "", encrptdMpin="",userId="";
	DialogBox dbs;
	CustomDialogClass cdc;
	
	int netFlg, gpsFlg,expdt;
	int timeout = 5;
	String pref = "G";
	String version="";

	private static final String MY_SESSION = "my_session";
	Editor e;
	public LocationManager locManager;
	public BatteryManager batteryManager;
	ImageView imageViewLogo;
	DatabaseManagement dbms;
	TextView tv_bankname;
	Cursor curSelectBankname;
	public String custid,customerId="";
	public String mpin;
	public String tranMpin,custId="",custnm="",usernm="",lastLogin="";
	private String mobNo;
	boolean custIdFlg=false,isWsCallSuccess=false;;
	String splitstr[];
	public String decryptedAccounts,strexpdate;
	PrivateKey var1 = null;
	String var5 = "", var3 = "",osVersion;
	SecretKeySpec var2 = null;
	boolean isBelowTen=true;
	public void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		Log.e("LoginActivity onCreate", "1111111");
		//setContentView(R.layout.login);
		setContentView(R.layout.login);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		if (!new DeviceUtils().isEmulator()) {
			MBSUtils.ifGooglePlayServicesValid(LoginActivity.this);
		} else {
			MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup),LoginActivity.this);
		}
		System.out.println("222222");
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
		if(var1.toString().length()==0||var3.toString().length()==0)
		{
			showAlert(getString(R.string.alert_restartapp));
		}
		Log.e("var1", var1.toString());
		Log.e("var3", var3.toString());
		//DatabaseManagement dbms;
		
		
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		customerId=getCustId();
		Log.e("onCreate","customerId== "+customerId);
		//setContentView(R.layout.login);
		et_custid = (EditText) findViewById(R.id.etCustId);
		et_mpin = (EditText) findViewById(R.id.etMpin);
		btnLogin = (Button) findViewById(R.id.button1);
		txt_register=(TextView)findViewById(R.id.txt_register);
		txt_forgot_pass=(TextView)findViewById(R.id.txt_forgot_pass);
		cntus = (ImageButton) findViewById(R.id.contactus);
		locus = (ImageButton) findViewById(R.id.locateus);
		cntus.setOnClickListener(this);
		locus.setOnClickListener(this);
		
		btnLogin.setOnClickListener(this);
		txt_register.setOnClickListener(this);
		txt_forgot_pass.setOnClickListener(this);
		
		imeiNo=MBSUtils.getImeiNumber(LoginActivity.this);
		String macadd=MBSUtils.getMacAddressnew("eth0",LoginActivity.this);//getMacAddress(LoginActivity.this);
		String macadd1=MBSUtils.getMacAddressnew("wlan0",LoginActivity.this);
		Log.e("mac adrress",macadd );
		Log.e("mac adrress",macadd1 );
		//mobNo=telephonyManager.getLine1Number();
		dbs = new DialogBox(this);

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		try
		{
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
			Log.e("PackageInfo","PackageInfo"+version);
			Log.e("PackageInfo","PackageInfo"+version);
			Log.e("PackageInfo","PackageInfo"+version);
			Log.e("PackageInfo","PackageInfo"+version);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        	
        		custId=c1.getString(2);
        		strMobNo=c1.getString(4);
	        	Log.e("custId","......"+custId);
	        }
        }
		if(customerId.length()>0)
        et_custid.setText(customerId);
		
		createSharedPrefTable();		
	}
	
	public void createConfigTable() {// createConfigTable
		String sts = "";
		String val[] = { "conf_bankname", "varchar(60)" };
	}// createConfigTable

	public void insertToCONFIG(String bankname) {// insertToCONFIG
		String[] columnNames = { "conf_bankname" };
		String[] columnValues = { bankname };
	}// insertToCONFIG

	private void setBankName() 
	{
		int flag = 0;
		try {
			while (curSelectBankname.moveToNext()) {
				//tv_bankname.setText(curSelectBankname.getString(0));
				flag = 1;
			}
			curSelectBankname.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("EXCEPTION", "------------------"+e);
		}
		if (flag == 0) {
			//tv_bankname.setText(getString(R.string.lbl_ideal_mobile_banking));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent in;
		switch (v.getId()) 
		{
		
		case R.id.txt_register:
			in=new Intent(this,Register.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		case R.id.txt_forgot_pass:
			in = new Intent(loginAct,ForgotPassword.class);
			Bundle b = new Bundle();
			b.putString("FROMACT", "FORGOT");
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			in.putExtras(b);
			loginAct.startActivity(in);
			loginAct.finish();
			break;
		case R.id.button1:

			String strCustId = et_custid.getText().toString().trim();
			boolean isNumeric;
			try
			{
				long t=Long.parseLong(strCustId);
				isNumeric=true;
			}
			catch (Exception e) {
				// TODO: handle exception
				isNumeric=false;
			}
			String strMpin = et_mpin.getText().toString().trim();
			
			if(strCustId.length()!=10 && isNumeric)
			{
				retMess = getString(R.string.alert_login_fail);
				setAlert();
			}
			else if(strMpin.length()!=6)
			{	
				retMess = getString(R.string.alert_login_fail);
				setAlert(); 
			}
			else
			{
				newMpin=strMpin;
				Log.e("onClick button1","newMpin = "+strMpin);
				Log.e("onClick button1","newMpin = "+strMpin);
				Log.i("MBS", "login btn clicked...");
				flag = chkConnectivity();
				if (flag == 0)
				{
					CallWebService C = new CallWebService();
					C.execute();
				}
			}
			break;
		
		case R.id.contactus:
			in=new Intent(loginAct,ContactUs.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			startActivity(in);
			loginAct.finish();
			break;
			
		case R.id.locateus:
			in=new Intent(loginAct,LocateUs.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			startActivity(in);
			loginAct.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		// app.app_onDestroy();
		super.onDestroy();
		System.out.println("MObile bank login - in onDestroy()");
		System.out.println("MObile bank login - in onDestroy()");
		System.out.println("MObile bank login - in onDestroy()");
		System.out.println("MObile bank login - in onDestroy()");

	}
	
	public void onBackPressed() 
	{
		showlogoutAlertbtn(getString(R.string.lbl_007));
		/*DialogBox dbs = new DialogBox(this);
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
		dbs.get_adb().show();*/
	}
	public void showlogoutAlertbtn(final String str)
	{
		CustomDialogClass alert = new CustomDialogClass(LoginActivity.this,str)
		{
			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						finish();
						System.exit(0);
						break;

					case R.id.btn_cancel:

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
	public int chkConnectivity() 
	{
		Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		Log.i("2222", "2222");
		try {
			State state = ni.getState();
			Log.i("3333", "3333");
			boolean state1 = ni.isAvailable();
			Log.i("4444", "4444");
			System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:

					Log.i("5555", "5555");
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						gpsFlg = 1;
						flag = 0;

					}
					break;
				case DISCONNECTED:
					Log.i("6666", "6666");
					flag = 1;
					// retMess = "Network Disconnected. Please Try Again.";
					/*retMess = getString(R.string.alert_000);
					dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
								}
							});
					dbs.get_adb().show();*/
					break;
				default:
					Log.i("7777", "7777");
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					/*retMess = getString(R.string.alert_000);
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
									startActivity(in);
									finish();
								}
							});
					dbs.get_adb().show();*/
					break;
				}
			} else {
				Log.i("8888", "8888");
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				/*retMess = getString(R.string.alert_000);
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
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();*/
			}
		} catch (NullPointerException ne) {

			Log.e("EXCEPTION", "------------------"+ne);
			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			/*retMess = getString(R.string.alert_000);
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
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();*/

		} catch (Exception e) {
			Log.e("EXCEPTION", "------------------"+e);
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			/*retMess = getString(R.string.alert_000);
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
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();*/
		}
		return flag;
	}

	class CallWebService extends AsyncTask<Void, Void, Void> 
	{
		
		LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
	
		boolean isWSCalled = false,isWsCallSuccess=false;
		JSONObject obj=new JSONObject();
		@Override
		protected void onPreExecute() {    
			
			loadProBarObj.show();
			custid = et_custid.getText().toString().trim();
			mpin=newMpin;
			Log.e("IN onPreExecute()", "custid :" + custid);
			Log.e("IN onPreExecute()", "mpin :" + mpin);
			Log.e("IN onPreExecute()", "imeiNo :" + imeiNo);

			encrptdMpin=mpin;//ListEncryption.encryptData(custid+mpin);
			//custid=custid;
			
			try {
				String location = MBSUtils.getLocation(LoginActivity.this);
				obj.put("CUSTID", custid + "~#~" + version);
				obj.put("MPIN", encrptdMpin);
				obj.put("IMEINO", imeiNo);// + "~" + mobNo
				obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
				obj.put("MOBILENO",
						MBSUtils.getMyPhoneNO(LoginActivity.this));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("REQSTATUS", "R");
				obj.put("REQFROM", "MBS");
				obj.put("METHODCODE","1");    
		Log.e("===========", "login==========="+obj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
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
					Log.e("login=", "after===="+str);
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
						respdesc = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdesc = "";
					}
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("retval","respdesc---------1-"+respdesc);
				if(respdesc.length()>0)
				{
					showAlert(respdesc);
				}
				else
				{
					Log.e("retval","retval---------1-"+retvalweb);
					if (retvalweb.indexOf("SUCCESS") >-1) 
					{
						post_success(retvalweb);
					} 
	            	else 
					{
						System.out.println("in else ***************************************");
						if(retvalweb.indexOf("~")==-1)
						{
							retMess = getString(R.string.alert_network_problem_pease_try_again);
						}
						else
						{
							String msg[] = retvalweb.split("~");
					
							Log.e("Check 1","msg[0]===="+msg[0]);
							Log.e("Check 2","msg[1]===="+msg[1]);
							
							if(msg[1].equals("1"))
							{
								retMess = getString(R.string.login_alert_009);
							}
							else if(msg[1].equals("2"))
							{
								retMess = getString(R.string.login_alert_002);		
								custId=msg[3];
								userId=msg[2];
							}
							else if(msg[1].equals("3"))
								retMess = getString(R.string.login_alert_003);
							else if(msg[1].equals("4"))
								retMess = getString(R.string.alert_login_fail);
							else if(msg[1].equals("5"))
								retMess = getString(R.string.login_alert_005);
							else if(msg[1].equals("6"))
								retMess = getString(R.string.login_alert_006);
							else if(msg[1].equals("7"))
							{
								Log.e("alskdaksjdlaksjd","1231892731782");
								retMess = getString(R.string.login_alert_007);						
							}
							else if(msg[1].equals("8"))
							{
								retMess = getString(R.string.login_alert_008);
							}
							else if(msg[1].equals("9"))
								retMess = getString(R.string.alert_login_fail);	
							
							/*else if(msg[1].indexOf("OLDVERSION")>-1)
								retMess = getString(R.string.alert_oldversionupdate);	*/
						}
						setAlert();
					}
				} 
			}
			else 
			{
				retMess = getString(R.string.alert_000);
				setAlert();
			}
		}

	}
	
	public void post_successRetvalfailed(String retvalwebg){
		//Log.e("In post_successRetvalfailed", "post_successRetvalfailed,,,,, :"
		//		+retvalwebg);
		if(retvalwebg.indexOf("NODATA")>-1){
			retMess = "No Operative Account Found";
			setAlert();
			}
		/*else if(retvalwebg.indexOf("OLDVERSION")>-1){
			retMess = getString(R.string.alert_oldversion);
			setAlert();}*/
	}
	
	public void post_success(String retvalwebg)
	{
		respcode="";
		respdesc="";
		
		System.out.println("xml_data.len :" + retvalwebg);

		if (retvalwebg.split("~").length >= 3) {
			if (retvalwebg.split("~")[2].equalsIgnoreCase("Y")) {
				flag = chkConnectivity();
				if (flag == 0) {
					custId = retvalwebg.split("~")[3];
					new CallGenerateOTPWebService().execute();
				}

			} else {
				LodandSave(retvalwebg);
			}
		} else {
			LodandSave(retvalwebg);
		}
	}
	
	class CallGenerateOTPWebService extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
		
		boolean isWSCalled = false;
	
	    JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() 
		{ 
			loadProBarObj.show();
			//ValidationData=MBSUtils.getValidationData(logAct);

			try
			{
				jsonObj.put("CUSTID", custId);
				jsonObj.put("REQSTATUS","R");
				jsonObj.put("REQFROM", "MBSL");
				jsonObj.put("MOBNO", strMobNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(LoginActivity.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
				jsonObj.put("METHODCODE","26");  
				

				METHOD_NAME1 = "mbsInterCall";
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
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
				isWSCalled = true;
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
							retvalotp = jsonObj.getString("RETVAL");
						}
						else
						{
							retvalotp = "";
						}
						if (jsonObj.has("RESPDESC"))
						{
							respdescresend = jsonObj.getString("RESPDESC");
						}
						else
						{	
							respdescresend = "";
						}
	            	
	            
				if(respdescresend.length()>0)
				{
					showAlert(respdescresend);
				}
				else{
				
				if(retvalotp.split("~")[0].indexOf("SUCCESS")>-1)
				{
	            	post_successresend(retvalotp);
				} 
				else 
				{
					retMess = LoginActivity.this.getString(R.string.alert_094);
					showAlert(retMess);
				}}
				
	            	
				} 
	            catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				retMess = LoginActivity.this.getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp
	
	 public void post_successresend(String retvalstr)
	    {
	    	respdescresend="";
	    	respcode="";
		String returnstr = retvalstr.split("~")[1];
		String val[] = returnstr.split("!!");
		
		 strRefId=val[2];
		String fromact="LOGIN";
		
		InputDialogBoxotp inputBox = new InputDialogBoxotp(LoginActivity.this);
		inputBox.show();
		
		
		
	}
	 
	 public class InputDialogBoxotp extends Dialog implements OnClickListener {
			Activity activity;
			Button submit,resennd;
			TextView txt_ref_id; 
			 EditText txt_otp;
			String textMessage,fromact,retstr;
			boolean flg;

			public InputDialogBoxotp(Activity activity) {
				super(activity);
			}// end InputDialogBox

			protected void onCreate(Bundle bdn) {
				super.onCreate(bdn);
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.otplogin);
				submit = (Button)findViewById(R.id.btn_otp_submit);
				resennd = (Button)findViewById(R.id.btn_otp_resend);
				txt_ref_id=(TextView)findViewById(R.id.txt_ref_id);
				txt_otp=(EditText)findViewById(R.id.txt_otp);
				
				txt_ref_id.setText(txt_ref_id.getText().toString() + " :" + strRefId);
				submit.setOnClickListener(this);
				resennd.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {
				try {

					switch (v.getId()) 
					{
						case R.id.btn_otp_submit:
							strOTP=txt_otp.getText().toString();
							flag = chkConnectivity();
							if (strOTP.length() == 0) {
								retMess = LoginActivity.this.getString(R.string.alert_076);
								showAlert(retMess);
								this.show();
							}/* else if (strOTP.length() != 6) {
								retMess = LoginActivity.this.getString(R.string.alert_075);
								showAlert(retMess);// setAlert();
								this.show();
							} */else {
								if (flag == 0)
								{
									new CallWebServiceValidateOTP().execute();
								}
								
							}
							
						  break;	
						  
						case R.id.btn_otp_resend:
							flag = chkConnectivity();
							if (flag == 0)
							{
								new CallGenerateOTPWebService().execute();
								this.dismiss();
							}
							
							
							break;
						default:
						  break;
					}
					//dismiss();
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("Exception in InputDialogBox of onClick:=====>"
									+ e);
				}
			}// end onClick
		}// end InputDialogBox
	 
	 class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> 
     {		
	
    JSONObject jsonObj = new JSONObject();
	LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
	
	boolean isWSCalled = false;

	@Override
	protected void onPreExecute() {  
		loadProBarObj.show();
	try
		{
			jsonObj.put("CUSTID", custId);
			jsonObj.put("OTPVAL",strOTP);// ListEncryption.encryptData(strOTP+custId));
			jsonObj.put("IMEINO", MBSUtils.getImeiNumber(LoginActivity.this));
			jsonObj.put("REFID", strRefId);
			jsonObj.put("ISREGISTRATION", "N");
			jsonObj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
			jsonObj.put("METHODCODE","20");  
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	};

	@Override
	protected Void doInBackground(Void... arg0) 
	{
		String value4 = getString(R.string.namespace);
		String value5 = getString(R.string.soap_action);
		String value6 = getString(R.string.url);
		final String value7 = "webServiceTwo";

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
			isWSCalled = true;
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
			{String str=CryptoClass.Function6(var5,var2);
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
						retvalvalidate = jsonObj.getString("RETVAL");
					}
					else
					{
						retvalvalidate = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdescvalidate = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescvalidate = "";
					}
				
				
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
			if(respdescvalidate.length()>0)
			{
				showAlert(respdescvalidate);
			}
			else
			{
				if (retvalvalidate.indexOf("SUCCESS")>-1) 
				{				
					post_successvalidate(retvalvalidate);
				} 
				else if(retvalvalidate.indexOf("FAILED~MAXATTEMPT")>-1)
				{
					retMess = getString(R.string.alert_076_1);
					showAlert(retMess);
				}
				else 
				{
					showAlert(getString(R.string.alert_076));
				}
			}
		}
		else
		{
			showAlert(getString(R.string.alert_000));
		}
	}

}
	 
	 public 	void post_successvalidate(String retval)
	    {

		respdescvalidate="";
		respcode="";
		String decryptedAccounts = retval;//xml_data[0];
		
		flag = chkConnectivity();
		if (flag == 0)
		{
			new CallfetchaccWebService().execute();			
		}
	}
	 
	 class CallfetchaccWebService extends AsyncTask<Void, Void, Void> {

			
			
			// LoadProgressBar loadProBarObj=new LoadProgressBar(this);
			LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
			
			boolean isWSCalled = false,isWsCallSuccess=false;
			JSONObject obj=new JSONObject();
			/*
			 * Key key_CustID,keyForMAC; String
			 * encodedCustIDStr,encodedCustKeyStr,encodedCustMAC,encodedCustMACKey;
			 */
		@Override
			protected void onPreExecute() {    
				loadProBarObj.show();
				respcode="";
				retvalweb="";
				respdesc="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);

				custid = et_custid.getText().toString().trim();
			//	mpin = et_mpin.getText().toString().trim();
				mpin=newMpin;	
				Log.e("IN onPreExecute()", "custid :" + custid);
				Log.e("IN onPreExecute()", "mpin :" + mpin);
				Log.e("IN onPreExecute()", "imeiNo :" + imeiNo);

				encrptdMpin=mpin;//ListEncryption.encryptData(custid+mpin);
				//custid=custid;
	                 
				try {
					String location=MBSUtils.getLocation(LoginActivity.this);
					Log.e("SIMNO-- ",MBSUtils.getSimNumber(LoginActivity.this));
					Log.e("MOBILENO-- ",MBSUtils.getMyPhoneNO(LoginActivity.this));
					Log.e("IPADDRESS-- ",MBSUtils.getLocalIpAddress());
					Log.e("OSVERSION-- ",Build.VERSION.RELEASE);
					Log.e("LATITUDE-- ",location.split("~")[0]);
					Log.e("LONGITUDE-- ",location.split("~")[1]);
					obj.put("CUSTID", custid+"~#~"+version);
					obj.put("MPIN", encrptdMpin);
					obj.put("IMEINO", MBSUtils.getImeiNumber(LoginActivity.this));
					obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
					//obj.put("VALIDATIONDATA", ValidationData);
					obj.put("MOBILENO", MBSUtils.getMyPhoneNO(LoginActivity.this));
					obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
					obj.put("OSVERSION", Build.VERSION.RELEASE);
					obj.put("LATITUDE", location.split("~")[0]);
					obj.put("LONGITUDE", location.split("~")[1]);
					obj.put("REQSTATUS", "R");
					obj.put("REQFROM", "MBS");
					obj.put("METHODCODE","54"); 
					
					//Log.e("ValidationData-- ",ValidationData);
		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);

				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				var5 = var5.substring(var5.indexOf("=") + 1,var5.length() - 3);
				isWSCalled = true;
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
	               
					//Log.e("OMG ERRO HERE",xml_data[1]);
					
				
					JSONObject jsonObj;
					try
					{
						String str=CryptoClass.Function6(var5,var2);
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
							respdesc = jsonObj.getString("RESPDESC");
						}
						else
						{	
							respdesc = "";
						}
					
					
					if(respdesc.length()>0)
					{
						showAlert(respdesc);
					}
					else
					{
						Log.e("retvalreturn ","retval----------"+retvalweb);
						if (retvalweb.indexOf("SUCCESS") >-1) 
						{
							LodandSave(retvalweb);
						} 
						else 
						{
							System.out.println("in else ***************************************=="+retvalweb);
							if(retvalweb.indexOf("~")==-1)
							{
								retMess = getString(R.string.alert_network_problem_pease_try_again);
							}
							else
							{
							String msg[] = retvalweb.split("~");
							
							Log.e("Check 1",msg[0]);
							Log.e("Check 2",msg[1]);
							
							if (msg[1].equals("1")) {
								retMess = getString(R.string.login_alert_009);
							} 
							else if(msg[1].equals("2"))
							{
								retMess = getString(R.string.login_alert_002);							
							}
							else if(msg[1].equals("3"))
								retMess = getString(R.string.login_alert_003);
							else if(msg[1].equals("4"))
								retMess = getString(R.string.login_alert_004);
							else if(msg[1].equals("5"))
								retMess = getString(R.string.login_alert_005);
							else if(msg[1].equals("6"))
								retMess = getString(R.string.login_alert_006);
							else if(msg[1].equals("7"))
							{
								Log.e("alskdaksjdlaksjd","1231892731782");
								retMess = getString(R.string.login_alert_007);						
							}
							else if(msg[1].equals("8")){
								retMess = getString(R.string.login_alert_008);}
							else if(msg[1].equals("9"))
								retMess = getString(R.string.alert_login_fail);	
							/*else if(msg[1].equals("10"))
								retMess = getString(R.string.login_alert_diffimei);	*/
							/*else if(msg[1].indexOf("OLDVERSION")>-1)
								retMess = getString(R.string.alert_oldversion);*/
						}
						setAlert();
					}
				} 
							
						
					} catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				else 
				{
					retMess = getString(R.string.alert_000);
					setAlert();
				}
			}
		}	
	public void LodandSave(String retrstr)
	{
		isWsCallSuccess=true;
		Log.e("In Login", "decryptedAccounts,,,,, :"+ decryptedAccounts);
		decryptedAccounts = retrstr.split("SUCCESS~")[1];
		
		if (!decryptedAccounts.equals("FAILED#")) 
		{
			System.out.println("decryptedAccounts :"+ decryptedAccounts); 
			splitstr =decryptedAccounts.split("!@!");
			Bundle b = new Bundle();
	   		String accounts = splitstr[0];
	   		String mobno =  splitstr[1];
	   		tranMpin =  splitstr[2];
	   		custid = splitstr[3];
	   		userId=splitstr[4];
	   		
			System.out.println("in if ***************************************");
			                        
			//splitstr =decryptedAccounts.split("!@!");
		
			//String oldversion=splitstr[5];
			strexpdate = splitstr[6];
			lastLogin = splitstr[7];
			Log.e("strexpdate== ","strexpdate=="+strexpdate);
			Log.e("strexpdate== ","lastLogin=="+lastLogin);
			Double dt=Double.parseDouble(strexpdate);
			Log.e("dt== ","dt=="+dt);
			expdt = dt.intValue(); 
			Log.e("expdt== ","expdt=="+expdt);
       
			Log.e("userIdpostexecute== ","userIdpostexecute=="+userId);
			/*if(oldversion.equals("OLDVERSION"))               
			{
				showlogoutAlert(getString(R.string.alert_oldversionupdate));
			}	
			else*/ if(expdt==1)
			{
				retMess=getString(R.string.alert_mpinexp)+" "+expdt+" day. Please Change MPIN";
				setAlert();
			}
			else if(expdt<=7 && expdt>=2 )
			{
				showlogoutAlert1(getString(R.string.alert_mpinexp)+" "+expdt+" days. Do You Want To Change ?");
			}
	       else
	       {
	    	   String[] columnNames={"retval_str","cust_name","cust_id","user_id","cust_mobno","last_login"};
	           String[] columnValues={accounts,"",custid,userId,mobno,lastLogin};
	           dbms.deleteFromTable("SHAREDPREFERENCE", "",null);
	           dbms.insertIntoTable("SHAREDPREFERENCE", 6, columnNames, columnValues);
				Log.e("LOGIN","accounts=="+accounts);
				Log.e("LOGIN","custid=="+custid);
				Log.e("LOGIN","encrptdMpin=="+encrptdMpin);
				Log.e("LOGIN","tranMpin=="+tranMpin);
				Log.e("LOGIN","mobno=="+mobno);
				Log.e("LOGIN","userId=="+userId);
				Log.e("LOGIN","custIdFlg=="+custIdFlg);
				Log.e("LOGIN","lastLogin=="+lastLogin);
				
				if(!custIdFlg)
				{
				String str="";
				String[] coulmnsAndTypes={"CFG_CUST_ID","varchar(10)"};
				String[] colNms={"CFG_CUST_ID"};
				String[] val=new String[1];
				val[0]=custid;
				try
				{
					str=dbms.createTable("CONFIG", coulmnsAndTypes);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				Log.e("SETMPIN","str after create table==="+str);
				int recCnt=0;
				try
				{
					
					Cursor c1=dbms.executePersonalQuery("select count(*) from CONFIG", null);
					if(c1.moveToNext())
					{
						recCnt=c1.getInt(0);
						Log.e("Login ", "recCnt"+recCnt);
					}
					c1.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					
					if(recCnt==0)
						str=dbms.insertIntoTable("CONFIG", 1, colNms, val);
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				Log.e("SETMPIN","str after insert==="+str);
			}
			else
			{
				String values[]={et_custid.getText().toString()};
				String[] colNms={"CFG_CUST_ID"};
				dbms.updateTable("CONFIG", colNms,null, values);
			}
			
			
		
			Intent intent = new Intent(this, DashboardActivity.class);
			intent.putExtra("var1", var1);
			intent.putExtra("var3", var3);
			// add bundle to the intent
			intent.putExtras(b);
			startActivity(intent);
			finish();
                                  }
		}
		else
		{
			retMess = getString(R.string.alert_prblm_login);
			setAlert();
		}
	}
	public void showlogoutAlert1(final String str) {
		CustomDialogClass alert = new CustomDialogClass(LoginActivity.this,str){ 
			
			@Override
			protected void onCreate(Bundle savedInstanceState)  
			{
				super.onCreate(savedInstanceState);
			}
			
			@Override
			public void onClick(View v)  
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if((str.equalsIgnoreCase(getString(R.string.alert_mpinexp)+" "+expdt+" days. Do You Want To Change ?")))
						{
							Log.e("Login","onCreate ==userId=  "+userId);
							Log.e("Login","onCreate ==custid=  "+custid);
							Intent in = new Intent(loginAct,SetMPIN.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							b.putString("USERNAME", userId);
							b.putString("CUSTID", custid);
							in.putExtra("var1", var1);
							in.putExtra("var3", var3);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						
					  break;	
					  
					case R.id.btn_cancel:
						
					 	                             
						Log.e("no pressed","1111111");
						if(!decryptedAccounts.equals("FAILED#"))
						{
							Bundle b = new Bundle();
							String accounts = splitstr[0];
							String mobno =  splitstr[1];
							tranMpin =  splitstr[2];
							custid = splitstr[3];
							 userId=splitstr[4];
							Log.e("Sharayu--","==Mob no=="+mobno);
							Log.e("Sharayu--","==accounts=="+accounts);
							//Log.e("Sharayu--","==tranMpin=="+tranMpin);
							Log.e("Sharayu--","==custid=="+custid);
							Log.e("Sharayu--","==userId=="+userId);
							
							System.out.println("mobno :" + mobno);
							
							String[] columnNames={"retval_str","cust_name","cust_id","user_id","cust_mobno","last_login"};
					           String[] columnValues={accounts,"",custid,userId,mobno,lastLogin};
					           dbms.deleteFromTable("SHAREDPREFERENCE", "",null);
					           dbms.insertIntoTable("SHAREDPREFERENCE", 6, columnNames, columnValues);
							
					           Log.e("Sharayu--","==lastLogin=="+lastLogin);
							if(!custIdFlg)
							{
								String str="";
								String[] coulmnsAndTypes={"CFG_CUST_ID","varchar(10)"};
								String[] colNms={"CFG_CUST_ID"};
								String[] val=new String[1];
								val[0]=custid;
								try
								{
									str=dbms.createTable("CONFIG", coulmnsAndTypes);
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								Log.e("SETMPIN","str after create table==="+str);
								int recCnt=0;
								try
								{
									
									Cursor c1=dbms.executePersonalQuery("select count(*) from CONFIG", null);
									if(c1.moveToNext())
									{
										recCnt=c1.getInt(0);
										Log.e("Login ", "recCnt"+recCnt);
									}
									c1.close();
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								try
								{
									
									if(recCnt==0)
										str=dbms.insertIntoTable("CONFIG", 1, colNms, val);
									
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								Log.e("SETMPIN","str after insert==="+str);
							}
							else
							{
								String values[]={et_custid.getText().toString()};
								String[] colNms={"CFG_CUST_ID"};
								dbms.updateTable("CONFIG", colNms,null, values);
							}
							
							
							//intent = new Intent(loginAct, MainActivity.class);
							Intent intent = new Intent(loginAct, DashboardActivity.class);
							// add bundle to the intent
							intent.putExtra("var1", var1);
							intent.putExtra("var3", var3);
							intent.putExtras(b);
							startActivity(intent);
							finish();
						}
				
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
	
	public void showAlert(final String str)
	{
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
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
							post_success(retvalweb);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							post_successRetvalfailed(retvalweb);
							//this.dismiss();
						}
						else if(str.equalsIgnoreCase(getString(R.string.alert_restartapp))) {
							finish();
							System.exit(0);
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
	
	public void showlogoutAlert(final String str) {
			CustomDialogClass alert = new CustomDialogClass(LoginActivity.this,str){ 
				
				@Override
				protected void onCreate(Bundle savedInstanceState)  
				{
					super.onCreate(savedInstanceState);
				}
				
				@Override
				public void onClick(View v)  
				{
					switch (v.getId()) 
					{
						case R.id.btn_ok:
							
							if((str.equalsIgnoreCase(getString(R.string.alert_mpinexp)+" "+expdt+" days. Do You Want To Change ?")))
							{
								Log.e("Login","onCreate ==userId=  "+userId);
								Log.e("Login","onCreate ==custid=  "+custid);
								Intent in = new Intent(loginAct,SetMPIN.class);
								Bundle b = new Bundle();
								b.putString("FROMACT", "FORGOT");
								b.putString("USERNAME", userId);
								b.putString("CUSTID", custid);
								in.putExtra("var1", var1);
								in.putExtra("var3", var3);
								in.putExtras(b);
								loginAct.startActivity(in);
								loginAct.finish();
							}
							/*else if(str.equalsIgnoreCase(getString(R.string.alert_oldversionupdate)))
							{
							 try 
							 {
								 Intent viewIntent =new Intent("android.intent.action.VIEW",
						                    Uri.parse("https://play.google.com/store/apps/details?id=panchganga.mobilebank"));
						                    startActivity(viewIntent);
						     }
							 catch(Exception e)
							 {
						            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
						                    Toast.LENGTH_LONG).show();
						            e.printStackTrace();
							 }
							//this.dismiss();
						}*/
					  break;	
						  
						case R.id.btn_cancel:
				 Log.e("no pressed","1111111");
							if(!decryptedAccounts.equals("FAILED#"))
							{
								Bundle b = new Bundle();
								String accounts = splitstr[0];
								String mobno =  splitstr[1];
								tranMpin =  splitstr[2];
								custid = splitstr[3];
								String userId=splitstr[4];
								Log.e("Sharayu--","==Mob no=="+mobno);
								Log.e("Sharayu--","==accounts=="+accounts);
								//Log.e("Sharayu--","==tranMpin=="+tranMpin);
								Log.e("Sharayu--","==custid=="+custid);
								Log.e("Sharayu--","==userId=="+userId);
								
								System.out.println("mobno :" + mobno);
								
								String[] columnNames={"retval_str","cust_name","cust_id","user_id","cust_mobno","last_login"};
								String[] columnValues={accounts,"",custid,userId,mobno,lastLogin};
									
								dbms.deleteFromTable("SHAREDPREFERENCE", "",null);
								dbms.insertIntoTable("SHAREDPREFERENCE", 6, columnNames, columnValues);
								
								if(!custIdFlg)
								{
									String str="";
									String[] coulmnsAndTypes={"CFG_CUST_ID","varchar(10)"};
									String[] colNms={"CFG_CUST_ID"};
									String[] val=new String[1];
									val[0]=custid;
									try
									{
										str=dbms.createTable("CONFIG", coulmnsAndTypes);
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
									Log.e("SETMPIN","str after create table==="+str);
									int recCnt=0;
									try
									{
										
										Cursor c1=dbms.executePersonalQuery("select count(*) from CONFIG", null);
										if(c1.moveToNext())
										{
											recCnt=c1.getInt(0);
											Log.e("Login ", "recCnt"+recCnt);
										}
										c1.close();
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
									try
									{
										
										if(recCnt==0)
											str=dbms.insertIntoTable("CONFIG", 1, colNms, val);
										
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
									Log.e("SETMPIN","str after insert==="+str);
								}
								else
								{
									String values[]={et_custid.getText().toString()};
									String[] colNms={"CFG_CUST_ID"};
									dbms.updateTable("CONFIG", colNms,null, values);
								}
								
								
								//intent = new Intent(loginAct, MainActivity.class);
								Intent intent = new Intent(loginAct, DashboardActivity.class);
								// add bundle to the intent
								intent.putExtra("var1", var1);
								intent.putExtra("var3", var3);
								intent.putExtras(b);
								startActivity(intent);
								finish();
							}
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

	public void setAlert() 
	{
		ErrorDialogClass alert = new ErrorDialogClass(loginAct, "" + retMess)
		{
			@Override
			public void onClick(View v) 
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
					{
						if(retMess == getString(R.string.login_alert_009))
						{
							/*Intent in = new Intent(loginAct,ValidateSecQueActivity.class);
							Bundle b = new Bundle();
							b.putString("custId", loginAct.custid);
							b.putString("mpin", loginAct.mpin);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
							this.dismiss();*/
						}
						else if(retMess == getString(R.string.login_alert_007))
						{
							/*Intent in = new Intent(loginAct,ChangeMobileNo.class);
							Bundle b = new Bundle();
							b.putString("custId", custid);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();*/
						}
						else if(retMess == getString(R.string.login_alert_002))
						{
							/*Intent in = new Intent(loginAct,ForgotPassword.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();*/
							Log.e("Login","onCreate ==userId111=  "+userId);
							Log.e("Login","onCreate ==custid111111=  "+custid);
							Intent in = new Intent(loginAct,SetMPIN.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							//b.putString("USERNAME", usernm);
							//b.putString("CUSTID", custnm);
							b.putString("USERNAME", userId);
							b.putString("CUSTID", custId);
							in.putExtra("var1", var1);
							in.putExtra("var3", var3);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						else if((retMess.equalsIgnoreCase(getString(R.string.alert_mpinexp)+" "+expdt+" day. Please Change MPIN")))
						{
							Log.e("Login","onCreate ==userId=  "+userId);
							Log.e("Login","onCreate ==custid=  "+custid);
							Intent in = new Intent(loginAct,SetMPIN.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							b.putString("USERNAME", userId);
							b.putString("CUSTID", custid);
							in.putExtra("var1", var1);
							in.putExtra("var3", var3);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						/*else if(retMess == getString(R.string.alert_oldversionupdate))
						{
							//Toast.makeText(LoginActivity.this,"in oldversion", Toast.LENGTH_LONG).show();
							 try {
				                    Intent viewIntent =
				                    new Intent("android.intent.action.VIEW",
				                    Uri.parse("https://play.google.com/store/apps/details?id=panchganga.mobilebank"));
				                    startActivity(viewIntent);
				        }catch(Exception e) {
				            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
				                    Toast.LENGTH_LONG).show();
				            e.printStackTrace();
						}
						}*/
					}
					break;			
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Intent intent = null;
		if (flag == 0) {
			new CallWebService().doInBackground();
			if (cnt == 1) {
				Log.i("mayuri success", "success");
				Bundle b = new Bundle();
				// add data to bundle
				Log.i("Return value:", retVal);
				/*
				 */
				// retVal="SUCCESS~ 5#101#SB#1#KADEKAR KAVITA KIRAN,5#101#SB#2#KADEKAR KAVITA KIRAN, 5#101#SB#3#Mrs. KADEKAR KAVITA KIRAN, 5#101#SB#6#KADEKAR DIGAMBAR HARI / KAVITA KIRAN, 5#101#SB#7#DESHPANDE JAGGANATH SHANKAR / KADEKAR KAVITA K., ~KAVITA KIRAN KADEKAR ~8007454533~";
				String string1[] = retVal.split("~");
				System.out.println("string1.length:" + string1.length);
				String cust_name = "", cust_mob_no = "";
				// for (int j = 0; j < string1.length; j++) {
				// System.out.println("[j]:" + j);
				System.out.println("string1[0]....:" + string1[0]);
				// System.out.println("string1[1]....:" + string1[1]);
				// System.out.println("string1[2]....:" + string1[2]);
				cust_name = string1[2];
				e.putString("cust_name", cust_name);

				cust_mob_no = string1[3];
				String string2[] = cust_mob_no.split(";");
				e.putString("cust_mob_no", string2[0]);
				// }
				b.putString("accounts", retVal);
				e.putString("retValStr", retVal);
				String custId = et_custid.getText().toString().trim();
				e.putString("custId", custId);
				String pin = et_mpin.getText().toString().trim();
				e.putString("pin", pin);
				e.commit();
				intent = new Intent(this, MainActivity.class);
				// add bundle to the intent
				intent.putExtra("var1", var1);
				intent.putExtra("var3", var3);
				intent.putExtras(b);
				// pb_wait.setVisibility(View.INVISIBLE);
				// pb_wait.setVisibility(ProgressBar.INVISIBLE);

				// intent = new Intent(getApplicationContext(),
				// BalanceEnquiry.class);
				startActivity(intent);
				finish();
			} else {
				setAlert();
				// pb_wait.setVisibility(View.INVISIBLE);
				// et_custid.setText("");
				// et_mpin.setText("");
				et_custid.setFocusableInTouchMode(true);
				et_custid.requestFocus();
				cnt = 0;
			}
		}
		// p_wait.setVisibility(ProgressBar.INVISIBLE);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public float getBatteryLevel() {
		Intent batteryIntent = registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return 50.0f;
		}

		return ((float) level / (float) scale) * 100.0f;
	}

	public class InputDialogBox extends Dialog implements OnClickListener {

		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText bankname;
		Button btnSubmit;
		String strmpin = "";
		TextView txtLbl;
		boolean flg;

		public InputDialogBox(Activity activity) {

			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) {

			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_design_bankname_input);
			bankname = (EditText) findViewById(R.id.txtBankname);
			btnSubmit = (Button) findViewById(R.id.btnSubmit);
			bankname.setVisibility(EditText.VISIBLE);
			btnSubmit.setVisibility(Button.VISIBLE);
			btnSubmit.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {

			try {
				System.out
						.println("========= inside onClick ============***********");
				String str = bankname.getText().toString().trim();
				Log.e("bank name =", str);
				insertToCONFIG(str);
				//tv_bankname.setText(str);
				this.hide();
			} catch (Exception e) {
				Log.e("EXCEPTION", "------------------"+e);
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox

	public String getCustId()
	{
		Log.e("LOGIN"," in getCustId");
		String customer_id="";
		int cnt=0;
		try
		{
			try
			{
				Cursor c=dbms.executePersonalQuery("select count(*) from CONFIG", null);
				if(c.moveToNext())
				{		
					cnt=c.getInt(0);
				}
				c.close();
			}
			catch(Exception ex)
			{
				Log.e("LOGIN", "msg=="+ex.toString());
			}
			Log.e("LOGIN","cnt==="+cnt);
			if(cnt>0)
			{	
				custIdFlg=true;
				Cursor c1=dbms.executePersonalQuery("select CFG_CUST_ID from CONFIG", null);
				if(c1.moveToNext())
				{
					customer_id=c1.getString(0);
					Log.e("Login ", "in loop"+customer_id);
				}
				c1.close();
			}
			else
			{	
				custIdFlg=false;
				customer_id="";
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		Log.e("Login", "in onCreate()"+customer_id);
		return customer_id;
	}
	
	public void createSharedPrefTable(){
		
		//dbms.dropTable("SHAREDPREFERENCE");
			String sts="";
			String val[]={"retval_str","varchar(2000)",
						  "cust_name","varchar(100)",
						  "cust_id","varchar(15)",
						  "user_id","varchar2(20)",
						  "cust_mobno","varchar(15)",
						  "last_login","varchar(50)"};
			Log.e("createSharedPrefTable","createSharedPrefTable==DBMS "+dbms);
		    	sts=dbms.createTable("SHAREDPREFERENCE",val);
			Log.e("LOGIN","SHAREDPREFERENCE_create"+sts);
			
		}// createSharedPrefTable
	

	/*public void createSharedPrefTable(){
		
	//dbms.dropTable("SHAREDPREFERENCE");
		String sts="";
		String val[]={"retval_str","varchar(2000)",
					  "cust_name","varchar(100)",
					  "cust_id","varchar(15)",
					  "user_id","varchar2(20)",
					  "cust_mobno","varchar(15)"};
		Log.e("createSharedPrefTable","createSharedPrefTable==DBMS "+dbms);
	    	sts=dbms.createTable("SHAREDPREFERENCE",val);
		Log.e("LOGIN","SHAREDPREFERENCE_create"+sts);
		
	}*/// createSharedPrefTable
	}

	class NetworkTimer extends CountDownTimer {
	LoginActivity logAct;

	@SuppressLint("MissingPermission")
	public NetworkTimer(long millisInFuture, long countDownInterval,
						LoginActivity log) {
		super(millisInFuture, countDownInterval);
		logAct = log;
		// Toast.makeText(logAct, "Inside Countdown", Toast.LENGTH_LONG).show();
		// logAct.p_wait.setVisibility(ProgressBar.VISIBLE);
		// logAct.batteryManager= (BatteryManager)
		// logAct.getSystemService(Context.POWER_SERVICE);

		logAct.locManager = (LocationManager) logAct
				.getSystemService(Context.LOCATION_SERVICE);
		logAct.locManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, logAct);
		start();
	}

	@Override
	public void onFinish() {
		// logAct.p_wait.setVisibility(ProgressBar.INVISIBLE);
		// Toast.makeText(logAct, "Inside Finish", Toast.LENGTH_LONG).show();
		// logAct.locManager.removeUpdates(logAct);
	}

	@Override
	public void onTick(long arg0) {
	}

}
