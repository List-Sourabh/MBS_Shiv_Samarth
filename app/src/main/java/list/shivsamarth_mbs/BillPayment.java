package list.shivsamarth_mbs;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import list.shivsamarth_mbs.R;

import mbLib.BillerBean;
import mbLib.CryptoUtil;
import mbLib.CusFntTextView;
import mbLib.CustomAdapterForRechargePlan;
import mbLib.CustomEditText;
import mbLib.DatabaseManagement;
import mbLib.HorizontalviewAdapter;

import mbLib.MBSUtils;
import mbLib.MyThread;
import mbLib.RechargePlanBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import mbLib.HorizontalListView;

public class BillPayment extends Activity implements OnClickListener 
{
	BillPayment billObj;
	DatabaseManagement dbms;
	
	LinearLayout linear_layout,confirm_layout;
	ListView  lstRpt;
	Spinner spi_biller,dbtAccSpnr;
	ImageButton btn_back;
	ImageButton spinner_btn;
	TextView txt_heading,biller;
	Button btn_submit,btn_confirm;
	CusFntTextView viewPlanTV;
	CustomEditText balanceEDT,accountEDT,planIdEDT,rchrgEDT;
	public  ArrayList<BillerBean> billerBeanArray=null;
	private MyThread t1;
	int timeOutInSecs=300;
	ArrayList<CusFntTextView>  CusFntTextViewArr=null;
	ArrayList<CustomEditText>  CustomEditTextArr=null;
	ArrayList<Button>  buttonArr=null;
	
	ArrayList<String> billerCdarr = new ArrayList<String>();
	ArrayList<String> fieldArr = new ArrayList<String>();
	ArrayList<String> arrListTemp = new ArrayList<String>();
	ArrayList<RechargePlanBean> rechargePlanBeanArr;
	Set<String> rechargeTags = new HashSet<String>();
	
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String METHOD_FETCH_BILLER = "fetchBiller";
	private static final String METHOD_FETCH_PAYEE="fetchPayeeDetails";
	private static String METHOD_FETCH_PLAN = "viewRechargePlan";
	private static String METHOD_VALIDATE_RCHRG = "validatePayment";
	private static String METHOD_RCHRG="doRecharge";
	private static final String GET_MMID = "generateMMID";
	
	String mpinStr="", custId="",imeiNo="",catcd="",retMess="",str="",billerCd="",billerAccId="",stringValue="",str2="",
			acnt_inf="",selectedAcc="",debitAccNo="",drBrnCD="",drSchmCD="",drAcNo="",encrptdTranMpin="",retVal="",
			paymntId="",rechargeType="",req_id = "",mmId="",custMobNo="";
	int flag=0,idNo=0,saveBtnId=0,confirmBtnId=0,viewPlanId=0;
	boolean flg=false;
	JSONObject billJsonObj;
	Accounts acArray[];
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public void onBackPressed() 
	{
		return ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		billObj=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_biller);

		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
        biller = (TextView)findViewById(R.id.biller);
        biller.setVisibility(TextView.INVISIBLE);
        txt_heading = (TextView)findViewById(R.id.txt_heading);
       
        imeiNo = MBSUtils.getImeiNumber(billObj);
        btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setImageResource(R.mipmap.backover);
        btn_back.setOnClickListener(this);
        var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
	   
        
        spi_biller = (Spinner) findViewById(R.id.spi_biller);
        spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
        spinner_btn.setVisibility(ImageButton.INVISIBLE);
        txt_heading.setText(billObj.getString(R.string.lbl_rech_Recharges1));
    	/*Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue = c1.getString(0);
        		custId=c1.getString(2);
        		custMobNo=c1.getString(4);
            }
        }*/
        stringValue="2#101#SB#8888#KULKARNI SUBHASH HARI##0020001010008888#O#362511#1536333.6#Y~2#602#RP#5176#KULKARNI SUBHASH  HARI##0020006020005176#I##259598#N~";
        custId="0002014455";
        custMobNo="8884447771";
        linear_layout = (LinearLayout)findViewById(R.id.main_layout);
        confirm_layout = (LinearLayout) findViewById(R.id.confirm_layout);
        
        btn_submit=new Button(billObj);
        btn_submit.setOnClickListener(this);
    	btn_confirm=new Button(billObj);
    	btn_confirm.setOnClickListener(this);
    	viewPlanTV = new CusFntTextView(billObj);
    	viewPlanTV.setOnClickListener(this);
    	Intent getdata=getIntent();
    	Bundle bundle = getdata.getExtras();
		if (bundle != null) 
		{
			catcd = bundle.getString("CATEGORY");
			billerCd = bundle.getString("BILLERCD");
			billerAccId = bundle.getString("BILLERACCID");
		}
		Log.e("ccat","......"+catcd);
    	//if(chkConnectivity()==0)
       // {	
        	Log.e("CallWebServiceFetchBiller***","......");
        	new CallWebServiceFetchBillerField().execute();
       // }
       // else
       // {
        	//showAlert(billObj.getString(R.string.alert_000));
       // }
       
