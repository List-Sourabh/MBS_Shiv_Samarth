package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ListBeneficiary extends Activity implements OnClickListener {
	
	
	ListBeneficiary listBenf;
	EditText txtAccNo;
	// Button btn_fetchName;
	EditText txtName;
	EditText txtmobNo;
	EditText txtEmail;
	EditText txtNick_Name;
	TextView lblTitle;
	// ProgressBar pro_bar;
	Button btn_submit;
	ImageButton btn_home, btn_back;
	int cnt = 0, flag = 0;
	String str = "", retMess = "", cust_name = "", tmpXMLString = "",getBeneficiariesrespdesc="",
			retVal = "",reTval="";
	DialogBox dbs;
	DatabaseManagement dbms;
	private MyThread t1;
	int timeOutInSecs=300;
	
	private static final String MY_SESSION = "my_session";
	//Editor e;

	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME1 = "";

	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "";
	ListBeneficiary act = this;

	private String benInfo = "";
	String mobPin = "",respcode="",retval="",respdesc="";
	String benSrno = "";
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	ListView lstRpt;
	TextView txt_heading;
	ImageView img_heading;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	public ListBeneficiary() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_beneficiary);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		 lstRpt = (ListView) findViewById(R.id.benList);

		txt_heading = (TextView) findViewById(R.id.txt_heading);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.list_beneficiary);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);

		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		txt_heading.setText(getString(R.string.lbl_list_benf));
	
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);
	        	
	        }
        }
		
		flag = chkConnectivity();
		if (flag == 0) 
		{
			new CallWebServiceFetchBenf().execute();
		}
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	
	private void setValues(String totalBenInfo) 
	{		
		try 
		{
			String allstr[] = totalBenInfo.split("~");
			String singleBenInfo[] = null;
			String benName = null;
			String benAccountNumber = null;
			if (allstr[0].equalsIgnoreCase("SUCCESS")) 
			{
				List<String> content = new ArrayList<String>();

				for (int i = 1; i < allstr.length; i++) 
				{
					singleBenInfo = allstr[i].split("#");
					benName = singleBenInfo[1];
					benAccountNumber = singleBenInfo[3];
					HashMap<String, String> map = new HashMap<String, String>();
				
					String[] from = new String[] { "rowid", "col_0", "col_1" };
					int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };

					map.put("col_0", benName.trim());
					
					map.put("col_1", benAccountNumber.trim().equals("-9999")?"-":benAccountNumber.trim());
					
					fillMaps.add(map);
						
					SimpleAdapter adapter = new SimpleAdapter(act, fillMaps,R.layout.view_ben_rpt, from, to);
					lstRpt.setAdapter(adapter);
				}
			} 
			else 
			{
				retMess = getString(R.string.alert_fail_to_load_benf);
				showAlert(retMess);
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Exception in setValue of ListEbenefiary:" + e);
		}
	}// end setValues

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
		}
	
		
	}
	
	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	//	ErrorDialogClass alert = new ErrorDialogClass(act, "" + str);
	ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		
		{
            Intent in = null;
            
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.btn_ok:
                    	//dismiss();
                    	if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
    					{
    						post_successfetch_all_beneficiaries(reTval);
    					}
    					else if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
    					{
    						this.dismiss();
    					}
                    	break;
                }this.dismiss();

            }
		
	 };	alert.show();
	}
	
	class CallWebServiceFetchBenf extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
      
        JSONObject jsonObj = new JSONObject();
	
		String accNo, debitAccno, benAcNo, amt, reMark;

		protected void onPreExecute() 
		{
			try
			{
				loadProBarObj.show();
				Log.e("","CUSTID:" + custId);
				Log.e("","TRANSFERTYPE:" + "A");
				Log.e("","BENSRNO:" + benSrno);
	
				 jsonObj.put("CUSTID", custId);
	             jsonObj.put("SAMEBNK", "A");
	             jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
	             jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
			        jsonObj.put("METHODCODE","13"); 
			} 
			catch (JSONException je) 
			{
                je.printStackTrace();
            }
            
            
		}// end onPreExecute

		@Override
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

		protected void onPostExecute(Void paramVoid) 
		{
			
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{

				String str=CryptoClass.Function6(var5,var2);
				Log.e("return listbenf", "========"+str);
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

			if (reTval.indexOf("SUCCESS") > -1) 
			{
				post_successfetch_all_beneficiaries(reTval);
					
			} else if (reTval.indexOf("NODATA") > -1)  {
				
				retMess = getString(R.string.alert_041);
				//  loadProBarObj.dismiss();
				showAlert(retMess);
			}
			else
			{
				 retMess=getString(R.string.alert_069);
		      	 // loadProBarObj.dismiss();
		      	  showAlert(retMess);
			}
			}
		}// end onPostExecute

	}// end callWbService
	
	public 	void post_successfetch_all_beneficiaries(String reTval)
	{
		respcode="";
   	   	getBeneficiariesrespdesc="";
		benInfo = reTval;
		setValues(reTval);
	}
	
	public int chkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try 
		{
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			if (state1) 
			{
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
						showAlert(retMess);
						break;
					default:
						flag = 1;
						retMess = getString(R.string.alert_000);
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
			Log.i("ListBeneficiary ", "NullPointerException Exception"	+ ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} catch (Exception e) {
			Log.i("ListBeneficiary", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
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
