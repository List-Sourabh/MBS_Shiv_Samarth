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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ProgressBar;
import android.widget.TextView;

public class AddSameBankBeneficiary  extends Activity implements OnClickListener {

	AddSameBankBeneficiary act = this;
	ProgressBar p_wait;
	ImageView img_heading;
	Button btn_fetchName, btn_submit;
	EditText txtName, txtmobNo, txtEmail, txtNick_Name, txtAccNo,txtAccNoconf;
	ImageButton btn_home, btn_back;
	TextView txt_heading;
	DatabaseManagement dbms;
	int cnt = 0, flag = 0;
	String str = "", retMess = "", cust_name = "", tmpXMLString = "",decryptedAccName="",
			confaccNo="",retVal = "",respcode="",retval="",respdesc="",respdesc_save_beneficiary="";
	DialogBox dbs;
	private MyThread t1;
	int timeOutInSecs=300;
	String reTval="",validateAndGetAccountInforespdesc="",saveBeneficiariesrespdesc="";
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME1 = "";
	private static String METHOD_NAME2 = "";

	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "", 
			mailId = "",userId="";

	String flg="false",mobPin = "",when_fetch = "";
	Bundle bdn;
	public String encrptdMpin;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	public AddSameBankBeneficiary() {
	}
	
	@Override

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_samebank_beneficiary);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.add_beneficiary);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txtAccNo = (EditText) findViewById(R.id.txtAccNo2);
		txtAccNoconf=(EditText) findViewById(R.id.txtAccNoconf);
		btn_fetchName = (Button) findViewById(R.id.btn_fetchName2);
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
		txt_heading.setText(getString(R.string.lbl_add_benf));
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);
		btn_fetchName.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
	
		
        Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);
	        	userId=c1.getString(3);
		    	Log.e("userId","......"+userId);
	        }
        }
		
		txtAccNo.addTextChangedListener(new TextWatcher() 
		{ 

			public void afterTextChanged(Editable s) { 
				// TODO Auto-generated method stub
				txtName.setText("");
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
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	public void initAll() {
		txtAccNo.setText("");
		txtName.setText("");
		txtmobNo.setText("");
		txtEmail.setText("");
		txtNick_Name.setText("");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			//System.out.println("Clicked on back");
			Intent in = new Intent(act,ManageBeneficiaryMenuActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			   
			act.startActivity(in);
			act.finish();
			
			break;

		case R.id.btn_home:
			
			break;
		case R.id.btn_fetchName2:
			accNo = txtAccNo.getText().toString();
			when_fetch = "EXPLICIT";
			if (accNo.length() != 16) {
				// /////retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_valid_accno);
				showAlert(retMess);
				txtAccNo.requestFocus();
			} else {
				flag = chkConnectivity();
				if (flag == 0) 
				{
					CallWebService_fetch_ac_holdernm C = new CallWebService_fetch_ac_holdernm();
					C.execute();
				}
			}
			break;

		case R.id.btn_submit2:

			accNo = txtAccNo.getText().toString().trim();
			confaccNo=txtAccNoconf.getText().toString().trim();
			accNm = txtName.getText().toString().trim();
			mobNo = txtmobNo.getText().toString().trim();
			String nick = txtNick_Name.getText().toString().trim();
			 Log.e("SAM3333333333","nick=="+nick);
			 nickNm = nick.toUpperCase(); 
			 Log.e("SAM1111111111111","nickNm=="+nickNm);
			mailId = txtEmail.getText().toString().trim();
			
			int niknm_len = nickNm.length();
			// if(accNo.equalsIgnoreCase(""))
			if (accNo.length() != 16) {
				// /////retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_valid_accno);
				showAlert(retMess);
				txtAccNo.requestFocus();
			} else if (accNm.length() == 0) {
				retMess = getString(R.string.alert_plz_name);
				showAlert(retMess);
				txtName.requestFocus();
			} 
			else if (confaccNo.length() == 0) {
				retMess = getString(R.string.alert_plz_confacc);
				showAlert(retMess);
				txtAccNoconf.requestFocus();
			}
			else if (!confaccNo.equals(accNo)) {
				// /////retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_conaccfmatch);
				showAlert(retMess);
				txtAccNoconf.requestFocus();
			} else if (accNm.length() > 100) {
				retMess = getString(R.string.alert_leng_nm);
				showAlert(retMess);
				txtName.requestFocus();
			} 
			/*else if (mobNo.length() != 10) 
			{
				retMess = getString(R.string.alert_002);
				showAlert(retMess);
				txtmobNo.requestFocus();
			}*/ 
			
			else if (nickNm.length() == 0) {
				// ////retMess = "Please Enter Nickname.";
				retMess = getString(R.string.alert_nicknm);
				showAlert(retMess);
				txtNick_Name.requestFocus();
			} else if (nickNm.contains(" ") == true) {
				// ////retMess = "You Can Not Use Blank Spaces In Nickname.";
				retMess = getString(R.string.alert_spce_nicknm);
				showAlert(retMess);
				txtNick_Name.requestFocus();
			} else if (niknm_len < 4 || niknm_len > 15) {
				//Log.i("niknm_len violated","niknm_len violated.................");
				retMess = getString(R.string.alert_nicknm_Len_valid);
				showAlert(retMess);
				txtNick_Name.requestFocus();
			}
			else if (mobNo.length() > 0 && !MBSUtils.validateMobNo(mobNo)) {
				// retMess = "Please Enter Valid Mobile Number.";
				retMess = getString(R.string.alert_002);
				showAlert(retMess);
				txtmobNo.requestFocus();
			}
			/*else if (mobNo.length() > 0 || mobNo.length() != 10 && (!MBSUtils.validateMobNo(mobNo))) {
				// retMess = "Please Enter Valid Mobile Number.";
				retMess = getString(R.string.alert_006);
				showAlert(retMess);
				txtmobNo.requestFocus();
			}*/ else if (mailId.length() > 0 && !MBSUtils.validateEmail(mailId)) {

				// /////retMess = "Please Enter Valid E-mail Id.";
				retMess = getString(R.string.alert_valid_mail);
				showAlert(retMess);
				txtEmail.requestFocus();
			}

			else {
				//flag = chkConnectivity();

				//if (flag == 0) 
				{
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
					/*CallWebService_save_beneficiary c = new CallWebService_save_beneficiary();
					c.execute();*/
				}
				// SaveBeneficiary();
			}
			break;

		default:
			break;
		}
	}
	
	public void SaveBeneficiary() 
	{
		// flag = chkConnectivity();

		// if (flag == 0)
		{
			InputDialogBox inputBox = new InputDialogBox(act);
			inputBox.show();

		}

	}
		
	public void post_save_beneficiary(){
		respcode="-1";respdesc_save_beneficiary= "";
		retMess = getString(R.string.alert_succ_add_benef);

		showAlert(retMess);
		initAll();
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
				
				Log.e("mobPinmobPin22","mobPinmobPin11"+mobPin);
				Log.e("mobPinmobPin22","mobPinmobPin11"+mobPin);
				Log.e("mobPinmobPin22","mobPinmobPin11"+mobPin);
				//System.out
				//		.println("========= inside onClick ============***********");
				String str = mpin.getText().toString();
				//System.out.println("======== strmpin:==" + str);
				//System.out.println("======== mobPin:==" + mobPin);
				  encrptdMpin =str; //ListEncryption.encryptData(custId+str);
				Log.e("encrptdMpin","encrptdMpin"+encrptdMpin);
				Log.e("encrptdMpin","encrptdMpin"+encrptdMpin);
				Log.e("encrptdMpin","encrptdMpin"+encrptdMpin);
				String encrptdUserpin=str;//ListEncryption.encryptData(userId+str);
				Log.e("encrptdUserpin","encrptdUserpin"+encrptdUserpin);
				Log.e("encrptdUserpin","encrptdUserpin"+encrptdUserpin);
				Log.e("encrptdUserpin","encrptdUserpin"+encrptdUserpin);
				if (str.equalsIgnoreCase("")) {
					this.hide();
					// ///retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.enter_pass);
					showAlert(retMess);
					mpin.setText("");
				} else //if (encrptdMpin.equalsIgnoreCase(mobPin) ||encrptdUserpin.equalsIgnoreCase(mobPin) ) {
					{
					//flag=chkConnectivity();
					//if (flag == 0) 
					{
						//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
						//C.execute();
						callValidateTranpinService C=new callValidateTranpinService();
						C.execute();
						this.hide();
					}
				} /*else {
					///System.out
					//		.println("=========== inside else ==============");
					this.hide();
					// ///retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
					mpin.setText("");
				}*/
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox
		
	public void post_ac_holdernm()
	{
		respcode="";respdesc= "";
		
		Bundle b = new Bundle();
		
		if (decryptedAccName.equalsIgnoreCase("")) {
			decryptedAccName = "ACCNAMEISNULL";
		} else {
			decryptedAccName = decryptedAccName;
		}

		txtName.setText(decryptedAccName);
		String acno = txtAccNo.getText().toString();
		if (when_fetch == "AUTO") {
			dbs = new DialogBox(act);

			dbs.get_adb().setMessage(
					"Continue With Name \"" + decryptedAccName
							+ "\" For Account No." + acno + " ?");
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0,
								int arg1) {
							// TODO Auto-generated method stub

							SaveBeneficiary();
							// System.exit(0);
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0,
								int arg1) {
							// TODO Auto-generated method stub
							arg0.cancel();
						}
					});
			dbs.get_adb().show();
			// break;
		}
	
	}
		
	public int chkConnectivity() 
	{
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			if (state1) 
			{
				switch (state) 
				{
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
					}
					break;
				case DISCONNECTED:
					flag = 1;
					retMess = getString(R.string.alert_ne_disconnt);
					showAlert(retMess);
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
		} 
		catch (NullPointerException ne) 
		{
			Log.i("AddSameBankBeneficiary","NullPointerException Exception" + ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} 
		catch (Exception e) 
		{
			Log.i("AddSameBankBeneficiary", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	class CallWebService_fetch_ac_holdernm extends AsyncTask<Void, Void, Void> 
	{
		String accNo = "";

		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
	
		
		JSONObject jsonObj = new JSONObject();
		String generatedXML = "";

		@Override
		protected void onPreExecute() {
			try{
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
			//Log.i("mayuri", "accNo :" + accNo + "@@@@@@@@@@");
			//Log.i("mayuri", "custId :" + custId + "@@@@@@@@@@");
			jsonObj.put("ACCNO", accNo);
            jsonObj.put("CUSTID", custId);
            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
            jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
            jsonObj.put("METHODCODE","18"); 
			/*valuesToEncrypt[0] = accNo;
			valuesToEncrypt[1] = custId;
			valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);*/
			}
			 catch (JSONException je) {
	                je.printStackTrace();
	            }
			
			//System.out.println("generatedXML" + generatedXML);
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
		protected void onPostExecute(final Void result) 
		{
			Intent intent = null;
			
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
					reTval = jsonObj.getString("RETVAL");
				}
				else
				{
					reTval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					validateAndGetAccountInforespdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					validateAndGetAccountInforespdesc= "";
				}
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(validateAndGetAccountInforespdesc.length()>0)
			{
				showAlert(validateAndGetAccountInforespdesc);
			}
			else
			{
				try
				{
					if (reTval.indexOf("FAILED") > -1) 
					{
						if (reTval.indexOf("NOT_EXISTS") > -1) 
						{
							retMess = getString(R.string.alert_008);
							txtAccNo.requestFocus();
							showAlert(retMess);
						} 
						else if (reTval.indexOf("EXISTS") > -1) 
						{
							loadProBarObj.dismiss();
							retMess = getString(R.string.alert_009);
							txtAccNo.requestFocus();
							showAlert(retMess);
						} 
						else 
						{
							retMess = getString(R.string.alert_000);
							txtAccNo.requestFocus();
							showAlert(retMess);
						}
					} 
					else 
					{
						post_successGetAccountInfo(reTval);				
					}
				}
				catch(Exception je)
				{
					je.printStackTrace();
				}
				loadProBarObj.dismiss();
			}
		}
	}
	
	public 	void post_successGetAccountInfo(String reTval)
	{
		try
		{
			respcode="";
    	   	validateAndGetAccountInforespdesc="";
    	   	JSONObject retJson = new JSONObject(reTval);
    	   	String decryptedAccName=retJson.getString("ACCNAME");
    	   	Bundle b = new Bundle();
    	   	if (decryptedAccName.equalsIgnoreCase("")) 
    	   	{
    	   		decryptedAccName = "ACCNAMEISNULL";
    	   	} 
    	   	else 
    	   	{
    	   		decryptedAccName = decryptedAccName;
    	   	}

    	   	txtName.setText(decryptedAccName);
    	   	String acno = txtAccNo.getText().toString().trim();
    	   	if (when_fetch == "AUTO") 
    	   	{
    	   		dbs = new DialogBox(act);
    	   		dbs.get_adb().setMessage("Continue With Name \"" + decryptedAccName+ "\" For Account No." + acno + " ?");
    	   		dbs.get_adb().setPositiveButton("Yes",new DialogInterface.OnClickListener() 
    	   		{
    	   			@Override
					public void onClick(DialogInterface arg0,int arg1) 
    	   			{
    	   				SaveBeneficiary();
					}
				});
    	   		dbs.get_adb().setNegativeButton("No",new DialogInterface.OnClickListener() 
    	   		{
    	   			@Override
					public void onClick(DialogInterface arg0,int arg1) 
    	   			{
    	   				arg0.cancel();
					}
				});
    	   		dbs.get_adb().show();
    	   	}
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}
	}

	// Save Beneficiary
	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
	     JSONObject jsonObj = new JSONObject();
		

		@Override
		protected void onPreExecute() {
			try{
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			Log.e("nickNm.trim()","nickNm.trim()=="+nickNm.trim());
			// txtName.setEnabled(true);
			accNm = txtName.getText().toString().trim();
			  jsonObj.put("CUSTID", custId.trim());
              jsonObj.put("ACCNO", accNo.trim());
              jsonObj.put("ACCNM",accNm.trim());
              jsonObj.put("MOBNO", mobNo.trim());
              jsonObj.put("NICKNM", nickNm.trim());
              jsonObj.put("MAILID",mailId.trim());
              jsonObj.put("TRANSFERTYPE","Y");
              jsonObj.put("IFSCCD", "DUMMY");
              jsonObj.put("MMID","DUMMY");
              jsonObj.put("IINSERTUPDTDLT", "I");
              jsonObj.put("BENSRNO","00");
              jsonObj.put("IMEINO",MBSUtils.getImeiNumber(act));
              jsonObj.put("MPIN",encrptdMpin);
              jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
              jsonObj.put("METHODCODE","14"); 
			}
			catch (JSONException je) {
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
			try{
				Intent intent = null;
		      
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
						reTval = jsonObj.getString("RETVAL");
					}
					else
					{
						reTval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						saveBeneficiariesrespdesc= jsonObj.getString("RESPDESC");
					}
					else
					{	
						saveBeneficiariesrespdesc= "";
					}
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(saveBeneficiariesrespdesc.length()>0)
				{
					showAlert(saveBeneficiariesrespdesc);
				}
				else{


				if (reTval.indexOf("FAILED") > -1) 
				{
					if (reTval.indexOf("DUPLICATEACCOUNT") > -1) 
					{
						loadProBarObj.dismiss();
						retMess = getString(R.string.alert_fail_dupli_acc_no);
						showAlert(retMess);
					} 
					else if (reTval.indexOf("DUPLICATENICKNAME") > -1) 
					{
						loadProBarObj.dismiss();
						retMess = getString(R.string.alert_fail_benef_duplinick);
						showAlert(retMess);
					} 
					else if (reTval.indexOf("InvalidAccount") > -1) 
					{
						loadProBarObj.dismiss();
						retMess = getString(R.string.alert_invalid_acc);
						showAlert(retMess);
					}
					else if (reTval.indexOf("WRONGMPIN") > -1) 
					{
						loadProBarObj.dismiss();
						retMess = getString(R.string.alert_wrn_mpin);
						showAlert(retMess);
						//SaveBeneficiary();
					}
					else 
					{
						retMess = getString(R.string.alert_012);

						loadProBarObj.dismiss();
						showAlert(retMess);
						initAll();
						onCreate(bdn);
					}
				} 
				else 
				{
					loadProBarObj.dismiss();
					post_successsaveBeneficiaries(reTval);
				}
			}}
	              catch (Exception je) {
	            je.printStackTrace();
	        }
			
			
		}
	}
	
	public 	void post_successsaveBeneficiaries(String reTval)
	{
	 respcode="";
   	 saveBeneficiariesrespdesc="";
		flg = "true";
		// ///retMess="Same Bank Beneficiary Added Successfully.";
		retMess = getString(R.string.alert_succ_add_benef);

		showAlert(retMess);
		initAll();
	}

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
	   				
	   			
			  String decryptedAccounts =str.trim();
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
					//{"RETVAL":"FAILED~WRONGMPIN~0","RESPCODE":"1"}
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
	              jsonObj.put("ACCNM",accNm.trim());
	              jsonObj.put("MOBNO", mobNo.trim());
	              jsonObj.put("NICKNM", nickNm.trim());
	              jsonObj.put("MAILID",mailId.trim());
	              jsonObj.put("TRANSFERTYPE","Y");
	              jsonObj.put("IFSCCD", "DUMMY");
	              jsonObj.put("MMID","DUMMY");
	              jsonObj.put("IINSERTUPDTDLT", "I");
	              jsonObj.put("BENSRNO","00");
	              jsonObj.put("IMEINO",MBSUtils.getImeiNumber(act));
	              jsonObj.put("MPIN",encrptdMpin);
	              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle bundle=new Bundle();
		/*Fragment fragment = new BeneficiaryOtp(act);*/
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "ADDSAMBENF");
		bundle.putString("JSONOBJ", jsonObj.toString());
		Intent in = new Intent(act,BeneficiaryOtp.class);
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
		   in.putExtras(bundle);
		act.startActivity(in);
		act.finish();
		/*fragment.setArguments(bundle);
		FragmentManager fragmentManager = addSameBankBenf.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
	} catch (Exception e) {
		e.printStackTrace();
	}

}	
	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{
			@Override
			public void onClick(View v) 
			{
				super.onClick(v);
				if((str.equalsIgnoreCase(validateAndGetAccountInforespdesc)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetAccountInfo(reTval);
				}
				else if((str.equalsIgnoreCase(validateAndGetAccountInforespdesc)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(saveBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successsaveBeneficiaries(reTval);
				}
				else if((str.equalsIgnoreCase(saveBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if(str.equalsIgnoreCase(act.getString(R.string.alert_wrn_mpin)))
				{
					SaveBeneficiary();
				}
			}
		};
		alert.show();
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
