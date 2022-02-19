package list.shivsamarth_mbs;

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


import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetMPIN extends Activity implements OnClickListener {
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	private static String METHOD_NAME_CHECK_AVAIL = "";
	EditText txt_enter_pass, txt_re_enter_pass;
	DatabaseManagement dbms;
	EditText txt_enter_tran_pass, txt_re_enter_tran_pass;
	ImageButton btn_home, btn_back;
	Button btn_save_pass, btn_reset;
	TextView txt_heading;
	ImageView img_heading;
	String enterMPIN, reEnteredMPIN, retVal, retMess, strCustId, queOne,
			queTwo, ansOne, ansTwo, strMobNo;
	String fromAct = "", enterTranMPIN = "", reEnteredTranMPIN = "",
			cust_name = "";
	private String userId, version, respcode = "", retval = "", respdesc = "",
			respdesc_changeMPIN = "", respdesc_available = "";
	String respdescSetMPIN = "", respdescChangeMPIN = "",
			respdescCheckUsrNmAvailability = "";
	int cnt = 0, flag = 0;
	Bundle b1;
	boolean WSCalled = false, isValidUser = false;
	boolean isWSCalled = false;
	private EditText txt_user_id;
	private MyThread t1;
	int timeOutInSecs = 300;
	private String availUserId;
	private TextView lbl_chk_avail;
	private LinearLayout lyt_usrnm_txt;
	private LinearLayout lyt_usrnm_lbl;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	private static final String MY_SESSION = "my_session";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_mpin);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
		Log.e("var1=======", "" + var1);
		Log.e("var3=======", var3);
		b1 = new Bundle();
		b1 = getIntent().getExtras();
		if (b1 != null) {
			strCustId = b1.getString("CUSTID");
			strMobNo = b1.getString("MOBNO");
			queOne = b1.getString("QUE1");
			queTwo = b1.getString("QUE2");
			ansOne = b1.getString("ANSWR1");
			ansTwo = b1.getString("ANSWR2");
			fromAct = b1.getString("FROMACT");
			userId = b1.getString("USERNAME");

		}
		txt_user_id = (EditText) findViewById(R.id.txt_prefer_usrname);
		lbl_chk_avail = (TextView) findViewById(R.id.check_availability);
		lyt_usrnm_txt = (LinearLayout) findViewById(R.id.lyt_usrnm_txt);
		lyt_usrnm_lbl = (LinearLayout) findViewById(R.id.lyt_usrnm_lbl);
		txt_enter_pass = (EditText) findViewById(R.id.txt_enter_pass);
		txt_re_enter_pass = (EditText) findViewById(R.id.txt_re_enter_pass);
		txt_enter_tran_pass = (EditText) findViewById(R.id.txt_enter_tran_pass);
		txt_re_enter_tran_pass = (EditText) findViewById(R.id.txt_re_enter_tran_pass);

		btn_save_pass = (Button) findViewById(R.id.btn_save_pass);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.change_mpin);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		//btn_home.setImageResource(R.mipmap.bank_logo);
		btn_back.setImageResource(R.mipmap.backover);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_set_mpin));
		btn_save_pass.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
		// btn_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		Log.e("SetMPIN", "fromAct======" + fromAct + "==userId==" + userId);
		if (fromAct.equalsIgnoreCase("REGISTER") || userId.equals("NA")) {
			lbl_chk_avail.setOnClickListener(this);
		} else {
			lyt_usrnm_txt.setVisibility(LinearLayout.GONE);
			lyt_usrnm_lbl.setVisibility(LinearLayout.GONE);
			txt_user_id.setText(userId);
		}
		// SharedPreferences sp =
		// this.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		// cust_name = sp.getString("userId", "userId");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				cust_name = c1.getString(3);
				Log.e("userId", "......" + cust_name);
			}
		}
		t1 = new MyThread(timeOutInSecs, this, var1, var3);
		t1.start();
	}

	public boolean isAlphaNumeric(String s) {
		String pattern = "^[a-zA-Z0-9]*$";
		Log.e("SETMPIN",
				"string==" + s + "===pattern match===" + s.matches(pattern));
		if (s.matches(pattern)) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			Intent in = new Intent(this, LoginActivity.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		case R.id.btn_save_pass:
			// Log.e("========","=====clickedddddd==="+fromAct);
			enterMPIN = txt_enter_pass.getText().toString().trim();
			reEnteredMPIN = txt_re_enter_pass.getText().toString().trim();
			enterTranMPIN = txt_enter_tran_pass.getText().toString().trim();
			reEnteredTranMPIN = txt_re_enter_tran_pass.getText().toString()
					.trim();

			if (enterMPIN == null || enterMPIN.length() == 0) {
				showAlert(getString(R.string.alert_mpin));
			} else if (reEnteredMPIN == null || reEnteredMPIN.length() == 0) {
				showAlert(getString(R.string.alert_rempin));
			} else if (enterTranMPIN == null || enterTranMPIN.length() == 0) {
				showAlert(getString(R.string.alert_tran));
			} else if (reEnteredTranMPIN == null
					|| reEnteredTranMPIN.length() == 0) {
				showAlert(getString(R.string.alert_retran));
			} else if (enterMPIN.length() != 6 || reEnteredMPIN.length() != 6) {
				showAlert(getString(R.string.alert_mpin_6_length));
			} else if (enterTranMPIN.length() != 6
					|| reEnteredTranMPIN.length() != 6) {
				showAlert(getString(R.string.alert_tran_6_length));
			} else if (!enterMPIN.equals(reEnteredMPIN)) {
				showAlert(getString(R.string.alert_mpin_same));
			} else if (!enterTranMPIN.equals(reEnteredTranMPIN)) {
				showAlert(getString(R.string.alert_tran_same));
			} else if (enterMPIN.equalsIgnoreCase(enterTranMPIN)) {
				showAlert(getString(R.string.alert_mpin_tran_diff));
			} else {
				if (fromAct.equalsIgnoreCase("REGISTER")) {
					userId = txt_user_id.getText().toString().trim();
					if (userId == null || userId.length() == 0) {
						showAlert(getString(R.string.alert_cust_user));
					} else if (userId.trim().length() < 5
							|| userId.trim().length() > 9) {
						showAlert(getString(R.string.alert_userid_5to9));
					} else if (!isAlphaNumeric(userId.trim())) {
						showAlert(getString(R.string.alert_usernm_alph));
					} else {
						flag = chkConnectivity();
						if (flag == 0) {
							Log.e("isValidUser", "isValidUser " + isValidUser);
							if (!isValidUser) {
								showAlert(getString(R.string.alert_user_avbl));
								/*
								 * CallWebServiceCheckUsrNmAvailability c = new
								 * CallWebServiceCheckUsrNmAvailability();
								 * c.execute();
								 */
							} else if (isValidUser) {
								CallWebServiceSetMPIN c = new CallWebServiceSetMPIN();
								c.execute();
							}
						}
					}
				} else if (fromAct.equalsIgnoreCase("FORGOT")) {
					// flag = chkConnectivity();
					// if (flag == 0)
					{
						CallWebServiceChangeMPIN c = new CallWebServiceChangeMPIN();
						c.execute();
					}
				}
			}
			break;
		case R.id.btn_reset:
			txt_enter_pass.setText("");
			txt_enter_pass.requestFocus();
			txt_re_enter_pass.setText("");
			txt_enter_tran_pass.setText("");
			txt_re_enter_tran_pass.setText("");
			if (fromAct.equalsIgnoreCase("REGISTER")) {
				txt_user_id.setEnabled(true);
				txt_user_id.setText("");
				isValidUser = false;
			}
			break;
		case R.id.check_availability:
			userId = txt_user_id.getText().toString().trim();
			Log.e("check_availability=", "UserId= " + userId.length());
			if (userId == null || userId.length() == 0) {
				showAlert(getString(R.string.alert_cust_user));
			} else if (userId.trim().length() < 5 || userId.trim().length() > 9) {
				showAlert(getString(R.string.alert_userid_5to9));
			} else if (!isAlphaNumeric(userId.trim())) {
				showAlert(getString(R.string.alert_usernm_alph));
			} else {
				CallWebServiceCheckUsrNmAvailability c = new CallWebServiceCheckUsrNmAvailability();
				c.execute();
			}
			break;

		default:
			break;
		}
	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {

			@Override
			public void onClick(View v) {
				// Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) {
				case R.id.btn_ok:

					if ((str.equalsIgnoreCase(respdescSetMPIN))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successSetMPIN(retval);
					} else if ((str.equalsIgnoreCase(respdescSetMPIN))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdescChangeMPIN))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successChangeMPIN(retval);
					} else if ((str.equalsIgnoreCase(respdescChangeMPIN))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str
							.equalsIgnoreCase(respdescCheckUsrNmAvailability))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successUsrNmAvailability(retval);
					} else if ((str
							.equalsIgnoreCase(respdescCheckUsrNmAvailability))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					}
					// Log.e("SetMPIN","SetMPIN...CASE trru="+isWSCalled);
					if (textMessage.equalsIgnoreCase(SetMPIN.this
							.getString(R.string.alert_070))) {
						// Log.e("SetMPIN","SetMPIN...mpin set");
						Intent in = new Intent(SetMPIN.this,
								LoginActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						startActivity(in);
						finish();
					} else

					if (textMessage.equalsIgnoreCase(SetMPIN.this
							.getString(R.string.alert_103))) {
						// Log.e("SetMPIN","SetMPIN...mpin set");
						Bundle bObj = new Bundle();
						Intent in = new Intent(SetMPIN.this,
								LoginActivity.class);
						bObj.putString("CUSTID", strCustId);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
						in.putExtras(bObj);
						startActivity(in);
						finish();
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

	class CallWebServiceSetMPIN extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(SetMPIN.this);

		boolean isWSCalled = false;

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			enterMPIN = txt_enter_pass.getText().toString().trim();
			enterTranMPIN = txt_enter_tran_pass.getText().toString().trim();
			userId = txt_user_id.getText().toString().trim();

			try {
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("QUE1", queOne);
				jsonObj.put("ANS1", ansOne);// ListEncryption.encryptData(ansOne+strCustId));
				jsonObj.put("QUE2", queTwo);
				jsonObj.put("ANS2", ansTwo);// ListEncryption.encryptData(ansTwo+strCustId));
				jsonObj.put("MPIN", enterMPIN);// ListEncryption.encryptData(strCustId
												// + enterMPIN));
				jsonObj.put("TRANMPIN", enterTranMPIN);// ListEncryption.encryptData(strCustId+enterTranMPIN));
				jsonObj.put("REGMOBNO", strMobNo);
				jsonObj.put("USERNAME", userId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SetMPIN.this));
				jsonObj.put("USRMPIN", enterMPIN);// ListEncryption.encryptData(userId
													// + enterMPIN));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(SetMPIN.this));
				jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(SetMPIN.this));
				jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
				String location = MBSUtils.getLocation(SetMPIN.this);
				jsonObj.put("LATITUDE", location.split("~")[0]);
				jsonObj.put("LONGITUDE", location.split("~")[1]);
				jsonObj.put("METHODCODE", "22");

				Log.e("jsonObj set mpin", "=========== :" + jsonObj.toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

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
				// String[] xmlTags = { "STATUS" };
				// String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);

				// Log.e("SecurityQuestion", "xml_data.length==" +
				// xml_data.length);

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
						retval = jsonObj.getString("RETVAL");
					} else {
						retval = "";
					}
					if (jsonObj.has("RESPDESC")) {
						respdescSetMPIN = jsonObj.getString("RESPDESC");
					} else {
						respdescSetMPIN = "";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (respdescSetMPIN.length() > 0) {
					showAlert(respdescSetMPIN);
				} else {

					if (retval.indexOf("SUCCESS") > -1) {

						post_successSetMPIN(retval);

					} else if (retval.indexOf("FAILED~") > -1) {
						String retCode = retval.split("~")[1];
						if (retCode.equalsIgnoreCase("1"))
							showAlert(getString(R.string.alert_122));
						else if (retCode.equalsIgnoreCase("2"))
							showAlert(getString(R.string.alert_123));
						else
							showAlert(getString(R.string.alert_085));
					} else {
						System.out
								.println("in else ***************************************");
						showAlert(getString(R.string.alert_085));
					}
				}
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
			/*
			 * loadProBarObj.dismiss(); if (isWSCalled) { //String[] xmlTags = {
			 * "STATUS" }; //String[] xml_data = CryptoUtil.readXML(retVal,
			 * xmlTags); String[] xml_data = CryptoUtil.readXML(retVal, new
			 * String[]{"PARAMS"}); // Log.e("SecurityQuestion",
			 * "xml_data.length==" + // xml_data.length);
			 * Log.e("SecurityQuestion", "xml_data[0]==" + xml_data[0]);
			 * 
			 * if (xml_data[0].indexOf("SUCCESS") > -1) { WSCalled = true;
			 * showAlert(getString(R.string.alert_103));
			 * 
			 * // dbms = new DatabaseManagement("rajapur.mbank","panchgangaMBS"); //dbms = new
			 * DatabaseManagement("panchganga.mobilebank", "panchgangaMBS");
			 * String str="";
			 * 
			 * String[] coulmnsAndTypes={"CFG_CUST_ID","varchar(10)"}; String[]
			 * colNms={"CFG_CUST_ID"}; String[] val=new String[1];
			 * val[0]=strCustId;
			 * 
			 * try { str=dbms.createTable("CONFIG", coulmnsAndTypes); }
			 * catch(Exception e) { e.printStackTrace(); }
			 * Log.e("SETMPIN","str after create table==="+str); try {
			 * str=dbms.deleteFromTable("CONFIG", null, null); } catch(Exception
			 * e) { e.printStackTrace(); } try {
			 * str=dbms.insertIntoTable("CONFIG", 1, colNms, val); }
			 * catch(Exception e) { e.printStackTrace(); }
			 * Log.e("SETMPIN","str after insert==="+str);
			 * 
			 * } else if (xml_data[0].indexOf("FAILED~") > -1) { String retCode
			 * = xml_data[0].split("~")[1]; if (retCode.equalsIgnoreCase("1"))
			 * showAlert(getString(R.string.alert_122)); else if
			 * (retCode.equalsIgnoreCase("2"))
			 * showAlert(getString(R.string.alert_123)); else
			 * showAlert(getString(R.string.alert_085)); } else { System.out
			 * .println("in else ***************************************");
			 * showAlert(getString(R.string.alert_085)); } } else { retMess =
			 * getString(R.string.alert_000); showAlert(retMess); }
			 */
		}

	}

	class CallWebServiceChangeMPIN extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(SetMPIN.this);

		boolean isWSCalled = false;

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			Log.i("Shrikant status", "preExecute in Change MPIN ");
			loadProBarObj.show();
			Log.e("SETMPIN", "cust_name==" + cust_name);
			enterMPIN = txt_enter_pass.getText().toString().trim();
			enterTranMPIN = txt_enter_tran_pass.getText().toString().trim();
			Log.e("tranpin", "--------" + enterTranMPIN);

			try {
				String location = MBSUtils.getLocation(SetMPIN.this);
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("MPIN", enterMPIN.trim());// ListEncryption.encryptData(strCustId+
														// enterMPIN.trim()));
				jsonObj.put("TRANMPIN", enterTranMPIN.trim());// ListEncryption.encryptData(strCustId+
																// enterTranMPIN.trim()));
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SetMPIN.this));
				jsonObj.put("USRMPIN", enterMPIN);// ListEncryption.encryptData(userId
													// + enterMPIN));
				jsonObj.put("USRID", userId);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(SetMPIN.this));
				jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(SetMPIN.this));
				jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
				jsonObj.put("LATITUDE", location.split("~")[0]);
				jsonObj.put("LONGITUDE", location.split("~")[1]);
				jsonObj.put("METHODCODE", "03");

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
				// String[] xmlTags = { "STATUS" };
				// String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);

				// Log.e("SecurityQuestion", "xml_data.length==" +
				// xml_data.length);
				// Log.e("SecurityQuestion", "xml_data==" + xml_data[0]);
				// xml_data[0]="SUCCESS";
				JSONObject jsonObj;
				try {
					String str = CryptoClass.Function6(var5, var2);

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
						respdescChangeMPIN = jsonObj.getString("RESPDESC");
					} else {
						respdescChangeMPIN = "";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (respdescChangeMPIN.length() > 0) {
					showAlert(respdescChangeMPIN);
				} else {
					if (retval.indexOf("SUCCESS") > -1) {
						post_successChangeMPIN(retval);

					} else if (retval.indexOf("FAILED~") > -1) {
						String retCode = retval.split("~")[1];
						if (retCode.equalsIgnoreCase("1"))
							showAlert(getString(R.string.alert_122));
						else if (retCode.equalsIgnoreCase("2"))
							showAlert(getString(R.string.alert_123));
						else
							showAlert(getString(R.string.alert_085));
					} else {
						// System.out.println("in else ***************************************");
						showAlert(getString(R.string.alert_085));
					}
				}
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
			/*
			 * loadProBarObj.dismiss(); if (isWSCalled) { //String[] xmlTags = {
			 * "STATUS" }; //String[] xml_data = CryptoUtil.readXML(retVal,
			 * xmlTags); String[] xml_data = CryptoUtil.readXML(retVal, new
			 * String[]{"PARAMS"}); // Log.e("SecurityQuestion",
			 * "xml_data.length==" + // xml_data.length); //
			 * Log.e("SecurityQuestion", "xml_data==" + xml_data[0]); //
			 * xml_data[0]="SUCCESS"; if (xml_data[0].indexOf("SUCCESS") > -1) {
			 * WSCalled = true; showAlert(getString(R.string.alert_070)); String
			 * textMessage="";
			 * if(textMessage.equalsIgnoreCase(SetMPIN.this.getString
			 * (R.string.alert_070))) { //Log.e("SetMPIN","SetMPIN...mpin set");
			 * Intent in = new Intent(SetMPIN.this,LoginActivity.class);
			 * startActivity(in); finish(); }
			 * 
			 * 
			 * 
			 * 
			 * 
			 * Intent in = new Intent(SetMPIN.this,LoginActivity.class);
			 * startActivity(in); } else if (xml_data[0].indexOf("FAILED~") >
			 * -1) { String retCode = xml_data[0].split("~")[1]; if
			 * (retCode.equalsIgnoreCase("1"))
			 * showAlert(getString(R.string.alert_122)); else if
			 * (retCode.equalsIgnoreCase("2"))
			 * showAlert(getString(R.string.alert_123)); else
			 * showAlert(getString(R.string.alert_085)); } else { //
			 * System.out.println
			 * ("in else ***************************************");
			 * showAlert(getString(R.string.alert_085)); } } else { retMess =
			 * getString(R.string.alert_000); showAlert(retMess); }
			 */
		}

	}

	class CallWebServiceCheckUsrNmAvailability extends
			AsyncTask<Void, Void, Void> {

		// String[] xmlTags = { "CUSTID", "USERNAME", "IMEINO" };
		// String[] valuesToEncrypt = new String[3];

		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(SetMPIN.this);

		// boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			userId = txt_user_id.getText().toString().trim();

			try {
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("USERNAME", userId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SetMPIN.this));

				jsonObj.put("SIMNO", MBSUtils.getSimNumber(SetMPIN.this));
				jsonObj.put("METHODCODE", "40");

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

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
				// String[] xmlTags = { "STATUS" };
				// String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);

				JSONObject jsonObj;
				try {
					String str = CryptoClass.Function6(var5, var2);
					jsonObj = new JSONObject(str.trim());
					// Log.e("IN return", "data :" + jsonObj.toString());
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
						respdescCheckUsrNmAvailability = jsonObj
								.getString("RESPDESC");
					} else {
						respdescCheckUsrNmAvailability = "";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (respdescCheckUsrNmAvailability.length() > 0) {
					showAlert(respdescCheckUsrNmAvailability);
				} else {
					if (retval.indexOf("UNAVAILABLE") > -1) {
						isValidUser=false;
						//showAlert(getString(R.string.alert_138));
						txt_user_id.setText("");
						post_successUsrNmAvailability(retval);

					} else {
						isValidUser = true;
						txt_user_id.setEnabled(false);
						showAlert(getString(R.string.alert_137));
					}
				}
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
			/*
			 * loadProBarObj.dismiss(); if (isWSCalled) { //String[] xmlTags = {
			 * "STATUS" }; //String[] xml_data = CryptoUtil.readXML(retVal,
			 * xmlTags); String[] xml_data = CryptoUtil.readXML(retVal, new
			 * String[]{"PARAMS"});
			 * 
			 * if (xml_data[0].indexOf("UNAVAILABLE") > -1) { isValidUser=false;
			 * showAlert(getString(R.string.alert_138));
			 * txt_user_id.setText(""); } else { isValidUser = true;
			 * 
			 * showAlert(getString(R.string.alert_137)); } } else { retMess =
			 * getString(R.string.alert_000); showAlert(retMess); }
			 */
		}

	}

	public void post_successSetMPIN(String retval) {
		respdescSetMPIN = "";
		respcode = "";
		WSCalled = true;
		showAlert(getString(R.string.alert_103));
		String str = "";

		String[] coulmnsAndTypes = { "CFG_CUST_ID", "varchar(10)" };
		String[] colNms = { "CFG_CUST_ID" };
		String[] val = new String[1];
		val[0] = strCustId;

		try {
			str = dbms.createTable("CONFIG", coulmnsAndTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("SETMPIN", "str after create table===" + str);
		try {
			str = dbms.deleteFromTable("CONFIG", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			str = dbms.insertIntoTable("CONFIG", 1, colNms, val);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("SETMPIN", "str after insert===" + str);
	}

	public void post_successChangeMPIN(String retval) {

		respdescChangeMPIN = "";
		respcode = "";
		WSCalled = true;
		showAlert(getString(R.string.alert_070));
	}

	public void post_successUsrNmAvailability(String retval) {

		respdescCheckUsrNmAvailability = "";
		respcode = "";
		isValidUser = false;
		showAlert(getString(R.string.alert_138));
		txt_user_id.setText("");
	}

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			// System.out.println("state1 ---------" + state1);
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

					break;
				default:
					flag = 1;
					retMess = getString(R.string.alert_000);
					// setAlert();
					showAlert(retMess);

					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				// setAlert();
				showAlert(retMess);

			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);

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
}
