package list.shivsamarth_mbs;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.HorizontalListView;
import mbLib.HorizontalviewAdapter;
import mbLib.MBSUtils;
import mbLib.MyThread;

public class DashboardActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener , OnItemSelectedListener
{
	TextView txt_heading,cust_nm,last_login,balance;
	LinearLayout billImg,transferImg,ministatementImg,rechargeImg;
	ImageView img_heading;
	Spinner select_accnt;
	ImageButton select_accnt_btn,btn_logout;
	HorizontalListView horizontalListview;
	ArrayList<String> rechargeTags = new ArrayList<String>();
	DatabaseManagement dbms,dbms1;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private MyThread t1;
	String retValStr="",custid="",str2 = "",retMess="",retval="",lastLogin=""; 
	String version="",respcode = "", retvalwbs = "", respdesc = "",
			respdescgetacc = "";
	Accounts acArray[];
	DialogBox dbs;
	int flag=0;
	int timeOutInSecs=300;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	private static final String MY_SESSION = "my_session";
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dash);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.home);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		cust_nm=(TextView)findViewById(R.id.cust_nm);
		last_login=(TextView)findViewById(R.id.last_login);
		balance=(TextView)findViewById(R.id.balance);
		select_accnt=(Spinner)findViewById(R.id.select_accnt);
		select_accnt_btn=(ImageButton)findViewById(R.id.select_accnt_btn);
		//txt_heading=(TextView)findViewById(R.id.txt_heading);
		billImg=(LinearLayout)findViewById(R.id.billImg);
		transferImg=(LinearLayout)findViewById(R.id.transferImg);
		ministatementImg=(LinearLayout)findViewById(R.id.ministatementImg);
		rechargeImg=(LinearLayout)findViewById(R.id.rechargeImg);
		btn_logout = (ImageButton) findViewById(R.id.btn_back);
		billImg.setOnClickListener(this);
		transferImg.setOnClickListener(this);
		ministatementImg.setOnClickListener(this);
		rechargeImg.setOnClickListener(this);
		select_accnt_btn.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		horizontalListview=(HorizontalListView)findViewById(R.id.list_tags);
		
		rechargeTags.add(getString(R.string.lbl_current_accnt));
		rechargeTags.add(getString(R.string.title_activity_loan_account));
		rechargeTags.add(getString(R.string.lbl_term_deposit));
		rechargeTags.add(getString(R.string.lbl_Manage_Beneficiary));
		//rechargeTags.add(getString(R.string.lbl_Services));
		rechargeTags.add(getString(R.string.lbl_Change_MPIN));
		
		HorizontalviewAdapter adapter=new HorizontalviewAdapter(this, rechargeTags);
		horizontalListview.setAdapter(adapter);
	
		///horizontalListview.setOnClickListener(this);
		horizontalListview.setOnItemClickListener(this);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) 
		{
			while(c1.moveToNext()) 
			{
				retValStr = c1.getString(0);
				Log.e("retValStr", "......" + retValStr);
				custid = c1.getString(2);
				Log.e("CustId", "c......" + custid);
				lastLogin = c1.getString(5);
				Log.e("lastLogin", "lastLogin......" + lastLogin);
			}
		}
		last_login.setText("Last Login "+lastLogin);
		//retValStr="2#101#SB#25730#Mr. KADAM SUSHANT  D##0020001010025730#O#NA#2797.4#Y~2#101#SB#25733#Mr. KADAM SUSHANT  D##0020001010025733#O#NA#12305#Y~2#101#SB#25768#Mr. KADAM SUSHANT  D##0020001010025768#O#NA#3050#Y~2#101#SB#25791#Mr. KADAM SUSHANT  D##0020001010025791#O#NA#7506#Y";
		Log.e("DashboardDesignActivity", "retValStr==" + retValStr);
		String[] arr = retValStr.split("~");
		// Log.e("DashboardDesignActivity","arr[0]=="+arr[0]);
		String name = arr[0].split("#")[4];
		
		cust_nm.setText(initCapName(name));
		addAccounts(retValStr);
		select_accnt.setOnItemSelectedListener(this);
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	
	public void addAccounts(String str) {
		System.out.println("Dashboard IN addAccounts()" + str);

		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			//arrList.add("Select Debit Account");
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) 
			{
				str2 = allstr[i];
				String tempStr=str2;
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				
				if (((accType.equals("SB")) || (accType.equals("CA"))
						|| (accType.equals("LO"))))// && oprcd.equalsIgnoreCase("O"))
				{
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(this,R.layout.spinner_item, debAccArr);
			//CustomSpinner debAccs = new CustomSpinner(this,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			select_accnt.setAdapter(debAccs);
			
			Accounts selectedDrAccount=acArray[0];
			String balStr=selectedDrAccount.getBalace();
			String drOrCr="";
			float amt=Float.parseFloat(balStr);
			if(amt>0)
				drOrCr=" Cr";
			else if(amt<0)
				drOrCr=" Dr";
			if(balStr.indexOf(".")==-1)
				balStr=balStr+".00";
			balStr=balStr+drOrCr;
			balance.setText("Rs. "+balStr);
			
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/*try
		{
			t1.stop();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
		if(v.getId() == R.id.rechargeImg)
		{
			Toast.makeText(DashboardActivity.this, "Currently In-Active", Toast.LENGTH_SHORT).show();
			/*Intent in=new Intent(this,Recharges.class);
			startActivity(in);
			this.finish();*/
		}
		else if(v.getId() == R.id.billImg)
		{
			Toast.makeText(DashboardActivity.this, "Currently In-Active", Toast.LENGTH_SHORT).show();
			/*Intent in=new Intent(this,BillList.class);
			startActivity(in);
			this.finish();*/
		}
		else if(v.getId() == R.id.transferImg)
		{
			Intent in=new Intent(this,FundTransferMenuActivity.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
		}
		else if(v.getId() == R.id.ministatementImg)
		{
			Intent in=new Intent(this,MiniStmtActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
		}
		else if(v.getId()==R.id.select_accnt_btn)
		{
			select_accnt.performClick();
		}
		else if(v.getId()==R.id.btn_back)
		{showlogoutAlertbtn(getString(R.string.lbl_exit));

			/*dbs = new DialogBox(this);
			dbs.get_adb().setMessage(getString(R.string.lbl_exit));
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							flag = chkConnectivity();
							if (flag == 0) 
							{
								CallWebService c = new CallWebService();
								c.execute();
							}
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
						}
					});
			dbs.get_adb().show();*/
		}
	}
	public void showlogoutAlertbtn(final String str)
	{
		CustomDialogClass alert = new CustomDialogClass(DashboardActivity.this,str)
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
						flag = chkConnectivity();
						if (flag == 0) {
							CallWebService c = new CallWebService();
							c.execute();

						}

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
	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) 
	{
		int pos = horizontalListview.getSelectedItemPosition();
		Intent in = null;
		Bundle b = new Bundle();
		switch (paramInt) 
		{
			case 0:
				Log.e("Patsanstha Case -2", "........SavingAccounts");
				Intent in1=new Intent(this,SavingAccounts.class);
				in1.putExtra("var1", var1);
				   in1.putExtra("var3", var3);
				startActivity(in1);
				finish();
				break;
			case 1:
				Log.e("Patsanstha Case -2", "........LoanAccount");
				in=new Intent(this,LoanAccount.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case 2:	
				Log.e("Patsanstha Case -2", "........ShowFdAccount");
				in=new Intent(this,ShowFdAccount.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case 3:
				Log.e("Patsanstha Case -2", "........ManageBeneficiaryMenuActivity");
				in=new Intent(this,ManageBeneficiaryMenuActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case 4:
				Log.e("Patsanstha Case -2", "........ChangeMpin");
				in=new Intent(this,ChangeMpin.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			
		/*case 5:
			
			Log.e("Patsanstha-6", "11 Receive QR Code");

			
			
			break;
		case 6:
			Log.e("Patsanstha -4", "11 Transfer History");

			
			break;*/
		default: 
			//Log.e("FUNDTRANSMENUACT","OnItemClick default case");
			break;
			
		
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	{
		String str=select_accnt.getSelectedItem().toString();
		Log.e("arg2= ","position="+position);
		str = arrListTemp.get(select_accnt.getSelectedItemPosition());
		String debitAc[] = str.split("-");
		System.out.println("account 1:" + debitAc[0]);// 5
		System.out.println("account 2:" + debitAc[1]);// 101
		System.out.println("account 4:" + debitAc[3]);// 7
	
		String mmid=debitAc[8];
		Log.e("MMID","MMID  "+mmid);
		if(mmid.equals("NA"))
		{
			//showAlert( getString(R.string.lbl_mmid_msg));
		}
			
		Accounts selectedDrAccount=acArray[select_accnt.getSelectedItemPosition()];
		String balStr=selectedDrAccount.getBalace();
		String drOrCr="";
		float amt=Float.parseFloat(balStr);
		if(amt>0)
			drOrCr=" Cr";
		else if(amt<0)
			drOrCr=" Dr";
		if(balStr.indexOf(".")==-1)
			balStr=balStr+".00";
		balStr=balStr+drOrCr;
		balance.setText("Rs. "+balStr);	
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	class CallWebService extends AsyncTask<Void, Void, Void> {
		// String[] xmlTags = { "CUSTID","IMEINO" };
		
		JSONObject jsonObj = new JSONObject();
	LoadProgressBar lod= new LoadProgressBar(DashboardActivity.this);

		@Override
		protected void onPreExecute() 
		{
			try 
			{lod.show();
				jsonObj.put("CUSTID", custid);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(DashboardActivity.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(DashboardActivity.this));
				jsonObj.put("METHODCODE","29"); 
			} catch (JSONException je) {
				je.printStackTrace();
			}
			
			// Log.e("Debug","Trying: "+generatedXML);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			//Log.e("@DEBUG","LOGOUT doInBackground()");
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

		protected void onPostExecute(final Void result) {
			Log.e("@DEBUG", "LOGOUT onPostExecute()");
			// String[] xmlTags = { "STATUS" };
		
			// String decryptedBeneficiaries = xml_data[0];
			JSONObject jsonObj;
			try {
				lod.dismiss();
				String str=CryptoClass.Function6(var5,var2);
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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		}
	}
	public void post_success(String retvalwbs) {
		respcode = "";
		respdesc = "";
		finish();
		System.exit(0);

	}
	
	public int chkConnectivity() {// chkConnectivity
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) 
					{}
					break;
				case DISCONNECTED:
					flag = 1;
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
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
					flag = 1;
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
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
	
	public void showAlert(final String str) 
	{
    	ErrorDialogClass alert = new ErrorDialogClass(DashboardActivity.this, "" + str)
    	{
    		@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retvalwbs);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(respdescgetacc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retvalwbs);
						}
						else if((str.equalsIgnoreCase(respdescgetacc)) && (respcode.equalsIgnoreCase("1")))
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
	 // Simply Do noting!
	}
	
	class CallWebService_getAccounts extends AsyncTask<Void, Void, Void> {

		// LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = { "IMEINO" };
	
		JSONObject jsonObj = new JSONObject();
	

		@Override
		protected void onPreExecute() {
			try {

				try {
					Log.e("Sharayu111", "Inside PreExcuted==getaccount");
					PackageInfo pInfo = DashboardActivity.this
							.getPackageManager()
							.getPackageInfo(
									DashboardActivity.this
											.getPackageName(),
									0);
					version = pInfo.versionName;
					Log.e("PackageInfo", "PackageInfo====" + version);
					Log.e("PackageInfo", "PackageInfo=====" + version);

				} catch (Exception e) {
					e.printStackTrace();
				}
				dbms1 = new DatabaseManagement("panchganga.mobilebank",
						"panchgangaMBS");
				Log.e("onPreExecute=", "dbms======" + dbms1);
				Cursor c2 = dbms1.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
				// null);
				if (c2 != null) {
					while (c2.moveToNext()) {
						retValStr = c2.getString(0);
						custid = c2.getString(2);
					}
				}

				/*
				 * String service = Context.TELEPHONY_SERVICE; TelephonyManager
				 * telephonyManager = (TelephonyManager)
				 * getSystemService(service); String imeiNo =
				 * telephonyManager.getDeviceId();
				 */
				String imeiNo = MBSUtils
						.getImeiNumber(DashboardActivity.this);
				jsonObj.put("CUSTID", custid + "~#~" + version);
				jsonObj.put("IMEINO", imeiNo);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(DashboardActivity.this));
				jsonObj.put("METHODCODE","54"); 
				Log.e("zzzzzzzzzzzzzzz", "zzzzzzzzzzzzzzzzzzzz==" + custid
						+ "~#~" + version);

				/*
				 * valuesToEncrypt[1] = custId; valuesToEncrypt[2] =
				 * MBSUtils.getImeiNumber(act);
				 */
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
		protected void onPostExecute(final Void result) {Log.e("DAAAAAAAAAAAAAAAA","INside  Post Excute");
		
		//String decryptedMsg = xml_data[0];
		
		//Log.e("DAAAAAAAAAAAAAAAA","DATa"+decryptedMsg);
		Log.e("111","0000");
		
		JSONObject jsonObj;
		try
		{

			String str=CryptoClass.Function6(var5,var2);
			jsonObj = new JSONObject(str.trim());
			Log.e("IN return", "data :" + jsonObj.toString());
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
				retvalwbs = jsonObj.getString("RETVAL");
			}
			else
			{
				retvalwbs = "";
			}
			if (jsonObj.has("RESPDESC"))
			{
				respdescgetacc = jsonObj.getString("RESPDESC");
			}
			else
			{	
				respdescgetacc = "";
			}
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(respdescgetacc.length()>0)
		{
			showAlert(respdescgetacc);
		}
		else{
			Log.e("LIST== ","tryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
		if (retvalwbs.indexOf("FAILED") > -1) {
			
			Log.e("FAILED= ","FAILED=");							
		} 
		else
		{
			if (retvalwbs.indexOf("SUCCESS") >-1) 
			{
				post_successgetacc(retvalwbs);
			}
		}
	}}
	}
	
	public void post_successgetacc(String retvalwbs)
	{
	    respcode="";
		respdescgetacc="";
	  String decryptedAccounts = retval.split("SUCCESS~")[1];
		Log.e("In Login", "decryptedAccounts,,,,, :"
				+ decryptedAccounts);
		if (!decryptedAccounts.equals("FAILED#")) 
		{
			System.out.println("in if ***************************************");
			System.out.println("decryptedAccounts :"
					+ decryptedAccounts);
			String splitstr[] =decryptedAccounts.split("!@!");
			Log.e("==--==","splitstr.len :" + splitstr);
			Log.e("==--==","splitstr.len :" + splitstr.length);
			String oldversion=splitstr[5];
			if(oldversion.equals("OLDVERSION"))
			{
				//showlogoutAlert(getString(R.string.alert_oldversionupdate));
			}
			else
			{
				Bundle b = new Bundle();
				String accounts = splitstr[0];
				String mobno =  splitstr[1];
				String tranMpin =  splitstr[2];
				custid = splitstr[3];
				String userId=splitstr[4];
				System.out.println("mobno :" + mobno);
				
				String[] columnNames={"retval_str","cust_name","cust_id","user_id","cust_mobno"};
				String[] columnValues={accounts,"",custid,userId,mobno};
					
				dbms.deleteFromTable("SHAREDPREFERENCE", "",null);
				dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);
					

				Log.e("LOGIN", "accounts==" + accounts);
				Log.e("LOGIN", "custid==" + custid);
				
				Log.e("LOGIN", "tranMpin==" + tranMpin);
				Log.e("LOGIN", "mobno==" + mobno);
				Log.e("LOGIN", "userId==" + userId);					
			}
		}
		else
		{
			retMess = getString(R.string.alert_prblm_login);
		}
		
	}
	
	public String initCapName(String name)
	{
		name=name.trim();
		String initCapNm="";
		String chara="";
		System.out.println("=="+name.length());
		int spaceCnt=0,initCapCnt=1;
		for(int i=0;i<name.length();i++)
		{
			if(name.charAt(i)==' ')
				spaceCnt++;
			else
				spaceCnt=0;
			//System.out.println("spaceCnt="+spaceCnt);
			if(spaceCnt==0)
			{
				chara=""+name.charAt(i);
				if(initCapCnt==1)
					initCapNm=initCapNm+chara.toUpperCase();
				else
					initCapNm=initCapNm+chara.toLowerCase();
				initCapCnt=0;
			}
			else if(spaceCnt==1)
			{
				initCapNm=initCapNm+" ";
				initCapCnt=1;
			}
		}
		System.out.println("initCapNm==="+initCapNm);
		return initCapNm;
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
