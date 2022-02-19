package list.shivsamarth_mbs;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Hashtable;

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


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Toast;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.common.HybridBinarizer;

public class QrcodeSendActivity extends Activity implements OnClickListener
{
	private static String METHOD_SAVE_TRANSFERTRAN = "";
	private static String METHOD_GET_TRANSFERCHARGE = "";

	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private String benInfo = "",errorCode="";
	public String QRCUSTID;
	public String QRDBTACCNO;
	public String QRAMT;
	public String QRCRACCNO;
	public String QRREMARK,RCUSTID;
	public static final int SELECT_PHOTO = 100;
	int scanOption=0;
	ImageButton btn_home, btn_back;
	DialogBox dbs;
	DatabaseManagement dbms;
	String respcode="",reTval="",getTransferChargesrespdesc="",saveTransferTranrespdesc="";
	Button btn_submit,btn_confirm,btn_con_back;
	TextView txt_heading,txt_remark,txt_from,txt_to,txt_amount,txt_charges;
	TextView cust_nm,txt_trantype;//txtTranId
	boolean status;
	double balance;
	//SharedPreferences.Editor e;
	int cnt = 0,flag = 0,frmno = 0,tono = 0;
	Intent in;
	ProgressBar pb_wait;
	Spinner spi_debit_account;
	
	String str = "",str2 = "",stringValue="",benSrno ="",strFromAccNo,strToAccNo, 
			strAmount,strRemark,benAccountNumber = "",drBrnCD = "",drSchmCD = "",
			drAcNo = "",mobPin = "",chrgCrAccNo="",tranPin="",retMess = "",
			custId = "",cust_name = "",acnt_inf,all_acnts,tranId="";
	EditText txtAmt,txtRemk,txtBalance;//txtAccNo
	//Activity act;
	View mainView;
	LinearLayout confirm_layout,same_bnk_layout;
	boolean noAccounts;
	public String encrptdTranMpin;
	private ImageButton spinenr_btn2;
	private ImageButton spinenr_btn;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private LayoutInflater inflater;
	QrcodeSendActivity act;
	public String debitAccno="",amt="",reMark="",accNo="",barcode="";
	CustomeSpinnerAdapter debAccs=null;
	int flg=0;
	Accounts acArray[];
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	ImageView img_heading;
	
	@SuppressLint("WrongConstant")
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendviaqrcode);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act = this;
		noAccounts=false;
		this.dbs = new DialogBox(act);
        dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
        var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
        Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		Log.e("retValStr","......"+stringValue);
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);
	        }
        }
		//stringValue="2#101#SB#25730#Mr. KADAM SUSHANT  D##0020001010025730#O#NA#10#Y~2#101#SB#25733#Mr. KADAM SUSHANT  D##0020001010025733#O#NA#544598#Y~2#101#SB#25768#Mr. KADAM SUSHANT  D##0020001010025768#O#NA#20#Y~2#101#SB#25791#Mr. KADAM SUSHANT  D##0020001010025791#O#NA#30#Y";
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		
		confirm_layout=(LinearLayout)findViewById(R.id.confirm_layout);
		same_bnk_layout=(LinearLayout)findViewById(R.id.same_bnk_layout);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.recharge);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		btn_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_qr_send));
		txt_trantype=(TextView)findViewById(R.id.txt_trantype);		
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_con_back= (Button) findViewById(R.id.btn_confirm_back);
	    txt_remark=(TextView)findViewById(R.id.txt_remark);
		txt_from=(TextView)findViewById(R.id.txt_from);
		txt_to=(TextView)findViewById(R.id.txt_to);
		txt_amount=(TextView)findViewById(R.id.txt_amount);
		//txt_amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
		txt_charges=(TextView)findViewById(R.id.txt_charges);
		//txtTranId=(TextView)findViewById(R.id.txt_tranid);
		btn_confirm.setOnClickListener(this);
		btn_con_back.setOnClickListener(this);
		spinenr_btn = (ImageButton) findViewById(R.id.spinner_btn);
		spinenr_btn.setOnClickListener(this);
		
		spi_debit_account = (Spinner) findViewById(R.id.sameBnkTranspi_debit_account);
