package list.shivsamarth_mbs;



import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoUtil;
import mbLib.CustomAdapterforPayeelist;
import mbLib.DatabaseManagement;

import mbLib.MBSUtils;
import mbLib.MyThread;
import mbLib.PayeeBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class Recharges_removepayee extends Activity implements OnClickListener{
	
	private static final String GET_MMID = "generateMMID";
	//private static final String METHOD_SAVE_TRANSFERTRAN = "";
	//private static final String METHOD_GET_TRANSFERCHARGE = "";

	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private static String METHOD_delete_payee="deletePayee";
	private static String METHOD_fetch_payee="fetchpayee";
	
	private ListView listView1;
	MainActivity act;
	int flag=0;
	int check=0;
	DatabaseManagement dbms;
	//HomeFragment homeFrag;
	Context context;
	private static final String MY_SESSION = "my_session";
	//Editor e;
	Editor e;
	String retMess = "", retVal = "";
	String stringValue = "",custid="";//,accountNo="";
	String all_acnts = "", str2 = "", str = "",req_id="",custId = "",userId="",cust_mob_no="",category="";
	String operator="",billercd="",consumerno="",dob="",mobno="",accounname="",imeino="";
	String acc_type = "SAVING_CUR",billerAccId="";
	int chekacttype=0;
	String lbltxtnew="";
	TextView txt_heading,emptyElement;
	ImageView img_heading;
	ImageButton btn_home,btn_back;
	ListView rechargelistremove;
	Button btnRemoveback;
	String acnt_inf = "",accountinfo="";
	String accNumber = null;
	String[] prgmNameList, prgmNameListTemp;
	RadioButton radio;
	public String encrptdTranMpin;
	///Bundle b1;
	public ArrayList<PayeeBean> payeeBeans;
	protected String accStr;
	Recharges_removepayee recharges_removepayee;
	private MyThread t1;
	int timeOutInSecs=300;
	
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	public Recharges_removepayee(){}
	public Recharges_removepayee(MainActivity a) { 
		act = a;
		recharges_removepayee=this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		recharges_removepayee=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recharge_remove_payee);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		Intent getdata=getIntent();
    	Bundle bundle = getdata.getExtras();
		if (bundle != null) {
			category = bundle.getString("CATEGORY");
			Log.e("CATEGORY","----"+category);
			
		}
		if(category.equalsIgnoreCase("PREPAID MOBILE"))
		{
			txt_heading.setText(recharges_removepayee.getString(R.string.lbl_rech_Rechargesremovemob));
		}
		if(category.equalsIgnoreCase("PREPAID DTH"))
		{
			txt_heading.setText(recharges_removepayee.getString(R.string.lbl_rech_Rechargesremovedth));
		}
		if(category.equalsIgnoreCase("DATACARD"))
		{
			txt_heading.setText(recharges_removepayee.getString(R.string.lbl_rech_Rechargesremovedata));
		}
		//txt_heading.setText(act.getString(R.string.lbl_rech_Rechargesremove));
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		
		rechargelistremove=(ListView)findViewById(R.id.rechargelistremove);
		
		emptyElement=(TextView)findViewById(R.id.emptyElement);
		
		btnRemoveback=(Button)findViewById(R.id.btnRemoveback);
		btnRemoveback.setOnClickListener(this);
		
		btn_back.setOnClickListener(this);
		
		var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		/*dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
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
        }*/
        
       // if(chkConnectivity()==0)
        	new CallWebServiceFetchPayee().execute();
       // else
        //	showAlert(act.getString(R.string.alert_000));
       //new CallWebServiceFetchPayeedth().execute();
		
         	t1 = new MyThread(timeOutInSecs,this,var1,var3);
    		t1.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btnRemoveback:	
			
			
		
			
			break;
		case R.id.btn_back:	
			Intent in=new Intent(recharges_removepayee,Recharges.class);
			startActivity(in);
			recharges_removepayee.finish(); 
			/*Fragment OthrSrvcFragment = new Recharges(act);
			act.setTitle(getString(R.string.lbl_title_change_mpin));
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, OthrSrvcFragment).commit();*/
			 break;
		}
		
	}
	
	class CallWebServiceFetchPayee extends AsyncTask<Void, Void, Void> {
        //String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(recharges_removepayee);

        String[] xmlTags = { "PARAMS" };
        String[] valuesToEncrypt = new String[1];
        String generatedXML ="";
        //String agentCd="1",memberId="";
        JSONObject jsonObj=new JSONObject();
       String fetchdata="";
        protected void onPreExecute()
        {
            loadProBarObj.show();
            try
            { 
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY",category);
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(recharges_removepayee));
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

           /* try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_fetch_payee );
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
            }*/
            return null;
        }// end doInBackground

        protected void onPostExecute(Void paramVoid)
        {
            String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
       if(category.equalsIgnoreCase("PREPAID MOBILE")){
            fetchdata="[{\"CONSUMERNO\":\"9970971040\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\",\"BILLERACCID\":\"94\",\"ACCNAME\":\"home\"},{\"CONSUMERNO\":\"9876121212\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\",\"BILLERACCID\":\"92\",\"ACCNAME\":\"same\"},{\"CONSUMERNO\":\"9970099700\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"4\",\"ACCNAME\":\"shri3\"},{\"CONSUMERNO\":\"9980099800\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"3\",\"ACCNAME\":\"shri2\"},{\"CONSUMERNO\":\"8908908908\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"93\",\"ACCNAME\":\"asach\"},{\"CONSUMERNO\":\"8908908908\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"91\",\"ACCNAME\":\"asach\"},{\"CONSUMERNO\":\"9764074608\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"6\",\"ACCNAME\":\"pooja madam\"},{\"CONSUMERNO\":\"9887649694\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"IDEAPRE\",\"BILLERNAME\":\"IDEA PREPAID\",\"BILLERACCID\":\"90\",\"ACCNAME\":\"ovk\"}]";
       }
       else if(category.equalsIgnoreCase("PREPAID DTH")){
    	   fetchdata="[{\"CONSUMERNO\":\"254568956\",\"CATEGORY\":\"PREPAID DTH\",\"BILLERCD\":\"AIRTELDTH\",\"BILLERNAME\":\"AIRTEL DTH\",\"BILLERACCID\":\"28\",\"ACCNAME\":\"dhata dth\"},{\"CONSUMERNO\":\"58448\",\"CATEGORY\":\"PREPAID DTH\",\"BILLERCD\":\"BIGTVDTH\",\"BILLERNAME\":\"BIG TV DTH\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"mahesh\"}]";
       }
       
       
            loadProBarObj.dismiss();
            try
            {
            	if(!xml_data[0].contains("NODATA"))
                {
            		JSONArray ja = new JSONArray(fetchdata);
            		payeeBeans=new ArrayList<PayeeBean>();
            		for(int i = 0; i < ja.length(); i++) 
            		{
                         JSONObject jObj = ja.getJSONObject(i);
                         PayeeBean payeeBeanobj= new PayeeBean();
                         if(jObj.has("CATEGORY"))
                         {
                        	 payeeBeanobj.setCategory(jObj.getString("CATEGORY"));
                         }
                         if(jObj.has("MOBNO"))
                         {
                        	 payeeBeanobj.setMobileno(jObj.getString("MOBNO"));
                         }
                         if(jObj.has("ACCNAME"))
                         {
                        	 payeeBeanobj.setAccname(jObj.getString("ACCNAME"));
                         }
                         if(jObj.has("BILLERCD"))
                         {
                        	  payeeBeanobj.setBillercd(jObj.getString("BILLERCD"));
                         }
                         if(jObj.has("CONSUMERNO"))
                         {
                        	  payeeBeanobj.setConsumerno(jObj.getString("CONSUMERNO"));
                         }
                         if(jObj.has("DOB"))
                         {
                        	 payeeBeanobj.setDob(jObj.getString("DOB"));
                         }
                         if(jObj.has("IMEINO"))
                         {
                        	 payeeBeanobj.setImei(jObj.getString("IMEINO"));
                         }
                         if(jObj.has("BILLERACCID"))
                         {
                        	 payeeBeanobj.setBillerAccId(jObj.getString("BILLERACCID"));
                         }
                         payeeBeans.add(payeeBeanobj);
                	}
	                if(ja.length()>0)
	                {
		                CustomAdapterforPayeelist addpayeelist=new CustomAdapterforPayeelist(recharges_removepayee,payeeBeans,category);
		                rechargelistremove.setAdapter(addpayeelist);
		                rechargelistremove.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		                rechargelistremove.setEnabled(true);
		                rechargelistremove.setOnItemClickListener(new AdapterView.OnItemClickListener()
		                {
		                	@Override
		                	public void onItemClick(AdapterView<?> parent,View view, int i, long id) 
		                	{
								consumerno=payeeBeans.get(i).getConsumerno();
								category=payeeBeans.get(i).getCategory();
								imeino=payeeBeans.get(i).getImei();
								billercd=payeeBeans.get(i).getBillercd();
								mobno=payeeBeans.get(i).getMobileno();
	                            dob=payeeBeans.get(i).getDob();
	                            billerAccId=payeeBeans.get(i).getBillerAccId();
	                            Log.e("consumerno", "----"+consumerno);
	                            Log.e("category", "----"+category);
	                            Log.e("imeino", "----"+imeino);
	                            Log.e("billercd", "----"+billercd);
	                            Log.e("mobno", "----"+mobno);
	                            Log.e("dob", "----"+dob);
	                            Log.e("billerAccId", "----"+billerAccId);
	                            //showAlert1(getString(R.string.alrt_rech_Rechargedeletepayee));
	                            Bundle b1=new Bundle();
	                            b1.putString("FROMACT","RECHARGE");
	            				b1.putString("CATEGORY",category);
	            				b1.putString("BILLERACCID",billerAccId);
	            				b1.putString("BILLERCD",billercd);
	            				
	            				b1.putString("MOBNO",consumerno);
	            				b1.putString("CUST_ID",consumerno);
		            			b1.putString("ACCNM",payeeBeans.get(i).getAccname());
		            			
	            				Intent in=new Intent(recharges_removepayee,RemovePayee.class);
	            				in.putExtras(b1);
								startActivity(in);
								recharges_removepayee.finish(); 
	                          /*  Fragment fragment =new RemovePayee(act);
	            				FragmentManager fragmentManager = getFragmentManager();
	                  			fragment.setArguments(b1);
	                  			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
		                	}
		                });
	                }
	                else
	                {
	                	rechargelistremove.setVisibility(View.GONE);
	                	emptyElement.setVisibility(View.VISIBLE);
	                }
                }
                else
                {
                	rechargelistremove.setVisibility(View.GONE);
                	emptyElement.setVisibility(View.VISIBLE);
                }
                
            }
            catch(JSONException je)
            {
               
                je.printStackTrace();
            }

        }// end onPostExecute

    }// end CallWebServiceFetchPayee
	
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) recharges_removepayee
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
						// pb_wait.setVisibility(ProgressBar.VISIBLE);
						// locManager = (LocationManager)
						// getSystemService(Context.LOCATION_SERVICE);
						// netFlg = gpsFlg = 1;
						// Toast.makeText(this, ""+pref,
						// Toast.LENGTH_LONG).show();
						// if (pref.equals("G"))
						// new GpsTimer(timeout * 1000, 1000,
						// this);
						// else
						// new NetworkTimer(timeout * 1000,
						// 1000, this);
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
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			;
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("rechadaddnmobile   mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			;
			showAlert(retMess);
		}
		return flag;
	}// end chkConnectivity
	
	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(recharges_removepayee,""+str)  
		{
			@Override
			public void onClick(View v)   
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if(str.equalsIgnoreCase(act.getString(R.string.alert_payeemobremov_rech)))
						{
							Intent in=new Intent(recharges_removepayee,Recharges.class);
							startActivity(in);
							recharges_removepayee.finish(); 
							
							/*Fragment OthrSrvcFragment = new Recharges(act);
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction().replace(R.id.frame_container, OthrSrvcFragment).commit();*/
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
	
	class CallWebServiceRemovePayee extends AsyncTask<Void, Void, Void> {
        //String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(recharges_removepayee);

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
                jsonObj.put("CATEGORY",category);
                jsonObj.put("IMEINO",MBSUtils.getImeiNumber(recharges_removepayee));
                jsonObj.put("BILLERCD",billercd);
                jsonObj.put("CONSUMERNO",consumerno);
                jsonObj.put("DOB",dob);
                jsonObj.put("MOBNO",mobno);
                jsonObj.put("ACCNAME",accounname);
                jsonObj.put("BILLERACCID",billerAccId);
         
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

            /*try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_delete_payee );
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
            }*/
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
                	showAlert(getString(R.string.alert_payeemobremov_rech));
                }
                else
                {
                	showAlert(getString(R.string.alert_payeemobremovfail_rech));
                }
            }
            catch(Exception je)
            {
                je.printStackTrace();
            }
        }// end onPostExecute

    }// end CallWebServiceremovePayee
	
	public void showAlert1(String str) {  
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass1 alert = new ErrorDialogClass1(recharges_removepayee, "" + str);
		alert.show();
	}
	
	public class ErrorDialogClass1 extends Dialog implements OnClickListener  
	{

		private Context activity;
		private Dialog d;
		private Button ok,no;
		private TextView txt_message; 
		public  String textMessage;
		public ErrorDialogClass1(Context activity,String textMessage) 
		{
			super(activity);		
			this.textMessage=textMessage;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.custom_alert_dialog);		
			ok = (Button)findViewById(R.id.btn_yes);
			no = (Button)findViewById(R.id.btn_no);
			txt_message=(TextView)findViewById(R.id.txt_dia);
			txt_message.setText(textMessage);
			ok.setOnClickListener(this);
			no.setOnClickListener(this);
		}//end onCreate

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.btn_yes:
					/*FragmentManager fragmentManager;
					Fragment fragment = new GenerateMMID(act);
					act.setTitle(getString(R.string.lbl_mmid));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();*/
					Log.e("onClick","btn_yes");
					Log.e("onClick","btn_yes");
					Log.e("onClick","btn_yes");
					
					

					InputDialogBox inputBox = new InputDialogBox(recharges_removepayee);
					inputBox.show();
					
					//.......new CallWebServiceRemovePayee().execute();
				 // break;	
				case R.id.btn_no:
					this.dismiss();
				  break;	
				default:
				  break;
			}
			dismiss();
		}
	}//end class
	
	public class InputDialogBox extends Dialog implements OnClickListener {
		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		Button btnOk;
		String strmpin = "";
		TextView txtLbl;
		boolean flg;

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
		public void onClick(View v) 
		{
			if(v.getId()==R.id.btnOK)
			{	
				try 
				{
					String str = mpin.getText().toString().trim();
					encrptdTranMpin = str;////ListEncryption.encryptData(custId + str);
					
					if (str.length() == 0) 
					{
						retMess = getString(R.string.alert_enterTranMpin);
						showAlert(retMess);// setAlert();
						this.show();
					} 
					else if (str.length() != 6) {
						retMess = getString(R.string.alert_TranmipnMust6dig);
						showAlert(retMess);// setAlert();
						this.show();
					} 
					else 
					{
						//if(chkConnectivity()==0)
		             	 //  {	
		              		   Log.e("new CallWebServiceRemovePayee().execute();***","......");
		              		 new CallWebServiceRemovePayee().execute();
		             	 //  }
		             	 //  else
		             	   //{
		             	  //     showAlert(act.getString(R.string.alert_000));
		             	  // }
		                   
						this.hide();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
				}
			}	
		}// end onClick
	}// end InputDialogBox

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
