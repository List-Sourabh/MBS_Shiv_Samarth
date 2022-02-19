package list.shivsamarth_mbs;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import mbLib.MBSUtils;
import mbLib.MyThread;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TransferHistoryRpt extends Activity implements OnClickListener
{
	TransferHistoryRpt act= this;
	
	ImageButton back,btn_home;
	
	String retMess,accNo;
	ListView listView1 ;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	TextView txt_heading,txt_acc_no;
	ImageView img_heading;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	public TransferHistoryRpt(){}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer_history_rpt);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_acc_no=(TextView)findViewById(R.id.txt_acc_no);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.transfer_history);
		back = (ImageButton) findViewById(R.id.btn_back);
		btn_home=(ImageButton)findViewById(R.id.btn_home);
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		back.setImageResource(R.mipmap.backover);
		btn_home.setOnClickListener(this);
		back.setOnClickListener(this);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		listView1 = (ListView) findViewById(R.id.listView1);
		//Log.e("TransferHistoryRpt"," onCreateView");
		
		Bundle bObj = getIntent().getExtras();
		if (bObj != null)
		{
			retMess= bObj.getString("RETVAL");
			accNo = bObj.getString("ACCNO");
			
			Log.e("LIST","Boundle-retMess"+retMess);
			Log.e("LIST","Boundle-accNo"+accNo);
		}
		setValues();
		t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_back:
			
				Intent in = new Intent(act,TransferHistory.class);
				act.startActivity(in);
				act.finish();
				
				break;
			case R.id.btn_home:
				/*Intent in=new Intent(act,DashboardDesignActivity.class);
				startActivity(in);
				act.finish();*/
				break;
			default:
				break;
		}
	}
	
	public void setValues() 
	{
		
		txt_heading.setText(getString(R.string.lbl_transfer_history));
		//img_heading.setBackgroundResource(R.mipmap.mini_statement);
		//HashMap<String, String> map = new HashMap<String, String>();
		String[] from = new String[] {"rowid", "col_0", "col_1", "col_2","col_3"};//,"col_6"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4,R.id.item5};//,R.id.item6 };
		int count=0;
		try
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("col_0","Date");
			map.put("col_1","Nick Name");
			map.put("col_2","Amount(Rs.)");
			map.put("col_3","Status");
			//map.put("col_6","Transaction Id");
			fillMaps.add(map);
			
			//JSONObject mainObj= new JSONObject(retMess);
			JSONArray ja=new JSONArray(retMess);
			for(int j=0;j<ja.length();j++)
			{
				JSONObject jObj=ja.getJSONObject(j);
				map = new HashMap<String, String>();
				//Log.e("TransferHistoryRpt","benf nm=="+jObj.getString("BNM_NICKNAME"));
				//jObj.getString("MFT_REQID"));
				map.put("col_0",jObj.getString("DATE"));
				map.put("col_1",jObj.getString("NICKNAME"));
				map.put("col_2",""+MBSUtils.amountFormat(jObj.getString("AMOUNT"),false,act));
			//	map.put("col_2",jObj.getString("AMOUNT"));
				/*if(jObj.getString("STATUS").equalsIgnoreCase("TRUE"))
					map.put("col_3","Success");
				else
					map.put("col_3","Failed");*/
				map.put("col_3",jObj.getString("STATUS"));
				/*if(jObj.getString("TYPE").equalsIgnoreCase("INTBANK"))
					map.put("col_6","SAMEBNK");
				else
					map.put("col_6",jObj.getString("TYPE"));*/
				
				fillMaps.add(map);
				
				count++;
			}
			SimpleAdapter adapter = new SimpleAdapter(act, fillMaps, R.layout.transfer_history_list, from, to);
			listView1.setAdapter(adapter);
			txt_acc_no.setText(accNo);
		}
		catch(Exception ex)
		{
			Log.e("Error",ex.toString());
		}
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
