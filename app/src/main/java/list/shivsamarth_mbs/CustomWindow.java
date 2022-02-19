package list.shivsamarth_mbs;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CustomWindow extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	//	getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

		/*
		 * requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		 * 
		 * setContentView(R.layout.login_x); //
		 * setContentView(R.layout.balance_enq);
		 * getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		 * R.layout.window_title);
		 * 
		 * // title = (TextView) findViewById(R.id.title); // icon = (ImageView)
		 * // findViewById(R.id.icon);
		 */
		final Window window = getWindow();
		boolean useTitleFeature = false;
		Log.i("111", "window.getContainer()="+window.getContainer());
		/*if (window.getContainer() == null) {
			useTitleFeature = window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
		}
		*/
		Log.i("222", "useTitleFeature="+useTitleFeature);
		
		setContentView(R.layout.login);
		
		/*if (useTitleFeature) {
			window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.window_title);
	
		}*/
		
	}
}