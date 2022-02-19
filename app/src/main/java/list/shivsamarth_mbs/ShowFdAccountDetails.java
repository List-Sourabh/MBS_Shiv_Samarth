package list.shivsamarth_mbs;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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

public class ShowFdAccountDetails extends Activity implements OnClickListener {
	Editor e;
	public ShowFdAccountDetails act;
	TextView txt_heading, cust_nm, accNo, txt_cust_name, txt_lbl_underLiened,
			tv_int_frq;
	EditText opening_amnt, opng_dt, maturity_date, maturity_amnt,
			intrest_value, underliened, txt_underliened, txt_instl_amt,
			txt_instl_frq, txt_curbal, txt_pnd_instl, txt_intvl_int,
			txt_intv_int_frq;
	Button btnChangeMpin;
	ImageView img_heading;
	public Bundle getBundle = null;
	Cursor curSelectBankname;
	TextView tv_bankname, tv_curbal, tv_pnd_instal, tv_intvl_int;
	ImageButton btn_home, btn_back;
	LinearLayout txtLayout, edtLayout, amtTxtLayout, amtEditLayout;
	String retMess = "", retVal = "", custid = "", accountNo = "", retval = "",
			respcode = "", respdesc = "", ret_retval = "",retvalweb="";
	int cnt = 0, flag = 0;
	// private String strtext;
	DatabaseManagement dbms;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	String accstr = "", accInfo = "";
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fdrdaccount_details);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.deposit);
		act = this;
		Bundle bObj = getIntent().getExtras();
		if (bObj != null) {
			accountNo = bObj.getString("accountnumber");
			accstr = bObj.getString("accountstr");
			accInfo = bObj.getString("accountinfo");
		}
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		accNo = (TextView) findViewById(R.id.accNo);
		txt_cust_name = (TextView) findViewById(R.id.cust_name);
		maturity_amnt = (EditText) findViewById(R.id.maturity_amnt);
		maturity_date = (EditText) findViewById(R.id.maturity_date);
		intrest_value = (EditText) findViewById(R.id.intrest_value);
		opening_amnt = (EditText) findViewById(R.id.opening_amnt);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_underliened = (EditText) findViewById(R.id.underliened);
		opng_dt = (EditText) findViewById(R.id.opng_dt);
		txt_instl_amt = (EditText) findViewById(R.id.instmnt_amnt);
		txt_instl_frq = (EditText) findViewById(R.id.instmnt_frqncy);
		txt_curbal = (EditText) findViewById(R.id.curbal);
		tv_curbal = (TextView) findViewById(R.id.txtlbl_curbal);
		txt_lbl_underLiened = (TextView) findViewById(R.id.txtlbl_underliened);
		txtLayout = (LinearLayout) findViewById(R.id.rdtxtextrslayout);
		edtLayout = (LinearLayout) findViewById(R.id.rdedtextrslayout);
		amtTxtLayout = (LinearLayout) findViewById(R.id.lyt_amt_txt);
		amtEditLayout = (LinearLayout) findViewById(R.id.lyt_amt_edttxt);
		tv_pnd_instal = (TextView) findViewById(R.id.txtlbl_pnd_instl);
		tv_intvl_int = (TextView) findViewById(R.id.txtlbl_intrvl_int);
		tv_int_frq = (TextView) findViewById(R.id.txtlbl_intrvl_int_frq);
		txt_pnd_instl = (EditText) findViewById(R.id.pndt_instmnt);
		txt_intvl_int = (EditText) findViewById(R.id.intrvl_int);
		txt_intv_int_frq = (EditText) findViewById(R.id.intrvl_int_frq);

		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);

		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);

		btn_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);

		txt_heading.setText(getString(R.string.lbl_acc_details));
		// Log.e("FdRdAccountDetail", strtext);
		// 5-101-FD-3355-KADEKAR PRAKASH KIRAN
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");

		String txt; // = sp.getString("retValStr", "retValStr");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custid = c1.getString(2);
				Log.e("custId", "......" + custid);
				txt = c1.getString(0);
				Log.e("retvalstr", "c......" + txt);
			}
		}

		Log.e("OMKAR", accstr);
		Log.e("OMKAR", accstr);
		accstr = accstr.replaceAll("#", "-");
		Log.e("OMKAR", accstr);
		Log.e("OMKAR", accstr);
		if (accstr.split("-")[2].equalsIgnoreCase("PG")
				|| accstr.split("-")[2].equalsIgnoreCase("RA")) {
			txtLayout.setVisibility(LinearLayout.VISIBLE);
			edtLayout.setVisibility(LinearLayout.VISIBLE);
			txt_lbl_underLiened.setVisibility(LinearLayout.GONE);
			txt_underliened.setVisibility(LinearLayout.GONE);
			tv_curbal.setVisibility(LinearLayout.VISIBLE);
			txt_curbal.setVisibility(LinearLayout.VISIBLE);
			txt_intvl_int.setVisibility(EditText.GONE);
			tv_intvl_int.setVisibility(TextView.GONE);
			tv_int_frq.setVisibility(TextView.GONE);
			txt_intv_int_frq.setVisibility(EditText.GONE);
			if (accstr.split("-")[2].equalsIgnoreCase("PG")) {
				amtTxtLayout.setVisibility(LinearLayout.GONE);
				amtEditLayout.setVisibility(LinearLayout.GONE);
				tv_pnd_instal.setVisibility(TextView.GONE);
				txt_pnd_instl.setVisibility(EditText.GONE);
			} else {
				amtTxtLayout.setVisibility(LinearLayout.VISIBLE);
				amtEditLayout.setVisibility(LinearLayout.VISIBLE);
				tv_pnd_instal.setVisibility(TextView.VISIBLE);
				txt_pnd_instl.setVisibility(EditText.VISIBLE);
			}
		} else {
			txtLayout.setVisibility(LinearLayout.GONE);
			edtLayout.setVisibility(LinearLayout.GONE);
			txt_lbl_underLiened.setVisibility(LinearLayout.VISIBLE);
			txt_underliened.setVisibility(LinearLayout.VISIBLE);
			tv_curbal.setVisibility(LinearLayout.GONE);
			txt_curbal.setVisibility(LinearLayout.GONE);
			txt_intvl_int.setVisibility(EditText.VISIBLE);
			tv_intvl_int.setVisibility(TextView.VISIBLE);
			tv_pnd_instal.setVisibility(TextView.GONE);
			txt_pnd_instl.setVisibility(EditText.GONE);
			tv_int_frq.setVisibility(TextView.VISIBLE);
			txt_intv_int_frq.setVisibility(EditText.VISIBLE);
		}

		String accountNoStr = accNo.getText() + " " + accInfo;// MBSUtils.get16digitsAccNo(accstr);
		accNo.setText(accountNoStr);

		String custName = MBSUtils.getCustName(accountNo);
		txt_cust_name.setText(custName);

		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetFDAccDetails c = new CallWebServiceGetFDAccDetails();
			c.execute();
		}
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			Intent in = new Intent(getApplicationContext(), ShowFdAccount.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;

		case R.id.btn_home:
			/*
			 * Intent in = new Intent(act, DashboardDesignActivity.class);
			 * startActivity(in); act.finish(); break;
			 */
		}

	}

	class CallWebServiceGetFDAccDetails extends AsyncTask<Void, Void, Void> {// CallWebServiceGetFDAccDetails

		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = { "CUSTID", "ACCNO", "IMEINO" };
	

		JSONObject jsonObj = new JSONObject();
	

		@Override
		protected void onPreExecute() {
			try {
				loadProBarObj.show();
				retval = "";
				respcode = "";
				respdesc = "";
				// accountNo=accNo.getText().toString().split(":")[1].trim();
				Log.e("CUSTID", "custId:" + custid);
				Log.e("ACCNO", "accountNo:" + accountNo);

				jsonObj.put("CUSTID", custid);
				jsonObj.put("ACCNO", accountNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","31"); 
			} 
			catch (JSONException je) 
			{
				je.printStackTrace();
			}
			
			

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			// Log.tem.out.println("============= inside doInBackground =================");
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
					//isWSCalled=true;
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
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			if(respdesc.length()>0)
			{
				showAlert(respdesc);
			}
			else{
		try{
			Log.e("Debug@retvalweb-------", retvalweb);
			if (retvalweb.indexOf("FAILED") > -1) 
			{
				post_success(retvalweb);
			} 		
			else 
			{
				post_GetFDAccDetails(retvalweb);
			}
	}
		catch (Exception je) {
            je.printStackTrace();
        }}
		}// end onPostExecute

	}// end CallWebServiceGetFDAccDetails

	public 	void post_success(String retvalweb)
	{
		respcode="";
		
		respdesc="";
		retMess = getString(R.string.alert_092);
		showAlert(retMess);
	}
	
	public void post_GetFDAccDetails(String retval) {
		respcode = "";
		respdesc = "";
		Log.e("post_GetFDAccDetails", "post_GetFDAccDetails=====" + retval);
		if (retval.indexOf("FAILED") > -1) {
			retMess = getString(R.string.alert_092);
			showAlert(retMess);
			//valToEncrpt= {"RETVAL":"SUCCESS~MAHADWAR ROAD BR.#FD# KULKARNI SHASHIKANT  RAJARAM#45000#0#PNSB0010102","RESPCODE":"0"}
		} else {
			retval = retval.split("SUCCESS~")[1];
			String[] retValues = retval.split("#");

			if (accstr.split("-")[2].equalsIgnoreCase("FD")
					|| accstr.split("-")[2].equalsIgnoreCase("CD")
					|| accstr.split("-")[2].equalsIgnoreCase("RP")) {
				// 2762#02-APR-18#8#2222#Y#02-APR-15#Mrs. SHARMA VIBHOR P
				//
				// retval="2763#02-AUG-18#8#2942#Y#02-AUG-15#3652#Monthly#Mr. KADAM SUSHANT  D";
				retValues = retval.split("#");
				maturity_amnt.setText(MBSUtils.amountFormat(retValues[0],
						false, this));
				maturity_date.setText(retValues[1]);
				intrest_value.setText(retValues[2]);
				opening_amnt.setText(MBSUtils.amountFormat(retValues[3], false,
						this));
				if (retValues[4].equalsIgnoreCase("Y")) {
					txt_underliened.setText("Yes");
				} else {
					txt_underliened.setText("No");
				}
				opng_dt.setText(retValues[5]);
				txt_intvl_int.setText(MBSUtils.amountFormat(retValues[6],
						false, this));
				txt_intv_int_frq.setText(retValues[7]);
				txt_cust_name.setText(retValues[8]);

			} else/*
				 * if(accstr.split("-")[2].equalsIgnoreCase("PG") ||
				 * accstr.split("-")[2].equalsIgnoreCase("RA"))
				 */
			{

				//retval = "241404#10-MAY-18#7#225220#10-MAY-17#1200#Monthly#3600#0#Mr. KADAM SUSHANT  D";
				retValues = retval.split("#");

				if (!accstr.split("-")[2].equalsIgnoreCase("PG"))
					maturity_amnt.setText(MBSUtils.amountFormat(retValues[0],
							false, this));
				maturity_date.setText(retValues[1]);
				intrest_value.setText(retValues[2]);
				opening_amnt.setText(MBSUtils.amountFormat(retValues[3], false,
						this));
				opng_dt.setText(retValues[4]);
				txt_instl_amt.setText(MBSUtils.amountFormat(retValues[5],
						false, this));
				txt_instl_frq.setText(retValues[6]);
				txt_curbal.setText(MBSUtils.amountFormat(retValues[7], false,
						this));
				txt_pnd_instl.setText(retValues[8]);
				txt_cust_name.setText(retValues[9]);

			}

		}
	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {

			@Override
			public void onClick(View v) {
				// Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) {
				case R.id.btn_ok:
					// Log.e("SetMPIN","SetMPIN...CASE trru="+isWSCalled);

					if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("0"))) {
						Log.e("sudarshan", "retvalllll=====11111==="
								+ ret_retval);
						post_GetFDAccDetails(ret_retval);
					} else if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
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
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			// Log.tem.out.println("state1 ---------" + state1);
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
