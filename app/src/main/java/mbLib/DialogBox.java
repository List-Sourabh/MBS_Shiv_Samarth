package mbLib;

import android.app.Activity;
import android.app.AlertDialog;

public class DialogBox 
{
	AlertDialog.Builder adb;
	Activity activity;
	String msg, title;

	public DialogBox(final Activity activity) {
		this.activity = activity;
		adb = new AlertDialog.Builder(activity);
		//adb.setView(R.layout.custom_dialog);
		
		adb.setTitle("शिवसमर्थ मल्टीस्टेट को.ऑप. क्रेडिट सोसायटी,तळमावले");//adb.setTitle(activity.getString(R.string.bank_name));
		adb.setMessage("Are You Sure To Exit?");
		adb.create();
	}
	
	public AlertDialog.Builder get_adb()
	{
		return adb;
	}
}