        	t1 = new MyThread(timeOutInSecs,this,var1,var3);
    		t1.start();
    }
	
	public void addAccounts(String str) 
	{
		System.out.println("BillPayment IN addAccounts()" + str);

		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");

			int noOfAccounts = allstr.length;
			arrList.add("Select Debit Account");
			
			acArray = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) 
			{
				str2 = allstr[i];

				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);

				if (((accType.equals("SB")) || (accType.equals("CA")) || (accType
						.equals("LO"))) && oprcd.equalsIgnoreCase("O")) {
					arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)
							+ ")");
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(billObj,
					R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			dbtAccSpnr.setAdapter(debAccs);
			dbtAccSpnr.setOnItemSelectedListener(new OnItemSelectedListener() 
			{
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
				{
					selectedAcc=dbtAccSpnr.getSelectedItem().toString();
					if (arg2 != 0) 
					{
						Log.e("str= ","str="+selectedAcc);
						if(selectedAcc.equalsIgnoreCase("Select Debit Account"))
						{
							balanceEDT.setText("");
							accountEDT.setText("");
						}
						else
						{
							if (!selectedAcc.equalsIgnoreCase("Select Debit Account")) 
							{
								debitAccNo = arrListTemp.get(dbtAccSpnr.getSelectedItemPosition()-1);

								String debitAc[] = debitAccNo.split("-");
								System.out.println("============account 1:"+ debitAc[0]);// 5
								System.out.println("============account 2:"+ debitAc[1]);// 101
								// System.out.println("account 3:"+debitAc[2]);//SB
								System.out.println("============account 4:"+ debitAc[3]);// 7
								
								drBrnCD = debitAc[0];
								drSchmCD = debitAc[1];
								drAcNo = debitAc[3];
		
								mmId = debitAc[8];
								Log.e("MMID", "MMID  " + mmId);
								//mmid="NA";
								if (mmId.equals("NA"))
									showAlert1(getString(R.string.lbl_mmid_msg));
								
								Accounts selectedDrAccount=acArray[dbtAccSpnr.getSelectedItemPosition()-1];
								Log.e("spi_debit_account","spi_debit_account==="+selectedDrAccount);
								
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
								balanceEDT.setText(balStr);
								accountEDT.setText(MBSUtils.get16digitsAccNo(debitAccNo));
							}
							else
							{
								balanceEDT.setText("");
								accountEDT.setText("");
							}
						}
					}
				}// end onItemSelected

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}

			});
			dbtAccSpnr.setSelection(1);
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount
	
	public void showAlert(String str) {
		
		ErrorDialogClass alert = new ErrorDialogClass(billObj, "" + str)
		{
            Intent in = null;
            
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.btn_ok:
                    	if(flg)
                    	{
                    		Intent in=new Intent(billObj,Recharges.class);
                			startActivity(in);
                			billObj.finish(); 
                    		
                    		/*Fragment fragment = new Recharges(act);
 							FragmentManager fragmentManager = getFragmentManager();
 							fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
						}
                    	 else
                    	{
                    		
                    	}
                }this.dismiss();
            }
		 };alert.show();
	}
	
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) billObj.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {}
					break;
				case DISCONNECTED:
					flag = 1;
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					retMess=getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) 
		{
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "ResourceAsColor" }) @Override
	public void onClick(View v) 
	{
		if(v.getId()==saveBtnId)
		{	
			Log.e("CusFntTextViewArr==","CusFntTextViewArr.size()=="+CusFntTextViewArr.size());
        	String[] str = new String[CusFntTextViewArr.size()];
   
        	char last = 0;
        	String  status="false";
        	for(int i=0;i<fieldArr.size();i++)
			{
        		last = CusFntTextViewArr.get(i).getText().toString().charAt(CusFntTextViewArr.get(i).getText().toString().length() - 1);
        		Log.e("LAST","LAst=="+last);
        		if(last=='*')
	            if(CustomEditTextArr.get(i).getText().toString().length() == 0)
	            {
	            	status="true";
	            }
	            else if(fieldArr.get(i).equalsIgnoreCase("DEBIT_ACCNT") && CustomEditTextArr.get(i).getText().toString().equalsIgnoreCase("Select Debit Account"))
	            {
	            	showAlert(billObj.getString(R.string.alert_mandtry_field));
	            }
			}
            if(status=="true")
            {
            	showAlert(billObj.getString(R.string.alert_mandtry_field));
            }
        	else 
            {
            	linear_layout.setVisibility(LinearLayout.GONE);
            	confirm_layout.setVisibility(LinearLayout.VISIBLE);
            	
            	txt_heading.setText(billObj.getString(R.string.lbl_confirm));            	
            	CusFntTextView title=new CusFntTextView(billObj);
            	title.setText("Please Confirm Below Details");
            	title.setTextSize(18);
            	title.setGravity(Gravity.CENTER);
            	
            	//title.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            	LinearLayout.LayoutParams titleParam=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            	titleParam.setMargins(0, 10, 0, 15);
            	title.setLayoutParams(titleParam);
    	        confirm_layout.addView(title);
    	       
            	for(int i=0; i < CusFntTextViewArr.size(); i++)
                {
            		LinearLayout parent = new LinearLayout(billObj);
            		LinearLayout.LayoutParams parentParam=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            		parentParam.setLayoutDirection(LinearLayout.HORIZONTAL);
            		//parentParam.setMargins(0, 0, 0, 10);
            		parent.setLayoutParams(parentParam);
            		parent.setWeightSum(2);
            		confirm_layout.addView(parent);
            		
            		CusFntTextView valueTV1 = new CusFntTextView(billObj);
            		if(CusFntTextViewArr.get(i).getText().toString().indexOf("Plan")>-1)
            			valueTV1.setText("Plan Id  : ");
            		else
            			valueTV1.setText(CusFntTextViewArr.get(i).getText().toString()+"  : ");
	                valueTV1.setId(idNo);
	                valueTV1.setGravity(Gravity.RIGHT);
	                valueTV1.setTextSize(14);
	                LinearLayout.LayoutParams newParam=new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
	                newParam.setMargins(0, 0, 5, 10);
	                newParam.weight=1;
	                valueTV1.setLayoutParams(newParam);
	                parent.addView(valueTV1);
	                idNo++;
	                
	                CusFntTextView valueTV2 = new CusFntTextView(billObj);
	                valueTV2.setText("  "+CustomEditTextArr.get(i).getText().toString());
	                valueTV2.setId(idNo);
	                valueTV2.setGravity(Gravity.LEFT);
	                valueTV2.setTextSize(14);
	                LinearLayout.LayoutParams newParam2=new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
	                newParam.setMargins(5, 0, 0, 10);
	                newParam2.weight=1;
	                valueTV2.setLayoutParams(newParam2);
	                parent.addView(valueTV2);
	                idNo++;
            	}
            	
    		    btn_confirm.setText("Confirm");
    		    btn_confirm.setId(idNo);
    		    confirmBtnId=idNo;
    	        @SuppressWarnings("deprecation")
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		        params.setMargins(0,15,0,5);
		        btn_confirm.setLayoutParams(params);
    	        confirm_layout.addView(btn_confirm);
    	        
    	        CusFntTextView noteTV = new CusFntTextView(billObj);
		        noteTV.setText("Amount Shown Are In Rs.");
		        noteTV.setTextColor(R.color.red_color);
		        noteTV.setGravity(Gravity.CENTER_HORIZONTAL);
		        noteTV.setTextSize(12);
                noteTV.setId(idNo);
                idNo++;
                noteTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                confirm_layout.addView(noteTV);
                
            }
		} 
		else if(v.getId()==viewPlanId)
		{
			//if(chkConnectivity()==0)
				new CallWebServiceViewRechargePlan().execute();
			//else
			//	showAlert(billObj.getString(R.string.alert_000));
		}
		else if(v.getId()==confirmBtnId)
		{
			InputDialogBox dialogObj= new InputDialogBox(billObj);
			dialogObj.show();
		}
		else if(v.getId()==R.id.btn_back)
		{
			Intent in=new Intent(billObj,Recharges.class);
			startActivity(in);
			billObj.finish(); 
			/*Fragment fragment = new Recharges(act);
			act.setTitle(getString(R.string.lbl_title_change_mpin));
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
		}
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
		public void onClick(View v) 
		{
			try 
			{
				String str = mpin.getText().toString().trim();
				encrptdTranMpin = str;//ListEncryption.encryptData(custId + str);
				if (str.length() == 0) 
				{
					retMess = getString(R.string.alert_enterTranMpin);
					showAlert(retMess);
					this.show();
				} 
				else if (str.length() != 6) 
				{
					retMess = getString(R.string.alert_TranmipnMust6dig);
					showAlert(retMess);
					this.show();
				} 
				else 
				{
					//if(chkConnectivity()==0)
				    {	
						mpinStr=encrptdTranMpin;
				    	Log.e("CallWebServiceAddPayee***","......"+mpinStr);
				       	new CallWebServiceValidateRecharge().execute();
				    }
				    /*else
				    {
				       	showAlert(billObj.getString(R.string.alert_000));
				    }*/
					this.hide();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
			}
		}// end onClick
	}// end InputDialogBox
	
	class CallWebServiceFetchBillerField extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
			LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);
			//String[] xmlTags = { "CUSTID", "ACCNO", "IMEINO" };
			 String[] xmlTags = {"PARAMS"};
		     String[] valuesToEncrypt = new String[1];
		     JSONObject jsonObj = new JSONObject();

			 String generatedXML = "";

			@Override
			protected void onPreExecute() 
			{
				try
				{
					loadProBarObj.show();
					jsonObj.put("CUSTID", custId);
		            jsonObj.put("CATEGORY", catcd);
		            jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(billObj));
		            //Log.e("CATEGORY","=catcd in preExcute="+cat);
				}
				catch (JSONException je) 
				{
		            je.printStackTrace();
		        }
			    valuesToEncrypt[0] = jsonObj.toString();
				generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
				System.out.println("&&&&&&&&&& generatedXML " + generatedXML);

			};

			@Override
			protected Void doInBackground(Void... arg0) 
			{
				System.out.println("============= inside doInBackground =================");
				NAMESPACE = getString(R.string.namespace);
				URL = getString(R.string.url);
				SOAP_ACTION = getString(R.string.soap_action);

				SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_BILLER);

				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,15000);
				System.out.println("============= inside doInBackground 2 =================");
				/*try 
				{
					androidHttpTransport.call(SOAP_ACTION, envelope);
					System.out.println(envelope.bodyIn.toString());
					retval = envelope.bodyIn.toString().trim();
					System.out.println("FdRdAccountDetail    retval-----"+ retval);
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					retval = retval.substring(pos + 1, retval.length() - 3);
					System.out.println("FdRdAccountDetail    retval AFTER SUBSTR-----"+ retval);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					System.out.println("FdRdAccountDetail   Exception" + e);
				}*/
				return null;
			}
			
			@SuppressLint("ResourceAsColor") @SuppressWarnings("deprecation")
			protected void onPostExecute(final Void result) 
			{
				loadProBarObj.dismiss();
				
				String[] xmlTags = { "PARAMS" };
				String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
				String decryptedRetVal ="";
				if(catcd.equalsIgnoreCase("PREPAID MOBILE"))
				{
					 decryptedRetVal = "SUCCESS~AIRCELPRE#AIRCEL PREPAID~AIRTELPRE#AIRTEL PREPAID~BSNLPRE#BSNL  PREPAID ~DOCOMOPRE#TATA DOCOMO GSM~IDEAPRE#IDEA PREPAID~MTNLDELPRE#MTNL DELHI PREPAID~MTNLMUMPRE#MTNL MUMBAI PREPAID~RIMGSMPRE#RELIANCE GSM PREPAID~UNINORPRE#TELENOR PREPAID~VODAFONPRE#VODAFONE PREPAID~JIOPRE#JIO PREPAID~VIDEOCNPRE#VIDEOCON PREPAID~MTSPRE#MTS PREPAID~TTSLPRE#TATA INDICOM PREPAID@MOBILE_NO#Y#Y#Mobile No#^[0-9]{10,12}$~ACC_NAME#Y#N#Account Name#~CIRCLE#Y#Y#Circle#^[0-9]{1,2}$";//xml_data[0];
				}
				else if(catcd.equalsIgnoreCase("PREPAID DTH"))
				{
					decryptedRetVal ="SUCCESS~AIRTELDTH#AIRTEL DTH~BIGTVDTH#BIG TV DTH~DISHTVDTH#DISH TV DTH~SUNTVDTH#SUN TV DTH~TATASKYDTH#TATASKY DTH~VIDEOCNDTH#VIDEOCON DTH@CUST_ID#N#Y#AIRTEL DTH#Customer Id#~CUST_ID#N#Y#BIG TV DTH#Smart Card Number#~CUST_ID#N#Y#DISH TV DTH#Viewing Card Number#~CUST_ID#N#Y#SUN TV DTH#Smart Card Number#~CUST_ID#N#Y#TATASKY DTH#Subscriber Number#~CUST_ID#N#Y#VIDEOCON DTH#Subscriber Id#~ACC_NAME#Y#N#Account Name#";
				}
				
				
				billerBeanArray=new ArrayList<BillerBean>();
				BillerBean Beanobj= new BillerBean();
				Beanobj.setField("-1");
				Beanobj.setIsCommon("-1");
				Beanobj.setIsMandatory("-1");
				Beanobj.setBiller("-1");
				Beanobj.setLabel("-1");
				String[] bil={};
				billerBeanArray.add(Beanobj);
				try
				{	
					Log.e("decryptedRetValAcc", decryptedRetVal);
					
					if (decryptedRetVal.indexOf("FAILED") > -1) 
					{
						showAlert(getString(R.string.alert_nobiller));
					}	
					else if (decryptedRetVal.indexOf("NODATA") > -1) 
					{
						showAlert(getString(R.string.alert_nobiller));
					}
					else 
					{
						
						String decryptedRet=decryptedRetVal.split("SUCCESS~")[1];
					
						Log.e("decryptedRet",decryptedRet);
						String billerStr=decryptedRet.split("@")[0];
						String billers[]=billerStr.split("~");
						
						ArrayList<String> arrList = new ArrayList<String>();
						
						//arrList.add("Select");
						//billerCdarr.add("-1");
						for(int i=0;i<billers.length;i++)
						{
							if(billerCd.equalsIgnoreCase(billers[i].split("#")[0]))
							{
								arrList.add(billers[i].split("#")[1]);
								billerCdarr.add(billers[i].split("#")[0]);
							}
						}
						String billersDtl=decryptedRet.split("@")[1];
						
						String[] allstr=billersDtl.split("~");
						Log.e("allstr","allstr"+allstr);
						int noOfbillr = allstr.length;
						Log.e("noOfbillr","noOfbillr"+noOfbillr);
						String billername="";
						String []dtlArr={};
						
					 
						for(int i=0;i<noOfbillr;i++)
						{
							Log.e("BILLER",i+"===="+allstr[i]);
							dtlArr=allstr[i].split("#");
						
							Beanobj= new BillerBean();
	            		 
							Beanobj.setField(dtlArr[0]);
							Beanobj.setIsCommon(dtlArr[1]);
							Beanobj.setIsMandatory(dtlArr[2]);
						
							if(dtlArr[1].equalsIgnoreCase("Y"))
							{
								Beanobj.setBiller("NA");
								Beanobj.setLabel(dtlArr[3]);
							}
							else
							{	
								Beanobj.setBiller(dtlArr[3]);
								Beanobj.setLabel(dtlArr[4]);
							}
						
							billerBeanArray.add(Beanobj);
						}
						
						Log.e("arrList.size()","arrList.size()"+arrList.size());
					
						String[] bilArr = new String[arrList.size()];
						bilArr = arrList.toArray(bilArr);
						Log.e("bilArr","bilArr"+bilArr);
						
						ArrayAdapter<String> biller = new ArrayAdapter<String>(billObj,R.layout.spinner_item, bilArr);
						biller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spi_biller.setAdapter(biller);
						spi_biller.setEnabled(false);
						
						str = spi_biller.getItemAtPosition(spi_biller.getSelectedItemPosition()).toString();
					
						Log.e("str===selected","str========="+str);
						//String pos=billercd[0];
						if(CusFntTextViewArr!=null)
						{
							for(int i=0; i<CusFntTextViewArr.size();i++)
								linear_layout.removeView(CusFntTextViewArr.get(i));
						}
						
						if(CustomEditTextArr!=null)
						{
							for(int i=0; i<CustomEditTextArr.size();i++)
								linear_layout.removeView(CustomEditTextArr.get(i));
						}
						
						if(btn_submit!=null)
							linear_layout.removeView(btn_submit);
						
						CusFntTextViewArr = new ArrayList<CusFntTextView>();
						CustomEditTextArr = new ArrayList<CustomEditText>();
						fieldArr = new ArrayList<String>();
						
						if(!str.equalsIgnoreCase("Select"))
						{
							Log.e("billerBeanArray==","size=="+billerBeanArray.size());
							for(int i=0;i<billerBeanArray.size();i++)
							{
								BillerBean beanObj=billerBeanArray.get(i);
								if(beanObj.getIsMandatory().equalsIgnoreCase("Y") && 
				        			beanObj.getIsCommon().equalsIgnoreCase("Y"))
								{
									Log.e("ONSELECT","add ==="+beanObj.getLabel());
										CusFntTextView valueTV = new CusFntTextView(billObj);
					                valueTV.setText(beanObj.getLabel()+"*");
					                fieldArr.add(beanObj.getField());
					                valueTV.setId(idNo);
					                idNo++;
					                valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueTV);
					                CusFntTextViewArr.add(valueTV);
					                
					                CustomEditText valueEDT = new CustomEditText(billObj);
					                valueEDT.setId(idNo);
					                idNo++;
					                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							        //params.setMargins(left, top, right, bottom);
							        params.setMargins(0,0,0,15);
							        valueEDT.setLayoutParams(params);
							        
					                //valueEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueEDT);
					                CustomEditTextArr.add(valueEDT);
					        	}
					        }
					        for(int i=0;i<billerBeanArray.size();i++)
					        {
					        	
					        	BillerBean beanObj=billerBeanArray.get(i);
					        	Log.e("billerBeanArray==","selected=="+str+"===="+beanObj.getBiller()+"==="+beanObj.getIsMandatory());
					        	if(beanObj.getIsMandatory().equalsIgnoreCase("Y") && 
					        		beanObj.getBiller().equalsIgnoreCase(str))
					        	{
					        		Log.e("ONSELECT","add ==="+beanObj.getLabel());
					        		CusFntTextView valueTV = new CusFntTextView(billObj);
					                valueTV.setText(beanObj.getLabel()+"*");
					                fieldArr.add(beanObj.getField());
					                valueTV.setId(idNo);
					                idNo++;
					                valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                try
					                {
					                	linear_layout.addView(valueTV);
					                }
					                catch(Exception e)
					                {
					                	e.printStackTrace();
					                }
					                CusFntTextViewArr.add(valueTV);
					                
					                CustomEditText valueEDT = new CustomEditText(billObj);
					                valueEDT.setId(idNo);
					                idNo++;
					                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							        //params.setMargins(left, top, right, bottom);
							        params.setMargins(0,0,0,15);
							        valueEDT.setLayoutParams(params);
							        
					                //valueEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueEDT);
					                 
					                CustomEditTextArr.add(valueEDT);
					        	}
					        }
					        for(int i=0;i<billerBeanArray.size();i++)
					        {
					        	BillerBean beanObj=billerBeanArray.get(i);
					        	if(beanObj.getIsMandatory().equalsIgnoreCase("N") && 
					        			beanObj.getIsCommon().equalsIgnoreCase("Y"))
					        	{
					        		Log.e("ONSELECT","add ==="+beanObj.getLabel());
					        		CusFntTextView valueTV = new CusFntTextView(billObj);
					                valueTV.setText(beanObj.getLabel());
					                fieldArr.add(beanObj.getField());
					                valueTV.setId(idNo);
					                idNo++;
					                valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueTV);
					                CusFntTextViewArr.add(valueTV);
					                
					                
					                CustomEditText valueEDT = new CustomEditText(billObj);
					                valueEDT.setId(idNo);
					                idNo++;
					                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							        //params.setMargins(left, top, right, bottom);
							        params.setMargins(0,0,0,15);
							        valueEDT.setLayoutParams(params);
							        
					                //valueEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueEDT);
					                CustomEditTextArr.add(valueEDT);
					        	}
					        }
					        for(int i=0;i<billerBeanArray.size();i++)
					        {
					        	BillerBean beanObj=billerBeanArray.get(i);
					        	if(beanObj.getIsMandatory().equalsIgnoreCase("N") && 
					        			beanObj.getIsCommon().equalsIgnoreCase("N") && 
						        		beanObj.getBiller().equalsIgnoreCase(str))
					        	{
					        		Log.e("ONSELECT","add ==="+beanObj.getLabel());
					        		CusFntTextView valueTV = new CusFntTextView(billObj);
					                valueTV.setText(beanObj.getLabel());
					                fieldArr.add(beanObj.getField());
					                valueTV.setId(idNo);
					                idNo++;
					                valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueTV);
					                CusFntTextViewArr.add(valueTV);
					                
					                CustomEditText valueEDT = new CustomEditText(billObj);
					                valueEDT.setId(idNo);
					                //valueEDT.setInputType(InputType.TYPE_NULL);
					                valueEDT.setKeyListener(null);
					                valueEDT.setEnabled(false);
					                idNo++;
					                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							        //params.setMargins(left, top, right, bottom);
							        params.setMargins(0,0,0,15);
							        valueEDT.setLayoutParams(params);
							        
					                //valueEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					                linear_layout.addView(valueEDT);
					                CustomEditTextArr.add(valueEDT);
					        	}
					        }
				        
					        CusFntTextView dbtAccTV = new CusFntTextView(billObj);
					        dbtAccTV.setText("Debit Account*");
			                fieldArr.add("DEBIT_ACCNT");
			                dbtAccTV.setId(idNo);
			                idNo++;
			                dbtAccTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                linear_layout.addView(dbtAccTV);
			                CusFntTextViewArr.add(dbtAccTV);
			                
			                dbtAccSpnr = new Spinner(billObj);
			                dbtAccSpnr.setBackgroundResource(R.mipmap.rounded_corner_spinner);
			                dbtAccSpnr.setId(idNo);
			                idNo++;
			                //dbtAccSpnr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                LinearLayout.LayoutParams spnrParams=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					        spnrParams.setMargins(0,0,0,15);
					        dbtAccSpnr.setLayoutParams(spnrParams);
					       
					        accountEDT = new CustomEditText(billObj);
					        CustomEditTextArr.add(accountEDT);
					        
			                linear_layout.addView(dbtAccSpnr);
			                
			                CusFntTextView balanceTV = new CusFntTextView(billObj);
			                balanceTV.setText("Balance");
			                fieldArr.add("BALANCE");
			                balanceTV.setId(idNo);
			                idNo++;
			                balanceTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                linear_layout.addView(balanceTV);
			                CusFntTextViewArr.add(balanceTV);
			                
			                balanceEDT = new CustomEditText(billObj);
			                balanceEDT.setId(idNo);
			                balanceEDT.setEnabled(false);
			                idNo++;
			                //balanceEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                LinearLayout.LayoutParams balParams=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					        balParams.setMargins(0,0,0,15);
			                balanceEDT.setLayoutParams(balParams);
			                linear_layout.addView(balanceEDT);
			                CustomEditTextArr.add(balanceEDT);
			                
			                if(catcd.equalsIgnoreCase("PREPAID MOBILE"))
			                {
			                	viewPlanTV.setText("View Plan");
				                viewPlanTV.setGravity(Gravity.CENTER_HORIZONTAL);
				                viewPlanTV.setPaintFlags(viewPlanTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				                fieldArr.add("RECHPLANID");
				                viewPlanTV.setId(idNo);
				                viewPlanId=idNo;
				                idNo++;
				                //viewPlanTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));

				                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
						        //params.setMargins(left, top, right, bottom);
						        params.setMargins(0,5,0,15);
						        viewPlanTV.setLayoutParams(params);
						        
				                linear_layout.addView(viewPlanTV);
				                CusFntTextViewArr.add(viewPlanTV);
				                
				                planIdEDT = new CustomEditText(billObj);
				                planIdEDT.setInputType(InputType.TYPE_CLASS_NUMBER);
				                planIdEDT.setId(idNo);
				                idNo++;
				                //planIdEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				                //linear_layout.addView(planIdEDT);
				                CustomEditTextArr.add(planIdEDT);
			                }
			                
			        		CusFntTextView rchrgAmntTV = new CusFntTextView(billObj);
			        		rchrgAmntTV.setText("Recharge Amount*");
			        		
			                fieldArr.add("AMOUNT");
			                rchrgAmntTV.setId(idNo);
			                idNo++;
			                rchrgAmntTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                linear_layout.addView(rchrgAmntTV);
			                CusFntTextViewArr.add(rchrgAmntTV);
			                
			                rchrgEDT = new CustomEditText(billObj);
			                rchrgEDT.setInputType(InputType.TYPE_CLASS_NUMBER);
			                rchrgEDT.setId(idNo);
			                idNo++;
			                rchrgEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                linear_layout.addView(rchrgEDT);
			                CustomEditTextArr.add(rchrgEDT);
			                
					        btn_submit.setText("Submit");
					        btn_submit.setId(idNo);
					        saveBtnId=idNo;
					        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					        //params.setMargins(left, top, right, bottom);
					        params.setMargins(0,15,0,5);
					        btn_submit.setLayoutParams(params);
					        linear_layout.addView(btn_submit);
					      
					        CusFntTextView noteTV = new CusFntTextView(billObj);
					        noteTV.setText("Amount Shown Are In Rs.");
					        noteTV.setTextColor(R.color.red_color);
					        noteTV.setGravity(Gravity.CENTER_HORIZONTAL);
					        noteTV.setTextSize(12);
			                noteTV.setId(idNo);
			                idNo++;
			                noteTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                linear_layout.addView(noteTV);
			                
			                
					        //if(chkConnectivity()==0)
					        	new CallWebServiceFetchPayee().execute();
					        //else
					        	//showAlert(billObj.getString(R.string.alert_000));
					        
					        addAccounts(stringValue);
					    }
					}
				}
				catch (Exception je) {
					
		            je.printStackTrace();
		        }
			}// end onPostExecute
		}// end CallWebServiceFetchBiller
	
	class CallWebServiceFetchPayee extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);
		String[] xmlTags = {"PARAMS"};
	    String[] valuesToEncrypt = new String[1];
	    JSONObject jsonObj = new JSONObject();
		String generatedXML = "";

		@Override
		protected void onPreExecute() 
		{
			try
			{
				loadProBarObj.show();
				jsonObj.put("CUSTID", custId);
	            jsonObj.put("CATEGORY", catcd);
	            jsonObj.put("BILLERCD", billerCd);
	            jsonObj.put("BILLERACCID", billerAccId);
	            jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(billObj));
	            //Log.e("CATEGORY","=catcd in preExcute="+cat);
			}
			catch (JSONException je) 
			{
	            je.printStackTrace();
	        }
		    valuesToEncrypt[0] = jsonObj.toString();
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
			Log.e("CATEGORY","=generatedXML===="+jsonObj.toString());
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			System.out.println("============= inside doInBackground =================");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);

			SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_PAYEE);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,15000);
			System.out.println("============= inside doInBackground 2 =================");
			/*try 
			{
				androidHttpTransport.call(SOAP_ACTION, envelope);
				System.out.println(envelope.bodyIn.toString());
				retval = envelope.bodyIn.toString().trim();
				System.out.println("FdRdAccountDetail    retval-----"+ retval);
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				retval = retval.substring(pos + 1, retval.length() - 3);
				System.out.println("FdRdAccountDetail    retval AFTER SUBSTR-----"+ retval);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("FdRdAccountDetail   Exception" + e);
			}*/
			return null;
		}
			
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			
			String[] xmlTags = { "PARAMS" };
			String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
			String decryptedRetVal ="";
			if(catcd.equalsIgnoreCase("PREPAID MOBILE"))
			{
				 decryptedRetVal = "{\"MOBILE_NO\":\"9970099700\",\"CIRCLE\":\"14--Maharashtra and Goa\",\"ACC_NAME\":\"shri3\"}";
			}
			else if(catcd.equalsIgnoreCase("PREPAID DTH"))
			{
				decryptedRetVal ="{\"CUST_ID\":\"254568956\",\"ACC_NAME\":\"dhata dth\"}";
			}
			
			try
			{	
				if(!decryptedRetVal.equalsIgnoreCase("NODATA"))
				{
					JSONObject jObj=new JSONObject(decryptedRetVal);
					for(int i=0;i<fieldArr.size();i++)
					{
						if(jObj.has(fieldArr.get(i)))
						{	
							CustomEditTextArr.get(i).setText(jObj.getString(fieldArr.get(i)));
						}
						if(!fieldArr.get(i).equalsIgnoreCase("AMOUNT"))
							CustomEditTextArr.get(i).setEnabled(false);
					}
				}
				else
				{
					showAlert("Payee Details Not Found Please Try After Some Time!");
				}
			}
			catch (Exception je) 
			{
	            je.printStackTrace();
	        }
		}// end onPostExecute
	}// end CallWebServiceFetchPayee
	
	class CallWebServiceViewRechargePlan extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);
		String[] xmlTags = {"PARAMS"};
	    String[] valuesToEncrypt = new String[1];
	    JSONObject jsonObj = new JSONObject();
		String generatedXML = "";

		@Override
		protected void onPreExecute() 
		{
			try
			{
				loadProBarObj.show();
				jsonObj.put("CUSTID", custId);
	            jsonObj.put("CATEGORY", catcd);
	            jsonObj.put("BILLERCD", billerCd);
	            jsonObj.put("BILLERACCID", billerAccId);
	            jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(billObj));
	            Log.e("CATEGORY","CusFntTextViewArr=="+CusFntTextViewArr.size()+"==fieldArr=="+fieldArr.size()+"==CustomEditTextArr=="+CustomEditTextArr.size());
	            for(int i=0; i < CusFntTextViewArr.size(); i++)
                {
	            	jsonObj.put(fieldArr.get(i), CustomEditTextArr.get(i).getText().toString());
                }
			}
			catch (JSONException je) 
			{
	            je.printStackTrace();
	        }
		    valuesToEncrypt[0] = jsonObj.toString();
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
			Log.e("CATEGORY","=generatedXML===="+jsonObj.toString());
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			System.out.println("============= inside doInBackground =================");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);

			/*SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_PLAN);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,15000);
			System.out.println("============= inside doInBackground 2 =================");
			try 
			{
				androidHttpTransport.call(SOAP_ACTION, envelope);
				System.out.println(envelope.bodyIn.toString());
				retval = envelope.bodyIn.toString().trim();
				System.out.println("FdRdAccountDetail    retval-----"+ retval);
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				retval = retval.substring(pos + 1, retval.length() - 3);
				System.out.println("FdRdAccountDetail    retval AFTER SUBSTR-----"+ retval);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("FdRdAccountDetail   Exception" + e);
			}*/
			return null;
		}
			
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			
			String[] xmlTags = { "PARAMS" };
			//String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
			String decryptedRetVal ="SUCCESS";//xml_data[0];
			try
			{	
				Log.e("BILLPAYMENT","decryptedRetVal=="+decryptedRetVal);
				if(!decryptedRetVal.equalsIgnoreCase("NODATA"))
				{
					JSONObject jObj=new JSONObject();//decryptedRetVal);
					jObj.put("RESPCODE", "0");
					JSONArray jArr1=new JSONArray();
					
					JSONObject jObj1=new JSONObject();
					jObj1.put("amount", "100");
					jObj1.put("plan_description", "Full Talktime");
					jObj1.put("planid", "1");
					jObj1.put("validity", "30 Days");
					jObj1.put("talktime", "0");
					jObj1.put("circleId", "14");
					jObj1.put("planCategoryName", "Recommended");
					jObj1.put("billerId", "VODAFONPRE");
					jArr1.put(jObj1);
					
					JSONObject jObj2=new JSONObject();
					jObj2.put("amount", "200");
					jObj2.put("plan_description", "200 Talktime");
					jObj2.put("planid", "2");
					jObj2.put("validity", "45 Days");
					jObj2.put("talktime", "0");
					jObj2.put("circleId", "14");
					jObj2.put("planCategoryName", "Recommended");
					jObj2.put("billerId", "VODAFONPRE");
					jArr1.put(jObj2);
					
					JSONObject jObj3=new JSONObject();
					jObj3.put("amount", "250");
					jObj3.put("plan_description", "250 Talktime");
					jObj3.put("planid", "3");
					jObj3.put("validity", "60 Days");
					jObj3.put("talktime", "0");
					jObj3.put("circleId", "14");
					jObj3.put("planCategoryName", "Recommended");
					jObj3.put("billerId", "VODAFONPRE");
					jArr1.put(jObj3);
					
					JSONObject jObj4=new JSONObject();
					jObj4.put("amount", "250");
					jObj4.put("plan_description", "1 GB data");
					jObj4.put("planid", "4");
					jObj4.put("validity", "30 Days");
					jObj4.put("talktime", "0");
					jObj4.put("circleId", "14");
					jObj4.put("planCategoryName", "Top Up");
					jObj4.put("billerId", "VODAFONPRE");
					jArr1.put(jObj4);
					
					JSONObject jObj5=new JSONObject();
					jObj5.put("amount", "350");
					jObj5.put("plan_description", "1.5 GB data");
					jObj5.put("planid", "4");
					jObj5.put("validity", "45 Days");
					jObj5.put("talktime", "0");
					jObj5.put("circleId", "14");
					jObj5.put("planCategoryName", "Top Up");
					jObj5.put("billerId", "VODAFONPRE");
					jArr1.put(jObj5);
					
					JSONObject jObj6=new JSONObject();
					jObj6.put("amount", "200");
					jObj6.put("plan_description", "1 GB data for 30 days");
					jObj6.put("planid", "4");
					jObj6.put("validity", "30 Days");
					jObj6.put("talktime", "0");
					jObj6.put("circleId", "14");
					jObj6.put("planCategoryName", "Special Recharge");
					jObj6.put("billerId", "VODAFONPRE");
					jArr1.put(jObj6);
					
					JSONObject jObj7=new JSONObject();
					jObj7.put("amount", "300");
					jObj7.put("plan_description", "1.5 GB data for 45 days");
					jObj7.put("planid", "4");
					jObj7.put("validity", "45 Days");
					jObj7.put("talktime", "0");
					jObj7.put("circleId", "14");
					jObj7.put("planCategoryName", "Special Recharge");
					jObj7.put("billerId", "VODAFONPRE");
					jArr1.put(jObj7);
					
					jObj.put("RETVAL", jArr1);
					
					if(jObj.getString("RESPCODE").equalsIgnoreCase("0"))
					{
						rechargePlanBeanArr=new ArrayList<RechargePlanBean>();
						//JSONObject planObj=jObj.getJSONObject("RETVAL");
							
						JSONArray jArr=jObj.getJSONArray("RETVAL");
						//Iterator<?> keys = planObj.getJSONObject("RechargeCategoryList").keys();

						//while(keys.hasNext()) 
						//{
						//    String key = (String) keys.next();
						for(int i=0;i<jArr.length();i++)
						{
							JSONObject jsonObj=jArr.getJSONObject(i);
						    rechargeTags.add(jsonObj.getString("planCategoryName"));
						    
						    //JSONArray jArr=planObj.getJSONObject("RechargeCategoryList").getJSONArray(key);
						    
								
							RechargePlanBean bean=new RechargePlanBean(); 
							
							bean.setMrp(jsonObj.getString("amount"));
							bean.setDescription(jsonObj.getString("plan_description"));
							bean.setPlanId(jsonObj.getString("planid"));
							bean.setValidity(jsonObj.getString("validity"));
							bean.setTalktime(jsonObj.getString("talktime"));
							bean.setCircleId(jsonObj.getString("circleId"));
							bean.setPlanCategoryName(jsonObj.getString("planCategoryName"));
							bean.setBillerId(jsonObj.getString("billerId"));
							bean.setRechargeType(jsonObj.getString("planCategoryName"));
							rechargePlanBeanArr.add(bean);
						}
						//}
						
						ViewRechargeDialogBox dialogBox=new ViewRechargeDialogBox(billObj, rechargePlanBeanArr, rchrgEDT);
						dialogBox.show();
						//Log.e("BILLPAYMENT","planObj=="+planObj);
					}
					else if(jObj.getString("RESPCODE").equalsIgnoreCase("1"))
					{
						showAlert(jObj.getString("ERRMSG"));
					}
					else if(jObj.getString("RESPCODE").equalsIgnoreCase("2"))
					{
						showAlert("Problem At Server, Please Try After Some Time");
					}
				}
				else
				{
					showAlert("Payee Details Not Found Please Try After Some Time!");
				}
			}
			catch (Exception je) {
	            je.printStackTrace();
	        }
		}// end onPostExecute
	}// end CallWebServiceViewRechargePlan

	class CallWebServiceValidateRecharge extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(BillPayment.this);

		String[] xmlTags = { "PARAMS" };
		String[] valuesToEncrypt = new String[1];
		String generatedXML = "";
		
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			try 
			{
				billJsonObj = new JSONObject();
				billJsonObj.put("BILLERACCID", billerAccId);
				billJsonObj.put("CUSTID", custId);
				billJsonObj.put("CATEGORY", catcd);
				billJsonObj.put("IMEINO", MBSUtils.getImeiNumber(billObj));
				billJsonObj.put("BILLERCD", billerCd);
				
				
				Log.e("BILLPAYMENT","CusFntTextViewArr.size()=="+CusFntTextViewArr.size()+"==fieldArr==="+fieldArr.size()+"==CustomEditTextArr==="+CustomEditTextArr.size());
				for(int i=0; i < CusFntTextViewArr.size(); i++)
                {
					Log.e("BILLPAYMENT","fieldArr.get(i)=="+fieldArr.get(i));
					billJsonObj.put(fieldArr.get(i), CustomEditTextArr.get(i).getText().toString());
                }
			} 
			catch (JSONException ex) 
			{
				ex.printStackTrace();
			}

			valuesToEncrypt[0] = billJsonObj.toString();

			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
		}

		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);

			try {
				/*SoapObject request = new SoapObject(NAMESPACE,METHOD_VALIDATE_RCHRG);
				request.addProperty("Params", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);

				if (androidHttpTransport != null)
					System.out
							.println("=============== androidHttpTransport is not null ");
				else
					System.out
							.println("=============== androidHttpTransport is  null ");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				retVal = envelope.bodyIn.toString().trim();
				retVal = retVal.substring(retVal.indexOf("=") + 1,
						retVal.length() - 3);*/
			}// end try
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) 
		{
			//String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
			String retStr="SUCCESS";//xml_data[0];
			
			loadProBarObj.dismiss();
			try 
			{
				if(!retStr.contains("NODATA")) 
				{
					JSONObject jObj=new JSONObject();//xml_data[0]);
					jObj.put("RESPCODE", "0");
					jObj.put("PAYMNTID", "10");
					if(jObj.getString("RESPCODE").equalsIgnoreCase("0"))
					{
						paymntId=jObj.getString("PAYMNTID");
						//if(chkConnectivity()==0)
							new CallWebServiceRecharge().execute();
						//else
						//	showAlert(billObj.getString(R.string.alert_000));
					}
					else
					{
						showAlert(jObj.getString("ERRMSG"));
					}
				} 
				else 
				{

				}

			} catch (Exception je) {
				je.printStackTrace();
			}

		}// end onPostExecute

	}// end CallWebServiceValidateRecharge
	
	class CallWebServiceRecharge extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);

		String[] xmlTags = { "PARAMS" };
		String[] valuesToEncrypt = new String[1];
		String generatedXML = "";
		JSONObject jsonObj = new JSONObject();

		protected void onPreExecute() {
			loadProBarObj.show();
			try 
			{
				billJsonObj.put("USERID", custId);
				billJsonObj.put("MMID", mmId);
				billJsonObj.put("IMEINO", MBSUtils.getImeiNumber(billObj));
				billJsonObj.put("PAYMNTID", paymntId);
				billJsonObj.put("PAYMNTTYPE", "RNP");
				billJsonObj.put("PAYMNTCHNLID", "MOBILE");
				billJsonObj.put("BANKREFNO", "132345667");
				billJsonObj.put("CARDTYPE", "IM");
				billJsonObj.put("OS", Build.VERSION.RELEASE);
				billJsonObj.put("APP", billObj.getString(R.string.app_name));
				billJsonObj.put("CUST_MOBNO", custMobNo);
				billJsonObj.put("IP", getLocalIpAddress());	
				billJsonObj.put("CHANNEL", "MOBB");
				billJsonObj.put("TRANMPIN", encrptdTranMpin);

			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			Log.e("RECHARGE","json=="+billJsonObj.toString());
			valuesToEncrypt[0] = billJsonObj.toString();
			System.out.println("&&&&&&&&&& billJsonObj.toString()== " +mpinStr);
			System.out.println("&&&&&&&&&& billJsonObj.toString()== " + billJsonObj.toString());
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
		}

		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);

			try {
				/*SoapObject request = new SoapObject(NAMESPACE,
						METHOD_RCHRG);
				request.addProperty("Params", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);

				if (androidHttpTransport != null)
					System.out
							.println("=============== androidHttpTransport is not null ");
				else
					System.out
							.println("=============== androidHttpTransport is  null ");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				retVal = envelope.bodyIn.toString().trim();
				retVal = retVal.substring(retVal.indexOf("=") + 1,
						retVal.length() - 3);*/
			}// end try
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) 
		{
			//String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
			String reStr="SUCCESS";//xml_data[0];

			loadProBarObj.dismiss();
			try {
				if(!reStr.contains("NODATA")) 
				{
					JSONObject jObj=new JSONObject();//xml_data[0]);
					jObj.put("RESPCODE", "0");
					jObj.put("STATUS", "SUCCESS");
					jObj.put("PAYMNTID", "10");
					if(jObj.getString("RESPCODE").equalsIgnoreCase("0"))
					{
						flg=true;
						if(jObj.getString("STATUS").equalsIgnoreCase("SUCCESS"))
							showAlert(billObj.getString(R.string.alert_rchrg_success)+" "+jObj.getString("PAYMNTID"));
						else if(jObj.getString("STATUS").equalsIgnoreCase("PENDING"))
							showAlert(billObj.getString(R.string.alert_rchrg_pending)+" "+jObj.getString("PAYMNTID"));
						else if(jObj.getString("STATUS").equalsIgnoreCase("FAILED"))
							showAlert(billObj.getString(R.string.alert_rchrg_failed)+" "+jObj.getString("PAYMNTID")+" "+billObj.getString(R.string.alert_refund));
					}
					else
					{
						flg=false;
						showAlert(jObj.getString("ERRMSG"));
					}
				} 
				else 
				{

				}

			} catch (Exception je) {
				je.printStackTrace();
			}

		}// end onPostExecute

	}// end CallWebServiceRecharge
	
	public class ViewRechargeDialogBox extends Dialog implements OnClickListener,OnItemClickListener {
		Activity activity;
		ListView rechargeplanlist;
		Button btn_talktime,btn_topup,btn_select,btn_close,btnOk;
		String strmpin = "";
		ArrayList<RechargePlanBean> rechargePlanBeanArr;
		ArrayList<RechargePlanBean> rechargePlanArr=new ArrayList<RechargePlanBean>();
		CustomEditText rechargeAmt;
		boolean flg;
		HorizontalListView horizontalListview;

		public ViewRechargeDialogBox(Activity activity, ArrayList<RechargePlanBean> rechargePlanBeanArr, CustomEditText rechargeAmt) {
			super(activity);
			this.rechargePlanBeanArr=rechargePlanBeanArr;
			this.rechargeAmt=rechargeAmt;
		}// end ViewRechargeDialogBox

		protected void onCreate(Bundle bdn) {
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			//setContentView(R.layout.recharge_viewplan);
			setContentView(R.layout.recharge_plan);
			
			rechargeplanlist=(ListView)findViewById(R.id.rechargeplanlist);
			horizontalListview=(HorizontalListView)findViewById(R.id.list_tags);
			//btn_talktime = (Button) findViewById(R.id.btn_talktime);
			//btn_topup = (Button) findViewById(R.id.btn_topup);
			//btn_close = (Button) findViewById(R.id.btn_close);
			
			//btn_talktime.setOnClickListener(this);
			//btn_topup.setOnClickListener(this);
			//btn_close.setOnClickListener(this);
			
			//btn_topup.setBackgroundResource(R.color.blur_buttonlist);
			ArrayList<String> rchrgList = new ArrayList<String>(rechargeTags);
			String selectedType = rchrgList.get(0);
			rechargeType=selectedType;
			rechargePlanArr=new ArrayList<RechargePlanBean>();
			for(int i=0;i<rechargePlanBeanArr.size();i++)
			{
				if(rechargePlanBeanArr.get(i).getRechargeType().equalsIgnoreCase(rechargeType))
	        	{
					rechargePlanArr.add(rechargePlanBeanArr.get(i));
	        	}
			}
			HorizontalviewAdapter adapter=new HorizontalviewAdapter(billObj, rchrgList);
			horizontalListview.setAdapter(adapter);
			horizontalListview.setOnItemClickListener(this);
			//rechargeplanlist.setOnItemClickListener(this);
			
			CustomAdapterForRechargePlan rchrgAdapter=new CustomAdapterForRechargePlan(billObj, rechargePlanArr,rechargeType);
			rechargeplanlist.setAdapter(rchrgAdapter);
			rechargeplanlist.setOnItemClickListener(this); 
		}

		@Override
		public void onClick(View v) 
		{
			
		}// end onClick


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) 
		{
			if(R.id.rechargeplanlist==parent.getId())
			{
				//Toast.makeText(act, ""+rechargePlanArr.get(position).getMrp(), Toast.LENGTH_SHORT).show();
				rechargeAmt.setText(rechargePlanArr.get(position).getMrp());
				planIdEDT.setText(rechargePlanArr.get(position).getPlanId());
				this.dismiss();
			}
			else
			{
				//view.setBackgroundColor(Color.BLACK);
				//Log.e("BillPay","horizonatl list clicked=="+position+"===="+rechargeTags.get(position));
				ArrayList<String> rchrgList = new ArrayList<String>(rechargeTags);
				String selectedType = rchrgList.get(position);
				
				rechargeType=selectedType;//rechargeTags.get(position);
				rechargePlanArr=new ArrayList<RechargePlanBean>();
				for(int i=0;i<rechargePlanBeanArr.size();i++)
				{
					if(rechargePlanBeanArr.get(i).getRechargeType().equalsIgnoreCase(rechargeType))
		        	{
						rechargePlanArr.add(rechargePlanBeanArr.get(i));
		        	}
				}
				CustomAdapterForRechargePlan adapter=new CustomAdapterForRechargePlan(billObj, rechargePlanArr,rechargeType);
				rechargeplanlist.setAdapter(adapter);
			}
			
		}
	}// end ViewRechargeDialogBox
	
	public void showAlert1(String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		GetMMIDDialog alert = new GetMMIDDialog(billObj, "" + str);
		alert.show();
	}
	
	public class GetMMIDDialog extends Dialog implements OnClickListener {

		private Context activity;
		private Dialog d;
		private Button ok, no;
		private TextView txt_message;
		public String textMessage;

		public GetMMIDDialog(Context activity, String textMessage) {
			super(activity);
			this.textMessage = textMessage;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.custom_alert_dialog);
			ok = (Button) findViewById(R.id.btn_yes);
			no = (Button) findViewById(R.id.btn_no);
			txt_message = (TextView) findViewById(R.id.txt_dia);
			txt_message.setText(textMessage);
			ok.setOnClickListener(this);
			no.setOnClickListener(this);
		}// end onCreate

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_yes:
				/*
				 * FragmentManager fragmentManager; Fragment fragment = new
				 * GenerateMMID(act);
				 * act.setTitle(getString(R.string.lbl_mmid)); fragmentManager =
				 * getFragmentManager(); fragmentManager.beginTransaction()
				 * .replace(R.id.frame_container, fragment).commit();
				 */
				Log.e("onClick", "btn_yes");
				Log.e("onClick", "btn_yes");
				Log.e("onClick", "btn_yes");
				getMMID();

				// break;
			case R.id.btn_no:
				this.dismiss();
				break;
			default:
				break;
			}
			dismiss();
		}
	}// end GetMMIDDialog
	
	public void getMMID() 
	{
		Log.e("In getMMID ", "In getMMID");

		try 
		{
			System.out.println("--------------- 44 ------------");
			flag =0; //chkConnectivity();
			if (flag == 0) 
			{
				new CallWebServiceGetMMID().execute();
			}
			else
				showAlert(billObj.getString(R.string.alert_000));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in onClick:==" + e);

		}
	}
	
	class CallWebServiceGetMMID extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);
		String retval = "", mmid = "", accNumber = "";

		String[] xmlTags = { "PARAMS" };
		String[] valuesToEncrypt = new String[1];

		JSONObject jsonObj = new JSONObject();
		String generatedXML = "";

		@Override
		protected void onPreExecute() 
		{
			try 
			{
				Log.e("CallWebServiceGetMMID ", "onPreExecute");
				loadProBarObj.show();
				accNumber = dbtAccSpnr.getSelectedItem().toString();
				accNumber = accNumber.substring(0, 16);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("ACCOUNTNO", accNumber);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(billObj));
				Log.e("BillPayment ", "custid777=" + custId);
				
				valuesToEncrypt[0] = jsonObj.toString();
				generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
				Log.e("BillPayment ", "accountNo777=" + accNumber);
			} 
			catch (JSONException je) 
			{
				Log.e("JSONException ","JSONException "+je);
				je.printStackTrace();
			}
			
		
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
		
			Log.e("CallWebServiceGetMMID ", "doInBackground=");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);

			/*SoapObject request = new SoapObject(NAMESPACE, GET_MMID);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,15000);
		
			try 
			{
				androidHttpTransport.call(SOAP_ACTION, envelope);
				retval = envelope.bodyIn.toString().trim();
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				retval = retval.substring(pos + 1, retval.length() - 3);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("LoanAccountDetail   Exception" + e);
			}*/
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
			Log.e("CallWebServiceGetMMID ", "onPostExecute=");
			loadProBarObj.dismiss();
			String[] xml_data = CryptoUtil.readXML(retval,new String[] { "PARAMS" });
			String decryptedRetVal ="FAILED~NA~~Trace No sequence not found.~9819531069~0020001010008888~ KULKARNI SUBHASH  HARI";//xml_data[0];
			Log.e("Debug@decryptedRetVal", decryptedRetVal);
			Log.e("BillPayment ", "mmid=" + mmid);
			if (decryptedRetVal.indexOf("FAILED") > -1) 
			{
				String msg[] = decryptedRetVal.split("~");
				if (msg[1].equalsIgnoreCase("NA")) 
				{
					Log.e("BillPayment ", "msg[2].length= " + msg[2].length());
					if (msg[2].length() > 0) 
					{
						req_id = msg[2];
						Log.e("BillPayment ", " Failed NA req_id=" + req_id);
						Log.e("BillPayment ", "Failed NA req_id=" + req_id);
						retMess = getString(R.string.alert_unableGenMMIDwithReq) + " " + req_id;
					} 
					else 
					{
						retMess = getString(R.string.alert_unablegenMMID);
					}
					showAlert(retMess);
				}
			} 
			else if (decryptedRetVal.indexOf("SUCCESS") > -1) 
			{
				String msg[] = decryptedRetVal.split("~");
				if (msg[1].equals("NA")) 
				{
					if (msg[2] != null || msg[2].length() > 0) 
					{
						req_id = msg[2];
						Log.e("BillPayment ", " Failed NA req_id=" + req_id);
						Log.e("BillPayment ", "Failed NA req_id=" + req_id);
						retMess = getString(R.string.alert_MMIDsmswithMob) + " " + req_id +" "+getString(R.string.alert_LogoutAndRelogin);
					}
					showAlert(retMess);
				} 
				else 
				{
					mmid = msg[1];
					Intent in=new Intent(billObj,Recharges.class);
					startActivity(in);
					billObj.finish(); 
					/*FragmentManager fragmentManager;
					Fragment fragment = new Recharges(act);
					Bundle b = new Bundle();
					act.setTitle(getString(R.string.lbl_fund_transfer));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
				}
			}
		}// end onPostExecute
	}// end CallWebServiceGetMMID
	
	public String getLocalIpAddress() {
		String ip="";
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                	ip = Formatter.formatIpAddress(inetAddress.hashCode());
	                    Log.e("BillPayment", "***** IP="+ ip);
	                    return ip;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("BillPayment", ex.toString());
	    }
	    return ip;
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

