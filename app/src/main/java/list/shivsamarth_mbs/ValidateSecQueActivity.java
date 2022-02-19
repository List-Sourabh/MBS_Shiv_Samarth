package list.shivsamarth_mbs;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;

import mbLib.MBSUtils;
import mbLib.MyThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.TextView;

public class ValidateSecQueActivity extends CustomWindow implements
		OnClickListener, LocationListener {

	private static final String MY_SESSION = "my_session";
	Button subButton;
	Button canButton;
	TextView queText, txt_heading;
	EditText sEditText;
	ImageButton btn_back;
	String respcode="",retval="",respdesc="",respdescval="";
	ImageView img_heading;
	ValidateSecQueActivity valSecAct;
	private MyThread t1;
	int timeOutInSecs=300;
	String custId = null, mpin = null, retVal = null, retMess = null,
			que_one = null, ans_one = null, qOne = null;
	int cnt,flag=0;
	String[] ques;
	JSONArray jsonArr;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.validsecque);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		subButton = (Button) findViewById(R.id.btn_submit_secu_que);
		subButton.setOnClickListener(this);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		queText = (TextView) findViewById(R.id.txt_security_que1);
		sEditText = (EditText) findViewById(R.id.edttxt_security_que1);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_security_que));
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.register);
		Bundle b1 = this.getIntent().getExtras();

		if (b1 != null) {
			custId = b1.getString("custId");
			mpin = b1.getString("mpin");
		} else
			custId = "0000000000";

		// INSTANTIATE SHARED PREFERENCES CLASS
		SharedPreferences sp = getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		// LOAD THE EDITOR REMEMBER TO COMMIT CHANGES!
		//cntx = sp.edit();

		flag = chkConnectivity();
		if (flag == 0)
		{
			CallWebServiceFetchSecuQue c = new CallWebServiceFetchSecuQue();
			c.execute();
		}
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit_secu_que:
			ans_one = sEditText.getText().toString().trim();

			if (ans_one == null || ans_one.length() == 0) {
				showAlert(getString(R.string.alert_ans_secQue));
			} else {
				// showAlert("ans_one=="+ans_one+"==ans_two=="+ans_two);
				flag = chkConnectivity();
				if (flag == 0)
				{
					CallWebServiceValidateSecuQue c = new CallWebServiceValidateSecuQue();
					c.execute();
				}
			}
			break;

		case R.id.btn_back:
			Intent in = new Intent(this, LoginActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
			break;
		default:
			break;
		}
	}

	class CallWebServiceFetchSecuQue extends AsyncTask<Void, Void, Void> {

	
		// LoadProgressBar loadProBarObj=new LoadProgressBar(this);

		LoadProgressBar loadProBarObj = new LoadProgressBar(
				ValidateSecQueActivity.this);
		JSONObject obj=new JSONObject();
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			custId = ValidateSecQueActivity.this.custId;
			
			try {
				obj.put("CUSTID", custId);
				obj.put("IMEINO", MBSUtils.getImeiNumber(ValidateSecQueActivity.this));
				obj.put("SIMNO", MBSUtils.getSimNumber(ValidateSecQueActivity.this));
			 	obj.put("METHODCODE","25"); 
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			//valuesToEncrypt[1] = MBSUtils
					//.getImeiNumber(ValidateSecQueActivity.this);
			
			//Log.i("IN onPreExecute@CallWebServiceFetchSecuQue",
			//		"generatedXML :" + generatedXML);
		};

		@Override
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
					isWSCalled=true;
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			if (isWSCalled) {
				// String[] xmlTags = { "STATUS" };
				// String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);
			
				// Log.e("FetchSecQue", "xml_data.length==" + xml_data.length);
				
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdesc = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdesc = "";
					}
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(respdesc.length()>0)
				{
					showAlert(respdesc);
				}
				else{
					post_success(retval);
				/*try {
					if (retval.indexOf("FAILED") > -1) {
						showAlert(getString(R.string.alert_err));
					} else {
						JSONArray ja = new JSONArray(retval);
						jsonArr = ja;
						ques = new String[ja.length()];
						for (int j = 0; j < ja.length(); j++) {
							JSONObject jObj = ja.getJSONObject(j);
							ques[j] = (jObj.getString("QUEDESC"));
							count++;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (count > 0) {
					// Log.e("HERE", "====" + retVal);
					double y = Math.random();
					int que_num = (int) (y * 100) % 2;
					queText.setText(ques[que_num]);
				}*/
				}
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
			/*int count = 0;
			loadProBarObj.dismiss();
			if (isWSCalled) {
				//String[] xmlTags = { "STATUS" };
				//String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);
				String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
				//Log.e("FetchSecQue", "xml_data.length==" + xml_data.length);

				try {
					if (xml_data[0].indexOf("FAILED") > -1) {
						showAlert(getString(R.string.alert_err));
					} else {
						JSONArray ja = new JSONArray(xml_data[0]);
						jsonArr = ja;
						ques = new String[ja.length()];
						for (int j = 0; j < ja.length(); j++) {
							JSONObject jObj = ja.getJSONObject(j);
							ques[j] = (jObj.getString("QUEDESC"));
							
							count++;
						}
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (count > 0) {
					//Log.e("HERE", "====" + retVal);
					double y = Math.random();
					int que_num = (int) (y * 100) % 2;
					queText.setText(ques[que_num]);
				}
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}*/
		}

	}
	
	public 	void post_success(String retval){
		int count = 0;
		respcode="";
		respdesc="";
		try {
			if (retval.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_err));
			} else {
				JSONArray ja = new JSONArray(retval);
				jsonArr = ja;
				ques = new String[ja.length()];
				for (int j = 0; j < ja.length(); j++) {
					JSONObject jObj = ja.getJSONObject(j);
					ques[j] = (jObj.getString("QUEDESC"));
					count++;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (count > 0) {
			// Log.e("HERE", "====" + retVal);
			double y = Math.random();
			int que_num = (int) (y * 100) % 2;
			queText.setText(ques[que_num]);
		}
	}

	class CallWebServiceValidateSecuQue extends AsyncTask<Void, Void, Void> {

		

		LoadProgressBar loadProBarObj = new LoadProgressBar(
				ValidateSecQueActivity.this);
	
		boolean isWSCalled = false;
		JSONObject newobj=new JSONObject();
		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
            
			custId = ValidateSecQueActivity.this.custId;
			que_one = queText.getText().toString().trim();
			ans_one = sEditText.getText().toString().trim();
			try {
				for (int k = 0; k < jsonArr.length(); k++) {
					JSONObject obj = jsonArr.getJSONObject(k);
					if (obj.getString("QUEDESC").equalsIgnoreCase(que_one))
						qOne = obj.getString("QUECD");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				
				newobj.put("CUSTID", custId);
				newobj.put("QUE", qOne);
				newobj.put("ANS", ans_one);
              	newobj.put("IMEINO",  MBSUtils.getImeiNumber(ValidateSecQueActivity.this));
            	newobj.put("SIMNO", MBSUtils.getSimNumber(ValidateSecQueActivity.this));
              	newobj.put("METHODCODE","24"); 
             
			
				Log.e("onPreExecute=","JSON="+newobj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		/*	valuesToEncrypt[1] = qOne;
			valuesToEncrypt[2] = ans_one;
			valuesToEncrypt[3] = MBSUtils
					.getImeiNumber(ValidateSecQueActivity.this);*/
			
			//Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);
		};

		@Override
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
					request.addProperty("value1", CryptoClass.Function5(newobj.toString(), var2));
					request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
					request.addProperty("value3", var3);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);

					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1,var5.length() - 3);
					isWSCalled=true;
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			if (isWSCalled) {
				// String[] xmlTags = { "STATUS" };
				// String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);
			
				// Log.e("Register", "xml_data.length==" + xml_data.length);
				// Log.e("Register", "xml_data[0]==" + xml_data[0]);

				// int start = xml_data[0].indexOf("SUCCESS");
				// System.out.println("start:" + start);

				
				
				JSONObject jsonObj;
				try
				{
	
					String str=CryptoClass.Function6(var5,var2);
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdescval = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescval = "";
					}
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(respdescval.length()>0)
				{
					showAlert(respdescval);
				}
				else{
				if (retval.indexOf("SUCCESS") > -1) {
					post_successValidateSecuQue(retval);
					/*Intent intent = new Intent(ValidateSecQueActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();*/
				} else {
					// System.out
					// .println("in else ***************************************");
					if (retval.indexOf("WRONGANS") >= 0) {
						retMess = getString(R.string.alert_087);
					} else {
						retMess = getString(R.string.alert_err);
					}
					showAlert(retMess);
				}
				}
			} else {
				retMess = getString(R.string.alert_err);
				showAlert(retMess);
			}
			/*loadProBarObj.dismiss();
			if (isWSCalled) 
			{
				String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
			
				Log.e("xml_data", xml_data[0]);
				if (xml_data[0].indexOf("SUCCESS") > -1) 
				{
					Intent intent = new Intent(ValidateSecQueActivity.this,LoginActivity.class);
					startActivity(intent);
					finish();
				} 
				else 
				{
					if (xml_data[0].indexOf("WRONGANS") >= 0) 
					{
						retMess = getString(R.string.alert_087);
					} 
					else 
					{
						retMess = getString(R.string.alert_err);
					}
					showAlert(retMess);
				}
			} 
			else 
			{
				retMess = getString(R.string.alert_err);
				showAlert(retMess);
			}*/
		}
	}
	
	public 	void post_successValidateSecuQue(String retval)
	{
		Log.e("ValidateSecQueActivity","retval=="+retval);
		respcode="";
		String decryptedAccounts = retval.split("~")[1];
		Bundle bObj=new Bundle();
		Intent in=new Intent(ValidateSecQueActivity.this,OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",custId);
		bObj.putString("FROMACT", "IMEIDIFF");
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
		in.putExtras(bObj);
		startActivity(in);
		
		finish();
		/*respcode="";
		respdescval="";
		Intent intent = new Intent(ValidateSecQueActivity.this,
				LoginActivity.class);
		startActivity(intent);
		finish();*/
	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
		{
			@Override
			public void onClick(View v)
 
			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retval);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(respdescval)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successValidateSecuQue(retval);
						}
						else if((str.equalsIgnoreCase(respdescval)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
							this.dismiss();
					  break;			
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.show();
	}
	
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
						// pb_wait.setVisibility(ProgressBar.VISIBLE);
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
					retMess = getString(R.string.alert_000);
					// setAlert();
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
				// setAlert();
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

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
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
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			// setAlert();
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
		}
		return flag;
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
