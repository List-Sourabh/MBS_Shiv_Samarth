package list.shivsamarth_mbs;

import java.security.PrivateKey;

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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FundTransferMenuActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener 
{
	ArrayAdapter<MenuIcon> aa;
	ImageButton btn_home, btn_back;
	Button but_exit;
	DialogBox dbs;

	private ListView listView1;
	ListView lst_dpt;
	TextView txt_heading;
	ImageView img_heading;
	int flag = 0;
	int check = 0;
	DatabaseManagement dbms, dbms1;
	String retValStr = "", custid = "", retMess = "", version = "",
			retval = "", respcode = "", respdesc = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME1 = "";
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finance_submenu);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		txt_heading.setText(getString(R.string.lbl_fund_transfer));
		img_heading.setBackgroundResource(R.mipmap.transfer);

		try {
			PackageInfo pInfo = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
			version = pInfo.versionName;
			Log.e("PackageInfo", "PackageInfo" + version);
			Log.e("PackageInfo", "PackageInfo" + version);
			Log.e("PackageInfo", "PackageInfo" + version);
			Log.e("PackageInfo", "PackageInfo" + version);
		} catch (Exception e) {
			e.printStackTrace();
		}

		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				retValStr = c1.getString(0);
				Log.e("retValStr", "......" + retValStr);
				custid = c1.getString(2);
				Log.e("CustId", "c......" + custid);
			}
		}
	
		MenuIcon menuItem[] = new MenuIcon[] {
				new MenuIcon(getString(R.string.lbl_own_account_trans),
						R.mipmap.arrow),
				new MenuIcon(getString(R.string.lbl_same_bnk_trans),
						R.mipmap.arrow),
				new MenuIcon(getString(R.string.lbl_other_bank_fund_trans_rtgs),
						R.mipmap.arrow),
				new MenuIcon(getString(R.string.lbl_qr_send), 
						R.mipmap.arrow),
				new MenuIcon(getString(R.string.lbl_qr_receive),
						R.mipmap.arrow),
				new MenuIcon(getString(R.string.lbl_transfer_history),
						R.mipmap.arrow), };

		MenuAdaptor adapter = new MenuAdaptor(this, R.layout.listview_item_row,
				menuItem);

		listView1 = (ListView) findViewById(R.id.listView1);
		View header = (View) this.getLayoutInflater().inflate(
				R.layout.fundmenu_listview_header_row, null);
		listView1.addHeaderView(header);
		listView1.setAdapter(adapter);

		listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView1.setOnItemClickListener(this);

		try {
			// this.flag = chkConnectivity();
			Log.e("ohtertranImpsbtn_submit", " SUBMIT SUBMIT" + flag);
			// if (this.flag == 0)
			{
				// saveData();
				// new CallWebServiceGetAccounts().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in CallWebServiceGetSrvcCharg is:"
					+ e);
		}
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_back || v.getId() == R.id.btn_home) {
			Intent in = new Intent(this, DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
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

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		// Log.i("Testing", "Inside itemclicklistener");
		// int pos=lst_dpt.getCheckedItemPosition();
		int pos = listView1.getCheckedItemPosition();
		Intent in = null;
		Bundle b = new Bundle();
		// Log.i("Testing", "pos :" + pos);

		/*
		 * Fragment fragment; FragmentManager fragmentManager;
		 */
		switch (pos) {

		case 1:
			Intent intet = new Intent(getApplicationContext(),
					OwnAccountTransfer.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;

		case 2:


			Log.e("MBS Case -2", "........11 SameBankTransfer");
			 intet = new Intent(getApplicationContext(),
					SameBankTransfer.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;

		case 3:

			Log.e("MBS Case -2", "........11 OtherbanktransferRTGS");
			intet = new Intent(getApplicationContext(),OtherBankTranRTGS.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;
			
		case 4:

			Log.e("MBS Case -2", "........11 QrcodeSendActivity");
			intet = new Intent(getApplicationContext(),
					QrcodeSendActivity.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;
		case 5:
			Log.e("MBS Case -2", "........11 QrcodeRcvActivity");
			intet = new Intent(getApplicationContext(), ShowAccForQrcode.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;

		case 6:
			Log.e("MBS Case -2", "........11 TransferHistory");
			intet = new Intent(getApplicationContext(), TransferHistory.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;
		default:
			// Log.e("FUNDTRANSMENUACT","OnItemClick default case");
			break;
		}
	}

	class CallWebService_getAccounts extends AsyncTask<Void, Void, Void> {

		// LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = { "IMEINO" };
	
		
		JSONObject jsonObj = new JSONObject();
		
		LoadProgressBar loadProBarObj = new LoadProgressBar(FundTransferMenuActivity.this);

		@Override
		protected void onPreExecute() 
		{
			try 
			{
				loadProBarObj.show();
				
				String service = Context.TELEPHONY_SERVICE;
				String imeiNo = MBSUtils.getImeiNumber(FundTransferMenuActivity.this);
				jsonObj.put("CUSTID", custid + "~#~" + version);
				jsonObj.put("IMEINO", imeiNo);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(FundTransferMenuActivity.this));
				jsonObj.put("METHODCODE","54"); 
				Log.e("CUSTID", "custid= " + custid + "~#~" + version);
				Log.e("IMEI NO", "imeino= " + imeiNo);
			} 
			catch (JSONException je) 
			{
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
			Log.e("Sharayu========","INside  Post Excute==getaccount");
			
			// String decryptedMsg = xml_data[0];
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
				// Log.e("Result===","Result====="+decryptedMsg);
				Log.e("111", "0000");

				Log.e("LIST== ", "tryy in FUND transfer activity");
				if (retval.indexOf("FAILED") > -1) {

					Log.e("FAILED= ", "FAILED=");
				} else {
					if (retval.indexOf("SUCCESS") > -1) {

						post_success(retval);

					}
				}// else
			}
		}
	}

	public void post_success(String retval) {

		respcode = "";
		respdesc = "";
		String decryptedAccounts = retval.split("SUCCESS~")[1];
		Log.e("In Login", "decryptedAccounts,,,,, :" + decryptedAccounts);
		if (!decryptedAccounts.equals("FAILED#")) {
			System.out.println("in if ***************************************");
			System.out.println("decryptedAccounts :" + decryptedAccounts);
			String splitstr[] = decryptedAccounts.split("!@!");
			Log.e("==--==", "splitstr.len :" + splitstr);
			Log.e("==--==", "splitstr.len :" + splitstr.length);

			{
				Bundle b = new Bundle();
				String accounts = splitstr[0];
				String mobno = splitstr[1];
				// String tranMpin = splitstr[2];
				custid = splitstr[3];
				String userId = splitstr[4];
				System.out.println("mobno :" + mobno);

				String[] columnNames = { "retval_str", "cust_name", "cust_id",
						"user_id", "cust_mobno" };
				String[] columnValues = { accounts, "", custid, userId, mobno };

				dbms1.deleteFromTable("SHAREDPREFERENCE", "", null);
				dbms1.insertIntoTable("SHAREDPREFERENCE", 5, columnNames,
						columnValues);

				Log.e("LOGIN", "accounts==" + accounts);
				Log.e("LOGIN", "custid==" + custid);
				// Log.e("LOGIN", "encrptdMpin==" + encrptdMpin);
				// Log.e("LOGIN", "tranMpin==" + tranMpin);
				Log.e("LOGIN", "mobno==" + mobno);
				Log.e("LOGIN", "userId==" + userId);
			}
		} else {
			retMess = getString(R.string.alert_prblm_login);
			showAlert(retMess);
		}
	}

	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(FundTransferMenuActivity.this, "" + str) 
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if ((str.equalsIgnoreCase(respdesc))&& (respcode.equalsIgnoreCase("0"))) 
						{
							post_success(retval);
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

	@Override
	public void onBackPressed() {
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
