package mbLib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import list.shivsamarth_mbs.ErrorDialogClass;
import list.shivsamarth_mbs.R;
import list.shivsamarth_mbs.SessionTimeout;

public class MyThread extends Thread {
    public int sec;
    public String fromAct = "DASH";
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    String custid = "";
    DatabaseManagement dbms;
    String respcode = "", retvalws = "", respdesc_web = "";
    PrivateKey var1;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    Activity act;

    public MyThread(int sec, Activity act, PrivateKey var1, String var3) {
        this.sec = sec;
        this.act = act;
        this.var1 = var1;
        this.var3 = var3;
    }

    @Override
    public void run() {
        while (sec > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sec--;
        }
        if (sec == 0) {
            new CallWebService().execute();
			/*Intent intent = new Intent(act, SessionOut.class);
			Bundle b = new Bundle();
			b.putString("FROMACT", "THREAD");
			intent.putExtras(b);
			act.startActivity(intent);
			act.finish();*/
        }
    }

    class CallWebService extends AsyncTask<Void, Void, Void> {
        // String[] xmlTags = { "CUSTID","IMEINO" };
        JSONObject jsonObj = new JSONObject();

        @Override
        protected void onPreExecute() {
            dbms = new DatabaseManagement("samarth.mbank", "listMobileBanking");
            dbms.initDatabase();
            Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
            if (c1 != null) {
                while (c1.moveToNext()) {
                    custid = c1.getString(2);
                    Log.e("CustId", "c......" + custid);
                }
            }

            try {
                jsonObj.put("CUSTID", custid);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "29");
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Log.e("@DEBUG","LOGOUT doInBackground()");
            String value4 = act.getString(R.string.namespace);
            String value5 = act.getString(R.string.soap_action);
            String value6 = act.getString(R.string.url);
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

        protected void onPostExecute(final Void result) {
            // Log.e("@DEBUG","LOGOUT onPostExecute()");
            // String[] xmlTags = { "STATUS" };

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
                    retvalws = jsonObj.getString("RETVAL");
                } else {
                    retvalws = "";
                }
                if (jsonObj.has("RESPDESC")) {
                    respdesc_web = jsonObj.getString("RESPDESC");
                } else {
                    respdesc_web = "";
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e("respdesc_web", "respdesc_web" + respdesc_web);
            Log.e("respdesc_web", "respdesc_web" + respdesc_web);
            post_websuccess();
        }
    }

    public void post_websuccess() {
        Log.e("fromAct", "fromAct11111" + fromAct);
        Log.e("fromAct", "fromAct11111" + fromAct);

        Fragment fragment = new SessionTimeout();
        FragmentManager fragmentManager = act.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commitAllowingStateLoss();
    }

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str);
        alert.show();
    }


}
