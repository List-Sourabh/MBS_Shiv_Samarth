package list.shivsamarth_mbs;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CommonLib;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;

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
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

public class TransferOTP extends Activity implements OnClickListener {
	int cnt = 0, flag = 0;
	int netFlg, gpsFlg;
	public EditText txt_otp;
	DialogBox dbs;
	TextView txt_ref_id;
	public String strRefId;
	TextView txt_heading;
	ImageButton btn_back;
	ImageView img_heading;
	Button btn_otp_submit, btn_otp_resend;
	String strOTP, retMess, retVal, strCustId, strFromAct, strRetVal, strMobNo,
			stratm = "", encrptdTranMpin = "";
	String strActno = "", cardno = "", strimeino = "", catdstatus = "",
			version = "", respcode = "", regenOtp = "", retval = "",
			respdesc = "", respdesc_resend_otp = "", retvalwbs = "",
			respdesc_SaveATMCard = "", respdescvalidate = "",
			respdescresend = "", respdescgent = "", respdescsendcust = "";

	String from_activity = "", customer_id = "";
	String otp = "";
	String imeino = "";
	String request_id="";
	public String refno = "";
	private String[] presidents;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	private static String METHOD_NAME2 = "";
	private static String METHOD_NAME3 = "";
	private static String responseJSON = "NULL";
	TelephonyManager telephonyManager;
	String imeiNo = "";
	private MyThread t1;
	int timeOutInSecs = 300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	JSONObject jObj;
	DatabaseManagement dbms;
	CommonLib comnObj;
	TransferOTP act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_activity);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act=this;
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
		txt_otp = (EditText) findViewById(R.id.txt_otp);
		txt_ref_id = (TextView) findViewById(R.id.txt_ref_id);
		btn_otp_submit = (Button) findViewById(R.id.btn_otp_submit);
		btn_otp_resend = (Button) findViewById(R.id.btn_otp_resend);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.otp);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_otp_validtn));
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		btn_otp_submit.setOnClickListener(this);
		btn_otp_resend.setOnClickListener(this);
		new CountDownTimer(30000, 1000){
			public void onTick(long millisUntilFinished){
				NumberFormat f = new DecimalFormat("00");
				long sec = (millisUntilFinished / 1000) % 60;
				String bttext=getString(R.string.resend_OTP)+" in "+f.format(sec)+" Seconds";
				btn_otp_resend.setText(bttext);
			}
			public  void onFinish(){
				Log.e("DSP", "resendtimeout====");
				btn_otp_resend.setText(getString(R.string.resend_OTP));
				btn_otp_resend.setClickable(true);
				//Toast.makeText(TransferOTP.this, "Resend OTP",Toast.LENGTH_SHORT).show();
			}
		}.start();
		dbs = new DialogBox(this);
		presidents = getResources().getStringArray(R.array.Errorinwebservice);
		Bundle bObj = getIntent().getExtras();
		if (bObj != null) {
			strCustId = bObj.getString("CUSTID");
			strFromAct = bObj.getString("FROMACT");
			try {
				jObj = new JSONObject(bObj.getString("JSONOBJ"));
			} catch (Exception je) {
				je.printStackTrace();
			}
		}
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				strMobNo = c1.getString(4);
			}
		}

		/*
		 * InputDialogBox inputBox = new InputDialogBox(act); inputBox.show();
		 */

		int flag = chkConnectivity();
		if (flag == 0) {
			regenOtp = "N";
			CallWebServiceGenerateOtp c = new CallWebServiceGenerateOtp();
			c.execute();
		}

		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_otp_submit:
			strOTP = txt_otp.getText().toString().trim();
			if (strOTP.length() == 0) {
				showAlert(act.getString(R.string.alert_entrvalidotp));
			} else {
				int flag = chkConnectivity();
				if (flag == 0) {
					CallWebServiceValidateOTP c = new CallWebServiceValidateOTP();
					c.execute();
				}

			}
			break;
		case R.id.btn_otp_resend:
			int flag = chkConnectivity();
			if (flag == 0) {
				regenOtp = "Y";
				CallWebServiceGenerateOtp c = new CallWebServiceGenerateOtp();
				c.execute();
			}
			break;
		case R.id.btn_back:
			
			if(strFromAct.equalsIgnoreCase("RTNTBANK"))
			{
				Intent in = new Intent(this, OtherBankTranRTGS.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
			}
			else if(strFromAct.equalsIgnoreCase("IMPSBANK"))
			{
				/*Fragment fragment = new OtherBankTranIFSC(act);
				FragmentManager fragmentManager = this.getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
			}
			else if(strFromAct.equalsIgnoreCase("QRSEND"))
			{
				Intent in = new Intent(this, QrcodeSendActivity.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
			}
			else if(strFromAct.equalsIgnoreCase("SAMEBANK"))
			{
				Intent in = new Intent(this, SameBankTransfer.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
			}
			/*else
			{
				Fragment fragment = new OwnAccountTransfer(act);
				FragmentManager fragmentManager = this.getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();			
			}*/
			break;
		default:
			break;
		}
	}

	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if((str.equalsIgnoreCase(respdescvalidate)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successvalidate(retval);
						}
						else if((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(respdescresend)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successresend(retval);
						}
						else if((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(respdescsendcust)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successsendcust(retval);
						}
						else if((str.equalsIgnoreCase(respdescsendcust)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if(str.equalsIgnoreCase(act.getString(R.string.alert_111)) || str.indexOf(act.getString(R.string.alert_125_1))>-1)
						{
							InputDialogBox inputBox = new InputDialogBox(act);
							inputBox.show();
						}
						/*else if(!str.equalsIgnoreCase(act.getString(R.string.alert_076)))
						{
							FragmentManager fragmentManager;
							Fragment fragment = new FundTransferMenuActivity(act);
							act.setTitle(getString(R.string.lbl_same_bnk_trans));
							fragmentManager = act.getFragmentManager();
							fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
						}*/
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
	public class InputDialogBox extends Dialog implements OnClickListener {
		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		Button btnOk;
		String strmpin = "";
		TextView txtLbl;
		boolean flg;

		public InputDialogBox(Activity activity) {
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) {
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.transfer_dialog);
			mpin = (EditText) findViewById(R.id.txtMpin);
			btnOk = (Button) findViewById(R.id.btnOK);
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
			txt_otp.setText("");
		}

		@Override
		public void onClick(View v) {
			try {
				String str = mpin.getText().toString().trim();
				encrptdTranMpin = str; // ListEncryption.encryptData(strCustId +
										// str);
				if (str.length() == 0) {
					encrptdTranMpin = "";
					retMess = getString(R.string.alert_116);
					showAlert(retMess);// setAlert();
					this.show();
				} else if (str.length() != 6) {
					encrptdTranMpin = "";
					retMess = getString(R.string.alert_037);
					showAlert(retMess);// setAlert();
					this.show();
				} else {
					/*
					 * int flag = comnObj.chkConnectivity(); if (flag == 0) {
					 * regenOtp="N"; CallWebServiceGenerateOtp c = new
					 * CallWebServiceGenerateOtp(); c.execute(); }
					 */
					this.hide();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}// end onClick
	}// end InputDialogBox

	class CallWebService extends AsyncTask<Void, Void, Void> {
		// String[] xmlTags = { "CUSTID","IMEINO" };

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			try {
				respcode = "";
				retvalwbs = "";
				respdesc = "";
				Log.e("@DEBUG", "LOGOUT preExecute()");
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE", "29");

				// valuesToEncrypt[0] = custid;
				// valuesToEncrypt[1] =
				// MBSUtils.getImeiNumber(DashboardDesignActivity.this);
			} catch (JSONException je) {
				je.printStackTrace();
			}
			// valuesToEncrypt[0] = jsonObj.toString();

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			// Log.e("@DEBUG","LOGOUT doInBackground()");
			String value4 = act.getString(R.string.namespace);
			String value5 = act.getString(R.string.soap_action);
			String value6 = act.getString(R.string.url);
			final String value7 = "callWebservice";

			try {
				String keyStr = CryptoClass.Function2();
				var2 = CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1",
						CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2",
						CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						value6, 45000);

				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
			}// end try
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final Void result) {
			Log.e("@DEBUG", "LOGOUT onPostExecute()");

			JSONObject jsonObj;
			try {
				String str = CryptoClass.Function6(var5, var2);
				jsonObj = new JSONObject(str.trim());

				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					retvalwbs = jsonObj.getString("RETVAL");
				} else {
					retvalwbs = "";
				}
				if (jsonObj.has("RESPDESC")) {
					respdesc = jsonObj.getString("RESPDESC");
				} else {
					respdesc = "";
				}

				if (respdesc.length() > 0) {
					showAlert(respdesc);
				} else {
					if (retvalwbs.indexOf("FAILED") > -1) {
						retMess = getString(R.string.alert_network_problem_pease_try_again);
						showAlert(retMess);

					} else {
						post_success(retvalwbs);
						/*
						 * finish(); System.exit(0);
						 */
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void post_success(String retvalwbs) {
		respcode = "";
		respdesc = "";
		act.finish();
		System.exit(0);

	}

	

	@Override
	public void onBackPressed() {
		// Simply Do noting!
		
	}

	class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {

		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			strOTP = txt_otp.getText().toString().trim();
			strRefId = txt_ref_id.getText().toString().trim();
			strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();

			try {
				jsonObj.put("OTPVAL", strOTP);
				jsonObj.put("CUSTID", strCustId);// ListEncryption.encryptData(strCustId
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("REFID", strRefId);
				jsonObj.put("ISREGISTRATION", "N");
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE", "20");
				// Log.e("jayesh=","===11");
				Log.e("validateotpbenf", "========" + jsonObj.toString());
				// jObj.put("VALIDATIONDATA",ValidationData);

				// jObj.put("TRANPIN", encrptdTranMpin);

			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "webServiceTwo";

			try {
				String keyStr = CryptoClass.Function2();
				var2 = CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1",
						CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2",
						CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						value6, 45000);

				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
				isWSCalled = true;
			}// end try
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			String errorCode = "";
			if (isWSCalled) {

				JSONObject jsonObj;
				try {
					String str = CryptoClass.Function6(var5, var2);
					jsonObj = new JSONObject(str.trim());

					/*
					 * if (jsonObj.has("VALIDATIONDATA") &&
					 * ValidationData.equals
					 * (jsonObj.getString("VALIDATIONDATA"))) {
					 */
					if (jsonObj.has("RESPCODE")) {
						respcode = jsonObj.getString("RESPCODE");
					} else {
						respcode = "-1";
					}
					if (jsonObj.has("RETVAL")) {
						retval = jsonObj.getString("RETVAL");
					} else {
						retval = "";
					}
					if (jsonObj.has("RESPDESC")) {
						respdescvalidate = jsonObj.getString("RESPDESC");
					} else {
						respdescvalidate = "";
					}

					if (respdescvalidate.length() > 0
							&& respdescvalidate.indexOf("Success") == -1) {
						showAlert(respdescvalidate);
					} else {
						// Log.e("SAM=","retval==="+retval);
						if (retval.indexOf("SUCCESS") > -1) {
							postSuccess_validateOTP(retval);
						} else if (retval.indexOf("FAILED~MAXATTEMPT") > -1) {
							retMess = act.getString(R.string.alert_076_1);
							showAlert(retMess);
						} else {
							retMess = act.getString(R.string.alert_076);
							showAlert(retMess);
						}

					}

					/*
					 * } else { MBSUtils.showInvalidResponseAlert(act); }
					 */

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				showAlert(act.getString(R.string.alert_000));
			}
		}
	}

	public void postSuccess_validateOTP(String retval) {

		CallWebServicestoreTransferTranWS storeTran = new CallWebServicestoreTransferTranWS();
		storeTran.execute();
	}

	class CallWebServicestoreTransferTranWS extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
	
		boolean isWSCalled = false;
		
		@Override
		protected void onPreExecute() 
		{  
			loadProBarObj.show();
			strOTP = txt_otp.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
			

			try
			{
				
				if(strFromAct.equalsIgnoreCase("QRSEND"))
				{
					jObj.put("TRANSFERTYPE", "QR");
				}
				else if(strFromAct.equalsIgnoreCase("IMPSBANK"))
				{
					jObj.put("TRANSFERTYPE", "P2A");
				}
				//jObj.put("OTPVAL", ListEncryption.encryptData(strOTP+strCustId));
				String location=MBSUtils.getLocation(act);
				jObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jObj.put("REFID", strRefId);
				jObj.put("ISREGISTRATION", "N");
				jObj.put("SIMNO", MBSUtils.getSimNumber(act));
			
				jObj.put("TRANPIN", encrptdTranMpin);
				jObj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				jObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				jObj.put("OSVERSION", Build.VERSION.RELEASE);
				jObj.put("LATITUDE", location.split("~")[0]);
				jObj.put("LONGITUDE", location.split("~")[1]);
				jObj.put("METHODCODE","16"); 
				Log.e("transfertran","========"+jObj.toString());
               
				
				/*Log.e("MOBILENO=","MOBILENO==="+MBSUtils.getMyPhoneNO(act));
				Log.e("IPADDRESS=","IPADDRESS==="+MBSUtils.getLocalIpAddress());
				Log.e("OSVERSION=","OSVERSION==="+Build.VERSION.RELEASE);
				Log.e("LATITUDE=","LATITUDE==="+location.split("~")[0]);
				Log.e("LONGITUDE=","LONGITUDE==="+location.split("~")[1]);*/
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
			final String value7 = "callWebservice";

			try 
			{
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1", CryptoClass.Function5(jObj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,60000);

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
			String errorCode="";
			if (isWSCalled) 
			{			
				
                JSONObject jsonObj;
				try
				{
					String str=CryptoClass.Function6(var5,var2);
					jsonObj = new JSONObject(str.trim());
					
					Log.e("strwebtran", "---------------"+str.trim());
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
						if(jsonObj.has("RESPDESC"))
						{
							respdescvalidate = jsonObj.getString("RESPDESC");
						}
						else
						{	
							respdescvalidate = "";
						}
						if(respdescvalidate.length()>0 && respdescvalidate.indexOf("Success")==-1)
						{
							showAlertPost(respdescvalidate);
						}
						else
						{
							if (retval.indexOf("SUCCESS")>-1) 
							{				
								post_successsaveTransferTran(retval);
							} 
							else
							{
								if (retval.indexOf("LIMIT_EXCEEDS") > -1) 
								{
									retMess = act.getString(R.string.alert_031);
									showAlertnew(retMess);
									/*Intent in = new Intent(TransferOTP.this, FundTransferMenuActivity.class);
									in.putExtra("var1", var1);
									in.putExtra("var3", var3);
									startActivity(in);
									finish();*/
								} 
								else if (retval.indexOf("DUPLICATE") > -1) 
								{
									try {
										retMess = act.getString(R.string.alert_119) + jObj.getString("TRANID")+ "\n"+ act.getString(R.string.alert_120);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									showAlertnew(retMess);
								/*	Intent in = new Intent(TransferOTP.this, FundTransferMenuActivity.class);
									in.putExtra("var1", var1);
									in.putExtra("var3", var3);
									startActivity(in);
									finish();*/
								} 
								else if (retval.indexOf("WRONGTRANPIN") > -1) 
								{
									
									String msg[] = retval.split("~");
									String first = msg[1];
									String second = msg[2];
									
									int count = Integer.parseInt(second);
									count = 5 - count;
									retMess = act.getString(R.string.alert_125_1) + " " + count
											+ " " + act.getString(R.string.alert_125_2);
									showAlertnew(retMess);
								}
								else if (retval.indexOf("BLOCKEDFORDAY") > -1) 
								{
									//loadProBarObj.dismiss();
									retMess = act.getString(R.string.login_alert_005);
									showAlertnew(retMess);
								} 
								else if (retval.indexOf("FAILED~") > -1) 
								{
									String msg[] = retval.split("~");
									if (msg.length > 3) 
									{
										String postingStatus = msg[1];
										String req_id = msg[2];
										String errorMsg = msg[3];
										if (req_id.length() > 0) 
										{
											if (req_id != null || req_id.length() > 0)
												retMess = act.getString(R.string.alert_162) + " "+ req_id;
										} 
										else if (errorMsg.length() > 0) 
										{
											retMess = act.getString(R.string.alert_032) + errorMsg;
										}
									} 
									else 
									{
										retMess = act.getString(R.string.alert_032);
									}
									showAlertnew(retMess);

									//FragmentManager fragmentManager;
									/*Intent in = new Intent(TransferOTP.this, FundTransferMenuActivity.class);
									in.putExtra("var1", var1);
									in.putExtra("var3", var3);
									startActivity(in);
									finish();*/
								}
								else if (retval.indexOf("FAILED") > -1)
								{
									if(retval.split("~")[1].length()>0)
									{
										errorCode=retval.split("~")[1];
									}
									else
									{
										errorCode="NA";
									}
									
									if(errorCode.equalsIgnoreCase("999"))
									{
										retMess = act.getString(R.string.alert_179);
									}
									else if(errorCode.equalsIgnoreCase("001"))
									{
										 retMess = act.getString(R.string.alert_180);
									}
									else if(errorCode.equalsIgnoreCase("002"))
									{
										retMess = act.getString(R.string.alert_181);
									}
									else if(errorCode.equalsIgnoreCase("003"))
									{
										retMess = act.getString(R.string.alert_182);
									}
									else if(errorCode.equalsIgnoreCase("004"))
									{
										retMess = act.getString(R.string.alert_179);
									}
									else if(errorCode.equalsIgnoreCase("005"))
									{
										retMess = act.getString(R.string.alert_183);
									}
									else if(errorCode.equalsIgnoreCase("006"))
									{
										retMess = act.getString(R.string.alert_184);
									}
									else if(errorCode.equalsIgnoreCase("007"))
									{
										retMess = act.getString(R.string.alert_179);
									}
									else if(errorCode.equalsIgnoreCase("008"))
									{
										retMess = act.getString(R.string.alert_176);
									}
									else
									{
										retMess = act.getString(R.string.trnsfr_alert_001);
										
									}
									
									showAlertPost(retMess);
								}
								else 
								{
									retMess = act.getString(R.string.alert_032);
									showAlertnew(retMess);
								}
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
				showAlertnew(act.getString(R.string.alert_000));
			}
		}
	}
	
	public 	void post_successsaveTransferTran(String reTval)
	{
		LoadProgressBar lod=new LoadProgressBar(TransferOTP.this);
	   lod.show();
		if(strFromAct.equalsIgnoreCase("RTNTBANK"))
		{
			respcode="";
			String msg[] = reTval.split("~");
			if (msg.length > 2) {
				if (msg[2] != null || msg[2].length() > 0) 
				{
					String postingStatus = msg[1];
					String req_id = msg[2];
					request_id=req_id;
					retMess = act.getString(R.string.alert_030) + " "+ act.getString(R.string.alert_121) + " " + req_id;
				}
			} else {
				retMess = act.getString(R.string.alert_030);
			}
		}
		else if(strFromAct.equalsIgnoreCase("IMPSBANK"))
		{
			respcode="";
			String msg[] = reTval.split("~"); 
			if(msg.length>2)
			{
				if(msg[2]!=null || msg[2].length()>0)
				{
					String postingStatus=msg[0];
					String req_id=msg[1];
					request_id=req_id;
					retMess=act.getString(R.string.alert_030)+" "+act.getString(R.string.alert_121)+" "+req_id;
				}
			}
			else
			{
				retMess = act.getString(R.string.alert_030);
			}
		}
		else if(strFromAct.equalsIgnoreCase("QRSEND"))
		{
			respcode="";
			String tranId=reTval.split("~")[2];
			request_id=tranId;
			retMess = act.getString(R.string.alert_030)+" "+act.getString(R.string.alert_121)+" "+tranId;
		}
		else
		{
			respcode="";
			String tranId=reTval.split("~")[2];
			request_id=tranId;
			retMess = act.getString(R.string.alert_030) + " "+ act.getString(R.string.alert_121) + " " + tranId;			
		}	
		
		//showAlertnew(retMess);
		showshareAlert(retMess);
		
		   lod.dismiss();
	/*	Intent in = new Intent(TransferOTP.this, FundTransferMenuActivity.class);
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		startActivity(in);
		finish();*/
	}

	public void showshareAlert(final String str)
	{
		CustomDialogClass alert=new CustomDialogClass(act, str) {
			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.custom_dialog_box);
				Button btn = (Button) findViewById(R.id.btn_cancel);
				TextView txt_message=(TextView)findViewById(R.id.txt_dia);
				txt_message.setText(str);
				btn.setOnClickListener(this);
				btn.setText("Share");
				Button btnok = (Button) findViewById(R.id.btn_ok);
				btnok.setOnClickListener(this);
				btnok.setText("OK");
			}
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						this.dismiss();
						Intent in = new Intent(TransferOTP.this, FundTransferMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
						break;

					case R.id.btn_cancel:
						String shareBody = null;
						try {
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
							String CurrentDateTime = sdf.format(c.getTime());
							String dbaccountno=jObj.getString("DRACCNO");
							String craccountno=jObj.getString("CRACCNO");
							dbaccountno="XXXXXXXXXXXX"+dbaccountno.substring(12);
							craccountno="XXXXXXXXXXXX"+craccountno.substring(12);
							//shareBody = "Paid On : "+CurrentDateTime+"\nFrom : "+dbaccountno+"\nTo : "+craccountno+"\nAmount : "+jObj.getString("AMOUNT")+".00 Rs"+"\nReference ID : "+request_id;
							Intent share = new Intent(Intent.ACTION_SEND);
							Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.bg); // the original file yourimage.jpg i added in resources
							Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

							String shareBody1 = "Paid On : "+"CurrentDateTime";
							Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/calibri.ttf");
							//Typeface typeface = Typeface.create(typeface1, Typeface.DEFAULT_BOLD);
							Canvas cs = new Canvas(dest);
							Paint tPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
							tPaint.setTypeface(typeface1);
							tPaint.setTextSize(22);
							tPaint.setColor(Color.BLACK);
							cs.drawBitmap(src, 0f, 0f, null);
							float height = tPaint.measureText("yY");
							float width = tPaint.measureText(shareBody1);
							float x_coord = (src.getWidth() - width)/2;
							cs.drawText("Paid On : "+CurrentDateTime, x_coord, height+150f, tPaint);
							cs.drawText("From : "+dbaccountno, x_coord, height+200f, tPaint);
							cs.drawText("To : "+craccountno, x_coord, height+250f, tPaint);
							cs.drawText("Amount : "+jObj.getString("AMOUNT")+".00 Rs", x_coord, height+300f, tPaint);
							cs.drawText("Reference ID : "+request_id, x_coord, height+350f, tPaint);// 15f is to put space between top edge and the text, if you want to change it, you can

							dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/SharedImage.jpg")));
							// dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
							share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							Uri apkURI = FileProvider.getUriForFile(act,act.getPackageName(), new File("/sdcard/SharedImage.jpg"));
							share.putExtra(Intent.EXTRA_STREAM,	Uri.parse("file:///sdcard/SharedImage.jpg"));
							share.putExtra(Intent.EXTRA_STREAM,apkURI);
							share.setType("image/*");
							startActivity(Intent.createChooser(share, "Share Image"));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch (JSONException e) {
							e.printStackTrace();
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

	public void post_successresend(String retval) {
		txt_ref_id.setText("");
		txt_ref_id.setText(act.getString(R.string.lbl_ref_id) + " :"
				+ retval.split("~")[1].split("!!")[2]);
	}

	public void post_successsendcust(String retval) {

		
	}

	public void showAlertnew(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{
			
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						Intent in = new Intent(TransferOTP.this, FundTransferMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
						//this.hide();
					  break;			
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.setCancelable(false);
		alert.show();
	
	}
	
	public void showAlertPost(final String str) {

		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(TransferOTP.this,
						FundTransferMenuActivity.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
				this.dismiss();
				/*
				 * FragmentManager fragmentManager; Fragment fragment = new
				 * FundTransferMenuActivity(act);
				 * act.setTitle(getString(R.string.lbl_same_bnk_trans));
				 * fragmentManager = getFragmentManager();
				 * fragmentManager.beginTransaction
				 * ().replace(R.id.frame_container, fragment).commit();
				 */
			}

		};
		// this.dismiss();
		
		alert.setCancelable(false);
		alert.show();
	}

	

	public int chkConnectivity() {
		// Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		// Log.i("2222", "2222");
		try {
			State state = ni.getState();
			// Log.i("3333", "3333");
			boolean state1 = ni.isAvailable();
			// Log.i("4444", "4444");
			// System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:

					// Log.i("5555", "5555");
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						gpsFlg = 1;
						flag = 0;

					}
					break;
				case DISCONNECTED:
					// Log.i("6666", "6666");
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
					// Log.i("7777", "7777");
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
				// Log.i("8888", "8888");
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		t1.sec = -1;
		System.gc();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		t1.sec = timeOutInSecs;
		Log.e("sec11= ", "sec11==" + t1.sec);
		return super.onTouchEvent(event);
	}

	public void post_successvalidate(String retval) {
		respdescvalidate = "";
		respcode = "";
		String decryptedAccounts = retval;
	}

	class CallWebServiceGenerateOtp extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		boolean isWSCalled = false;

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();

			try {
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("REQSTATUS", "R");
				jsonObj.put("REQFROM", strFromAct);
				jsonObj.put("MOBNO", strMobNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE", "26");

			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = act.getString(R.string.namespace);
			String value5 = act.getString(R.string.soap_action);
			String value6 = act.getString(R.string.url);
			final String value7 = "webServiceOne";

			try {
				String keyStr = CryptoClass.Function2();
				var2 = CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1",
						CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2",
						CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						value6, 45000);

				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
				isWSCalled = true;
			}// end try
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			if (isWSCalled) {

				JSONObject jsonObj;
				try {
					Log.e("strotpbenf", "======" + var5);
					String str = CryptoClass.Function6(var5, var2);
					Log.e("strotpbenf", "======" + str.trim());
					jsonObj = new JSONObject(str.trim());

					if (jsonObj.has("RESPCODE")) {
						respcode = jsonObj.getString("RESPCODE");
					} else {
						respcode = "-1";
					}
					if (jsonObj.has("RETVAL")) {
						retval = jsonObj.getString("RETVAL");
					} else {
						retval = "";
					}
					if (jsonObj.has("RESPDESC")) {
						respdescresend = jsonObj.getString("RESPDESC");
					} else {
						respdescresend = "";
					}

					if (respdescresend.length() > 0) {
						showAlert(respdescresend);
					} else {

						if (retval.split("~")[0].indexOf("SUCCESS") > -1) {
							post_successresend(retval);
						} else {
							retMess = act.getString(R.string.alert_094);
							showAlert(retMess);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				retMess = act.getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp
}
