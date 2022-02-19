package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DatePickerDailog;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class TransferHistory extends Activity implements OnClickListener {
	TransferHistory act;
	ImageView img_heading;
	TextView txt_heading;
	Spinner spi_account_no, spi_transfer_type, spi_status;
	EditText txt_from_dt, txt_to_dt;
	ImageButton btn_home, btn_back,spnr_btn_acc_no,spnr_btn_tran_type,spnr_btn_status;
	Button btn_show_history, btn_from_date, btn_to_date;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	DialogBox dbs;
	boolean noAccounts;
	private DatePicker datePicker;
	Calendar dateandtime;
	DatabaseManagement dbms;
	String retval = "", respcode = "", respdesc = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String MY_SESSION = "my_session";
	private static String METHOD_NAME_TRANSFER_HISTORY = "";
	SimpleDateFormat df;
	Date dt1, dt2, fromDate;
	String fundTransferHistoryrespdesc = "", retvalweb = "";
	String tranType = "", postingStatus = "", fromDt = "", toDt = "",
			curDate = "", retMess = "", custId = "", str2 = "",
			stringValue = "", acnt_inf = "", all_acnts = "", accNo = "";
	int cnt = 0, flag = 0;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public TransferHistory() {
	}

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer_history);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		act = this;
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		spi_account_no = (Spinner) findViewById(R.id.spi_account_no);
		spi_transfer_type = (Spinner) findViewById(R.id.spi_transfer_type);
		spi_status = (Spinner) findViewById(R.id.spi_status);
		txt_from_dt = (EditText) findViewById(R.id.txt_from_dt);
		txt_to_dt = (EditText) findViewById(R.id.txt_to_dt);
		btn_show_history = (Button) findViewById(R.id.btn_show_history);
		btn_from_date = (Button) findViewById(R.id.btn_from_date);
		btn_to_date = (Button) findViewById(R.id.btn_to_date);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_transfer_history));
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		spnr_btn_acc_no = (ImageButton) findViewById(R.id.spnr_btn_acc_no);
		spnr_btn_tran_type = (ImageButton) findViewById(R.id.spnr_btn_tran_type);
		spnr_btn_status = (ImageButton) findViewById(R.id.spnr_btn_status);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		spnr_btn_acc_no.setOnClickListener(this);
		spnr_btn_tran_type.setOnClickListener(this);
		spnr_btn_status.setOnClickListener(this);
		btn_show_history.setOnClickListener(this);
		btn_from_date.setOnClickListener(this);
		btn_to_date.setOnClickListener(this);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.transfer_history);
		dateandtime = Calendar.getInstance(Locale.US);
		df = new SimpleDateFormat("dd/MM/yyyy");
		curDate = df.format(dateandtime.getTime());
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);

				Log.e("custId", "......" + custId);
				stringValue = c1.getString(0);
				Log.e("retValStr", "c......" + stringValue);
			}
		}

		// custId="0005008372";
		// stringValue="2#101#SB#25430#KULKARNI SHASHIKANT  RAJARAM##0020001010025430#O#9999999#15427048#Y~2#101#SB#25584#KULKARNI SHASHIKANT  RAJARAM##0020001010025584#O#NA#498.8#Y~2#101#SB#25635#KULKARNI SHASHIKANT  RAJARAM##0020001010025635#O#NA#25471.2#Y~2#101#SB#25636#KULKARNI SHASHIKANT  RAJARAM##0020001010025636#O#NA#17848.2#Y~2#101#SB#25637#KULKARNI SHASHIKANT  RAJARAM##0020001010025637#O#NA#37783.53#Y~2#1011#SB#1#KULKARNI SHASHIKANT  RAJARAM##0020010110000001#O#NA#0#Y~2#301#LO#58#KULKARNI SHASHIKANT  RAJARAM##0020003010000058#I#NA#13100.3#Y~2#301#LO#131#KULKARNI SHASHIKANT  RAJARAM##0020003010000131#I#NA#452922.87#Y~2#337#LO#8345#KULKARNI SHASHIKANT  RAJARAM##0020003370008345#I#NA#142105#Y~2#TEST2#LO#6#KULKARNI SHASHIKANT  RAJARAM##0020TEST20000006#I#NA#140000#Y~";

		addAccounts(stringValue);
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_show_history:
			accNo = spi_account_no.getSelectedItem().toString();
			fromDt = txt_from_dt.getText().toString().trim();
			toDt = txt_to_dt.getText().toString().trim();

			if (accNo.equalsIgnoreCase("Select Debit Account")) {
				showAlert(getString(R.string.alert_sel_debit_acc));
			} else if (accNo.length() == 0) {
				showAlert(getString(R.string.alert_sel_debit_acc));
			} else if (fromDt.length() == 0) {
				showAlert(getString(R.string.alert_entr_validfrmdt));
			} else if (toDt.length() == 0) {
				showAlert(getString(R.string.alert_entr_validtodt));
			}
			/*
			 * else if(fromDt>toDt) { showAlert(getString(R.string.alert_056));
			 * }
			 */
			else {
				if (chkConnectivity() == 0) {
					CallWebServiceGetTranHistory c = new CallWebServiceGetTranHistory();
					c.execute();
				}
			}
			break;
		case R.id.btn_from_date:
			onFromDateCalendarClick(arg0);
			break;
		case R.id.btn_to_date:
			String fromdate = txt_from_dt.getText().toString().trim();
			if (fromdate.toString().equals("")) {
				retMess = getString(R.string.alert_sel_frmdt);
				showAlert(retMess);
			} else {
				onToDateCalendarClick(arg0);
			}
			break;
		case R.id.btn_back:
		case R.id.btn_home:
			Intent intet = new Intent(TransferHistory.this,
					FundTransferMenuActivity.class);
			intet.putExtra("var1", var1);
			   intet.putExtra("var3", var3);
			startActivity(intet);
			finish();

			break;
		case R.id.spnr_btn_acc_no:
			spi_account_no.performClick();
			break;
		case R.id.spnr_btn_tran_type:
			spi_transfer_type.performClick();
			break;
		case R.id.spnr_btn_status:
			spi_status.performClick();
			break;
		default:
			break;
		}
	}

	public void addAccounts(String str) {
		try {
			Log.e("LIST", "Sharayu Mali" + str);
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");

			Log.e("LIST", " allstr.length----" + allstr.length);
			int noOfAccounts = allstr.length;
			arrList.add("Select Debit Account");
			arrListTemp.add("Select Debit Account");
			Log.e("LIST", "arrList======" + arrList);
			Accounts acArray[] = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				Log.e("LIST", "str2=====" + str2);

				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				Log.e("LIST", "accType=====" + accType);

				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				if ((accType.equals("SB")) || (accType.equals("CA"))
						|| (accType.equals("LO"))) {
					arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)
							+ ")");
					arrListTemp.add(str2);
				}
			}

			if (arrList.size() == 0) {
				noAccounts = true;
				showAlert(getString(R.string.alert_no_recrdfound));
			}
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_account_no.setAdapter(debAccs);

			String[] tranTypeArr = { "Same Bank","NEFT","RTGS"};//, "NEFT", "RTGS", "IMPS", "All" };// "QRCODE",
			ArrayAdapter<String> tranTypes = new ArrayAdapter<String>(act,
					R.layout.spinner_item, tranTypeArr);
			tranTypes
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_transfer_type.setAdapter(tranTypes);

			String[] postingStatusArr = { "Successful", "Failed", "Pending",
					"All" };
			ArrayAdapter<String> postingStatus = new ArrayAdapter<String>(act,
					R.layout.spinner_item, postingStatusArr);
			postingStatus
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_status.setAdapter(postingStatus);

			acnt_inf = spi_account_no.getItemAtPosition(
					spi_account_no.getSelectedItemPosition()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception===" + e);
		}

	}// end addAccount

	public void onFromDateCalendarClick(View v) {
		Log.i("Calendar clicked", "######");
		DatePickerDailog dp = new DatePickerDailog(act, dateandtime,
				new DatePickerDailog.DatePickerListner() {

					public void OnDoneButton(Dialog datedialog, Calendar c) {
						datedialog.dismiss();
						dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
						dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
						dateandtime.set(Calendar.DAY_OF_MONTH,
								c.get(Calendar.DAY_OF_MONTH));
						String strDate = new SimpleDateFormat("dd/MM/yyyy")
								.format(c.getTime());
						SimpleDateFormat formatter = new SimpleDateFormat(
								"dd/MM/yyyy");
						try {
							dt1 = df.parse(curDate);
							dt2 = formatter.parse(strDate);
							if (dt2.compareTo(dt1) > 0) {
								showAlert(getString(R.string.alert_fmdt_less_todaydt));
								txt_from_dt.setText("");
							} else {
								fromDate = dt2;
								txt_from_dt.setText(strDate);
							}

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					public void OnCancelButton(Dialog datedialog) {
						// TODO Auto-generated method stub
						datedialog.dismiss();
					}
				});
		dp.show();

	}

	public void onToDateCalendarClick(View v) {
		Log.i("Calendar clicked", "######");
		DatePickerDailog dp = new DatePickerDailog(act, dateandtime,
				new DatePickerDailog.DatePickerListner() {

					public void OnDoneButton(Dialog datedialog, Calendar c) {
						datedialog.dismiss();
						dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
						dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
						dateandtime.set(Calendar.DAY_OF_MONTH,
								c.get(Calendar.DAY_OF_MONTH));
						String strDate = new SimpleDateFormat("dd/MM/yyyy")
								.format(c.getTime());
						SimpleDateFormat formatter = new SimpleDateFormat(
								"dd/MM/yyyy");
						try {
							dt1 = df.parse(curDate);
							dt2 = formatter.parse(strDate);
							long diff = dt2.getTime() - fromDate.getTime();
							long day = diff / (1000 * 60 * 60 * 24);
							if (dt2.compareTo(dt1) > 0) {
								showAlert(getString(R.string.alert_less_todaydt));
								txt_to_dt.setText("");
							} else if (dt2.compareTo(fromDate) < 0) {
								showAlert(getString(R.string.alert_frmdt_less_todt));
								txt_to_dt.setText("");
							}
							/*
							 * else if(day>10) {
							 * showAlert(getString(R.string.alert_142));
							 * txt_to_dt.setText(""); }
							 */
							else {
								txt_to_dt.setText(strDate);
							}

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					public void OnCancelButton(Dialog datedialog) {
						// TODO Auto-generated method stub
						datedialog.dismiss();
					}
				});
		dp.show();

	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v)

			{
				// Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) {
				case R.id.btn_ok:
					// Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
					if ((str.equalsIgnoreCase(fundTransferHistoryrespdesc))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successGetTranHistory(retvalweb);
					} else if ((str
							.equalsIgnoreCase(fundTransferHistoryrespdesc))
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

	class CallWebServiceGetTranHistory extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

	// "CUSTID","FRMDATE","TODATE",
										// "DRACCNO","TRANTYPE","POSTSTATUS","IMEINO"};
		
		JSONObject obj = new JSONObject();

		protected void onPreExecute() {
			loadProBarObj.show();

			accNo = arrListTemp.get(spi_account_no.getSelectedItemPosition());
			fromDt = txt_from_dt.getText().toString().trim();
			toDt = txt_to_dt.getText().toString().trim();
			// {"Same Bank","NEFT","RTGS","IMPS","All"};
			if (spi_transfer_type.getSelectedItemPosition() == 0)
				tranType = "INTBANK";
			else if (spi_transfer_type.getSelectedItemPosition() == 1)
				tranType = "NT";
			else if (spi_transfer_type.getSelectedItemPosition() == 2)
				tranType = "RT";
			else if (spi_transfer_type.getSelectedItemPosition() == 1)
				tranType = "IMPS";
			/*
			 * else if(spi_transfer_type.getSelectedItemPosition()==3)
			 * tranType="QRCODE";
			 */
			else
				tranType = "ALL";

			// tranType=spi_transfer_type.getSelectedItem().toString();

			if (spi_status.getSelectedItemPosition() == 0)
				postingStatus = "0";// success
			else if (spi_status.getSelectedItemPosition() == 1)
				postingStatus = "1";// failed
			else if (spi_status.getSelectedItemPosition() == 2)
				postingStatus = "2";// pending
			else
				postingStatus = "3";// all
			// postingStatus=spi_status.getSelectedItem().toString();
		
			try {

				obj.put("CUSTID", custId);
				obj.put("FRMDATE", fromDt);
				obj.put("TODATE", toDt);
				obj.put("DRACCNO", accNo);
				obj.put("TRANTYPE", tranType);
				obj.put("POSTSTATUS", postingStatus);
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","41"); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
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
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
			
			
			
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try {

				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());
				Log.e("IN return", "data :" + jsonObj.toString());
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					retvalweb = jsonObj.getString("RETVAL");
				} else {
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC")) {
					fundTransferHistoryrespdesc = jsonObj.getString("RESPDESC");
				} else {
					fundTransferHistoryrespdesc = "";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fundTransferHistoryrespdesc.length() > 0) {
				showAlert(fundTransferHistoryrespdesc);
			} else {
				// decryptedAccounts="SUCCESS";
				if (retvalweb.indexOf("NOREC") > -1) {
					Log.e("decryptedAccounts", "FAILED");
					Log.e("decryptedAccounts", "FAILED");
					Log.e("decryptedAccounts", "FAILED");
					showAlert(getString(R.string.alert_089));
				}

				else if (retvalweb.length() > 0) {
					post_successGetTranHistory(retvalweb);
				}

				else {
					showAlert(getString(R.string.alert_143));
				}// end else

			}
		}
	}

	public void post_successGetTranHistory(String retvalweb) {
		respcode = "";
		fundTransferHistoryrespdesc = "";
		JSONArray ja = null;
		try {
			ja = new JSONArray(retvalweb);
			Log.e("TRANHISTORY", "JSONArray==" + ja.length());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ja.length() > 0) 
		{
			Bundle b=new Bundle();
			b.putString("RETVAL", retvalweb);
			b.putString("ACCNO", accNo);
			Intent in = new Intent(act, TransferHistoryRpt.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			in.putExtras(b);
			startActivity(in);
			finish();
		} else {
			showAlert(getString(R.string.alert_089));
		}

	}

	public int chkConnectivity() {// chkConnectivity
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
					dbs = new DialogBox(act);
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
					flag = 1;
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

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	@Override
	public void onBackPressed() {
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
