package list.shivsamarth_mbs;




import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;



public class BillCategory  extends Activity implements  OnClickListener,OnItemClickListener
{
	
	MainActivity act;
	
	ListView biller_catgry;
	ListView lstRpt;
	ImageButton btn_back;
	Button cat;
	String imeiNo = "",retVal="";
	DatabaseManagement dbms;
	String retMess="",custid="",category="";
	int flag=0;
	private MyThread t1;
	int timeOutInSecs=300;
	public ArrayList<BillerBean>  billerBeans;
	private String custId;
	private static String NAMESPACE = "";
	private static String URL = "";
	//private static final String URL = "http://172.100.30.251:8082/axis2/services/MobBankServices";
	private static String SOAP_ACTION = "";
	TextView txt_heading ;
	BillCategory billCategory;
	public  ArrayList<BillerBean> billerBeanArray=null;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	//private static final String METHOD_NAME1 = "getAccountInfo";
	private static final String METHOD_NAME = "fetchCategory";
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	
	public BillCategory(){}
	
	public BillCategory(MainActivity a)
	{
		System.out.println("BillerCategory()");
		act = a;
		billCategory=this;
	}
	
	public void onBackPressed() 
	{
		return ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		billCategory=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_select_cat);			
		System.out.println("onCreateView()==BillerCategory");		
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
		var3 = (String) getIntent().getSerializableExtra("var3");
	   
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(billCategory.getString(R.string.lbl_sel_cat));
	   
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		lstRpt = (ListView) findViewById(R.id.list_cat);
		lstRpt.setOnItemClickListener(this);
		imeiNo = MBSUtils.getImeiNumber(billCategory);   
		/*Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
		if(c1!=null)
		{
    	   while(c1.moveToNext())
	       {	
    		   custId=c1.getString(2);
    		   Log.e("custId","......"+custId);
	       }
       }*/
      // if(chkConnectivity()==0)
      // {	
    	   Log.e("CallWebServiceFetchCategory***","......");
    	   new CallWebServiceFetchCategory().execute();
       //}
      // else
      // {
    	//   showAlert(act.getString(R.string.alert_000));
     //  }
    	   
