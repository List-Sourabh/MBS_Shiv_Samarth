package list.shivsamarth_mbs;



import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import mbLib.BillerBean;
import mbLib.CryptoUtil;
import mbLib.CustomAdapterforPayeelist;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;

import mbLib.MBSUtils;
import mbLib.MyThread;
import mbLib.PayeeBean;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RemoveBiller extends Activity implements  OnItemClickListener,OnClickListener {

	MainActivity act;
	RemoveBiller billObj;
	Button btn_submit;
	LinearLayout linearLayout ;
	ListView remove_list;
	int selected=-1;
	int saveBtnId=-1;
	int idNo=1;
	int flag=0;
	public String encrptdTranMpin;
	ImageButton btn_home,btn_back;
	public  ArrayList<BillerBean> billerBeanArray=null;
	public  ArrayList<PayeeBean> payeeBeanArray=null;
	TextView txt_heading ;
	
	String retMess = "", retVal = "",imeiNo = "";
	String stringValue = "",custid="";//,accountNo="";
	String all_acnts = "", str2 = "", str = "",req_id="",custId = "",userId="",cust_mob_no="",category="BILLER";
	LinearLayout linear_layout,confirm_layout ;
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private static String METHOD_fetch_payee="fetchpayee";
	private static String METHOD_delete_biller="deletePayee";
	String consumercd="",billercd="",billername="",accname="",DOB="",mobno="",billerAccId="";
	private MyThread t1;
	int timeOutInSecs=300;
	DatabaseManagement dbms;
	ListView listbill;
	//HomeFragment homeFrag;
	Context context;
	 PrivateKey var1 = null;
		String var5 = "", var3 = "";
		SecretKeySpec var2 = null;
	private static final String MY_SESSION = "my_session";
	public RemoveBiller(){}
	
	public RemoveBiller(MainActivity a)
	{
		System.out.println("AddBiller()");
		act = a;
		billObj=this;
	}
	
	public void onBackPressed() 
	{
		return ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		billObj=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remove_biller);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        
        txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(billObj.getString(R.string.lbl_remove_bills));
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		remove_list=(ListView)findViewById(R.id.list_biller_remove);
		btn_back.setOnClickListener(this);
		//dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		
		linear_layout = (LinearLayout)findViewById(R.id.main_layout);
		remove_list.setOnItemClickListener(this);
		
		/*Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		Log.e("retValStr","...."+stringValue);
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);
	        	userId=c1.getString(3);
		    	Log.e("UserId","......"+userId);
		    	cust_mob_no=c1.getString(4);
		    	Log.e("cust_mobNO","..."+cust_mob_no);
	        }
        }*/
        

 	  // if(chkConnectivity()==0)
	  // {	
 		   Log.e("CallWebServiceFetchPayee***","......");
	       new CallWebServiceFetchPayee().execute();
	   //}
	  // else
	  // {
	  //     showAlert(act.getString(R.string.alert_000));
	  // }
        
	   	t1 = new MyThread(timeOutInSecs,this,var1,var3);
			t1.start();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
	}

	public int chkConnectivity() 
	{
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
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) 
					{
					}
					break;
				case DISCONNECTED:
					flag = 1;
					//retMess = "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					//retMess = "Network Unavailable. Please Try Again.";
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

			//Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			//Log.i("mayuri", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	class CallWebServiceFetchPayee extends AsyncTask<Void, Void, Void> {
        //String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        //String agentCd="1",memberId="";
        JSONObject jsonObj=new JSONObject();

        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
            
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY",category);
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(billObj));
             
            	Log.e("custId","--"+custId);
            	Log.e("MBSUtils.getImeiNumber(billObj)","--"+MBSUtils.getImeiNumber(billObj));
            	//Log.e("category","--"+category);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
           
            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);;//getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);

          /*  try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_fetch_payee );
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new        SoapSerializationEnvelope(SoapEnvelope.VER11);
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
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
           // xml_data[0]="{\"POLICY_NO\":\"963258\",\"INSTL_PRMUM\":\"2650\",\"DOB\":\"29-06-2018\",\"CLIENT_ID\":\"485\",\"ACC_NAME\":\"nitin\"}";
            String decryptedCategory = xml_data[0];
			Log.e("decryptedCategory=***===", decryptedCategory);
	        loadProBarObj.dismiss();
            try
            {
              //  JSONObject retJson = new JSONObject(xml_data[0]);
                if(!xml_data[0].contains("NODATA"))
                {
                JSONArray ja = new JSONArray("[{\"CATEGORY\":\"Insurance\",\"DOB\":\"2018-06-29 00:00:00.0\",\"BILLERCD\":\"AVIVALIFE\",\"BILLERNAME\":\"Aviva Life Insurance\",\"BILLERACCID\":\"30\",\"ACCNAME\":\"nitin\"},{\"CONSUMERNO\":\"25\",\"CATEGORY\":\"Telecom\",\"BILLERCD\":\"AIRTLLMH\",\"BILLERNAME\":\"Airtel Telephone Maharashtra\",\"BILLERACCID\":\"66\",\"ACCNAME\":\"babita\"},{\"CONSUMERNO\":\"258258\",\"CATEGORY\":\"Telecom\",\"BILLERCD\":\"AIRTMH\",\"BILLERNAME\":\"Airtel Mobile Maharashtra\",\"BILLERACCID\":\"65\",\"ACCNAME\":\"hello\"},{\"CONSUMERNO\":\"008\",\"CATEGORY\":\"Telecom\",\"BILLERCD\":\"DOCOMOMH\",\"BILLERNAME\":\"Tata Docomo GSM, Mumbai\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"SRK\"},{\"CONSUMERNO\":\"369\",\"CATEGORY\":\"Electricity\",\"BILLERCD\":\"MSEBMU\",\"BILLERNAME\":\"Maharashtra State Electricity Board\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"Arista dev\"},{\"CONSUMERNO\":\"369635\",\"CATEGORY\":\"Electricity\",\"BILLERCD\":\"MSEBMU\",\"BILLERNAME\":\"Maharashtra State Electricity Board\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"rama\"}]");
                
                billerBeanArray=new ArrayList<BillerBean>();
                payeeBeanArray=new ArrayList<PayeeBean>();
              
                for(int i = 0; i < ja.length(); i++) 
				{
                	 JSONObject jObj = ja.getJSONObject(i);
                     PayeeBean payeeBeanobj= new PayeeBean();
                    
                     if(jObj.has("CONSUMERNO"))
                     {
                    	  payeeBeanobj.setConsumerno(jObj.getString("CONSUMERNO"));
                     }
                     if(jObj.has("BILLERCD"))
                     {
                    	  payeeBeanobj.setBillercd(jObj.getString("BILLERCD"));
                     }
                     if(jObj.has("BILLERNAME"))
                     {
                    	 payeeBeanobj.setBillername(jObj.getString("BILLERNAME"));
                     }
                     if(jObj.has("ACCNAME"))
                     {
                    	 payeeBeanobj.setAccname(jObj.getString("ACCNAME"));
                     }
                     if(jObj.has("DOB"))
                     {
                    	 payeeBeanobj.setDob(jObj.getString("DOB"));
                     }
                     if(jObj.has("IMEINO"))
                     {
                    	 payeeBeanobj.setImei(jObj.getString("IMEINO"));
                     }
                     if(jObj.has("CATEGORY"))
                     {	 payeeBeanobj.setCategory(jObj.getString("CATEGORY"));
                     }
                     if(jObj.has("MOBNO"))
                     {
                    	 payeeBeanobj.setMobileno(jObj.getString("MOBNO"));
                     }
                     if(jObj.has("BILLERACCID"))
                     {
                    	 payeeBeanobj.setBillerAccId(jObj.getString("BILLERACCID"));
                     }
                    
                    payeeBeanArray.add(payeeBeanobj);
                     
                     
				}
	            if(ja.length()>0)
	            {
	            	CustomAdapterforPayeelist addpayeelist=new CustomAdapterforPayeelist(billObj, payeeBeanArray,category);
	            	remove_list.setAdapter(addpayeelist);
	            	remove_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	            	remove_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
						{
							
							Log.e("position--***-","888---"+ position);
							
							if(position>=0)
					     	{
								consumercd=payeeBeanArray.get(position).getConsumerno();
						    	billercd=payeeBeanArray.get(position).getBillercd();
						    	billername=payeeBeanArray.get(position).getBillername();
						    	accname=payeeBeanArray.get(position).getAccname();
						    	DOB=payeeBeanArray.get(position).getDob();
						    	mobno=payeeBeanArray.get(position).getMobileno();
						    	billerAccId=payeeBeanArray.get(position).getBillerAccId();
						    	category=payeeBeanArray.get(position).getCategory();
						    	Log.e("consumercd delete","......"+consumercd);
						     	Log.e("billercd delete","......"+billercd);
						     	Log.e("billername delete","......"+billername);
						     	Log.e("accname delete","......"+accname);
						     	Log.e("DOB delete","......"+DOB);
						     	Log.e("MOBNO delete","......"+mobno);
					     		//setAlert(act.getString(R.string.alert_remconfirm));
						     	Bundle b1=new Bundle();
						     	b1.putString("FROMACT","BILL");
	            				b1.putString("CATEGORY",category);
	            				b1.putString("BILLERACCID",billerAccId);
	            				b1.putString("BILLERCD",billercd);
	            				
	            				b1.putString("CONSUMERNO",consumercd);
	            				b1.putString("ACCNM",accname);
	            				
	            				Intent in=new Intent(billObj,RemovePayee.class);
	            				in.putExtras(b1);
	            				startActivity(in);
	            				billObj.finish(); 
	                            /*Fragment fragment =new RemovePayee(act);
	            				FragmentManager fragmentManager = getFragmentManager();
	                  			fragment.setArguments(b1);
	                  			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
					     	}
					     	else
					     	{
					     		showAlert("Please select the Biller");
					     	}
						}
						});
	                }
                }
            }
            catch(JSONException je)
            {
               
                je.printStackTrace();
            }

        }// end onPostExecute

    }// end CallWebServiceFetchPayee
	
	class CallWebServiceDeleteBiller extends AsyncTask<Void, Void, Void> {
        //String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(billObj);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        //String agentCd="1",memberId="";
        JSONObject jsonObj=new JSONObject();

        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
            	jsonObj.put("CUSTID",custId);
                jsonObj.put("CONSUMERNO",consumercd);
                jsonObj.put("BILLERCD",billercd);
                jsonObj.put("CATEGORY",category);
                jsonObj.put("MOBNO","");
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(billObj));
                jsonObj.put("BILLERACCID",billerAccId);
             
            	Log.e("custId","--"+custId);
            	Log.e("consumercd","--"+consumercd);
            	Log.e("billercd","--"+billercd);
            	//Log.e("category","--"+category);
            	Log.e("MBSUtils.getImeiNumber(billObj)","--"+MBSUtils.getImeiNumber(billObj));
            	//Log.e("category","--"+category);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
           
            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);;//getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);

            /*try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_delete_biller );
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new        SoapSerializationEnvelope(SoapEnvelope.VER11);
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
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
            
            String decryptedCategory = xml_data[0];
			//decryptedCategory=decryptedCategory.split("SUCCESS~")[1];
			
			Log.e("decryptedCategory====", decryptedCategory);
			
			Log.e("decryptedCategory=***===", decryptedCategory);
			//[{"CONSUMERNO":"101","BILLERCD":"AIRTELDTH","BILLERNAME":"AIRTEL DTH","ACCNAME":"sam"}]
            
            loadProBarObj.dismiss();
            try
            {
              //  JSONObject retJson = new JSONObject(xml_data[0]);
                if(!xml_data[0].contains("FAILED#"))
                {
                	showAlert(getString(R.string.alert_removebiller));
                
                	Intent in=new Intent(billObj,BillList.class);
    				
    				startActivity(in);
    				billObj.finish(); 
    				/*Fragment fragment1 =new BillList(act);// 
    				FragmentManager fragmentManager1 = getFragmentManager();
    				fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();*/
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
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_back:
				
				Intent in=new Intent(billObj,BillList.class);
				startActivity(in);
				billObj.finish(); 
				/*Fragment fragment1 =new BillList(act);// 
				FragmentManager fragmentManager1 = getFragmentManager();
				fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();*/
				break;

		default:
			break;
		}
	}

	public void setAlert(String str)
{
	CustomDialogClass alert =new CustomDialogClass(billObj,""+str)
	//ErrorDialogClass alert = new ErrorDialogClass(act,""+str)
	{
		@Override
		public void onClick(View v) 
		{
			
			switch (v.getId()) 
			{
				case R.id.btn_ok:
					
					
					InputDialogBox inputBox = new InputDialogBox(billObj);
					inputBox.show();
					 
               	 
				
				  break;
				case R.id.btn_cancel:
					dismiss();
					
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
		public void onClick(View v) 
		{
			if(v.getId()==R.id.btnOK)
			{	
				try 
				{
					String str = mpin.getText().toString().trim();
					encrptdTranMpin = str;//ListEncryption.encryptData(custId + str);
					
					if (str.length() == 0) 
					{
						retMess = getString(R.string.alert_enterTranMpin);
						showAlert(retMess);// setAlert();
						this.show();
					} 
					else if (str.length() != 6) {
						retMess = getString(R.string.alert_TranmipnMust6dig);
						showAlert(retMess);// setAlert();
						this.show();
					} 
					else 
					{
						//if(chkConnectivity()==0)
		             	   {	
		              		   Log.e("CallWebServiceDeleteBiller***","......");
		             	       new CallWebServiceDeleteBiller().execute();
		             	   }
		             	  /* else
		             	   {
		             	       showAlert(billObj.getString(R.string.alert_000));
		             	   }*/
		                   
						this.hide();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
				}
			}	
		}// end onClick
	}// end InputDialogBox

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
                 
                	
            }this.dismiss();

        }
	 };alert.show();
	
		
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

