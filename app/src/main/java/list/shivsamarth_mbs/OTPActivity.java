package list.shivsamarth_mbs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CryptoUtil;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;

public class OTPActivity extends Activity implements OnClickListener {
    int cnt = 0, flag = 0;
    int netFlg, gpsFlg;
    public EditText txt_otp;
    DialogBox dbs;
    TextView txt_ref_id;
    public String strRefId;
    TextView txt_heading;
    ImageButton btn_back;
    ImageView img_heading;
    Button btn_otp_submit, btn_otp_resend;
    String strOTP, retMess, retVal, strCustId, strFromAct, strRetVal, strMobNo, stratm = "";
    String strActno = "", cardno = "", strimeino = "", catdstatus = "", version = "", respcode = "", retval = "", respdesc = "",
            respdesc_resend_otp = "", respdesc_SaveATMCard = "", respdescvalidate = "", respdescresend = "", respdescgent = "", respdescsendcust = "";

    String from_activity = "", customer_id = "";
    String otp = "";
    String imeino = "";
    public String refno = "";
    private String[] presidents;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    private static String METHOD_NAME1 = "";
    private static String METHOD_NAME2 = "";
    private static String METHOD_NAME3 = "";
    private static String responseJSON = "NULL";
    TelephonyManager telephonyManager;
    String imeiNo = "";
    private MyThread t1;
    int timeOutInSecs = 300;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    public int counter;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_activity);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
        var3 = (String) getIntent().getSerializableExtra("var3");
        txt_otp = (EditText) findViewById(R.id.txt_otp);
        txt_ref_id = (TextView) findViewById(R.id.txt_ref_id);
        btn_otp_submit = (Button) findViewById(R.id.btn_otp_submit);
        btn_otp_resend = (Button) findViewById(R.id.btn_otp_resend);
        txt_heading = (TextView) findViewById(R.id.txt_heading);
        //textView = (TextView) findViewById(R.id.textView);
        txt_heading.setText(getString(R.string.lbl_otp_validtn));
        img_heading = (ImageView) findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.otp);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setImageResource(R.mipmap.backover);
        btn_back.setOnClickListener(this);
        btn_otp_submit.setOnClickListener(this);
        btn_otp_resend.setOnClickListener(this);
        btn_otp_resend.setClickable(false);

        dbs = new DialogBox(this);
        presidents = getResources().getStringArray(R.array.Errorinwebservice);
        Bundle bObj = getIntent().getExtras();
        if (bObj != null) {
            strCustId = bObj.getString("CUSTID");
            strFromAct = bObj.getString("FROMACT");
            strRetVal = bObj.getString("RETVAL");
            strMobNo = bObj.getString("MOBNO");

            Log.e("strRetVal=", "strRetVal-----" + strRetVal);

            String val[] = strRetVal.split("!!");

            strRefId = val[2];

            Log.e("strRefId=", "strRefId-----" + strRefId);

            showAlert(getString(R.string.alert_otp_msg) + val[2]);

            txt_ref_id.setText(txt_ref_id.getText().toString() + " :" + val[2]);
        }
        /*
         * Button ombutButton=(Button)findViewById(R.id.omkar);
         * ombutButton.setOnClickListener(new OnClickListener() {
         *
         * @Override public void onClick(View arg0) { // TODO Auto-generated
         * method stub Cursor cursor =
         * getContentResolver().query(Uri.parse("content://sms/inbox"), null,
         * "read = 0 and body like '%Narad%'", null, null);
         *
         * if (cursor.moveToFirst()) { // must check the result to prevent
         * exception String body=""; String number=""; do { String msgData = "";
         * for(int idx=0;idx<cursor.getColumnCount();idx++) { msgData += " " +
         * cursor.getColumnName(idx) + ":" + cursor.getString(idx); body =
         * cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
         * number =
         * cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
         * } Toast.makeText(OTPActivity.this, body + "From =="+number,
         * Toast.LENGTH_LONG).show(); // use msgData } while
         * (cursor.moveToNext()); } else { // empty box, no SMS } } });
         */
