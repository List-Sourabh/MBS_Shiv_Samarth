package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CommonLib;
import mbLib.CryptoClass;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class BeneficiaryOtp extends Activity implements OnClickListener {
	int cnt = 0, flag = 0;
	int netFlg, gpsFlg;
	public EditText txt_otp;
	DialogBox dbs;
	TextView txt_ref_id;
	public String strRefId;
	TextView txt_heading;
	ImageView img_heading;
	ImageButton btn_back;
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
	String imeiNo = "";
	private MyThread t1;
	int timeOutInSecs = 300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	JSONObject jObj;
	DatabaseManagement dbms;
	CommonLib comnObj;
	BeneficiaryOtp act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_activity);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
		act=this;
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
				//Toast.makeText(BeneficiaryOtp.this, "Resend OTP",Toast.LENGTH_SHORT).show();
			}
		}.start();
		this.dbs = new DialogBox(this);
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
				showAlert(getString(R.string.alert_entrvalidotp));
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
			if (strFromAct.equalsIgnoreCase("ADDSAMBENF")) {
				Intent in = new Intent(this, AddSameBankBeneficiary.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
			} else if (strFromAct.equalsIgnoreCase("ADDOTHERBENF")) {
				Intent in = new Intent(this, AddOtherBankBeneficiary.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
			}
			break;
		default:
			break;
		}
	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("0"))) {
						post_successvalidate(retval);
					} else if ((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("0"))) {
						post_successresend(retval);
					} else if ((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdescsendcust)) && (respcode.equalsIgnoreCase("0"))) {
						post_successsendcust(retval);
					} else if ((str.equalsIgnoreCase(respdescsendcust)) && (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
						post_success(retvalwbs);
					} else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if (str.equalsIgnoreCase(act.getString(R.string.alert_111)) || str.indexOf(act.getString(R.string.alert_125_1)) > -1) {
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} else if (str.equals(act.getString(R.string.alert_ivalidtransaction))) {
						int flag = chkConnectivity();
						if (flag == 0) {
							CallWebService c = new CallWebService();
							c.execute();
						}
						
					} 
					else if(str.equalsIgnoreCase(act.getString(R.string.alert_0171)) || str.equalsIgnoreCase(act.getString(R.string.alert_020)))
					{
						Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
					}
					else if(str.equalsIgnoreCase(act.getString(R.string.alert_022)) || str.equalsIgnoreCase(act.getString(R.string.alert_023)) ||
							str.equalsIgnoreCase(act.getString(R.string.alert_017)))
					{
						Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
					}
					else if(str.equalsIgnoreCase(act.getString(R.string.alert_016)) || str.equalsIgnoreCase(act.getString(R.string.alert_019)) ||
							str.equalsIgnoreCase(act.getString(R.string.login_alert_008)))
					{
						Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
					}
					else if(str.equalsIgnoreCase(act.getString(R.string.alert_125)) || str.equalsIgnoreCase(act.getString(R.string.alert_167_2)) ||
							str.equalsIgnoreCase(act.getString(R.string.alert_ivalidtransaction)))
					{
						Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
					}
					else if(str.equalsIgnoreCase(act.getString(R.string.alert_018)) || str.equalsIgnoreCase(act.getString(R.string.alert_021)))
					{
						Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
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
		alert.setCancelable(false);
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
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
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

	public void showAlert1(String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass1 alert = new ErrorDialogClass1(this, "" + str);
		alert.show();
	}

	public class ErrorDialogClass1 extends Dialog implements OnClickListener {

		public ErrorDialogClass1(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		private Context activity;
		private Dialog d;
		private Button ok;
		private TextView txt_message;
		public String textMessage;

		public ErrorDialogClass1(Context activity, String textMessage) {
			super(activity);
			this.textMessage = textMessage;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.custom_dialog);
			ok = (Button) findViewById(R.id.btn_ok);
			txt_message = (TextView) findViewById(R.id.txt_dia);
			txt_message.setText(textMessage);
			ok.setOnClickListener(this);
		}// end onCreate

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_ok:

				if (strFromAct.equalsIgnoreCase("ENABLEATM")) {

					Intent in = new Intent(BeneficiaryOtp.this,
							MainActivity.class);
					Bundle b1 = new Bundle();
					b1.putInt("FRAGINDEX", 7);
					in.putExtra("var1", var1);
					in.putExtra("var3", var3);
					in.putExtras(b1);
					startActivity(in);
					finish();
				} else {

					Intent in = new Intent(BeneficiaryOtp.this,
							LoginActivity.class);
					in.putExtra("var1", var1);
					in.putExtra("var3", var3);

					startActivity(in);
					finish();
				}
				break;
			default:
				break;
			}
			dismiss();
		}
	}// end class

	public void onBackPressed() {
		
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
							retMess = getString(R.string.alert_076_1);
							showAlert(retMess);
						} else {
							retMess = getString(R.string.alert_076);
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
				showAlert(getString(R.string.alert_000));
			}
		}
	}

	public void postSuccess_validateOTP(String retval) {

		CallWebService_save_beneficiary storeTran = new CallWebService_save_beneficiary();
		storeTran.execute();
	}

	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> {

		// JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			strOTP = txt_otp.getText().toString().trim();
			strRefId = txt_ref_id.getText().toString().trim();
			strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();

			try {
				// jObj.put("OTPVAL",
				// ListEncryption.encryptData(strOTP+strCustId));
				String location = MBSUtils.getLocation(act);
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
				jObj.put("METHODCODE", "14");
				Log.e("sevebenf", "========" + jObj.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";

			try {
				String keyStr = CryptoClass.Function2();
				var2 = CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1",
						CryptoClass.Function5(jObj.toString(), var2));
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

					if (respdescvalidate.length() > 0) {
						showAlertPost(respdescvalidate);
					} else {
						// Log.e("TRANSFEROTP","retval==="+retval);
						if (retval.indexOf("FAILED") > -1) {
							// SUCCESS/FAILED~DUPLICATEACCOUNT/DUPLICATENICKNAME
							// FAILED~DUPLICATEACCOUNT~
							if (retval.indexOf("DUPLICATEACCOUNT") > -1) {

								// loadProBarObj.dismiss();
								// retMess="Duplicate Account Number. This Account Number Is Already Added.";
								// ///retMess =
								// "Failed To Add Other Bank Beneficiary Due To Duplicate Account Number.";
								/*
								 * if(strFromAct.equals("ADDOTHBENF")) { retMess
								 * = act.getString(R.string.alert_019); } else
								 */if (strFromAct.equals("ADDSAMBENF")) {
									retMess = getString(R.string.alert_016);
								 	}
									 else if(strFromAct.equals("ADDOTHERBENF")) {
									  retMess = getString(R.string.alert_019);
									  }
									 else {
										retMess = getString(R.string.login_alert_008);
									}

								showAlert(retMess);

							} else if (retval.indexOf("DUPLICATENICKNAME") > -1) {

								// loadProBarObj.dismiss();
								// retMess="Duplicate Nickname. Use Other Nickname.";
								// ////retMess="Failed To Add Other Bank Beneficiary Due To Duplicate Nickname.";

								/*
								 * if(strFromAct.equals("ADDOTHBENF")) { retMess
								 * = act.getString(R.string.alert_020); } else
								 */if (strFromAct.equals("ADDSAMBENF")) {
									retMess = getString(R.string.alert_0171);
								}
								else if (strFromAct.equals("ADDOTHERBENF")) {
									retMess = getString(R.string.alert_020);
								} 
								else {
									retMess = getString(R.string.login_alert_008);
								}

								showAlert(retMess);
							} /*
							 * else if (retval.indexOf("INCORRECTIFSC") > -1) {
							 * //loadProBarObj.dismiss(); //
							 * retMess="Duplicate Nickname. Use Other Nickname."
							 * ; // ////retMess=
							 * "Failed To Add Other Bank Beneficiary Due To Duplicate Nickname."
							 * ; retMess =
							 * act.getString(R.string.alert_185);//alert_018);
							 * showAlert(retMess); }
							 */
							else if (retval.indexOf("INCORRECTIFSC") > -1) {
/**/
								retMess = getString(R.string.alert_185);//alert_018);
								showAlert(retMess);
							}
							else if (retval.indexOf("WRONGMPIN") > -1) {
								// loadProBarObj.dismiss();
								retMess = getString(R.string.alert_125);
								showAlert(retMess);
							} else if (retval.indexOf("INVALIDTRANS") > -1) {
								retMess = getString(R.string.alert_ivalidtransaction);

								showAlert(retMess);

							} else if (retval.indexOf("InvalidAccount") > -1) {

								retMess = getString(R.string.alert_167_2);
								showAlert(retMess);

							} else {

								// /////retMess="Failed To Add Other Bank Beneficiary Due To Server Problem.";
								/*
								 * if(strFromAct.equals("ADDOTHBENF")) { retMess
								 * = act.getString(R.string.alert_021); } else
								 */if (strFromAct.equals("ADDSAMBENF")) {
									retMess = getString(R.string.alert_018);
								}
								
								else if (strFromAct.equals("ADDOTHERBENF")) {
									retMess = getString(R.string.alert_021);
								}
								// loadProBarObj.dismiss();
								showAlert(retMess);
								// initAll();

							}
							/*Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
							in.putExtra("var1", var1);
							in.putExtra("var3", var3);
							startActivity(in);
							finish();*/
						} else {
							// loadProBarObj.dismiss();
							post_successsaveBeneficiaries(retval);
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
				showAlert(getString(R.string.alert_000));
			}
		}
	}

	public void post_successresend(String retval) {
		txt_ref_id.setText("");
		txt_ref_id.setText(getString(R.string.lbl_ref_id) + " :"
				+ retval.split("~")[1].split("!!")[2]);
	}

	public void post_successsendcust(String retval) {

		respdescsendcust = "";
		respcode = "";
		showAlert1(getString(R.string.alert_send_custID));
	}

	public void showAlertPost(final String str) {

		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(BeneficiaryOtp.this,
						ManageBeneficiaryMenuActivity.class);
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

	public void post_successsaveBeneficiaries(String reTval) {
		/*
		 * if(strFromAct.equalsIgnoreCase("ADDOTHBENF")) { respcode=""; retMess
		 * =act.getString(R.string.alert_022); //showAlert(retMess); } else
		 */if (strFromAct.equalsIgnoreCase("ADDSAMBENF")) {
			respcode = "";
			retMess = getString(R.string.alert_023);

			// showAlert(retMess);
		}
		
		  else if(strFromAct.equalsIgnoreCase("ADDOTHERBENF")) {
			respcode="";
		  retMess = getString(R.string.alert_022); 
		  //showAlert(retMess); 
		  }
		 
		else {
			respcode = "";
			retMess = getString(R.string.alert_017);
		}

		showAlert(retMess);
		/*Intent in = new Intent(BeneficiaryOtp.this,ManageBeneficiaryMenuActivity.class);
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		startActivity(in);
		finish();*/
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out.println("OtherBankTranRTGS	in chkConnectivity () state1 ---------"							+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
					}
					break;
				case DISCONNECTED:
					flag = 1;
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					/*
					 * dbs = new DialogBox(this);
					 * dbs.get_adb().setMessage(retMess);
					 * dbs.get_adb().setPositiveButton("Ok", new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
					 * } }); dbs.get_adb().show();
					 */
					break;
				default:
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);

					/*
					 * dbs = new DialogBox(this);
					 * dbs.get_adb().setMessage(retMess);
					 * dbs.get_adb().setPositiveButton("Ok", new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
					 * Intent in = null; in = new
					 * Intent(getApplicationContext(), LoginActivity.class);
					 * startActivity(in); finish(); } }); dbs.get_adb().show();
					 */
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);

				/*
				 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
				 * dbs.get_adb().setPositiveButton("Ok", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
				 * Intent in = null; in = new Intent(getApplicationContext(),
				 * LoginActivity.class); startActivity(in); finish(); } });
				 * dbs.get_adb().show();
				 */
			}
		} catch (NullPointerException ne) {

			//Log.i("OtherBankTranRTGS    mayuri",					"NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

			/*
			 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
			 * dbs.get_adb().setPositiveButton("Ok", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
			 * in = null; in = new Intent(getApplicationContext(),
			 * LoginActivity.class); startActivity(in); finish(); } });
			 * dbs.get_adb().show();
			 */

		} catch (Exception e) {
			//Log.i("OtherBankTranRTGS   mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

			
		}
		return flag;
	}// end chkConnectivity

	
	public int chkConnectivityold() {
		// Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
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
							retMess = getString(R.string.alert_094);
							showAlert(retMess);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp
}
