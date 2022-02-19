package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.BillerBean;
import mbLib.CryptoUtil;
import mbLib.CustomAdapterforPayeelist;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;
import mbLib.PayeeBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class BillList extends Activity implements OnClickListener, OnItemClickListener{
	
	
	MainActivity act;
	BillList billObj;
	Button btn_submit;
	LinearLayout linearLayout ;
	ListView biller_list;
	int selected=-1;
	int saveBtnId=-1;
	int idNo=1;
	int flag=0;
	ImageButton btn_home,btn_back;
	Button btnAddBiller,btnRemoveBiller,btnHistory;
	TextView txt_heading ;
	ImageView img_heading;
	private MyThread t1;
	int timeOutInSecs=300;
	String retMess = "", retVal = "",imeiNo = "";
	String stringValue = "",custid="";//,accountNo="";
	String all_acnts = "", str2 = "", str = "",req_id="",custId = "",userId="",cust_mob_no="",category="BILLER";
	LinearLayout linear_layout,confirm_layout ;
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private static String METHOD_fetch_payee="fetchpayee";
	private static String METHOD_fetch_bipll="fetchbill";
	public  ArrayList<BillerBean> billerBeanArray=null;
	public  ArrayList<PayeeBean> payeeBeanArray=null;
	DatabaseManagement dbms;
	ListView listbill;
	//HomeFragment homeFrag;
	Context context;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	private static final String MY_SESSION = "my_session";
	public BillList(){}
	
	public BillList(MainActivity a)
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
		setContentView(R.layout.bill_list);		
		System.out.println("onCreateView()");
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(billObj.getString(R.string.lbl_bills));
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.bill);
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
	   
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		
		btnAddBiller=(Button)findViewById(R.id.btnAddBiller);
		biller_list=(ListView)findViewById(R.id.biller_list);
		btnRemoveBiller=(Button)findViewById(R.id.btnRemoveBiller);
		btnHistory=(Button)findViewById(R.id.btnHistory);
		btnRemoveBiller.setOnClickListener(this);
		btnAddBiller.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		//dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		
		linear_layout = (LinearLayout)findViewById(R.id.main_layout);
        confirm_layout = (LinearLayout) findViewById(R.id.confirm_layout);
        
        listbill = (ListView) findViewById(R.id.list_bill);
        listbill.setOnItemClickListener(this);
		
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
	  // }
	  // else
	  // {
	   //    showAlert(act.getString(R.string.alert_000));
	   //}
        
	       t1 = new MyThread(timeOutInSecs,this,var1,var3);
			t1.start();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) 
		{
			case R.id.btnAddBiller:	
				
				Intent in1 = new Intent(billObj, BillCategory.class);
     			startActivity(in1);
     			billObj.finish();
				/*act.frgIndex=101;
				Fragment fragment =new BillCategory(act);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
			break;
			
			case R.id.btnRemoveBiller:	
				Intent in3 = new Intent(billObj, RemoveBiller.class);
     			startActivity(in3);
     			billObj.finish();
				/*act.frgIndex=102;
				Fragment fragment1 =new RemoveBiller(act);// 
				FragmentManager fragmentManager1 = getFragmentManager();
				fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();*/
			break;
			case R.id.btn_back:
				Intent in=new Intent(billObj,DashboardActivity.class);
				startActivity(in);
				billObj.finish(); 
			break;
			case R.id.btnHistory:
				Bundle b1=new Bundle();
				b1.putString("FROMACT","BILL");
				Intent in2=new Intent(billObj,ViewHistory.class);
				in2.putExtras(b1);
				startActivity(in2);
				billObj.finish(); 
				/*Fragment fragment2 =new ViewHistory(act);// 
				fragment2.setArguments(b1);
				FragmentManager fragmentManager2 = getFragmentManager();
				fragmentManager2.beginTransaction().replace(R.id.frame_container, fragment2).commit();	*/		
	    	break;
		}
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
            	Log.e("MBSUtils.getImeiNumber(act)","--"+MBSUtils.getImeiNumber(billObj));
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

           /* try {
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
            xml_data[0]="[{\"CATEGORY\":\"Insurance\",\"DOB\":\"2018-06-29 00:00:00.0\",\"BILLERCD\":\"AVIVALIFE\",\"BILLERNAME\":\"Aviva Life Insurance\",\"BILLERACCID\":\"30\",\"ACCNAME\":\"nitin\"},{\"CONSUMERNO\":\"25\",\"CATEGORY\":\"Telecom\",\"BILLERCD\":\"AIRTLLMH\",\"BILLERNAME\":\"Airtel Telephone Maharashtra\",\"BILLERACCID\":\"66\",\"ACCNAME\":\"babita\"},{\"CONSUMERNO\":\"258258\",\"CATEGORY\":\"Telecom\",\"BILLERCD\":\"AIRTMH\",\"BILLERNAME\":\"Airtel Mobile Maharashtra\",\"BILLERACCID\":\"65\",\"ACCNAME\":\"hello\"},{\"CONSUMERNO\":\"008\",\"CATEGORY\":\"Telecom\",\"BILLERCD\":\"DOCOMOMH\",\"BILLERNAME\":\"Tata Docomo GSM, Mumbai\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"SRK\"},{\"CONSUMERNO\":\"58665869\",\"CATEGORY\":\"Electricity\",\"BILLERCD\":\"BESTMU\",\"BILLERNAME\":\"BEST Mumbai\",\"BILLERACCID\":\"33\",\"ACCNAME\":\"dummy2\"},{\"CONSUMERNO\":\"369\",\"CATEGORY\":\"Electricity\",\"BILLERCD\":\"MSEBMU\",\"BILLERNAME\":\"Maharashtra State Electricity Board\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"Arista dev\"},{\"CONSUMERNO\":\"369635\",\"CATEGORY\":\"Electricity\",\"BILLERCD\":\"MSEBMU\",\"BILLERNAME\":\"Maharashtra State Electricity Board\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"rama\"},{\"CONSUMERNO\":\"69\",\"CATEGORY\":\"Electricity\",\"BILLERCD\":\"RELENG\",\"BILLERNAME\":\"Reliance Energy Limited\",\"BILLERACCID\":\"60\",\"ACCNAME\":\"shiv\"}]";
            String decryptedCategory = xml_data[0];
			Log.e("decryptedCategory====", decryptedCategory);
			Log.e("decryptedCategory=***===", decryptedCategory);
            loadProBarObj.dismiss();
            try
            {
              //  JSONObject retJson = new JSONObject(xml_data[0]);
                if(!xml_data[0].contains("NODATA"))
                {
                JSONArray ja = new JSONArray(xml_data[0]);
                
               billerBeanArray=new ArrayList<BillerBean>();
                payeeBeanArray=new ArrayList<PayeeBean>();
                Log.e("BILLLIST","ja.length()=="+ja.length());
                for(int i = 0; i < ja.length(); i++) 
				{
                	 JSONObject jObj = ja.getJSONObject(i);
                	 //Log.e("*********","jObj.getString(BLLERACCID)"+jObj.getString("BILLERACCID"));
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
	                listbill.setAdapter(addpayeelist);
	                listbill.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	                listbill.setOnItemClickListener(new AdapterView.OnItemClickListener() 
	                {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
						{
							
							Log.e("position--***-",","+ position);
							
					        String cat = payeeBeanArray.get(position).getCategory();
					        String billeraccid=payeeBeanArray.get(position).getBillerAccId();
					        
					        Log.e("cat--***-",","+ cat);
					        Log.e("billeraccid--***-",","+ billeraccid);
					        Log.e("billeraccid--***-",","+ billeraccid);
					        
							
					        Bundle b1=new Bundle();
							b1.putInt("SELECTED", position);
							b1.putString("CATEGORY", payeeBeanArray.get(position).getCategory());
							b1.putString("BILLERCD", payeeBeanArray.get(position).getBillercd());
							b1.putString("BILLERACCID", billeraccid);
							

							//Fragment fragment = new BillPayment(act);
							
							Intent in2 = new Intent(billObj, PayBill.class);
							in2.putExtras(b1);
							startActivity(in2);
							billObj.finish();
							/*Fragment fragment = new PayBill(act);
							fragment.setArguments(b1);
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();*/
							
							/*if(cat.equalsIgnoreCase("Electricity"))
							{
								act.frgIndex=103;
								Log.e("in side  Electricity*****=","Spinner Selected Value--"+cat);
								//Fragment fragment = new fetch_electricity_biller(act);
								Fragment fragment = new BillPayment(act);
								fragment.setArguments(b1);
								FragmentManager fragmentManager = getFragmentManager();
								fragmentManager.beginTransaction()
										.replace(R.id.frame_container, fragment).commit();
							}
							else if(cat.equalsIgnoreCase("Insurance"))
							{
								act.frgIndex=104;
								Fragment fragment = new fetch_insurance_biller(act);
								fragment.setArguments(b1);
								FragmentManager fragmentManager = getFragmentManager();
								fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
							}
							else if(cat.equalsIgnoreCase("Telecom"))
							{
								act.frgIndex=105;
								Fragment fragment = new fetch_telecom_biller(act);
								fragment.setArguments(b1);
								FragmentManager fragmentManager = getFragmentManager();
								fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
							}*/
						}
					});
                }
	            else{}
            }
            else
            {
            	linear_layout.setVisibility(LinearLayout.GONE);
            	confirm_layout.setVisibility(LinearLayout.VISIBLE);
            }
        }
        catch(JSONException je)
        {
        	je.printStackTrace();
        }

        }// end onPostExecute
    }// end CallWebServiceFetchPayee
	
	/*
	 * class CallWebServiceFetchBill extends AsyncTask<Void, Void, Void> {
        //String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

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
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(act));
                
             
            	Log.e("custId","--"+custId);
            	Log.e("MBSUtils.getImeiNumber(act)","--"+MBSUtils.getImeiNumber(act));
            	Log.e("category","--"+category);
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

            try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_fetch_bill );
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
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });

            String decryptedCategory = xml_data[0];
			//decryptedCategory=decryptedCategory.split("SUCCESS~")[1];
			
			Log.e("decryptedCategory====", decryptedCategory);
			
			Log.e("decryptedCategory=***===", decryptedCategory);
            
            
            loadProBarObj.dismiss();
            try
            {
              //  JSONObject retJson = new JSONObject(xml_data[0]);
                if(!xml_data[0].contains("NODATA"))
                {
                	
                	
                }
                else
                {
                	
                }
                
            }
            catch(Exception je)
            {
               
                je.printStackTrace();
            }

        }// end onPostExecute

    }// end CallWebServiceFetchPayee
	
	
	*/
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
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
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
                    
                    /*	 if(flg=="true")
						{
                    		Intent in = new Intent(act, DashboardDesignActivity.class);
                 			startActivity(in);
                 			act.finish();
						}
                    	 else
                    	{
                    		
                    	}
                   */
                    	
                }this.dismiss();

            }
		 };alert.show();
		
			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
		Log.e("position--***-",","+ position);
		Bundle b1=new Bundle();
		b1.putInt("SELECTED", position);
		
		Intent in2 = new Intent(billObj, PayBill.class);
		in2.putExtras(b1);
		startActivity(in2);
		billObj.finish();
		/*Fragment fragment = new PayBill(act);
		fragment.setArguments(b1);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
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
