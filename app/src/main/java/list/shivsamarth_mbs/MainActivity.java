package list.shivsamarth_mbs;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.security.PrivateKey;

import mbLib.DialogBox;

public class MainActivity extends Activity {
    public int frgIndex = -1;
    static PrivateKey var1 = null;
    DialogBox dbs;
    String var5 = "", retMess = "";
    static String var3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbs = new DialogBox(this);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            if (!haveConnectedWifi || !haveConnectedMobile) {
                ErrorDialogClass errorDialogClass = new ErrorDialogClass(this, getString(R.string.SML_alert_175)) {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.btn_ok) {
                            this.dismiss();
                        }
                        dismiss();
                    }
                };
                errorDialogClass.show();
            }
        } catch (Exception e) {
            Log.e("Shubham", "Check Internet Error:-" + e.getMessage());
        }
        return haveConnectedWifi || haveConnectedMobile;

    }
}
