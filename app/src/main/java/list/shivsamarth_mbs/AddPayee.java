package list.shivsamarth_mbs;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Enumeration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import mbLib.AuthenticatorBean;
import mbLib.BillerBean;
import mbLib.CryptoUtil;
import mbLib.CusFntTextView;
import mbLib.CustomEditText;
import mbLib.DatabaseManagement;

import mbLib.MBSUtils;
import mbLib.MyThread;
import mbLib.PaymentChannelBean;
import mbLib.PaymentMethodBean;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

public class AddPayee extends Activity implements OnClickListener 
{
	MainActivity act;
	AddPayee billObj;
	ListView biller_list;
	Button btn_submit,btn_confirm;
	LinearLayout linear_layout,confirm_layout ;
	int selected=-1;
	int saveBtnId=-1;
	int confirmBtnId=-1;
	int idNo=1;
	String str="";
	int flag=0;
	private String custId;
	String imeiNo = "",retVal="",retMess = "",mpinStr="", cat="", catcd="",custFirstNm="";
	DatabaseManagement dbms;
	public  ArrayList<BillerBean> billerBeanArray=null;
	boolean flg=false;
	private MyThread t1;
	int timeOutInSecs=300;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_ADD_PAYEE = "";
	private static String METHOD_FETCH_BILLER = "";
	private static String METHOD_FETCH_CIRCLE="";
	private static String METHOD_BILLER_DTL = "";
	public String encrptdTranMpin;
	ArrayList<CusFntTextView>  CusFntTextViewArr=null;
	ArrayList<CustomEditText>  CustomEditTextArr=null;
	CustomEditText circleEdt;
	ArrayList<Button>  buttonArr=null;
	String tv1="",tv2="",ed1="",ed2="",fromAct="",custMobNo="",retValStr="";
	ListView  lstRpt;
	Spinner spi_biller,spi_circle;
	ImageButton btn_back;
	ImageButton spinner_btn;
	CustomEditText shortNameEdt;
	ArrayList<String> billerCdarr = new ArrayList<String>();
	ArrayList<String> fieldArr = new ArrayList<String>();
	ArrayList<String> validatorArr = new ArrayList<String>();
	ArrayList<String> circleCdArr = new ArrayList<String>();
	ArrayList<String> errorMsgArr = new ArrayList<String>();
	ArrayList<AuthenticatorBean> AuthenticatorBeanArr=null;
	ArrayList<PaymentChannelBean> PaymentChannelBeanArr=null;
	ArrayList<PaymentMethodBean> PaymentMethodBeanArr=null;
	ArrayList<String> arrList = null;
	TextView txt_heading;
	ArrayList<String> circleArrList=null;
	boolean isBbpsBiller=false;
	ImageView imgView;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	public AddPayee(){}
	
	public AddPayee(MainActivity a)
	{
		System.out.println("AddPayee()");
		act = a;
		billObj=this;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		billObj=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_biller);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
        txt_heading = (TextView)findViewById(R.id.txt_heading);
        var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
        imeiNo = MBSUtils.getImeiNumber(billObj);
        btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setImageResource(R.mipmap.backover);
        btn_back.setOnClickListener(this);
        txt_heading.setText(billObj.getString(R.string.lbl_add_biller));
        