/*		br = new SMSReceiver(OTPActivity.this);
		registerReceiver(br, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));*/

		/*Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Log.e("DSP", "resendtimeout====");
				btn_otp_resend.setClickable(true);
				Toast.makeText(OTPActivity.this, "Resend OTP",Toast.LENGTH_SHORT).show();
			}
		}, 30000);*/

        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long sec = (millisUntilFinished / 1000) % 60;
                String bttext = getString(R.string.resend_OTP) + " in " + f.format(sec) + " Seconds";
                btn_otp_resend.setText(bttext);
            }

            public void onFinish() {
                Log.e("DSP", "resendtimeout====");
                btn_otp_resend.setText(getString(R.string.resend_OTP));
                btn_otp_resend.setClickable(true);
                //Toast.makeText(OTPActivity.this, "Resend OTP",Toast.LENGTH_SHORT).show();
            }
        }.start();
        t1 = new MyThread(timeOutInSecs, this, var1, var3);
        t1.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_otp_submit:
                strOTP = txt_otp.getText().toString().trim();
                if (strOTP.length() != 6) {
                    showAlert(getString(R.string.alert_076));
                } else {
                    // sam flag = chkConnectivity();
                    // sam if (flag == 0)
                    {
                        if (strFromAct.equalsIgnoreCase("IMEIDIFF")) {
                            CallWebServiceValidateOTPDiffIMEI c = new CallWebServiceValidateOTPDiffIMEI();
                            c.execute();
                        }
					/*else if (strFromAct.equalsIgnoreCase("GetCustID")) {
						CallWebServiceSendCustId s = new CallWebServiceSendCustId();
						s.execute();
					}*/
                        else {
                            CallWebServiceValidateOTP c = new CallWebServiceValidateOTP();
                            c.execute();
                        }
                    }

                }
                break;
            case R.id.btn_otp_resend:
                // flag = chkConnectivity();
                // if (flag == 0)
            {
                CallWebService_resend_otp c = new CallWebService_resend_otp();
                c.execute();
            }
            break;
            case R.id.btn_back:
				Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
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

    public void showAlert(final String str) {
        // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {
            @Override
            public void onClick(View v) {
                // Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:
                        // Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
                        if ((str.equalsIgnoreCase(respdescvalidate))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_successvalidate(retval);
                        } else if ((str.equalsIgnoreCase(respdescvalidate))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescresend))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_successresend(retval);
                        } else if ((str.equalsIgnoreCase(respdescresend))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescsendcust))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_successsendcust(retval);
                        } else if ((str.equalsIgnoreCase(respdescsendcust))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else
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

    public void showAlert1(String str) {
        // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass1 alert = new ErrorDialogClass1(this, "" + str);
        alert.show();
    }

    public class ErrorDialogClass1 extends Dialog implements OnClickListener {

        public ErrorDialogClass1(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        private Context activity;
        private Dialog d;
        private Button ok;
        private TextView txt_message;
        public String textMessage;

        public ErrorDialogClass1(Context activity, String textMessage) {
            super(activity);
            this.textMessage = textMessage;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.custom_dialog);
            ok = (Button) findViewById(R.id.btn_ok);
            txt_message = (TextView) findViewById(R.id.txt_dia);
            txt_message.setText(textMessage);
            ok.setOnClickListener(this);
        }// end onCreate

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_ok:

                    if (strFromAct.equalsIgnoreCase("ENABLEATM")) {

                        Intent in = new Intent(OTPActivity.this, MainActivity.class);
                        Bundle b1 = new Bundle();
                        b1.putInt("FRAGINDEX", 7);
                        in.putExtra("var1", var1);
                        in.putExtra("var3", var3);
                        in.putExtras(b1);
                        startActivity(in);
                        finish();
                    } else {

                        Intent in = new Intent(OTPActivity.this,
                                LoginActivity.class);
                        in.putExtra("var1", var1);
                        in.putExtra("var3", var3);

                        startActivity(in);
                        finish();
                    }
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }// end class

    public void onBackPressed() {
        Intent in = new Intent(this, LoginActivity.class);
        in.putExtra("var1", var1);
        in.putExtra("var3", var3);
        startActivity(in);
        finish();
    }

    class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {
        /*
         * String[] xmlTags =
         * {"CUSTID","OTPVAL","IMEINO","REFID","ISREGISTRATION"}; String[]
         * valuesToEncrypt = new String[5];
         */


        JSONObject jsonObj = new JSONObject();

        LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);

        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();

            strOTP = txt_otp.getText().toString().trim();
            strRefId = txt_ref_id.getText().toString().trim();
            // showAlert(strRefId.substring(strRefId.indexOf(":")+1));
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();

            try {
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("OTPVAL", strOTP);
                //ListEncryption.encryptData(strOTP + strCustId));
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
                jsonObj.put("REFID", strRefId);
                jsonObj.put("ISREGISTRATION", "Y");
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
                jsonObj.put("METHODCODE", "20");
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "webServiceTwo";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
                isWSCalled = true;
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {

                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    Log.e("valdateotp", "====" + str.toString());
                    jsonObj = new JSONObject(str.trim());
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
                        respdescvalidate = jsonObj.getString("RESPDESC");
                    } else {
                        respdescvalidate = "";
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (respdescvalidate.length() > 0) {
                    showAlert(respdescvalidate);
                } else {
                    if (retval.indexOf("SUCCESS") > -1) {
                        post_successvalidate(retval);
                    } else {
                        showAlert(getString(R.string.alert_076));
                    }
                }
            } else {
                showAlert(getString(R.string.alert_000));
            }
        }
    }

    public void post_successvalidate(String retval) {

        respdescvalidate = "";
        respcode = "";
        String decryptedAccounts = retval;// xml_data[0];

        if (strFromAct.equalsIgnoreCase("REGISTER")) {

            Bundle b1 = new Bundle();
            Intent in = new Intent(OTPActivity.this, SecurityQuestion.class);
            b1.putString("CUSTID", strCustId);
            b1.putString("OTPVAL", strOTP);
            b1.putString("REFID", strRefId);
            b1.putString("FROMACT", strFromAct);
            b1.putString("MOBNO", strMobNo);
            in.putExtra("var1", var1);
            in.putExtra("var3", var3);
            in.putExtras(b1);
            startActivity(in);
            finish();
        } else if (strFromAct.equalsIgnoreCase("FORGOT")) {

            Bundle b1 = new Bundle();
            Intent in = new Intent(OTPActivity.this, SetMPIN.class);
            b1.putString("CUSTID", strCustId);
            b1.putString("OTPVAL", strOTP);
            b1.putString("REFID", strRefId);
            b1.putString("FROMACT", strFromAct);
            b1.putString("USERNAME", retval.split("~")[1]);
            in.putExtra("var1", var1);
            in.putExtra("var3", var3);
            in.putExtras(b1);
            startActivity(in);
            finish();
        } else if (strFromAct.equalsIgnoreCase("ENABLEATM")) {

            Log.e("strActno", "strActnostrActno" + strActno);
            Log.e("cardno", "cardnocardno" + cardno);
            Log.e("catdstatus", "catdstatuscatdstatus" + catdstatus);
            Log.e("strimeino", "strimeinostrimeino" + strimeino);
            Log.e("strCustId", "strCustId" + strCustId);

            new CallWebServiceSaveATMCard().execute();

        } else if (strFromAct.equalsIgnoreCase("IMEIDIFF")) {
            Intent in = new Intent(OTPActivity.this, LoginActivity.class);
            in.putExtra("var1", var1);
            in.putExtra("var3", var3);
            startActivity(in);
            finish();
        } else if (strFromAct.equalsIgnoreCase("GetCustID")) {

            CallWebServiceSendCustId s = new CallWebServiceSendCustId();
            s.execute();
        }
    }

    public void post_successresend(String retval) {

        respdescresend = "";
        respcode = "";
        String decryptedAccounts = retval.split("~")[1];
        Bundle bObj = new Bundle();
        Intent in = new Intent(OTPActivity.this, OTPActivity.class);
        bObj.putString("RETVAL", decryptedAccounts);
        bObj.putString("CUSTID", strCustId);
        bObj.putString("MOBNO", strMobNo);
        bObj.putString("FROMACT", strFromAct);
        in.putExtra("var1", var1);
        in.putExtra("var3", var3);
        in.putExtras(bObj);
        startActivity(in);
        finish();
    }

    public void post_successsendcust(String retval) {

        respdescsendcust = "";
        respcode = "";
        showAlert1(getString(R.string.alert_send_custID));
    }

    public void post_success(String retval) {
        respcode = "";
        respdesc = "";
        Log.e("Sudarshan", "post_success==" + retval);
        String decryptedAccounts = retval;

        if (strFromAct.equalsIgnoreCase("REGISTER")) {

            Bundle b1 = new Bundle();
            Intent in = new Intent(OTPActivity.this, SecurityQuestion.class);
            b1.putString("CUSTID", strCustId);
            b1.putString("OTPVAL", strOTP);
            b1.putString("REFID", strRefId);
            b1.putString("FROMACT", strFromAct);
            b1.putString("MOBNO", strMobNo);
            in.putExtra("var1", var1);
            in.putExtra("var3", var3);
            in.putExtras(b1);
            startActivity(in);
            finish();
        } else if (strFromAct.equalsIgnoreCase("FORGOT")) {

            Bundle b1 = new Bundle();
            Intent in = new Intent(OTPActivity.this, SetMPIN.class);
            b1.putString("CUSTID", strCustId);
            b1.putString("OTPVAL", strOTP);
            b1.putString("REFID", strRefId);
            b1.putString("FROMACT", strFromAct);
            b1.putString("USERNAME", "123");// retval.split("~")[1]);
            in.putExtra("var1", var1);
            in.putExtra("var3", var3);
            in.putExtras(b1);
            startActivity(in);
            finish();
        }

    }

    class CallWebService_resend_otp extends AsyncTask<Void, Void, Void> {// CallWebService_resend_otp
        LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
        // String[] xmlTags ;//= { "CUSTID", "REQSTATUS", "REQFROM" };
        // String[] valuesToEncrypt ;//= new String[3];

        boolean isWSCalled = false;


        JSONObject jsonObj = new JSONObject();

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            if (strFromAct.equalsIgnoreCase("REGISTER")) {
                try {
                    jsonObj.put("CUSTID", strCustId);
                    jsonObj.put("REQSTATUS", "R");
                    jsonObj.put("REQFROM", "MBSREG");
                    jsonObj.put("MOBNO", strMobNo);
                    jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
                    jsonObj.put("SIMNO",
                            MBSUtils.getSimNumber(OTPActivity.this));
                    jsonObj.put("METHODCODE", "27");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    jsonObj.put("CUSTID", strCustId);
                    jsonObj.put("REQSTATUS", "R");
                    jsonObj.put("REQFROM", "MBSREG");
                    jsonObj.put("IMEINO",
                            MBSUtils.getImeiNumber(OTPActivity.this));
                    jsonObj.put("SIMNO",
                            MBSUtils.getSimNumber(OTPActivity.this));
                    jsonObj.put("METHODCODE", "26");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            // Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);
        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "webServiceOne";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
                isWSCalled = true;
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                // String[] xmlTags = {"STATUS"};
                // String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);

                // int start = xml_data[0].indexOf("SUCCESS");
                JSONObject jsonObj;
                try {

                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
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
                        respdescresend = jsonObj.getString("RESPDESC");
                    } else {
                        respdescresend = "";
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (respdescresend.length() > 0) {
                    showAlert(respdescresend);
                } else {

                    if (retval.split("~")[0].indexOf("SUCCESS") > -1) {
                        post_successresend(retval);
                    } else {
                        // System.out.println("in else ***************************************");
                        retMess = getString(R.string.alert_094);
                        showAlert(retMess);
                    }
                }
            } else {
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        }
    }// CallWebService_resend_otp

    public void resend_otp_post_success(String retval) {
        respcode = "-1";
        respdesc_resend_otp = "";
        String decryptedAccounts = retval.split("~")[1];
        Bundle bObj = new Bundle();
        Intent in = new Intent(OTPActivity.this, OTPActivity.class);
        bObj.putString("RETVAL", decryptedAccounts);
        bObj.putString("CUSTID", strCustId);
        bObj.putString("MOBNO", strMobNo);
        bObj.putString("FROMACT", strFromAct);
        in.putExtra("var1", var1);
        in.putExtra("var3", var3);
        in.putExtras(bObj);
        startActivity(in);
        finish();
    }

    public int chkConnectivity() {
        // Log.i("1111", "1111");
        // p_wait.setVisibility(ProgressBar.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        // Log.i("2222", "2222");
        try {
            State state = ni.getState();
            // Log.i("3333", "3333");
            boolean state1 = ni.isAvailable();
            // Log.i("4444", "4444");
            // System.out.println("state1 ---------" + state1);
            if (state1) {
                switch (state) {
                    case CONNECTED:

                        // Log.i("5555", "5555");
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                                || ni.getType() == ConnectivityManager.TYPE_WIFI) {

                            gpsFlg = 1;
                            flag = 0;

                        }
                        break;
                    case DISCONNECTED:
                        // Log.i("6666", "6666");
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
                        // Log.i("7777", "7777");
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
                // Log.i("8888", "8888");
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

    class CallWebServiceSendCustId extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);

        JSONObject jsonObj = new JSONObject();
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            strOTP = txt_otp.getText().toString().trim();

            strRefId = txt_ref_id.getText().toString().trim();
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();

            try {
                jsonObj.put("CUSTOMERID", strCustId);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
                jsonObj.put("OTPVAL", strOTP);
                //ListEncryption.encryptData(strOTP + strCustId));
                jsonObj.put("REFNO", strRefId);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
                jsonObj.put("METHODCODE", "51");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


        }

        ;

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
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 80000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
                isWSCalled = true;
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {

            // String responseJSON=xml_data[0];
            String str = CryptoClass.Function6(var5, var2);
            String responseJSON = str.trim();
            loadProBarObj.dismiss();
            JSONObject json;
            try {
                if (!responseJSON.equalsIgnoreCase("NULL")) {
                    // System.out.println("in if");
                    json = new JSONObject(responseJSON);


                    String RESPCODE = json.getString("RESPCODE");
                    if (RESPCODE.equals("0")) {

                        showAlert1(getString(R.string.alert_send_custID));

                    } else if (!RESPCODE.equals("0")) {
                        showAlert(getString(R.string.alert_invalid_otp));
                    } else {
                        if (!responseJSON.equalsIgnoreCase("NULL")) {
                            String RESPREASON = json.getString("RETVAL");
                            int pos = Integer.parseInt(RESPCODE);
                            String errmsg = presidents[pos];
                            // Log.e("IN getCustId",errmsg );
                            showAlert("" + errmsg);
                        } else {
                            showAlert(getString(R.string.alert_network_problem_pease_try_again));
                        }
                    }


                } else {
                    // System.out.println("in else");
                    showAlert(getString(R.string.alert_network_problem_pease_try_again));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // Log.e("IN Choose MPin",""+ e);
            }
        }// onPostExecute
    }

    public class CallWebServiceSaveATMCard extends AsyncTask<Void, Void, Void> {
        String atmstatus = "";

        LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
        String generatedXML = "", retVal = "";
        JSONObject jsonobj = new JSONObject();

        @Override
        protected void onPreExecute() {

            loadProBarObj.show();
            stratm = "";
            retval = "";
            if (catdstatus.equals("T")) {
                stratm = "Disabled";
            } else {
                stratm = "Enabled";
            }


            Log.e("strActno", "strActnostrActno" + strActno);
            Log.e("cardno", "cardnocardno" + cardno);
            Log.e("catdstatus", "catdstatuscatdstatus" + catdstatus);
            Log.e("strimeino", "strimeinostrimeino" + strimeino);
            Log.e("strCustId", "strCustId" + strCustId);

            try {
                jsonobj.put("CUSTID", strCustId);
                jsonobj.put("ACCNO", strActno);
                jsonobj.put("CARDNO", cardno);
                jsonobj.put("IMEINO", strimeino);
                jsonobj.put("CARDSTATUS", catdstatus);
                jsonobj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
                jsonobj.put("METHODCODE", "63");

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        ;

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
                request.addProperty("value1", CryptoClass.Function5(jsonobj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

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
            Log.e("onPostExecute==", "11111 ");
            loadProBarObj.dismiss();
            Log.e("onPostExecute==", "11111");

            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());

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
                    respdesc_SaveATMCard = jsonObj.getString("RESPDESC");
                } else {
                    respdesc_SaveATMCard = "";
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (respdesc_SaveATMCard.length() > 0) {
                showAlert(respdesc_SaveATMCard);
            } else {
                loadProBarObj.dismiss();
                if (retval.indexOf("SUCCESS~") > -1) {
                    Log.e("Sudarshan", "SaveATMCard==" + retval);
                    post_SaveATMCard(retval);
                }

            }
        }// onPostExecute
    }

    public void post_SaveATMCard(String retval) {
        respcode = "";
        respdesc_SaveATMCard = "";
        Log.e("Sudarshan", "post_SaveATMCard==" + retval);
        String values[] = retval.split("~")[1].split("!!");
        String refn = values[0];
        showAlert1("Your ATM Card Is " + stratm
                + " Successfully With Request Id " + refn);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        t1.sec = -1;
        System.gc();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        t1.sec = timeOutInSecs;
        Log.e("sec11= ", "sec11==" + t1.sec);
        return super.onTouchEvent(event);
    }

    class CallWebServiceValidateOTPDiffIMEI extends AsyncTask<Void, Void, Void> {
        String[] xmlTags = {"PARAMS"};
        String[] valuesToEncrypt = new String[1];

        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
        String generatedXML = "";
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            retval = "";
            respdescvalidate = "";
            respcode = "";

            strOTP = txt_otp.getText().toString().trim();
            strRefId = txt_ref_id.getText().toString().trim();
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();

            try {
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("OTPVAL", strOTP);//ListEncryption.encryptData(strOTP+strCustId));	//valuesToEncrypt[1] =ListEncryption.encryptData(strOTP+strCustId); //strOTP;
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));    //valuesToEncrypt[2] = MBSUtils.getImeiNumber(OTPActivity.this);
                jsonObj.put("REFID", strRefId);    //valuesToEncrypt[3] = strRefId;
                jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(OTPActivity.this));
            } catch (Exception e) {
                e.printStackTrace();
            }

            valuesToEncrypt[0] = jsonObj.toString();
            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);
            METHOD_NAME = "confirmSecQueIMEIWS";

            int i = 0;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("para_value", generatedXML);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 15000);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                //	String[] xmlTags = { "STATUS" };
                //	String[] xml_data =CryptoUtil.readXML(retVal, xmlTags);
                String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
                Log.e("OTPAct", "xml_data[0]======" + xml_data[0]);
                JSONObject jsonObj;
                try {

                    jsonObj = new JSONObject(xml_data[0]);
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
                        respdescvalidate = jsonObj.getString("RESPDESC");
                    } else {
                        respdescvalidate = "";
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (respdescvalidate.length() > 0) {
                    showAlert(respdescvalidate);
                } else {
                    if (retval.indexOf("SUCCESS") > -1) {
                        post_successvalidate(retval);
                    } else {
                        showAlert(getString(R.string.alert_076));
                    }
                }
            } else {
                showAlert(getString(R.string.alert_000));
            }
        }
    }
}
