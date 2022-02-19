package list.shivsamarth_mbs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DeviceUtils;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class SplashPage extends Activity implements OnClickListener {
	Button continueBtn;
	TextView txt_version_no, dynamicmsg;
	String version = "", retVal = "", retMess = "", respcode = "",
			respdesc = "", retval = "", versionFlg = "";
	int flag = 0;
	private static final int SWIPE_MIN_DISTANCE = 60;
	private static final int SWIPE_THRESHOLD_VELOCITY = 400;
	private static final int REQUEST_APP_SETTINGS = 168;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";

	private static String METHOD_NAME1 = "";
	private ViewFlipper mViewFlipper;
	ImageView splash_logo;
	private AnimationListener mAnimationListener;
	private Context mContext;
	JSONArray jsonArr;
	Map<String, Object> keys = null;
	static PrivateKey var1 = null;
	SecretKeySpec var2 = null;
	static PublicKey var4 = null;
	String var5 = "", var3 = "";
	DialogBox dbs;
	public int counter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_page);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		if (!new DeviceUtils().isEmulator()) {
			MBSUtils.ifGooglePlayServicesValid(SplashPage.this);
		} else {
			MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup),SplashPage.this);
		}

		continueBtn = (Button) findViewById(R.id.continue_btn);

		dynamicmsg = (TextView) findViewById(R.id.dynamicmsg);
		dynamicmsg.setSelected(true);
		dynamicmsg.setEllipsize(TruncateAt.MARQUEE);
		dynamicmsg.setSingleLine(true);

		String macadd = MBSUtils.getMacAddressnew("eth0", SplashPage.this);// getMacAddress(LoginActivity.this);
		String macadd1 = MBSUtils.getMacAddressnew("wlan0", SplashPage.this);
		Log.e("mac adrress", macadd);
		Log.e("mac adrress", macadd1);
		try {
			keys = CryptoClass.Function1();
			var1 = (PrivateKey) keys.get("private");
			var4 = (PublicKey) keys.get("public");
		} catch (Exception e) {
			e.printStackTrace();
		}
		txt_version_no = (TextView) findViewById(R.id.txt_version_no);
		mContext = this;

		splash_logo = (ImageView) findViewById(R.id.splash_logo);

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version = pInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		continueBtn.setOnClickListener(this);
		txt_version_no.setText("Version : " + version);
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_PHONE_STATE)) {
			} else {
				ActivityCompat.requestPermissions(this,
						new String[] { Manifest.permission.READ_PHONE_STATE },
						1);
			}
		} else {
			if (chkConnectivity() == 0) {
				// new CallWebService_dynamic_msg().execute();
				CallWSFirst c = new CallWSFirst();
				c.execute();
			} else {
				showAlert(getString(R.string.alert_000));
			}
		}
	}

	/*class CallWSFirst extends AsyncTask<Void, Void, Void> {

		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(SplashPage.this);

		protected void onPreExecute() {
			loadProBarObj.show();
			try {
				jsonObj.put("METHODCODE", "85");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SplashPage.this));
				jsonObj.put("PUBLICKEY",
						new String(Base64.encodeBase64(var4.getEncoded())));
				Log.e("jsonObj", "----" + jsonObj.toString());
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
			Log.e("strvar", "----" + var3);
			try {
				loadProBarObj.dismiss();
				CallWebService_dynamic_msg dyc = new CallWebService_dynamic_msg();
				dyc.execute();
			}// try
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}// end onPostExecute

	}// end CallWebServiceGetBank*/
	class CallWSFirst extends AsyncTask<Void, Void, Void>
	{
		JSONObject jsonObj = new JSONObject();
		String version1="VERSION~"+version;
		LoadProgressBar loadProBarObj = new LoadProgressBar(SplashPage.this);
		protected void onPreExecute()
		{
			try
			{
				loadProBarObj.show();
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SplashPage.this));
				jsonObj.put("PUBLICKEY", new String(Base64.encodeBase64(var4.getEncoded())));
				jsonObj.put("METHODCODE", "85");

			}
			catch (JSONException je)
			{
				je.printStackTrace();
			}
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";
			try {
				Log.e("DSP","splashpage---version1==="+jsonObj.toString());

				SoapObject request = new SoapObject(value4, value7);
				String val=CryptoClass.Function3(jsonObj.toString(),CryptoClass.getPrivateKey());
				Log.e("SPLASHPAGE","val=="+val);
				request.addProperty("value1", val);
				request.addProperty("value2", version1);
				request.addProperty("value3", "NA");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,
						45000);
				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				var5 = var5.substring(var5.indexOf("=") + 1,
						var5.length() - 3);

			}// end try
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("Splashpage   Exception" + e);
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid)
		{
			loadProBarObj.dismiss();
			try {
				String resp=CryptoClass.Function4(var5,CryptoClass.getPrivateKey());
				Log.e("DSP","splashpage---str==="+resp);
				if(resp.indexOf("EXCEPTION")>-1)
				{
					showAlertserver(respdesc);
				}
				else if(resp.indexOf("OLDVERSION")>-1)
				{
					versionFlg="2";
					retMess = getString(R.string.alert_oldversion);
					showversionAlert(retMess);
				}
				else {
					JSONObject jsonObj = new JSONObject(resp.trim());

					if (jsonObj.has("RESPCODE")) {
						respcode = jsonObj.getString("RESPCODE");
					} else {
						respcode = "-1";
					}
					if (jsonObj.has("RESPDESC")) {
						respdesc = jsonObj.getString("RESPDESC");
					} else {
						respdesc = "";
					}

					if (respdesc.length() > 0) {
						if (respdesc.equalsIgnoreCase("Server Not Found")) {
							showAlertserver(respdesc);
						} else {
							showAlert(respdesc);
						}
					} else {
						if (respcode.equalsIgnoreCase("0")) {
							var3 = jsonObj.getString("TOKEN");
							Log.e("DSP", "splashpage---var3===" + var3);
							CallWebService_dynamic_msg c = new CallWebService_dynamic_msg();
							c.execute();
						} else if (respcode.equalsIgnoreCase("1")) {
							retMess = getString(R.string.alert_oldversion);
							showversionAlert(retMess);
						} else if (respcode.equalsIgnoreCase("2")) {
							retMess = getString(R.string.alert_179_2);
							showversionAlert(retMess);
						}
					}
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public void showAlertserver(String str) {

		// Log.e("SAM","===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						finish();
						System.exit(0);
						dismiss();
						break;
				}
				this.dismiss();

			}
		};
		alert.show();
	}

	public void showversionAlert(final String str) {
		Log.e("SAM","===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
					case R.id.btn_ok:
						if (retMess == getString(R.string.alert_oldversion))
						{
							try
							{
								Intent viewIntent = new Intent("android.intent.action.VIEW",
										Uri.parse("https://play.google.com/store/apps/details?id=list.shivsamarth_mbs"));
								startActivity(viewIntent);
							}
							catch (Exception e)
							{
								Toast.makeText(getApplicationContext(),	"Unable to Connect Try Again...",Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}
						}
						break;
				}this.dismiss();

			}
		};alert.show();

	}

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			// Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			// Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	public void onRequestPermissionsResult(int requestCode,
			String permissions[], int[] grantResults) {
		switch (requestCode) {
		case 1: {

			if (permissions[0]
					.equalsIgnoreCase(Manifest.permission.READ_PHONE_STATE)) {
				if (ContextCompat.checkSelfPermission(this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

					if (ActivityCompat.shouldShowRequestPermissionRationale(
							this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					} else {

						ActivityCompat
								.requestPermissions(
										this,
										new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
										1);
					}
				}
			} else if (permissions[0]
					.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				if (ContextCompat.checkSelfPermission(this,
						Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

					// Should we show an explanation?
					if (ActivityCompat.shouldShowRequestPermissionRationale(
							this, Manifest.permission.READ_CONTACTS)) {
					} else {

						ActivityCompat
								.requestPermissions(
										this,
										new String[] { Manifest.permission.READ_CONTACTS },
										1);
					}
				}
			} else if (permissions[0]
					.equalsIgnoreCase(Manifest.permission.READ_CONTACTS)) {
				if (ContextCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

					// Should we show an explanation?
					if (ActivityCompat.shouldShowRequestPermissionRationale(
							this, Manifest.permission.ACCESS_FINE_LOCATION)) {
					} else {

						ActivityCompat
								.requestPermissions(
										this,
										new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
										1);
					}
				}
			} else if (permissions[0]
					.equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
				if (ContextCompat.checkSelfPermission(this,
						Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

					// Should we show an explanation?
					if (ActivityCompat.shouldShowRequestPermissionRationale(
							this, Manifest.permission.CAMERA)) {
					} else {

						ActivityCompat.requestPermissions(this,
								new String[] { Manifest.permission.CAMERA }, 1);
					}
				}
			} else if (permissions[0]
					.equalsIgnoreCase(Manifest.permission.CAMERA)) {
				if (ContextCompat.checkSelfPermission(this,
						Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

					// Should we show an explanation?
					if (ActivityCompat.shouldShowRequestPermissionRationale(
							this, Manifest.permission.CAMERA)) {
					} else {

						ActivityCompat.requestPermissions(this,
								new String[] { Manifest.permission.CAMERA }, 1);
					}
				}
			}

			return;
		}
		}
	}

	@Override
	public void onClick(View arg0) {
		int i=0;
		Log.e("SAM", "ONCLICK1 ");
		if (versionFlg.equalsIgnoreCase("2")) {
			retMess = getString(R.string.alert_oldversion);
			setAlert(retMess);
		} else if ((ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
				&& (ContextCompat.checkSelfPermission(this,
						Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
				&& (ContextCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
			Log.e("versionFlg---", "versionFlg--" + versionFlg);
			/*
			 * comment by SAM 10/04/2019 Not Cheaking version flag here and no
			 * any link to going updated on play store
			 */
			// if(versionFlg.equalsIgnoreCase("0") ||
			// versionFlg.equalsIgnoreCase("2"))
			{
				Log.e("SAM2", "ONCLICK if 2 ");

				Intent in = new Intent(this, LoginActivity.class);
				in.putExtra("var1", var1);
				in.putExtra("var3", var3);
				startActivity(in);
				finish();
			}
			/*
			 * else { setAlert(getString(R.string.alert_oldversion)); }
			 */
		} else {
			Log.e("SAM==else", "ONCLICK ELSE ");

			showAlertNW("Please Grant All Permissions And Restart The Application");

		}
	}

	private void goToSettings() {
		Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				Uri.parse("package:" + getPackageName()));
		i.addCategory(Intent.CATEGORY_DEFAULT);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivityForResult(i, REQUEST_APP_SETTINGS);
	}

	public void showAlertNW(String str) {

		Log.e("SAM", "===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					goToSettings();
					// finish();
					dismiss();
					break;
				}
				this.dismiss();

			}
		};
		alert.show();
	}

	public void showAlert(String str) {

		Log.e("SAM", "===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:

					dismiss();
					break;
				}
				this.dismiss();

			}
		};
		alert.show();
	}

	public void onBackPressed() {
		showlogoutAlertbtn(getString(R.string.lbl_007));
		/*DialogBox dbs = new DialogBox(this);
		dbs.get_adb().setMessage(getString(R.string.lbl_007));
		dbs.get_adb().setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// startActivity(lang_activity);
						finish();
						System.exit(0);
					}
				});
		dbs.get_adb().setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
		dbs.get_adb().show();*/
	}
	public void showlogoutAlertbtn(final String str)
	{
		CustomDialogClass alert = new CustomDialogClass(SplashPage.this,str)
		{
			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						finish();
						System.exit(0);
						break;

					case R.id.btn_cancel:

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
	class CallWebService_dynamic_msg extends AsyncTask<Void, Void, Void> {

		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(SplashPage.this);

		@Override
		protected void onPreExecute() {
			try {
				loadProBarObj.show();
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SplashPage.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(SplashPage.this));
				jsonObj.put("VERSION", version);
				jsonObj.put("METHODCODE", "53");
			} catch (JSONException je) {
				je.printStackTrace();
			}

			// System.out.println("generatedXML" + generatedXML);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";

			try {
				String keyStr = CryptoClass.Function2();
				var2 = CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
				request.addProperty("value1",
						CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2",
						CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.e("DAAAAAAAAAAAAAAAA", "INside  Post Excute");
			// String[] xmlTags = { "PARAMS" };
			// String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);
			// Log.e("SAM", "data :" + xml_data[0]);
			JSONObject jsonObj;
			try {
				String str = CryptoClass.Function6(var5, var2);
				// loadProBarObj.dismiss();
				Log.e("IN return", "data :" + str.trim());
				jsonObj = new JSONObject(str.trim());
				loadProBarObj.dismiss();
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					retval = jsonObj.getString("RETVAL");
				} else {
					retval = "";
				}
				if (jsonObj.has("RESPDESC")) {
					respdesc = jsonObj.getString("RESPDESC");
				} else {
					respdesc = "";
				}
				if (jsonObj.has("VERSIONFLG")) {
					versionFlg = jsonObj.getString("VERSIONFLG");
				} else {
					versionFlg = "-1";
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (respdesc.length() > 0) {
				showAlert(respdesc);
			} else {
				Log.e("versionFlg==", "versionFlg===" + versionFlg);
				if (versionFlg.length() > 0
						&& versionFlg.equalsIgnoreCase("1")) {
					showUpdateAlert(getString(R.string.alert_oldversionupdate));
				}
				else if (versionFlg.length() > 0
						&& versionFlg.equalsIgnoreCase("2")) {
					retMess = getString(R.string.alert_oldversion);
					setAlert(retMess);
				}
				else if (retval.indexOf("FAILED") > -1) {
					post_success(retval);
				}
				else {

							try {
								Log.e("DDDDD1111", "TRY======" + retval);
								JSONArray ja = new JSONArray(retval);

								Log.e("DDDDD2222", "DATA" + ja);
								int count = 0;
								String data = "";
								for (int j = 0; j < ja.length(); j++) {
									JSONObject jObj = ja.getJSONObject(j);

									Log.e("LIST=", "length=" + data.length());
									if (data.length() == 0) {
										data = jObj.getString("mm_msg");
									} else {
										data = data + ".    "
												+ jObj.getString("mm_msg") + ".   ";
									}
									count++;
									Log.e("LIST=", "Count1111111111=" + count);
									Log.e("LIST=", "Count22222222222=" + count);
								}
								data = MBSUtils.lPad(data, 51, " ");
								// data=data+""+data;
								Log.e("LIST=", "Count=" + count);
								Log.e("data=", "data=" + data);
								dynamicmsg.setText(data);
								Log.e("DDDDD1111", "DATa" + data);

								Log.e("DDDDDDDDDD", "DyanmicMsg" + dynamicmsg);

							} catch (JSONException je) {
								je.printStackTrace();
							}
				}

			}
		}
	}

	public void post_success(String retval) {
		respcode = "";
		respdesc = "";
		Log.e("FAILED= ", "FAILED=");
	}

	public void showUpdateAlert(final String str) {
		CustomDialogClass alert = new CustomDialogClass(SplashPage.this, str) {

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if (str.equalsIgnoreCase(getString(R.string.alert_oldversionupdate))) {
						try {
							Intent viewIntent = new Intent(
									"android.intent.action.VIEW",
									Uri.parse("https://play.google.com/store/apps/details?id=list.shivsamarth_mbs"));
							startActivity(viewIntent);
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(),
									"Unable to Connect Try Again...",
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
					break;

				case R.id.btn_cancel:
					if (retval.indexOf("FAILED") > -1) {
						post_success(retval);
					} else {
						try {
							Log.e("DDDDD1111", "TRY");
							JSONArray ja = new JSONArray(retval);

							Log.e("DDDDD2222", "DATA" + ja);
							int count = 0;
							String data = "";
							for (int j = 0; j < ja.length(); j++) {
								JSONObject jObj = ja.getJSONObject(j);

								Log.e("LIST=", "length=" + data.length());
								if (data.length() == 0) {
									data = jObj.getString("mm_msg");
								} else {
									data = data + ".    "
											+ jObj.getString("mm_msg") + ".   ";
								}
								count++;
								Log.e("LIST=", "Count1111111111=" + count);
								Log.e("LIST=", "Count22222222222=" + count);
							}
							data = MBSUtils.lPad(data, 51, " ");
							// data=data+""+data;
							Log.e("LIST=", "Count=" + count);
							Log.e("data=", "data=" + data);
							dynamicmsg.setText(data);
							Log.e("DDDDD1111", "DATa" + data);

							Log.e("DDDDDDDDDD", "DyanmicMsg" + dynamicmsg);
						} catch (JSONException je) {
							je.printStackTrace();
						}
					}
					break;
				default:
					break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	public void setAlert(final String str) {
		/*dbs = new DialogBox(this);
		dbs.get_adb().setMessage(retMess);
		dbs.get_adb().setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
						if (retMess == getString(R.string.alert_oldversion)) {
							try {
								Intent viewIntent = new Intent(
										"android.intent.action.VIEW",
										Uri.parse("https://play.google.com/store/apps/details?id=list.sadguru_mbs"));
								startActivity(viewIntent);
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(),
										"Unable to Connect Try Again...",
										Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}
						}

					}
				});
		dbs.get_adb().show();*/

		ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						if (str == getString(R.string.alert_oldversion))
						{
							try
							{
								Intent viewIntent = new Intent(
										"android.intent.action.VIEW",
										Uri.parse("https://play.google.com/store/apps/details?id=list.shivsamarth_mbs"));
								startActivity(viewIntent);
							}
							catch (Exception e)
							{
								Toast.makeText(getApplicationContext(),	"Unable to Connect Try Again...",Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}
						}
						break;
				}
				this.dismiss();

			}
		};
		alert.show();
	}
}
