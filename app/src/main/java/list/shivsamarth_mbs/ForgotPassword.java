package list.shivsamarth_mbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import mbLib.CryptoClass;
import mbLib.DeviceUtils;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class ForgotPassword extends Activity implements OnClickListener
{
	TextView txt_heading,txt_que_one;
	EditText txt_ans_one,txt_cust_id;
	Button btn_validate_que,btn_proceed;
	ImageView img_heading;
	LinearLayout secu_que_layout,cust_id_layout;
	ImageButton btn_home,btn_back;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	int cnt = 0, flag = 0;
	String[] ques;
	JSONArray jsonArr;
	String custCd="",qOne="",que_one="",fromAct="";
	String retMess,retVal,custId,ans_one,ans_two,retval = "",respcode="",respdesc="",respdesc_ValidateSecuQue="";
	String retvalweb="",ExistingCustomerrespdesc="",SecQuestionrespdesc="";
	DialogBox dbs;
	int netFlg, gpsFlg;
	int count=0;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		if (!new DeviceUtils().isEmulator()) {
			MBSUtils.ifGooglePlayServicesValid(ForgotPassword.this);
		} else {
			MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup),ForgotPassword.this);
		}
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.register);
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		Bundle b1=new Bundle();
		b1=getIntent().getExtras();
		if(b1!=null)
		{
			fromAct=b1.getString("FROMACT");
		}

		secu_que_layout=(LinearLayout)findViewById(R.id.secu_que_layout);
		cust_id_layout=(LinearLayout)findViewById(R.id.cust_id_layout);
		txt_cust_id=(EditText)findViewById(R.id.txt_cust_id);
		txt_ans_one=(EditText)findViewById(R.id.edttxt_security_que1);
		
		btn_proceed=(Button)findViewById(R.id.btn_proceed);
		btn_validate_que=(Button)findViewById(R.id.btn_submit_secu_que);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_que_one=(TextView)findViewById(R.id.txt_security_que1);
		
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		//btn_home.setImageResource(R.mipmap.bank_logo);
		btn_back.setImageResource(R.mipmap.backover);
		
		if(fromAct.equalsIgnoreCase("LOGIN"))
		{
			txt_heading.setText(getString(R.string.reset_mpin));
		}
		else
		{	
			txt_heading.setText(getString(R.string.forgot_mpin));
		}
		btn_back.setOnClickListener(this);
		
		btn_proceed.setOnClickListener(this);
		btn_validate_que.setOnClickListener(this);
		dbs = new DialogBox(this);
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_back:
				Intent in=new Intent(this,LoginActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case R.id.btn_proceed:
				custId=txt_cust_id.getText().toString().trim();
				if(custId.length()!=10)
				{
					showAlert(getString(R.string.alert_001));
				}
				else
				{
					//showAlert("custId==="+custId);
					flag = chkConnectivity();
					if (flag == 0)
					{
						CallWebServiceValidateExtngCust c=new CallWebServiceValidateExtngCust();
						c.execute();
					}
				}
				break;
			case R.id.btn_submit_secu_que:
				ans_one=txt_ans_one.getText().toString().trim();
				
				if(ans_one==null ||ans_one.length()==0)
				{
					showAlert(getString(R.string.alert_ans_secQue));
				}
				else
				{
					//showAlert("ans_one=="+ans_one+"==ans_two=="+ans_two);
					flag = chkConnectivity();
					if (flag == 0)
					{
						CallWebServiceValidateSecuQue c=new CallWebServiceValidateSecuQue();
						c.execute();
					}
				}
				break;
	
			default:
				break;
		}
	}

	public void onBackPressed() { 
		Intent mainIntent;
		mainIntent = new Intent(ForgotPassword.this, LoginActivity.class);
		mainIntent.putExtra("var1", var1);
		mainIntent.putExtra("var3", var3);
		startActivity(mainIntent);
		// overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
		finish();
	}

	class CallWebServiceValidateExtngCust extends AsyncTask<Void, Void, Void> 
	{
		
		
		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(ForgotPassword.this);
		
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();

			custId=txt_cust_id.getText().toString().trim();
			
		
			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ForgotPassword.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ForgotPassword.this));
				jsonObj.put("METHODCODE","25"); 
				 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);
		};

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
			if (isWSCalled) 
			{
				
				//Log.e("ForgotPassword","xml_data.length=="+xml_data[0]);
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
							retvalweb = jsonObj.getString("RETVAL");
						}
						else
						{
							retvalweb = "";
						}
						if (jsonObj.has("RESPDESC"))
						{
							ExistingCustomerrespdesc = jsonObj.getString("RESPDESC");
						}
						else
						{	
							ExistingCustomerrespdesc = "";
						}
					} catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(ExistingCustomerrespdesc.length()>0)
					{
						showAlert(ExistingCustomerrespdesc);
					}
					else{
				
					if(retvalweb.indexOf("NODATA")>-1)
					{
						showAlert(getString(R.string.alert_072));
					}
					else if(retvalweb.indexOf("NOREG")>-1)
					{
						showAlert(getString(R.string.alert_cust_not_reg));
					}
					else if(retvalweb.indexOf("FAILED")>-1)
					{
						showAlert(getString(R.string.alert_err));
					}
					else
					{
						post_successExistingCustomer(retvalweb);
					}
				if(count>0)
				{
					//Log.e("HERE","===="+retVal);
					cust_id_layout.setVisibility(cust_id_layout.INVISIBLE);
					secu_que_layout.setVisibility(secu_que_layout.VISIBLE);
					double y = Math.random();
					int que_num=(int)(y*100)%2;
					txt_que_one.setText(ques[que_num]);
				}
				
			} }
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}

	}
	
	public 	void post_successExistingCustomer(String retvalweb)
	{
		respcode="";
		ExistingCustomerrespdesc="";
		JSONArray ja = null;
		
		int j = 0;				
		try 
		{
			ja=new JSONArray(retvalweb);
			Log.e("JSONException ", "retvalweb=" + retvalweb);
			jsonArr=ja;
			ques=new String[ja.length()];
			for(;j<ja.length();j++)
			{
				JSONObject jObj=ja.getJSONObject(j);
				ques[j]=(jObj.getString("QUEDESC"));
				count++;
			}
		
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
            try 
            {
				JSONObject jObj = ja.getJSONObject(j);
				custCd = jObj.getString("QUECD");
				Log.e("JSONException ", "custCd=" + custCd);
				Log.e("JSONException ", "custCd=" + custCd);
				Log.e("JSONException ", "custCd=" + custCd);
			} 
            catch (JSONException je) 
            {
				je.printStackTrace();
			}
		}
		if (count > 0) 
		{
			// Log.e("HERE","===="+retVal);
			cust_id_layout.setVisibility(cust_id_layout.INVISIBLE);
			secu_que_layout.setVisibility(secu_que_layout.VISIBLE);
			double y = Math.random();
			int que_num = (int) (y * 100) % 2;
			txt_que_one.setText(ques[que_num]);
		}
		
	}
	
	class CallWebServiceValidateSecuQue extends AsyncTask<Void, Void, Void> {

		/*String[] xmlTags = { "CUSTID","QUE","ANS","IMEINO" };
		String[] valuesToEncrypt = new String[4];*/
		
		
		JSONObject jsonObj = new JSONObject();
		
		LoadProgressBar loadProBarObj = new LoadProgressBar(ForgotPassword.this);
	
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();

			custId=txt_cust_id.getText().toString().trim();
			que_one=txt_que_one.getText().toString().trim();
			ans_one=txt_ans_one.getText().toString().trim();
			try 
			{
				for(int k=0;k<jsonArr.length();k++)
				{
					JSONObject obj=jsonArr.getJSONObject(k);
					 
					if (obj.getString("QUEDESC").trim().equalsIgnoreCase(que_one))
						qOne = obj.getString("QUECD");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("QUE", qOne);
				jsonObj.put("ANS", ans_one);//)ListEncryption.encryptData(ans_one+custId));
			//	Log.e("FORGOTPASS","answer===="+ListEncryption.encryptData(ans_one+custId));
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ForgotPassword.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ForgotPassword.this));
				jsonObj.put("METHODCODE","23"); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			 
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			 String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "webServiceOne";

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
			if (isWSCalled) 
			{
			//	String[] xmlTags = {"STATUS"};
			//	String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);
				//Log.e("Register","xml_data.length=="+xml_data.length);
				//Log.e("Register","xml_data[0]=="+xml_data[0]);
				
				
				
				//System.out.println("start:" + start);
								
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
						retvalweb = jsonObj.getString("RETVAL");
					}
					else
					{
						retvalweb = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						SecQuestionrespdesc = jsonObj.getString("RESPDESC");
					}
					else
					{	
						SecQuestionrespdesc = "";
					}
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(SecQuestionrespdesc.length()>0)
				{
					showAlert(SecQuestionrespdesc);
				}
				else{
				if(retvalweb.indexOf("SUCCESS")>-1)
				{
					post_successValidateSecuQue(retvalweb);
				}
				
				else 
				{
					//System.out.println("in else ***************************************");
					if (retvalweb.indexOf("WRONGANS") >= 0) 
					{
						retMess = getString(R.string.alert_087);
					}
					else
					{
						retMess = getString(R.string.alert_err);
					}					
					showAlert(retMess);
				}
			} }
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}

	}
	
	public 	void post_successValidateSecuQue(String retvalweb)
	{
		respcode="";
		SecQuestionrespdesc="";
		String decryptedAccounts = retvalweb.split("~")[1];
		Bundle bObj=new Bundle();
		Intent in=new Intent(ForgotPassword.this,OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",custId);
		bObj.putString("FROMACT", "FORGOT");
		in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		in.putExtras(bObj);
		startActivity(in);
		
		finish();
	
	}
	
	public void showAlert(final String str)
	{
			//Toast.makeText(this, str, Toast.LENGTH_LONG).show();	
			ErrorDialogClass alert = new ErrorDialogClass(this,""+str)
			{@Override
				public void onClick(View v)

				{
					//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
					switch (v.getId()) 
					{
						case R.id.btn_ok:
							//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
							if((str.equalsIgnoreCase(ExistingCustomerrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
							{
								post_successExistingCustomer(retvalweb);
							}
							else if((str.equalsIgnoreCase(ExistingCustomerrespdesc)) && (respcode.equalsIgnoreCase("1")))
							{
								this.dismiss();
							}
							else if((str.equalsIgnoreCase(SecQuestionrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
							{
								post_successValidateSecuQue(retvalweb);
							}
							else if((str.equalsIgnoreCase(SecQuestionrespdesc)) && (respcode.equalsIgnoreCase("1")))
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
		//Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		//Log.i("2222", "2222");
		try {
			State state = ni.getState();
			//Log.i("3333", "3333");
			boolean state1 = ni.isAvailable();
			//Log.i("4444", "4444");
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:

					//Log.i("5555", "5555");
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						gpsFlg = 1;
						flag = 0;

					}
					break;
				case DISCONNECTED:
					//Log.i("6666", "6666");
					flag = 1;
					// retMess = "Network Disconnected. Please Try Again.";
					retMess = getString(R.string.alert_000);
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
					//Log.i("7777", "7777");
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();

					dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
									Intent in = null;
									in = new Intent(getApplicationContext(),
											LoginActivity.class);
									in.putExtra("var1", var1);
									in.putExtra("var3", var3);
									startActivity(in);
									finish();
								}
							});
					dbs.get_adb().show();
					break;
				}
			} else {
				//Log.i("8888", "8888");
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();

				dbs = new DialogBox(this);
				dbs.get_adb().setMessage(retMess);
				dbs.get_adb().setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
								Intent in = null;
								in = new Intent(getApplicationContext(),
										LoginActivity.class);
								in.putExtra("var1", var1);
								in.putExtra("var3", var3);
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							in.putExtra("var1", var1);
							in.putExtra("var3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							in.putExtra("var1", var1);
							in.putExtra("var3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
		}
		return flag;
	}
}
