package list.shivsamarth_mbs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.MyThread;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class QrcodeRcvActivity extends Activity implements OnClickListener {
	ImageView myImage;
	TextView txt_heading, qr_str;
	ImageButton btn_home, btn_back;
	ImageView img_heading;
	Button btn_share_qr;
	String accNo = "",accNm="";
	Bitmap bitmap = null;
	FileOutputStream jpgFile = null;
	private MyThread t1;
	int timeOutInSecs=300;
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_qr);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		  var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		myImage = (ImageView) findViewById(R.id.img_result);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		qr_str = (TextView) findViewById(R.id.qr_str);
		btn_share_qr = (Button) findViewById(R.id.btn_share_qr);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.transfer);
		txt_heading.setText(R.string.lbl_qr_code);
		
		//btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_back.setImageResource(R.mipmap.backover);

		btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		btn_share_qr.setOnClickListener(this);

		Bundle bObj = getIntent().getExtras();
		if (bObj != null)
		{
			accNo = bObj.getString("ACCNO");
			accNm = bObj.getString("ACCNM");
			
		}
		
		qr_str.setText("This Is Your QR Code For Account " + accNo
				+ ". Use This To Request Money.");
		try {
			WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = manager.getDefaultDisplay();
			Point point = new Point();
			display.getSize(point);
			int width = point.x;
			int height = point.y;
			int smallerDimension = width < height ? width : height;
			smallerDimension = smallerDimension * 3 / 4;

			// Log.e("GenQRCode","accNo==="+accNo);
			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(strToSend(accNo),
					null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
					smallerDimension);
			try {
				bitmap = qrCodeEncoder.encodeAsBitmap();
				
				myImage.setImageBitmap(bitmap);
				myImage.setVisibility(ImageView.VISIBLE);
				qr_str.setVisibility(TextView.VISIBLE);
			
			} catch (WriterException e) {
				e.printStackTrace();
			}
		} catch (ActivityNotFoundException activity) {
			// qrDroidRequired(MainActivity.this);
		}
	   	t1 = new MyThread(timeOutInSecs,this,var1,var3);
		t1.start();
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btn_back:
				Intent in = new Intent(this, ShowAccForQrcode.class);
				in.putExtra("var1", var1);
				   in.putExtra("var3", var3);
				startActivity(in);
				finish();
				break;
			case R.id.btn_share_qr:
				Bitmap icon = bitmap;
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				icon.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
				Log.e("A","accNo =="+accNo);
				File f = new File(Environment.getExternalStorageDirectory()+ File.separator +accNo+".jpg");
				boolean flg=false;
				
				if(f.exists())
				{	
					flg=f.delete();
					Log.e("QRRCV","file =="+flg);
					Log.e("QRRCV","file =="+flg);
					Log.e("QRRCV","file =="+flg);
				}
				Log.e("QRRCV","file 111=="+flg);
				f = new File(Environment.getExternalStorageDirectory()+ File.separator +accNo+".jpg");
				
				
				try 
				{
					FileOutputStream out = new FileOutputStream(f);
					Canvas canvas = new Canvas(bitmap);
					Paint paint = new Paint();
					paint.setColor(Color.BLACK); // Text Color
					//paint.setStrokeWidth(25); // Text Size
					paint.setTextSize(18);
					paint.setTextAlign(Align.LEFT);
					paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
					// some more settings...

					canvas.drawBitmap(bitmap, 0, 0, paint);
					canvas.drawText(accNm+"\n***"+accNo.substring(12), 20, bitmap.getHeight()-9, paint);
					// NEWLY ADDED CODE ENDS HERE ]
	                // bitmap.getWidth()/
					bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
					out.flush();
					out.close();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	/*Log.e("-----", ""+act.getApplicationContext());
			Log.e("-----", ""+act.getPackageName());
			Log.e("-----", ""+new File(accNo+".jpg"));
			Uri uri = FileProvider.getUriForFile(act.getApplicationContext(), act.getPackageName(),
					new File(accNo+".jpg"));
			Log.e("-----", ""+uri.toString());
			Log.e("-----", ""+uri.getPath());
			// FileProvider.getUriForFile(act.getApplicationContext(), act.getPackageName() + ".provider",new File("file:///sdcard/"+accNo+".jpg"));
			share.putExtra(Intent.EXTRA_STREAM, uri.toString());//	uri.parse("file:///sdcard/"+accNo+".jpg"));
		//	startActivity(Intent.createChooser(share, "Share Image"));
		 * 
*/			
			share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri apkURI = FileProvider.getUriForFile(this,this.getPackageName() , f);
				share.putExtra(Intent.EXTRA_STREAM,	Uri.parse("file:///sdcard/"+accNo+".jpg"));
	                  share.putExtra(Intent.EXTRA_STREAM,apkURI);
			 share.setType("image/*");
				startActivity(Intent.createChooser(share, "Share Image"));
	break;
			
			default:
				break;
		}
	}
	
	public String strToSend(String str) 
	{
		int sum = 0, grandSum = 0;
		for (int i = 0; i < str.length(); i++) 
		{
			sum = sum + Integer.parseInt("" + str.charAt(i));
		}
		while (sum > 9) 
		{
			grandSum = 0;
			while (sum > 0) 
			{
				int rem;
				rem = sum % 10;
				grandSum = grandSum + rem;
				sum = sum / 10;
			}
			sum = grandSum;
		}
		return str + grandSum;
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
