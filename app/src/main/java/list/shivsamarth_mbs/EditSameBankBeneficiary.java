package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;

import mbLib.MBSUtils;
import mbLib.MyThread;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditSameBankBeneficiary extends Activity implements
		OnClickListener {

	EditSameBankBeneficiary act = this;
	ProgressBar p_wait;
	EditText txtAccNo, txtName, txtmobNo, txtEmail, txtNick_Name;
	TextView txt_heading;
	Button btn_submit, btn_fetchname;
	ImageButton btn_home, btn_back, spinner_btn;
	Spinner spi_sel_beneficiery;
	DialogBox dbs;
	DatabaseManagement dbms;
	boolean WSCalled = false;
	String flg = "false", respcode = "", retval = "", respdesc = "",
			respdesc_save_beneficiary = "", respdesc_fetch_beneficiary = "";
	ImageView img_heading;
	Editor e;
	private MyThread t1;
	int timeOutInSecs=300;
	private String benInfo = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME1 = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME2 = "";
	private static final String MY_SESSION = "my_session";

	int cnt = 0, flag = 0;
	String reTval = "", getBeneficiariesrespdesc = "",
			GetAccountInforespdesc = "", saveBeneficiariesrespdesc = "";
	String benAccountNumber = "", benAccountName = "";
	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "", nicknamee = "";
	String str = "", retMess = "", cust_name = "", tmpXMLString = "",
			retVal = "";
	String mobPin = "", benNickname = "", when_fetch = "", benSrno = null;
	public String encrptdMpin;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	public EditSameBankBeneficiary() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_samebank_beneficiary);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.edit_beneficiary);
		txtAccNo = (EditText) findViewById(R.id.txtAccNo2);
		btn_fetchname = (Button) findViewById(R.id.btn_fetchName2);
		txtName = (EditText) findViewById(R.id.txtName2);
		txtmobNo = (EditText) findViewById(R.id.txtmobNo2);
		txtEmail = (EditText) findViewById(R.id.txtEmail2);
		txtNick_Name = (EditText) findViewById(R.id.txtNick_Name2);
		btn_submit = (Button) findViewById(R.id.btn_submit2);
		p_wait = (ProgressBar) findViewById(R.id.pro_bar);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);

		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.frmtitle_edit_same_bnk_bnf));
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);
		btn_fetchname.setOnClickListener(this);
		btn_submit.setOnClickListener(this);

		spi_sel_beneficiery = (Spinner) findViewById(R.id.sameBnkTranspi_sel_beneficiery);
		spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
		spinner_btn.setOnClickListener(this);

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				Log.e("custId", "......" + custId);
			}
		}

		this.flag = chkConnectivity();
		if (this.flag == 0) {
			new CallWebService_fetch_all_beneficiaries().execute();
		}

		spi_sel_beneficiery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					String benMobNo = "";
					String benEmail = "";

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						// System.out.println("spi_sel_beneficiery.getSelectedItemPosition() ===="+
						// spi_sel_beneficiery.getSelectedItemPosition());
						String str = spi_sel_beneficiery.getItemAtPosition(
								spi_sel_beneficiery.getSelectedItemPosition())
								.toString();
						if (str.equalsIgnoreCase("Select Beneficiary")) {
							txtAccNo.setText("");
							txtName.setText("");
							txtmobNo.setText("");
							txtEmail.setText("");
							txtNick_Name.setText("");
							txtAccNo.setEnabled(false);
							txtName.setEnabled(false);
							txtmobNo.setEnabled(false);
							txtEmail.setEnabled(false);
							btn_fetchname.setEnabled(false);
							Log.e("EDITSAMEBAKBENF", "text field disabled");
						}
						if (arg2 != 0) {
							// System.out.println("selected
							// benefic////System.outstr);
							if (str.equalsIgnoreCase("Select Beneficiary")) {
								txtAccNo.setText("");
								txtName.setText("");
								txtmobNo.setText("");
								txtEmail.setText("");
								txtNick_Name.setText("");
								txtAccNo.setEnabled(false);
								txtName.setEnabled(false);
								txtmobNo.setEnabled(false);
								txtEmail.setEnabled(false);
								btn_fetchname.setEnabled(false);
								Log.e("EDITSAMEBAKBENF", "text field disabled");
							} else {
								txtAccNo.setEnabled(true);
								txtName.setEnabled(true);
								txtmobNo.setEnabled(true);
								txtEmail.setEnabled(true);
								btn_fetchname.setEnabled(true);
								Log.e("EDITSAMEBAKBENF", "text field enabled");
								String allStr[] = benInfo.split("~");

								for (int i = 1; i <= allStr.length; i++) {
									String str1[] = allStr[i - 1].split("#");
									benNickname = str1[2] + "(" + str1[1] + ")";
									// if (str.indexOf("(" + str1[1] + ")") >
									// -1) {
									// str.indexOf(str1[2])>-1
									if (str.equalsIgnoreCase(benNickname)) {
										// //System.out.println("========== inside if ============");
										benSrno = str1[0];
										benAccountName = str1[1];
										nicknamee = str1[2];
										benAccountNumber = str1[3];
										benMobNo = str1[6];
										benEmail = str1[7];

										if (str1[7].equalsIgnoreCase("NA")) {
											benEmail = "";
										}

										if (str1[6].equalsIgnoreCase("NA")) {
											benMobNo = "";
										}
										txtAccNo.setText(benAccountNumber);
										txtName.setText(benAccountName);
										txtmobNo.setText(benMobNo);
										txtEmail.setText(benEmail);
										txtNick_Name.setText(nicknamee);
									}

								}// end for
							}
						}// end arg2 end if
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});// end spi_sel_beneficiery
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	private void addBeneficiaries(String retval) {
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			int noOfben = allstr.length;
			String benName = "";
			arrList.add("Select Beneficiary");
			for (int i = 1; i <= noOfben; i++) {
				
				String[] str2 = allstr[i - 1].split(Pattern.quote("#"));
				
				Log.e("Benificiary", allstr[i - 1] + " Length="+ str2.length);
				
				benName = str2[2] + "(" + str2[1] + ")";
				arrList.add(benName);
			}
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			ArrayAdapter<String> accs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, arrList);
			accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(accs);
		} catch (Exception e) {
			Log.e("IN ADDBenificiary", "" + e);
			e.printStackTrace();
		}
	}// end addBeneficiaries

	public void initAll() {
		txtAccNo.setText("");
		txtName.setText("");
		txtmobNo.setText("");
		txtEmail.setText("");
		txtNick_Name.setText("");

	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				if ((str.equalsIgnoreCase(getBeneficiariesrespdesc))
						&& (respcode.equalsIgnoreCase("0"))) {
					post_successfetch_all_beneficiaries(reTval);
				} else if ((str.equalsIgnoreCase(getBeneficiariesrespdesc))
						&& (respcode.equalsIgnoreCase("1"))) {
					this.dismiss();
				} else if ((str.equalsIgnoreCase(GetAccountInforespdesc))
						&& (respcode.equalsIgnoreCase("0"))) {
					post_successfetch_ac_holdernm(reTval);
				} else if ((str.equalsIgnoreCase(GetAccountInforespdesc))
						&& (respcode.equalsIgnoreCase("1"))) {
					this.dismiss();
				} else if ((str.equalsIgnoreCase(saveBeneficiariesrespdesc))
						&& (respcode.equalsIgnoreCase("0"))) {
					post_successsaveBeneficiaries(reTval);
				} else if ((str.equalsIgnoreCase(saveBeneficiariesrespdesc))
						&& (respcode.equalsIgnoreCase("1"))) {
					this.dismiss();
				} else if (act.getString(R.string.alert_125).equalsIgnoreCase(
						textMessage)) {
					SaveBeneficiary();
				} else if (flg == "true") {
					Log.e("Inside If", "Inside if===" + flg);
					Log.e("Inside If", "Inside if===" + flg);
					Log.e("Inside If", "Inside if===" + flg);
					switch (v.getId()) {

					case R.id.btn_ok:

						Intent in = new Intent(act,ManageBeneficiaryMenuActivity.class);
						in.putExtra("var1", var1);
						   in.putExtra("var3", var3);
						act.startActivity(in);
						act.finish();

					}

				} else {
					this.dismiss();
				}

			}
		};
		alert.show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			Intent in = new Intent(act, ManageBeneficiaryMenuActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			act.startActivity(in);
			act.finish();
			break;

		case R.id.btn_home:
			// dashboard....
			break;
		case R.id.spinner_btn:
			Log.e("DROP DOWN IMG BTN CLICKED....",
					"DROP DOWN IMG BTN CLICKED....");
			spi_sel_beneficiery.performClick();
			break;

		case R.id.btn_fetchName2:
			when_fetch = "EXPLICIT";
			accNo = txtAccNo.getText().toString();
			Log.i("fetchname", "accNo :" + accNo);

			if (accNo.equalsIgnoreCase("")) {
				retMess = getString(R.string.alert_001);
				showAlert(retMess);
				txtAccNo.requestFocus();
			} else {
				flag = chkConnectivity();
				if (flag == 0)
				{
					new CallWebService_fetch_ac_holdernm().execute();
				}
			}
			break;

		case R.id.btn_submit2:
			String str = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			accNo = txtAccNo.getText().toString().trim();
			accNm = txtName.getText().toString().trim();
			mobNo = txtmobNo.getText().toString().trim();
			nickNm = txtNick_Name.getText().toString().trim();// nicknamee;
			mailId = txtEmail.getText().toString().trim();

			if (str.equalsIgnoreCase("Select Beneficiary")) {
				retMess = getString(R.string.alert_sel_benef);
				showAlert(retMess);
			}

			else if (accNo.length() == 0) {
				retMess = getString(R.string.alert_008);
				showAlert(retMess);
				txtAccNo.requestFocus();
			}

			else if (accNo.length() != 16) {
				// /////retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_009);
				showAlert(retMess);
				txtAccNo.requestFocus();
			}

			
			else if (mobNo.length() > 0 && !MBSUtils.validateMobNo(mobNo)) {
				retMess = getString(R.string.alert_011);
				showAlert(retMess);
				txtmobNo.requestFocus();

			} else if (mailId.length() > 0 && !MBSUtils.validateEmail(mailId)) {
				retMess = getString(R.string.alert_valid_mail);
				showAlert(retMess);
				txtEmail.requestFocus();

			} else if (accNm.length() == 0) {
				retMess = getString(R.string.alert_plz_name);
				showAlert(retMess);
				txtName.requestFocus();
			} else if (accNm.length() > 100) {
				retMess = getString(R.string.alert_lengthofname);
				showAlert(retMess);
				txtName.requestFocus();
			} 
			else if (nickNm.trim().length() == 0) 
			{
				retMess = getString(R.string.alert_nicknm);
				showAlert(retMess);
				Log.e("Onclick of submmit button", "====Nick name  " + nickNm);
				txtNick_Name.requestFocus();
			} 
			else if (nickNm.trim().length() < 4 || nickNm.trim().length() > 15) 
			{
				retMess = getString(R.string.alert_nicknm_Len_valid);
				showAlert(retMess);
				Log.e("Onclick of submmit button", "====Nick name lenght  "+ nickNm.trim().length());
				txtNick_Name.requestFocus();
			}
			else 
			{
				flag = chkConnectivity();

				if (flag == 0)
				{
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
				}
			}
			break;

		default:
			break;
		}
	}// end onClick

	public void SaveBeneficiary() {
		// flag = chkConnectivity();

		// if (flag == 0)
		{
			// SharedPreferences sp =
			// act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
			// e = sp.edit();
			// custId = sp.getString("custId", "custId");
			// mobPin = sp.getString("pin", "pin");
			Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																			// null);
			if (c1 != null) {
				while (c1.moveToNext()) {
					custId = c1.getString(2);
					Log.e("custId", "......" + custId);
				}
			}

			InputDialogBox inputBox = new InputDialogBox(act);
			inputBox.show();

		}

	}

	public void post_fetch_beneficiary(String retval) {
		respcode = "";
		respdesc_fetch_beneficiary = "";
		retval = retval.split("SUCCESS~")[1];
		benInfo = retval;
		addBeneficiaries(retval);
	}

	public void post_ac_holdernm(String retval) {
		respcode = "";
		respdesc = "";
		Bundle b = new Bundle();
		// retVal=""SUCCESS~KAVITA KIRAN KADEKAR";
		System.out.println("decrypted Acc holder Name :" + retval);
		String acno = txtAccNo.getText().toString();
		if (when_fetch == "AUTO") {
			dbs = new DialogBox(act);
			dbs.get_adb().setMessage(
					"Continue With Name \"" + retval + "\" For Account No."
							+ acno + " ?");
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							SaveBeneficiary();
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
						}
					});
			dbs.get_adb().show();
		}
	}

	public void post_save_beneficiary() {
		respcode = "-1";
		respdesc_save_beneficiary = "";
		WSCalled = true;
		flg = "true";
		retMess = getString(R.string.alert_benf_updt_succ);
		showAlert(retMess);
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("editSameBankBeneficiary	in chkConnectivity () state1 ---------"
							+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
						// pb_wait.setVisibility(ProgressBar.VISIBLE);
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
					// /////retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					// ///////retMess =
					// "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();

					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				// //////retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();

				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("editSameBankBeneficiary", "NullPointerException Exception"
					+ ne);
			flag = 1;
			// ////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			showAlert(retMess);

		} catch (Exception e) {
			Log.i("editSameBankBeneficiary", "Exception" + e);
			flag = 1;
			// ///retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);
		}
		return flag;
	}

	// inner class
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
			setContentView(R.layout.dialog_design);
			mpin = (EditText) findViewById(R.id.txtMpin);
			btnOk = (Button) findViewById(R.id.btnOK);
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			try {
				String str = mpin.getText().toString();
				encrptdMpin = str;//ListEncryption.encryptData(custId + str);
				if (str.equalsIgnoreCase("")) {
					this.hide();
					// ////retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.enter_pass);
					showAlert(retMess);
					mpin.setText("");
				} else {// if (encrptdMpin.equalsIgnoreCase(mobPin)) {
						// SharedPreferences sp =
						// act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
						// e = sp.edit();
						// custId = sp.getString("custId", "custId");

					Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "",
							null);// ("select * from ", null);
					if (c1 != null) {
						while (c1.moveToNext()) {
							custId = c1.getString(2);
							Log.e("custId", "......" + custId);
						}
					}
					//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
					//C.execute();
					callValidateTranpinService c=new callValidateTranpinService();
					c.execute();
					this.hide();
				}
				/*
				 * else { this.hide(); retMess = getString(R.string.alert_125);
				 * showAlert(retMess); mpin.setText(""); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox
	class callValidateTranpinService extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		
		JSONObject obj = new JSONObject();

		protected void onPreExecute() 
		{
			loadProBarObj.show();
		
			
		
			try 
			{
				String location=MBSUtils.getLocation(act);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("MPIN", encrptdMpin);
				obj.put("CUSTID", custId);
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("METHODCODE","84"); 
				
				
				Log.e("SAMwebser===","SIMNO:"+MBSUtils.getSimNumber(act));
				Log.e("SAMwebser===","IMEINO:"+MBSUtils.getImeiNumber(act));
				Log.e("SAMwebser===","TRANPIN:"+encrptdMpin);
				Log.e("SAMwebser===","CUSTID:"+custId);
				Log.e("SAMwebser===","MOBILENO:"+MBSUtils.getMyPhoneNO(act));
				Log.e("SAMwebser===","IPADDRESS:"+MBSUtils.getLocalIpAddress());
				Log.e("SAMwebser===","OSVERSION:"+Build.VERSION.RELEASE);
				Log.e("SAMwebser===","LATITUDE:"+location.split("~")[0]);
				Log.e("SAMwebser===","LONGITUDE:"+location.split("~")[1]);
				Log.e("sendsring===","LONGITUDE:"+obj.toString());
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		
		}

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
			}// end try
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
			
			
			  
			  
			  JSONObject jsonObj;
	   			try
	   			{
	   				String str=CryptoClass.Function6(var5,var2);
	   				jsonObj = new JSONObject(str.trim());
	   				
	   				
			  String decryptedAccounts = str.trim();
			Log.e("SAMgdg===","xml_data[0]=decryptedAccounts:"+decryptedAccounts);
			loadProBarObj.dismiss();
			/*if (retJson.has("VALIDATIONDATA") && ValidationData.equals(retJson.getString("VALIDATIONDATA")))
			{
			}	
			else
			{
				MBSUtils.showInvalidResponseAlert(act);
			}*/
			if (decryptedAccounts.indexOf("SUCCESS") > -1) 
			{
				saveData();
			} 
			
			else if (decryptedAccounts.indexOf("FAILED#") > -1) 
			{
				retMess = getString(R.string.alert_032);
				showAlert(retMess);// setAlert();
			} 
			else if (decryptedAccounts.indexOf("BLOCKEDFORDAY") > -1) 
			{
				retMess = getString(R.string.login_alert_005);
				showAlert(retMess);// setAlert();
			} 
			else if (decryptedAccounts.indexOf("WRONGMPIN") > -1) 
			{
				JSONObject obj=null;
				try {
					obj = new JSONObject(decryptedAccounts);
					String msg[] = obj.getString("RETVAL").split("~");
					String first = msg[1];
					String second = msg[2];
					Log.e("OMKAR", "---"+second+"----");
					int count = Integer.parseInt(second);
					count = 5 - count;
					loadProBarObj.dismiss();
					retMess = act.getString(R.string.alert_125) + " " + count + " "
							+ act.getString(R.string.alert_125_2);
					showAlert(retMess);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
	   				
	   			} catch (JSONException e) 
	   			{
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   			}

		}// end onPostExecute
	}// end callValidateTranpinService
	public void saveData() 
	{
		try 
		{
			JSONObject jsonObj = new JSONObject();
			try 
			{
				jsonObj.put("CUSTID", custId.trim());
				jsonObj.put("ACCNO", accNo.trim());
				jsonObj.put("ACCNM", accNm.trim());
				jsonObj.put("MOBNO", mobNo.trim());
				jsonObj.put("NICKNM", nickNm.trim());
				jsonObj.put("MAILID", mailId.trim());
				jsonObj.put("TRANSFERTYPE", "Y");
				jsonObj.put("IFSCCD", "DUMMY");
				jsonObj.put("MMID", "DUMMY");
				jsonObj.put("IINSERTUPDTDLT", "U");
				jsonObj.put("BENSRNO", benSrno.trim());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", encrptdMpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle bundle=new Bundle();
		//Fragment fragment = new BeneficiaryOtp(act);
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "EDSAMBENF");
		bundle.putString("JSONOBJ", jsonObj.toString());
		Intent in = new Intent(act,BeneficiaryOtp.class);
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
		   in.putExtras(bundle);
		act.startActivity(in);
		act.finish();
		/*fragment.setArguments(bundle);
		FragmentManager fragmentManager = editSameBnkBenf.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	@Override
	public void onBackPressed() {
		// Simply Do noting!
	}

	class CallWebService_fetch_all_beneficiaries extends
			AsyncTask<Void, Void, Void> {

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = { "CUSTID", "SAMEBNK", "IMEINO" };
	
		JSONObject jsonObj = new JSONObject();
	

		protected void onPreExecute() {
			try {
				loadProBarObj.show();
				// System.outut.println("+++++++++same bank trans doInBackground:custId===>"+
				// custId);

				jsonObj.put("CUSTID", custId);
				jsonObj.put("SAMEBNK", "Y");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				   jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
		             jsonObj.put("METHODCODE","13"); 
				/*
				 * valuesToEncrypt[0] = custId; valuesToEncrypt[1] = "Y";
				 * valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
				 */
			} catch (JSONException je) {
				je.printStackTrace();
			}
			
			// System.out.println("&&&&&&&&&& generatedXML "+generatedXML);
		}// end onPreExecute

		protected Void doInBackground(Void[] paramArrayOfVoid) {
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
		}// end dodoInBackground

		protected void onPostExecute(Void paramVoid) {
		

			loadProBarObj.dismiss();
	
			JSONObject jsonObj;
			try {
				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					reTval = jsonObj.getString("RETVAL");
				} else {
					reTval = "";
				}
				if (jsonObj.has("RESPDESC")) {
					getBeneficiariesrespdesc = jsonObj.getString("RESPDESC");
				} else {
					getBeneficiariesrespdesc = "";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (getBeneficiariesrespdesc.length() > 0) {
				showAlert(getBeneficiariesrespdesc);
			} else {
				if (reTval.indexOf("SUCCESS") > -1) {
					post_successfetch_all_beneficiaries(reTval);

				} else {
					retMess = getString(R.string.alert_041);
					flg = "true";
					showAlert(retMess);
				}
			}
		}// end onPostExecute

	}// end callWbService

	public void post_successsaveBeneficiaries(String reTval) {
		respcode = "";
		saveBeneficiariesrespdesc = "";
		WSCalled = true;
		retMess = getString(R.string.alert_017);
		flg = "true";
		showAlert(retMess);

	}

	public void post_successfetch_all_beneficiaries(String reTval) {
		respcode = "";
		getBeneficiariesrespdesc = "";
		String decryptedBeneficiaries = reTval.split("SUCCESS~")[1];
		Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);
		benInfo = decryptedBeneficiaries;
		addBeneficiaries(decryptedBeneficiaries);

	}

	// Fetch account name
	class CallWebService_fetch_ac_holdernm extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = { "ACCNO", "CUSTID" };
	

		JSONObject jsonObj = new JSONObject();
		

		@Override
		protected void onPreExecute() {
			try {
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				String accNo = txtAccNo.getText().toString().trim();

				// Log.i("mayuri", "accNo :" + accNo + "@@@@@@@@@@");
				// Log.i("mayuri", "custId :" + custId + "@@@@@@@@@@");

				jsonObj.put("ACCNO", accNo);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				  jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
		             jsonObj.put("METHODCODE","18"); 
				/*
				 * valuesToEncrypt[0] = accNo; valuesToEncrypt[1] = custId;
				 */
			} catch (JSONException je) {
				je.printStackTrace();
			}
			
		};

		@Override
		protected Void doInBackground(Void... arg0) {
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
		}

		@Override
		protected void onPostExecute(final Void result) {
			Intent intent = null;
			
			// String decryptedAccName = xml_data[0];
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try {

				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					reTval = jsonObj.getString("RETVAL");
				} else {
					reTval = "";
				}
				if (jsonObj.has("RESPDESC")) {
					GetAccountInforespdesc = jsonObj.getString("RESPDESC");
				} else {
					GetAccountInforespdesc = "";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (GetAccountInforespdesc.length() > 0) {
				showAlert(GetAccountInforespdesc);
			} else {
				if (reTval.indexOf("FAILED") > -1) {

					if (reTval.indexOf("NOT_EXISTS") > -1) {
						// ///retMess = "Invalid Account Number.";
						retMess = getString(R.string.alert_008);
						txtAccNo.requestFocus();
						showAlert(retMess);
					} else if (reTval.indexOf("EXISTS") > -1) {
						// ////retMess = "This Beneficiary Is Already Added.";
						retMess = getString(R.string.alert_009);
						txtAccNo.requestFocus();
						showAlert(retMess);
					} else {
						// ////retMess =
						// "Network Unavailable. Please Try Again.";
						retMess = getString(R.string.alert_000);
						txtAccNo.requestFocus();
						showAlert(retMess);
					}

				} else {
					post_successfetch_ac_holdernm(reTval);
				}

			}
		}
	}

	public void post_successfetch_ac_holdernm(String reTval) {
		respcode = "";
		GetAccountInforespdesc = "";
		Log.i("mayuri success", "success");

		Bundle b = new Bundle();

		Log.i("Return value:", retVal);

		Log.i("mayuri success", "success" + retVal.split("~")[1]);

		System.out.println("decrypted Acc holder Name :" + reTval);

		String acno = txtAccNo.getText().toString().trim();
		if (when_fetch == "AUTO") {
			dbs = new DialogBox(act);

			dbs.get_adb().setMessage(
					"Continue With Name \"" + reTval + "\" For Account No."
							+ acno + " ?");
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

							SaveBeneficiary();
							// System.exit(0);
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.cancel();
						}
					});
			dbs.get_adb().show();
			// break;
		}

	}

	// Save Beneficiary
	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
	

		JSONObject jsonObj = new JSONObject();
		

		@Override
		protected void onPreExecute() {
			try {
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				jsonObj.put("CUSTID", custId.trim());
				jsonObj.put("ACCNO", accNo.trim());
				jsonObj.put("ACCNM", accNm.trim());
				jsonObj.put("MOBNO", mobNo.trim());
				jsonObj.put("NICKNM", nickNm.trim());
				jsonObj.put("MAILID", mailId.trim());
				jsonObj.put("TRANSFERTYPE", "Y");
				jsonObj.put("IFSCCD", "DUMMY");
				jsonObj.put("MMID", "DUMMY");
				jsonObj.put("IINSERTUPDTDLT", "U");
				jsonObj.put("BENSRNO", benSrno.trim());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", encrptdMpin);
				   jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
		             jsonObj.put("METHODCODE","14"); 
			} catch (JSONException je) {
				je.printStackTrace();
			}
			
			// System.out.println("generatedXML" + generatedXML);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
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
		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			Intent intent = null;
			
			loadProBarObj.dismiss();
			// String decrypted = xml_data[0];
			JSONObject jsonObj;
			try {

				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					reTval = jsonObj.getString("RETVAL");
				} else {
					reTval = "";
				}
				if (jsonObj.has("RESPDESC")) {
					saveBeneficiariesrespdesc = jsonObj.getString("RESPDESC");
				} else {
					saveBeneficiariesrespdesc = "";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (saveBeneficiariesrespdesc.length() > 0) 
			{
				showAlert(saveBeneficiariesrespdesc);
			} 
			else 
			{
				Log.i("mayuri retVal", "retVal :" + retVal);
				if (reTval.indexOf("FAILED") > -1) 
				{
					txtAccNo.setFocusableInTouchMode(true);
					txtAccNo.requestFocus();
					cnt = 0;
					flg = "false";
					if (reTval.indexOf("WRONGMPIN") > -1) 
					{
						retMess = getString(R.string.alert_125);
						showAlert(retMess);
					}
					else if (reTval.indexOf("DUPLICATENICKNAME") > -1) 
					{
						retMess = getString(R.string.alert_fail_benef_duplinick);
						showAlert(retMess);
					} 
					else if (reTval.indexOf("DUPLICATEACCOUNT") > -1) 
					{
						retMess = getString(R.string.alert_fail_dupli_acc_no);
						showAlert(retMess);
					} 
					else 
					{
						retMess = getString(R.string.alert_028_1);
						showAlert(retMess);
					}
				} 
				else 
				{
					post_successsaveBeneficiaries(reTval);
				}
			}
		}
	}
	
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