    	   t1 = new MyThread(timeOutInSecs,this,var1,var3);
   		t1.start();
	
    }
	
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) billCategory.getSystemService(Context.CONNECTIVITY_SERVICE);
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
						//pb_wait.setVisibility(ProgressBar.VISIBLE);
						// locManager = (LocationManager)
						// getSystemService(Context.LOCATION_SERVICE);
						// netFlg = gpsFlg = 1;
						// Toast.makeText(this, ""+pref,
						// Toast.LENGTH_LONG).show();
						// if (pref.equals("G"))
						// new GpsTimer(timeout * 1000, 1000,
						// this);
						// else
						// new NetworkTimer(timeout * 1000,
						// 1000, this);
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
	
	class CallWebServiceFetchCategory extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(billCategory);

		//String[] xmlTags = { "CUSTID", "IMEINO" };
		String[] xmlTags = {"PARAMS"};
        String[] valuesToEncrypt = new String[1];
        JSONObject jsonObj = new JSONObject();
		
		String generatedXML = "";
		
		
		protected void onPreExecute() 
		{
			try
			{
				Log.e("CallWebServiceFetchCategory INSIDE ---","onPreExecute()");
				loadProBarObj.show();

				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", imeiNo);
				jsonObj.put("FROMACT", "BILL");
            
				Log.e("CallWebServiceFetchCategory CUSTID--","onPreExecute()"+custId);
				Log.e("CallWebServiceFetchCategory IMEINO---","onPreExecute()"+imeiNo);
			}
			catch (JSONException je) 
			{
				je.printStackTrace();
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
	                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME );
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
	            	Log.e("retVal====","retVal===="+retVal);
	            	Log.e("retVal===","retVal===="+retVal);
	                retVal = retVal.substring(retVal.indexOf("=") + 1,
	                        retVal.length() - 3);
	                
	            	Log.e("retVal==","retVal===="+retVal);	
	            	Log.e("retVal==","retVal===="+retVal);
	            	
	            }// end try
	            catch (Exception e) {
	                e.printStackTrace();
	                System.out.println("CallWebServiceFetchCategory--Exception 2");
	                System.out.println("CallWebServiceFetchCategory--Exception" + e);
	            }*/
	            return null;
		
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) 
		{
			//String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "BANKNAMES" });
			String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
			String decryptedCategory = "SUCCESS~[{\"CATEGORY\":\"Electricity\",\"CATEGORYCD\":\"Electricity\"},{\"CATEGORY\":\"Insurance\",\"CATEGORYCD\":\"Insurance\"},{\"CATEGORY\":\"Telecom\",\"CATEGORYCD\":\"Telecom\"}]";//,{\"CATEGORY\":\"Broadband\",\"CATEGORYCD\":\"Broadband\"}]";//xml_data[0];
			decryptedCategory=decryptedCategory.split("SUCCESS~")[1];
			
			Log.e("decryptedCategory====", decryptedCategory);
			
			Log.e("decryptedCategory=***===", decryptedCategory);
			
			//Log.e("EDIT BENF", decryptedBeneficiaries);
			loadProBarObj.dismiss();
			
			
			if(decryptedCategory.indexOf("FAILED")>-1)
			{
				showAlert(getString(R.string.alert_cat));
			}
			else
			{
				try
				{
					
					JSONArray ja = new JSONArray(decryptedCategory);
	             
					billerBeanArray=new ArrayList<BillerBean>();
					String[] billerArr;
	              
	                
					List<String> list = new ArrayList<String>();
					for(int i = 0; i < ja.length(); i++) 
					{
						BillerBean Beanobj= new BillerBean();
            		 	if(ja.getJSONObject(i).getString("CATEGORY").equalsIgnoreCase("Y"))
						{
							Beanobj.setBiller("NA");
							Beanobj.setLabel(ja.getJSONObject(i).getString("CATEGORY"));
						}
						else
						{	
							Beanobj.setBiller(ja.getJSONObject(i).getString("CATEGORY"));
							Beanobj.setCategorycd(ja.getJSONObject(i).getString("CATEGORYCD"));
							Beanobj.setLabel(ja.getJSONObject(i).getString("CATEGORY"));
						}
						billerBeanArray.add(Beanobj);
					}
		            if(ja.length()>0)
		            {
		                CustomAdapterForBiller adapter = new CustomAdapterForBiller(billCategory,billerBeanArray);
						lstRpt.setAdapter(adapter);
						lstRpt.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		            }
		                else{}
	                }
	           catch(JSONException je)
	            {
	               
	                je.printStackTrace();
	            }
			}
		}// end onPostExecute

	}// end CallWebServiceFetchCategory
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
	{
		Log.e("position--***-",","+ position);
		String select=billerBeanArray.get(position).getBiller();
		
		
		Log.e("Selected===*******=","Spinner Selected Value--"+select);
		Log.e("Selected===********=","Spinner Selected Value--"+select);
		Log.e("Selected===******=","Spinner Selected Value--"+select);
		
		/*if(select.equalsIgnoreCase("Electricity"))
		{
			act.frgIndex=111;
			Log.e("in side  Electricity*****=","Spinner Selected Value--"+select);
			Fragment fragment = new Add_electricity_biller(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		}
		else if(select.equalsIgnoreCase("Insurance"))
		{
			act.frgIndex=112;
			Fragment fragment = new Add_insurance_biller(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		}
		else if(select.equalsIgnoreCase("Telecom"))
		{
			act.frgIndex=113;
			Fragment fragment = new Add_telecom_biller(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		}
		else
		{	
		
			Bundle b1=new Bundle();
			b1.putInt("SELECTED", position);
			b1.putString("CATEGORY",MainActivity.billerBeanArray.get(position).getCategory());
			Fragment fragment = new AddPayee(act);
			fragment.setArguments(b1);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		}*/
		Bundle b1=new Bundle();
		b1.putInt("SELECTED", position);
		b1.putString("CATEGORY",billerBeanArray.get(position).getCategorycd());
		b1.putString("FROMACT","BILL");
		
		Intent in=new Intent(billCategory,AddPayee.class);
		in.putExtras(b1);
		startActivity(in);
		billCategory.finish(); 
		/*Fragment fragment = new AddPayee(act);
		fragment.setArguments(b1);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
	}
	
	public void showAlert(String str) {
			
		ErrorDialogClass alert = new ErrorDialogClass(billCategory, "" + str)
		
		{
            Intent in = null;
            
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.btn_ok:
                    	//dismiss();
                      
                    	Log.e("On click ok ===","go to select category");
                    	Intent in=new Intent(billCategory,BillCategory.class);
                		startActivity(in);
                		billCategory.finish(); 
                    	/*Fragment fragment = new BillCategory();
            			FragmentManager fragmentManager = getFragmentManager();
            			fragmentManager.beginTransaction()
            					.replace(R.id.frame_container, fragment).commit();*/
            			//act.frgIndex = 7;
                    	
                    	
                }this.dismiss();

            }
		 };alert.show();
		
			
	}

	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.btn_back:
			Intent in=new Intent(billCategory,BillList.class);
    		startActivity(in);
    		billCategory.finish();
			/*Fragment fragment =new BillList(act);// new Recharge_mobile(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit(); */
		break;

		default:
			break;
		}
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
	
}//End Bills_category



