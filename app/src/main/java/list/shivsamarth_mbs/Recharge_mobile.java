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

public class Recharge_mobile extends Activity implements OnClickListener{
	private static final String METHOD_fetch_operator = "fetchOperator";
	private static final String METHOD_addpayee = "addpayee";
	private static final String METHOD_deletePayee = "deletePayee";
	//private static final String METHOD_SAVE_TRANSFERTRAN = "";
	//private static final String METHOD_GET_TRANSFERCHARGE = "";

	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private ListView listView1;
	MainActivity act;
	int flag=0;
	int check=0;
	DatabaseManagement dbms;
	public String encrptdTranMpin;
	//HomeFragment homeFrag;
	Context context;
	private static final String MY_SESSION = "my_session";
	
	boolean flg=false;
	int chekacttype=0;

	EditText txt_mobno,txt_accname;
	TextView txt_heading,txtc_mobno,txtc_accname,txtc_operator;
	ImageView img_heading;
	ImageButton btn_home,btn_back,spinner_btn;
	Button btnmobile,btnDTH,btndatacard,btnFavorites,btnAddNew,btnRemove,btn_submit,btn_confirm;
	String stringValue = "",custId="",userId="",cust_mob_no="";
	String oper = "",retVal = "",retMess="",billercode="";
	String accNumber = null;
	String operator="",category="",billercd="",consumerno="",dob="",mobno="",accounname="";
	String[] prgmNameList, prgmNameListTemp;
	RadioButton radio;
	Spinner spin_operator;
	LinearLayout rechaddmobno,confirm_layout ;
	public ArrayAdapter<String> optrArr =null;
	ArrayList<String> opertList = null;
	ArrayList<String> billercodel = null;
	private ArrayList<Accountbean> Accountbean_arr;
	protected String accStr;
	Recharge_mobile recharge_mobile;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	public Recharge_mobile(MainActivity a)
	{
		act = a;
		recharge_mobile=this;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
        recharge_mobile=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrech_mobileno);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		btn_submit=(Button)findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		spin_operator=(Spinner)findViewById(R.id.spin_operator);
		txt_heading.setText(recharge_mobile.getString(R.string.lbl_rech_Recharges_mob));
		txt_mobno=(EditText)findViewById(R.id.txt_mobno);
		txt_accname=(EditText)findViewById(R.id.txt_accname);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
		spinner_btn.setOnClickListener(this);

		btn_confirm = (Button) findViewById(R.id.btn_confirm);

		txtc_mobno = (TextView) findViewById(R.id.txtc_mobno);

		txtc_accname = (TextView)findViewById(R.id.txtc_accname);
		txtc_operator = (TextView) findViewById(R.id.txt_operator);

