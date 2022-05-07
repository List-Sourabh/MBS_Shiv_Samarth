package list.shivsamarth_mbs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
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
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;

public class AddOtherBankBeneficiary extends Activity implements OnClickListener {
	AddOtherBankBeneficiary act = this;
	AddOtherBankBeneficiary addOtherBankBenf;
	EditText txtIFSC_Code, txtMMID, txtMobile_No, txtAccNo, txtName, txtBank,
			txtBranch, txtEmail, txtNick_Name,txtAccNoconf;
	Button btn_submit, fetchIFSC;
	LinearLayout add_benf_layout, get_ifsc_layout;
	TextView txt_heading;
	ProgressBar p_wait;
	JSONArray jsonArr;
	DatabaseManagement dbms;
	String flg="false",confaccNo="";
	Spinner spi_bank, spi_state, spi_district, spi_city, spi_branch;
	ImageButton btn_home, spnr_btn1, spnr_btn2, spnr_btn3, spnr_btn4,btn_back,
			spnr_btn5, btn_fetchBnkBrn;
	// DialogBox dbs;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME2 = "";
	// private static final String METHOD_GET_BANK_BRANCH = "getBnkBrn";
	private static  String METHOD_GET_BANK = "";
	private static  String METHOD_GET_STATES = "";
	private static  String METHOD_GET_DISTRICT = "";
	private static  String METHOD_GET_CITIES = "";
	private static  String METHOD_GET_BRANCH = "";
	private static  String METHOD_GET_IFSC = "";

