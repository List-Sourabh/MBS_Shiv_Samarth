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

import mbLib.BillerBean;
import mbLib.CryptoUtil;
import mbLib.CusFntTextView;
import mbLib.CustomEditText;
import mbLib.DatabaseManagement;

import mbLib.MBSUtils;
import mbLib.MyThread;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class RemovePayee  extends Activity implements OnClickListener  
{
	MainActivity act;
	RemovePayee billObj;
	DatabaseManagement dbms;
	
	LinearLayout linear_layout,confirm_layout;
	ListView  lstRpt;
	Spinner spi_biller,dbtAccSpnr;
	ImageButton btn_back;
	ImageButton spinner_btn;
	TextView txt_heading,biller;
	Button btn_submit,btn_confirm;
	CusFntTextView viewPlanTV;
	CustomEditText balanceEDT,accountEDT;
	
	ArrayList<CusFntTextView>  CusFntTextViewArr=null;
	ArrayList<CustomEditText>  CustomEditTextArr=null;
	ArrayList<Button>  buttonArr=null;
	
	ArrayList<String> billerCdarr = new ArrayList<String>();
	ArrayList<String> fieldArr = new ArrayList<String>();
	ArrayList<String> arrListTemp = new ArrayList<String>();
	
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String METHOD_FETCH_BILLER = "fetchBiller";
	private static final String METHOD_FETCH_PAYEE="fetchPayeeDetails";
	private static final String METHOD_DELETE_PAYEE="deletePayee";
	public  ArrayList<BillerBean> billerBeanArray=null;
	String mpinStr="", custId="",imeiNo="",catcd="",retMess="",str="",billerCd="",billerAccId="",stringValue="",str2="",
			acnt_inf="",selectedAcc="",debitAccNo="",drBrnCD="",drSchmCD="",drAcNo="",encrptdTranMpin="",retVal="",
			paymntId="",rechargeType="",req_id = "",mmId="",custMobNo="",fromAct="";
	int flag=0,idNo=0,saveBtnId=0,confirmBtnId=0,viewPlanId=0;
	boolean flg=false;
	JSONObject billJsonObj;
	Accounts acArray[];
	String mobNo="",accNm="",customerId="";
	private MyThread t1;
	 PrivateKey var1 = null;
		String var5 = "", var3 = "";
		SecretKeySpec var2 = null;
	int timeOutInSecs=300;
	
	public RemovePayee(){}
	
	public RemovePayee(MainActivity a)
	{
		System.out.println("AddPayee()");
		act = a;
		billObj=this;
	}
	
	@Override
	public void onBackPressed() {
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
        txt_heading.setText(billObj.getString(R.string.lbl_remove_payee)); 
        imeiNo = MBSUtils.getImeiNumber(billObj);
        btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setImageResource(R.mipmap.backover);
        btn_back.setOnClickListener(this);
        var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
        
        spi_biller = (Spinner)findViewById(R.id.spi_biller);
        spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
        spinner_btn.setVisibility(ImageButton.INVISIBLE);
         
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
			fromAct = bundle.getString("FROMACT");
			catcd = bundle.getString("CATEGORY");
			billerCd = bundle.getString("BILLERCD");
			billerAccId = bundle.getString("BILLERACCID");
			
			if(fromAct.equalsIgnoreCase("BILL"))
			{
				customerId= bundle.getString("CONSUMERNO");
				accNm= bundle.getString("ACCNM");
			}
			else
			{
				mobNo= bundle.getString("MOBNO");
				accNm= bundle.getString("ACCNM");
				customerId= bundle.getString("CUST_ID");
			}	
			
		}
		Log.e("ccat","......"+catcd);
    	//if(chkConnectivity()==0)
        //{	
        	Log.e("CallWebServiceFetchBiller***","......");
        	new CallWebServiceFetchBillerField().execute();
       // }
       // else
       // {
        //	showAlert(act.getString(R.string.alert_000));
       // }
     
        	t1 = new MyThread(timeOutInSecs,this,var1,var3);
    		t1.start();
    }

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
                    		if(fromAct.equalsIgnoreCase("BILL"))
                    		{
                    			Intent in=new Intent(billObj,BillList.class);
                    			startActivity(in);
                    			billObj.finish(); 
                    			/*Fragment fragment1 =new BillList(act);// 
                    			FragmentManager fragmentManager1 = getFragmentManager();
                    			fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();*/
                    		}
                    		else
                    		{	
                    			Intent in=new Intent(billObj,Recharges.class);
                    			startActivity(in);
                    			billObj.finish(); 
                    		/*	Fragment fragment1 =new Recharges(act);// 
                    			FragmentManager fragmentManager1 = getFragmentManager();
                    			fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();*/
                    		}
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
	
	@Override
	public void onClick(View v) 
	{
		if(v.getId()==saveBtnId)
		{	
			InputDialogBox dialogObj= new InputDialogBox(billObj);
			dialogObj.show();
		} 
		else if(v.getId()==confirmBtnId)
		{
			InputDialogBox dialogObj= new InputDialogBox(billObj);
			dialogObj.show();
		}
		else if(v.getId()==R.id.btn_back)
		{
			Fragment fragment=null;
			if(fromAct.equalsIgnoreCase("BILL")){
				/*fragment = new BillList(act);*/
				Intent in=new Intent(billObj,RemoveBiller.class);
    			startActivity(in);
    			billObj.finish();
			}
			else{
				Intent in=new Intent(billObj,Recharges.class);
    			startActivity(in);
    			billObj.finish(); 
			}
			
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
				encrptdTranMpin =str; //ListEncryption.encryptData(custId + str);
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
				    	Log.e("CallWebServiceDeletePayee***","......"+encrptdTranMpin);
				       	new CallWebServiceDeletePayee().execute();
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

				/*SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_BILLER);

				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,20000);
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
				String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
				String decryptedRetVal="";
				
				if(fromAct.equalsIgnoreCase("BILL"))
				{
					if(catcd.equalsIgnoreCase("Electricity"))
						decryptedRetVal ="SUCCESS~BESTMU#BEST Mumbai~RELENG#Reliance Energy Limited~TATAMU#Tata Power Company Limited~TORNTBHVDI#Torrent Power Limited - Bhiwandi~MSEBMU#Maharashtra State Electricity Board~MSEBOB#MSEDCL@PROCESS_CYCLE#N#Y#Reliance Energy Limited#Process Cycle#^[0-9]{2}$~CONSUMER_NO#N#Y#Torrent Power Limited - Bhiwandi#Service Number#^[0-9]{11}$~CONSUMER_NO#N#Y#Maharashtra State Electricity Board#Consumer Number#^[0-9]{11}$~PROCESS_CYCLE#N#Y#Maharashtra State Electricity Board#Process Cycle#^[0-9]{2}$~BILLING_UNIT#N#Y#Maharashtra State Electricity Board#Billing Unit#^[0-9]{4}$~ACC_NAME#Y#N#Account Name#"; //xml_data[0];
					else if(catcd.equalsIgnoreCase("Insurance"))
						decryptedRetVal ="SUCCESS~SANGLINSU#Sangli Insurance~AEGONLIFE#Aegon Life insurance Company Ltd~AVIVALIFE#Aviva Life Insurance~AXAINS#Bharti AXA Life Insurance Company Limited~BALIC#Bajaj Allianz Life Insurance~BIRSUN#Aditya Birla Sun Life Insurance Company Limited~HDFCERGO#HDFC ERGO General Insurance~HDFCSL#HDFC Life Insurance Company Limited~HSBCLIFE#Canara HSBC OBC Life Insurance~ICIPRU#ICICI Prudential Life Insurance~INGLIF#Exide Life Insurance Company Limited~LICIND#Life Insurance Corporation of India~METLIFE#PNB Met Life India Insurance Company Limited~MNYL#MAX Newyork Life Insurance Company Limited~OMKMLI#Kotak Mahindra Old Mutual Life Insurance~RELLIFE#Reliance Life Insurance Company Limited~SBILIF#SBI Life Insurance Company Limited~TATAIG#TATA AIA Life Company Limited~ETLION#Edelweiss Tokio Life Insurance~sangli#miraj@CLIENT_ID#N#N#Aviva Life Insurance#Client Name#~CUST_NM#N#N#Bharti AXA Life Insurance Company Limited#Customer Name#~INSTL_PRMUM#N#N#Bajaj Allianz Life Insurance#Installment Premium#~CUST_NM#N#N#Bajaj Allianz Life Insurance#Customer Name#~CLIENT_ID#N#N#Aditya Birla Sun Life Insurance Company Limited#Client Id#~EXPRY_DT#N#N#HDFC ERGO General Insurance#Expiry Date#~DOB#N#N#HDFC Life Insurance Company Limited#Date Of Birth #~CUST_NM#N#N#Canara HSBC OBC Life Insurance#Customer Name#~CONTACT_NO#N#N#Canara HSBC OBC Life Insurance#Contact Number#~INSTL_PRMUM#N#N#ICICI Prudential Life Insurance#Installment Premium#~INSTL_PRMUM#N#N#Exide Life Insurance Company Limited#Installment Premium#~RCPT_TYPE#N#N#Life Insurance Corporation of India#Receipt Type#~MOBILE_NO#N#N#Life Insurance Corporation of India#Mobile Number#~INSTL_PRMUM#N#N#Life Insurance Corporation of India#Installment Premium#~EMAIL_ID#N#N#Life Insurance Corporation of India#Email ID#~INSTL_PRMUM#N#N#Kotak Mahindra Old Mutual Life Insurance#Installment Premium#~CLIENT_ID#N#N#Kotak Mahindra Old Mutual Life Insurance#Client Id#~DOB#N#N#Reliance Life Insurance Company Limited#Date Of Birth #~INSTL_PRMUM#N#N#SBI Life Insurance Company Limited#Installment Premium#~DOB#N#N#Edelweiss Tokio Life Insurance#Date Of Birth #~POLICY_NO#Y#Y#Policy Number#"; //xml_data[0];
					else if(catcd.equalsIgnoreCase("Telecom"))
						decryptedRetVal ="SUCCESS~AIRTLLMH#Airtel Telephone Maharashtra~AIRTMH#Airtel Mobile Maharashtra~BPLMMH#Vodafone Maharashtra~BSNLBULDH#Bharat Sanchar Nigam Limited, Buldhana~BSNLGO#Bharat Sanchar Nigam Limited , Goa~BSNLKLP#Bharat Sanchar Nigam Limited , Kolhapur~BSNLNA#Bharat Sanchar Nigam Limited,  Nagpur~BSNLPU#Bharat Sanchar Nigam Limited, Pune~DOCOMOMH#Tata Docomo GSM, Mumbai~SANGLTEL#Sangli Telcom Bill@RLTNSP_NO#N#Y#Airtel Telephone Maharashtra#Account Number#^[0-9]{8}$|^[0-9]{10}$~CONSUMER_NO#N#Y#Airtel Telephone Maharashtra#Telephone Number#^020[0-9]{8}$~ACC_NAME#N#N#Airtel Telephone Maharashtra#Account Name#~RLTNSP_NO#N#Y#Airtel Mobile Maharashtra#Relationship Number#^112-[0-9]{9}$|^[0-9]{10}$~CONSUMER_NO#N#Y#Airtel Mobile Maharashtra#Airtel Number#^[1-9]{1}[0-9]{9}$~ACC_NAME#N#N#Airtel Mobile Maharashtra#Account Name#~RLTNSP_NO#N#Y#Vodafone Maharashtra#Relationship Number#^[1-9]{1}[0-9]{9}$~CONSUMER_NO#N#Y#Vodafone Maharashtra#Vodafone Number#^[1-9]{1}[0-9]{9}$~ACC_NAME#N#N#Vodafone Maharashtra#Customer Name#^[0-9a-zA-Z\\s\\.]{2,100}$~RLTNSP_NO#N#Y#Bharat Sanchar Nigam Limited, Buldhana#Billing Account Number#^10[0-9]{8}$~CONSUMER_NO#N#Y#Bharat Sanchar Nigam Limited, Buldhana#Telephone Number#^72[0-9]{8}$~ACC_NAME#N#N#Bharat Sanchar Nigam Limited, Buldhana#Account Name#~RLTNSP_NO#N#Y#Bharat Sanchar Nigam Limited , Goa#Account Number#^10[0-9]{8}$~CONSUMER_NO#N#Y#Bharat Sanchar Nigam Limited , Goa#Telephone Number#^83[0-9]{8}$~ACC_NAME#N#N#Bharat Sanchar Nigam Limited , Goa#Account Name#~RLTNSP_NO#N#Y#Bharat Sanchar Nigam Limited , Kolhapur#Account Number#^10[0-9]{8}$~CONSUMER_NO#N#Y#Bharat Sanchar Nigam Limited , Kolhapur#Telephone Number#^23[0-9]{8}$~ACC_NAME#N#N#Bharat Sanchar Nigam Limited , Kolhapur#Account Name#~RLTNSP_NO#N#Y#Bharat Sanchar Nigam Limited,  Nagpur#Account Number#^10[0-9]{8}$~CONSUMER_NO#N#Y#Bharat Sanchar Nigam Limited,  Nagpur#Phone Number#^71[0-9]{8}$~ACC_NAME#N#N#Bharat Sanchar Nigam Limited,  Nagpur#Account Name#~RLTNSP_NO#N#Y#Bharat Sanchar Nigam Limited, Pune#Account Number#^10[0-9]{8}$~CONSUMER_NO#N#Y#Bharat Sanchar Nigam Limited, Pune#Telephone Number#^2[0-9]{9}$~ACC_NAME#N#N#Bharat Sanchar Nigam Limited, Pune#Account Name#~RLTNSP_NO#N#Y#Tata Docomo GSM, Mumbai#Relationship Number#^([1-9]{1}[0-9]{8})$~CONSUMER_NO#N#Y#Tata Docomo GSM, Mumbai#Tata Docomo Number#^[0-9]{10}$~ACC_NAME#N#N#Tata Docomo GSM, Mumbai#Account Name#";
				}
				else
				{
					if(catcd.equalsIgnoreCase("PREPAID MOBILE"))
					{
						decryptedRetVal = "SUCCESS~AIRCELPRE#AIRCEL PREPAID~AIRTELPRE#AIRTEL PREPAID~BSNLPRE#BSNL  PREPAID ~DOCOMOPRE#TATA DOCOMO GSM~IDEAPRE#IDEA PREPAID~MTNLDELPRE#MTNL DELHI PREPAID~MTNLMUMPRE#MTNL MUMBAI PREPAID~RIMGSMPRE#RELIANCE GSM PREPAID~UNINORPRE#TELENOR PREPAID~VODAFONPRE#VODAFONE PREPAID~JIOPRE#JIO PREPAID~VIDEOCNPRE#VIDEOCON PREPAID~MTSPRE#MTS PREPAID~TTSLPRE#TATA INDICOM PREPAID@MOBILE_NO#Y#Y#Mobile No#^[0-9]{10,12}$~ACC_NAME#Y#N#Account Name#~CIRCLE#Y#Y#Circle#^[0-9]{1,2}$";//xml_data[0];
					}
					else if(catcd.equalsIgnoreCase("PREPAID DTH"))
					{
						decryptedRetVal = "SUCCESS~AIRTELDTH#AIRTEL DTH~BIGTVDTH#BIG TV DTH~DISHTVDTH#DISH TV DTH~SUNTVDTH#SUN TV DTH~TATASKYDTH#TATASKY DTH~VIDEOCNDTH#VIDEOCON DTH@CUST_ID#N#Y#AIRTEL DTH#Customer Id#~CUST_ID#N#Y#BIG TV DTH#Smart Card Number#~CUST_ID#N#Y#DISH TV DTH#Viewing Card Number#~CUST_ID#N#Y#SUN TV DTH#Smart Card Number#~CUST_ID#N#Y#TATASKY DTH#Subscriber Number#~CUST_ID#N#Y#VIDEOCON DTH#Subscriber Id#~ACC_NAME#Y#N#Account Name#";//xml_data[0];
					}
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
						Log.e("billerStr",billerStr);
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
				            
					        btn_submit.setText("Remove");
					        btn_submit.setId(idNo);
					        saveBtnId=idNo;
					        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					        //params.setMargins(left, top, right, bottom);
					        params.setMargins(0,15,0,5);
					        btn_submit.setLayoutParams(params);
					        linear_layout.addView(btn_submit);
					        
					        //if(chkConnectivity()==0)
					        	new CallWebServiceFetchPayee().execute();
					        //else
					        //	showAlert(billObj.getString(R.string.alert_000));
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

			/*SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_PAYEE);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,20000);
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
			String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
			String decryptedRetVal="";
			
			if(fromAct.equalsIgnoreCase("BILL"))
			{
				decryptedRetVal="{\"POLICY_NO\":\"963258\",\"INSTL_PRMUM\":\"2650\",\"DOB\":\"29062018\",\"CLIENT_ID\":\"485\",\"ACC_NAME\":\"nitin\"}";
			}
			else{
				if(catcd.equalsIgnoreCase("PREPAID MOBILE"))
				{
					decryptedRetVal = "{\"MOBILE_NO\":\"9970971040\",\"ACC_NAME\":\"home\"}";
				}
				else if(catcd.equalsIgnoreCase("PREPAID DTH"))
				{
					decryptedRetVal = "{\"CUST_ID\":\"58448\",\"ACC_NAME\":\"mahesh\"}";//xml_data[0];
				}
			}
			try
			{	
				if(!decryptedRetVal.equalsIgnoreCase("NODATA"))
				{
					JSONObject jObj=new JSONObject();//decryptedRetVal);
					if(catcd.equalsIgnoreCase("PREPAID MOBILE") || catcd.equalsIgnoreCase("PREPAID DTH"))
					{
						jObj.put("MOBILE_NO", mobNo);
						jObj.put("ACC_NAME", accNm);
						jObj.put("CIRCLE", "14--Maharashtra");
						jObj.put("CUST_ID", customerId);
					}
					else if(catcd.equalsIgnoreCase("Electricity"))
					{
						jObj.put("PROCESS_CYCLE", "01");
						jObj.put("CONSUMER_NO", customerId);
						jObj.put("BILLING_UNIT", "6201");
						jObj.put("ACC_NAME", accNm);
					}
					else if(catcd.equalsIgnoreCase("Insurance"))
					{
						jObj.put("POLICY_NO", "36452169563");
						jObj.put("CUST_NM", accNm);
						jObj.put("INSTL_PRMUM", "1250");
						jObj.put("CLIENT_ID", billerCd);
						jObj.put("EXPRY_DT", "01/04/2021");
						jObj.put("CONTACT_NO", "9876543210");
						jObj.put("RCPT_TYPE", "A");
						jObj.put("MOBILE_NO", "9638527410");
						jObj.put("EMAIL_ID", "rajukadam@gmail.com");
						jObj.put("DOB", "01/06/1986");
						jObj.put("ACC_NAME", accNm);
					}
					else if(catcd.equalsIgnoreCase("Telecom"))
					{
						jObj.put("CONSUMER_NO", "02320136980");
						jObj.put("RLTNSP_NO", "3698513652");
						jObj.put("ACC_NAME", accNm);
					}
					
					for(int i=0;i<fieldArr.size();i++)
					{
						if(jObj.has(fieldArr.get(i)))
						{	
							CustomEditTextArr.get(i).setText(jObj.getString(fieldArr.get(i)));
						}
						if(!fieldArr.get(i).equalsIgnoreCase("RCHRG_AMNT"))
							CustomEditTextArr.get(i).setEnabled(false);
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
	}// end CallWebServiceFetchPayee
	
	class CallWebServiceDeletePayee extends AsyncTask<Void, Void, Void> 
	{
        LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        JSONObject jsonObj=new JSONObject();

        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
            	jsonObj.put("CUSTID",custId);
                jsonObj.put("IMEINO",MBSUtils.getImeiNumber(billObj));
                jsonObj.put("BILLERACCID",billerAccId);
                jsonObj.put("TRANMPIN",mpinStr);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
           
            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) 
        {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);

           /* try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_DELETE_PAYEE);
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new        SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
                        20000);

                if (androidHttpTransport != null)
                    System.out
                            .println("=============== androidHttpTransport is not null ");
                else
                    System.out
                            .println("=============== androidHttpTransport is  null ");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                retVal = envelope.bodyIn.toString().trim();
                retVal = retVal.substring(retVal.indexOf("=") + 1,
                        retVal.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Exception" + e);
            }*/
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            //String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });

            String decryptedCategory ="SUCCESS";// xml_data[0];
			//Log.e("decryptedCategory====", decryptedCategory);
        	
			flg=false;
            loadProBarObj.dismiss();
            try
            {
                if(!decryptedCategory.contains("FAILED#"))
                {
                	JSONObject jObj=new JSONObject();//xml_data[0]);
                	jObj.put("RESPCODE", "0");
					if(jObj.getString("RESPCODE").equalsIgnoreCase("0"))
					{
						flg=true;
						if(fromAct.equalsIgnoreCase("BILL"))
							showAlert(getString(R.string.alert_removebiller));
						else
							showAlert(getString(R.string.alert_payeemobremov_rech));
					}
					else
					{
						showAlert(jObj.getString("ERRMSG"));
					}
                }
	            else
	            {
	            	showAlert(getString(R.string.alert_removebillerfailed));
	            }
             }
            catch(Exception je)
            {
               
                je.printStackTrace();
            }

        }// end onPostExecute

    }// end CallWebService delete biller 
	
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

