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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


public class Recharges extends Activity implements OnClickListener
{
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private static String METHOD_fetch_payee="fetchpayee";
	
	private ListView listView1;
	MainActivity act;
	
	TextView txt_heading,emptyElement;
	ImageView img_heading;
	ImageButton btn_home,btn_back;
	ListView rechargelist;
	Button btnmobile,btnDTH,btndatacard,btnFavorites,btnAddNew,btnRemove,btnHistory;
	RadioButton radio;
	public ArrayList<PayeeBean> payeeBeans;
	DatabaseManagement dbms;
	String fetchdata="[{\"CONSUMERNO\":\"9970971040\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\",\"BILLERACCID\":\"94\",\"ACCNAME\":\"home\"},{\"CONSUMERNO\":\"9876121212\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\",\"BILLERACCID\":\"92\",\"ACCNAME\":\"same\"},{\"CONSUMERNO\":\"9970099700\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"4\",\"ACCNAME\":\"shri3\"},{\"CONSUMERNO\":\"9980099800\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"3\",\"ACCNAME\":\"shri2\"},{\"CONSUMERNO\":\"8908908908\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"93\",\"ACCNAME\":\"asach\"},{\"CONSUMERNO\":\"8908908908\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"91\",\"ACCNAME\":\"asach\"},{\"CONSUMERNO\":\"9764074608\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"6\",\"ACCNAME\":\"pooja madam\"},{\"CONSUMERNO\":\"9887649694\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"IDEAPRE\",\"BILLERNAME\":\"IDEA PREPAID\",\"BILLERACCID\":\"90\",\"ACCNAME\":\"ovk\"}]";
	Context context;
	private static final String MY_SESSION = "my_session";
	Editor e;
	String retMess = "", retVal = "", stringValue = "",custid="",all_acnts = "", str2 = "", str = "",req_id="",
			custId = "",userId="",cust_mob_no="",category="",acc_type = "SAVING_CUR",operator="",billercd="",
			consumerno="",dob="",mobno="",accounname="",imeino="",acnt_inf = "",accountinfo="",billerAccId="",
			accNumber = null;
	String[] prgmNameList, prgmNameListTemp;
	int chekacttype=0, flag=0,check=0;
	private MyThread t1;
	int timeOutInSecs=300;
	protected String accStr;
	Recharges recharge;
	Bundle b1rech=new Bundle();
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		recharge=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recharge);

		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(recharge.getString(R.string.lbl_rech_Recharges));
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.recharge);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		btnAddNew=(Button)findViewById(R.id.btnAddNew);
		rechargelist=(ListView)findViewById(R.id.rechargelist);
		btnmobile=(Button)findViewById(R.id.btnmobile);
		btnDTH=(Button)findViewById(R.id.btnDTH);
		emptyElement=(TextView)findViewById(R.id.emptyElement);
		btndatacard=(Button)findViewById(R.id.btndatacard);
		btnRemove=(Button)findViewById(R.id.btnRemove);
		btnHistory=(Button)findViewById(R.id.btnHistory);
		btnRemove.setOnClickListener(this);
		btndatacard.setOnClickListener(this);
		btnDTH.setOnClickListener(this);
		btnmobile.setOnClickListener(this);
		btnAddNew.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
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
        btnDTH.setBackgroundResource(R.color.blur_buttonlist);
		btndatacard.setBackgroundResource(R.color.blur_buttonlist);
        category="PREPAID MOBILE";
        //if(chkConnectivity()==0)
        	new CallWebServiceFetchPayee().execute();
        	
        	t1 = new MyThread(timeOutInSecs,this,var1,var3);
    		t1.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnAddNew:	
			if(category.equalsIgnoreCase("PREPAID MOBILE"))
			{
				Bundle b1=new Bundle();
				b1.putString("CATEGORY","PREPAID MOBILE");
				b1.putString("FROMACT","RECHARGE");
				Intent in2=new Intent(recharge,AddPayee.class);//ViewHistory
				in2.putExtras(b1);
				startActivity(in2);
				recharge.finish(); 
				//Fragment fragment =new Recharge_mobile(act);// new Recharge_mobile(act);
				/*Fragment fragment =new AddPayee(act);// new Recharge_mobile(act);
				fragment.setArguments(b1);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
			}
			if(category.equalsIgnoreCase("PREPAID DTH"))
			{
				Bundle b1=new Bundle();
				b1.putString("CATEGORY","PREPAID DTH");
				b1.putString("FROMACT","RECHARGE");
				Intent in2=new Intent(recharge,AddPayee.class);//ViewHistory
				in2.putExtras(b1);
				startActivity(in2);
				recharge.finish(); 
				//Fragment fragment =new Recharge_dth(act);// new Recharge_mobile(act);
				/*Fragment fragment =new AddPayee(act);
				fragment.setArguments(b1);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
			}
			if(category.equalsIgnoreCase("DATACARD"))
			{
				Bundle b1=new Bundle();
				b1.putString("CATEGORY","DATACARD");
				b1.putString("FROMACT","RECHARGE");
			/*	Fragment fragment =new Recharge_datacard(act);// new Recharge_mobile(act);
				//Fragment fragment =new AddPayee(act);
				fragment.setArguments(b1);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
			}
			break;
		case R.id.btnRemove:	
			
			Bundle b1=new Bundle();
			b1.putString("CATEGORY", category);
			Log.e("category","-----------"+category);
			Intent in=new Intent(recharge,Recharges_removepayee.class);
			in.putExtras(b1);
			startActivity(in);
			recharge.finish(); 
			/*Fragment fragment =new Recharges_removepayee(act);// new Recharge_mobile(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragment.setArguments(b1);
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
		
			
			break;
		case R.id.btnmobile:	
			category="PREPAID MOBILE";
			rechargelist.setVisibility(View.VISIBLE);
			btnmobile.setBackgroundResource(R.color.buttomrechargecolor);
			btnDTH.setBackgroundResource(R.color.blur_buttonlist);
			btndatacard.setBackgroundResource(R.color.blur_buttonlist);
			rechargelist.setAdapter(null);
			fetchpayeemob();
			break;
        case R.id.btnDTH:	
        	category="PREPAID DTH";
        	rechargelist.setVisibility(View.VISIBLE);
        	btnmobile.setBackgroundResource(R.color.blur_buttonlist);
			btnDTH.setBackgroundResource(R.color.buttomrechargecolor);
			btndatacard.setBackgroundResource(R.color.blur_buttonlist);
        	rechargelist.setAdapter(null);
        	fetchpayeedth();
			break;
        case R.id.btndatacard:	
        	category="DATACARD";
        	rechargelist.setVisibility(View.VISIBLE);
        	btnmobile.setBackgroundResource(R.color.blur_buttonlist);
			btnDTH.setBackgroundResource(R.color.blur_buttonlist);
			btndatacard.setBackgroundResource(R.color.buttomrechargecolor);
        	rechargelist.setAdapter(null);
        	fetchpayeedatacard();
			break;
	    case R.id.btn_back:
			Intent inw=new Intent(recharge,DashboardActivity.class);
			startActivity(inw);
			recharge.finish(); 
			break;
	    case R.id.btnHistory:
	    	Bundle b2=new Bundle();
			b2.putString("FROMACT","RECHARGE");
			Intent in2=new Intent(recharge,ViewHistory.class);//ViewHistory
			in2.putExtras(b2);
			startActivity(in2);
			recharge.finish(); 
			/*Fragment fragment2 =new ViewHistory(act); 
			fragment2.setArguments(b2);
			FragmentManager fragmentManager2 = getFragmentManager();
			fragmentManager2.beginTransaction().replace(R.id.frame_container, fragment2).commit();*/
	    	break;
		}
	}
	
	class CallWebServiceFetchPayee extends AsyncTask<Void, Void, Void> 
	{
        LoadProgressBar loadProBarObj = new LoadProgressBar(recharge);

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
                jsonObj.put("CATEGORY",category);
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(recharge));
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

            loadProBarObj.dismiss();
            try
            {
              //  JSONObject retJson = new JSONObject(xml_data[0]);
                if(!fetchdata.contains("NODATA"))
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
                     if(jObj.has("BILLERNAME"))
                     {
                    	 payeeBeanobj.setOperator(jObj.getString("BILLERNAME"));
                     }
                     if(jObj.has("BILLERACCID"))
                     {
                    	 payeeBeanobj.setBillerAccId(jObj.getString("BILLERACCID"));
                     }
                     payeeBeans.add(payeeBeanobj);
                }
	            if(ja.length()>0)
	            {
	                CustomAdapterforPayeelist addpayeelist=new CustomAdapterforPayeelist(recharge,payeeBeans,category);
	                rechargelist.setAdapter(addpayeelist);
	                rechargelist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	                rechargelist.setEnabled(true);
	                
	                rechargelist.setOnItemClickListener(new AdapterView.OnItemClickListener()
	                {
						@Override
						public void onItemClick(AdapterView<?> parent,View view, int i, long id) 
						{
							consumerno=payeeBeans.get(i).getConsumerno();
							imeino=payeeBeans.get(i).getImei();
							billercd=payeeBeans.get(i).getBillercd();
							mobno=payeeBeans.get(i).getMobileno();
                            dob=payeeBeans.get(i).getDob();
                            accounname=payeeBeans.get(i).getAccname();
                            operator=payeeBeans.get(i).getOperator();
                            billerAccId=payeeBeans.get(i).getBillerAccId();
                            Log.e("consumerno", "----"+consumerno);
                            Log.e("category", "----"+category);
                            Log.e("imeino", "----"+imeino);
                            Log.e("billercd", "----"+billercd);
                            Log.e("mobno", "----"+mobno);
                            Log.e("dob", "----"+dob);
                            Log.e("operator", "----"+operator);
                            Log.e("billerAccId", "----"+billerAccId);
                            
                            b1rech.putString("CATEGORY", category);
                            b1rech.putString("IMEINO", imeino);
                            b1rech.putString("BILLERCD", billercd);
                            b1rech.putString("CONSUMERNO", consumerno);
                            b1rech.putString("DOB", dob);
                            b1rech.putString("MOBNO", mobno);
                            b1rech.putString("ACCNAME", accounname);
                            b1rech.putString("BILLERNAME", operator);
                            b1rech.putString("BILLERACCID", billerAccId);
                            recgargetransaction();
						}
	                	
	                });
	                
	                }
	                else
	                {
	                	rechargelist.setVisibility(View.GONE);
	                	emptyElement.setVisibility(View.VISIBLE);
	                	
	                }
                }
                else
                {
                	rechargelist.setVisibility(View.GONE);
                	emptyElement.setVisibility(View.VISIBLE);
                }
                
            }
            catch(JSONException je)
            {
               
                je.printStackTrace();
            }

        }// end onPostExecute

    }// end CallWebServiceFetchPayee
	
	public void fetchpayeemob() 
	{
		try 
		{
			System.out.println("--------------- 44 ------------");
			this.flag = 0;//chkConnectivity();
			Log.e("saveData", "saveDatasaveData " + flag);
			fetchdata="[{\"CONSUMERNO\":\"9970971040\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\",\"BILLERACCID\":\"94\",\"ACCNAME\":\"home\"},{\"CONSUMERNO\":\"9876121212\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRCELPRE\",\"BILLERNAME\":\"AIRCEL PREPAID\",\"BILLERACCID\":\"92\",\"ACCNAME\":\"same\"},{\"CONSUMERNO\":\"9970099700\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"4\",\"ACCNAME\":\"shri3\"},{\"CONSUMERNO\":\"9980099800\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"3\",\"ACCNAME\":\"shri2\"},{\"CONSUMERNO\":\"8908908908\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"93\",\"ACCNAME\":\"asach\"},{\"CONSUMERNO\":\"8908908908\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"91\",\"ACCNAME\":\"asach\"},{\"CONSUMERNO\":\"9764074608\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"AIRTELPRE\",\"BILLERNAME\":\"AIRTEL PREPAID\",\"BILLERACCID\":\"6\",\"ACCNAME\":\"pooja madam\"},{\"CONSUMERNO\":\"9887649694\",\"CATEGORY\":\"PREPAID MOBILE\",\"BILLERCD\":\"IDEAPRE\",\"BILLERNAME\":\"IDEA PREPAID\",\"BILLERACCID\":\"90\",\"ACCNAME\":\"ovk\"}]";
			if (this.flag == 0) 
			{
				new CallWebServiceFetchPayee().execute();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Exception is in onclick :" + e);
		}
	}// end fetchpayeemob
	
	public void fetchpayeedth() {
		try {
			System.out.println("--------------- 44 ------------");
			this.flag = 0;//chkConnectivity();
			Log.e("saveData", "saveDatasaveData " + flag);
			fetchdata="[{\"CONSUMERNO\":\"254568956\",\"CATEGORY\":\"PREPAID DTH\",\"BILLERCD\":\"AIRTELDTH\",\"BILLERNAME\":\"AIRTEL DTH\",\"BILLERACCID\":\"28\",\"ACCNAME\":\"dhata dth\"},{\"CONSUMERNO\":\"58448\",\"CATEGORY\":\"PREPAID DTH\",\"BILLERCD\":\"BIGTVDTH\",\"BILLERNAME\":\"BIG TV DTH\",\"BILLERACCID\":\"10\",\"ACCNAME\":\"mahesh\"}]";
			if (this.flag == 0) {
				new CallWebServiceFetchPayee().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is in onclick :" + e);
		}
	}// end fetchpayeedth
	
	public void fetchpayeedatacard() {
		try {
			System.out.println("--------------- 44 ------------");
			this.flag =0; //chkConnectivity();
			Log.e("saveData", "saveDatasaveData " + flag);
			fetchdata="NODATA";
			if (this.flag == 0) {
				new CallWebServiceFetchPayee().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is in onclick :" + e);
		}
	}// end fetchpayeedatacard
	
	public void recgargetransaction() {
		try {
			System.out.println("--------------- 44 ------------");
			this.flag = 0;//chkConnectivity();
			Log.e("saveData", "saveDatasaveData " + flag);
			if (this.flag == 0) {
				//new CallWebServiceFetchPayee().execute();
				///Bundle b1=new Bundle();
				//b1.putString("CATEGORY","PREPAID MOBILE");
				//Fragment fragment =new Recharge_mobile(act);// new Recharge_mobile(act);
				/*Fragment fragment =new BillPayment(act);// new Recharge_mobile(act);
				//fragment.setArguments(b1);
				
			    //Fragment fragment =new Recharge_transaction(act);// new Recharge_mobile(act);
      			FragmentManager fragmentManager = getFragmentManager();
      			fragment.setArguments(b1rech);
      			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
      			
      			Intent in=new Intent(recharge,BillPayment.class);
      			in.putExtras(b1rech);
    			startActivity(in);
    			recharge.finish(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is in onclick :" + e);
		}
	}// end recgargetransaction
	
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) recharge
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
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) 
					{}
					break;
				case DISCONNECTED:
					flag = 1;
					retMess = getString(R.string.alert_014);
					showAlert(retMess);

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
		ErrorDialogClass alert = new ErrorDialogClass(recharge,""+str)  
		{
			@Override
			public void onClick(View v)   
			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+isWSCalled);
						/*if(textMessage.equalsIgnoreCase(SetMPIN.this.getString(R.string.alert_070)))
						{
							//Log.e("SetMPIN","SetMPIN...mpin set");
							Intent in = new Intent(SetMPIN.this,LoginActivity.class);
							startActivity(in);
							finish();
						}
						else
						
						if(textMessage.equalsIgnoreCase(SetMPIN.this.getString(R.string.alert_103)))
						{
							//Log.e("SetMPIN","SetMPIN...mpin set");
							Bundle bObj=new Bundle();
							Intent in = new Intent(SetMPIN.this,LoginActivity.class);
							bObj.putString("CUSTID", strCustId);
							in.putExtras(bObj);
							startActivity(in);
							finish();
						}*/
					  break;
					  
					  
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.show();
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

