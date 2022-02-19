package list.shivsamarth_mbs;



import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoUtil;
import mbLib.DatabaseManagement;
import mbLib.DateValidator;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;

public class ViewHistory extends Activity implements OnClickListener,OnItemSelectedListener
{
	MainActivity act;
	TextView cust_nm, txt_heading;
	EditText txt_from_date,txt_to_date;
	ImageView img_heading,btn_back;
	Button btn_from_date,btn_to_date,btn_view_history;
	Spinner spi_category,spi_operator;
	ImageButton spinner_cat_btn,spinner_oprtr_btn;
	
	DialogBox dbs;
	DatabaseManagement dbms;
	Calendar dateandtime,frmDtCalndr,toDtCalndr;
	SimpleDateFormat df;
	Date dt1, dt2,fromDate;
	ProgressBar pb_wait;
	DateValidator dv;
	 String date="";
	 ViewHistory viewHistory;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String METHOD_CATEGORY = "fetchCategory";
	private static final String METHOD_OPERATOR = "fetchOperator";
	 private int year, month, day;
	int flag = 0,cnt=0;
	String str = "", retMess = "", cust_name = "", stringValue = "",retVal="",fromAct="";
	String all_str = "", branch_cd = "", schm_cd = "", acnt_no = "",custId="";
	String selAcc = "", str2 = "",balnaceamnt = "",accountNo="",curDate="";
	ArrayList<String> categoryCd=new ArrayList<String>();
	ArrayList<String> category=new ArrayList<String>();
	ArrayList<String> operator=new ArrayList<String>();
	ArrayList<String> operatorCd=new ArrayList<String>();
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	protected void onCreate(Bundle savedInstanceState) 
	{
		viewHistory=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_history);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		txt_from_date = (EditText) findViewById(R.id.txt_from_date);
		txt_to_date = (EditText) findViewById(R.id.txt_to_date);
		btn_from_date=(Button)findViewById(R.id.btn_from_date);
		btn_to_date=(Button)findViewById(R.id.btn_to_date);
		btn_view_history=(Button)findViewById(R.id.btn_view_history);
		spinner_cat_btn=(ImageButton)findViewById(R.id.spinner_cat_btn);
		spinner_oprtr_btn=(ImageButton)findViewById(R.id.spinner_oprtr_btn);
		spi_category=(Spinner)findViewById(R.id.spi_category);
		spi_operator=(Spinner)findViewById(R.id.spi_operator);
		
