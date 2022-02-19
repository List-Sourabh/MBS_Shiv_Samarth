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

import mbLib.*;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class History extends Activity implements OnClickListener
{
	MainActivity act;
	TextView txt_heading;
	ImageView img_heading,btn_back;
	ListView tran_listView;
	DialogBox dbs;
	DatabaseManagement dbms;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String METHOD_RECHARGE = "ViewRechargeHistory";
	private static final String METHOD_BILL = "ViewBillHistory";
	ArrayList<HistoryBean> historyBeanArr;
	History history;
	String fromAct = "", retMess = "", custId = "", stringValue = "",retVal="",fromDt="",toDt="",category="",operator="";
	int flag = 0,cnt=0;
	boolean flg=false;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public History() {
	}

	public History(MainActivity a) 
	{
		act = a;
		dbs = new DialogBox(act);
		history=this;
	}
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		history=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		tran_listView=(ListView)findViewById(R.id.tran_listView);
		txt_heading.setText(history.getString(R.string.lbl_view_history));
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		/*dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		Log.e("retvalstr","....."+stringValue);
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);       	
	        }
        }*/
        Intent getdata=getIntent();
    	Bundle bundle = getdata.getExtras();
		if (bundle != null) 
		{
			fromAct = bundle.getString("FROMACT");
	        fromDt = bundle.getString("FROMDATE");
	        toDt = bundle.getString("TODATE");
	        category = bundle.getString("CATEGORY");
	        operator = bundle.getString("BILLERCD");
		}
		Log.e("HISTORY","fromAct=="+fromAct);
		Log.e("HISTORY","category=="+category);
		//if(chkConnectivity()==0)
		//{	
			if(fromAct.equalsIgnoreCase("BILL"))
				new CallWebServiceViewBillHistory().execute();
			else
				new CallWebServiceViewRechargeHistory().execute();
		//}
		////else
        //	showAlert(act.getString(R.string.alert_000));
			t1 = new MyThread(timeOutInSecs,this,var1,var3);
			t1.start();
		
	}
	
	@Override
	public void onClick(View v) 
	{
		Bundle b1=new Bundle();
		b1.putString("FROMACT",fromAct);
		Intent in=new Intent(history,ViewHistory.class);
		in.putExtras(b1);
		startActivity(in);
		history.finish(); 
		/*Fragment fragment =new ViewHistory(act);
		fragment.setArguments(b1);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
	}
	
	public void showAlert(String str) {
		ErrorDialogClass alert = new ErrorDialogClass(history, "" + str)
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
                    		Bundle b1=new Bundle();
                    		b1.putString("FROMACT",fromAct);
                    		Intent in=new Intent(history,ViewHistory.class);
                    		in.putExtras(b1);
                    		startActivity(in);
                    		history.finish(); 
/*                    		Fragment fragment =new ViewHistory(act);
                    		fragment.setArguments(b1);
                    		FragmentManager fragmentManager = getFragmentManager();
                    		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
						}
                    	else
                    	{
                    		
                    	}
                }this.dismiss();
            }
		 };
		 alert.show();
	}
	
	class CallWebServiceViewRechargeHistory extends AsyncTask<Void, Void, Void> 
	{
		String[] xmlTags = {"PARAMS"};
		String[] valuesToEncrypt = new String[1];
		LoadProgressBar loadProBarObj = new LoadProgressBar(history);
		String generatedXML = "";
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			JSONObject obj=new JSONObject();
			try 
			{
				obj.put("CUSTID", custId);
				obj.put("CATEGORY",category);
				obj.put("BILLERCD",operator);
				obj.put("FROMDATE",fromDt);
				obj.put("TODATE",toDt);
				obj.put("IMEINO", MBSUtils.getImeiNumber(history));
				valuesToEncrypt[0] = obj.toString();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			
			int i = 0;
			/*try 
			{
				SoapObject request = new SoapObject(NAMESPACE, METHOD_RECHARGE);
				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				String status = "";
				try {
					androidHttpTransport.call(SOAP_ACTION, envelope);
					System.out.println(envelope.bodyIn.toString());
					status = envelope.bodyIn.toString().trim();
					retVal = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						retVal = status;
						isWSCalled = true;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			} 
			catch (Exception e) 
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}*/
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			int count = 0;
			loadProBarObj.dismiss();
			//if (isWSCalled) 
		//	{
				//String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
				String decryptedData="SUCCESS";//xml_data[0];
				try 
				{
					if (decryptedData.indexOf("FAILED") > -1) 
					{
						flg=true;
						showAlert(getString(R.string.alert_err));
					} 
					else 
					{
						
						historyBeanArr=new ArrayList<HistoryBean>();
						String decryptedOperator=decryptedData;
						Log.e("HISTORY",decryptedOperator);
						if(decryptedOperator.equalsIgnoreCase("NODATA"))
						{
							flg=true;
							showAlert(history.getString(R.string.alert_089));
						}
						else
						{	
							flg=false;
							JSONArray jArr=new JSONArray();//decryptedOperator);
							if(category.indexOf("MOBILE")>-1 || category.equalsIgnoreCase("All"))
							{
								JSONObject jObj1=new JSONObject();
								jObj1.put("ACC_NAME","home");
								jObj1.put("PYMNT_AMNT","100");
								jObj1.put("BILLER_NAME","Vodafone");
								jObj1.put("STATUS","Success");
								jArr.put(jObj1);
								
								JSONObject jObj2=new JSONObject();
								jObj2.put("ACC_NAME","mahesh");
								jObj2.put("PYMNT_AMNT","150");
								jObj2.put("BILLER_NAME","Airtel");
								jObj2.put("STATUS","Success");
								jArr.put(jObj2);
								
								JSONObject jObj3=new JSONObject();
								jObj3.put("ACC_NAME","jayesh");
								jObj3.put("PYMNT_AMNT","99");
								jObj3.put("BILLER_NAME","Jio");
								jObj3.put("STATUS","Success");
								jArr.put(jObj3);
							}
							if(category.indexOf("DTH")>-1 || category.equalsIgnoreCase("All"))
							{
								JSONObject jObj1=new JSONObject();
								jObj1.put("ACC_NAME","home dish");
								jObj1.put("PYMNT_AMNT","199");
								jObj1.put("BILLER_NAME","Tata Sky");
								jObj1.put("STATUS","Success");
								jArr.put(jObj1);
								
								JSONObject jObj2=new JSONObject();
								jObj2.put("ACC_NAME","mahesh dish");
								jObj2.put("PYMNT_AMNT","250");
								jObj2.put("BILLER_NAME","Dish TV");
								jObj2.put("STATUS","Success");
								jArr.put(jObj2);
								
								JSONObject jObj3=new JSONObject();
								jObj3.put("ACC_NAME","jayesh dish");
								jObj3.put("PYMNT_AMNT","299");
								jObj3.put("BILLER_NAME","Big TV");
								jObj3.put("STATUS","Success");
								jArr.put(jObj3);
							}
							Log.e("HISTORY","jArr.length()==="+jArr.length());
							for(int i=0;i<jArr.length();i++)
							{
								JSONObject jObj=jArr.getJSONObject(i);
								HistoryBean beanObj=new HistoryBean();
								if(jObj.has("ACC_NAME"))
									beanObj.setPayeenm(jObj.getString("ACC_NAME"));
								else
									beanObj.setPayeenm("");
								
								if(jObj.has("PYMNT_AMNT"))
									beanObj.setAmount(jObj.getString("PYMNT_AMNT"));
								else
									beanObj.setAmount("");
									
								if(jObj.has("BILLER_NAME"))
									beanObj.setBiller(jObj.getString("BILLER_NAME"));
								else
									beanObj.setBiller("");
								
								if(jObj.has("STATUS"))
									beanObj.setStatus(jObj.getString("STATUS"));
								else
									beanObj.setStatus("");
								historyBeanArr.add(beanObj);
							}
							Log.e("HISTORY","historyBeanArr.length()==="+historyBeanArr.size());
							CustomAdapterForHistory historylist=new CustomAdapterForHistory(history, historyBeanArr);
							tran_listView.setAdapter(historylist);
						}	
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			//} 
			//else 
			//{
			//	retMess = getString(R.string.alert_000);
			//	showAlert(retMess);
			//}
		}

	}
	
	class CallWebServiceViewBillHistory extends AsyncTask<Void, Void, Void> 
	{
		String[] xmlTags = {"PARAMS"};
		String[] valuesToEncrypt = new String[1];
		LoadProgressBar loadProBarObj = new LoadProgressBar(history);
		String generatedXML = "";
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			JSONObject obj=new JSONObject();
			try 
			{
				obj.put("CUSTID", custId);
				obj.put("CATEGORY",category);
				obj.put("BILLERCD",operator);
				obj.put("FROMDATE",fromDt);
				obj.put("TODATE",toDt);
				obj.put("IMEINO", MBSUtils.getImeiNumber(history));
				valuesToEncrypt[0] = obj.toString();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			
			int i = 0;
			/*try 
			{
				SoapObject request = new SoapObject(NAMESPACE, METHOD_BILL);
				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				String status = "";
				try {
					androidHttpTransport.call(SOAP_ACTION, envelope);
					System.out.println(envelope.bodyIn.toString());
					status = envelope.bodyIn.toString().trim();
					retVal = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						retVal = status;
						isWSCalled = true;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			} 
			catch (Exception e) 
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}*/
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			int count = 0;
			loadProBarObj.dismiss();
			isWSCalled = true;
			if (isWSCalled) 
			{
				//String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
				String decryptedData="SUCCESS";//xml_data[0];
				try 
				{
					if (decryptedData.indexOf("FAILED") > -1) 
					{
						flg=true;
						showAlert(getString(R.string.alert_err));
					} 
					else 
					{
						flg=false;
						historyBeanArr=new ArrayList<HistoryBean>();
						//String decryptedOperator=xml_data[0];
						//Log.e("HISTORY",decryptedOperator);
						if(decryptedData.equalsIgnoreCase("NODATA"))
						{
							flg=true;
							showAlert(history.getString(R.string.alert_089));
						}
						else
						{	
							flg=false;
							JSONArray jArr=new JSONArray();//decryptedOperator);
							if(category.indexOf("Electricity")>-1 || category.equalsIgnoreCase("All"))
							{
								JSONObject jObj1=new JSONObject();
								jObj1.put("ACC_NAME","home");
								jObj1.put("PYMNT_AMNT","458");
								jObj1.put("BILLER_NAME","Maharashtra State Electricity Board");
								jObj1.put("STATUS","Success");
								jArr.put(jObj1);
								
								JSONObject jObj2=new JSONObject();
								jObj2.put("ACC_NAME","mahesh");
								jObj2.put("PYMNT_AMNT","987");
								jObj2.put("BILLER_NAME","Maharashtra State Electricity Board");
								jObj2.put("STATUS","Success");
								jArr.put(jObj2);
								
								JSONObject jObj3=new JSONObject();
								jObj3.put("ACC_NAME","jayesh");
								jObj3.put("PYMNT_AMNT","991");
								jObj3.put("BILLER_NAME","Maharashtra State Electricity Board");
								jObj3.put("STATUS","Success");
								jArr.put(jObj3);
							}
							if(category.indexOf("Insurance")>-1 || category.equalsIgnoreCase("All"))
							{
								JSONObject jObj1=new JSONObject();
								jObj1.put("ACC_NAME","family");
								jObj1.put("PYMNT_AMNT","10000");
								jObj1.put("BILLER_NAME","Aviva Life Insurance");
								jObj1.put("STATUS","Success");
								jArr.put(jObj1);
								
								JSONObject jObj2=new JSONObject();
								jObj2.put("ACC_NAME","Baba");
								jObj2.put("PYMNT_AMNT","2500");
								jObj2.put("BILLER_NAME","Life Insurance Corp");
								jObj2.put("STATUS","Success");
								jArr.put(jObj2);
							}
							if(category.indexOf("Telecom")>-1 || category.equalsIgnoreCase("All"))
							{
								JSONObject jObj1=new JSONObject();
								jObj1.put("ACC_NAME","home");
								jObj1.put("PYMNT_AMNT","299");
								jObj1.put("BILLER_NAME","Bharat Sanchar Nigam Ltd");
								jObj1.put("STATUS","Success");
								jArr.put(jObj1);
								
								JSONObject jObj2=new JSONObject();
								jObj2.put("ACC_NAME","mahesh");
								jObj2.put("PYMNT_AMNT","250");
								jObj2.put("BILLER_NAME","Bharat Sanchar Nigam Ltd");
								jObj2.put("STATUS","Success");
								jArr.put(jObj2);
							}
							Log.e("HISTORY","jArr.length()==="+jArr.length());
							for(int i=0;i<jArr.length();i++)
							{
								JSONObject jObj=jArr.getJSONObject(i);
								HistoryBean beanObj=new HistoryBean();
								beanObj.setPayeenm(jObj.getString("ACC_NAME"));
								beanObj.setAmount(jObj.getString("PYMNT_AMNT"));
								beanObj.setBiller(jObj.getString("BILLER_NAME"));
								beanObj.setStatus(jObj.getString("STATUS"));
								historyBeanArr.add(beanObj);
							}
							Log.e("HISTORY","historyBeanArr.length()==="+historyBeanArr.size());
							CustomAdapterForHistory historylist=new CustomAdapterForHistory(history, historyBeanArr);
							tran_listView.setAdapter(historylist);
						}
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			} 
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}

	}
	
	public int chkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) history.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try 
		{
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("state1 ---------" + state1);
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
						retMess = getString(R.string.alert_014);
						showAlert(retMess);
						break;
					default:
						flag = 1;
						retMess=getString(R.string.alert_000);
						showAlert(retMess);
						break;
				}
			} 
			else 
			{
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} 
		catch (NullPointerException ne) 
		{
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} 
		catch (Exception e) 
		{
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
	
	@Override
	public void onBackPressed() {
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
