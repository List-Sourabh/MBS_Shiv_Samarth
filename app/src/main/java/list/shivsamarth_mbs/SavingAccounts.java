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

//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class SavingAccounts extends Activity implements OnClickListener{
	private ListView listView1;
	SavingAccounts act;
	String balnaceamnt = "",accountNo="";
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
	
	public SavingAccounts(){}
	public SavingAccounts(SavingAccounts a) {
		act = a;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.savingaccounts);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");
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
				
	//	SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
	//			Context.MODE_PRIVATE);
		//e = sp.edit();
	//	stringValue = sp.getString("retValStr", "retValStr");
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
	        	Log.e("retvalstr","......"+stringValue);
	        }
        }
		//stringValue="2#310#LO#7965#KADAM SUSHANT D##0020003100007965#I#NA#0#Y~2#101#SB#19355#Mr. KADAM SUSHANT  D##0020001010019355#O#NA#2797.4#Y~2#101#SB#25721#Mr. KADAM SUSHANT  D##0020001010025721#O#NA#22023069.78#Y~2#101#SB#25730#Mr. KADAM SUSHANT  D##0020001010025730#O#NA#0#Y~2#101#SB#25733#Mr. KADAM SUSHANT  D##0020001010025733#O#NA#544598#Y~2#101#SB#25768#Mr. KADAM SUSHANT  D##0020001010025768#O#NA#0#Y~2#101#SB#25791#Mr. KADAM SUSHANT  D##0020001010025791#O#NA#0#Y~2#101#SB#25792#Mr. KADAM SUSHANT  D##0020001010025792#O#NA#0#Y~2#101#SB#25793#Mr. KADAM SUSHANT  D##0020001010025793#O#NA#0#Y~2#101#SB#25828#Mr. KADAM SUSHANT  D##0020001010025828#O#NA#455#Y~2#101#SB#25833#Mr. KADAM SUSHANT  D##0020001010025833#O#NA#6179.2#Y~2#101#SB#25859#Mr. KADAM SUSHANT  D##0020001010025859#O#NA#0#Y~3#101#SB#6628#Mr. KADAM SUSHANT  D##0030001010006628#O#NA#14800#Y~3#101#SB#6629#Mr. KADAM SUSHANT  D##0030001010006629#O#NA#0#Y~3#101#SB#6630#Mr. KADAM SUSHANT  D##0030001010006630#O#NA#0#Y~3#101#SB#6631#Mr. KADAM SUSHANT  D##0030001010006631#O#NA#2997020#Y~2#102#SB#18309#Mr. KADAM SUSHANT  D##0020001020018309#O#NA#0#Y~2#201#CA#2272#Mr. KADAM SUSHANT  D##0020002010002272#O#NA#0#Y~2#201#CA#2275#Mr. KADAM SUSHANT  D##0020002010002275#O#NA#0#Y~2#201#CA#2281#Mr. KADAM SUSHANT  D##0020002010002281#O#NA#0#Y~2#301#LO#187#Mr. KADAM SUSHANT  D##0020003010000187#I#NA#55000#Y~2#301#LO#206#Mr. KADAM SUSHANT  D##0020003010000206#I#NA#0#Y~2#301#LO#207#Mr. KADAM SUSHANT  D##0020003010000207#I#NA#0#Y~2#301#LO#208#Mr. KADAM SUSHANT  D##0020003010000208#I#NA#100#Y~2#301#LO#209#Mr. KADAM SUSHANT  D##0020003010000209#I#NA#895#Y~2#301#LO#211#Mr. KADAM SUSHANT  D##0020003010000211#I#NA#998650#Y~2#301#LO#215#Mr. KADAM SUSHANT  D##0020003010000215#I#NA#0#Y~2#301#LO#220#Mr. KADAM SUSHANT  D##0020003010000220#I#NA#0#Y~2#303#LO#9107#Mr. KADAM SUSHANT  D##0020003030009107#O#NA#0#Y~2#303#LO#9108#Mr. KADAM SUSHANT  D##0020003030009108#O#NA#0#Y~2#303#LO#9109#Mr. KADAM SUSHANT  D##0020003030009109#O#NA#0#Y~2#303#LO#9110#Mr. KADAM SUSHANT  D##0020003030009110#O#NA#0#Y~2#303#LO#9111#Mr. KADAM SUSHANT  D##0020003030009111#O#NA#0#Y~2#303#LO#9112#Mr. KADAM SUSHANT  D##0020003030009112#O#NA#0#Y~2#303#LO#9114#Mr. KADAM SUSHANT  D##0020003030009114#O#NA#0#Y~2#303#LO#9124#Mr. KADAM SUSHANT  D##0020003030009124#O#NA#10000#Y~2#303#LO#9126#Mr. KADAM SUSHANT  D##0020003030009126#O#NA#1840#Y~2#303#LO#9127#Mr. KADAM SUSHANT  D##0020003030009127#O#NA#1840#Y~2#305#LO#48#Mr. KADAM SUSHANT  D##0020003050000048#O#NA#0#Y~2#315#LO#1016#Mr. KADAM SUSHANT  D##0020003150001016#I#NA#577949.4#Y~2#315#LO#1017#Mr. KADAM SUSHANT  D##0020003150001017#I#NA#1996677#Y~2#315#LO#1021#Mr. KADAM SUSHANT  D##0020003150001021#I#NA#0#Y~2#315#LO#1025#Mr. KADAM SUSHANT  D##0020003150001025#I#NA#9900#Y~2#315#LO#1026#Mr. KADAM SUSHANT  D##0020003150001026#I#NA#0#Y~2#315#LO#1027#Mr. KADAM SUSHANT  D##0020003150001027#I#NA#0#Y~2#315#LO#1029#Mr. KADAM SUSHANT  D##0020003150001029#I#NA#991490#Y~2#337#LO#8347#Mr. KADAM SUSHANT  D##0020003370008347#I#NA#39001#Y~2#301#LO#161#SUSHANT D KADAM##0020003010000161#I#NA#0#Y~2#201#CA#2270#KHEBUDKAR JAGDISH  GOVIND##0020002010002270#O#NA#0#Y~2#101#SB#25721#Mr. KADAM SUSHANT  D##0020001010025721#O#NA#22023069.78#Y~2#101#SB#19216#PATIL ASHOK   MAHADEV##0020001010019216#O#NA#3858#Y~2#501#FD#59708#Mr. KADAM SUSHANT  D##0020005010059708#I##836876#N~2#501#FD#59741#Mr. KADAM SUSHANT  D##0020005010059741#I##37000#N~2#501#FD#59750#Mr. KADAM SUSHANT  D##0020005010059750#I##1000#N~2#601#RP#117#Mr. KADAM SUSHANT  D##0020006010000117#I##1481659#N~2#601#RP#118#Mr. KADAM SUSHANT  D##0020006010000118#I##0#N~2#602#RP#12128#Mr. KADAM SUSHANT  D##0020006020012128#I##50000#N~2#801#RA#9823#Mr. KADAM SUSHANT  D##0020008010009823#I##0#N~2#801#RA#9836#Mr. KADAM SUSHANT  D##0020008010009836#I##0#N~2#801#RA#9844#Mr. KADAM SUSHANT  D##0020008010009844#I##120000#N~2#801#RA#9845#Mr. KADAM SUSHANT  D##0020008010009845#I##0#N~2#801#RA#9846#Mr. KADAM SUSHANT  D##0020008010009846#I##0#N~2#801#RA#9848#Mr. KADAM SUSHANT  D##0020008010009848#I##29500#N";
		//stringValue="2#101#SB#25730#Mr. KADAM SUSHANT  D##0020001010025730#O#NA#10#Y~2#101#SB#25733#Mr. KADAM SUSHANT  D##0020001010025733#O#NA#544598#Y~2#101#SB#25768#Mr. KADAM SUSHANT  D##0020001010025768#O#NA#20#Y~2#101#SB#25791#Mr. KADAM SUSHANT  D##0020001010025791#O#NA#30#Y";
		all_acnts = stringValue;
		
		///chekacttype =  getArguments().getInt("CHECKACTTYPE");  
		//Log.e("HOME FRAGMENT11111","stringValue=="+stringValue);
		//Log.e("HOME FRAGMENT11111","chekacttype=="+chekacttype);
		
			img_heading.setBackgroundResource(R.mipmap.savings);
			txt_heading.setText(getString(R.string.lbl_saving_and_current));
		
			
		addAccounts(all_acnts);
		
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	public void addAccounts(String str) {
		//System.out.println("MiniStmtActivity IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			//System.out.println("MiniStmtActivity noOfAccounts:" + noOfAccounts);

			ArrayList<Accountbean> Accountbean_arr = new ArrayList<Accountbean>();
			final ArrayList<String> Account_arrTemp = new ArrayList<String>();
			Accounts acArray[] = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) 
			{
				Log.e("noOfAccounts ","noOfAccounts "+i);
				//System.out.println(i + "----STR1-----------" + allstr[i]);
				str2 = allstr[i];

				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
               String acctype=str2.split("-")[2];
               Log.e("noOfAccounts ","11111111 "+acctype);
				// arrList.add(str2);
			//	if (str2.indexOf("FD") == -1 && str2.indexOf("RP") == -1 && str2.indexOf("PG") == -1 && str2.indexOf("CA") == -1) 
            if ((acctype.equalsIgnoreCase("SB") || acctype.equalsIgnoreCase("CA")))
				{
					Log.e("MINISTMT","str2 added=="+str2);
					Log.e("noOfAccounts ","22222 ");
					Accountbean Accountbeanobj = new Accountbean();
					Accountbean_arr.add(Accountbeanobj);
					Account_arrTemp.add(str2);
					Log.e("noOfAccounts ","33333 ");
				//	String acctype=str2.split("-")[2];
					str2 = MBSUtils.get16digitsAccNo(str2);
					Log.e("noOfAccounts ","44444 ");
					Accountbeanobj.setAccountinfo(str2+" ("+MBSUtils.getAccTypeDesc(acctype)+")");
					Accountbeanobj.setAccountNumber(str2);
					Log.e("noOfAccounts ","55555 ");
				}	

			}

		
			Customlist_radioadt adapter = new Customlist_radioadt(this,
					Accountbean_arr);
			listView1.setAdapter(adapter);
			listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			Log.e("noOfAccounts ","6666 ");
			listView1
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView,
								View view, int i, long l) {

							Log.e("noOfAccounts ","7777 ");
							btn_show_details.setEnabled(true);
							Accountbean dataModel = (Accountbean) adapterView
									.getItemAtPosition(i);

							//Log.e("SSSSS:dataModel","dataModel=="+ dataModel.getAccountinfo());
							accountNo=dataModel.getAccountNumber();
							//Log.e("temp acc str ==", Account_arrTemp.get(i));
							// acnt_inf=dataModel.getAccountinfo();
							acnt_inf = Account_arrTemp.get(i);
							Log.e("noOfAccounts ","8888 ");

							for (int i1 = 0; i1 < adapterView.getCount(); i1++) {

								try {
									Log.e("noOfAccounts ","9999 ");

									View v = adapterView.getChildAt(i1);
									RadioButton radio = (RadioButton) v
											.findViewById(R.id.radio);
									radio.setChecked(false);

								} catch (Exception e) {
									Log.e("radio button", "radio");
								}

							}

							try {
								RadioButton radio = (RadioButton) view
										.findViewById(R.id.radio);
								radio.setChecked(true);
							} catch (Exception e) {
								Log.e("radio button", "radio");
							}

						}
					});

			//Log.e("MiniStmtActivity ", "Exiting from adding accounts");

			//Log.e("MiniStmtActivity MAYURI....", acnt_inf);

		} catch (Exception e) {
			System.out.println("jayesh===" + e);
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Log.e("onClick Event ","Clicked");
		if(v.getId()==R.id.btn_home)//v.getId()==R.id.btn_back||
		{
				Intent in=new Intent(this,DashboardActivity.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				this.finish();
		}
		else if(v.getId()==R.id.btnShowDetails)
		{
			Log.e("In onClick","Show Details Button Clicked for ");
			Log.e("In onClick","Show Details Button Clicked for ");
			Log.e("In onClick","Show Details Button Clicked for ");
			Log.e("In onClick","jayesh accNumber "+accountNo);
			// acnt_inf = dataModel.getAccountinfo();
			Bundle b = new Bundle();

			// Storing data into bundle
			b.putString("accountinfo", acnt_inf);
			b.putString("accountstr", accStr);
			b.putString("accountnumber", accountNo);
			
			Intent in=new Intent(this,SavingAccountsDetails.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			in.putExtras(b);
			startActivity(in);
			this.finish();			
		}
		else if(v.getId()==R.id.btn_back)
		{
			Intent in=new Intent(this,DashboardActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			this.finish();
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
