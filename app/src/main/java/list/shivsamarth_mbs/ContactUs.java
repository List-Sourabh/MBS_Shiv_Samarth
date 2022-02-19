package list.shivsamarth_mbs;


import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.MBSUtils;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.json.JSONException;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("NewApi") 
public class ContactUs extends Activity implements OnClickListener
{
	Activity act;
	ContactUs contactUsObj;
	ImageButton btn_home, btn_back;
	TextView txt_heading;
	ImageView img_heading;
	
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME_GET_CONF = "";
	
	TextView txt_bank_email,txt_bank_phone_number,txt_bank_web_url;
	
	
	String custId="",retMess="",retVal="",bankUrl="",bankPhoneNo="",bankEmail="", retval = "",respcode="",respdesc="";
	int cnt = 0, flag = 0;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public ContactUs() {
		//Log.e("ContactUs","Default CONSTR");
		act = this;
	}

	public ContactUs(Activity a) {
		Log.e("ContactUs","CONSTR");
		act = a;
		contactUsObj = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act=this;
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		txt_bank_email=(TextView)findViewById(R.id.txt_bank_email);
		txt_bank_phone_number=(TextView)findViewById(R.id.txt_bank_phone_number);
		txt_bank_web_url=(TextView)findViewById(R.id.txt_bank_web_url);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		img_heading=(ImageView)findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.contact);
		txt_heading.setText(getString(R.string.lbl_contact_us));
		btn_back.setOnClickListener(this);
		txt_bank_email.setOnClickListener(this);
		txt_bank_phone_number.setOnClickListener(this);
		txt_bank_web_url.setOnClickListener(this);
		
		flag = chkConnectivity();
		if (flag == 0) 
		{
			CallWebServiceGetConfiguration C = new CallWebServiceGetConfiguration();
			C.execute();
		}
	}
		
	@Override
	public void onClick(View arg0) 
	{
		Intent in=null;
		switch (arg0.getId()) 
		{
			case R.id.txt_bank_email:
				in = new Intent (Intent.ACTION_VIEW, Uri.parse("mailto:" +bankEmail));	
				startActivity(in);
				break;
			case R.id.txt_bank_phone_number:
				if(bankPhoneNo.indexOf(",")>-1)
				{
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
				}
				else
				{
					in = new Intent(Intent.ACTION_DIAL);
					in.setData(Uri.parse("tel:"+bankPhoneNo));
					startActivity(in);
				}
				break;
			case R.id.txt_bank_web_url:
				
				if (!bankUrl.startsWith("http://") && !bankUrl.startsWith("https://")) 
					bankUrl = "http://" + bankUrl;
				
				in = new Intent(Intent.ACTION_VIEW, Uri.parse(bankUrl)); 
				startActivity(in); 
				break;
			case R.id.btn_back:
				in = new Intent(this,LoginActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);			
				finish();
				break;	
			default:
				break;
		}
	}
	
	public void onBackPressed() {
		Intent in = new Intent(this, LoginActivity.class);
		in.putExtra("var1", var1);
		   in.putExtra("var3", var3);
			startActivity(in);
			finish();
	}

	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
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
	
	class CallWebServiceGetConfiguration extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		
		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {
			try{
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			
            jsonObj.put("CUSTID", custId);
            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
            jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
            jsonObj.put("METHODCODE","43"); 
			//valuesToEncrypt[0] = custId;
			//valuesToEncrypt[1] = MBSUtils.getImeiNumber(act);
			}
			 catch (JSONException je) {
	                je.printStackTrace();
	            }
			
		}

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
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) //"CONTACTSDTLS" 
		{
			
			//String decryptedBeneficiaries = xml_data[0];
			loadProBarObj.dismiss();
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
			
			//decryptedBeneficiaries="SUCCESS";
			//Log.e("CONTACTUS","decryptedBeneficiaries=="+decryptedBeneficiaries);
			if(retval.indexOf("FAILED")>-1)
			{
				showAlert(getString(R.string.alert_133));
			}
			else
			{
				post_success(retval);
							
			}
				}
		}// end onPostExecute

	}// end CallWebServiceGetConfiguration
	
	public 	void post_success(String retval)
	{
		try 
		{
			respcode="";
			respdesc="";
			JSONObject jObj= new JSONObject(retval);
			bankUrl=jObj.getString("URL");
			bankPhoneNo=jObj.getString("PHONE");
			bankEmail=jObj.getString("EMAIL");
			
			txt_bank_email.setText(bankEmail);
			txt_bank_phone_number.setText(bankPhoneNo);
			txt_bank_web_url.setText(bankUrl);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
		
	}
	
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			
			if (state1) {
				switch (state) {
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
					retMess = getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				Log.e("chkConnectivity","7");
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("BalanceEnquiry    mayuri", "NullPointerException Exception"
					+ ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}// end chkConnectivity
	
	public class InputDialogBox extends Dialog implements OnClickListener {
		Spinner spi_select_phone;
		Button btn_dial;
		//ImageButton btn_spnr_phone;
		

		public InputDialogBox(Activity activity) {
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) 
		{
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.phone_select_design);
			spi_select_phone = (Spinner) findViewById(R.id.spnr_select_phone);
			//btn_spnr_phone=(ImageButton)findViewById(R.id.btn_spnr_phone);
			btn_dial = (Button) findViewById(R.id.btn_dial);
			btn_dial.setVisibility(Button.VISIBLE);
			btn_dial.setOnClickListener(this);
			//btn_spnr_phone.setOnClickListener(this);
			
			String[] phoneNoArr = bankPhoneNo.split(",");
			
			ArrayAdapter<String> bankNames = new ArrayAdapter<String>(act,R.layout.spinner_item, phoneNoArr);
			bankNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_select_phone.setAdapter(bankNames);
		}

		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case R.id.btn_dial:
					try 
					{				
						String str = spi_select_phone.getSelectedItem().toString();
						Intent in = new Intent(Intent.ACTION_DIAL);
						in.setData(Uri.parse("tel:"+str));
						startActivity(in);
						this.hide();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
					}
					break;
				/*case R.id.btn_spnr_phone:
					spi_select_phone.performClick();
					break;*/
				default:
					break;
			}
		}// end onClick
	}// end InputDialogBox
}
