package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
//import mbLib.DialogBox;
//import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class MiniStmtActivity extends Activity implements OnClickListener,
		android.view.View.OnKeyListener {
	// MainActivity act;
	MiniStmtActivity act;
	Intent in = null;
	TextView cust_nm, txt_heading;
	ImageView img_heading;
	Button btn_get_stmt;
	ImageButton btn_home, btn_back;
	ImageButton spinenr_btn;
	Spinner spi_account;
	ProgressBar pb_wait;
	ListView acnt_listView;
	String acnt_inf = "", all_acnts = "", avil_bal = "";
	String str = "", retMess = "", cust_name = "";
	private static final String MY_SESSION = "my_session";
	// Editor e;
	String stringValue = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	String all_str = "", branch_cd = "", schm_cd = "", acnt_no = "",
			custId = "";
	String selAcc = "", respcode = "", retval = "", respdesc = "";
	String str2 = "";
	String balnaceamnt = "", accountNo = "";
	// DialogBox dbs;
	DatabaseManagement dbms;
	// ProgressBar pb_wait;
	int flag = 0, noOfTran = 5;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	public MiniStmtActivity() {
	}

	public MiniStmtActivity(MiniStmtActivity a) {
		// System.out.println("MiniStmtActivity()");
		act = a;
		// miniStmt = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mini_statement);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act = this;
		// System.out.println("onCreateView()");
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				stringValue = c1.getString(0);
				Log.e("retvalstr", "...." + stringValue);
				custId = c1.getString(2);
				Log.e("custId", "......" + custId);
			}
		}
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		// btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		// /stringValue="2#101#SB#25730#Mr. KADAM SUSHANT  D##0020001010025730#O#NA#10#Y~2#101#SB#25733#Mr. KADAM SUSHANT  D##0020001010025733#O#NA#544598#Y~2#101#SB#25768#Mr. KADAM SUSHANT  D##0020001010025768#O#NA#20#Y~2#101#SB#25791#Mr. KADAM SUSHANT  D##0020001010025791#O#NA#30#Y";
		acnt_listView = (ListView) findViewById(R.id.acnt_listView);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		img_heading = (ImageView) findViewById(R.id.img_heading);

		btn_get_stmt = (Button) findViewById(R.id.btnGetStmt);
		btn_get_stmt.setOnClickListener(this);
		// btn_get_stmt.setTypeface(tf_calibri);
		all_acnts = stringValue;

		txt_heading.setText(getString(R.string.lbl_mini_statement));
		img_heading.setBackgroundResource(R.mipmap.ministatement);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		addAccounts(all_acnts);

		pb_wait = (ProgressBar) findViewById(R.id.pb_wait2);
		pb_wait.setMax(10);
		pb_wait.setProgress(1);
		pb_wait.setVisibility(ProgressBar.INVISIBLE);

		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	public void addAccounts(String str) {

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");

			int noOfAccounts = allstr.length;

			ArrayList<Accountbean> Accountbean_arr = new ArrayList<Accountbean>();
			final ArrayList<String> Account_arrTemp = new ArrayList<String>();
			Accounts acArray[] = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) {

				str2 = allstr[i];

				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String acctype = str2.split("-")[2];
				
				if ((!acctype.equalsIgnoreCase("FD") && !acctype.equalsIgnoreCase("RP"))) 
				{
					Accountbean Accountbeanobj = new Accountbean();
					Accountbean_arr.add(Accountbeanobj);
					Account_arrTemp.add(str2);
					str2 = MBSUtils.get16digitsAccNo(str2);
					Accountbeanobj.setAccountinfo(str2 + " ("+ MBSUtils.getAccTypeDesc(acctype) + ")");
					Accountbeanobj.setAccountNumber(str2);
				}
			}

			Customlist_radioadt adapter = new Customlist_radioadt(this,Accountbean_arr);
			acnt_listView.setAdapter(adapter);
			acnt_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			acnt_listView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView,
								View view, int i, long l) {

							btn_get_stmt.setEnabled(true);
							Accountbean dataModel = (Accountbean) adapterView
									.getItemAtPosition(i);

							// Log.e("SSSSS:dataModel","dataModel=="+
							// dataModel.getAccountinfo());
							accountNo = dataModel.getAccountNumber();
							// Log.e("temp acc str ==", Account_arrTemp.get(i));
							// acnt_inf=dataModel.getAccountinfo();
							acnt_inf = Account_arrTemp.get(i);

							for (int i1 = 0; i1 < adapterView.getCount(); i1++) {

								try {

									View v = adapterView.getChildAt(i1);
									RadioButton radio = (RadioButton) v
											.findViewById(R.id.radio);
									radio.setChecked(false);

								} catch (Exception e) {
									Log.e("radio button", "radio");
								}

							}

							try {
								RadioButton radio = (RadioButton) view
										.findViewById(R.id.radio);
								radio.setChecked(true);
							} catch (Exception e) {
								Log.e("radio button", "radio");
							}

						}
					});
		} catch (Exception e) {
			System.out.println("" + e);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGetStmt:

			InputDialogBox inputBox = new InputDialogBox(this);
			inputBox.show();
			break;
		// case R.id.btn_back:
		case R.id.btn_back:
			Intent in = new Intent(this, DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
			break;

		default:
			break;
		}
	}

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) act
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
					// retMess = "Network Disconnected. Please Try Again.";
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
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
				// setAlert();
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

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
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
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
			// setAlert();
			/*
			 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
			 * dbs.get_adb().setPositiveButton("Ok", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
			 * in = null; in = new Intent(getApplicationContext(),
			 * LoginActivity.class); startActivity(in); finish(); } });
			 * dbs.get_adb().show();
			 */
		}
		return flag;
	}

	class CallWebService extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		

		JSONObject jsonObj = new JSONObject();
		

		@Override
		protected void onPreExecute() 
		{
			try 
			{
				loadProBarObj.show();

				all_str = acnt_inf;
				Log.e("accountNo :", accountNo);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("ACCNO", accountNo);
				jsonObj.put("NOOFTRAN", "" + noOfTran);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				 jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
	             jsonObj.put("METHODCODE","2"); 
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

		// @SuppressLint("NewApi")
		protected void onPostExecute(final Void result) 
		{
			
			
			// String decryptedStatments=xml_data[0];
			
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
					retval = jsonObj.getString("RETVAL");
				} else {
					retval = "";
				}
				if (jsonObj.has("RESPDESC")) {
					respdesc = jsonObj.getString("RESPDESC");
				} else {
					respdesc = "";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (respdesc.length() > 0) {
				showAlert(respdesc);
			} else {
			
				if (retval.indexOf("FAILED") > -1) {
					// retMess="Can Not Get Mini Statement. Please Retry";
					retMess = "Network Problem. Please Try Again.";
					// Show message dialouge
					// setAlert();
					showAlert(retMess);
				} else {
					post_success(retval);

				}
				// pb_wait.setVisibility(ProgressBar.INVISIBLE);
				loadProBarObj.dismiss();
			}
		}
	}

	public void post_success(String retval) {
		try {
			respcode = "";
			respdesc = "";
			Log.e("SAM","retval"+retval);
			String values[] = retval.split("~@~");

			balnaceamnt = MBSUtils.amountFormat(values[1], true, this);
			Log.e("SAM","balnaceamnt"+balnaceamnt);
			
			avil_bal = MBSUtils.amountFormat(values[2], true, this);
			Log.e("SAM","avil_bal"+avil_bal);
			
			retval = values[0];
			Log.e("SAM","retval"+retval);
			str = acnt_inf;

			act.setTitle(act.getString(R.string.lbl_mini_statement));

			Bundle b = new Bundle();

			// Storing data into bundle
			b.putString("str", str);
			b.putString("all_str", all_str);
			b.putString("retval", retval);
			b.putString("balnaceamnt", balnaceamnt);
			b.putString("avil_bal", avil_bal);

			Intent in = new Intent(this, MiniStmtReport.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			in.putExtras(b);
			startActivity(in);
			this.finish();

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("MInistmnts", "" + e);
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onFinish() {
		// //////////mini.pb_wait.setVisibility(ProgressBar.INVISIBLE);
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

	public class InputDialogBox extends Dialog implements OnClickListener {
		MiniStmtActivity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		TextView txt_dia;
		Button btnOk;
		String strmpin = "";
		TextView txtLbl;
		boolean flg;

		public InputDialogBox(MiniStmtActivity activity) {
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) {
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_design);
			txt_dia = (TextView) findViewById(R.id.txt_dia);
			mpin = (EditText) findViewById(R.id.txtMpin);
			mpin.setInputType(InputType.TYPE_CLASS_NUMBER);
			btnOk = (Button) findViewById(R.id.btnOK);
			txt_dia.setText(getString(R.string.no_of_tran));
			mpin.setText("5");
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			try {

				// System.out.println("========= inside onClick ============***********");
				String str = mpin.getText().toString().trim();

				if (str.length() != 0) {
					int no = Integer.parseInt(str);
					if (no == 0) {
						retMess = getString(R.string.alert_Non_zeroNoOf_tran);
						showAlert(retMess);
						this.show();
					} else if (no > 25) {
						retMess = getString(R.string.alert_tran_upto25);
						showAlert(retMess);
						this.show();
					} else {
						noOfTran = no;
						// flag = chkConnectivity();
						flag = 0;
						// System.out.println("flag in Ministatement---" +
						// flag);
						if (flag == 0) {
							new CallWebService().execute();
							this.hide();
						}
					}
				} else {
					retMess = getString(R.string.alert_entr_NoOf_tran);
					showAlert(retMess);
					this.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox

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
