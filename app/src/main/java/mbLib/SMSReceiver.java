package mbLib;

import list.shivsamarth_mbs.OTPActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	OTPActivity otpActivity;
	
	public SMSReceiver(OTPActivity o)
	{
		otpActivity=o;
	}
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
		try
		{
			/*Cursor cursor = arg0.getContentResolver().query(Uri.parse("content://sms/inbox"), null, "body like '%Reference Id is : "+otpActivity.strRefId+"%'",  null,null);
			if (cursor.moveToFirst()) { // must check the result to prevent exception
				String body="";
				String number="";
			    do { 
		           body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
		           number = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
		           otpActivity.txt_otp.setText("");
		           otpActivity.txt_otp.setText(body.substring(0,6));
		           Toast.makeText(arg0, body, Toast.LENGTH_LONG).show();
		           //break;
			       
			       // use msgData
			    } while (cursor.moveToNext());
			} else {
			   // empty box, no SMS
			}*/
			final Bundle bundle = arg1.getExtras();
				
			if (bundle != null) {
				
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				
				for (int i = 0; i < pdusObj.length; i++) {
					
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					
			        String senderNum = phoneNumber;
			        String message = currentMessage.getDisplayMessageBody();
			        if(message.indexOf("Reference Id is : "+otpActivity.strRefId)>-1)
			        {
			        	Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
			            int duration = Toast.LENGTH_LONG;
			            //Toast toast = Toast.makeText(arg0, "senderNum: "+ senderNum + ", message: " + message, duration);
			            //toast.show();
			            otpActivity.txt_otp.setText("");
				        otpActivity.txt_otp.setText(message.substring(0,6));
			        }
					
				} // end for loop
              } // bundle is null

			/*GregorianCalendar cal=new GregorianCalendar();
		 	cal.setTimeInMillis(System.currentTimeMillis());
			AlarmManager am=(AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(arg0, SMSReceiver.class);
			arg0.startService(i);
			PendingIntent pi = PendingIntent.getService(arg0, 0, i, 0);
			
			long period=1000l * 60 * 1;
			
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+2000,period , pi);*/
		}
		catch(Exception e)
		{
			Toast.makeText(arg0, e+"", Toast.LENGTH_LONG).show();
		}
	}

}