	String account_No = "", name = "", mobile_no = "", nick_name = "",
			email = "", same_bank = "", ifsc_code = "", insrtUpdtDlt = "";
	String stringValue, str2 = "", mobPin = "", tmpXMLString = "", retVal = "",
			userpin = "";
	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "";
	String ifsCD = "", bnCD = "", brCD = "", mmId = "", strIfsc = "";
	String acnt_inf, all_acnts, str = "", retMess = "", cust_name = "",
			checkedValue = "";
	int cnt = 0, flag = 0, checkCnt = 0;
	Bundle bdn;
	public String encrptdMpin;
	String when_fetch = "",respcode="",retvalweb="",respdescget_bnkbrn="",respdescsave_beneficiary="";
	String respdescGetStates="",respdescGetDistricts="",respdescGetCities="",respdescGetBranches="",respdescGetIFSC="";
	private String userId;
	ImageView img_heading,btn_home1;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	private MyThread t1;
	int timeOutInSecs=300;
	public AddOtherBankBeneficiary() {
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_otherbank_beneficiary);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.add_beneficiary);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				Log.e("retValStr", "......" + custId);
			}
		}

		txtIFSC_Code = (EditText) findViewById(R.id.txtIFSC_Code2);
		txtMMID = (EditText) findViewById(R.id.txtMMID2);
		txtMobile_No = (EditText) findViewById(R.id.txtMobile_No2);
		txtAccNo = (EditText) findViewById(R.id.txtAccNo2);
		txtAccNoconf= (EditText) findViewById(R.id.txtAccNoconf);
		txtName = (EditText) findViewById(R.id.txtName2);
		txtBank = (EditText) findViewById(R.id.txt_bank_name);
		txtEmail = (EditText) findViewById(R.id.txtEmail2);
		txtNick_Name = (EditText) findViewById(R.id.txtNick_Name2);
		p_wait = (ProgressBar) findViewById(R.id.pro_bar);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		btn_back.setImageResource(R.mipmap.backover);
		txt_heading.setText(getString(R.string.frmtitle_add_other_bnk_bnf));

		spi_bank = (Spinner) findViewById(R.id.spnr_bank_name);
		spi_state = (Spinner) findViewById(R.id.spnr_state);
		spi_district = (Spinner) findViewById(R.id.spnr_district);
		spi_city = (Spinner) findViewById(R.id.spnr_city);
		spi_branch = (Spinner) findViewById(R.id.spnr_branch);

		spnr_btn1 = (ImageButton) findViewById(R.id.spinner_btn1);
		spnr_btn2 = (ImageButton) findViewById(R.id.spinner_btn2);
		spnr_btn3 = (ImageButton) findViewById(R.id.spinner_btn3);
		spnr_btn4 = (ImageButton) findViewById(R.id.spinner_btn4);
		spnr_btn5 = (ImageButton) findViewById(R.id.spinner_btn5);

		// fetchIFSC.setOnClickListener(this);
		spnr_btn1.setOnClickListener(this);
		spnr_btn2.setOnClickListener(this);
		spnr_btn3.setOnClickListener(this);
		spnr_btn4.setOnClickListener(this);
		spnr_btn5.setOnClickListener(this);
	
		add_benf_layout = (LinearLayout) findViewById(R.id.add_benf_layout);
		get_ifsc_layout = (LinearLayout) findViewById(R.id.get_ifsc_layout);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);

		btn_fetchBnkBrn = (ImageButton) findViewById(R.id.btn_fetchIFSC);
		btn_submit = (Button) findViewById(R.id.btn_submit2);

		System.out.println("============ inside onCreate 4 ===============");
		btn_fetchBnkBrn.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		txtIFSC_Code.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});
		spi_bank.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_bank.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select Bank--")) {
					spi_state.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetStates C = new CallWebServiceGetStates();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_state.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_state.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select State--")) {
					spi_district.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetDistricts C = new CallWebServiceGetDistricts();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_district.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_district.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select District--")) {
					spi_city.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetCities C = new CallWebServiceGetCities();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_city.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_city.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select City--")) {
					spi_branch.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetBranches C = new CallWebServiceGetBranches();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_branch.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_city.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select Branch--")) {
					// showAlert();
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetIFSC C = new CallWebServiceGetIFSC();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	public void initAll() {
		txtIFSC_Code.setText("");
		txtMMID.setText("");
		txtMobile_No.setText("");
		txtAccNo.setText("");
		txtAccNoconf.setText("");
		txtName.setText("");
		txtBank.setText("");
		// txtBranch.setText("");
		txtEmail.setText("");
		txtNick_Name.setText("");
	}

	class CallWebServiceGetBanks extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		// String[] xmlTags = { "CUSTID", "IMEINO" };
	
		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescget_bnkbrn="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","34");   
				// valuesToEncrypt[0] = custId;
				// valuesToEncrypt[1] = MBSUtils.getImeiNumber(act);
			} catch (JSONException je) {
				je.printStackTrace();
			}
			//valuesToEncrypt[0] = jsonObj.toString();
		
		}

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

		protected void onPostExecute(Void paramVoid) {
			
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescget_bnkbrn = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescget_bnkbrn = "";
				}
				
			if(respdescget_bnkbrn.length()>0)
			{
				showAlert(respdescget_bnkbrn);
			}
			else{
			loadProBarObj.dismiss();
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_126));
			} else {
				post_successget_bnkbrn(retvalweb);
			}}
				
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebServiceGetBank
	
	public 	void post_successget_bnkbrn(String retvalweb)
	{
		respcode="";
		respdescget_bnkbrn="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select Bank--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("BANKNAME"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] bankNamesArr = new String[arrList.size()];
			bankNamesArr = arrList.toArray(bankNamesArr);
			ArrayAdapter<String> bankNames = new ArrayAdapter<String>(act,R.layout.spinner_item, bankNamesArr);
			/*CustomeSpinnerAdapter bankNames = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					bankNamesArr);*/
			bankNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_bank.setAdapter(bankNames);
		}
	
	}

	class CallWebServiceGetStates extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		// String[] xmlTags = { "CUSTID", "BANKNAME", "IMEINO" };
		
		JSONObject jsonObj = new JSONObject();
	

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetStates="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				// valuesToEncrypt[0] = custId;
				// valuesToEncrypt[1] = spi_bank.getSelectedItem().toString();
				// valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				
				jsonObj.put("METHODCODE","35");  
				
			} catch (JSONException je) {
				je.printStackTrace();
			}
			
		}

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

		protected void onPostExecute(Void paramVoid) {
			
			// Log.e("EDIT BENF", decryptedBeneficiaries);
			loadProBarObj.dismiss();
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescGetStates= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetStates= "";
				}
				
				
			if(respdescGetStates.length()>0)
			{
				showAlert(respdescGetStates);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_127));
			} else {
				post_successGetStates(retvalweb);
			}}
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebServiceGetStates

	public 	void post_successGetStates(String retvalweb)
	{
		respcode="";

		respdescGetStates="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select State--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("STATE"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] statesArr = new String[arrList.size()];
			statesArr = arrList.toArray(statesArr);
			ArrayAdapter<String> states = new ArrayAdapter<String>(act,R.layout.spinner_item, statesArr);
			/*CustomeSpinnerAdapter states = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					statesArr);*/
			states.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_state.setAdapter(states);
		}
	
	}
	class CallWebServiceGetDistricts extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		// String[] xmlTags = { "CUSTID", "BANKNAME", "STATE", "IMEINO" };
	
		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetDistricts="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
			
				jsonObj.put("METHODCODE","36");  
				// valuesToEncrypt[0] = custId;
				// valuesToEncrypt[1] = spi_bank.getSelectedItem().toString();
				// valuesToEncrypt[2] = spi_state.getSelectedItem().toString();
				// valuesToEncrypt[3] = MBSUtils.getImeiNumber(act);
			} catch (JSONException je) {
				je.printStackTrace();
			}
			//valuesToEncrypt[0] = jsonObj.toString();
			
		}

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

		protected void onPostExecute(Void paramVoid) {
			
			// String decryptedBeneficiaries = xml_data[0];

			loadProBarObj.dismiss();
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescGetDistricts= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetDistricts= "";
				}
				
			if(respdescGetDistricts.length()>0)
			{
				showAlert(respdescGetDistricts);
			}
			else{
			// Log.e("EDIT BENF", decryptedBeneficiaries);
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_128));
			} else {
				post_successGetDistricts(retvalweb);
			}}
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}// end onPostExecute

	}// end CallWebServiceGetDistricts
	public 	void post_successGetDistricts(String retvalweb)
	{
		respcode="";
		
		respdescGetDistricts="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select District--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("DISTRICT"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] districtArr = new String[arrList.size()];
			districtArr = arrList.toArray(districtArr);
			ArrayAdapter<String> districts = new ArrayAdapter<String>(act,R.layout.spinner_item, districtArr);
			/*CustomeSpinnerAdapter districts = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					districtArr);*/
			districts
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_district.setAdapter(districts);
		}
	
	}

	class CallWebServiceGetCities extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		// String[] xmlTags = { "CUSTID", "BANKNAME", "STATE", "DISTRICT",
		
		JSONObject jsonObj = new JSONObject();
	

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetCities="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				
				jsonObj.put("METHODCODE","37");  
				// valuesToEncrypt[0] = custId;
				// valuesToEncrypt[1] = spi_bank.getSelectedItem().toString();
				// valuesToEncrypt[2] = spi_state.getSelectedItem().toString();
				// valuesToEncrypt[3] =
				// spi_district.getSelectedItem().toString();
				// valuesToEncrypt[4] = MBSUtils.getImeiNumber(act);
			} catch (JSONException je) {
				je.printStackTrace();
			}
			//valuesToEncrypt[0] = jsonObj.toString();
	
		}

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

		protected void onPostExecute(Void paramVoid) {
		
			//String decryptedBeneficiaries = xml_data[0];

			loadProBarObj.dismiss();
			// Log.e("EDIT BENF", decryptedBeneficiaries);
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescGetCities= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetCities= "";
				}
				
			if(respdescGetCities.length()>0)
			{
				showAlert(respdescGetCities);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_129));
			} else {
				post_successGetCities(retvalweb);
			}}
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebServiceGetCities
	public 	void post_successGetCities(String retvalweb)
	{
		respcode="";
		respdescGetCities="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select City--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("CITY"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] cityArr = new String[arrList.size()];
			cityArr = arrList.toArray(cityArr);
			ArrayAdapter<String> cities = new ArrayAdapter<String>(act,R.layout.spinner_item, cityArr);
			/*CustomeSpinnerAdapter cities = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item, cityArr);*/
			cities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_city.setAdapter(cities);
		}
	
	}

	class CallWebServiceGetBranches extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		// String[] xmlTags = { "CUSTID", "BANKNAME", "STATE", "DISTRICT",
		// "CITY",
		
		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetBranches="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("CITY", spi_city.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","38"); 
				
				/*
				 * valuesToEncrypt[0] = custId; valuesToEncrypt[1] =
				 * spi_bank.getSelectedItem().toString(); valuesToEncrypt[2] =
				 * spi_state.getSelectedItem().toString(); valuesToEncrypt[3] =
				 * spi_district.getSelectedItem().toString(); valuesToEncrypt[4]
				 * = spi_city.getSelectedItem().toString(); valuesToEncrypt[5] =
				 * MBSUtils.getImeiNumber(act);
				 */
			} catch (JSONException je) {
				je.printStackTrace();
			}
			//valuesToEncrypt[0] = jsonObj.toString();
			
		}

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

		protected void onPostExecute(Void paramVoid) {
		
			
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescGetBranches= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetBranches= "";
				}
				
			if(respdescGetBranches.length()>0)
			{
				showAlert(respdescGetBranches);
			}
			else{
			loadProBarObj.dismiss();
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_130));
			} else {
				post_successGetBranches(retvalweb);
			}}
			
			
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebServiceGetBranches
	public 	void post_successGetBranches(String retvalweb)
	{
		respcode="";
	
		respdescGetBranches="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select Branch--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("BRANCH"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] branchArr = new String[arrList.size()];
			branchArr = arrList.toArray(branchArr);
			ArrayAdapter<String> branches = new ArrayAdapter<String>(act,R.layout.spinner_item, branchArr);
			/*CustomeSpinnerAdapter branches = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					branchArr);*/
			branches.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_branch.setAdapter(branches);
		}
	
	}

	class CallWebServiceGetIFSC extends AsyncTask<Void, Void, Void> {
		String retval = "", bankName = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		// String[] xmlTags = { "CUSTID", "BANKNAME", "STATE", "DISTRICT",
		// "CITY",
		
		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {

			try {
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				respcode="";
				retvalweb="";
				respdescGetIFSC="";
				System.out.println("ifsCD:" + ifsCD);
				bankName = spi_bank.getSelectedItem().toString();
				/*
				 * valuesToEncrypt[0] = custId; valuesToEncrypt[1] =
				 * spi_bank.getSelectedItem().toString(); valuesToEncrypt[2] =
				 * spi_state.getSelectedItem().toString(); valuesToEncrypt[3] =
				 * spi_district.getSelectedItem().toString(); valuesToEncrypt[4]
				 * = spi_city.getSelectedItem().toString(); valuesToEncrypt[5] =
				 * spi_branch.getSelectedItem().toString(); valuesToEncrypt[6] =
				 * MBSUtils.getImeiNumber(act);
				 */
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("CITY", spi_city.getSelectedItem().toString());
				jsonObj.put("BRANCH", spi_branch.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","39"); 
				
			} catch (JSONException je) {
				je.printStackTrace();
			}
			//valuesToEncrypt[0] = jsonObj.toString();
			
		}

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

		protected void onPostExecute(Void paramVoid) { // "IFSC"
			
			
			loadProBarObj.dismiss();
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescGetIFSC= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetIFSC= "";
				}
				
			if(respdescGetIFSC.length()>0)
			{
				showAlert(respdescGetIFSC);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_131));
			} else {
				post_successGetIFSC(retvalweb);
			}}
				
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}// end onPostExecute

	}// end CallWebServiceGetIFSC
	public 	void post_successGetIFSC(String retvalweb)
	{
		
		try {
			respcode="";
			
			respdescGetIFSC="";
			JSONObject jObj = new JSONObject(retvalweb);
			strIfsc = jObj.getString("IFSC");
			add_benf_layout.setVisibility(add_benf_layout.VISIBLE);
			get_ifsc_layout.setVisibility(get_ifsc_layout.INVISIBLE);
			txtIFSC_Code.setText(strIfsc);
			txtBank.setText(spi_bank.getSelectedItem().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}

	// Save benefiaciary
	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> {// CallWebService

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		/*
		 * String[] xmlTags = { "CUSTID", "ACCNO", "ACCNM", "MOBNO", "NICKNM",
		 * "MAILID", "TRANSFERTYPE", "IFSCCD", "MMID", "IINSERTUPDTDLT",
		 * "BENSRNO", "IMEINO" };
		 */
		
		JSONObject jsonObj = new JSONObject();
		

		@Override
		protected void onPreExecute() {

			try {
				respcode="";
				retvalweb="";
				respdescsave_beneficiary="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				Log.e("custId:", custId);
				Log.e("accNo:", accNo);
				Log.e("accNm:", accNm);
				Log.e("mobNo:", mobNo);
				Log.e("nickNm:", nickNm);
				Log.e("mailId", mailId);
				Log.e("sameBnk:", "same_bank");
				Log.e("ifscCd:", ifsCD);
				Log.e("mmid:", mmId);
				Log.e("InsrtUpdtDlt:", insrtUpdtDlt);
				Log.e("benSrno:", "00");

				jsonObj.put("CUSTID", custId);
				jsonObj.put("ACCNO", accNo);
				jsonObj.put("ACCNM", accNm);
				jsonObj.put("MOBNO", mobNo);
				jsonObj.put("NICKNM", nickNm);
				jsonObj.put("MAILID", mailId);
				jsonObj.put("TRANSFERTYPE", same_bank);
				jsonObj.put("IFSCCD", ifsCD);
				jsonObj.put("MMID", mmId);
				jsonObj.put("IINSERTUPDTDLT", insrtUpdtDlt);
				jsonObj.put("BENSRNO", "00");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", encrptdMpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","14"); 
			
				/*
				 * valuesToEncrypt[0] = custId.trim(); valuesToEncrypt[1] =
				 * accNo.trim(); valuesToEncrypt[2] = accNm.trim();
				 * valuesToEncrypt[3] = mobNo.trim(); valuesToEncrypt[4] =
				 * nickNm.trim(); valuesToEncrypt[5] = mailId.trim();
				 * valuesToEncrypt[6] = same_bank; valuesToEncrypt[7] = ifsCD;
				 * valuesToEncrypt[8] = mmId.trim(); valuesToEncrypt[9] =
				 * insrtUpdtDlt; valuesToEncrypt[10] = "00"; valuesToEncrypt[11]
				 * = MBSUtils.getImeiNumber(act);
				 */
			} catch (JSONException je) {
				je.printStackTrace();
			}
			//valuesToEncrypt[0] = jsonObj.toString();
			

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			System.out
					.println("============= inside doInBackground =================");
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

		protected void onPostExecute(final Void result) {


			loadProBarObj.dismiss();
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescsave_beneficiary= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescsave_beneficiary= "";
				}
				
			if(respdescsave_beneficiary.length()>0)
			{
				showAlert(respdescsave_beneficiary);
			}
			else{
			// retMess = "SUCCESS";
			if (retvalweb.indexOf("FAILED") > -1) {
				// SUCCESS/FAILED~DUPLICATEACCOUNT/DUPLICATENICKNAME
				// FAILED~DUPLICATEACCOUNT~
				if (retvalweb.indexOf("DUPLICATEACCOUNT") > -1) {

					//loadProBarObj.dismiss();
					// retMess="Duplicate Account Number. This Account Number Is Already Added.";
					// ///retMess =
					// "Failed To Add Other Bank Beneficiary Due To Duplicate Account Number.";
					retMess = getString(R.string.alert_019);
					showAlert(retMess);
				} else if (retvalweb.indexOf("DUPLICATENICKNAME") > -1) {

				//	loadProBarObj.dismiss();
					// retMess="Duplicate Nickname. Use Other Nickname.";
					// ////retMess="Failed To Add Other Bank Beneficiary Due To Duplicate Nickname.";
					retMess = getString(R.string.alert_020);
					showAlert(retMess);
				} else if (retvalweb.indexOf("INCORRECTIFSC") > -1) {
					//loadProBarObj.dismiss();
					// retMess="Duplicate Nickname. Use Other Nickname.";
					// ////retMess="Failed To Add Other Bank Beneficiary Due To Duplicate Nickname.";
					retMess = getString(R.string.alert_185);//alert_018);
					showAlert(retMess);
				} else if (retvalweb.indexOf("WRONGMPIN") > -1) {
				//	loadProBarObj.dismiss();
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
				} else {

					// /////retMess="Failed To Add Other Bank Beneficiary Due To Server Problem.";
					retMess = getString(R.string.alert_021);
					//loadProBarObj.dismiss();
					showAlert(retMess);
					//initAll();

				}
			} else {
				//loadProBarObj.dismiss();
				post_successsaveBeneficiaries(retvalweb);
			}
			}
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebService
	
	public 	void post_successsaveBeneficiaries(String retvalweb)
	{

		respcode="";
		respdescsave_beneficiary="";
		
		// ///////retMess="Other Bank Beneficiary Added Successfully.";
		flg="true";
		retMess = getString(R.string.alert_022);
		showAlert(retMess);
		initAll();
		onCreate(bdn);
	
	}

	public void onClick(View arg0) {
		System.out.println("=========== inside onClick ==========");
		System.out.println("VIew ID is:==========>" + arg0.getId());
		switch (arg0.getId()) {
		case R.id.btn_back:
			//System.out.println("Clicked on back");
			Intent in = new Intent(AddOtherBankBeneficiary.this,ManageBeneficiaryMenuActivity.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3); 
			act.startActivity(in);
			act.finish();
			
			break;

		case R.id.btn_home:
			
			break;
		
		case R.id.btn_fetchIFSC:
			spi_bank.setAdapter(null);
			spi_state.setAdapter(null);
			spi_district.setAdapter(null);
			spi_city.setAdapter(null);
			spi_branch.setAdapter(null);
			add_benf_layout.setVisibility(add_benf_layout.INVISIBLE);
			get_ifsc_layout.setVisibility(get_ifsc_layout.VISIBLE);
			//act.frgIndex = 651;
			flag = chkConnectivity();
			if (flag == 0) {
				CallWebServiceGetBanks C = new CallWebServiceGetBanks();
				C.execute();
			}
			break;
		case R.id.btn_submit2:
			String saveFlag = "";

			accNo = txtAccNo.getText().toString().trim();
			confaccNo=txtAccNoconf.getText().toString().trim();
			accNm = txtName.getText().toString().trim();
			mobNo = txtMobile_No.getText().toString().trim();
			nickNm = txtNick_Name.getText().toString().trim();
			mailId = txtEmail.getText().toString().trim();
			same_bank = "N";
			ifsCD = txtIFSC_Code.getText().toString().trim();
			mmId = txtMMID.getText().toString().trim();
			insrtUpdtDlt = "I";
			// bnCD = txtBank.getText().toString();
			// brCD = txtBranch.getText().toString();
			// Log.e("Onclick of submmit button","Mobile Number "+mobNo);
			ifsCD = ifsCD.toUpperCase();
			int niknm_len = nickNm.length();

			int ifsc_len = ifsCD.length();
			int mmid_len = mmId.length();
			/* New Logic */
			// boolean isAccComplete = (accNo.length() > 0 && ifsCD.length() >
			// 0) ? true
			// : false;
			// boolean isMMIDComplete = (mmId.length() > 0 && mobNo.length() >
			// 0) ? true
			boolean isAccComplete = ((accNo.length() > 0 && ifsCD.length() > 0) || (accNo
					.length() == 0 && ifsCD.length() == 0)) ? true : false;
			boolean isMMIDComplete = (((mmId.length() > 0 && mobNo.length() > 0) || (mmId
					.length() == 0 && mobNo.length() == 0))) ? true : false;
			// : false;
			// saveFlag = "ERR";
			if (isAccOrMMID().equalsIgnoreCase("FAIL")) {
				saveFlag = "ERR";
				retMess = getString(R.string.alert_151);
				showAlert(retMess);
			} else // if(isAccComplete || isMMIDComplete)//Either One is true so
					// valid
			{
				
				if (ifsc_len != 0 && ifsc_len != 11) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_166);
					showAlert(retMess);
					txtIFSC_Code.requestFocus();
				} 
				 else if (confaccNo.length() == 0) {
						saveFlag = "ERR";
						retMess = getString(R.string.alert_plz_confacc);
						showAlert(retMess);

						txtAccNoconf.requestFocus();
				}
				 else if (!confaccNo.equals(accNo)) {
					 saveFlag = "ERR";
						retMess = getString(R.string.alert_conaccfmatch);
						showAlert(retMess);
						txtAccNoconf.requestFocus();
					}
				 else if (accNm.length() == 0) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_132);
					showAlert(retMess);
					txtName.requestFocus();
				} else if (mmId.length() != 0 && mmid_len != 7) {
					saveFlag = "ERR";
					// ///////retMess = "Please Enter 7-digits MMID.";
					retMess = getString(R.string.alert_025);
					showAlert(retMess);
					txtMMID.requestFocus();
				} else if (mobNo.length() != 0 //&& //mobNo.length() != 10
						&& (!MBSUtils.validateMobNo(mobNo))) {
					Log.e("Onclick ", "====mob_num  " + mobNo);
					saveFlag = "ERR";
					retMess = getString(R.string.alert_validmob);
					showAlert(retMess);
					txtMobile_No.requestFocus();
					// }
				}

				else if (nickNm.trim().length() == 0) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_nicknm);
					showAlert(retMess);
					Log.e("Onclick", "====Nick name  "+ nickNm);
					txtNick_Name.requestFocus();
				} else if (niknm_len < 4 || niknm_len > 15) {
					retMess = getString(R.string.alert_nicknm_Len_valid);
					saveFlag = "ERR";
					showAlert(retMess);

					txtNick_Name.requestFocus();

				} else if (mailId.length() != 0
						&& !MBSUtils.validateEmail(mailId)) {
					// saveFlag = "OK";
					saveFlag = "ERR";
					retMess = getString(R.string.alert_valid_mail);
					showAlert(retMess);
					txtEmail.requestFocus();
				} else {
					// SaveBeneficiary();
					saveFlag = "OK";
				}
			}
		
			if (saveFlag.equalsIgnoreCase("OK")) {
				SaveBeneficiary();
			}
			break;
		case R.id.spinner_btn1:
			spi_bank.performClick();
			break;
		case R.id.spinner_btn2:
			spi_state.performClick();
			break;
		case R.id.spinner_btn3:
			spi_district.performClick();
			break;
		case R.id.spinner_btn4:
			spi_city.performClick();
			break;
		case R.id.spinner_btn5:
			spi_branch.performClick();
			break;

		default:
			break;
		}// end switch
	}// end onClick

	public String isAccOrMMID() {
		String chkaccNo = txtAccNo.getText().toString().trim();
		String chkifsCD = txtIFSC_Code.getText().toString().trim();
		String chkmmId = txtMMID.getText().toString().trim();
		String chkmobNo = txtMobile_No.getText().toString().trim();

		checkCnt = 0;
		if (chkaccNo.length() != 0) {
			checkCnt++;
		}
		if (chkifsCD.length() != 0) {
			checkCnt++;
		}
		if (chkmmId.length() != 0) {
			checkCnt++;
		}
		if (chkmobNo.length() != 0) {
			checkCnt++;
		}

		if (checkCnt > 2) {
			checkedValue = "SUCCESS";
		} else if (checkCnt == 2) {
			if ((chkaccNo.length() != 0) && (chkifsCD.length() != 0)) {
				checkedValue = "SUCCESS";
			} else if ((chkmmId.length() != 0) && (chkmobNo.length() != 0)) {
				checkedValue = "SUCCESS";
			} else {
				checkedValue = "FAIL";
			}
		} else {
			checkedValue = "FAIL";
		}

		return checkedValue;
	}

	public void SaveBeneficiary() {
		/*
		 * SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
		 * Context.MODE_PRIVATE); custId = sp.getString("custId", "custId");
		 * mobPin = sp.getString("pin", "pin"); userId = sp.getString("userId",
		 * "userId");
		 */

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				Log.e("CustId", "......" + custId);
				userId = c1.getString(3);
				Log.e("UserId", "......" + userId);
			}
		}
		InputDialogBox inputBox = new InputDialogBox(act);
		inputBox.show();
	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
				if((str.equalsIgnoreCase(respdescget_bnkbrn)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successget_bnkbrn(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescget_bnkbrn)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescsave_beneficiary)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successsaveBeneficiaries(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescsave_beneficiary)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescget_bnkbrn)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successget_bnkbrn(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescget_bnkbrn)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetStates)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetStates(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetStates)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetDistricts)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetDistricts(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetDistricts)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetCities)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetCities(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetCities)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetBranches)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetBranches(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetBranches)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetIFSC)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetIFSC(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetIFSC)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if (act.getString(R.string.alert_125).equalsIgnoreCase(
						textMessage)) {
					SaveBeneficiary();
				}
				/*if (flg == "true") 
				{
					Log.e("Inside If", "Inside if===" + flg);
					switch (v.getId()) {
					case R.id.btn_ok:
						// if (WSCalled) {
						Fragment fragment = new ManageBeneficiaryMenuActivity(
								act);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					}
					this.dismiss();
				}*/
			}
		};
		alert.show();
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		System.out
				.println("========================= end chkConnectivity ==================");
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("BalanceEnquiry	in chkConnectivity () state1 ---------"
							+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

					}
					break;
				case DISCONNECTED:
					flag = 1;
					// ////////retMess =
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
					// //////retMess = "Network Unavailable. Please Try Again.";
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
				// ////retMess = "Network Unavailable. Please Try Again.";
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

			flag = 1;
			// ///////retMess = "Network Unavailable. Please Try Again.";
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
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			// ///////retMess = "Network Unavailable. Please Try Again.";
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
		}
		System.out
				.println("========================= end chkConnectivity ==================");
		return flag;
	}// end chkConnectivity

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
				String str = mpin.getText().toString().trim();
				encrptdMpin = str;//ListEncryption.encryptData(custId + str);
				// String encrptdUpin = ListEncryption.encryptData(userId +
				// str);
				if (str.equalsIgnoreCase("")) {
					this.hide();
					retMess = getString(R.string.alert_mpin);
					showAlert(retMess);
					mpin.setText("");
				} else { // if (mobPin.equalsIgnoreCase(encrptdMpin)
							// || mobPin.equalsIgnoreCase(encrptdUpin)) {
					/*
					 * SharedPreferences sp =
					 * act.getSharedPreferences(MY_SESSION,
					 * Context.MODE_PRIVATE); custId = sp.getString("custId",
					 * "custId");
					 */

					flag = chkConnectivity();
					if (flag == 0) {
						callValidateTranpinService C =new  callValidateTranpinService();
						//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
						C.execute();
						//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
						//C.execute();
						this.hide();
					}
				} /*
				 * else { this.hide(); retMess = getString(R.string.alert_125);
				 * showAlert(retMess); mpin.setText("");
				 */
				// }
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
				jsonObj.put("CUSTID", custId);
	            jsonObj.put("ACCNO", accNo);
	            jsonObj.put("ACCNM", accNm);
	            jsonObj.put("MOBNO", mobNo);
	            jsonObj.put("NICKNM", nickNm);
	            jsonObj.put("MAILID", mailId);
	            jsonObj.put("TRANSFERTYPE", same_bank);
	            jsonObj.put("IFSCCD", ifsCD);
	            jsonObj.put("MMID", mmId);
	            jsonObj.put("IINSERTUPDTDLT", insrtUpdtDlt);
	            jsonObj.put("BENSRNO", "00");
	            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
	            jsonObj.put("MPIN", encrptdMpin);
	            jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				//jsonObj.put("METHODCODE","14"); 
				
	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle bundle=new Bundle();
			/*Fragment fragment = new BeneficiaryOtp(act);*/
			bundle.putString("CUSTID", custId);
			bundle.putString("FROMACT", "ADDOTHERBENF");
			bundle.putString("JSONOBJ", jsonObj.toString());
			Intent in = new Intent(act,BeneficiaryOtp.class);
			in.putExtra("var1", var1);
			in.putExtra("var3", var3);
			 in.putExtras(bundle);
			act.startActivity(in);
			act.finish();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} //end saveData
	public void onBackPressed() {
		/*Intent in = new Intent(act, ManageBeneficiaryMenuActivity.class);
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		startActivity(in);
		act.finish();*/
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
}// end AddOtherBankBeneficiary