		btn_confirm.setOnClickListener(this);
		confirm_layout = (LinearLayout) findViewById(R.id.confirm_layout);
		rechaddmobno = (LinearLayout)findViewById(R.id.rechaddmobno);
		//act.frgIndex=92;
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
        new CallWebServiceFetchOperator().execute();
		//btnAddNew=(Button)rootView.findViewById(R.id.btnAddNew);
		//btnAddNew.setOnClickListener(this);
     	t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.spinner_btn:
			Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn",
					"DROP DOWN IMG BTN CLICKED....");
			spin_operator.performClick();
			break;
		
		case R.id.btn_submit:
			// oper = spin_operator.getSelectedItem().toString();
			accounname=txt_accname.getText().toString();
			mobno=txt_mobno.getText().toString();
			operator=spin_operator.getSelectedItem().toString();
			
			if (operator.equalsIgnoreCase("Select Operator")) {
				showAlert(getString(R.string.alert_rechargeoper1));
			} 
			else if(mobno.length()==0)
			{
				showAlert(getString(R.string.alert_rechargeoper3));
				txt_mobno.requestFocus();
			}
			else if (mobno.length() > 0 && !MBSUtils.validateMobNo(mobno)) 
			{
				// retMess = "Please Enter Valid Mobile Number.";
				retMess = getString(R.string.alert_006);
				showAlert(retMess);
				txt_mobno.requestFocus();
			}
			 else if (accounname.length()==0) 
			 {
					showAlert(getString(R.string.alert_rechargeoper2));
					txt_accname.requestFocus();
			}
			 else{
			 // ConfirmDialog confirm = new ConfirmDialog(act);
              //confirm.show();
				 
				 if(chkConnectivity()==0)
			        {	
					  rechaddmobno.setVisibility(LinearLayout.GONE);
					  confirm_layout.setVisibility(LinearLayout.VISIBLE);
					  
					  txt_heading.setText(act.getString(R.string.lbl_confirm));
					  txtc_mobno.setText(mobno);
					  txtc_accname.setText(accounname);
					  txtc_operator.setText(operator);
					  
					  
			        }
			        else
			        {
			        	showAlert(act.getString(R.string.alert_000));
			        }
				 
				 
			 }
              break;
              
		case R.id.btn_confirm:
			
			//showAlert("On click==confirm=");
			InputDialogBox inputBox = new InputDialogBox(act);
			inputBox.show();
		
		break;
		case R.id.btn_back:
			Intent in=new Intent(recharge_mobile,Recharges.class);
			startActivity(in);
			recharge_mobile.finish(); 
			/*Fragment OthrSrvcFragment = new Recharges(act);
			act.setTitle(getString(R.string.lbl_title_change_mpin));
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, OthrSrvcFragment).commit();*/
			 break;
		}
	}
		
	/*public class ConfirmDialog extends Dialog implements OnClickListener {
		   Activity activity;
		    Recharge_mobile recharge_mobile;
		    String msg, title;
		    Context appAcontext;
		    Button confirm,back;
		    String strmpin = "";
		    TextView txtLbl;
		    String Period="";
		    boolean flg;
		    // ListView mem_listView;
		    String checkString="",chkcheckbox="",checkacctype="";
		    LinearLayout layout_memberid, layout_membernm, layout_reqid, listview_layout,layout_acctype, layout_accno, layout_amount, layout_button, fd_layout,layout_jointmem;
		    TextView txt_confrech_oper, txt_confrech_mobno, txt_confrech_accnm, txt_accounttype, txt_accountno,txt_amount, lbl_amount;
		    TextView txt_duration,txt_tax,txt_inttype,txt_interest_rates,txt_principleamt;
		    JSONArray jsonArray;
		    String memberName,memid;
		    TextView txt_jointmemvalue,txt_jointmem,txt_narration;

		    public ConfirmDialog(Activity recharge_mobile)
		    {
		        super(recharge_mobile);
		        this.activity=recharge_mobile;
		    }
		
		    protected void onCreate(Bundle bdn) {
		        super.onCreate(bdn);
		        requestWindowFeature(Window.FEATURE_NO_TITLE);
		        setContentView(R.layout.confirm_recharges);
		     //   layout_memberid=(LinearLayout)findViewById(R.id.);
		        
		       
		        txt_confrech_oper=(TextView)findViewById(R.id.txt_confrech_oper);
		        txt_confrech_mobno=(TextView)findViewById(R.id.txt_confrech_mobno);
		        txt_confrech_accnm=(TextView)findViewById(R.id.txt_confrech_accnm);
		        

		        confirm=(Button)findViewById(R.id.btn_confirm);
		        confirm.setVisibility(Button.VISIBLE);
		        confirm.setOnClickListener(this);
		        back=(Button)findViewById(R.id.btncnf_back);
		        confirm.setVisibility(Button.VISIBLE);
		        confirm.setOnClickListener(this);
		        back.setOnClickListener(this);
		        txt_confrech_oper.setText(spin_operator.getSelectedItem().toString().trim());
		        txt_confrech_mobno.setText(txt_mobno.getText().toString().trim());
		        txt_confrech_accnm.setText(txt_accname.getText().toString().trim());
		        

		    }
		      

		    @Override
		    public void onClick(View v) {
		        if (v.getId() == R.id.btn_confirm)
		        {
		            
		        	InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();

		         // new CallWebServiceAddPayee().execute();
		          

		            this.dismiss();
		        }
		        else if(v.getId()== R.id.btncnf_back)
		        {
		        	 this.dismiss();
		        }

		    }// end onClick
		}// end InputDialogBox
	*/
	class CallWebServiceFetchOperator extends AsyncTask<Void, Void, Void> {
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
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY","PREPAID MOBILE");
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

            try {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_fetch_operator );
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
                if(!xml_data[0].contains("NODATA"))
                {
                JSONArray ja = new JSONArray(xml_data[0]);
                opertList = new ArrayList<String>();
                billercodel = new ArrayList<String>();
                opertList.add("Select Operator");
                billercodel.add("-1");
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
							
							Intent in=new Intent(recharge_mobile,Recharges.class);
							startActivity(in);
							recharge_mobile.finish(); 
							
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
				} else {
					// System.out.println("======== strmpin:=="+str);
					// System.out.println("======== mobPin:=="+mobPin);
					/*if (encrptdTranMpin.equals(tranPin)
							|| encrptdUranMpin.equals(tranPin))*/ 
						saveData();
						this.hide();
					/* else {
						// System.out.println("=========== inside else ==============");
						retMess = getString(R.string.alert_118);
						showAlert(retMess);// setAlert();
						this.show();
					}*/
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}

		}// end onClick
	}// end InputDialogBox
	
	public void saveData() {
		try {
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
            
                jsonObj.put("CUSTID",custId);
                jsonObj.put("CATEGORY","PREPAID MOBILE");
                jsonObj.put("IMEI",MBSUtils.getImeiNumber(act));
                jsonObj.put("BILLERCD",billercodel.get(spin_operator.getSelectedItemPosition()));
                jsonObj.put("CONSUMERNO","");
                jsonObj.put("DOB","");
                jsonObj.put("MOBNO",mobno);
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

            
            
            String decryptedCategory = xml_data[0];
			//decryptedCategory=decryptedCategory.split("SUCCESS~")[1];
			
			Log.e("decryptedCategory====", decryptedCategory);
			
			Log.e("decryptedCategory=***===", decryptedCategory);
            loadProBarObj.dismiss();
            try
            {
                //JSONObject retJson = new JSONObject(xml_data[0]);
               
                
                if(xml_data[0].contains("SUCCESS"))
                {
                	showAlert(getString(R.string.alert_payeemob_rech));
                	flg=true;
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