        spi_biller = (Spinner) findViewById(R.id.spi_biller);
        spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
        spinner_btn.setOnClickListener(this);
         
        Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		retValStr = c1.getString(0);
        		custId=c1.getString(2);
        		custMobNo = c1.getString(4);
            }
        }
        String[] arr = retValStr.split("~");
		// Log.e("DashboardDesignActivity","arr[0]=="+arr[0]);
		String name= arr[0].split("#")[4];
		if(name.indexOf(" ")>-1) 
			custFirstNm=name.split(" ")[0];
		else
			custFirstNm=name;
        linear_layout = (LinearLayout)findViewById(R.id.main_layout);
        confirm_layout = (LinearLayout)findViewById(R.id.confirm_layout);
        
        btn_submit=new Button(billObj);
        btn_submit.setOnClickListener(this);
    	btn_confirm=new Button(billObj);
    	btn_confirm.setOnClickListener(this);
    	spi_circle=new Spinner(billObj);
    	circleEdt = new CustomEditText(billObj);
    	imgView=new ImageView(billObj);
    	Intent getdata=getIntent();
    	Bundle bundle = getdata.getExtras();
    	
		if (bundle != null) 
		{
			catcd = bundle.getString("CATEGORY");
			fromAct = bundle.getString("FROMACT");
		}
		
		if(chkConnectivity()==0)
		{	
			Log.e("CallWebServiceFetchCircle***","......");
		    new CallWebServiceFetchBiller().execute();
		}
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
    }
	
	class CallWebServiceFetchBiller extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
			LoadProgressBar loadProBarObj = new LoadProgressBar(act);
			//String[] xmlTags = { "CUSTID", "ACCNO", "IMEINO" };
			 String[] xmlTags = {"PARAMS"};
		     String[] valuesToEncrypt = new String[1];
		     JSONObject jsonObj = new JSONObject();

			 String generatedXML = "";

			@Override
			protected void onPreExecute() 
			{
				try
				{
					flg=false;
					loadProBarObj.show();
					jsonObj.put("CUSTID", custId);
		            jsonObj.put("CATEGORY", catcd);
		            jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(act));
		            jsonObj.put("instituteCode", act.getString(R.string.lbl_institute_code));
		            //jsonObj.put("biller_location", "Maharashtra");
		            Log.e("CATEGORY","=catcd in preExcute="+cat);
				}
				catch (JSONException je) 
				{
		            je.printStackTrace();
		        }
			    valuesToEncrypt[0] = jsonObj.toString();
				generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
				System.out.println("&&&&&&&&&& generatedXML " + generatedXML);

			};

			@Override
			protected Void doInBackground(Void... arg0) {
				System.out.println("============= inside doInBackground =================");
				NAMESPACE = getString(R.string.billdesk_namespace);
				URL = getString(R.string.billdesk_url);
				SOAP_ACTION = getString(R.string.billdesk_soap_action);
				METHOD_FETCH_BILLER="FetchBillers";
				SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_BILLER);

				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						65000);
				System.out
						.println("============= inside doInBackground 2 =================");
				try {

					//Log.i("FdRdAccountDetail   ", "111");
					androidHttpTransport.call(SOAP_ACTION, envelope);
					//Log.i("FdRdAccountDetail   ", "222");
					System.out.println(envelope.bodyIn.toString());
					//Log.i("FdRdAccountDetail   ", "333");
					retval = envelope.bodyIn.toString().trim();
					//Log.e("FdRdAccountDetail", retVal);
					//Log.i("FdRdAccountDetail   retval", retval);
					System.out.println("FdRdAccountDetail    retval-----"+ retval);
					// pb_wait.setVisibility(ProgressBar.INVISIBLE);
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					retval = retval.substring(pos + 1, retval.length() - 3);
					System.out
							.println("FdRdAccountDetail    retval AFTER SUBSTR-----"
									+ retval);

				} catch (Exception e) {
					e.printStackTrace();
					//Log.e("FdRdAccountDetail", retVal);
					System.out.println("FdRdAccountDetail   Exception" + e);
				}
				return null;
			}// end doInBackground

			protected void onPostExecute(final Void result) 
			{
				String[] xmlTags = { "PARAMS" };
				String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
				String decryptedRetVal = xml_data[0];
				billerBeanArray=new ArrayList<BillerBean>();
				BillerBean Beanobj= new BillerBean();
				Beanobj.setField("-1");
				Beanobj.setIsCommon("-1");
				Beanobj.setIsMandatory("-1");
				Beanobj.setBiller("-1");
				Beanobj.setLabel("-1");
				String[] bil={};
				billerBeanArray.add(Beanobj);
				try
				{	
					Log.e("decryptedRetValAcc", decryptedRetVal);
					
					if (decryptedRetVal.indexOf("FAILED") > -1) 
					{
						loadProBarObj.dismiss();
						flg=true;
						showAlert(getString(R.string.alert_nobiller));
					}	
					else if (decryptedRetVal.indexOf("NODATA") > -1) 
					{
						loadProBarObj.dismiss();
						flg=true;
						showAlert(getString(R.string.alert_nobiller));
					}
					else 
					{
						
						JSONObject jsonObj=new JSONObject(decryptedRetVal);//.split("SUCCESS~")[1];
						Log.e("decryptedRet",jsonObj.toString());
						if(jsonObj.getString("RESPCODE").equalsIgnoreCase("0"))
						{
							JSONArray jar=new JSONArray(jsonObj.getString("RETVAL"));
							arrList = new ArrayList<String>();
							arrList.add("Select");
							billerCdarr.add("-1");
							for(int i=0;i<jar.length();i++)
							{	
								JSONObject Obj=jar.getJSONObject(i);
								//if(catcd.equalsIgnoreCase(Obj.getString("biller_category")))
								{	
									BillerBean billerObj=new BillerBean();
									billerObj.setBillercd(Obj.getString("billerid"));
									billerObj.setBiller(Obj.getString("biller_name"));
									billerObj.setBillertype(Obj.getString("biller_type"));
									
									arrList.add(Obj.getString("biller_name"));
									billerCdarr.add(Obj.getString("billerid"));
									
									billerBeanArray.add(Beanobj);
								}	
							}							
						}
						else if(jsonObj.getString("RESPCODE").equalsIgnoreCase("1"))
						{
							loadProBarObj.dismiss();
							flg=true;
							showAlert(getString(R.string.alert_server_fail));
						}
						else
						{
							loadProBarObj.dismiss();
							JSONObject rtnObj=new JSONObject(jsonObj.getString("RETVAL"));
							flg=true;
							showAlert(rtnObj.getString("message"));
						}
						
						
						Log.e("arrList.size()","arrList.size()"+arrList.size());
					
						if(arrList.size()>1)
						{
							String[] bilArr = new String[arrList.size()];
							bilArr = arrList.toArray(bilArr);
							Log.e("bilArr","bilArr"+bilArr);
							ArrayAdapter<String> biller = new ArrayAdapter<String>(act,R.layout.spinner_item, bilArr);
							biller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spi_biller.setAdapter(biller);
							loadProBarObj.dismiss();
							spi_biller.setOnItemSelectedListener(new OnItemSelectedListener() 
							{ 
								@Override
								public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
								{
									str = spi_biller.getItemAtPosition(spi_biller.getSelectedItemPosition()).toString();
								
									Log.e("str===selected","str========="+str);
									//String pos=billercd[0];
									if(CusFntTextViewArr!=null)
									{
										for(int i=0; i<CusFntTextViewArr.size();i++)
											linear_layout.removeView(CusFntTextViewArr.get(i));
									}
									
									if(CustomEditTextArr!=null)
									{
										for(int i=0; i<CustomEditTextArr.size();i++)
											linear_layout.removeView(CustomEditTextArr.get(i));
									}
									
									if(spi_circle!=null)
										linear_layout.removeView(spi_circle);
									
									if(btn_submit!=null)
										linear_layout.removeView(btn_submit);
									
									CusFntTextViewArr = new ArrayList<CusFntTextView>();
									CustomEditTextArr = new ArrayList<CustomEditText>();
									fieldArr = new ArrayList<String>();
									validatorArr = new ArrayList<String>();
									errorMsgArr = new ArrayList<String>();
									if(!str.equalsIgnoreCase("Select"))
									{
										str = spi_biller.getItemAtPosition(spi_biller.getSelectedItemPosition()).toString();
										
										Log.e("str===selected","str========="+str);
										//String pos=billercd[0];
										if(CusFntTextViewArr!=null)
										{
											for(int i=0; i<CusFntTextViewArr.size();i++)
												linear_layout.removeView(CusFntTextViewArr.get(i));
										}
										
										if(CustomEditTextArr!=null)
										{
											for(int i=0; i<CustomEditTextArr.size();i++)
												linear_layout.removeView(CustomEditTextArr.get(i));
										}
										
										if(spi_circle!=null)
											linear_layout.removeView(spi_circle);
										
										if(btn_submit!=null)
											linear_layout.removeView(btn_submit);
										
										CusFntTextViewArr = new ArrayList<CusFntTextView>();
										CustomEditTextArr = new ArrayList<CustomEditText>();
										fieldArr = new ArrayList<String>();
										validatorArr = new ArrayList<String>();
										errorMsgArr = new ArrayList<String>();
										if(!str.equalsIgnoreCase("Select"))
										{
											if(chkConnectivity()==0)
										    {
												new CallWebServiceFetchBillerDtl().execute();
											}	
										}
									}
								}// end onItemSelected

								@Override
								public void onNothingSelected(AdapterView<?> arg0) {
								}
							});
						}
						
					}
				}
				catch (Exception je) {
		            je.printStackTrace();
		        }
			}// end onPostExecute
	}// end CallWebServiceFetchBiller
	
	class CallWebServiceFetchBillerDtl extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
			LoadProgressBar loadProBarObj = new LoadProgressBar(act);
			//String[] xmlTags = { "CUSTID", "ACCNO", "IMEINO" };
			 String[] xmlTags = {"PARAMS"};
		     String[] valuesToEncrypt = new String[1];
		     JSONObject jsonObj = new JSONObject();

			 String generatedXML = "";

			@Override
			protected void onPreExecute() 
			{
				try
				{
					flg=false;
					loadProBarObj.show();
					jsonObj.put("CUSTID", custId);
		            jsonObj.put("CATEGORY", catcd);
		            jsonObj.put("BILLERID", billerCdarr.get(spi_biller.getSelectedItemPosition()));
		            jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(act));
		            jsonObj.put("instituteCode", act.getString(R.string.lbl_institute_code));
		            Log.e("CATEGORY","=catcd in preExcute="+cat);
				}
				catch (JSONException je) 
				{
		            je.printStackTrace();
		        }
			    valuesToEncrypt[0] = jsonObj.toString();
				generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
				System.out.println("&&&&&&&&&& generatedXML " + generatedXML);

			};

			@Override
			protected Void doInBackground(Void... arg0) {
				System.out
						.println("============= inside doInBackground =================");
				NAMESPACE = getString(R.string.billdesk_namespace);
				URL = getString(R.string.billdesk_url);
				SOAP_ACTION = getString(R.string.billdesk_soap_action);
				METHOD_BILLER_DTL="FetchBillerDtl";
				SoapObject request = new SoapObject(NAMESPACE, METHOD_BILLER_DTL);

				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				System.out
						.println("============= inside doInBackground 2 =================");
				try {

					//Log.i("FdRdAccountDetail   ", "111");
					androidHttpTransport.call(SOAP_ACTION, envelope);
					//Log.i("FdRdAccountDetail   ", "222");
					System.out.println(envelope.bodyIn.toString());
					//Log.i("FdRdAccountDetail   ", "333");
					retval = envelope.bodyIn.toString().trim();
					//Log.e("FdRdAccountDetail", retVal);
					//Log.i("FdRdAccountDetail   retval", retval);
					System.out.println("FdRdAccountDetail    retval-----"+ retval);
					// pb_wait.setVisibility(ProgressBar.INVISIBLE);
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					retval = retval.substring(pos + 1, retval.length() - 3);
					System.out
							.println("FdRdAccountDetail    retval AFTER SUBSTR-----"
									+ retval);

				} catch (Exception e) {
					e.printStackTrace();
					//Log.e("FdRdAccountDetail", retVal);
					System.out.println("FdRdAccountDetail   Exception" + e);
				}
				return null;
			}// end doInBackground

			protected void onPostExecute(final Void result) 
			{
				loadProBarObj.dismiss();
				
				String[] xmlTags = { "PARAMS" };
				String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
				String decryptedRetVal=xml_data[0];
				Log.e("ADDPAYEE","decryptedRetVal====="+decryptedRetVal);
				//decryptedRetVal = "{  \"RESPCODE\":\"0\",\"billerid\": \"RELENG\",  \"objectid\": \"biller\",  \"sourceid\": \"abcd\",  \"biller_legal_name\": \"Reliance Energy Limited\",  \"biller_name\": \"Reliance Energy\",  \"biller_location\": \"India, Maharashtra\",  \"biller_location_desc\": \"India (except J&K)\",  \"biller_category\": \"Electricity\",  \"biller_reg_address\": \"Off C.S.T. Road, Kalina, Santacruz (E)\",  \"biller_reg_city\": \"Mumbai\",  \"biller_reg_pin\": \"400001\",  \"biller_reg_state\": \"Maharashtra\",  \"biller_reg_country\": \"India\",  \"isbillerbbps\": \"Y\",  \"currency\": \"356\",  \"biller_type\": \"Biller\",  \"biller_mode\": \"Online\",  \"allowed_payment_methods\": [    {      \"payment_method\": \"BankAccount\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"Y\"    },    {      \"payment_method\": \"DebitCard\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"Y\"    },    {      \"payment_method\": \"CreditCard\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"Y\"    },    {      \"payment_method\": \"PrepaidCard\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"N\"    },    {      \"payment_method\": \"IMPS\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"N\"    },    {      \"payment_method\": \"Cash\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"N\"    },    {      \"payment_method\": \"UPI\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"N\"    },    {      \"payment_method\": \"Wallet\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"N\"    },    {      \"payment_method\": \"NEFT\",      \"min_limit\": \"10.00\",      \"max_imit\": \"99999.00\",      \"autopay_allowed\": \"N\"    }  ],  \"payment_channels\": [    {      \"payment_channel\": \"Internet\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"InternetBanking\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"Mobile\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"MobileBanking\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"POS\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"MPOS\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"ATM\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"BankBranch\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"Kiosk\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"Agent\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    },    {      \"payment_channel\": \"BusinessCorrespondent\",      \"min_limit\": \"10.00\",      \"max_limit\": \"200000.00\"    }  ],  \"biller_effective_from\": \"27-12-2016\",  \"biller_effective_to\": \"01-01-2017\",  \"biller_status\": \"ACTIVE\",  \"temp_deactivation_start\": \"01-02-2017\",  \"temp_deactivation_end\": \"01-03-2017\",  \"biller_created_date\": \"01-12-2016 22:26:14\",  \"biller_lastmodified_date\": \"01-01-2016 22:26:14\",  \"authenticators\": [    {      \"seq\": \"1\",      \"parameter_name\": \"Consumer No\",      \"data_type\": \"NUMERIC\",      \"optional\": false,      \"regex\": \"^[0-9](10)\",      \"error_message\": \"Please enter a valid Consumer No\",      \"encryption_required\": \"Y\"    },    {      \"seq\": \"2\",      \"parameter_name\": \"BU\",      \"data_type\": \"LIST\",      \"optional\": false,      \"regex\": \"^[0-9](10)\",      \"error_message\": \"Please enter a valid BU\",      \"list_of_values\": [        {          \"name\": \"Panvel\",          \"value\": \"0311\"        },        {          \"name\": \"Vasai\",          \"value\": \"0019\"        }      ]    }  ],    \"partial_pay\": \"Y\",  \"pay_after_duedate\": \"Y\",  \"online_validation\": \"Y\",  \"customer_conv_fee\": [    {      \"cou_conv_fee\": \"0.25\",      \"cou_conv_fee_type\": \"percentage\",      \"min_cou_conv_fee\": \"5.00\",      \"max_cou_conv_fee\": \"20.00\",      \"bou_conv_fee\": \"1.25\",      \"bou_conv_fee_type\": \"percentage\",      \"min_bou_conv_fee\": \"5.00\",      \"max_bou_conv_fee\": \"20.00\",      \"payment_channel\": \"Internet\",      \"payment_method\": \"BankAccount\",      \"amount_slab_start\": \"0.00\",      \"amount_slab_end\": \"999999.00\"    },    {      \"cou_conv_fee\": \"15.00\",      \"cou_conv_fee_type\": \"fixed\",      \"payment_channel\": \"Mobile\",      \"payment_method\": \"DebitCard\",      \"amount_slab_start\": \"0.00\",      \"amount_slab_end\": \"2000.00\"    },    {      \"cou_conv_fee\": \"25.00\",      \"cou_conv_fee_type\": \"fixed\",      \"payment_channel\": \"Mobile\",      \"payment_method\": \"DebitCard\",      \"amount_slab_start\": \"2000.00\",      \"amount_slab_end\": \"999999.00\"    }  ],  \"paymentamount_validation\": \"Y\",  \"additional_validation_details\": [    {      \"parameter_name\": \"Recharge Type\",      \"data_type\": \"LIST\",      \"optional\": false,      \"regex\": \"^[0-9](1)\",      \"error_message\": \"Please select the type of recharge\",      \"list_of_values\": [        {          \"name\": \"TopUp\",          \"value\": \"1\"        },        {          \"name\": \"Special Recharge\",          \"value\": \"3\"        }      ]    },    {      \"parameter_name\": \"Recharge Planid\",      \"data_type\": \"ALPHANUMERIC\",      \"optional\": false,      \"regex\": \"^[0-9a-zA-Z]{1,20}$\",      \"error_message\": \"Please select the recharge planid\"    }  ]}";//xml_data[0];
				/*if(catcd.equalsIgnoreCase("PREPAID MOBILE"))
				{
					decryptedRetVal = "SUCCESS~AIRCELPRE#AIRCEL PREPAID~AIRTELPRE#AIRTEL PREPAID~BSNLPRE#BSNL  PREPAID ~DOCOMOPRE#TATA DOCOMO GSM~IDEAPRE#IDEA PREPAID~MTNLDELPRE#MTNL DELHI PREPAID~MTNLMUMPRE#MTNL MUMBAI PREPAID~RIMGSMPRE#RELIANCE GSM PREPAID~UNINORPRE#TELENOR PREPAID~VODAFONPRE#VODAFONE PREPAID~JIOPRE#JIO PREPAID~VIDEOCNPRE#VIDEOCON PREPAID~MTSPRE#MTS PREPAID~TTSLPRE#TATA INDICOM PREPAID@MOBILE_NO#Y#Y#Mobile No#^[0-9]{10,12}$~ACC_NAME#Y#N#Account Name#~CIRCLE#Y#Y#Circle#^[0-9]{1,2}$";//xml_data[0];
				}
				else if(catcd.equalsIgnoreCase("PREPAID DTH"))
				{
					decryptedRetVal = "SUCCESS~AIRTELDTH#AIRTEL DTH~BIGTVDTH#BIG TV DTH~DISHTVDTH#DISH TV DTH~SUNTVDTH#SUN TV DTH~TATASKYDTH#TATASKY DTH~VIDEOCNDTH#VIDEOCON DTH@CUST_ID#N#Y#AIRTEL DTH#Customer Id#~CUST_ID#N#Y#BIG TV DTH#Smart Card Number#~CUST_ID#N#Y#DISH TV DTH#Viewing Card Number#~CUST_ID#N#Y#SUN TV DTH#Smart Card Number#~CUST_ID#N#Y#TATASKY DTH#Subscriber Number#~CUST_ID#N#Y#VIDEOCON DTH#Subscriber Id#~ACC_NAME#Y#N#Account Name#";//xml_data[0];
				}*/
				billerBeanArray=new ArrayList<BillerBean>();
				BillerBean Beanobj= new BillerBean();
				Beanobj.setField("-1");
				Beanobj.setIsCommon("-1");
				Beanobj.setIsMandatory("-1");
				Beanobj.setBiller("-1");
				Beanobj.setLabel("-1");
				String[] bil={};
				billerBeanArray.add(Beanobj);
				try
				{	
					Log.e("decryptedRetValAcc", decryptedRetVal);
					
					if (decryptedRetVal.indexOf("FAILED") > -1) 
					{
						flg=true;
						showAlert(getString(R.string.alert_nobiller));
					}	
					else if (decryptedRetVal.indexOf("NODATA") > -1) 
					{
						flg=true;
						showAlert(getString(R.string.alert_nobiller));
					}
					else 
					{
						JSONObject jsonObj=new JSONObject(decryptedRetVal);
						
						Log.e("decryptedRet",jsonObj.toString());
						if(jsonObj.getString("RESPCODE").equalsIgnoreCase("0"))
						{		
							JSONArray addtnlJsonArr=null,pymntMthdJsonArr=null,pymntChnnlJsonArr=null,authJsonArr=null;
							JSONObject retValObj=new JSONObject(jsonObj.getString("RETVAL"));
							BillerBean billerObj=new BillerBean();
							billerObj.setBillercd(retValObj.getString("billerid"));
							billerObj.setBiller(retValObj.getString("biller_name"));
							billerObj.setBillertype(retValObj.getString("biller_type"));
							if(retValObj.getString("isbillerbbps").equalsIgnoreCase("Y"))
								isBbpsBiller=true;
							else
								isBbpsBiller=false;
							if(retValObj.has("allowed_payment_methods"))
								pymntMthdJsonArr=retValObj.getJSONArray("allowed_payment_methods");
							if(retValObj.has("payment_channels"))
								pymntChnnlJsonArr=retValObj.getJSONArray("payment_channels"); 
							if(retValObj.has("authenticators"))
								authJsonArr=retValObj.getJSONArray("authenticators");
							if(retValObj.has("additional_validation_details"))
								addtnlJsonArr=retValObj.getJSONArray("additional_validation_details");
							
							Log.e("AddPayeee","authJsonArr=="+authJsonArr.length());
							AuthenticatorBeanArr=new ArrayList<AuthenticatorBean>();
							
							if(authJsonArr!=null)
							{	
								for(int i=0;i<authJsonArr.length();i++)
								{
									AuthenticatorBean authObj=new AuthenticatorBean();
									
									JSONObject jObj=authJsonArr.getJSONObject(i);
									Log.e("ADDPAYEE","auth json=="+jObj.toString());
									if(jObj.has("data_type"))
										authObj.setDataType(jObj.getString("data_type"));
									
									if(jObj.has("encryption_required"))
										authObj.setEncryReq(jObj.getString("encryption_required"));
									
									if(jObj.has("error_message"))
										authObj.setErrMsg(jObj.getString("error_message"));
									
									if(jObj.has("optional"))
										authObj.setOptional(jObj.getString("optional"));
									
									if(jObj.has("parameter_name"))
										authObj.setParaName(jObj.getString("parameter_name"));
									
									if(jObj.has("regex"))
										authObj.setRegex(jObj.getString("regex"));
									
									if(jObj.has("seq"))
										authObj.setSeq(jObj.getString("seq"));
									
									Log.e("AddPayeee","getDataType=="+authObj.getDataType());
									Log.e("AddPayeee","getEncryReq=="+authObj.getEncryReq());
									Log.e("AddPayeee","getErrMsg=="+authObj.getErrMsg());
									Log.e("AddPayeee","getOptional=="+authObj.getOptional());
									Log.e("AddPayeee","getParaName=="+authObj.getParaName());
									Log.e("AddPayeee","getRegex=="+authObj.getRegex());
									Log.e("AddPayeee","getSeq=="+authObj.getSeq());
									
									AuthenticatorBeanArr.add(authObj);
								}
								billerObj.setAuthenticatorBeanArr(AuthenticatorBeanArr);
							}
							
							Log.e("AddPayeee","AuthenticatorBeanArr=="+AuthenticatorBeanArr.size());
							/*if(addtnlJsonArr!=null)
							{	
								for(int i=0;i<addtnlJsonArr.length();i++)
								{
									if(AuthenticatorBeanArr==null)
										AuthenticatorBeanArr=new ArrayList<AuthenticatorBean>();
									AuthenticatorBean authObj=new AuthenticatorBean();
									
									JSONObject jObj=addtnlJsonArr.getJSONObject(i);
									
									if(jObj.has("data_type"))
										authObj.setDataType(jObj.getString("data_type"));
									
									if(jObj.has("encryption_required"))
										authObj.setEncryReq(jObj.getString("encryption_required"));
									
									if(jObj.has("error_message"))
										authObj.setErrMsg(jObj.getString("error_message"));
									
									if(jObj.has("optional"))
										authObj.setOptional(jObj.getString("optional"));
									
									if(jObj.has("parameter_name"))
										authObj.setParaName(jObj.getString("parameter_name"));
									
									if(jObj.has("regex"))
										authObj.setRegex(jObj.getString("regex"));
									
									if(jObj.has("seq"))
										authObj.setSeq(jObj.getString("seq"));
									
									AuthenticatorBeanArr.add(authObj);
								}
								billerObj.setAuthenticatorBeanArr(AuthenticatorBeanArr);
							}*/
							
							if(pymntChnnlJsonArr!=null)
							{	
								for(int i=0;i<pymntChnnlJsonArr.length();i++)
								{
									if(PaymentChannelBeanArr==null)
										PaymentChannelBeanArr=new ArrayList<PaymentChannelBean>();
									PaymentChannelBean pymntChnlObj=new PaymentChannelBean();
									
									JSONObject jObj=pymntChnnlJsonArr.getJSONObject(i);
									
									if(jObj.has("payment_channel"))
										pymntChnlObj.setPaymentChannel(jObj.getString("payment_channel"));
									
									if(jObj.has("max_limit"))
										pymntChnlObj.setMaxLimit(jObj.getString("max_limit"));
									
									if(jObj.has("min_limit"))
										pymntChnlObj.setMinLimit(jObj.getString("min_limit"));
									
									PaymentChannelBeanArr.add(pymntChnlObj);
								}
								billerObj.setPaymentChannelBeanArr(PaymentChannelBeanArr);
							}
							
							if(pymntMthdJsonArr!=null)
							{	
								for(int i=0;i<pymntMthdJsonArr.length();i++)
								{
									if(PaymentMethodBeanArr==null)
										PaymentMethodBeanArr=new ArrayList<PaymentMethodBean>();
									PaymentMethodBean pymntMthdObj=new PaymentMethodBean();
									
									JSONObject jObj=pymntMthdJsonArr.getJSONObject(i);
									
									if(jObj.has("payment_method"))
										pymntMthdObj.setPaymentMethod(jObj.getString("payment_method"));
									
									if(jObj.has("max_limit"))
										pymntMthdObj.setMaxLimit(jObj.getString("max_limit"));
									
									if(jObj.has("min_limit"))
										pymntMthdObj.setMinLimit(jObj.getString("min_limit"));
									
									if(jObj.has("autopay_allowed"))
										pymntMthdObj.setAutopayAllowed(jObj.getString("autopay_allowed"));
									
									PaymentMethodBeanArr.add(pymntMthdObj);
								}
								billerObj.setPaymentMethodBeanArr(PaymentMethodBeanArr);
							}	
							
							billerBeanArray.add(Beanobj);
						}
						else if(jsonObj.getString("RESPCODE").equalsIgnoreCase("1"))
						{
							flg=true;
							showAlert(getString(R.string.alert_server_fail));
						}
						else
						{
							flg=true;
							JSONObject rtnObj=new JSONObject(jsonObj.getString("RETVAL"));
							showAlert(rtnObj.getString("message"));
						}
						
						if(CusFntTextViewArr!=null)
						{
							for(int i=0; i<CusFntTextViewArr.size();i++)
								linear_layout.removeView(CusFntTextViewArr.get(i));
						}
						
						if(CustomEditTextArr!=null)
						{
							for(int i=0; i<CustomEditTextArr.size();i++)
								linear_layout.removeView(CustomEditTextArr.get(i));
						}
						
						if(spi_circle!=null)
							linear_layout.removeView(spi_circle);
						
						if(btn_submit!=null)
							linear_layout.removeView(btn_submit);
						
						Log.e("ADDPAYEE","imgView====="+imgView);
						if(imgView!=null)
							linear_layout.removeView(imgView);
						
						CusFntTextViewArr = new ArrayList<CusFntTextView>();
						CustomEditTextArr = new ArrayList<CustomEditText>();
						fieldArr = new ArrayList<String>();
						validatorArr = new ArrayList<String>();
						errorMsgArr = new ArrayList<String>();
						Log.e("ADDPAYEE","auth size=="+AuthenticatorBeanArr.size());
						//ArrayList<AuthenticatorBean> authBeanArray=billerObj.getAuthenticatorBeanArr();
						for(int i=0;i<AuthenticatorBeanArr.size();i++)
						{
							AuthenticatorBean beanObj=AuthenticatorBeanArr.get(i);
							
							CusFntTextView valueTV = new CusFntTextView(act);
			                valueTV.setText(beanObj.getParaName()+"*");
			                fieldArr.add(beanObj.getParaName());
			                valueTV.setId(idNo);
			                idNo++;
			                valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                linear_layout.addView(valueTV);
			                CusFntTextViewArr.add(valueTV);
			                validatorArr.add(beanObj.getRegex());
			                errorMsgArr.add(beanObj.getErrMsg());
			                if(beanObj.getDataType().equalsIgnoreCase("List"))
			                {
			                	Spinner spnr=new Spinner(act);
			                	spnr.setBackgroundResource(R.mipmap.rounded_corner_spinner);
			                	spnr.setId(idNo);
			                	idNo++;
			                	
			                	LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
						        params.setMargins(0,0,0,15);
						        spnr.setLayoutParams(params);
			                	linear_layout.addView(spnr);
			                	
			                	if(beanObj.getParaName().indexOf("Circle")>-1)
			                	{
			                		String[] circleArr = new String[circleArrList.size()];
			    					circleArr = circleArrList.toArray(circleArr);
			    					Log.e("circleArr","circleArr"+circleArr);
			    					ArrayAdapter<String> biller = new ArrayAdapter<String>(act,R.layout.spinner_item, circleArr);
			    					biller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			    					spnr.setAdapter(biller);
			    					spnr.setOnItemSelectedListener(new OnItemSelectedListener() 
			    					{
			    						@Override
			    						public void onItemSelected(AdapterView<?> parent,View view, int position, long id) 
			    						{
			    							if(position!=0)
			    							{
			    								if(circleCdArr.get(position).length()==1)
			    									circleEdt.setText("0"+circleCdArr.get(position));//+"--"+spi_circle.getSelectedItem().toString());
			    								else
			    									circleEdt.setText(circleCdArr.get(position));
			    							}
			    							else
			    								circleEdt.setText("");
			    						}
			    						
			    						@Override
			    						public void onNothingSelected(AdapterView<?> parent) 
			    						{
			    							
			    						}
									});
			    					CustomEditTextArr.add(circleEdt);
			                	}
			                	/*CustomEditText valueEDT = new CustomEditText(billObj);
				                valueEDT.setId(idNo);
				                idNo++;
				                CustomEditTextArr.add(valueEDT);*/
			                }
			                else
			                {	
				                CustomEditText valueEDT = new CustomEditText(act);
				                valueEDT.setId(idNo);
				                if(beanObj.getDataType().equalsIgnoreCase("NUMERIC"))
				                	valueEDT.setInputType(InputType.TYPE_CLASS_NUMBER);
				                else
				                	valueEDT.setInputType(InputType.TYPE_CLASS_TEXT);
				                idNo++;
				                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
						        //params.setMargins(left, top, right, bottom);
						        params.setMargins(0,0,0,15);
						        valueEDT.setLayoutParams(params);
						        
				                //valueEDT.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				                linear_layout.addView(valueEDT);
				                CustomEditTextArr.add(valueEDT);
			                }    
						}
						
						CusFntTextView valueTV = new CusFntTextView(act);
		                valueTV.setText("Short Name");
		                fieldArr.add("short_name");
		                valueTV.setId(idNo);
		                idNo++;
		                valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		                linear_layout.addView(valueTV);
		                CusFntTextViewArr.add(valueTV);
		                validatorArr.add("^[a-zA-Z0-9]*$");
		                errorMsgArr.add("Enter Valid Short Name (without spaces and upto 50 characters)");
		                
		                CustomEditText valueEDT = new CustomEditText(act);
		                valueEDT.setId(idNo);
		                valueEDT.setInputType(InputType.TYPE_CLASS_TEXT);
		                shortNameEdt=valueEDT;
		                idNo++;
		                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
				        params.setMargins(0,0,0,15);
				        valueEDT.setLayoutParams(params);
				        
		                linear_layout.addView(valueEDT);
		                CustomEditTextArr.add(valueEDT);
						
		                if(isBbpsBiller)
		                {
		                	
		                	imgView.setId(idNo);
		                	idNo++;
		                	imgView.setBackgroundResource(R.mipmap.bbps_logo);
		                	LinearLayout.LayoutParams imgParams=new LinearLayout.LayoutParams(160,80);
		                	imgParams.gravity=Gravity.CENTER;
		                	imgParams.setMargins(10,0,10,0);
		                	imgView.setLayoutParams(imgParams);
		                	linear_layout.addView(imgView);
		                }
		              
				        btn_submit.setText("Submit");
				        btn_submit.setId(idNo);
				        saveBtnId=idNo;
				        //btn_submit.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				        params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		 		        params.setMargins(0,15,0,5);
		 		        btn_submit.setLayoutParams(params);
				        linear_layout.addView(btn_submit);
						
					}
				}
				catch (Exception je) {
		            je.printStackTrace();
		        }
			}// end onPostExecute
	}// end CallWebServiceFetchBillerDtl
	
	class CallWebServiceFetchCircle extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String[] xmlTags = {"PARAMS"};
		String[] valuesToEncrypt = new String[1];
	    JSONObject jsonObj = new JSONObject();

		String generatedXML = "";

		@Override
		protected void onPreExecute() 
		{
			try
			{
				loadProBarObj.show();
				jsonObj.put("CUSTID", custId);
	            jsonObj.put("CATEGORY", catcd);
	            jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(act));
	            Log.e("CATEGORY","=catcd in preExcute="+cat);
			}
			catch (JSONException je) 
			{
	            je.printStackTrace();
	        }
		    valuesToEncrypt[0] = jsonObj.toString();
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);

		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			System.out.println("============= inside doInBackground =================");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			METHOD_FETCH_CIRCLE="fetchCircle";
			SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_CIRCLE);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,	15000);
			System.out.println("============= inside doInBackground 2 =================");
			try 
			{
				androidHttpTransport.call(SOAP_ACTION, envelope);
				System.out.println(envelope.bodyIn.toString());
				retval = envelope.bodyIn.toString().trim();
				System.out.println("FdRdAccountDetail    retval-----"+ retval);
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				retval = retval.substring(pos + 1, retval.length() - 3);
				System.out.println("FdRdAccountDetail    retval AFTER SUBSTR-----"+ retval);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("FdRdAccountDetail   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			
			String[] xmlTags = { "PARAMS" };
			String[] xml_data = CryptoUtil.readXML(retval,xmlTags );
			String decryptedRetVal = xml_data[0];
			
			try
			{	
				Log.e("decryptedRetValAcc", decryptedRetVal);
				
				if (decryptedRetVal.indexOf("FAILED") > -1) 
				{
					showAlert(getString(R.string.alert_nobiller));
				}	
				else if (decryptedRetVal.indexOf("NODATA") > -1) 
				{
					showAlert(getString(R.string.alert_nobiller));
				}
				else 
				{
					
					String decryptedRet=decryptedRetVal.split("SUCCESS~")[1];
				
					Log.e("decryptedRet",decryptedRet);
					JSONArray jArr=new JSONArray(decryptedRet);
					ArrayList<String> circleArrList = new ArrayList<String>();
					circleArrList.add("Select");
					circleCdArr.add("-1");
					
					for(int i=0;i<jArr.length();i++)
					{
						JSONObject jObj=jArr.getJSONObject(i);
						circleArrList.add(jObj.getString("CIRCLE_NM"));
						circleCdArr.add(jObj.getString("CIRCLE_ID"));
					}
										
					String[] circleArr = new String[circleArrList.size()];
					circleArr = circleArrList.toArray(circleArr);
					Log.e("circleArr","circleArr"+circleArr);
					ArrayAdapter<String> biller = new ArrayAdapter<String>(act,R.layout.spinner_item, circleArr);
					biller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spi_circle.setAdapter(biller);
					
					spi_circle.setOnItemSelectedListener(new OnItemSelectedListener() 
					{ 
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
						{
							if(!spi_biller.getSelectedItem().toString().equalsIgnoreCase("Select"))
								circleEdt.setText(circleCdArr.get(spi_circle.getSelectedItemPosition()));//+"--"+spi_circle.getSelectedItem().toString());
							else
								circleEdt.setText("");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) 
						{
							
						}
					});	
				}
			}
			catch (Exception je) {
	            je.printStackTrace();
	        }
		}// end onPostExecute
	}// end CallWebServiceFetchBiller
	
	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.btn_back)
		{
			Fragment fragment=null;
			if(fromAct.equalsIgnoreCase("RECHARGE"))
			{
				Bundle b1=new Bundle();				
				b1.putString("CATEGORY",catcd);
				b1.putString("FROMACT",fromAct);
				Intent in=new Intent(billObj,Recharges.class);
				in.putExtras(b1);
				startActivity(in);
				billObj.finish();
			}
			else
			{
				Intent in=new Intent(billObj,BillList.class);
				startActivity(in);
				billObj.finish();
				/*Intent in=new Intent(billObj,Recharges.class);
				startActivity(in);
				billObj.finish();*///billlist
			}
		
		}
		else if (v.getId() == R.id.spinner_btn)
		{
			Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn","DROP DOWN IMG BTN CLICKED....");
			spi_biller.performClick();
		}
		else if(v.getId()==saveBtnId)
		{
			Log.e("CusFntTextViewArr==","CusFntTextViewArr.size()=="+CusFntTextViewArr.size());
        	String[] str = new String[CusFntTextViewArr.size()];
   
        	char last = 0;
        	String  status="false";
        	for(int i=0;i<fieldArr.size();i++)
			{
        		last = CusFntTextViewArr.get(i).getText().toString().charAt(CusFntTextViewArr.get(i).getText().toString().length() - 1);
        		Log.e("LAST","LAst=="+last);
        		if(last=='*')
	            if(CustomEditTextArr.get(i).getText().toString().length() == 0)
	            {
	            	showAlert(billObj.getString(R.string.alert_mandtry_field));
	            	status="true";
	            	break;
	            }
	            else if(!validateField(validatorArr.get(i),CustomEditTextArr.get(i).getText().toString()))
	            {
	            	showAlert("Please Enter Valid "+CusFntTextViewArr.get(i).getText().toString().substring(0,CusFntTextViewArr.get(i).getText().toString().length()-1));
	            	status="true";
	            	break;
	            }
	            else if(fieldArr.get(i).equalsIgnoreCase("short_name") && CustomEditTextArr.get(i).getText().toString().length()>10)
	            {
	            	showAlert(billObj.getString(R.string.alert_long_acc_nm));
	            	status="true";
	            	break;
	            }
	            	
			}
        	if(!status.equalsIgnoreCase("true"))
            {
            	linear_layout.setVisibility(LinearLayout.GONE);
            	confirm_layout.setVisibility(LinearLayout.VISIBLE);
            	
            	txt_heading.setText(billObj.getString(R.string.lbl_confirm));            	
            	CusFntTextView title=new CusFntTextView(billObj);
            	title.setText("Please Confirm Below Details");
            	title.setTextSize(18);
            	title.setGravity(Gravity.CENTER);
            	
            	LinearLayout.LayoutParams titleParam=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            	titleParam.setMargins(0, 10, 0, 15);
            	title.setLayoutParams(titleParam);
    	        confirm_layout.addView(title);
            	
    	        for(int i=0; i < CusFntTextViewArr.size(); i++)
                {
            		LinearLayout parent = new LinearLayout(billObj);
            		LinearLayout.LayoutParams parentParam=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            		parentParam.setLayoutDirection(LinearLayout.HORIZONTAL);
            		//parentParam.setMargins(0, 0, 0, 10);
            		parent.setLayoutParams(parentParam);
            		parent.setWeightSum(2);
            		confirm_layout.addView(parent);
            		            		
            		CusFntTextView valueTV1 = new CusFntTextView(billObj);
	                valueTV1.setText(CusFntTextViewArr.get(i).getText().toString()+"  : ");
	                valueTV1.setId(idNo);
	                valueTV1.setGravity(Gravity.RIGHT);
	                valueTV1.setTextSize(14);
	                LinearLayout.LayoutParams newParam=new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
	                newParam.setMargins(0, 0, 5, 10);
	                newParam.weight=1;
	                valueTV1.setLayoutParams(newParam);
	                parent.addView(valueTV1);
	                idNo++;
	                
	                CusFntTextView valueTV2 = new CusFntTextView(billObj);
	                valueTV2.setText("  "+CustomEditTextArr.get(i).getText().toString());
	                valueTV2.setId(idNo);
	                valueTV2.setGravity(Gravity.LEFT);
	                valueTV2.setTextSize(14);
	                LinearLayout.LayoutParams newParam2=new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
	                newParam.setMargins(5, 0, 0, 10);
	                newParam2.weight=1;
	                valueTV2.setLayoutParams(newParam2);
	                parent.addView(valueTV2);
	                idNo++;
            	}
            		
    		    btn_confirm.setText("Confirm");
    		    btn_confirm.setId(idNo);
    		    confirmBtnId=idNo;
    	        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
 		        params.setMargins(0,15,0,5);
 		        btn_confirm.setLayoutParams(params);
    	        confirm_layout.addView(btn_confirm);
   	        
            }
		}
		else if(v.getId()==confirmBtnId)
		{
			//showAlert("On click==confirm=");
			InputDialogBox inputBox = new InputDialogBox(billObj);
			inputBox.show();
		}
    	
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
			try 
			{
				String str = mpin.getText().toString().trim();
				encrptdTranMpin = str;//ListEncryption.encryptData(custId + str);

				if (str.length() == 0) 
				{
					retMess = getString(R.string.alert_enterTranMpin);
					showAlert(retMess);
					this.show();
				} 
				else if (str.length() != 6) 
				{
					retMess = getString(R.string.alert_TranmipnMust6dig);
					showAlert(retMess);
					this.show();
				} 
				else 
				{
					if(chkConnectivity()==0)
			        {	
						mpinStr=encrptdTranMpin;
						Log.e("CallWebServiceAddPayee***","......");
				        new CallWebServiceAddPayee().execute();
				    }
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

	class  CallWebServiceAddPayee extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		String[] xmlTags = {"PARAMS"};
        String[] valuesToEncrypt = new String[1];
        JSONObject jsonObj = new JSONObject();
		
		String generatedXML = "";

		protected void onPreExecute() 
		{
			try
			{
				flg=false;
				loadProBarObj.show();
				Log.e("ADDPAYEE","BILLERCD=="+billerCdarr.get(spi_biller.getSelectedItemPosition()));
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("billerid", billerCdarr.get(spi_biller.getSelectedItemPosition()));
				//jsonObj.put("CATEGORY", catcd);
				jsonObj.put("TRANMPIN", mpinStr);
				jsonObj.put("instituteCode", act.getString(R.string.lbl_institute_code));
				jsonObj.put("autopay_status", "N");
				jsonObj.put("short_name", shortNameEdt.getText().toString());
				jsonObj.put("CATEGORY",catcd);
				JSONArray authJsonArr=new JSONArray();
				for(int i=0;i<AuthenticatorBeanArr.size();i++)
				{
					AuthenticatorBean beanObj=AuthenticatorBeanArr.get(i);
					if(fieldArr.get(i).equalsIgnoreCase(AuthenticatorBeanArr.get(i).getParaName()))
					{
						JSONObject nObj=new JSONObject();
						nObj.put("parameter_name", fieldArr.get(i));
						nObj.put("value", CustomEditTextArr.get(i).getText().toString());
						if(fieldArr.get(i).contains("Mobile") || fieldArr.get(i).contains("Subscriber"))
							jsonObj.put("CONSUMERNO",CustomEditTextArr.get(i).getText().toString());
						authJsonArr.put(nObj);
					}
				}
				jsonObj.put("authenticators", authJsonArr);
				Log.e("custId","--"+custId);Log.e("MBSUtils.getImeiNumber(act)","--"+MBSUtils.getImeiNumber(act));
				
				JSONObject deviceJson=new JSONObject();
				deviceJson.put("init_channel","MobileBanking");
				deviceJson.put("ip", getLocalIpAddress());	
				deviceJson.put("os", Build.VERSION.RELEASE);
				deviceJson.put("app", "Panchganga Bank");//act.getString(R.string.app_name));
				deviceJson.put("imei", MBSUtils.getImeiNumber(act));
				
				JSONObject agentJson=new JSONObject();
				agentJson.put("agentid", "BD01BD02MBB000000001");//act.getString(R.string.app_name));
				
				JSONObject metaJson=new JSONObject();
				metaJson.put("agent", agentJson);
				metaJson.put("device", deviceJson);
				
				jsonObj.put("metadata", metaJson);
				
				JSONObject custJson=new JSONObject();
				custJson.put("firstname", custFirstNm);
				custJson.put("mobile", custMobNo);
				//custJson.put("pan", "BLXPP0523J");
				//custJson.put("aadhaar", "987654321012");
				jsonObj.put("customer", custJson);
            }
			catch (JSONException je) 
			{
				je.printStackTrace();
	        }
			valuesToEncrypt[0] = jsonObj.toString();
			Log.e("ADDPAYEE","valuesToEncrypt[0]--"+jsonObj.toString());
			
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
		}

		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.billdesk_namespace);
			URL = getString(R.string.billdesk_url);
			SOAP_ACTION = getString(R.string.billdesk_soap_action);
			METHOD_ADD_PAYEE="addPayee";
			try {
				SoapObject request = new SoapObject(NAMESPACE, METHOD_ADD_PAYEE);
				request.addProperty("para_value", generatedXML);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
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
				System.out.println("CallWebServiceAddPayee   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) 
		{
			String[] xml_data = CryptoUtil.readXML(retVal,new String[] { "PARAMS" });
			String decrypted = xml_data[0];
			
			Log.e("ADDPAYEE","decrypted==="+decrypted);
			loadProBarObj.dismiss();
			if(decrypted.indexOf("FAILED")>-1)
			{
				flg=true;
				showAlert(getString(R.string.alert_failpayee));
			}
			else
			{
				try
				{
					JSONObject rtnObj=new JSONObject(decrypted);
					if(rtnObj.getString("RESPCODE").equalsIgnoreCase("0"))
					{
						flg=true;
						showAlert(act.getString(R.string.alert_addpayee));
					}
					else if(rtnObj.getString("RESPCODE").equalsIgnoreCase("1") || 
							rtnObj.getString("RESPCODE").equalsIgnoreCase("2") ||
							rtnObj.getString("RESPCODE").equalsIgnoreCase("3") ||
							rtnObj.getString("RESPCODE").equalsIgnoreCase("4"))
					{
						if(rtnObj.has("message"))
							showAlert(rtnObj.getString("message"));
						else
							showAlert(getString(R.string.alert_server_fail));
					}
					else
					{
						JSONObject rObj=new JSONObject(rtnObj.getString("RETVAL"));
						showAlert(rObj.getString("message"));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}// end onPostExecute

	}// end CallWebServiceAddPayee

	public void showAlert(String str) {
		
		ErrorDialogClass alert = new ErrorDialogClass(billObj, "" + str)
		{
			Intent in = null;
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.btn_ok:
                    	if(flg)
						{
                    		Intent in=null;
                    		if(fromAct.equalsIgnoreCase("RECHARGE"))
                    			in = new Intent(billObj, Recharges.class);
                    		else
                    			in = new Intent(billObj, BillList.class);
                 			startActivity(in);
                 			billObj.finish();
						}
                    	break;
                }
                this.dismiss();
            }
		 };
		 alert.show();
	}
	
	public int chkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) billObj.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
	public boolean validateField(String comparePattern, String compareValue)
	{
		//Toast.makeText(AddPayee.this, comparePattern+"=="+compareValue, Toast.LENGTH_LONG).show();
		Pattern pattern = Pattern.compile(comparePattern);
		Matcher matcher = pattern.matcher(compareValue);
		if (matcher.matches())
			return true;
		else
			return false;
	}
	
	@Override
	public void onBackPressed() {
	 // Simply Do noting!
	}	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		t1.sec=-1;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		t1.sec = timeOutInSecs;
		Log.e("sec11= ","sec11=="+t1.sec);
		return super.onTouchEvent(event);
	}
	
	public String getLocalIpAddress() {
		String ip="";
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                	ip = Formatter.formatIpAddress(inetAddress.hashCode());
	                    Log.e("BillPayment", "***** IP="+ ip);
	                    return ip;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("BillPayment", ex.toString());
	    }
	    return ip;
	}
}

