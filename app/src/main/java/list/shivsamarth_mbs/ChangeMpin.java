package list.shivsamarth_mbs;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;

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
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ChangeMpin extends Activity implements OnClickListener {
	ChangeMpin changMpin;
	LinearLayout layout_mpin, layout_tranmpin, layout_otp;
	TextView cust_nm, txt_heading, txt_ref_id;
	Button btnChangeMpin;
	RadioButton radiobtn_tranmpin, radiobtn_mpin;
	RadioGroup groupradio;
	int index;
	String strmpin, respcode = "", strRefId = "", retval = "",
			respdescchangempin = "", respdescvalidateotp = "",
			retvalvalidateotp = "", respdescchangeTranMPIN = "", strMobNo = "",
			retvalotp = "", respdescresend = "";
	EditText et_old_mpin, et_new_mpin, et_renew_mpin;
	EditText et_old_tran_mpin, et_new_tran_mpin, et_renew_tran_mpin, etotptxt;
	ChangeMpin changeAct = this;
	String imeiNo = "", tmpXMLString = "", retMess = "", pin = "",
			tranPin = "", userId = "", otpvalset = "";
	TelephonyManager telephonyManager;
	ImageButton btn_home, btn_back;
	ImageView img_heading;
	private MyThread t1;
	int timeOutInSecs = 300;
	int cnt = 0, flag = 0, radioflag = 0;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	DatabaseManagement dbms;
	DialogBox dbs;
	// ProgressBar pb_wait;
	private static final String MY_SESSION = "my_session";
	boolean isWSCalled = false;
	Editor e;
	String custId = "", cust_name = "", encrptMpin = "",
			encrptOldUserMpin = "", encrptTranMpin = "", encrptOldMpin = "",
			encrptOldTranMpin = "", encrptUserMpin = "";
	ChangeMpin obj;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	public ChangeMpin() {
	}

	public ChangeMpin(ChangeMpin a) {
		// System.out.println("v()"+a);
		changMpin = a;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_mpin);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		changMpin = this;
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		et_old_mpin = (EditText) findViewById(R.id.etOldMpin);
		et_new_mpin = (EditText) findViewById(R.id.etNewMpin);
		et_renew_mpin = (EditText) findViewById(R.id.etRetypeNewMpin);

		et_old_tran_mpin = (EditText) findViewById(R.id.etOldTranMpin);
		et_new_tran_mpin = (EditText) findViewById(R.id.etNewTranMpin);
		et_renew_tran_mpin = (EditText) findViewById(R.id.etRetypeNewTranMpin);
		layout_mpin = (LinearLayout) findViewById(R.id.layout_mpin);
		layout_tranmpin = (LinearLayout) findViewById(R.id.layout_tranmpin);
		layout_otp = (LinearLayout) findViewById(R.id.layout_otp);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.change_mpin);
		txt_ref_id = (TextView) findViewById(R.id.txt_ref_id);
		txt_heading.setText(getString(R.string.lbl_title_change_mpin));
		etotptxt = (EditText) findViewById(R.id.etotptxt);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);

		groupradio = (RadioGroup) findViewById(R.id.groupradio);
		radiobtn_mpin = (RadioButton) findViewById(R.id.radiobtn_mpin);
		radiobtn_tranmpin = (RadioButton) findViewById(R.id.radiobtn_tranmpin);
		groupradio
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						View radioButton = groupradio.findViewById(checkedId);
						radioflag = 1;
						index = checkedId;

						Log.e("iiiiii", "1111111*****" + radioButton.getId());
						Log.e("iiiiii", "1111111*****" + R.id.radiobtn_mpin);
						Log.e("iiiiii", "22222*****" + R.id.radiobtn_tranmpin);
						if (radioButton.getId() == R.id.radiobtn_mpin) {
							radiobtn_tranmpin.setSelected(false);
							radiobtn_tranmpin.setChecked(false);
							strmpin = "MPIN";
							layout_mpin.setVisibility(LinearLayout.VISIBLE);
							layout_tranmpin.setVisibility(LinearLayout.GONE);
							et_old_mpin.setText("");
							et_new_mpin.setText("");
							et_renew_mpin.setText("");
							layout_otp.setVisibility(LinearLayout.GONE);
							btnChangeMpin
									.setText(getString(R.string.lbl_genotp_btn));
							txt_ref_id.setText("");
							etotptxt.setText("");
						} else if (radioButton.getId() == R.id.radiobtn_tranmpin) {
							radiobtn_mpin.setSelected(false);
							radiobtn_mpin.setChecked(false);
							strmpin = "TRANMPIN";
							layout_tranmpin.setVisibility(LinearLayout.VISIBLE);
							layout_mpin.setVisibility(LinearLayout.GONE);
							et_old_tran_mpin.setText("");
							et_new_tran_mpin.setText("");
							et_renew_tran_mpin.setText("");

							layout_otp.setVisibility(LinearLayout.GONE);
							btnChangeMpin
									.setText(getString(R.string.lbl_genotp_btn));
							txt_ref_id.setText("");
							etotptxt.setText("");
						} else {
							strmpin = "NOTSELECT";
						}
					}
				});
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		btnChangeMpin = (Button) findViewById(R.id.btnChangeMpin);
		btnChangeMpin.setOnClickListener(this);

		// imeiNo = MBSUtils.getImeiNumber(act);
		// dbs = new DialogBox(act);

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				cust_name = c1.getString(0);
				Log.e("retvatstr", "...." + cust_name);
				custId = c1.getString(2);
				Log.e("custId", "......" + custId);
				userId = c1.getString(3);
				Log.e("UserId", "c......" + userId);
			}
		}
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnChangeMpin:
			if (btnChangeMpin.getText().equals(
					getString(R.string.lbl_genotp_btn))) {
				String oldMpin = et_old_mpin.getText().toString().trim();
				String newMpin = et_new_mpin.getText().toString().trim();
				String reNewMpin = et_renew_mpin.getText().toString().trim();

				String oldTranMpin = et_old_tran_mpin.getText().toString()
						.trim();
				String newTranMpin = et_new_tran_mpin.getText().toString()
						.trim();
				String reNewTranMpin = et_renew_tran_mpin.getText().toString()
						.trim();

				encrptOldMpin = oldMpin;// ListEncryption.encryptData(custId+oldMpin);
				encrptUserMpin = oldMpin;// ListEncryption.encryptData(userId+oldMpin);
				encrptOldTranMpin = oldTranMpin;// ListEncryption.encryptData(custId+oldTranMpin);

				encrptMpin = newMpin;// ListEncryption.encryptData(custId+newMpin);
				encrptTranMpin = newTranMpin;// ListEncryption.encryptData(custId+newTranMpin);

				if (radioflag == 0) {
					retMess = "Please select MPIN Or TRANSACTION MPIN ";
					showAlert(retMess);
				} else {

					flag = chkConnectivity();
					if (flag == 0) {
						if (strmpin.equalsIgnoreCase("MPIN")) {
							if (oldMpin.equals("") || newMpin.equals("")
									|| reNewMpin.equals("")) {
								cnt = 0;
								retMess = "Please fill all fields!";
								showAlert(retMess);
							}

							else if (!newMpin.equals(reNewMpin)) {
								cnt = 0;
								retMess = getString(R.string.alert_Changempin_1);
								showAlert(retMess);
							} else if (newMpin.length() != 6
									|| reNewMpin.length() != 6) {
								showAlert(getString(R.string.alert_Changempin_2));
							} else {
								layout_otp.setVisibility(LinearLayout.VISIBLE);
								new CallGenerateOTPWebService().execute();
								// new CallWebService().execute();
							}
						} else if (strmpin.equalsIgnoreCase("TRANMPIN")) {
							if (oldTranMpin.equals("")
									|| newTranMpin.equals("")
									|| reNewTranMpin.equals("")) {
								cnt = 0;
								retMess = "Please fill all fields!";
								showAlert(retMess);
							} else if (newTranMpin.length() != 6
									|| reNewTranMpin.length() != 6) {
								showAlert(getString(R.string.alert_Changempin_3));
							} else if (!newTranMpin.equals(reNewTranMpin)) {
								cnt = 0;
								retMess = getString(R.string.alert_Changempin_4);
								showAlert(retMess);
							} else {
								layout_otp.setVisibility(LinearLayout.VISIBLE);
								new CallGenerateOTPWebService().execute();
								// new CallWebServicetran().execute();
							}
						} else {

							retMess = "Please select MPIN Or TRANSACTION MPIN ";
							showAlert(retMess);
						}
					}

				}
			} else {

				String oldMpin = et_old_mpin.getText().toString().trim();
				String newMpin = et_new_mpin.getText().toString().trim();
				String reNewMpin = et_renew_mpin.getText().toString().trim();
				String txtotp = etotptxt.getText().toString().trim();
				String oldTranMpin = et_old_tran_mpin.getText().toString()
						.trim();
				String newTranMpin = et_new_tran_mpin.getText().toString()
						.trim();
				String reNewTranMpin = et_renew_tran_mpin.getText().toString()
						.trim();
				encrptOldMpin = oldMpin;// ListEncryption.encryptData(custId +
										// oldMpin);
				encrptUserMpin = oldMpin;// ListEncryption.encryptData(userId +
											// oldMpin);
				encrptOldTranMpin = oldTranMpin;// ListEncryption.encryptData(custId
				// + oldTranMpin);
				encrptOldUserMpin = oldMpin;// ListEncryption
				// .encryptData(userId + oldMpin);

				encrptMpin = newMpin;// ListEncryption.encryptData(custId +
										// newMpin);
				encrptTranMpin = newTranMpin;// ListEncryption.encryptData(custId
				// + newTranMpin);
				encrptUserMpin = newMpin;// ListEncryption.encryptData(userId +
											// newMpin);
				if (radioflag == 0) {
					retMess = "Please select MPIN Or TRANSACTION MPIN ";
					showAlert(retMess);
				} else {

					flag = chkConnectivity();
					if (flag == 0) {
						if (strmpin.equalsIgnoreCase("MPIN")) {
							if (oldMpin.equals("") || newMpin.equals("")
									|| reNewMpin.equals("")) {

								cnt = 0;
								retMess = "Please fill all fields!";
								showAlert(retMess);
							}
							/*
							 * else if (!(encrptOldMpin.equals(pin) ||
							 * encrptUserMpin.equals(pin))) { cnt = 0; retMess =
							 * getString(R.string.alert_050);
							 * showAlert(retMess); }
							 * 
							 * else if (!(encrptOldMpin.equals(pin) ||
							 * encrptUserMpin.equals(pin))) { cnt = 0; retMess =
							 * getString(R.string.alert_115);
							 * showAlert(retMess); }
							 */
							else if (!newMpin.equals(reNewMpin)) {
								cnt = 0;
								retMess = getString(R.string.alert_Changempin_1);
								showAlert(retMess);
							} else if (newMpin.length() != 6
									|| reNewMpin.length() != 6) {
								showAlert(getString(R.string.alert_Changempin_2));
							} else if (txtotp.length() != 6) {
								showAlert(getString(R.string.alert_076));
							} else {

								new CallWebServiceValidateOTP().execute();

							}

						} else if (strmpin.equalsIgnoreCase("TRANMPIN")) {
							if (oldTranMpin.equals("")
									|| newTranMpin.equals("")
									|| reNewTranMpin.equals("")) {
								cnt = 0;
								retMess = "Please fill all fields!";
								showAlert(retMess);
							} else if (newTranMpin.length() != 6
									|| reNewTranMpin.length() != 6) {

								showAlert(getString(R.string.alert_tran_6_length));
							}
							/*
							 * else
							 * if(encrptMpin.equalsIgnoreCase(encrptTranMpin)) {
							 * showAlert(getString(R.string.alert_124)); }
							 */
							else if (!newTranMpin.equals(reNewTranMpin)) {
								cnt = 0;
								retMess = getString(R.string.alert_tran_same);
								showAlert(retMess);
							} else if (txtotp.length() != 6) {
								showAlert(getString(R.string.alert_076));
							} else {
								new CallWebServiceValidateOTP().execute();
							}
						} else {

							retMess = "Please select MPIN Or TRANSACTION MPIN ";
							showAlert(retMess);
						}
					}

				}
			}
			break;
		case R.id.btn_back:
			Intent in = new Intent(this, DashboardActivity.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
			break;
		case R.id.btn_home:
			Intent intent = new Intent(this, DashboardActivity.class);
			intent.putExtra("var1", var1);
			intent.putExtra("var3", var3);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}

	class CallGenerateOTPWebService extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(ChangeMpin.this);

		boolean isWSCalled = false;

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			// ValidationData=MBSUtils.getValidationData(logAct);

			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("REQSTATUS", "R");
				jsonObj.put("REQFROM", "MBSCH");
				jsonObj.put("MOBNO", strMobNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ChangeMpin.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ChangeMpin.this));
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
					String str = CryptoClass.Function6(var5, var2);
					jsonObj = new JSONObject(str.trim());

					if (jsonObj.has("RESPCODE")) {
						respcode = jsonObj.getString("RESPCODE");
					} else {
						respcode = "-1";
					}
					if (jsonObj.has("RETVAL")) {
						retvalotp = jsonObj.getString("RETVAL");
					} else {
						retvalotp = "";
					}
					if (jsonObj.has("RESPDESC")) {
						respdescresend = jsonObj.getString("RESPDESC");
					} else {
						respdescresend = "";
					}
					if (respdescresend.length() > 0) {
						showAlert(respdescresend);
					} else {
						if (retvalotp.split("~")[0].indexOf("SUCCESS") > -1) {
							post_successresend(retvalotp);
						} else {
							retMess = ChangeMpin.this
									.getString(R.string.alert_094);
							showAlert(retMess);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				retMess = ChangeMpin.this.getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp

	public void post_successresend(String retvalstr) {

		String returnstr = retvalstr.split("~")[1];
		String val[] = returnstr.split("!!");
		strRefId = val[2];
		layout_otp.setVisibility(LinearLayout.VISIBLE);

		// txt_ref_id.setText(txt_ref_id.getText().toString() + " :" +
		// strRefId);
		txt_ref_id.setText(ChangeMpin.this.getString(R.string.lbl_ref_id)
				+ " :" + strRefId);
		btnChangeMpin.setText(getString(R.string.lbl_change_btn));

	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(ChangeMpin.this, "" + str) {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdescchangempin))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successchangempin(retval);
					} else if ((str.equalsIgnoreCase(respdescchangempin))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdescchangeTranMPIN))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successcchangeTranMPIN(retval);
					} else if ((str.equalsIgnoreCase(respdescchangeTranMPIN))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdescresend))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successresend(retvalotp);
					} else if ((str.equalsIgnoreCase(respdescresend))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if ((str.equalsIgnoreCase(respdescvalidateotp))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successvalidate(retvalvalidateotp);
					} else if ((str.equalsIgnoreCase(respdescvalidateotp))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else if (isWSCalled) {
						Intent in = new Intent(ChangeMpin.this,
								DashboardActivity.class);
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
		};
		alert.show();
	}

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) changMpin
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	class CallWebService extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(ChangeMpin.this);
		String oldMpin = "", newMpin = "", reNewMpin = "", newTranMpin = "",
				otpval = "";;

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();

			// System.out.println("custId:"+custId);
			// System.out.println("newMpin:"+newMpin);
			oldMpin = et_old_mpin.getText().toString().trim();
			newMpin = et_new_mpin.getText().toString().trim();
			encrptUserMpin = newMpin;// ListEncryption.encryptData(userId+newMpin);
			// reNewMpin = et_renew_mpin.getText().toString();
			// newTranMpin = et_new_tran_mpin.getText().toString();

			encrptMpin = newMpin;// ListEncryption.encryptData(custId+newMpin);
			// otpval = etotptxt.getText().toString().trim();
			// strRefId = txt_ref_id.getText().toString().trim();
			// strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();
			// encrptTranMpin=ListEncryption.encryptData(custId+newTranMpin);*/
			try {
				String location=MBSUtils.getLocation(ChangeMpin.this);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("MPIN", encrptMpin);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ChangeMpin.this));
				jsonObj.put("USRMPIN", encrptUserMpin);
				jsonObj.put("USRID", userId);
				jsonObj.put("OLDMPIN", oldMpin); // ListEncryption.encryptData(custId+oldMpin));
				jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(ChangeMpin.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ChangeMpin.this));
			    jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
	            jsonObj.put("OSVERSION", Build.VERSION.RELEASE);	             
	            jsonObj.put("LATITUDE", location.split("~")[0]);
	            jsonObj.put("LONGITUDE", location.split("~")[1]);
	            jsonObj.put("REFNO", strRefId);
				jsonObj.put("OTPVAL", otpvalset);
				jsonObj.put("METHODCODE", "4");
				
				Log.e("data","jsonObjchgmpin====="+jsonObj);
				// valuesToEncrypt[0] = custid;
				// valuesToEncrypt[1] =
				// MBSUtils.getImeiNumber(DashboardDesignActivity.this);
			} catch (JSONException je) {
				je.printStackTrace();
			}

			// valuesToEncrypt[0] = custId;
			/*
			 * valuesToEncrypt[1] = encrptMpin; //valuesToEncrypt[2] =
			 * encrptTranMpin; valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
			 * valuesToEncrypt[3] = encrptUserMpin;
			 */

			// System.out.println("generatedXML" + generatedXML);
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
			}// end try
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final Void result) {

			// Log.e("ForgotPassword","xml_data.length=="+xml_data.length);

			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try {

				String str = CryptoClass.Function6(var5, var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("data","strchgmpin====="+str);
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
					respdescchangempin = jsonObj.getString("RESPDESC");
				} else {
					respdescchangempin = "";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (respdescchangempin.length() > 0) {
				showAlert(respdescchangempin);
			} else {

				if (retval.indexOf("SUCCESS") > -1) {
					post_successchangempin(retval);

				} else if (retval.indexOf("FAILED~") > -1) {
					String retCode = retval.split("~")[1];
					if (retCode.equalsIgnoreCase("1"))
						showAlert(getString(R.string.alert_122));
					else if (retCode.equalsIgnoreCase("2"))
						showAlert(getString(R.string.alert_123));
					else if (retCode.equalsIgnoreCase("3"))
						showAlert(getString(R.string.alert_050));
					else if (retCode.equalsIgnoreCase("4"))
						showAlert(getString(R.string.alrt_newmpin));
					else
						showAlert(getString(R.string.alert_085));
				} else {
					retMess = getString(R.string.alert_085);
					showAlert(retMess);
				}
			}
		}

	}

	public void post_successchangempin(String retval) {
		respcode = "";
		respdescchangempin = "";
		retMess = getString(R.string.alert_070);
		isWSCalled = true;
		showAlert(retMess);
	}

	public void post_successcchangeTranMPIN(String retval) {

		respcode = "";
		respdescchangeTranMPIN = "";
		retMess = getString(R.string.alert_Changempin_10);
		isWSCalled = true;
		showAlert(retMess);
	}

	class CallWebServicetran extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(ChangeMpin.this);
		String oldMpin = "", newMpin = "", oldTranMpin = "", newTranMpin = "";
		// changed

		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			oldTranMpin = et_old_tran_mpin.getText().toString().trim();
			newTranMpin = et_new_tran_mpin.getText().toString().trim();
			encrptUserMpin = newMpin;// ListEncryption.encryptData(userId+newMpin);
			encrptTranMpin = newTranMpin;// ListEncryption.encryptData(custId+newTranMpin);

			try {
				String location = MBSUtils.getLocation(ChangeMpin.this);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("TRANMPIN", encrptTranMpin);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ChangeMpin.this));
				jsonObj.put("USRTRANMPIN", encrptUserMpin);
				jsonObj.put("USRID", userId);
				jsonObj.put("OLDTRANMPIN", oldTranMpin);// ListEncryption.encryptData(custId+oldTranMpin));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ChangeMpin.this));
				jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(ChangeMpin.this));
				jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
				jsonObj.put("LATITUDE", location.split("~")[0]);
				jsonObj.put("LONGITUDE", location.split("~")[1]);
				jsonObj.put("REFNO", strRefId);
	            jsonObj.put("OTPVAL",otpvalset);
				// jsonObj.put("REFNO", strRefId);
				// jsonObj.put("OTPVAL",otpval );/
				jsonObj.put("METHODCODE", "5");
			} catch (JSONException je) {
				je.printStackTrace();
			}

			Log.e("CHANGE", "jsonObj.toString()==" + jsonObj.toString());

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
			}// end try
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final Void result) {

			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try {
				String str = CryptoClass.Function6(var5, var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("data","strchgtranmpin====="+str);
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
					respdescchangeTranMPIN = jsonObj.getString("RESPDESC");
				} else {
					respdescchangeTranMPIN = "";
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (respdescchangeTranMPIN.length() > 0) {
				showAlert(respdescchangeTranMPIN);
			} else {
				if (retval.indexOf("SUCCESS") > -1) {
					post_successcchangeTranMPIN(retval);
				} else if (retval.indexOf("FAILED~") > -1) {
					String retCode = retval.split("~")[1];
					if (retCode.equalsIgnoreCase("1"))
						showAlert(getString(R.string.alert_123));
					else if (retCode.equalsIgnoreCase("2"))
						showAlert(getString(R.string.alert_123));
					else if (retCode.equalsIgnoreCase("3"))
						showAlert(getString(R.string.alert_Changempin_11));
					else if (retCode.equalsIgnoreCase("5"))
						showAlert(getString(R.string.alert_124));
					/*
					 * else if (retCode.equalsIgnoreCase("5"))
					 * showAlert(getString(R.string.alert_124));
					 */
					else
						showAlert(getString(R.string.alert_085));
				} else {
					retMess = getString(R.string.alert_085);
					showAlert(retMess);
				}
			}
		}
	}

	class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {

		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(ChangeMpin.this);

		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			otpvalset = etotptxt.getText().toString().trim();
			strRefId = txt_ref_id.getText().toString().trim();
			strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();
			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("OTPVAL", otpvalset);
				// ListEncryption.encryptData(strOTP + custId));
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ChangeMpin.this));
				jsonObj.put("REFID", strRefId);
				jsonObj.put("ISREGISTRATION", "N");
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ChangeMpin.this));

				jsonObj.put("METHODCODE", "20");
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
			if (isWSCalled) {

				JSONObject jsonObj;
				try {
					String str = CryptoClass.Function6(var5, var2);
					jsonObj = new JSONObject(str.trim());

					Log.e("DSP","strvalidateotp====="+str);
					if (jsonObj.has("RESPCODE")) {
						respcode = jsonObj.getString("RESPCODE");
					} else {
						respcode = "-1";
					}
					if (jsonObj.has("RETVAL")) {
						retvalvalidateotp = jsonObj.getString("RETVAL");
					} else {
						retvalvalidateotp = "";
					}
					if (jsonObj.has("RESPDESC")) {
						respdescvalidateotp = jsonObj.getString("RESPDESC");
					} else {
						respdescvalidateotp = "";
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (respdescvalidateotp.length() > 0) {
					showAlert(respdescvalidateotp);
				} else {
					if (retvalvalidateotp.indexOf("SUCCESS") > -1) {
						post_successvalidate(retvalvalidateotp);
					} else if (retvalvalidateotp.indexOf("FAILED~MAXATTEMPT") > -1) {
						retMess = ChangeMpin.this
								.getString(R.string.alert_076_1);
						showAlert(retMess);
					} else {
						showAlert(getString(R.string.alert_076));
					}
				}
			} else {
				showAlert(getString(R.string.alert_000));
			}
		}

	}

	public void post_successvalidate(String retval) {

		respdescvalidateotp = "";
		respcode = "";

		flag = chkConnectivity();
		if (flag == 0) {
			if (strmpin.equalsIgnoreCase("MPIN")) {
				new CallWebService().execute();
			} else if (strmpin.equalsIgnoreCase("TRANMPIN")) {
				new CallWebServicetran().execute();
			}

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
		t1.sec = -1;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		t1.sec = timeOutInSecs;
		Log.e("sec11= ", "sec11==" + t1.sec);
		return super.onTouchEvent(event);
	}
}
