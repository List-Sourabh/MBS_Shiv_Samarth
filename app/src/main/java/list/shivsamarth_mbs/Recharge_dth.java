package list.shivsamarth_mbs;



import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

//import com.google.android.gms.wallet.LineItem.Role;

import mbLib.Accountbean;
import mbLib.CryptoUtil;
import mbLib.DatabaseManagement;

import mbLib.MBSUtils;
import mbLib.MyThread;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Recharge_dth extends Activity implements OnClickListener
{
	MainActivity act;
	Context context;
	TextView txt_heading;
	EditText txt_cunno,txt_accname;
	ImageView img_heading;
	ImageButton btn_home,btn_back,spinner_btn;
	Button btnmobile,btnDTH,btndatacard,btnFavorites,btnAddNew,btnRemove,btn_submit;
	RadioButton radio;
	Spinner spin_operator;
	private ListView listView1;
	
    private static final String METHOD_fetch_operator = "fetchOperator";
	private static final String METHOD_addpayee = "addpayee";
	private static final String METHOD_deletePayee = "deletePayee";
	private static final String METHOD_FETCH_BILLER = "fetchBiller";
	
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	
	DatabaseManagement dbms;
	
	public String encrptdTranMpin;
	int flag=0,check=0,chekacttype=0;
	String oper = "",cunsumerno="",stringValue="",custId="",userId,cust_mob_no="",retVal="",retMess="";
	String accNumber = null,operator="",category="",billercd="",consumerno="",dob="",mobno="",accounname="";
	String[] prgmNameList, prgmNameListTemp;
	
	public ArrayAdapter<String> optrArr =null;
    ArrayList<String> billercodel = null;
	ArrayList<String> opertList = null;
	private ArrayList<Accountbean> Accountbean_arr;
	protected String accStr;
	private MyThread t1;
	int timeOutInSecs=300;
	Recharge_dth recharge_dth;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public Recharge_dth(MainActivity a)
	{
		act = a;
		recharge_dth=this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		recharge_dth=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrech_dthno);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		btn_submit=(Button)findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		spin_operator=(Spinner)findViewById(R.id.spin_operator);
		txt_heading.setText(act.getString(R.string.lbl_rech_Recharges_dth));
		txt_cunno=(EditText)findViewById(R.id.txt_cunno);
		txt_accname=(EditText)findViewById(R.id.txt_accname);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
		spinner_btn.setOnClickListener(this);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		Log.e("retValStr","...."+stringValue);
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);
	        	userId=c1.getString(3);
		    	Log.e("UserId","......"+userId);
		    	cust_mob_no=c1.getString(4);
		    	Log.e("cust_mobNO","..."+cust_mob_no);
	        }
        }
        if(chkConnectivity()==0)
        	new CallWebServiceFetchOperator().execute();
        else
        	showAlert(act.getString(R.string.alert_000));
        
       	t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.spinner_btn:
			Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn",
					"DROP DOWN IMG BTN CLICKED....");
			spin_operator.performClick();
			break;
			
			case R.id.btn_submit:
				accounname=txt_accname.getText().toString();
				consumerno=txt_cunno.getText().toString();
				operator=spin_operator.getSelectedItem().toString();
				if (operator.equalsIgnoreCase("Select Operator")) 
				{
					showAlert(getString(R.string.alert_rechargeoper1));
				} 
				else if(consumerno.length()==0)
				{
					showAlert(getString(R.string.alert_rechargeoper5));
					txt_cunno.requestFocus();
				}
				else if ( consumerno.length()>20 ) 
				{
					// retMess = "Please Enter Valid Mobile Number.";
					retMess = getString(R.string.alert_rechargeoper4);
					showAlert(retMess);
					txt_cunno.requestFocus();
				}
				else if (accounname.length()==0) 
				{
					showAlert(getString(R.string.alert_rechargeoper2));
					txt_accname.requestFocus();
				}
				else
				{
					ConfirmDialog confirm = new ConfirmDialog(act);
					confirm.show();
                }
                break;
			case R.id.btn_back:
				Intent in=new Intent(recharge_dth,Recharges.class);
				startActivity(in);
				recharge_dth.finish(); 
				/*Fragment OthrSrvcFragment = new Recharges(act);
				act.setTitle(getString(R.string.lbl_title_change_mpin));
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, OthrSrvcFragment).commit();*/ 
				break;
		}
	 }
	
	public class ConfirmDialog extends Dialog implements OnClickListener 
	{
		Activity activity;
	    Recharge_dth recharge_dth;
	    String msg, title;
	    Context appAcontext;
	    Button confirm,back;
	    String strmpin = "";
	    TextView txtLbl;
	    String Period="";
	    boolean flg;
	    // ListView mem_listView;
	    String checkString="",chkcheckbox="",checkacctype="";
	    LinearLayout layout_confrech_mobno, layout_confrech_consuno, layout_reqid, listview_layout,layout_acctype, layout_accno, layout_amount, layout_button, fd_layout,layout_jointmem;
	    TextView txt_confrech_oper, txt_confrech_mobno, txt_confrech_accnm, txt_accounttype, txt_accountno,txt_amount, lbl_amount,txt_confrech_consuno;
	    TextView txt_duration,txt_tax,txt_inttype,txt_interest_rates,txt_principleamt;
	    JSONArray jsonArray;
	    String memberName,memid;
	    TextView txt_jointmemvalue,txt_jointmem,txt_narration;

	    public ConfirmDialog(Activity recharge_dth)
	    {
	        super(recharge_dth);
	        this.activity=recharge_dth;
	    }
	    protected void onCreate(Bundle bdn) 
	    {
	        super.onCreate(bdn);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.confirm_recharges);
	        layout_confrech_mobno=(LinearLayout)findViewById(R.id.layout_confrech_mobno);
	        layout_confrech_mobno.setVisibility(View.GONE);
	        layout_confrech_consuno=(LinearLayout)findViewById(R.id.layout_confrech_consuno);
	        layout_confrech_consuno.setVisibility(View.VISIBLE);
	        txt_confrech_consuno=(TextView)findViewById(R.id.txt_confrech_consuno);
	        txt_confrech_oper=(TextView)findViewById(R.id.txt_confrech_oper);
	        txt_confrech_accnm=(TextView)findViewById(R.id.txt_confrech_accnm);
	        

	        confirm=(Button)findViewById(R.id.btn_confirm);
	        confirm.setVisibility(Button.VISIBLE);
	        confirm.setOnClickListener(this);
	        back=(Button)findViewById(R.id.btncnf_back);
	        confirm.setVisibility(Button.VISIBLE);
	        confirm.setOnClickListener(this);
	        back.setOnClickListener(this);
	        txt_confrech_oper.setText(spin_operator.getSelectedItem().toString().trim());
	        txt_confrech_consuno.setText(txt_cunno.getText().toString().trim());
	        txt_confrech_accnm.setText(txt_accname.getText().toString().trim());
	    }
		@Override
	    public void onClick(View v) 
		{
	        if (v.getId() == R.id.btn_confirm)
	        {
	            InputDialogBox inputBox = new InputDialogBox(act);
				inputBox.show();
	            this.dismiss();
	        }
	        else if(v.getId()== R.id.btncnf_back)
	        {
	        	 this.dismiss();
	        }
	    }
	}// end InputDialogBox
	
	class CallWebServiceFetchOperator extends AsyncTask<Void, Void, Void> 
	{
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        JSONObject jsonObj=new JSONObject();

        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY","PREPAID DTH");
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(act));
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
           
            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);;//getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);

            try 
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_fetch_operator );
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new        SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,15000);

                if (androidHttpTransport != null)
                    System.out.println("=============== androidHttpTransport is not null ");
                else
                    System.out.println("=============== androidHttpTransport is  null ");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                retVal = envelope.bodyIn.toString().trim();
                retVal = retVal.substring(retVal.indexOf("=") + 1,retVal.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Exception" + e);
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });

            loadProBarObj.dismiss();
            try
            {
            	if(!xml_data[0].contains("NODATA"))
                {
	                JSONArray ja = new JSONArray(xml_data[0]);
	                opertList = new ArrayList<String>();
	                billercodel = new ArrayList<String>();
	                billercodel.add("-1");
	                opertList.add("Select Operator");
	                for(int i=0;i<ja.length();i++)
	                {
	                    JSONObject jObj=ja.getJSONObject(i);
	                    billercodel.add(jObj.getString("BILLERCD"));
	                    opertList.add(jObj.getString("BILLERNAME"));
	                    
	                    Log.e("BILLERNAME","BILLERNAME===="+jObj.getString("BILLERNAME"));
	                }
	                optrArr =new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, opertList);// new ArrayAdapter<String>(Recharge_mobile.this, R.layout.spinner_item, opertList);
	                optrArr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	                spin_operator.setAdapter(optrArr);
	            }
	            else
	            {
	                showAlert(getString(R.string.alert_payeemob_nodata));
	            }
            }
            catch(JSONException je)
            {
               
                je.printStackTrace();
            }
        }// end onPostExecute

    }// end CallWebServiceFetchPayee
	
	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(act,""+str)  
		{
			@Override
			public void onClick(View v)   
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						
                        if(str.equalsIgnoreCase(act.getString(R.string.alert_payeemob_rech)))
						{
                        	
                        	Intent in=new Intent(recharge_dth,Recharges.class);
            				startActivity(in);
            				recharge_dth.finish(); 
							/*Fragment OthrSrvcFragment = new Recharges(act);
							act.setTitle(getString(R.string.lbl_title_change_mpin));
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, OthrSrvcFragment).commit();
							this.dismiss();*/
						}
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
	
	public class InputDialogBox extends Dialog implements OnClickListener {
		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		Button btnOk;
		String strmpin = "";
		TextView txtLbl;
		boolean flg;
		private String userId;

		public InputDialogBox(Activity activity) {
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) {
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.transfer_dialog);
			mpin = (EditText) findViewById(R.id.txtMpin);
			btnOk = (Button) findViewById(R.id.btnOK);
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			try {

				// System.out.println("========= inside onClick ============***********");
				String str = mpin.getText().toString().trim();
				 encrptdTranMpin = str;//ListEncryption.encryptData(custId
					//	+ str);
				String encrptdUranMpin =str;// ListEncryption.encryptData(userId
						//+ str);

				if (str.length() == 0) {
					retMess = getString(R.string.alert_enterTranMpin);
					showAlert(retMess);// setAlert();
					this.show();
				} else if (str.length() != 6) {
					retMess = getString(R.string.alert_TranmipnMust6dig);
					showAlert(retMess);// setAlert();
					this.show();
				} 
				else 
				{
					saveData();
					this.hide();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
			}

		}// end onClick
	}// end InputDialogBox
	
	public void saveData() 
	{
		try 
		{
			System.out.println("--------------- 44 ------------");
			this.flag = chkConnectivity();
			Log.e("saveData", "saveDatasaveData " + flag);
			if (this.flag == 0) {
				new CallWebServiceAddPayee().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is in onclick :" + e);
		}
	}// end saveData
	
	class CallWebServiceAddPayee extends AsyncTask<Void, Void, Void> {
        //String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        //String agentCd="1",memberId="";
        JSONObject jsonObj=new JSONObject();

        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
            
            	Log.e("con","11111"+consumerno);
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY","PREPAID DTH");
                jsonObj.put("IMEINO",MBSUtils.getImeiNumber(act));
                jsonObj.put("BILLERCD",billercodel.get(spin_operator.getSelectedItemPosition()));
                jsonObj.put("CONSUMERNO",consumerno);
                jsonObj.put("DOB","");
                jsonObj.put("MOBNO","");
                jsonObj.put("ACCNAME",accounname);
         
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
           
            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);;//getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);

            try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_addpayee );
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new        SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
                        15000);

                if (androidHttpTransport != null)
                    System.out
                            .println("=============== androidHttpTransport is not null ");
                else
                    System.out
                            .println("=============== androidHttpTransport is  null ");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                retVal = envelope.bodyIn.toString().trim();
                retVal = retVal.substring(retVal.indexOf("=") + 1,
                        retVal.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Exception" + e);
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });

            loadProBarObj.dismiss();
            try
            {
               if(xml_data[0].contains("SUCCESS"))
               {
                	showAlert(getString(R.string.alert_payeemob_rech));
               }
               else
               {
                	showAlert(getString(R.string.alert_payeemobfail_rech));
               }
            }
            catch(Exception je)
            {
               
                je.printStackTrace();
            }

        }// end onPostExecute

    }// end CallWebServiceAddPayee
	
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("rchmobiadd	in chkConnectivity () state1 ---------"
							+ state1);
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
					retMess = getString(R.string.alert_000);
					;
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				;
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("rechadaddnmobile",
					"NullPointerException Exception" + ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("rechadaddnmobile   mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}// end chkConnectivity
	
	class CallWebServiceFetchBiller extends AsyncTask<Void, Void, Void> 
	{
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        JSONObject jsonObj=new JSONObject();

        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY","PREPAID DTH");
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(act));
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
           
            valuesToEncrypt[0] = jsonObj.toString();

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
            System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);;//getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);

            try 
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_FETCH_BILLER );
                request.addProperty("Params", generatedXML);
                SoapSerializationEnvelope envelope = new        SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,15000);

                if (androidHttpTransport != null)
                    System.out.println("=============== androidHttpTransport is not null ");
                else
                    System.out.println("=============== androidHttpTransport is  null ");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                retVal = envelope.bodyIn.toString().trim();
                retVal = retVal.substring(retVal.indexOf("=") + 1,retVal.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Exception" + e);
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });

            loadProBarObj.dismiss();
            try
            {
            	if(!xml_data[0].contains("NODATA"))
                {
	                JSONArray ja = new JSONArray(xml_data[0]);
	                opertList = new ArrayList<String>();
	                billercodel = new ArrayList<String>();
	                billercodel.add("-1");
	                opertList.add("Select Operator");
	                for(int i=0;i<ja.length();i++)
	                {
	                    JSONObject jObj=ja.getJSONObject(i);
	                    billercodel.add(jObj.getString("BILLERCD"));
	                    opertList.add(jObj.getString("BILLERNAME"));
	                    
	                    Log.e("BILLERNAME","BILLERNAME===="+jObj.getString("BILLERNAME"));
	                }
	                optrArr =new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, opertList);// new ArrayAdapter<String>(Recharge_mobile.this, R.layout.spinner_item, opertList);
	                optrArr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	                spin_operator.setAdapter(optrArr);
	            }
	            else
	            {
	                showAlert(getString(R.string.alert_payeemob_nodata));
	            }
            }
            catch(JSONException je)
            {
               
                je.printStackTrace();
            }
        }// end onPostExecute

    }// end CallWebServiceFetchPayee
	
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
