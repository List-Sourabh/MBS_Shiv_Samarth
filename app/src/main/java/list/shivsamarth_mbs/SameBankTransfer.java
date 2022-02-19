package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

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

import android.annotation.SuppressLint;
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
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SameBankTransfer extends Activity implements OnClickListener 
{
	private static String METHOD_NAME = "";
	private static String METHOD_SAVE_TRANSFERTRAN = "";
	private static String METHOD_GET_TRANSFERCHARGE = "";

	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private String benInfo = "";
	ImageButton btn_home, btn_back;
	ImageView img_heading;
	DialogBox dbs;
	String pp="";
	DatabaseManagement dbms;
	Button btn_submit,btn_confirm,btn_con_back;
	TextView txt_heading,txt_remark,txt_from,txt_to,txt_amount,txt_charges;
	int cnt = 0;
	TextView cust_nm,txtTranId,txt_trantype;
	boolean status;
	//SharedPreferences.Editor e;
	int flag = 0,frmno = 0,tono = 0;
	Intent in;
	ProgressBar pb_wait;
	Spinner spi_debit_account,spi_sel_beneficiery;
	//StopPayment stp = null;
	String str = "",str2 = "",stringValue="",benSrno ="",strFromAccNo,strToAccNo,
			strAmount,strRemark,benAccountNumber = "",drBrnCD = "",drSchmCD = "",
			drAcNo = "",mobPin = "",chrgCrAccNo="",tranPin="",retMess = "",
			custId = "",cust_name = "",acnt_inf,all_acnts,tranId="";
	EditText txtAccNo,txtAmt,txtRemk, txtBalance;
	SameBankTransfer act;
	
	View mainView;
	LinearLayout confirm_layout,same_bnk_layout;
	boolean noAccounts;
	private ImageButton spinenr_btn2;
	private ImageButton spinenr_btn;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private LayoutInflater inflater;
	SameBankTransfer sameBnkTran;
	private String userId,errorCode="";
	public String encrptdTranMpin;
	public String encrptdUTranMpin;
	Accounts acArray[];
	String reTval="",getBeneficiariesrespdesc="",saveTransferTranrespdesc="",getTransferChargesrespdesc="";
	String wsFlag ="false",retval = "",respcode="",respdesc_fetch_all_beneficiaries="",respdesc_web2="",respdesc_GetSrvcCharg="";
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@SuppressLint("WrongConstant")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.same_bank_transfer);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.transfer);
		act=this;
		noAccounts=false;
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		this.dbs = new DialogBox(this);
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
	        	userId=c1.getString(3);
		    	Log.e("userId","......"+userId);
	        }
        }
        
        btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		
		confirm_layout=(LinearLayout)findViewById(R.id.confirm_layout);
		same_bnk_layout=(LinearLayout)findViewById(R.id.same_bnk_layout);
		
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		btn_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_same_bnk_trans));
		txt_trantype=(TextView)findViewById(R.id.txt_trantype);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
	    txt_remark=(TextView)findViewById(R.id.txt_remark);
		txt_from=(TextView)findViewById(R.id.txt_from);
		txt_to=(TextView)findViewById(R.id.txt_to);
		txt_amount=(TextView)findViewById(R.id.txt_amount);
		txt_charges=(TextView)findViewById(R.id.txt_charges);
		//txtTranId=(TextView)findViewById(R.id.txt_tranid);
		
		btn_confirm.setOnClickListener(this);
		spinenr_btn2 = (ImageButton) findViewById(R.id.spinner_btn2);
		spinenr_btn2.setOnClickListener(this);
		spinenr_btn = (ImageButton) findViewById(R.id.spinner_btn);
		spinenr_btn.setOnClickListener(this);
		
		spi_debit_account = (Spinner)findViewById(R.id.sameBnkTranspi_debit_account);

		if (spi_debit_account == null)
			System.out.println("spi_debit_account is null");
		else {
			System.out.println("spi_debit_account is not null");
			spi_debit_account.requestFocus();
		}

		spi_sel_beneficiery = (Spinner)findViewById(R.id.sameBnkTranspi_sel_beneficiery);
		if (spi_sel_beneficiery == null)
			System.out.println("spi_sel_beneficiery is null");
		else
			System.out.println("spi_sel_beneficiery is not null");

		btn_submit = (Button)findViewById(R.id.sameBnkTranbtn_submit);
		if (btn_submit == null)
			System.out.println("btn_submit is null");
		else
			System.out.println("btn_submit is not null");

		txtAccNo = (EditText)findViewById(R.id.sameBnkTrantxtAccNo);
		if (txtAccNo == null)
			System.out.println("txtAccNo is null");
		else
			System.out.println("txtAccNo is not null");
		txtBalance = (EditText) findViewById(R.id.sameBnkTrantxtBal);
		if (txtBalance == null)
			System.out.println("txtBalance is null");
		else
			System.out.println("txtBalance is not null");
		txtAmt = (EditText)findViewById(R.id.sameBnkTrantxtAmt);
		if (txtAmt == null)
			System.out.println("txtAmt is null");
		else
			System.out.println("txtAmt is not null");

		txtRemk = (EditText)findViewById(R.id.sameBnkTrantxtRemk);
		if (txtRemk == null)
			System.out.println("txtRemk is null");
		else
			System.out.println("txtRemk is not null");

		pb_wait = (ProgressBar) findViewById(R.id.sameBnkTranpro_bar);
		if (pb_wait == null)
			System.out.println("pb_wait is null");
		else
			System.out.println("pb_wait is not null");

		btn_submit.setOnClickListener(this);
		
		// btn_submit.setTypeface(tf_calibri);
		// logic that set text box value according to spinner
		spi_sel_beneficiery.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			String benAccountNumber = "";

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				String nickname="";
				String str = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();
				if (str.indexOf("Select Beneficiary") > -1) 
				{
					txtAccNo.setText("");
				}
				if (arg2 != 0) {
					String allStr[] = benInfo.split("~");
					
					for (int i = 1; i <= allStr.length; i++) {
						
						Log.e("allStr[i - 1]==","allStr[i - 1]==="+allStr[i - 1]);
						
						String str1[] = allStr[i - 1].split("#");
						
						Log.e("Str=="," Str== "+str);
						Log.e("str1[1]=","str1[1]== "+str1[1]);
						Log.e("str1[2]=","str1[2]== "+str1[2]);
						
						nickname=str1[2] + "(" + str1[1] + ")";
						
						Log.e("nickname11=","nickname11== "+nickname);
						
						
						if (str.equalsIgnoreCase(nickname)) {
							nickname=str1[2];
							benAccountNumber = str1[3];
							benSrno = str1[0];
						}

					}// end for
					
					Log.e("accno ","account== "+benAccountNumber);
					
					txtAccNo.setText(benAccountNumber);
					// System.out.println("benSrno:=====>" + benSrno);
				}
						
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				txtAccNo.setText("");
			}

		});// end spi_sel_beneficiery

		spi_debit_account.setOnItemSelectedListener(new OnItemSelectedListener() 
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
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
							String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition()-1);
							retMess = "Selected Account number" + str;
							Log.e("TRANSFER","str==="+str);
							Log.e("TRANSFER","retMess==="+retMess);
					
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
			public void onNothingSelected(AdapterView<?> arg0) 
			{}// end onNothingSelected
		});// end spi_debit_account

		//all_acnts = "2#101#SB#25430#KULKARNI SHASHIKANT  RAJARAM##0020001010025430#O#9999999#15433163#Y~2#101#SB#25584#KULKARNI SHASHIKANT  RAJARAM##0020001010025584#O#NA#618.8#Y~2#101#SB#25635#KULKARNI SHASHIKANT  RAJARAM##0020001010025635#O#NA#25471.2#Y~2#101#SB#25636#KULKARNI SHASHIKANT  RAJARAM##0020001010025636#O#NA#17848.2#Y~2#101#SB#25637#KULKARNI SHASHIKANT  RAJARAM##0020001010025637#O#NA#37783.53#Y~2#1011#SB#1#KULKARNI SHASHIKANT  RAJARAM##0020010110000001#O#NA#0#Y~2#301#LO#58#KULKARNI SHASHIKANT  RAJARAM##0020003010000058#I#NA#13100.3#Y~2#301#LO#131#KULKARNI SHASHIKANT  RAJARAM##0020003010000131#I#NA#452922.87#Y~2#337#LO#8345#KULKARNI SHASHIKANT  RAJARAM##0020003370008345#I#NA#142105#Y~2#TEST2#LO#6#KULKARNI SHASHIKANT  RAJARAM##0020TEST20000006#I#NA#140000#Y~2#901#PG#10020209#KULKARNI SHASHIKANT  RAJARAM##0020009011002020#O##0#N~";//stringValue;
		all_acnts=stringValue;
		addAccounts(all_acnts);
		//System.out.println("========== 1 ============");
		if(!noAccounts)
		{
			noAccounts=false;
			this.flag = chkConnectivity();
			if (this.flag == 0) 
			{
				new CallWebService_fetch_all_beneficiaries().execute();
			}
		}
		this.pb_wait.setMax(10);
		this.pb_wait.setProgress(1);
		this.pb_wait.setVisibility(4);
		txtAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	
	private void addBeneficiaries(String retval) 
	{
		Log.e("addBeneficiaries====", "split=="+retval);
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			int noOfben = allstr.length;
			String benName = "";
			arrList.add("Select Beneficiary");
			for (int i = 1; i <= noOfben; i++) 
			{
				String[] str2 = allstr[i - 1].split("#");
				benName = str2[2] + "(" + str2[1] + ")";
				arrList.add(benName);
			}
			
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			ArrayAdapter<String> accs = new ArrayAdapter<String>(SameBankTransfer.this,R.layout.spinner_item, benfArr);
			accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(accs);

		} catch (Exception e) {
			System.out.println("" + e);
		}
	}// end addBeneficiaries
	
	public void addAccounts(String str) 
	{
		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			int noOfAccounts = allstr.length;
			arrList.add("Select Debit Account");
			arrListTemp.add("Select Debit Account");
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) 
			{
				str2 = allstr[i];
				String tempStr=str2;
				//System.out.println(i + "str2-----------" + str2);
				
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				Log.e("add accounts","accType"+accType);
				Log.e("add accounts","oprcd"+oprcd);
				String withdrawalAllowed=allstr[i].split("#")[10];
				if (((accType.equals("SB")) ||(accType.equals("LO"))
						||(accType.equals("CA")))&& oprcd.equalsIgnoreCase("O")&& withdrawalAllowed.equalsIgnoreCase("Y"))
				{
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2);
				}
			}
			
			if(arrList.size()==0)
			{
				noAccounts=true;
				showAlert(getString(R.string.alert_089));
			}
			
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(this,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
		} 
		catch (Exception e) 
		{
			System.out.println("" + e);
			e.printStackTrace();
			Log.e("Exception ","Exception in add accounts"+e);
		}
	}// end addAccount

	public int chkConnectivity() 
	{
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try 
		{
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
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
					// setAlert();

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

	class CallWebService_fetch_all_beneficiaries extends
			AsyncTask<Void, Void, Void> {

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject obj = new JSONObject();

		protected void onPreExecute() {
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
		
			try {

				obj.put("CUSTID", custId);
				obj.put("SAMEBNK", "Y");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","13"); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
		}// end dodoInBackground

		protected void onPostExecute(Void paramVoid) {
			
			loadProBarObj.dismiss();
			// System.out.println("xml_data.len :" + xml_data.length);
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
					getBeneficiariesrespdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					getBeneficiariesrespdesc = "";
				}
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(getBeneficiariesrespdesc.length()>0)
			{
				showAlert(getBeneficiariesrespdesc);
			}
			else{
			String decryptedBeneficiaries = reTval;

			
			if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) 
			{	
				post_successfetch_all_beneficiaries(reTval);	
			} 
			else 
			{
				if (decryptedBeneficiaries.indexOf("NODATA") > -1) 
				{
					Toast.makeText(act, getString(R.string.alert_041),Toast.LENGTH_LONG).show();
					Intent in=new Intent(act, FundTransferMenuActivity.class);
					in.putExtra("var1", var1);
					   in.putExtra("var3", var3);
					startActivity(in);
					finish();
				} 
				else 
				{
					retMess = getString(R.string.alert_069);
					showAlert(retMess);
				}
			}
			}
		}// end onPostExecute

	}// end callWbService

	public 	void post_successfetch_all_beneficiaries(String reTval)
	{
		respcode="";
		getBeneficiariesrespdesc="";
		String decryptedBeneficiaries=reTval;
		decryptedBeneficiaries = decryptedBeneficiaries
				.split("SUCCESS~")[1];
		// Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);
		benInfo = decryptedBeneficiaries;
		addBeneficiaries(decryptedBeneficiaries);
		
	}
	
	class CallWebService2 extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		String accNo, debitAccno, benAcNo, amt, reMark;
		JSONObject obj = new JSONObject();
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
			debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
			benAcNo = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			amt = txtAmt.getText().toString().trim();
			reMark = txt_remark.getText().toString().trim();

			Log.e("TRANSFER", "debitAccno==" + debitAccno);
			String crAccNo = txt_to.getText().toString().trim();
			String charges = txt_charges.getText().toString().split(" ")[1];
			String drAccNo = txt_from.getText().toString().trim();

			Log.e("TRANSFER", "drAccNo==" + drAccNo);
			
			try {

				obj.put("BENFSRNO", benSrno);
				obj.put("CRACCNO", crAccNo);
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

		protected void onPostExecute(Void paramVoid) {
			
			//String decryptedAccounts = xml_data[0];
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
			//
			if (reTval.indexOf("SUCCESS") > -1) {
				
				post_successsaveTransferTran(reTval);
				
			} else if (reTval.indexOf("DUPLICATE") > -1) {

				retMess = getString(R.string.alert_119) + tranId + "\n"
						+ getString(R.string.alert_120);
				showAlert(retMess);
				Intent in=new Intent(act, FundTransferMenuActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				
			} else if (reTval.indexOf("FAILED#") > -1) {
				retMess = getString(R.string.alert_032);
				showAlert(retMess);// setAlert();
				// System.out
				// .println("================== in onPostExecute 2 ============================");
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
			 else if (reTval.indexOf("FAILED") > -1) {
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
					//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("001"))
				{
					    retMess = getString(R.string.alert_180);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("002"))
				{
					    retMess = getString(R.string.alert_181);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("003"))
				{
					    retMess = getString(R.string.alert_182);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("004"))
				{
					retMess = getString(R.string.alert_179);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("005"))
				{
					    retMess = getString(R.string.alert_183);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("006"))
				{
					    retMess = getString(R.string.alert_184);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("007"))
				{
					retMess = getString(R.string.alert_179);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("008"))
				{
					    retMess = getString(R.string.alert_176);
						//showAlert(retMess);
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
			}

		}// end onPostExecute
	}// end callWbService2
	
	public void post_successsaveTransferTran(String reTval)
	{
		//respcode="";
		//saveTransferTranrespdesc="";
		retMess = getString(R.string.alert_030) + " "
				+ getString(R.string.alert_121) + " " + tranId;
		saveTransferTranrespdesc=retMess;
		showAlert(retMess);
		/*Intent in=new Intent(act, FundTransferMenuActivity.class);
		startActivity(in);
		finish();*/
	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) 
			{
				switch (v.getId()) 
				{
				case R.id.btn_ok:
					if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successfetch_all_beneficiaries(reTval);
					}
					else if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(saveTransferTranrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
					{
						
						Intent in=new Intent(act, FundTransferMenuActivity.class);
						in.putExtra("var1", var1);
						   in.putExtra("var3", var3);
						startActivity(in);
						finish();
						this.dismiss();//post_successsaveTransferTran(reTval);
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
					else if(this.textMessage.equalsIgnoreCase(act.getString(R.string.alert_125_1)))
						{
							InputDialogBox inputBox = new InputDialogBox(act);
							inputBox.show();
					    }
                                 	else{
					if (noAccounts) {
						if (same_bnk_layout.getVisibility() == View.VISIBLE) 
						{
							Intent in = new Intent(act,FundTransferMenuActivity.class);
							in.putExtra("var1", var1);
							   in.putExtra("var3", var3);
							startActivity(in);
							finish();
							//act.frgIndex = 5;
						} else if (confirm_layout.getVisibility() == View.VISIBLE) {
							confirm_layout
									.setVisibility(confirm_layout.INVISIBLE);
							same_bnk_layout
									.setVisibility(same_bnk_layout.VISIBLE);
							//act.frgIndex = 51;
                                                       }
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
				
				 // System.out.println("========= inside onClick ============***********"); 
				  String str=mpin.getText().toString().trim(); 
				  encrptdTranMpin=str;//ListEncryption.encryptData(custId+str);
				 //String encrptdUTranMpin=ListEncryption.encryptData(userId+str);
				  
				  if(str.length()==0) 
				  {
				  	retMess=getString(R.string.alert_116); 
				  	showAlert(retMess);//setAlert();
				  	this.show(); 
				  } 
				 /* else if(str.length()!=6)
				  {
					retMess=getString(R.string.alert_037); 
					showAlert(retMess);//setAlert();
				  	this.show(); 
				  } */
				  else 
				  {
				  	//System.out.println("======== strmpin:=="+str);
				  	//System.out.println("======== mobPin:=="+mobPin);
				  	//if(encrptdTranMpin.equals(tranPin)||encrptdUTranMpin.equals(tranPin)) 
				   
				  		//saveData(); 
					  callValidateTranpinService validateTran = new callValidateTranpinService();
						validateTran.execute();
				  		this.hide(); 
				 
				  	/*else 
				  	{
				  		//System.out.println("=========== inside else ==============");
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
	class callValidateTranpinService extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		JSONObject obj = new JSONObject();

		protected void onPreExecute() {
			loadProBarObj.show();
			
			
			try {
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
				
				
			} catch (JSONException e) {
				e.printStackTrace();
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

		protected void onPostExecute(Void paramVoid) {


			String str=CryptoClass.Function6(var5,var2);
			String decryptedAccounts = str.trim();
			Log.e("decryptedAccounts=","decryptedAccounts=="+decryptedAccounts);
			loadProBarObj.dismiss();
			
			
				if (decryptedAccounts.indexOf("SUCCESS") > -1) {
					saveData();
				} else if (decryptedAccounts.indexOf("FAILED#") > -1) {
					retMess = getString(R.string.alert_032);
					showAlert(retMess);// setAlert();
				} 
				else if (decryptedAccounts.indexOf("BLOCKEDFORDAY") > -1) 
				{
					retMess = getString(R.string.login_alert_005);
					showAlert(retMess);// setAlert();
				} 
				else if (decryptedAccounts.indexOf("WRONGTRANPIN") > -1) {
					JSONObject obj = null;
					try {
						obj = new JSONObject(decryptedAccounts);
						String msg[] = obj.getString("RETVAL").split("~");
						String first = msg[1];
						String second = msg[2];
						int count = Integer.parseInt(second);
						count = 5 - count;
						loadProBarObj.dismiss();
						retMess = act.getString(R.string.alert_125_1) + " " + count
								+ " " + act.getString(R.string.alert_125_2);
						showAlert(retMess);
					} catch (JSONException e) {
						// TODO Auto-generat                                                                                                                                                                                                                                                        ed catch block
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
				else{
					JSONObject obj = null;
					try {
						obj = new JSONObject(decryptedAccounts);
						String msg[] = obj.getString("RETVAL").split("~");
						String first = msg[1];
						if(first.equalsIgnoreCase("8")){
							showAlert("trying from diff device");	
						}
						else if(first.equalsIgnoreCase("9")){
							showAlert("account blocked for today");	
						}
						else if(first.equalsIgnoreCase("3")){
							showAlert("Invalide customer");	
						}
					} catch (JSONException e) {
						// TODO Auto-generat                                                                                                                                                                                                                                                        ed catch block
						e.printStackTrace();
					}
			}
			
		}// end onPostExecute
	}// end callValidateTranpinService
	public void onClick(View v) 
	{ // logic to show input box
		
		if(v.getId() == R.id.btn_back)
		{
			Intent intet=new Intent(SameBankTransfer.this,FundTransferMenuActivity.class);
			intet.putExtra("var1", var1);
			   intet.putExtra("var3", var3);
			startActivity(intet);
			finish();
		}
		else if(v.getId() == R.id.btn_home)
		{/*
			Intent in=new Intent(getActivity(),DashboardDesignActivity.class);
			startActivity(in);
		*/}
		else if (v.getId() == R.id.spinner_btn2) 
		{
			spi_sel_beneficiery.performClick();
		} 
		else if (v.getId() == R.id.spinner_btn) 
		{
			spi_debit_account.performClick();
		} 
		else if (v.getId() == R.id.sameBnkTranbtn_submit) 
		{
			strFromAccNo=spi_debit_account.getSelectedItem().toString();
			strToAccNo = txtAccNo.getText().toString().trim();
			strAmount = txtAmt.getText().toString().trim();
			strRemark = txtRemk.getText().toString().trim();
			String balString = txtBalance.getText().toString().trim();//acArray[spi_debit_account.getSelectedItemPosition()-1].getBalace();
			//double balance = Double.parseDouble(balString);
			//balance = Math.abs(balance);
			double balance=0.0;//Double.parseDouble(balString);
			//balance=Math.abs(balance);
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
			
			String debitAcc=strFromAccNo.substring(0, 16);
			
			if(strFromAccNo.equalsIgnoreCase("Select Debit Account"))
			{
				showAlert(getString(R.string.alert_0981));
			}
			else if(strFromAccNo.length()==0)
			{
				showAlert(getString(R.string.alert_0981));
			}
			else if(strToAccNo.length()==0)
			{
				showAlert(getString(R.string.alert_098));
			}
			else if(strToAccNo.equalsIgnoreCase(debitAcc))
			{
				showAlert(getString(R.string.alert_107));
			}
			else if(strAmount.length()==0)
			{
				showAlert(getString(R.string.alert_033));
			}
			else if(Double.parseDouble(strAmount)==0)
			{
				showAlert(getString(R.string.alert_034));
			}
			else if(strRemark.length()==0)
			{
				showAlert(getString(R.string.alert_035));
			} else if (Double.parseDouble(strAmount) > balance) {
				showAlert(getString(R.string.alert_176));
		} 
		else
		{
			try 
			{
				//this.flag = chkConnectivity();
				//if (this.flag == 0) 
				{
					CallWebServiceGetSrvcCharg c=new CallWebServiceGetSrvcCharg();
					c.execute();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("Exception in CallWebServiceGetSrvcCharg is:" + e);
			}
		}
		}
		else if (v.getId() == R.id.btn_confirm)
		{
			//System.out.println("amount when null:==>" + strAmount.length()
			//		+ " content is:==>" + strAmount);
			if (strAmount.length() == 0) {
				strAmount = "0";
				//System.out.println("Cuttent thread name:==>"
				///		+ Thread.currentThread().getName());
				//System.out.println("--------------- 22 ------------");
				retMess = getString(R.string.alert_033);
				//System.out.println("--------------- 22.1 ------------");
				showAlert(retMess);//setAlert();
				//System.out.println("--------------- 22.2 ------------");
				txtAmt.requestFocus();
				//System.out.println("--------------- 22.3 ------------");
			} 
			else 
			{
				//int amt = Double.parseDouble(strAmount);
				if (Double.parseDouble(strAmount) <= 0) 
				{
					//System.out.println("--------------- 44 ------------");
					retMess = getString(R.string.alert_034);
					showAlert(retMess);//setAlert();
					txtAmt.requestFocus();
				} 
				else 
				{
					if (strRemark.length() > 200) 
					{
						//System.out.println("--------------- 33 ------------");
						retMess = getString(R.string.alert_097);
						showAlert(retMess);//setAlert();
						txtRemk.requestFocus();
					} 
					else if (strToAccNo.length() == 0) 
					{
						retMess = getString(R.string.alert_067);
						showAlert(retMess);//setAlert();
					} 
					else 
					{
						InputDialogBox inputBox = new InputDialogBox(this);
						inputBox.show();
					} // end else
				}
			}// end if
		}

	}// end click
	
	public void saveData() {
		try {
			//System.out.println("--------------- 44 ------------");
			//this.flag = chkConnectivity();
			//if (this.flag == 0) 
			//{
				//new CallWebService2().execute();
			//}
			Log.e("saveData","saveData==");

			String accNo = txtAccNo.getText().toString().trim();
			//String debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition()-1);
			String debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
			String benAcNo = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			String amt = txtAmt.getText().toString().trim();
			String reMark = txt_remark.getText().toString().trim();

			String crAccNo = txt_to.getText().toString().trim();
			String charges = txt_charges.getText().toString().split(" ")[1];
			String drAccNo = txt_from.getText().toString().trim();
Log.e("encrptdTranMpin===same", encrptdTranMpin);
Log.e("debitAccno===same", debitAccno);
			JSONObject obj = new JSONObject();
			try {
				obj.put("BENFSRNO", benSrno);
				obj.put("CRACCNO", crAccNo);
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

			Bundle bundle = new Bundle();
			//Fragment fragment = new TransferOTP(act);
			bundle.putString("CUSTID", custId);
			bundle.putString("FROMACT", "SAMEBANK");
			bundle.putString("JSONOBJ", obj.toString());
			Intent in = new Intent(SameBankTransfer.this,TransferOTP.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			   in.putExtras(bundle);
			act.startActivity(in);
			act.finish();
		/*	fragment.setArguments(bundle);
			FragmentManager fragmentManager = sameBnkTran.getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();*/
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in saveTransferTran is:" + e);
		}
	}// end saveData
	
	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject obj = new JSONObject();
	
		String accNo, debitAccno, benAcNo, amt, reMark;

		protected void onPreExecute() {
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
			debitAccno = arrListTemp.get(spi_debit_account
					.getSelectedItemPosition());
			benAcNo = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();

			amt = txtAmt.getText().toString().trim();
			reMark = txtRemk.getText().toString().trim();
			debitAccno=debitAccno.substring(0, 16);

			Log.e("GETSRVCCHRG", "custId==" + custId);
			Log.e("GETSRVCCHRG", "TRANTYPE==SAME==");
			Log.e("GETSRVCCHRG", "debitAccno==" + debitAccno);
			Log.e("GETSRVCCHRG", "amt==" + amt);
			Log.e("GETSRVCCHRG", "benAcNo==" + benAcNo);
			Log.e("GETSRVCCHRG", "IMEI==" + MBSUtils.getImeiNumber(act));

			
			try {

				obj.put("CUSTID", custId);
				obj.put("TRANTYPE", "INTBANK");
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("CRACCNO", accNo);
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("BENFSRNO", benSrno);
				obj.put("METHODCODE","28"); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		protected void onPostExecute(Void paramVoid) {
			
			//String decryptedAccounts = xml_data[0];
	          
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
			// retval = "SUCCESS";
			if (reTval.indexOf("SUCCESS") > -1) {
				//act.frgIndex = 52;///511
				
				post_successGetSrvcCharg(reTval);
				
			} else {
				if (reTval.indexOf("LIMIT_EXCEEDS") > -1) {
					retMess = getString(R.string.alert_031);
					//loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
                       	} else if (reTval.indexOf("LOWBALANCE") > -1) {
					retMess = getString(R.string.alert_176);
					//loadProBarObj.dismiss();
					showAlert(retMess);
				} 
                        else if (reTval.indexOf("SingleLimitExceeded") > -1) {
    						retMess = getString(R.string.alert_193);
    						//loadProBarObj.dismiss();
    						showAlert(retMess);
    					}
    				
    				 else if (reTval.indexOf("TotalLimitExceeded") > -1) {
    						retMess = getString(R.string.alert_194);
    						//loadProBarObj.dismiss();
    						showAlert(retMess);
    					}
                       	else {
					// this case consider when in retval string contains only
					// "FAILED"
					retMess = getString(R.string.alert_032);
					//loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
					// System.out
					// .println("================== in onPostExecute 2 ============================");
				}
			}// end else
	   			}
		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg
	
	public 	void post_successGetSrvcCharg(String reTval)
	{
		respcode="";
		getTransferChargesrespdesc="";
		//act.frgIndex = 52;///511
		Log.e("11","reTval=="+reTval);
		same_bnk_layout.setVisibility(same_bnk_layout.INVISIBLE);
		confirm_layout.setVisibility(confirm_layout.VISIBLE);
		// Log.e("SAMEBANKTRANSFER","xml_data[0]=="+xml_data[0]);

		String retStr = reTval.split("~")[1];
         String tranType=reTval.split("~")[2];
		String[] val = retStr.split("#");
		txt_heading.setText("Confirmation");
		txt_remark.setText(strRemark);
		txt_from.setText(strFromAccNo);
		txt_to.setText(strToAccNo);
		txt_amount.setText("INR " + strAmount);
		txt_charges.setText("INR " + val[0]);
        txt_trantype.setText(tranType);
		chrgCrAccNo = val[1];
		tranId = val[2];
		// txtTranId.setText(tranId);
		if (chrgCrAccNo.length() == 0
				|| chrgCrAccNo.equalsIgnoreCase("null"))
			chrgCrAccNo = "";
	}
	
	@Override
	public void onBackPressed() {
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
