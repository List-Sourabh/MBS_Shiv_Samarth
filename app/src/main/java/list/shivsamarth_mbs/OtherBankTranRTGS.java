package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MyThread;

import mbLib.MBSUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



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
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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


public class OtherBankTranRTGS extends Activity implements OnClickListener 
{
	OtherBankTranRTGS otherBnkIfsc = null;
	OtherBankTranRTGS act;
	Button btn_submit,btn_confirm;
	Spinner spi_debit_account,spi_sel_beneficiery,spi_payment_option;
	ImageButton spinner_btn, spinner_btn2, btn_home, btn_back,spinner_btn3;
	TextView cust_nm,txt_heading,txt_remark,txt_from,txt_to,txt_amount,txt_charges,txtTranId,txt_trantype;
	EditText txtAccNo, txtAmt, txtRemk, txtBank, txtBranch, txtIfsc;
	DialogBox dbs;
	ProgressBar pb_wait;
	Editor e;
	String benf="";
	LinearLayout confirm_layout,other_bnk_layout;
	private String benInfo = "";
	private static String URL = "";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_validateTranMPINWS = "";
	private static final String MY_SESSION = "my_session";
	String retval = "",respcode="",respdesc_fetch_all_beneficiaries="",respdesc_SaveTransfer,respdesc_GetSrvcCharg="",respdesc_FetBnkBrn="",respdesc_GetMMID="";
	View mainView;
	DatabaseManagement dbms;
	public String encrptdTranMpin;
	String retStr = "", userId = "", cust_mob_no = "", transferType = "";
	String postingStatus = "", req_id = "",errorCode="",gst="";
	int frmno = 0, tono = 0, flag = 0, cnt = 0;
	String stringValue, str = "", retMess = "", cust_name = "", custId = "",
			str2 = "", ifsCD = "", benSrno = null, tranPin = "",nickname="";
	String mobPin = "", acnt_inf, all_acnts, bnCD, brCD, benAccountNumber = "",
			chrgCrAccNo = "", tranId = "", tranType = "", servChrg = "",
			cess = "",transaction="";
	String otherIfsctxtIFSCCode = "", drBrnCD = "", drSchmCD = "", drAcNo = "",
			strFromAccNo = "", strToAccNo = "", strAmount = "", strRemark = "";
	String onlyCharge = "";
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private boolean noAccounts;
	private EditText txtBalance;
	Accounts acArray[];
	String flg ="false";
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	private MyThread t1;
	int timeOutInSecs=300;
	ImageView img_heading;
	
