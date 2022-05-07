package list.shivsamarth_mbs;


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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class SessionOut extends Activity implements OnClickListener {
	Button continueBtn;
	TextView txt_version_no;
	TextView txt_welcome,dyn_msg;
	String version = "",retVal = "",retMess="";;
	private static final int SWIPE_MIN_DISTANCE = 60;
	private static final int SWIPE_THRESHOLD_VELOCITY = 400;
	private ViewFlipper mViewFlipper;
	ImageView splash_logo;
	private AnimationListener mAnimationListener;
	private Context mContext;
	DialogBox dbs;
	int cnt = 0, flag = 0;
	int netFlg, gpsFlg;
	String retval = "",respcode="",respdesc="";
	private static final int REQUEST_APP_SETTINGS = 168;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	Map<String, Object> keys = null;
	static PrivateKey var1 = null;
	SecretKeySpec var2 = null;
	static PublicKey var4 = null;
	String var5 = "", var3 = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_page_out);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		continueBtn = (Button) findViewById(R.id.continue_btn);
		
		txt_version_no = (TextView) findViewById(R.id.txt_version_no);
		dyn_msg = (TextView) findViewById(R.id.dynamicmsg);
		//dyn_msg.setTextSize(15);
		//dyn_msg.setText(getString(R.string.alert_session));
		//txt_welcome=(TextView)findViewById(R.id.welcome);
		//txt_welcome.setText(getString(R.string.alert_session));
		//txt_version_no.setVisibility(View.INVISIBLE);
	 		
		continueBtn.setOnClickListener(this);
		//txt_version_no.setText("Version : " + version);
		
		dbs = new DialogBox(this);
	try {
			keys = CryptoClass.Function1();
			var1 = (PrivateKey) keys.get("private");
			var4 = (PublicKey) keys.get("public");
		} catch (Exception e) {
			e.printStackTrace();
		}
		flag = chkConnectivity();
		if (flag == 0) {
		CallWSFirst c = new CallWSFirst();
		c.execute();
		}
	}
	
	
	@Override
	public void onClick(View arg0) {
		
		Log.e("SAM","ONCLICK1 ");
		Log.e("SAM2","ONCLICK if 2 ");
		Intent in = new Intent(this, SplashPage.class);
	in.putExtra("var1", var1);
		in.putExtra("var3", var3);
		startActivity(in);
		finish();
	}
	
   
	public void onBackPressed() {
			DialogBox dbs = new DialogBox(this);
			dbs.get_adb().setMessage(getString(R.string.lbl_007));
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							//startActivity(lang_activity);
							//finish();
							System.exit(0);
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
						}
					});
			dbs.get_adb().show();
		}
		
	
	class CallWSFirst extends AsyncTask<Void, Void, Void> {
			String[] xmlTags = { "PARAMS" };
			String[] valuesToEncrypt = new String[1];
			String generatedXML = "";
			String ValidationData = "";
			JSONObject jsonObj = new JSONObject();

			protected void onPreExecute() {
				try {
					jsonObj.put("METHODCODE", "85");
					jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SessionOut.this));
					jsonObj.put("PUBLICKEY",
							new String(Base64.encodeBase64(var4.getEncoded())));
					Log.e("jsonObj","----"+jsonObj.toString());
				} catch (JSONException je) {
					je.printStackTrace();
				}
			}

			protected Void doInBackground(Void... arg0) {
				String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "callWebservice";

				try {
					SoapObject request = new SoapObject(value4, value7);
					request.addProperty("value1", jsonObj.toString());
					request.addProperty("value2", "NA");
					request.addProperty("value3", "NA");
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(
							value6, 45000);
					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
				}// end try
				catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception 2");
					System.out.println("SameBankTransfer   Exception" + e);
				}
				return null;
			}// end doInBackground

			protected void onPostExecute(Void paramVoid) // "BANKNAMES"
			{
				var3 = var5;// xml_data[0];
	Log.e("strvar","----"+var3);
				try {
					continueBtn.setEnabled(true);
					
				}// try
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}// end onPostExecute

		}// end CallWebServiceGetBank
	
		public int chkConnectivity() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			try {
				State state = ni.getState();
				boolean state1 = ni.isAvailable();
				// System.out.println("state1 ---------" + state1);
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
		
		public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(SessionOut.this,""+str);
		alert.show();
	}
}
