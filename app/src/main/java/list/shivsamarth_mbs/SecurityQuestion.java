package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SecurityQuestion extends Activity implements OnClickListener
{
	int cnt = 0, flag = 0;
	int netFlg, gpsFlg;
	DialogBox dbs;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	ImageView img_heading;
	Spinner security_que1,security_que2;
	EditText txt_secu_que1,txt_secu_que2;
	Button btn_submit_secu_que;
	ImageButton btn_home,btn_back,spinner_btn,spinner_btn2;
	TextView txt_heading;
	JSONArray jsonArr;
	String que_one,que_two,ans_one,ans_two,strCustId,strOTP,strRefId,strFromAct,strMobNo;
	String qOne="",qTwo="";
	String retVal,retMess,respcode="",retval="",respdesc="";
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security_question);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.register);
		Bundle b1=new Bundle();
		b1=getIntent().getExtras();
		if(b1!=null)
		{
			strCustId=b1.getString("CUSTID");
			strOTP=b1.getString("OTPVAL");
			strRefId=b1.getString("REFID");
			strFromAct=b1.getString("FROMACT");
			strMobNo = b1.getString("MOBNO");
		}
		
		dbs = new DialogBox(this);
		security_que1=(Spinner)findViewById(R.id.security_que1);
		security_que2=(Spinner)findViewById(R.id.security_que2);
		
		txt_secu_que1=(EditText)findViewById(R.id.edttxt_security_que1);
		txt_secu_que2=(EditText)findViewById(R.id.edttxt_security_que2);
		
		btn_submit_secu_que=(Button)findViewById(R.id.btn_submit_secu_que);
		spinner_btn=(ImageButton)findViewById(R.id.spinner_btn);
		spinner_btn2=(ImageButton)findViewById(R.id.spinner_btn2);
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		
		//btn_home.setImageResource(R.mipmap.bank_logo);
		btn_back.setImageResource(R.mipmap.backover);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_security_que));
		spinner_btn.setOnClickListener(this);
		spinner_btn2.setOnClickListener(this);
		btn_submit_secu_que.setOnClickListener(this);
		//btn_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		//:3#What is your birth place?~1#What is your favourite color? ~
		flag = chkConnectivity();
		if (flag == 0)
		{
			CallWebServiceSecuQue c=new CallWebServiceSecuQue();
			c.execute();
		}
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_submit_secu_que:
				
				ans_one=txt_secu_que1.getText().toString().trim();
				ans_two=txt_secu_que2.getText().toString().trim();
				que_one=security_que1.getSelectedItem().toString();
				que_two=security_que2.getSelectedItem().toString();
				if((ans_one==null || ans_two==null)||(ans_one.length()==0 || ans_two.length()==0))
				{
					showAlert(getString(R.string.alert_ans_secQue));
				}
				else
				{
					if(que_one.equalsIgnoreCase("--Select Question--") || que_two.equalsIgnoreCase("--Select Question--"))
					{
						showAlert(getString(R.string.alert_selQue));
					}
					else if(que_one.equalsIgnoreCase(que_two))
					{
						showAlert(getString(R.string.alert_diffQue));
					}
					else
					{
						try 
						{
							for(int k=0;k<jsonArr.length();k++)
							{
								JSONObject obj=jsonArr.getJSONObject(k);
								if(obj.getString("QUEDESC").equalsIgnoreCase(que_one))
									qOne=obj.getString("QUECD");
								else if(obj.getString("QUEDESC").equalsIgnoreCase(que_two))
									qTwo=obj.getString("QUECD");
									
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//showAlert("ans_one=="+ans_one+"==ans_two=="+ans_two);	
						Bundle bObj=new Bundle();
						Intent in=new Intent(this, SetMPIN.class);
						bObj.putString("CUSTID", strCustId);
						bObj.putString("MOBNO", strMobNo);
						bObj.putString("QUE1", qOne);
						bObj.putString("QUE2", qTwo);
						bObj.putString("ANSWR1", ans_one);
						bObj.putString("ANSWR2", ans_two);
						bObj.putString("FROMACT", "REGISTER");
						
						//Log.e("SECUQUE","strCustId=="+strCustId);
						//Log.e("SECUQUE","strMobNo=="+strMobNo);
						//Log.e("SECUQUE","qOne=="+qOne);
						//Log.e("SECUQUE","qTwo=="+qTwo);
						//Log.e("SECUQUE","ans_one=="+ans_one);
						//Log.e("SECUQUE","ans_two=="+ans_two);
						///Log.e("SECUQUE","REGISTER==");
						in.putExtra("var1", var1);
						   in.putExtra("var3", var3);
						in.putExtras(bObj);
						startActivity(in);
						finish();
					}
				}
			break;

			case R.id.btn_back:
				Intent in=new Intent(this,LoginActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case R.id.spinner_btn: 
				security_que1.performClick();
				break;
			case R.id.spinner_btn2: 		
				security_que2.performClick();
				break;
			 
		default:
			break;
		}
	}
	
	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
	{@Override
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
	
	public void onBackPressed() {
		Intent in=new Intent(this,LoginActivity.class);
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
				startActivity(in);	
				finish();
	}

	class CallWebServiceSecuQue extends AsyncTask<Void, Void, Void> {

		
		LoadProgressBar loadProBarObj = new LoadProgressBar(SecurityQuestion.this);
	
		boolean isWSCalled = false;
		JSONObject jsonObj = new JSONObject();
		
		@Override
		protected void onPreExecute() { 
			loadProBarObj.show();
            respcode="";
            retval="";
            respdesc="";
			try
			{
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SecurityQuestion.this));
				jsonObj.put("METHODCODE","21");
			}
			catch(JSONException je)
			{
				je.printStackTrace();
			}
			
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
			int count = 0;
			if (isWSCalled) {
			
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
				}
			/*	Log.e("SecurityQuestion", "xml_data.length==" + xml_data.length);
				ArrayList<String> arrList = new ArrayList<String>();
				arrList.add("--Select Question--");*/
	} 
			else 
			{
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}

	}
	
	public 	void post_success(String retval){
		respcode="";
		respdesc="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select Question--");
				try {
					JSONArray ja = new JSONArray(retval);
					jsonArr = ja;
					if (ja.length() == 0)// xml_data[0].indexOf("FAILED")>-1)
					{
						showAlert(getString(R.string.alert_084));
					} else {
						for (int j = 0; j < ja.length(); j++) {
							JSONObject jObj = ja.getJSONObject(j);
							arrList.add(jObj.getString("QUEDESC"));
							count++;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (count > 0) {
					String[] seqQuesArr = new String[arrList.size()];
					seqQuesArr = arrList.toArray(seqQuesArr);
					/*
					 * CustomeSpinnerAdapter debAccs = new
					 * CustomeSpinnerAdapter(SecurityQuestion.this,
					 * R.layout.spinner_layout, debAccArr);
					 */
					ArrayAdapter<String> seqQues = new ArrayAdapter<String>(SecurityQuestion.this,R.layout.spinner_item, seqQuesArr);
					/*CustomeSpinnerAdapter seqQues = new CustomeSpinnerAdapter(
							SecurityQuestion.this,
							R.layout.spinner_item, seqQuesArr);*/
					seqQues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					security_que1.setAdapter(seqQues);
					security_que2.setAdapter(seqQues);
				}
			/*} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}*/

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
			Log.i("4444", "4444");
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