txtBalance = (EditText) findViewById(R.id.sameBnkTrantxtBal);
		
		btn_submit = (Button) findViewById(R.id.sameBnkTranbtn_submit);
		//txtAccNo = (EditText) findViewById(R.id.sameBnkTrantxtAccNo);
		txtAmt = (EditText) findViewById(R.id.sameBnkTrantxtAmt);
		txtRemk = (EditText) findViewById(R.id.sameBnkTrantxtRemk);
		pb_wait = (ProgressBar) findViewById(R.id.sameBnkTranpro_bar);
		btn_submit.setOnClickListener(this);
		Log.e("QRSEND","initialized");
		Log.e("QRSEND","initialized");
		Log.e("QRSEND","initialized");
		Log.e("QRSEND","initialized");
		Log.e("QRSEND","initialized");
		Log.e("QRSEND","initialized");
		// logic to get debit a/c number from spi_debit_account according to
		// selected debit account
		if(flg==0)
		{	
		
	spi_debit_account
			.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

		
					str=spi_debit_account.getSelectedItem().toString();
					Log.e("arg2= ","arg2="+arg2);
					Log.e("arg2= ","arg2="+arg2);
					Log.e("arg2= ","arg2="+arg2);
					if (arg2 == 0)
					{
						txtBalance.setText("");
					}
						
					else if (arg2 != 0) {
					Log.e("str= ","str="+str);
					Log.e("str= ","str="+str);
					Log.e("str= ","str="+str);
                    if(str.equalsIgnoreCase("Select Debit Account"))
					{
                           txtBalance.setText("");
					}else
					{
				if (spi_debit_account.getCount() > 0) {
					
						Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition()-1];
						String balStr = selectedDrAccount.getBalace();
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
				}// end onItemSelected

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}// end onNothingSelected
			});// end spi_debit_account
			all_acnts = stringValue;

			addAccounts(all_acnts);
			this.pb_wait.setMax(10);
			this.pb_wait.setProgress(1);
			this.pb_wait.setVisibility(4);
			txtAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
		}
		else{
			new CallWebServiceGetSrvcCharg().execute();
		}
	   	t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
		
	}

	public void addAccounts(String str) {
		//System.out.println("SameBankTransfer IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			arrList.add("Select Debit Account");

			int noOfAccounts = allstr.length;
			//System.out.println("SameBankTransfer noOfAccounts:" + noOfAccounts);
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) {
				// System.out.println(i + "----STR1-----------" + str1[i]);
				// str2 = str1[i];
				//System.out.println(i + "----STR1-----------" + allstr[i]);
				str2 = allstr[i];
				String tempStr=str2;
				//System.out.println(i + "str2-----------" + str2);
				
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String withdrawalAllowed=str2.split("-")[10];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
			//	Log.e("add accounts","accType"+accType);
			//	Log.e("add accounts","oprcd"+oprcd);
				if (((accType.equals("SB")) ||(accType.equals("LO"))
						||(accType.equals("CA")))&& oprcd.equalsIgnoreCase("O")&& withdrawalAllowed.equalsIgnoreCase("Y"))
				{
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2);
				}
			}
			/*
			 * ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,
			 * R.layout.spinner_item, arrList);
			 * arrAdpt.setDropDownViewResource
			 * (android.R.layout.simple_spinner_dropdown_item);
			 * spi_debit_account.setAdapter(arrAdpt);
			 * Log.i("OtherBankTranIMPS ", "Exiting from adding accounts");
			 */
			if(arrList.size()==0)
			{
				noAccounts=true;
				showAlert(getString(R.string.alert_089));
				
			}
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			/*CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, debAccArr);*/
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);

	//Me..		acnt_inf = spi_debit_account.getItemAtPosition(
	//				spi_debit_account.getSelectedItemPosition()).toString();
			//Log.i("SameBankTransfer MAYURI....", acnt_inf);
		} catch (Exception e) {
			System.out.println("" + e);
			e.printStackTrace();

		}

	}// end addAccount

	public int chkConnectivity() {// chkConnectivity
		//System.out.println("============= inside chkConnectivity ================== ");
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//System.out.println("============= inside chkConnectivity 1 ================== ");
		NetworkInfo ni = cm.getActiveNetworkInfo();
		//System.out.println("============= inside chkConnectivity  2 ================== ");
		try {
			//System.out.println("============= inside chkConnectivity 3 ================== ");
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) 
				{
					case CONNECTED:
						if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) 
						{}
						break;
					case DISCONNECTED:
						flag = 1;
						retMess = getString(R.string.alert_014);
					//	showAlert(retMess);
						dbs = new DialogBox(act);
						dbs.get_adb().setMessage(retMess);
						dbs.get_adb().setPositiveButton("Ok",
								new DialogInterface.OnClickListener()  
								{
									public void onClick(DialogInterface arg0, int arg1) 
									{
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
				// setAlert();

				showAlert(retMess);

			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			// setAlert();

			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			// setAlert();

			showAlert(retMess);
		}
		//System.out.println("=========== Exit from chkConnectivity ================");
		return flag;
	}

	// webservice to save data
	class CallWebService2 extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject obj=new JSONObject();
		
		
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			String charges=txt_charges.getText().toString().split(" ")[1];
			
			Log.e("QRSEND","debitAccno==="+debitAccno.substring(0, 16));
		
			try {
				
				obj.put("BENFSRNO", "");
				obj.put("CRACCNO", accNo);
				obj.put("DRACCNO", debitAccno.substring(0, 16));
				obj.put("AMOUNT", amt);
				obj.put("REMARK", reMark);
				obj.put("TRANSFERTYPE", "QR");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("CUSTID", custId);
				obj.put("CHARGES", charges);
				obj.put("CHRGACCNO", chrgCrAccNo);
				obj.put("TRANID", tranId);
				obj.put("SERVCHRG", "0");
				obj.put("CESS", "0");
				obj.put("TRANPIN", encrptdTranMpin);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				String location=MBSUtils.getLocation(act);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("METHODCODE","16"); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
		
			//System.out.println("xml_data.len :" + xml_data.length);
			
		
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
					reTval = jsonObj.getString("RETVAL");
				}
				else
				{
					reTval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					saveTransferTranrespdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					saveTransferTranrespdesc= "";
				}
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(saveTransferTranrespdesc.length()>0)
			{
				showAlert(saveTransferTranrespdesc);
			}
			else{
			// retval = "SUCCESS";
			
			if (reTval.indexOf("SUCCESS") > -1) 
			{
				post_successsaveTransferTran(reTval);
	
			}
			else if(reTval.indexOf("DUPLICATE") > -1)
			{
				
				retMess = getString(R.string.alert_119)+tranId+"\n"+ getString(R.string.alert_120);
				showAlert(retMess);
				/*Intent in1=new Intent(act,FundTransferMenuActivity.class);
				startActivity(in1);
				act.finish();*/
			}
			else if(reTval.indexOf("FAILED#") > -1)
			{
				retMess = getString(R.string.alert_032);
				showAlert(retMess);//setAlert();

	        }
            else if (reTval.indexOf("WRONGTRANPIN") > -1) 
			{
				String msg[] = reTval.split("~");
				String first=msg[1];
				String second=msg[2];
				Log.e("first", "-------"+first);
				Log.e("second", "-------"+second);

				int count=Integer.parseInt(second);
				count= 5-count;
				//loadProBarObj.dismiss();
				retMess = getString(R.string.alert_125_1)+" "+count+" "+getString(R.string.alert_125_2);
				showAlert(retMess);
			}
			else if (reTval.indexOf("BLOCKEDFORDAY") > -1) 
			{
				//loadProBarObj.dismiss();
				retMess = getString(R.string.login_alert_005);
				showAlert(retMess);
			}
			else if(reTval.indexOf("FAILED") > -1)
			{
				if(reTval.split("~")[1]!="null" || reTval.split("~")[1]!="")
				{
					errorCode=reTval.split("~")[1];
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
					showAlert(retMess);//setAlert();
					/*Intent in1=new Intent(act,FundTransferMenuActivity.class);
					startActivity(in1);
					act.finish();*/
				}
			}// end else
			}
			
		}// end onPostExecute
	}// end callWbService2
	
	public 	void post_successsaveTransferTran(String reTval)
	{
		respcode="";
		saveTransferTranrespdesc="";
		retMess = getString(R.string.alert_FundTransfersuccess)+" "+getString(R.string.alert_reqId)+" "+tranId;
		showAlert(retMess);
		/*Intent in1=new Intent(this,DashboardActivity.class);
		startActivity(in1);
		act.finish();*/
	}
	
	@Override
	public void onClick(View v) 
	{ 
		if(v.getId() == R.id.btn_home)
		{
			Intent in=new Intent(act,DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
		}
		else if (v.getId() == R.id.btn_back) 
		{
			Intent in=new Intent(act,FundTransferMenuActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
		} 
		else if (v.getId() == R.id.spinner_btn) 
		{
			spi_debit_account.performClick();
		} 
		else if (v.getId() == R.id.sameBnkTranbtn_submit) 
		{
			//strFromAccNo=arrListTemp.get(spi_debit_account.getSelectedItemPosition());
			strFromAccNo = spi_debit_account.getSelectedItem().toString();
			//act.selectedItem=spi_debit_account.getSelectedItemPosition();
			strAmount = txtAmt.getText().toString().trim();
			strRemark = txtRemk.getText().toString().trim();
			String debitAcc = strFromAccNo.substring(0, 16);
			Log.e("dbtacc", "-----"+debitAcc);
			String balString=txtBalance.getText().toString().trim();
			//acArray[spi_debit_account.getSelectedItemPosition()-1].getBalace();
			if(balString.length()>0)
			{
				balString=balString.substring(0,balString.length()-2);
				Log.e("balance=","balString=="+balString);
				Log.e("balance=","balString=="+balString);
				balance=Double.parseDouble(balString);
				balance=Math.abs(balance);
				Log.e("balance=","balance=="+balance);
				Log.e("balance=","balance=="+balance);
			}
			
			if(debitAcc.indexOf("Select")> -1){
				showAlert(getString(R.string.alert_plzSelectDebitAcc));
			}                                        
			else if(strAmount.length()==0)
			{	
				showAlert(getString(R.string.alert_plzEnterAmount));
			}	
			else if(strAmount.length()==1 && strAmount.equalsIgnoreCase("."))
			{
				showAlert(getString(R.string.alert_plzEnterValidAmo));
			}
			
			else if(Double.parseDouble(strAmount)==0)
			{
				showAlert(getString(R.string.alert_plzAmoutGrtZero));
			}
			
			else if (Double.parseDouble(strAmount) > balance) {
				showAlert(getString(R.string.alert_InsufficentBalance));
			}
			else if(strRemark.length()==0)
			{
				showAlert(getString(R.string.alert_plzentrRemark));
			}
			else
			{
				ScanInputDialogBox inputBox = new ScanInputDialogBox(act);
				inputBox.show();
				/*act.QRAMT=strAmount;
				act.QRCUSTID=custId;
				act.QRDBTACCNO=strFromAccNo;
				act.QRREMARK=strRemark;
				new IntentIntegrator(act).initiateScan();*/
				
			}
		}
		else if (v.getId() == R.id.btn_confirm)
		{
			if (amt.length() == 0) 
			{
				amt = "0";
				retMess = getString(R.string.alert_plzEnterAmount);
				showAlert(retMess);//setAlert();
				txtAmt.requestFocus();
			} 
			else 
			{
				//int amt = Integer.parseInt(strAmount);
				if (Double.parseDouble(amt)==0) 
				{
					//System.out.println("--------------- 44 ------------");
					retMess = getString(R.string.alert_plzAmoutGrtZero);
					showAlert(retMess);//setAlert();
					txtAmt.requestFocus();
				} 
				else 
				{
					if (reMark.length() > 200) 
					{
						//System.out.println("--------------- 33 ------------");
						retMess = getString(R.string.alert_RemarkLength200);
						showAlert(retMess);//setAlert();
						txtRemk.requestFocus();
					} 
					else if (accNo.length() == 0) 
					{
						retMess = getString(R.string.alert_AccountEmpty);
						showAlert(retMess);//setAlert();
					} 
					else 
					{
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
		}
		else if (v.getId() == R.id.btn_confirm_back)
		{
			confirm_layout.setVisibility(confirm_layout.INVISIBLE);
			same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
		}
	}// end click

	public void onBackPressed() 
	{
	//	 super.onBackPressed();
		if((confirm_layout.getVisibility()==View.VISIBLE)||(same_bnk_layout.getVisibility()==View.VISIBLE))
		{

			/*Intent in1=new Intent(act,FunndTransferMenuActivity.class);
			startActivity(in1);
			act.finish();*/
		}
	}
	
	public void setAlert() 
	{
		System.out.println("======== in set alert ==========");
		showAlert(retMess);
	}// end setAlert

	public void saveData() {
		try {
			//System.out.println("--------------- 44 ------------");
			//this.flag = chkConnectivity();
			/*if (this.flag == 0) 
			{
				new CallWebService2().execute();
			}*/
			
			JSONObject obj=new JSONObject();
			try {
				String charges=txt_charges.getText().toString().split(" ")[1];
				obj.put("BENFSRNO", "");
				obj.put("CRACCNO", accNo);
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("REMARK", reMark);
				obj.put("TRANSFERTYPE", "INTBANK");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("CUSTID", custId);
				obj.put("CHARGES", charges);
				obj.put("CHRGACCNO", chrgCrAccNo);
				obj.put("TRANID", tranId);
				obj.put("SERVCHRG", "0");
				obj.put("CESS", "0");
				obj.put("TRANPIN", encrptdTranMpin);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Log.e("SAMEBANK","obj.toString()=="+obj.toString());
			Bundle bundle=new Bundle();
			//Fragment fragment = new TransferOTP(act);
			bundle.putString("CUSTID", custId);
			bundle.putString("FROMACT", "QRSEND");
			bundle.putString("JSONOBJ", obj.toString());
			Intent in = new Intent(QrcodeSendActivity.this,TransferOTP.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			   in.putExtras(bundle);
			act.startActivity(in);
			act.finish();
			/*fragment.setArguments(bundle);
			FragmentManager fragmentManager = this.getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in saveTransferTran is:" + e);
		}
	}// end saveData

	// innser class
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
		public void onClick(View v) 
		{
			try 
			{
				  String str=mpin.getText().toString().trim(); 
				   encrptdTranMpin=str;//ListEncryption.encryptData(custId+str);
				  if(str.length()==0) 
				  {
				  	retMess=getString(R.string.alert_enterTranMpin); 
				  	showAlert(retMess);//setAlert();
				  	this.show(); 
				  } 
				  /*else if(str.length()!=6)
				  {
					retMess=getString(R.string.alert_tranmpin);
					showAlert(retMess);//setAlert();
				  	this.show(); 
				  } */
				  else 
				  {
				  	//if(encrptdTranMpin.equals(tranPin)) 
				  	{ 
				  		//saveData(); 
				  		 callValidateTranpinService validateTran=new callValidateTranpinService();
							validateTran.execute();
				  		this.hide(); 
				 	} 
				  	/*else 
				  	{
				  		retMess=getString(R.string.alert_118); 
				  		showAlert(retMess);//setAlert();
				  		this.show(); 
				  	} */
				  }
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
				obj.put("TRANPIN", encrptdTranMpin);
				obj.put("CUSTID", custId);
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
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
			String str=CryptoClass.Function6(var5,var2);
			String decryptedAccounts = str.trim();
			
			loadProBarObj.dismiss();
			
			
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
	
	public class ScanInputDialogBox extends Dialog implements OnClickListener 
	{
		Activity activity;
		Button btnGalary,btnCamera;

		public ScanInputDialogBox(Activity activity) 
		{
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) 
		{
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.scan_option);
			
			btnGalary = (Button) findViewById(R.id.btnGalary);
			btnCamera = (Button) findViewById(R.id.btnCamera);
			btnGalary.setVisibility(Button.VISIBLE);
			btnGalary.setOnClickListener(this);
			btnCamera.setVisibility(Button.VISIBLE);
			btnCamera.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.btnCamera:
					act.QRAMT=strAmount;
					act.RCUSTID=custId;
					act.QRDBTACCNO=strFromAccNo;
					act.QRREMARK=strRemark;
					act.scanOption=1;
					custId=custId;
					debitAccno=strFromAccNo;
					amt=strAmount;
					reMark=strRemark;
					new IntentIntegrator(act).initiateScan();
					break;
				case R.id.btnGalary:
					custId=custId;
					debitAccno=strFromAccNo;
					amt=strAmount;
					reMark=strRemark;
					act.scanOption=2;
					Intent photoPic = new Intent(Intent.ACTION_PICK);
			        photoPic.setType("image/*");
			        startActivityForResult(photoPic, act.SELECT_PHOTO);
					/*new IntentIntegrator(act).initiateScan();*/
					break;
				default:
					break;
			}
			this.hide();
		}// end onClick
	}// end InputDialogBox

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
						
						if((str.equalsIgnoreCase(saveTransferTranrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successsaveTransferTran(reTval);
						}
						else if((str.equalsIgnoreCase(saveTransferTranrespdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(getTransferChargesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successGetSrvcCharg(reTval);
						}
						else if((str.equalsIgnoreCase(getTransferChargesrespdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
					else if(this.textMessage.equalsIgnoreCase(act.getString(R.string.alert_wrongtranpin)))
						{
							InputDialogBox inputBox = new InputDialogBox(act);
							inputBox.show();
					    }
					else if(str.equalsIgnoreCase(getString(R.string.alert_FundTransfersuccess)+" "+getString(R.string.alert_reqId)+" "+tranId))
					{
						Intent in1=new Intent(act,FundTransferMenuActivity.class);
						in1.putExtra("var1", var1);
						   in1.putExtra("var3", var3);
						startActivity(in1);
						act.finish();
					}
					else if(str.equalsIgnoreCase(getString(R.string.trnsfr_alert_001)))
					{
						Intent in1=new Intent(act,FundTransferMenuActivity.class);
						in.putExtra("var1", var1);
						   in.putExtra("var3", var3);
						startActivity(in1);
						act.finish();
					}
					else
					{
						if(noAccounts)
						{
							if(same_bnk_layout.getVisibility()==View.VISIBLE)
							{
								/*Fragment fragment = new FundTransferMenuActivity(act);
								//act.setTitle(getString(R.string.lbl_fund_transfer));
								FragmentManager fragmentManager = act.getFragmentManager();
								fragmentManager.beginTransaction()
										.replace(R.id.frame_container, fragment).commit();
								act.frgIndex=5;*/
							}
							else if(confirm_layout.getVisibility()==View.VISIBLE)
							{
								confirm_layout.setVisibility(confirm_layout.INVISIBLE);
								same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
								//act.frgIndex=51;
							}
						}
					 } break;			
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	public void clearFields() {
		//txtAccNo.setText("");
		spi_debit_account.setSelection(0);
		
		txtAmt.setText("");
		txtRemk.setText("");
	}
	
	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj;//
		JSONObject obj=new JSONObject();
		
		String benAcNo;
		protected void onPreExecute() 
		{
			loadProBarObj = new LoadProgressBar(act);
			Log.e("QRCodeSend","debitAccno=="+debitAccno);
			
			try {
				
				obj.put("CUSTID", custId);
				obj.put("TRANTYPE", "QR");
				obj.put("DRACCNO", debitAccno.substring(0, 16));
				obj.put("AMOUNT", amt);
				obj.put("CRACCNO", accNo);
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","28"); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			loadProBarObj.show();
		}

		protected Void doInBackground(Void... arg0) 
		{
			try
			{
				Thread.sleep(2000);
			}
			catch(Exception e)
			{e.printStackTrace();}
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
			
		      
				loadProBarObj.dismiss();
				 JSONObject jsonObj;
		   			try
		   			{
		   				String str=CryptoClass.Function6(var5,var2);
						Log.e("onPostExecuteqrsend---","str=="+str);
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
		   					reTval = jsonObj.getString("RETVAL");
		   				}
		   				else
		   				{
		   					reTval = "";
		   				}
		   				if (jsonObj.has("RESPDESC"))
		   				{
		   					getTransferChargesrespdesc= jsonObj.getString("RESPDESC");
		   				}
		   				else
		   				{	
		   					getTransferChargesrespdesc= "";
		   				}
		   			} catch (JSONException e) 
		   			{
		   				// TODO Auto-generated catch block
		   				e.printStackTrace();
		   			}
		   			
		   			if(getTransferChargesrespdesc.length()>0)
		   			{
		   				showAlert(getTransferChargesrespdesc);
		   			}
		   			else{
				if (reTval.indexOf("SUCCESS") > -1) 
				{
					post_successGetSrvcCharg(reTval);
					
				} 	
				else 
				{
					if (reTval.indexOf("LIMIT_EXCEEDS") > -1) 
					{
						retMess = getString(R.string.alert_031);
						//loadProBarObj.dismiss();
						showAlert(retMess);//setAlert();
					} 
					else if (reTval.indexOf("LOWBALANCE") > -1) {
						retMess = getString(R.string.alert_176);
						loadProBarObj.dismiss();
						showAlert(retMess);
					}                                    
					 else if (reTval.indexOf("SingleLimitExceeded") > -1) {
							retMess = getString(R.string.alert_signledaylmt);
							loadProBarObj.dismiss();
							showAlert(retMess);
						}
					
					 else if (reTval.indexOf("TotalLimitExceeded") > -1) {
							retMess = getString(R.string.alert_194);
							loadProBarObj.dismiss();
							showAlert(retMess);
						}
					else
					{
						retMess = getString(R.string.alert_032);
						//loadProBarObj.dismiss();
						showAlert(retMess);//setAlert();
					}
				}// end else
		   			}
		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg

	public 	void post_successGetSrvcCharg(String reTval)
	{
		respcode="";
		getTransferChargesrespdesc="";

		//loadProBarObj.dismiss();
		same_bnk_layout.setVisibility(same_bnk_layout.INVISIBLE);
		confirm_layout.setVisibility(confirm_layout.VISIBLE);

		String retStr=reTval.split("~")[1];
         String tranType=reTval.split("~")[2];
		Log.e("PostExecute==","tranType==="+tranType);
		String[] val=retStr.split("#");
		txt_heading.setText("Confirmation");
		txt_remark.setText(reMark.trim());
		txt_from.setText(debitAccno.trim());
		txt_to.setText(accNo.trim());
		txt_amount.setText("INR "+amt);
		txt_charges.setText("INR "+val[0]);
		txt_trantype.setText(tranType.trim());
		chrgCrAccNo=val[1];
		tranId=val[2];
		if(chrgCrAccNo.length()==0 || chrgCrAccNo.equalsIgnoreCase("null"))
			chrgCrAccNo="";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("DEMOHOME","11====="+requestCode);
		Log.e("DEMOHOME","22====="+resultCode);
		Log.e("DEMOHOME","33====="+data);
		if(requestCode==100)
		{
			if(data==null || data.equals(null))
        	{
				Toast.makeText(QrcodeSendActivity.this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        		/*Fragment fragment = new QrcodeSendActivity();
        		act.setTitle(getString(R.string.lbl_qr_send));
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				act.frgIndex=55;*/
        	}
			else
			{	
				InputStream imageStream = null;
	            try 
	            {
	            		
	    			Uri selectedImage = data.getData();
	                //getting the image
	    			Log.e("QRSEND","data.getData()111111====="+selectedImage);
	    			
	                imageStream = act.getContentResolver().openInputStream(selectedImage);
	            } 
	            catch (FileNotFoundException e) 
	            {
	                Toast.makeText(act, "File not found", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            }
	            catch (Exception e) 
	            {
	                Toast.makeText(act, "File not found", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            }
	            //decoding bitmap
	            Bitmap bMap = BitmapFactory.decodeStream(imageStream);
	            //Scan.setImageURI(selectedImage);// To display selected image in image view
	            int[] intArray = new int[bMap.getWidth() * (bMap.getHeight()-25)];
	            // copy pixel data from the Bitmap into the 'intArray' array
	            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),(bMap.getHeight()-25));
	
	            Log.e("DEMOHOME","img ht==="+bMap.getHeight());
	            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),(bMap.getHeight()-25), intArray);
	            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	
	            Reader reader = new MultiFormatReader();// use this otherwise
	            Log.e("DEMOHOME","reader===="+reader);
	            // ChecksumException
	            try 
	            {
	                Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
	               
	                //Log.e("DEMOHOME","barcode=111="+barcode);
	                decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	               // Log.e("DEMOHOME","barcode=222="+barcode);
	                decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
	                Log.e("DEMOHOME","decodeHints=333="+decodeHints);
	                Result result = reader.decode(bitmap, decodeHints);
	                Log.e("DEMOHOME","result=444="+result);
	 //*I have created a global string variable by the name of barcode to easily manipulate data across the application*//
	                barcode =  result.getText().toString().trim();
	                Log.e("DEMOHOME","barcode=555="+barcode);
	               
	                if(barcode!=null)
	                {
	                	
	                	if(validateAccNo(barcode))
	    				{
	                		  Log.e("DEMOHOME5555","barcode==++"+barcode);	
	             
	                		//DEMOHOME5555: barcode==++00200138000000027
	                		  Log.e("debitAccno","debitAccno==++"+debitAccno);
	                		
	                		//debitAccno==++0020013800000002 (Savings) 
	                		  
	                		  
	                		  //accNo="00200042010036521";
	                		  accNo= barcode.substring(0,barcode.length()-1);
	                		  Log.e("accNo===============","accNo==++"+accNo);
	                		  // accNo==++0020013800000002
	                		  debitAccno=strFromAccNo.substring(0,16);
	                		  Log.e("SAM==========","debitAccno=="+debitAccno);

	                		  Log.e("SAM==========","debitAccno=="+debitAccno+"=="+accNo);
	                		if(accNo.equals(debitAccno) )
	                		{
	                			showAlert(getString(R.string.alert_canNotSame));
	                		}
	                		else
	                		{
	                		flg=1;
	                		
	                		 CallWebServiceGetSrvcCharg c=new CallWebServiceGetSrvcCharg();
	                			c.execute();
	                		}
	    				}
	    				else
	    				{
	    					Toast.makeText(act, "Invalid Account Number", Toast.LENGTH_LONG).show();
	    					/*Fragment fragment = new FundTransferMenuActivity(act);
	    					FragmentManager fragmentManager = getFragmentManager();
	    					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
	    				}
	            		
	            		
	                }
	                else
	                {
	                	Log.e("QRSEND","ERROR");
	                }
	             //the end of do something with the button statement.
	
	            } catch (NotFoundException e) 
	            {
	            	Log.e("DEMOHOME","invalid image");
	                Toast.makeText(act, "Nothing Found", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            } catch (com.google.zxing.NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ChecksumException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	          
			}
		}
	}
	
	public boolean validateAccNo(String str)
	{
		String regex = "[0-9]+";
		if(str.matches(regex)) 
		{
			int sum=0,grandSum=0;
			String str2=str.substring(0,str.length()-1);
			
			for(int i=0;i<str2.length();i++)
			{
				sum=sum+Integer.parseInt(""+str2.charAt(i));
			}
			while (sum > 9 ) 
			{
				grandSum=0;
	            while (sum > 0) 
	            {
	            	int rem;
	            	rem = sum % 10;
	            	grandSum = grandSum + rem;
	            	sum = sum / 10;
	            }
	            sum = grandSum;
			}
			if(grandSum==Integer.parseInt(str.substring(str.length()-1,str.length())))
				return true;
			else
				return false;
		}
		else
			return false;
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
}// end class
