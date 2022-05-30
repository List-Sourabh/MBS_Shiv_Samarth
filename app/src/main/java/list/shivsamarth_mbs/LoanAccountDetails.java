package list.shivsamarth_mbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;

public class LoanAccountDetails extends Activity implements OnClickListener {
	LinearLayout layout_lable, layout_text1, layout_text2;
	TextView txt_heading, cust_nm, rate_of_interest, sanction_limit,
			drawing_power, accNo, txt_cust_name, txtlbl_instlmnt_frq,
			txtlbl_instlmnt_amt, txtlbl_pnd_instlmnts, txtlbl_principal,
			txtlbl_interest;
	EditText txt_current_bal, txt_utilisable_amt, txt_instlmnt_frq,
			txt_instlmnt_amt, txt_pnd_instlmnts, txt_pnd_interest,
			txt_instlmnt_interest, txt_pend_pinstlmntamt,vir_upa_ID,vir_ac_no;
	Button btnChangeMpin;
	ImageView img_heading;
	public Bundle getBundle = null;
	Cursor curSelectBankname;
	ImageButton btn_home, btn_back;
	String retMess = "", retVal = "", custid = "", accountNo = "", accStr = "",retvalweb="",
			accountinfo = "", retval = "", respcode = "", respdesc = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	DatabaseManagement dbms;
	int flag = 0;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loanaccount_details);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.loan);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		accNo = (TextView) findViewById(R.id.accNo);
		txt_cust_name = (TextView) findViewById(R.id.cust_name);
		rate_of_interest = (TextView) findViewById(R.id.txt_rate_of_interest);
		sanction_limit = (TextView) findViewById(R.id.txt_sanction_limit);
		drawing_power = (TextView) findViewById(R.id.txt_drawing_power);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_current_bal = (EditText) findViewById(R.id.ed_current_bal);
		txt_utilisable_amt = (EditText) findViewById(R.id.txt_drawable_amt);

		// pending installments
		txtlbl_instlmnt_amt = (TextView) findViewById(R.id.txtlbl_instl_amt);
		txt_instlmnt_amt = (EditText) findViewById(R.id.txt_instlmnt_amt);
		txtlbl_instlmnt_frq = (TextView) findViewById(R.id.txtlbl_instl_frq);
		txt_instlmnt_frq = (EditText) findViewById(R.id.txt_instlmnt_frq);
		txtlbl_pnd_instlmnts = (TextView) findViewById(R.id.txtlbl_pnd_instlmnts);
		txt_pnd_instlmnts = (EditText) findViewById(R.id.txt_pnd_instlmnts);
		txtlbl_principal = (TextView) findViewById(R.id.txtlbl_principal);
		txtlbl_interest = (TextView) findViewById(R.id.txtlbl_interest);
		txt_pnd_interest = (EditText) findViewById(R.id.txt_pnd_interest);
		txt_instlmnt_interest = (EditText) findViewById(R.id.txt_instlmnt_interest);
		layout_lable = (LinearLayout) findViewById(R.id.layout_lable);
		layout_text1 = (LinearLayout) findViewById(R.id.layout_text1);
		layout_text2 = (LinearLayout) findViewById(R.id.layout_text2);
		txt_pend_pinstlmntamt = (EditText) findViewById(R.id.txt_pndinstlmnt_amt);
		vir_ac_no = (EditText) findViewById(R.id.txt_vir_ac_no);
		vir_upa_ID = (EditText) findViewById(R.id.txt_vir_upa_ID);

		Bundle bObj = getIntent().getExtras();
		if (bObj != null) {
			accountNo = bObj.getString("accountnumber");
			accStr = bObj.getString("accountstr");
			accountinfo = bObj.getString("accountinfo");
		}

		if (accStr.split("-")[7].equalsIgnoreCase("I")) {
			txtlbl_instlmnt_amt.setVisibility(TextView.VISIBLE);
			txt_instlmnt_amt.setVisibility(EditText.VISIBLE);
			txtlbl_instlmnt_frq.setVisibility(TextView.VISIBLE);
			txt_instlmnt_frq.setVisibility(EditText.VISIBLE);
			txtlbl_pnd_instlmnts.setVisibility(TextView.VISIBLE);
			txt_pnd_instlmnts.setVisibility(EditText.VISIBLE);
			txt_pnd_interest.setVisibility(EditText.VISIBLE);
			txt_instlmnt_interest.setVisibility(EditText.VISIBLE);
			layout_lable.setVisibility(EditText.VISIBLE);
			layout_text1.setVisibility(EditText.VISIBLE);
			layout_text2.setVisibility(EditText.VISIBLE);
		} else {
			// txtlbl_instlmnt_amt.setVisibility(TextView.INVISIBLE);
			// txt_instlmnt_amt.setVisibility(EditText.INVISIBLE);
			txtlbl_instlmnt_frq.setVisibility(TextView.INVISIBLE);
			txt_instlmnt_frq.setVisibility(EditText.INVISIBLE);
			txtlbl_pnd_instlmnts.setVisibility(TextView.INVISIBLE);
			txt_pnd_instlmnts.setVisibility(EditText.INVISIBLE);
			txt_pnd_interest.setVisibility(EditText.INVISIBLE);
			txt_instlmnt_interest.setVisibility(EditText.INVISIBLE);
			layout_lable.setVisibility(EditText.INVISIBLE);
			layout_text1.setVisibility(EditText.INVISIBLE);
			layout_text2.setVisibility(EditText.INVISIBLE);

		}
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);

		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);

		txt_heading.setText(getString(R.string.lbl_acc_details));
		// Log.e("LoanAccountDetail",strtext);

		String accountNoStr = accNo.getText() + " " + accountinfo;// MBSUtils.get16digitsAccNo(strtext);
		accNo.setText(accountNoStr);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custid = c1.getString(2);
				Log.e("custId", "......" + custid);
			}
		}

		String custName = MBSUtils.getCustName(accStr);
		txt_cust_name.setText(custName);

		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		//new CallWebServiceGetLoanAccDetails().execute();

		flag = chkConnectivity();
		if (flag == 0) {
			new CallWebServiceGetLoanAccDetails().execute();
		}

		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_home:
		case R.id.btn_back:
			Intent in = new Intent(this, DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		}

	}

	class CallWebServiceGetLoanAccDetails extends AsyncTask<Void, Void, Void> {// CallWebServiceGetLoanAccDetails

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(LoanAccountDetails.this);
		// String[] xmlTags = { "CUSTID", "ACCNO", "IMEINO" };

		

		JSONObject jsonObj = new JSONObject();

		

		@Override
		protected void onPreExecute() {
			try {

				loadProBarObj.show();
				jsonObj.put("CUSTID", custid);
				jsonObj.put("ACCNO", accountNo);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(LoanAccountDetails.this));
				 jsonObj.put("SIMNO", MBSUtils.getSimNumber(LoanAccountDetails.this));
		            jsonObj.put("METHODCODE","32"); 
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
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
			
			loadProBarObj.dismiss();
			
			JSONObject jsonObj;
			try 
			{
				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) 
					respcode = jsonObj.getString("RESPCODE");
				else 
					respcode = "-1";
				
				if (jsonObj.has("RETVAL")) 
					retvalweb = jsonObj.getString("RETVAL");
				 else 
					retvalweb = "";
				
				if (jsonObj.has("RESPDESC")) 
					respdesc = jsonObj.getString("RESPDESC");
				else 
					respdesc = "";
				
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			if (respdesc.length() > 0) 
			{
				showAlert(respdesc);
			} 
			else 
			{
				if (retvalweb.indexOf("FAILED") > -1) 
				{
					retMess = getString(R.string.alert_092);
					showAlert(retMess);
				} 
				else 
				{
					post_success(retvalweb);
				}
			}
		}// end onPostExecute
	}// end CallWebServiceGetLoanAccDetails

	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(LoanAccountDetails.this, "" + str) 
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) 
					{
						post_success(retvalweb);
					} 
					else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) 
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

	public void post_success(String retvalweb) {
		respcode = "";
		respdesc = "";
		String data = retvalweb.split("SUCCESS~")[1];
		// retval=retval.split("SUCCESS~")[1];
		String[] retValues = data.split("#");

		rate_of_interest.setText(retValues[0]);
		sanction_limit.setText(MBSUtils.amountFormat(retValues[1], false,
				LoanAccountDetails.this));
		drawing_power.setText(MBSUtils.amountFormat(retValues[2], false,
				LoanAccountDetails.this));
	
		txt_current_bal.setText(MBSUtils.amountFormat(retValues[3], true,
				LoanAccountDetails.this));
		txt_utilisable_amt.setText(MBSUtils.amountFormat(retValues[4], false,
				LoanAccountDetails.this));
		if (accStr.split("-")[7].equalsIgnoreCase("I")) {
			txt_instlmnt_frq.setText(retValues[5]);
			txt_instlmnt_amt.setText(MBSUtils.amountFormat(retValues[6], false,
					LoanAccountDetails.this));
			txt_pnd_instlmnts.setText(retValues[7]);
			txt_pnd_interest.setText(retValues[8]);
			txt_pend_pinstlmntamt.setText(retValues[9]);
			txt_instlmnt_interest.setText(retValues[10]);

			vir_upa_ID.setText(retValues[11]);
			vir_ac_no.setText(retValues[12]);

		}
	}

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) this
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
			Log.e("EXCEPTION", "---------------" + ne);
			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.e("EXCEPTION", "---------------" + e);
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	@Override
	public void onBackPressed() {
		// Simply Do noting!
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