		btn_back.setOnClickListener(this);
		btn_from_date.setOnClickListener(this);
		btn_to_date.setOnClickListener(this);
		btn_view_history.setOnClickListener(this);
		spi_category.setOnItemSelectedListener(this);
		spinner_cat_btn.setOnClickListener(this);
		spinner_oprtr_btn.setOnClickListener(this);
		dateandtime = Calendar.getInstance(Locale.US);
		//  frmDtCalndr = Calendar.getInstance(Locale.US);
		//  toDtCalndr = Calendar.getInstance(Locale.US);
		df = new SimpleDateFormat("dd/MM/yyyy");
		curDate = df.format(dateandtime.getTime());
		dv = new DateValidator();
		txt_heading.setText(viewHistory.getString(R.string.lbl_view_history));
		
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		Intent getdata=getIntent();
    	Bundle bundle = getdata.getExtras();
		if (bundle != null) {
		        fromAct = bundle.getString("FROMACT");
		}
		//if(fromAct.equalsIgnoreCase("RECHARGE"))
		//	act.frgIndex=121;
		//else
		//	act.frgIndex=131;
		/*dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		Log.e("retvalstr","....."+stringValue);
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);       	
	        }
        }*/
        
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);
        Log.e("ViewHistory","formattedDate=="+formattedDate);
        txt_to_date.setText(formattedDate);
        String month=formattedDate.split("/")[1];
        
        month=String.format("%02d", (Integer.parseInt(month)-1));
        txt_from_date.setText(formattedDate.split("/")[0]+"/"+month+"/"+formattedDate.split("/")[2]);
        
        
        //if(chkConnectivity()==0)
        	new CallWebServiceFetchCategory().execute();
       // else
        //	showAlert(act.getString(R.string.alert_000));
        	t1 = new MyThread(timeOutInSecs,this,var1,var3);
    		t1.start();
	}
	
	@Override
	
	
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_back:	
				if(fromAct.equalsIgnoreCase("RECHARGE"))
				{
					
					Intent in=new Intent(viewHistory,Recharges.class);
        			startActivity(in);
        			viewHistory.finish(); 
					/*Fragment fragment =new Recharges(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
				}
				else
				{	
					
					Intent in=new Intent(viewHistory,BillList.class);
        			startActivity(in);
        			viewHistory.finish(); 
					/*Fragment fragment =new BillList(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
				//billlist
				
				}	
				break;
			case R.id.btn_from_date:	
				//onFromDateCalendarClick(v);
				Log.e("btn_from_date","onclick==btn_from_date");
				date="F";
                onFrmDateClick();
                setDate(v);
                break;
				
			
			case R.id.btn_to_date:	
				//onToDateCalendarClick(v);
				Log.e("btn_to_date","onclick==btn_to_date");
				date="T";
                onToDateClick();
                setDate(v);
         
				break;
			case R.id.btn_view_history:
				if(txt_from_date.getText().toString().length()==0)
				{
					showAlert(viewHistory.getString(R.string.alert_selectfromDate));
				}
				else if(txt_to_date.getText().toString().length()==0)
				{
					showAlert(viewHistory.getString(R.string.alert_selecttoDate));
				}
				else
				{	
					Bundle b1=new Bundle();
					b1.putString("FROMACT",fromAct);
					b1.putString("CATEGORY",categoryCd.get(spi_category.getSelectedItemPosition()));
					b1.putString("BILLERCD",operatorCd.get(spi_operator.getSelectedItemPosition()));
					b1.putString("FROMDATE",txt_from_date.getText().toString());
					b1.putString("TODATE",txt_to_date.getText().toString());
					Intent in=new Intent(viewHistory,History.class);
					in.putExtras(b1);
					startActivity(in);
				}	
				break;
			case R.id.spinner_cat_btn:
				spi_category.performClick();
				break;
			case R.id.spinner_oprtr_btn:
				spi_operator.performClick();
				break;
		}	
	}
	
	public void showAlert(String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(viewHistory, "" + str);
		alert.show();
	}
		
	public void onFromDateCalendarClick(View v) {
		Log.e("Calendar clicked", "######");
		/*DatePickerDailog dp = new DatePickerDailog(act,
				dateandtime, new DatePickerDailog.DatePickerListner() {

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
							if (dt2.compareTo(dt1) > 0)
							{
								showAlert(getString(R.string.alert_139));
								txt_from_date.setText("");
							} 
							else 
							{
								fromDate=dt2;
								txt_from_date.setText(strDate);
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
*/
		
	}
	
	public void onToDateCalendarClick(View v) {
		Log.e("Calendar clicked", "######");
		/*
		Calendar cal = Calendar.getInstance();              
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", cal.getTimeInMillis());
		intent.putExtra("allDay", true);
		intent.putExtra("rrule", "FREQ=YEARLY");
		intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
		intent.putExtra("title", "A Test Event from android app");
		startActivity(intent);*/
	/*	DatePickerDailog dp = new DatePickerDailog(act,
				dateandtime, new DatePickerDailog.DatePickerListner() {

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
							Log.e("dt2.getTime()=","dt2.getTime()="+dt2.getTime());
							Log.e("fromDate.getTime()=","fromDate.getTime()="+fromDate.getTime());
							long diff = dt2.getTime() - fromDate.getTime();							
							long day=diff/(1000*60*60*24);	
							if (dt2.compareTo(dt1) > 0)
							{
								showAlert(getString(R.string.alert_140));
								txt_to_date.setText("");
							}
							else if(dt2.compareTo(fromDate)<0)
							{
								showAlert(getString(R.string.alert_141));
								txt_to_date.setText("");
							}
							else 
							{
								txt_to_date.setText(strDate);
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
*/
	}

    public void onFrmDateClick()
    {
    	
    	Log.e("Calendar clicked", "from date ######");
      frmDtCalndr = Calendar.getInstance();
        year = frmDtCalndr.get(Calendar.YEAR);

        month = frmDtCalndr.get(Calendar.MONTH);
        day = frmDtCalndr.get(Calendar.DAY_OF_MONTH);
        
        Log.e("year","year=="+year);
        Log.e("month","month=="+month);
        Log.e("day","day=="+day);
    }

    public void onToDateClick()
    {
    	
    	Log.e("btn_to_date","onclick==btn_to_date");
    	Log.e("Calendar clicked", "to date  #####");
    	
    	
    	
        toDtCalndr = Calendar.getInstance();
        year = toDtCalndr.get(Calendar.YEAR);

        month = toDtCalndr.get(Calendar.MONTH);
        day = toDtCalndr.get(Calendar.DAY_OF_MONTH);
        
        Log.e("year","year=11="+year);
        Log.e("month","month=11="+month);
        Log.e("day","day=11="+day);
        
    }
    
    public DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener()
            {
	  
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3)
                {
                    if( date=="F")
                    {
                    	Log.e("Calendar date===F", "######"+date);
                        showFromDate(arg1, arg2 + 1, arg3);
                    }
                    else if( date=="T")
                    {
                    	Log.e("Calendar date===T", "######"+date);
                        showToDate(arg1, arg2+1, arg3);
                    }
                }
            };

    @SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void setDate(View view)
    {
    	Log.e("Calendar===setDate", "######");
    	viewHistory.showDialog(999);
      //  Toast.makeText(act, "Test",Toast.LENGTH_SHORT).show();
        
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");

    }
	            
	/*		protected Dialog onCreateDialog(int id)
	            {
	               Log.e("onCreateDialog== ","onCreateDialog before 999");
	                if (id == 999)
	                {
	                	Log.e("Calendar===id", "######"+id);
	                    return new DatePickerDialog(getActivity(),
	                            myDateListener, year, month, day);
	                }
	                return null;
	            }
*/
	@SuppressLint({"NewApi", "ValidFragment"})
	public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener 
	{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3)
        {
            if( date=="F")
            {
            	Log.e("Calendar date===F", "######"+date);
                showFromDate(arg1, arg2 + 1, arg3);
            }
            else if( date=="T")
            {
            	Log.e("Calendar date===T", "######"+date);
                showToDate(arg1, arg2+1, arg3);
            }
        }
    }
			
	private void showFromDate(int year, int month, int day)
    {
    	
    	Log.e("showFromDate=", "######");
        StringBuilder seldate =new StringBuilder();
        if(day<10)
            seldate.append("0"+day);
        else
            seldate.append(day);

        seldate.append("/");
        if(month<10)
            seldate.append("0"+month);
        else
            seldate.append(month);
        seldate.append("/");
        seldate.append(year);
      
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strFromDate = seldate.toString();

        try
        {
            dt1 = df.parse(curDate);
            dt2 = formatter.parse(strFromDate);
           
           
            if (dt2.compareTo(dt1) > 0)
			{
				showAlert(getString(R.string.alert_frmDatelessthanTodayes));
				txt_from_date.setText("");
			} 
			else 
			{
				fromDate=dt2;
                txt_from_date.setText(strFromDate);
			}
            
            

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showToDate(int year, int month, int day)
    {
    	
    	Log.e("showToDate===showToDate", "######");
        StringBuilder seldate =new StringBuilder();
        if(day<10)
            seldate.append("0"+day);
        else
            seldate.append(day);

        seldate.append("/");
        if(month<10)
            seldate.append("0"+month);
        else
            seldate.append(month);
        seldate.append("/");
        seldate.append(year);
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy");
        String strToDate = seldate.toString();

        try
        {
        	fromDate=df.parse(txt_from_date.getText().toString());
            dt1 = df.parse(curDate);
            dt2 = formatter.parse(strToDate);
            //Toast.makeText(ViewHistory.this, fromDate+"==="+dt2, Toast.LENGTH_LONG).show();
            if (dt2.compareTo(dt1) > 0)
            {
            	showAlert(getString(R.string.alert_frmDatelessthanTodayes));
            	txt_to_date.setText("");
            }
            else if(dt2.compareTo(fromDate)<0)
			{
				showAlert(getString(R.string.alert_toDatelessthanTodayes));
				txt_to_date.setText("");
			}
            else
            {
                //fromDate=dt2;
                txt_to_date.setText(strToDate);
            }
             

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
                      
	class CallWebServiceFetchCategory extends AsyncTask<Void, Void, Void> 
	{
		String[] xmlTags = {"PARAMS"};
		String[] valuesToEncrypt = new String[1];
		LoadProgressBar loadProBarObj = new LoadProgressBar(viewHistory);
		String generatedXML = "";
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			JSONObject obj=new JSONObject();
			try 
			{
				obj.put("CUSTID", custId);
				obj.put("FROMACT", fromAct);
				obj.put("IMEINO", MBSUtils.getImeiNumber(viewHistory));
				valuesToEncrypt[0] = obj.toString();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			
			int i = 0;
			/*try 
			{
				SoapObject request = new SoapObject(NAMESPACE, METHOD_CATEGORY);
				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
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
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			} 
			catch (Exception e) 
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}*/
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			int count = 0;
			loadProBarObj.dismiss();
			//if (isWSCalled) 
			//{
				String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
				if(fromAct.equalsIgnoreCase("BILL"))
					xml_data[0]="SUCCESS~[{\"CATEGORY\":\"Electricity\",\"CATEGORYCD\":\"Electricity\"},{\"CATEGORY\":\"Insurance\",\"CATEGORYCD\":\"Insurance\"},{\"CATEGORY\":\"Telecom\",\"CATEGORYCD\":\"Telecom\"}]";
				else
					xml_data[0]="SUCCESS~[{\"CATEGORY\":\"DTH\",\"CATEGORYCD\":\"PREPAID DTH\"},{\"CATEGORY\":\"Mobile\",\"CATEGORYCD\":\"PREPAID MOBILE\"}]";
				try 
				{
					if (xml_data[0].indexOf("FAILED") > -1) 
					{
						showAlert(getString(R.string.alert_err));
					} 
					else 
					{
						categoryCd=new ArrayList<String>();
						category=new ArrayList<String>();
						String decryptedCategory=xml_data[0].split("SUCCESS~")[1];
						Log.e("CATEGORY",decryptedCategory);
						category.add("All");
						categoryCd.add("All");
						JSONArray jArr=new JSONArray(decryptedCategory);
						for(int i=0;i<jArr.length();i++)
						{
							JSONObject jObj=jArr.getJSONObject(i);
							category.add(jObj.getString("CATEGORY"));
							categoryCd.add(jObj.getString("CATEGORYCD"));
						}
						
						ArrayAdapter<String> catArr = new ArrayAdapter<String>(viewHistory,R.layout.spinner_item, category);
						catArr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spi_category.setAdapter(catArr);
						
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			//} 
			//else 
			//{
			//	retMess = getString(R.string.alert_000);
			//	showAlert(retMess);
			//}
		}

	}
	
	class CallWebServiceFetchOperator extends AsyncTask<Void, Void, Void> 
	{
		String[] xmlTags = {"PARAMS"};
		String[] valuesToEncrypt = new String[1];
		LoadProgressBar loadProBarObj = new LoadProgressBar(viewHistory);
		String generatedXML = "";
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			JSONObject obj=new JSONObject();
			try 
			{
				obj.put("CUSTID", custId);
				obj.put("FROMACT", fromAct);
				obj.put("CATEGORY", categoryCd.get(spi_category.getSelectedItemPosition()));
				obj.put("IMEINO", MBSUtils.getImeiNumber(viewHistory));
				valuesToEncrypt[0] = obj.toString();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			
			int i = 0;
			/*try 
			{
				SoapObject request = new SoapObject(NAMESPACE, METHOD_OPERATOR);
				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
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
			} 
			catch (Exception e) 
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}*/
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			int count = 0;
			loadProBarObj.dismiss();
			//if (isWSCalled) 
			//{
				String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
				xml_data[0]="[{\"BILLERCD\":\"AIRTELDTH\",\"BILLERNAME\":\"AIRTEL DTH\"},{\"BILLERCD\":\"AVIVALIFE\",\"BILLERNAME\":\"Aviva Life Insurance\"},{\"BILLERCD\":\"ICIPRU\",\"BILLERNAME\":\"ICICI Prudential Life Insurance\"},{\"BILLERCD\":\"METLIFE\",\"BILLERNAME\":\"PNB Met Life India Insurance Company Limited\"},{\"BILLERCD\":\"RELLIFE\",\"BILLERNAME\":\"Reliance Life Insurance Company Limited\"},{\"BILLERCD\":\"AIRTLLMH\",\"BILLERNAME\":\"Airtel Telephone Maharashtra\"},{\"BILLERCD\":\"AIRTMH\",\"BILLERNAME\":\"Airtel Mobile Maharashtra\"},{\"BILLERCD\":\"BPLMMH\",\"BILLERNAME\":\"Vodafone Maharashtra\"},{\"BILLERCD\":\"DOCOMOMH\",\"BILLERNAME\":\"Tata Docomo GSM, Mumbai\"},{\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\"},{\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\"},{\"BILLERCD\":\"BSNLPRE\",\"BILLERNAME\":\"BSNL  PREPAID \"},{\"BILLERCD\":\"DOCOMOPRE\",\"BILLERNAME\":\"TATA DOCOMO GSM\"},{\"BILLERCD\":\"IDEAPRE\",\"BILLERNAME\":\"IDEA PREPAID\"},{\"BILLERCD\":\"MTNLDELPRE\",\"BILLERNAME\":\"MTNL DELHI PREPAID\"},{\"BILLERCD\":\"MTNLMUMPRE\",\"BILLERNAME\":\"MTNL MUMBAI PREPAID\"},{\"BILLERCD\":\"RIMGSMPRE\",\"BILLERNAME\":\"RELIANCE GSM PREPAID\"},{\"BILLERCD\":\"UNINORPRE\",\"BILLERNAME\":\"TELENOR PREPAID\"},{\"BILLERCD\":\"VODAFONPRE\",\"BILLERNAME\":\"VODAFONE PREPAID\"},{\"BILLERCD\":\"JIOPRE\",\"BILLERNAME\":\"JIO PREPAID\"},{\"BILLERCD\":\"BIGTVDTH\",\"BILLERNAME\":\"BIG TV DTH\"},{\"BILLERCD\":\"DISHTVDTH\",\"BILLERNAME\":\"DISH TV DTH\"},{\"BILLERCD\":\"SUNTVDTH\",\"BILLERNAME\":\"SUN TV DTH\"},{\"BILLERCD\":\"TATASKYDTH\",\"BILLERNAME\":\"TATASKY DTH\"},{\"BILLERCD\":\"VIDEOCNDTH\",\"BILLERNAME\":\"VIDEOCON DTH\"},{\"BILLERCD\":\"SANGLTEL\",\"BILLERNAME\":\"Sangli Telcom Bill\"},{\"BILLERCD\":\"BOIPAY\",\"BILLERNAME\":\"BOI payment\"},{\"BILLERCD\":\"VIDEOCNPRE\",\"BILLERNAME\":\"VIDEOCON PREPAID\"},{\"BILLERCD\":\"MTSPRE\",\"BILLERNAME\":\"MTS PREPAID\"},{\"BILLERCD\":\"indian\",\"BILLERNAME\":\"mumbai\"},{\"BILLERCD\":\"shrikant\",\"BILLERNAME\":\"patil\"},{\"BILLERCD\":\"shrik\",\"BILLERNAME\":\"ss\"},{\"BILLERCD\":\"shrikant\",\"BILLERNAME\":\"sss\"},{\"BILLERCD\":\"dktsap\",\"BILLERNAME\":\"jjssssss\"},{\"BILLERCD\":\"llllllPPP\",\"BILLERNAME\":\"pppppppppp\"},{\"BILLERCD\":\"voatinggg\",\"BILLERNAME\":\"666\"}]";
				try 
				{
					if (xml_data[0].indexOf("FAILED") > -1) 
					{
						showAlert(getString(R.string.alert_err));
					} 
					else 
					{
						operator=new ArrayList<String>();
						operatorCd=new ArrayList<String>();
						String decryptedOperator=xml_data[0];
						Log.e("OPERATOR",decryptedOperator);
						operator.add("All");
						operatorCd.add("All");
						JSONArray jArr=new JSONArray(decryptedOperator);
						for(int i=0;i<jArr.length();i++)
						{
							JSONObject jObj=jArr.getJSONObject(i);
							operator.add(jObj.getString("BILLERNAME"));
							operatorCd.add(jObj.getString("BILLERCD"));
						}
						
						ArrayAdapter<String> catArr = new ArrayAdapter<String>(viewHistory,R.layout.spinner_item, operator);
						catArr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spi_operator.setAdapter(catArr);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			//} 
			//else 
			//{
			//	retMess = getString(R.string.alert_000);
			//	showAlert(retMess);
			//}
		}

	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) 
	{
		switch(parent.getId())
		{
			case R.id.spi_category:
				if(!spi_category.getSelectedItem().toString().equalsIgnoreCase("Select"))
				{
					//if(chkConnectivity()==0)
						new CallWebServiceFetchOperator().execute();
			      //  else
			        //	showAlert(act.getString(R.string.alert_000));
				}
			break;
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) 
	{
	}
	
	public int chkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) viewHistory.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try 
		{
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("state1 ---------" + state1);
			if (state1) 
			{
				switch (state) 
				{
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
					retMess=getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} 
			else 
			{
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} 
		catch (NullPointerException ne) 
		{
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} 
		catch (Exception e) 
		{
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