	public void onBackPressed() 
	{
	
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_bank_tranf_rtgs);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act=this;
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		this.dbs = new DialogBox(this);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	     var3 = (String) getIntent().getSerializableExtra("var3");

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				stringValue = c1.getString(0);
				//Log.e("retValStr", "...." + stringValue);
				custId = c1.getString(2);
				//Log.e("custId", "......" + custId);
				userId = c1.getString(3);
				//Log.e("UserId", "......" + userId);
				cust_mob_no = c1.getString(4);
				//Log.e("cust_mobNO", "..." + cust_mob_no);
			}
		}
		//System.out.println("========== 6 ============");
		//System.out				.println("OtherBankTranRTGS	value of cust_name :" + cust_name);
		//System.out.println("========== 7 ============");
		spi_debit_account = (Spinner)findViewById(R.id.otherIfsc_spi_debit_account);
		spi_sel_beneficiery = (Spinner)findViewById(R.id.otherIfscspi_sel_beneficiery);
		spi_payment_option= (Spinner)findViewById(R.id.payment_options);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.transfer);
		btn_submit = (Button)findViewById(R.id.otherIfscbtn_submit);
		txtAccNo = (EditText)findViewById(R.id.otherIfsctxtAccNo);
		txtAmt = (EditText)findViewById(R.id.otherIfsctxtAmt);
		txtRemk = (EditText)findViewById(R.id.otherIfsctxtRemk);
		pb_wait = (ProgressBar)findViewById(R.id.otherIfscpro_bar);
		txtBank = (EditText)findViewById(R.id.otherIfsctxtBank);
		txtBranch = (EditText)findViewById(R.id.otherIfsctxtBranch);
		txtIfsc = (EditText)findViewById(R.id.otherIfsctxtIFSCCode);
		all_acnts = stringValue;
		btn_submit.setOnClickListener(this);
		txtAccNo.setText("");
		txtIfsc.setText("");
		btn_confirm = (Button)findViewById(R.id.btn_confirm);
	    txt_remark=(TextView)findViewById(R.id.txt_remark);
		txt_from=(TextView)findViewById(R.id.txt_from);
		txt_to=(TextView)findViewById(R.id.txt_to);
		txt_amount=(TextView)findViewById(R.id.txt_amount);
		txt_charges=(TextView)findViewById(R.id.txt_charges);
		//txtTranId=(TextView)rootView.findViewById(R.id.txt_tranid);
		txt_trantype=(TextView)findViewById(R.id.txt_trantype);
		btn_confirm.setOnClickListener(this);
		
		// btn_submit.setTypeface(tf_calibri);
		btn_home = (ImageButton)findViewById(R.id.btn_home);
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
		spinner_btn2 = (ImageButton) findViewById(R.id.spinner_btn2);
		spinner_btn3 = (ImageButton) findViewById(R.id.spinner_btn3);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_other_bank_fund_trans_rtgs));
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		confirm_layout=(LinearLayout)findViewById(R.id.othr_confirm_layout);
		other_bnk_layout=(LinearLayout)findViewById(R.id.other_bnk_ifsc_layout);
		btn_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		spinner_btn.setOnClickListener(this);
		spinner_btn2.setOnClickListener(this);
		spinner_btn3.setOnClickListener(this);
		String[] arrList = {"NEFT","RTGS"};
		ArrayAdapter<String> paymentOption = new ArrayAdapter<String>(OtherBankTranRTGS.this,R.layout.spinner_item, arrList);
		paymentOption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spi_payment_option.setAdapter(paymentOption);
		txtBalance = (EditText) findViewById(R.id.sameBnkTrantxtBal);
		
		addAccounts(all_acnts);
		
		flag = chkConnectivity();
		if (flag == 0)
		{			
			new CallWebService_fetch_all_beneficiaries().execute();			
		}
		
		pb_wait.setMax(10);		
		pb_wait.setProgress(1);		
		pb_wait.setVisibility(ProgressBar.INVISIBLE);

		spi_sel_beneficiery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					String otherIfsctxtIFSCCode = "";
					String otherIfsctxtBank = "";
					String otherIfsctxtBranch = "";

					// Toast.makeText(this, "str=111 ",
					// Toast.LENGTH_LONG).show();

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						//Log.e("spi_sel_beneficiery ", "arg2== " + arg2);
						//Log.e("spi_sel_beneficiery ", "arg3== " + arg3);
						String str = spi_sel_beneficiery.getItemAtPosition(
								spi_sel_beneficiery.getSelectedItemPosition())
								.toString();

						if (str.equalsIgnoreCase("Select Beneficiary")) {
							txtAccNo.setText("");
							txtAmt.setText("");
							txtRemk.setText("");
							txtIfsc.setText("");
							
						
						}
						
						//System.out.println("ben info :===========>" + benInfo);
						if (arg2 != 0) {
						String allStr[] = benInfo.split("~");

						for (int i = 1; i < allStr.length; i++) {
							String str1[] = allStr[i].split("#");
							 nickname=str1[2] + "(" + str1[1] + ")";

							//System.out.println("==== str :" + str);
							//System.out									.println("Beneficiary serial number:=====>"											+ str1[0]);
							//System.out.println("(" + str1[1] + ")");

								// if (str.indexOf("(" + str1[1] + ")") > -1)
								//if (str.indexOf(str1[2]) > -1) 
							 if(str.equalsIgnoreCase(nickname)){
								//System.out	.println("========== inside if ============");
								benSrno = str1[0];
								benAccountNumber = str1[3];
								otherIfsctxtIFSCCode = str1[4];
								ifsCD = otherIfsctxtIFSCCode;
								//Log.e("OTHERBANK","ifsCD====="+ifsCD);

								flag = chkConnectivity();
								if (flag == 0)
								{
									//new CallWebServiceFetBnkBrn().execute();
								}
								}
						}// end for

							//txtAccNo.setText(benAccountNumber.trim());
							//Log.e("LIST","benAccountNumber"+benAccountNumber);
							//Log.e("LISt","IFSC CODE=="+ifsCD);
							txtAccNo.setText(benAccountNumber);
						txtIfsc.setText(ifsCD.trim());
						}
					}// end onItemSelected

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		
		spi_debit_account
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {


						if (arg2 == 0)
						{
							txtBalance.setText("");
						}
						if(arg2 !=0)
						{
							if(!str.equalsIgnoreCase("Select Debit Account"))
							{
						
								if (spi_debit_account.getCount() > 0) 
								{
									String str = arrListTemp.get(spi_debit_account
									.getSelectedItemPosition()-1);

									String debitAc[] = str.split("-");
									//System.out.println("account 1:" + debitAc[0]);// 5
									//System.out.println("account 2:" + debitAc[1]);// 101
									// //System.out.println("account 3:"+debitAc[2]);//SB
									//System.out.println("account 4:" + debitAc[3]);// 7
									
									drBrnCD = debitAc[0];
									drSchmCD = debitAc[1];
									drAcNo = debitAc[3];
									Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition()-1];
							
									//Log.e("LIST","selectedDrAccount==="+selectedDrAccount);
									//Log.e("LIST","selectedDrAccount==="+selectedDrAccount);
							
									String balStr = selectedDrAccount.getBalace();
									//Log.e("LIST","balStr==="+balStr);
									//Log.e("LIST","balStr==="+balStr);
							
									String drOrCr = "";
									float amt = Float.parseFloat(balStr);
									if (amt > 0)
										drOrCr = " Cr";
									else if (amt < 0)
										drOrCr = " Dr";
									if (balStr.indexOf(".") == -1)
										balStr = balStr + ".00";
									balStr = balStr + drOrCr;
									txtBalance.setText(balStr);
								}
							}
						}
				}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});

		spi_debit_account.requestFocus();
		//System.out.println("========== 8 ============");
		txtAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
		
		t1 = new MyThread(timeOutInSecs,OtherBankTranRTGS.this,var1,var3);
		t1.start();
	}

	public void addAccounts(String str) {
		//System.out.println("OtherBankTranRTGS IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			arrList.add("Select Debit Account");
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				String tempStr=str2;
				
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String withdrawalAllowed=str2.split("-")[10];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				if ((accType.equals("SB") || accType.equals("CA") || accType
						.equals("LO")) && oprcd.equalsIgnoreCase("O") && withdrawalAllowed.equalsIgnoreCase("Y")) {
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2);
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			/*CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, debAccArr);*/
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(OtherBankTranRTGS.this,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
			acnt_inf = spi_debit_account.getItemAtPosition(
					spi_debit_account.getSelectedItemPosition()).toString();
			//Log.i("OtherBankTranRTGS MAYURI....", acnt_inf);
		} catch (Exception e) {
			//System.out.println("" + e);
			//Toast.makeText(act, ""+e, Toast.LENGTH_LONG).show();
		}

	}// end addAccount

	private void addBeneficiaries(String retval) {
		//System.out				.println("================ IN addBeneficiaries() of OtherBankTranRTGS ======================");
		//System.out.println("OtherBankTranRTGS IN addBeneficiaries()" + retval);
		//Log.e("retval==", "retval==" + retval);
		//Log.e("retval==", "retval==" + retval);
		//Log.e("retval==", "retval==" + retval);
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");

			int noOfben = allstr.length;
			//Log.e("OTHERBNK","noOfben=="+noOfben);
			//Log.e("OTHERBNK","noOfben=="+noOfben);
			//Log.e("OTHERBNK","noOfben=="+noOfben);
			String benName = "";
			arrList.add("Select Beneficiary");

			for (int i = 1; i < noOfben; i++) {
				//System.out.println(i + "----STR1-----------" + allstr[i - 1]);
				String[] str2 = allstr[i].split("#");

				//Log.e("OTHERBNK", "noOfben==" + noOfben);
				//Log.e("OTHERBNK", "noOfben==" + noOfben);
				//Log.e("OTHERBNK", "forth====" + str2[4]);
				//Log.e("OTHERBNK", "forth====" + str2[4]);
				//Log.e("OTHERBNK", "third====" + str2[3]);
				//Log.e("OTHERBNK", "third====" + str2[3]);
				// String benName = "";
				if (!(str2[4].equals("NA") || (str2[3].equals("-9999")))) {
					benName = str2[2] + "(" + str2[1] + ")";
					arrList.add(benName);
					//System.out.println("=============== benificiary Name is:======"							+ benName);
				}
			}
			
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, benfArr);*/
			ArrayAdapter<String> benfAccs = new ArrayAdapter<String>(OtherBankTranRTGS.this,R.layout.spinner_item, benfArr);
			benfAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(benfAccs);

		} catch (Exception e) {
			//System.out.println("" + e);
		}
	}// end addBeneficiaries

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out.println("OtherBankTranRTGS	in chkConnectivity () state1 ---------"							+ state1);
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

			//Log.i("OtherBankTranRTGS    mayuri",					"NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
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
			//Log.i("OtherBankTranRTGS   mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

			
		}
		return flag;
	}// end chkConnectivity

	class CallWebService_fetch_all_beneficiaries extends
			AsyncTask<Void, Void, Void> {
		
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = {"CUSTID","SAMEBNK","IMEINO"};

		

		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {
			try {

			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();

				jsonObj.put("CUSTID", custId);
				jsonObj.put("SAMEBNK", "N");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","13"); 
				
				
			} catch (JSONException je) {
				je.printStackTrace();
			}

			
			////System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			retval = "";respcode="";respdesc_fetch_all_beneficiaries="";respdesc_SaveTransfer="";respdesc_GetSrvcCharg="";respdesc_FetBnkBrn="";respdesc_GetMMID="";
			
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
			
			//System.out.println("xml_data.len :" + xml_data.length);
			//Log.e("benificiary====", "benificiary=====" + xml_data.length);
		
			//decryptedBeneficiaries="SUCCESS";
			JSONObject jsonObj;
			try
			{	String str=CryptoClass.Function6(var5,var2);
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdesc_fetch_all_beneficiaries= jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdesc_fetch_all_beneficiaries= "";
					}
					if(respdesc_fetch_all_beneficiaries.length()>0)
					{
						showAlert(respdesc_fetch_all_beneficiaries);
					}
					else{
					if (retval.indexOf("SUCCESS") > -1) {
						
						benf=retval;
						post_fetch_all_beneficiaries(retval);
					
					} else {
						loadProBarObj.dismiss();
						if (retval.indexOf("NODATA") > -1) {
							//Toast.makeText(act, getString(R.string.alert_041), Toast.LENGTH_LONG).show();
							retMess = getString(R.string.alert_041);
							  loadProBarObj.dismiss();
							  flg="true";
							 showAlert(retMess);
							
					
						} else {
							retMess = getString(R.string.alert_069);
							showAlert(retMess);
						}
					}
				}
								
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}// end onPostExecute

	}// end callWbService
		
	public void post_fetch_all_beneficiaries(String retval){
		respcode="";respdesc_fetch_all_beneficiaries= "";
		//System.out.println("retval:"				+ retval);
		//Log.e("post_fetch_all_beneficiaries", "sud====="+retval);
		benInfo = retval;
		addBeneficiaries(retval);
	}

	// for save TRAN
	class CallWebServiceSaveTransfer extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
	

		JSONObject jsonObj = new JSONObject();
	
		String amt;
		String accNo, debitAccno, benAcNo, amtStr, reMark;

		protected void onPreExecute() {
			try {
				// pb_wait.setVisibility(ProgressBar.VISIBLE);
				retval = "";respcode="";respdesc_fetch_all_beneficiaries="";respdesc_SaveTransfer="";respdesc_GetSrvcCharg="";respdesc_FetBnkBrn="";respdesc_GetMMID="";
				
				loadProBarObj.show();
				accNo = txtAccNo.getText().toString().trim();
				debitAccno = spi_debit_account.getItemAtPosition(
						spi_debit_account.getSelectedItemPosition()).toString();
				benAcNo = spi_sel_beneficiery.getItemAtPosition(
						spi_sel_beneficiery.getSelectedItemPosition())
						.toString();
				amt = txtAmt.getText().toString().trim();
				reMark = txt_remark.getText().toString().trim();
				amtStr = txtAmt.getText().toString().trim();

				tranType = spi_payment_option.getItemAtPosition(
						spi_payment_option.getSelectedItemPosition())
						.toString();

				if (tranType.equalsIgnoreCase("RTGS"))
					tranType = "RT";
				else if (tranType.equalsIgnoreCase("NEFT"))
					tranType = "NT";
				
				String crAccNo = txt_to.getText().toString().trim();
				String charges = txt_charges.getText().toString().split(" ")[1];
				String drAccNo = txt_from.getText().toString().trim();

				drAccNo = strFromAccNo.substring(0, 16);
				crAccNo = strToAccNo;
				
				
				jsonObj.put("BENFSRNO", benSrno);
				jsonObj.put("CRACCNO", crAccNo);
				jsonObj.put("DRACCNO", drAccNo);
				jsonObj.put("AMOUNT", amt);
				jsonObj.put("REMARK", reMark);
				jsonObj.put("TRANSFERTYPE", tranType);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("CUSTID", custId);
				jsonObj.put("CHARGES", onlyCharge);
				jsonObj.put("CHRGACCNO", chrgCrAccNo);
				jsonObj.put("TRANID", tranId);
				jsonObj.put("SERVCHRG", servChrg);
				jsonObj.put("CESS", cess);
				jsonObj.put("TRANPIN", encrptdTranMpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
				String location=MBSUtils.getLocation(act);
				jsonObj.put("LATITUDE", location.split("~")[0]);
				jsonObj.put("LONGITUDE", location.split("~")[1]);
				jsonObj.put("METHODCODE","16"); 
				
			
			} catch (JSONException je) {
				je.printStackTrace();
			}

		
			//System.out.println("&&&&&&&&&& generatedXML " + generatedXML);

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
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
			
		
			//System.out.println("xml_data :" + xml_data);
			//Log.e("TRANSFER",xml_data[0]);
			
			loadProBarObj.dismiss();
			
			JSONObject jsonObj;
			try
			{String str=CryptoClass.Function6(var5,var2);
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdesc_SaveTransfer= jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdesc_SaveTransfer= "";
					}
					if(respdesc_SaveTransfer.length()>0)
					{
						showAlert(respdesc_SaveTransfer);
					}
					
					if (retval.indexOf("SUCCESS") > -1) {
						post_SaveTransfer(retval);
					} 
					else {
						if (retval.indexOf("LIMIT_EXCEEDS") > -1) {
							// retMess="Problem in Fund Transfer,Your Transfer amount is exceeds Limite";
							retMess = getString(R.string.alert_031);
							loadProBarObj.dismiss();
							// showAlert(retMess);
							Intent in=new Intent(act, FundTransferMenuActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
							// loadProBarObj.dismiss();
						} else if (retval.indexOf("DUPLICATE") > -1) {

							retMess = getString(R.string.alert_119)+tranId+"\n"+ getString(R.string.alert_120);
							showAlert(retMess);
							Intent in=new Intent(act, FundTransferMenuActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
						} else if (retval.indexOf("WRONGTRANPIN") > -1) {
							String msg[] = retval.split("~");
							String first = msg[1];
							String second = msg[2];
							//Log.e("first", "-------" + first);
							//Log.e("second", "-------" + second);

							int count = Integer.parseInt(second);
							count = 5 - count;
							loadProBarObj.dismiss();
							retMess = getString(R.string.alert_125_1) + " " + count
									+ " " + getString(R.string.alert_125_2);
							showAlert(retMess);
						} else if (retval.indexOf("BLOCKEDFORDAY") > -1) {
							loadProBarObj.dismiss();
							retMess = getString(R.string.login_alert_005);
							showAlert(retMess);
						} else if (retval.indexOf("FAILED~") > -1) {
							//Log.e("in failed", "--------");
							String msg[] = retval.split("~");
							if (msg.length > 3) {
								// String wrongtran=msg[1];
								postingStatus = msg[1];
								req_id = msg[2];
								String errorMsg = msg[3];
								// if(msg[2]!=null || msg[2].length()>0)
								if (req_id.length() > 0) {

									if (req_id != null || req_id.length() > 0)
										retMess = getString(R.string.alert_162) + " "+ req_id;

								} else if (errorMsg.length() > 0) {
									retMess = getString(R.string.alert_032) + errorMsg;
								}

							} else {
								retMess = getString(R.string.alert_032);
							}
							showAlert(retMess);

							Intent in=new Intent(act, FundTransferMenuActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();

						}

						else if (retval.indexOf("FAILED") > -1)
						{
							if(retval.split("~")[1]!="null" || retval.split("~")[1]!="")
							{
								errorCode=retval.split("~")[1];
							}
							else
							{
								errorCode="NA";
							}
							if(errorCode.equalsIgnoreCase("999"))
							{
								retMess = getString(R.string.alert_179);
								showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("001"))
							{
								    retMess = getString(R.string.alert_180);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("002"))
							{
								    retMess = getString(R.string.alert_181);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("003"))
							{
								    retMess = getString(R.string.alert_182);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("004"))
							{
								retMess = getString(R.string.alert_179);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("005"))
							{
								    retMess = getString(R.string.alert_183);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("006"))
							{
								    retMess = getString(R.string.alert_184);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("007"))
							{
								retMess = getString(R.string.alert_179);
									showAlert(retMess);
							}
							else if(errorCode.equalsIgnoreCase("008"))
							{
								    retMess = getString(R.string.alert_176);
									showAlert(retMess);
							}
							else
							{
							retMess = getString(R.string.trnsfr_alert_001);
							showAlert(retMess);// setAlert();
							Intent in=new Intent(act, FundTransferMenuActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
							
							}
						}// end else
						else {
							// retMess="Fund Transfer Failed due to server problem ,Please try after some time";
							retMess = getString(R.string.alert_032);
							loadProBarObj.dismiss();
							showAlert(retMess);

							// loadProBarObj.dismiss();
							//System.out							.println("================== in onPostExecute 2 ============================");
						}
					}// end else
							
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}// end onPostExecute

	}// end callWbService2
	
	public void post_SaveTransfer(String retval){
		respcode="-1";respdesc_SaveTransfer= "";

		String msg[] = retval.split("~");
		if (msg.length > 2) {
			if (msg[2] != null || msg[2].length() > 0) {
			
			
				postingStatus=msg[1];
				req_id=msg[2];
				//Log.e("Ganesh "," Failed NA req_id="+req_id);
				//Log.e("Ganesh ","Failed NA req_id="+postingStatus);
				//retMess = getString(R.string.alert_150)+" "+req_id;
				retMess=getString(R.string.alert_192)+" "+getString(R.string.alert_121)+" "+req_id;
			
			}
		} else {
			retMess = getString(R.string.alert_192);
		}
		showAlert(retMess);

		Intent in=new Intent(act, FundTransferMenuActivity.class);
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
		startActivity(in);
		finish();

	
	}
	
	// to get values from getBnkBrn method.....
	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		

		JSONObject jsonObj = new JSONObject();
		
		String accNo, debitAccno, benAcNo, amt, reMark;

		protected void onPreExecute() {
			try {
				// pb_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				accNo = txtAccNo.getText().toString().trim();
				//Log.e("ohtertranImpsbtn_submit",						" spnr size= " + arrListTemp.size());
				//Log.e("ohtertranImpsbtn_submit", " spnr index "						+ spi_sel_beneficiery.getSelectedItemPosition());
				
				//..debitAccno = spi_debit_account.getSelectedItem().toString();
				//debitAccno = spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()-1).toString();
				debitAccno = spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
				debitAccno=debitAccno.substring(0, 16);
				
				//Log.e("debitAccno","debitAccno11111===="+debitAccno);
				//debitAccno=debitAccno.substring(0, 16);

				////Log.e("debitAccno","debitAccno222===="+debitAccno);
				
				benAcNo = spi_sel_beneficiery.getSelectedItem().toString();

				tranType = spi_payment_option.getItemAtPosition(
						spi_payment_option.getSelectedItemPosition())
						.toString();

				amt = txtAmt.getText().toString().trim();
				reMark = txtRemk.getText().toString().trim();
				
				debitAccno=debitAccno.substring(0,16);
				if (tranType.equalsIgnoreCase("RTGS"))
				{
					tranType = "RT";
				    transaction = "RTGS";
				}
				 else if (tranType.equalsIgnoreCase("NEFT"))
				 {
					 tranType = "NT";
					 transaction="NEFT";
				 }
					

				//Log.e("ohtertranImpsbtn_submit", " onPreExecute ");
				jsonObj.put("CUSTID", custId);
				jsonObj.put("TRANTYPE", tranType);
				jsonObj.put("DRACCNO", debitAccno);
				jsonObj.put("AMOUNT", amt);
				jsonObj.put("CRACCNO", accNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("BENFSRNO", benSrno);
				jsonObj.put("METHODCODE","28"); 
				
				Log.e("jsonObj","jsonObj===="+jsonObj);
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
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
			// String[] xmlTags = { "RETVAL" };
			// String[] xml_data = CryptoUtil.readXML(retval, xmlTags);
			loadProBarObj.dismiss();
			
			
			//Log.e("DEBUG@ANAND",decryptedAccounts );
			
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				Log.e("DSP","strRTGS====="+str);
				String decryptedAccounts =str.trim();
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdesc_GetSrvcCharg= jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdesc_GetSrvcCharg= "";
					}
					if(respdesc_GetSrvcCharg.length()>0)
					{
						showAlert(respdesc_GetSrvcCharg);
					}
					else{
					if (retval.indexOf("SUCCESS") > -1) {
						loadProBarObj.dismiss();
						post_GetSrvcCharg(retval);
					} else {
						if (retval.indexOf("TRANAMTLIMIT") > -1) {

							String errCd = decryptedAccounts.split("~")[2];
							if(errCd.equalsIgnoreCase("01"))
								retMess = getString(R.string.alert_148);
							else
								retMess = getString(R.string.alert_149);
							loadProBarObj.dismiss();
							showAlert(retMess);//setAlert();
						} 
						else if (retval.indexOf("LimitExceeded") > -1) {
							retMess = getString(R.string.alert_limit_excd);
							loadProBarObj.dismiss();
							showAlert(retMess);
						}
						 else if (retval.indexOf("LOWBALANCE") > -1) {
								retMess = getString(R.string.alert_176);
								loadProBarObj.dismiss();
								showAlert(retMess);
							}
						 else if (retval.indexOf("SingleLimitExceeded") > -1) {
								retMess = getString(R.string.alert_193);
								loadProBarObj.dismiss();
								showAlert(retMess);
							}
						
						 else if (retval.indexOf("TotalLimitExceeded") > -1) {
								retMess = getString(R.string.alert_194);
								loadProBarObj.dismiss();
								showAlert(retMess);
							}
						 else if (retval.indexOf("LIMIT_EXCEEDS") > -1) 
							{
									retMess = getString(R.string.alert_031);
									loadProBarObj.dismiss();
									showAlert(retMess);//setAlert();
							}
						else 
						{
							retMess = getString(R.string.alert_032);
							loadProBarObj.dismiss();
							showAlert(retMess);// setAlert();
						}
					}// end else
				}
								
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg
	
	public void post_GetSrvcCharg(String retval){

		respcode="";	respdesc_GetSrvcCharg= "";
		//act.frgIndex = 52;
		
		retStr = retval.split("~")[1];
		
		String retStr1 = "";
		retStr1 =retval.split("~")[2];
		if (retStr1.equalsIgnoreCase("nextDay"))
		{
			proceedTransaction();
		} else 
		{
			//Log.e("HELL",retStr);
			
			other_bnk_layout.setVisibility(other_bnk_layout.INVISIBLE);
			confirm_layout.setVisibility(confirm_layout.VISIBLE);
			String[] val=retStr.split("#");
			txt_heading.setText("Confirmation");
			txt_remark.setText(strRemark);
			txt_trantype.setText(transaction);
			txt_from.setText(strFromAccNo);
			txt_to.setText(strToAccNo);
			//txt_
			txt_amount.setText("INR "+strAmount);
			txt_charges.setText("INR "+val[0]);
			onlyCharge=val[0];
			chrgCrAccNo=val[1];
			tranId=val[2];
			servChrg=val[3];
			cess=val[4];
			//Log.e("OTHERBNKTRAN","servChrg==="+servChrg+"==cess=="+cess);
			if(chrgCrAccNo.length()==0 || chrgCrAccNo.equalsIgnoreCase("null"))
				chrgCrAccNo="";
			
			if(servChrg.equalsIgnoreCase("null"))
				servChrg="0";
			
			if(cess.equalsIgnoreCase("null"))
				cess="0";
			
			//Log.e("OTHERBNKTRAN","2222servChrg==="+servChrg+"==cess=="+cess);
			txt_charges.setText("INR "+(Float.parseFloat(val[0])+Float.parseFloat(servChrg)+Float.parseFloat(cess)));
		 
		}

	
	}

	public void proceedTransaction() { 
		DialogBox dbs = new DialogBox(act);
		dbs.get_adb().setMessage(getString(R.string.alert_150));
		dbs.get_adb().setPositiveButton("OK",
				new DialogInterface.OnClickListener() {       

					public void onClick(DialogInterface arg0, int arg1) {        
						Intent in=new Intent(act,DashboardActivity.class);
						in.putExtra("var1", var1);
						in.putExtra("var3", var3);
							startActivity(in);
						//arg0.cancel();
					}
				});
	
		dbs.get_adb().show();
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_back:
			Intent intet=new Intent(OtherBankTranRTGS.this,FundTransferMenuActivity.class);
			intet.putExtra("var1", var1);
			intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
			break;

		case R.id.btn_home:
			
			break;
		case R.id.spinner_btn:
			//Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn",					"DROP DOWN IMG BTN CLICKED....");
			spi_debit_account.performClick();
			break;

		case R.id.spinner_btn2:
			//Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn2",					"DROP DOWN IMG BTN CLICKED....");
			spi_sel_beneficiery.performClick();
			break;
		case R.id.spinner_btn3:
			//Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn3",					"DROP DOWN IMG BTN CLICKED....");
			spi_payment_option.performClick();
			break;
			
		case R.id.otherIfscbtn_submit:
			strFromAccNo=spi_debit_account.getSelectedItem().toString();
			strToAccNo = txtAccNo.getText().toString().trim();
			strAmount = txtAmt.getText().toString().trim().trim();
			strRemark = txtRemk.getText().toString().trim().trim();
			String transferType=spi_payment_option.getSelectedItem().toString();
			String balString = txtBalance.getText().toString().trim();//acArray[spi_debit_account.getSelectedItemPosition()-1].getBalace();
			//double balance = Double.parseDouble(balString);
			//balance = Math.abs(balance);
			double balance=0.0;//Double.parseDouble(balString);
			//balance=Math.abs(balance);
			if(balString.length()>0)
			{
				balString=balString.substring(0,balString.length()-2);
				//Log.e("balance=","balString=="+balString);
				//Log.e("balance=","balString=="+balString);
				balance=Double.parseDouble(balString);
				balance=Math.abs(balance);
				//Log.e("balance=","balance=="+balance);
				//Log.e("balance=","balance=="+balance);
			}
			
			String debitAcc=strFromAccNo.substring(0, 16);
			
			if(strFromAccNo.equalsIgnoreCase("Select Debit Account"))
			{
				showAlert(getString(R.string.alert_0981));
			}
			else if(strToAccNo.length()==0)
			{
				showAlert(getString(R.string.alert_098));
			}
			else if(strToAccNo.equalsIgnoreCase(strFromAccNo))
			{
				showAlert(getString(R.string.alert_107));
			}
			else if(strAmount.matches(""))
			{
				showAlert(getString(R.string.alert_033));
			}
			else if(strAmount.length()==1 && strAmount.equalsIgnoreCase("."))
			{
				showAlert(getString(R.string.alert_195));
			}
			else if(Double.parseDouble(strAmount)==0)
			{
				showAlert(getString(R.string.alert_034));
			}
			else if(strRemark.length()==0)
			{
				showAlert(getString(R.string.alert_165));
			}
			else if(strToAccNo.equalsIgnoreCase(debitAcc))//if(accNo.equals(debitAccno) )
			{
				showAlert(getString(R.string.alert_107));
			}
			else if(Double.parseDouble(strAmount)>balance)
			{
				showAlert(getString(R.string.alert_InsufficentBalance));
			}
			  else if(transferType.equalsIgnoreCase("NEFT") && Double.parseDouble(strAmount)>=200000) 
			  {
				  showAlert(getString(R.string.alert_147)); 
			  }
			  else if(transferType.equalsIgnoreCase("RTGS") &&  Double.parseDouble(strAmount)<200000)
			  {
				  showAlert(getString(R.string.alert_147_1)); 
			  }
			else
			{
				try 
				{
					this.flag = chkConnectivity();
					if (this.flag == 0) 
					{
						CallWebServiceGetSrvcCharg c=new CallWebServiceGetSrvcCharg();
						c.execute();
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					//System.out.println("Exception in CallWebServiceGetSrvcCharg is:" + e);
				}
			}
			break;
		case R.id.btn_confirm:
			String str = txtAmt.getText().toString().trim();
			String str2 = txtRemk.getText().toString().trim();
			String accNo = txtAccNo.getText().toString().trim();
			String bnk = txtBank.getText().toString().trim();
			String brnch = txtBranch.getText().toString().trim();
			String ifscCd = txtIfsc.getText().toString().trim();
			if (accNo.length() == 0 || ifscCd.length() == 0) {
				if (accNo.length() == 0)
					retMess = "Account number " + getString(R.string.alert_068);
				if (ifscCd.length() == 0)
					retMess = "IFSC code " + getString(R.string.alert_068);
				if(retMess!=null && retMess.length()>0)
					showAlert(retMess);
			} else if (str.length() == 0) {
				//System.out.println("Cuttent thread name:==>"						+ Thread.currentThread().getName());
				//System.out.println("--------------- 22 ------------");
				retMess = getString(R.string.alert_033);
				//System.out.println("--------------- 22.1 ------------");
				showAlert(retMess);
				//System.out.println("--------------- 22.2 ------------");
				txtAmt.requestFocus();
				//System.out.println("--------------- 22.3 ------------");
			} else {
			//int amt = Integer.parseInt(str); Gives error for fraction so
				double amt = Double.parseDouble(str);				
				if (amt <= 0) {
					//System.out.println("--------------- 44 ------------");
					retMess = getString(R.string.alert_034);
					showAlert(retMess);
					txtAmt.requestFocus();
				} else {
					/*if (str2.length() == 0) {
						//System.out.println("--------------- 33 ------------");
						retMess = getString(R.string.alert_035);
						showAlert(retMess);
						txtRemk.requestFocus();
					} else */
					
					{
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
			break;

		default:
			break;
		}

	}// end onClick

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {

			@Override
			public void onClick(View v) {
				switch (v.getId()) 
				{ 
				//retval = "",respcode="",="",,="",="",="";
					case R.id.btn_ok:
						////Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc_fetch_all_beneficiaries)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_fetch_all_beneficiaries(retval);
						}
						else if((str.equalsIgnoreCase(respdesc_fetch_all_beneficiaries)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(respdesc_SaveTransfer)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_SaveTransfer(retval);
						}
						else if((str.equalsIgnoreCase(respdesc_SaveTransfer)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(respdesc_GetSrvcCharg)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_GetSrvcCharg(retval);
						}
						else if((str.equalsIgnoreCase(respdesc_GetSrvcCharg)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(respdesc_FetBnkBrn)) &&  (respcode.equalsIgnoreCase("0")))
						{
							//post_FetBnkBrn();
						}
						else if((str.equalsIgnoreCase(respdesc_FetBnkBrn)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
							this.dismiss();
					  break;			
					default:
					  break;
				}
				if(flg=="true")
				{
					//Log.e("Inside If","Inside if==="+flg);
					switch (v.getId())
					{
	                	case R.id.btn_ok:
	                	//dismiss();
	                		Intent in=new Intent(act, FundTransferMenuActivity.class);
	    					in.putExtra("var1", var1);
	    					   in.putExtra("var3", var3);
	    					startActivity(in);
	    					finish();
	                	
	                	
	            }this.dismiss();
			}//End If
				
				
				switch (v.getId()) {
				case R.id.btn_ok:
					if (noAccounts) {
						if (other_bnk_layout.getVisibility() == View.VISIBLE) {
							Intent in=new Intent(act, FundTransferMenuActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
						} else if (confirm_layout.getVisibility() == View.VISIBLE) {
							confirm_layout
									.setVisibility(confirm_layout.INVISIBLE);
							other_bnk_layout
									.setVisibility(other_bnk_layout.VISIBLE);
							//act.frgIndex = 51;
						}
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
			setContentView(R.layout.transfer_dialog);
			mpin = (EditText) findViewById(R.id.txtMpin);
			btnOk = (Button) findViewById(R.id.btnOK);
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			try {

				// //System.out.println("========= inside onClick ============***********");
				String str = mpin.getText().toString();
				encrptdTranMpin =str; //ListEncryption.encryptData(custId + str);
				if (str.length() == 0) {
					retMess = getString(R.string.alert_116);
					showAlert(retMess);// setAlert();
					this.show();
				} /*else if (str.length() != 6) {
					retMess = getString(R.string.alert_037);
					showAlert(retMess);// setAlert();
					this.show();
				} */
				else {

					{
						//saveData();
						callValidateTranpinService validateTran=new callValidateTranpinService();
						validateTran.execute();
						this.hide();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				//System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
			}
		}// end onClick
	}// end InputDialogBox

	public void saveData() 
	{
		String accNo = txtAccNo.getText().toString().trim();
		String debitAccno = spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
		String benAcNo = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();
		String amt = txtAmt.getText().toString().trim();
		String reMark = txt_remark.getText().toString().trim();
		String amtStr = txtAmt.getText().toString().trim();
		tranType = spi_payment_option.getItemAtPosition(spi_payment_option.getSelectedItemPosition()).toString();

		debitAccno=debitAccno.substring(0, 16);
		if (tranType.equalsIgnoreCase("RTGS"))
			tranType = "RT";
		else if (tranType.equalsIgnoreCase("NEFT"))
			tranType = "NT";
		
		String crAccNo = txt_to.getText().toString().trim();
		String charges = txt_charges.getText().toString().split(" ")[1];
		String drAccNo = txt_from.getText().toString().trim();

		drAccNo = strFromAccNo.substring(0, 16);
		crAccNo = strToAccNo;
		JSONObject jsonObj=new JSONObject();
		try
		{
			jsonObj.put("BENFSRNO", benSrno);
			jsonObj.put("CRACCNO", crAccNo);
			jsonObj.put("DRACCNO", drAccNo);
			jsonObj.put("AMOUNT", amt);
			jsonObj.put("REMARK", reMark);
			jsonObj.put("TRANSFERTYPE", tranType);
			jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
			jsonObj.put("CUSTID", custId);
			jsonObj.put("CHARGES", charges);
			jsonObj.put("CHRGACCNO", chrgCrAccNo);
			jsonObj.put("TRANID", tranId);
			jsonObj.put("SERVCHRG", servChrg);
			jsonObj.put("CESS", cess);
			jsonObj.put("TRANPIN", encrptdTranMpin);
			jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
		}
		catch (JSONException je) 
		{
			je.printStackTrace();
		}
		/*Bundle bundle=new Bundle();
		Fragment fragment = new TransferOTP(act);
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "RTNTBANK");
		bundle.putString("JSONOBJ", jsonObj.toString());
		fragment.setArguments(bundle);
		FragmentManager fragmentManager = this.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
		
		Bundle bundle=new Bundle();
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "RTNTBANK");
		bundle.putString("JSONOBJ", jsonObj.toString());
		Intent in = new Intent(act,TransferOTP.class);
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		in.putExtras(bundle);
		startActivity(in);
		finish();
	}// end saveData
	
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
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("TRANPIN", encrptdTranMpin);
				obj.put("CUSTID", custId);
				obj.put("METHODCODE","73"); 
				
				
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
		
			
			//Log.e("SAM===","xml_data[0]=decryptedAccounts:"+decryptedAccounts);
			loadProBarObj.dismiss();
			String str=CryptoClass.Function6(var5,var2);
			String decryptedAccounts = str.trim();
			
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
				else if (decryptedAccounts.indexOf("WRONGTRANPIN") > -1) 
				{
					JSONObject obj=null;
					try {
						obj = new JSONObject(decryptedAccounts);
						String msg[] = obj.getString("RETVAL").split("~");
						String first = msg[1];
						String second = msg[2];
						//Log.e("OMKAR", "---"+second+"----");
						int count = Integer.parseInt(second);
						count = 5 - count;
						loadProBarObj.dismiss();
						retMess = act.getString(R.string.alert_125_1) + " " + count + " "
								+ act.getString(R.string.alert_125_2);
						showAlert(retMess);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				else if (decryptedAccounts.indexOf("FAILED~") > -1) 
				{
					JSONObject obj=null;
					try {
						obj = new JSONObject(decryptedAccounts);
						String msg[] = obj.getString("RETVAL").split("~");
						String first = msg[1];
						if(first.equalsIgnoreCase("9"))
						{
							retMess = getString(R.string.login_alert_005);
							showAlert(retMess);// setAlert();
						}
						else
						{	
							retMess = getString(R.string.alert_032);
							showAlert(retMess);// setAlert();
						}	
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			
		}// end onPostExecute
	}// end callValidateTranpinService
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		t1.sec = timeOutInSecs;
		Log.e("sec11= ","sec11=="+t1.sec);
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		t1.sec=-1;
		System.gc();
	}
}// end OtherBankTranIFSC
