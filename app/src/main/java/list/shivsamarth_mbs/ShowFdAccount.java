package list.shivsamarth_mbs;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import mbLib.MyThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

public class ShowFdAccount extends Activity implements OnClickListener{
	private ListView listView1;
	
	//HomeFragment homeFrag;
	Context context;
	private static final String MY_SESSION = "my_session";
	//Editor e;
	String stringValue = "";
	String all_acnts = "", str2 = "", str = "";
	String acc_type = "SAVING_CUR";
	int chekacttype=0;
	TextView txt_heading;
	ImageView img_heading;
	ImageButton btn_home,btn_back;
	Button btn_show_details;
	//Button btn_saving_cur, btn_deposits, btn_loan;
	//ImageButton stmntbtn,imgBtnChequeRelated,imgBtnTransfer;
	//ImageButton img_btn_transfer, img_btn_mini_stmt, img_btn_chq_related;
	String acnt_inf = "";
	String accNumber = null;
	String[] prgmNameList, prgmNameListTemp;
	private ArrayList<Accountbean> Accountbean_arr;
	protected String accStr;
	DatabaseManagement dbms;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		listView1 = (ListView) findViewById(R.id.listView1);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		img_heading=(ImageView)findViewById(R.id.img_heading);
		
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		btn_back=(ImageButton)findViewById(R.id.btn_back);
		
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);
		
		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		
		btn_show_details = (Button) findViewById(R.id.btnShowDetails);
		btn_show_details.setOnClickListener(this);
		
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
	        	Log.e("retvalstr","......"+stringValue);
	        }
        }
        
        //all_acnts ="2#101#SB#4642#Mr. KADAM SUSHANT  D##0020001010004642#O~2#201#CA#1915#Mr. KADAM SUSHANT  D##0020002010001915#O~2#302#LO#10481#Mr. KADAM SUSHANT  D##0020003020010481#I~2#501#FD#36006#Mr. KADAM SUSHANT  D##0020005010036006#O~2#602#RP#4066#Mr. KADAM SUSHANT  D##0020006020004066#O~2#801#RA#5845#Mr. KADAM SUSHANT  D##0020008010005845#O~2#901#PG#11611#Mr. KADAM SUSHANT  D##0020009010011611#O~~";//stringValue;
        all_acnts =stringValue;
        img_heading.setBackgroundResource(R.mipmap.deposit);
		txt_heading.setText(getString(R.string.lbl_deposits));
		
		addAccounts(all_acnts, acc_type);
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}
	
	public void addAccounts(String all_accstr, String acc_type) 
	{
		try 
		{
			Accountbean_arr = new ArrayList<Accountbean>();
			
			ArrayList<Accountbean> depositbean_arr = new ArrayList<Accountbean>();
			
			
			ArrayList<String> arrList = new ArrayList<String>();
			
			ArrayList<String> depositArrList = new ArrayList<String>();
			
			String allstr[] = all_accstr.split("~");
			
			ArrayList<String> arrListTemp = new ArrayList<String>();
			
			//System.out.println("HomeFragment Mayuri.....................:");
			//System.out.println("HomeFragment Accounts:::" + allstr[1]);
			int noOfAccounts = allstr.length;
			Log.e("noOfAccounts==","noOfAccounts=="+noOfAccounts);
			//System.out.println("HomeFragment noOfAccounts:" + noOfAccounts);
			for (int i = 0; i < noOfAccounts; i++) 
			{
				//System.out.println(i + "----STR1-----------" + allstr[i]);
				str2 = allstr[i];
				//System.out.println(i + "str2-----------" + str2);
				str2 = str2.replaceAll("#", "-");

				String acType = str2.split("-")[2];
				//System.out.println("mbs============="+str2);
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				Log.e("acType==","acType=="+acType);
				Accountbean accountbean = new Accountbean();
				if (acType.equalsIgnoreCase("FD")
							|| acType.equalsIgnoreCase("CD")
							|| acType.equalsIgnoreCase("RP")
							//|| acType.equalsIgnoreCase("PG")
							|| acType.equalsIgnoreCase("RA")
							|| acType.equalsIgnoreCase("RD")) {
						
						
						depositbean_arr.add(accountbean);
						
						depositArrList.add(str2);
						arrListTemp.add(str2Temp);
					}
			
				Log.e("str2Temp==","str2Temp=="+str2Temp);
					accountbean.setAccStr(str2Temp);
					accountbean.setAccountinfo(str2+" ("+MBSUtils.getAccTypeDesc(acType)+")");
					accountbean.setAccountNumber(str2);
					accountbean.setMainType("LO");
					accountbean.setOprcd(str2Temp.split("-")[7]);
				
			}

			
				Accountbean_arr=depositbean_arr;
				arrList=depositArrList;
			
			//Log.e("HomeFragment","arrList=="+arrList);
			int[] prgmImages = new int[arrList.size()];

			for (int x = 0; x < arrList.size(); x++) {
				prgmImages[x] = R.mipmap.arrow;
			}
			prgmNameList = new String[arrList.size()];
			prgmNameList = arrList.toArray(prgmNameList);

			prgmNameListTemp = new String[arrListTemp.size()];
			prgmNameListTemp = arrListTemp.toArray(prgmNameListTemp);
			
			//Log.e("Debug@HomeFragment ","Before from adding accounts");
			Log.e("Accountbean_arr","Accountbean_arr"+Accountbean_arr);
			Log.e("Accountbean_arr","Accountbean_arr"+Accountbean_arr);
			Log.e("Accountbean_arr","Accountbean_arr"+Accountbean_arr);
			if(Accountbean_arr.size()>0)
			{
				//Customlist_radioadt adapter = new Customlist_radioadt(act,Accountbean_arr);
				Customlist_radioadt adapter = new Customlist_radioadt(this,Accountbean_arr);
				listView1.setAdapter(adapter);
				listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			}
			else
			{
				//showAlert(getString(R.string.alert_089));
				Toast.makeText(this, getString(R.string.alert_089), Toast.LENGTH_LONG).show();
				Intent in=new Intent(this,DashboardActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
			}
			//Log.e("Debug@HomeFragment ","After from adding accounts");
			listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> adapterView,
						View view, int i, long l) 
				{
					//Log.e("Debug@HomeFragment ","Click Added for Radio.");
					btn_show_details.setEnabled(true);
					
					//f = prgmNameListTemp[i];
					accNumber = Accountbean_arr.get(i).getAccountNumber();
					accStr=Accountbean_arr.get(i).getAccStr();
					acnt_inf=Accountbean_arr.get(i).getAccountinfo();
					Log.e("Omkar ","accStr="+accStr);
					Log.e("Omkar ","accNumber="+accNumber);
					for (int i1 = 0; i1 < adapterView.getCount(); i1++) {

						try {

							View v = adapterView.getChildAt(i1);
							RadioButton radio = (RadioButton) v
									.findViewById(R.id.radio);
							radio.setChecked(false);

						} catch (Exception e) {
							Log.e("radio button", "radio");
							e.printStackTrace();
						}

					}
					
					try {
						RadioButton radio = (RadioButton) view
								.findViewById(R.id.radio);
						radio.setChecked(true);
					} catch (Exception e) {
						Log.e("radio button", "radio");
						e.printStackTrace();
					}
					
					setTitle(getString(R.string.lbl_acc_details));
				}
			});
			
			
			
		} 
		catch (Exception e) 
		{
			Log.e("EXCEPTION", "---------------"+e);
			System.out.println("" + e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Log.e("onClick Event ","Clicked");
		if(v.getId()==R.id.btn_back||v.getId()==R.id.btn_home)
		{
				Intent in=new Intent(this,DashboardActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
		}
		else if(v.getId()==R.id.btnShowDetails)
		{
			//Log.e("In onClick","Show Details Button Clicked for "+prgmName);
			
			// acnt_inf = dataModel.getAccountinfo();
			Bundle b = new Bundle();
			Intent in = new Intent(ShowFdAccount.this,ShowFdAccountDetails.class);
			// Storing data into bundle
			b.putString("accountinfo", acnt_inf);
			b.putString("accountstr", accStr);
			b.putString("accountnumber", accNumber);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			in.putExtras(b);
			startActivity(in);
			finish();
			
		}
	}
	public void showAlert(String str)
	{
			//Toast.makeText(this, str, Toast.LENGTH_LONG).show();	
			ErrorDialogClass alert = new ErrorDialogClass(this,""+str);
			alert.show();
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
