package list.shivsamarth_mbs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import androidx.fragment.app.FragmentActivity;
import mbLib.CryptoClass;
import mbLib.MBSUtils;

public class LocateUs extends FragmentActivity implements LocationListener,
		OnClickListener {
	ArrayList<Marker> marker;
	Marker currMarker;
	StringBuffer urlString = new StringBuffer();
	ArrayList<LatLng> locList;
	Button btAddLoc, btCalcArea, btStart;
	ImageButton btn_home, btn_back;
	TextView txt_heading;
	ImageView img_heading;
	EditText txtArea, txtLatitude, txtLongitude;
	ToggleButton toggleMap, toggleDrag;
	private LatLng curlatlng;
	private CameraUpdate center;
	private LocationManager locManager;
	private Marker temp;
	private GoogleMap map;
	double maxLat = 0.0, maxLong = 0.0, minLat = 0.0, minLong = 0.0;
	static final LatLng HAMBURG = new LatLng(53.558, 74.927);

	static LocateUs obj;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME_GET_MAP_INFO = "";
	PrivateKey var1 = null;
	String var5 = "", var3 = "";
	SecretKeySpec var2 = null;

	String custId = "", retMess = "", retVal = "", currentLocation = "",retval = "",respcode="",respdesc="",retvalweb="";
	int cnt = 0, flag = 0;

	@SuppressLint("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
		Log.e("LocateUs", "onCreate");
		setContentView(R.layout.locate_us);
			//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		 var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
	       var3 = (String) getIntent().getSerializableExtra("var3");

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setImageResource(R.mipmap.backover);
		btn_back.setOnClickListener(this);
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_locate_us));
		img_heading=(ImageView)findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.location);
		obj = this;
		/*Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
		SupportMapFragment mapFragment = (SupportMapFragment) fragment;
		map = mapFragment.getMap();*/
            SupportMapFragment supportmapfragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            supportmapfragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map=googleMap;
                    map.setMyLocationEnabled(true);
                }
            });
		//map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		// map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		// map.setMyLocationEnabled(true);
		if(chkConnectivity()==0)
			new CallWebServiceGetBnkBrnMapInfo().execute();
		/*if (map == null) {
			showAlert("Sorry! unable to create maps");
		}*/
		locList = new ArrayList<LatLng>();
		marker = new ArrayList<Marker>();

		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				// TODO Auto-generated method stub
				if(currMarker!=null )
				{
					if(point!=currMarker.getPosition())
					{
						//currMarker.setPosition(new LatLng(16.8,74.6));
						String str="http://maps.google.com/maps?saddr="+currMarker.getPosition().latitude+","+currMarker.getPosition().longitude+"&daddr="+point.latitude+","+point.longitude;//+"&key="+getString(R.string.map_key);
						//String str="http://maps.google.com/maps?saddr=16.8,74.6&daddr="+point.latitude+","+point.longitude;//+"&key="+getString(R.string.map_key);
						Uri gmmIntentUri = Uri.parse(str);

						Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
						mapIntent.setPackage("com.google.android.apps.maps");
						startActivity(mapIntent);
					}
				}
			}
		});
		/*map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				if(currMarker!=null )
				{
					if(marker.getPosition()!=currMarker.getPosition())
					{
						//currMarker.setPosition(new LatLng(16.8,74.6));
						String str="http://maps.google.com/maps?saddr="+currMarker.getPosition().latitude+","+currMarker.getPosition().longitude+"&daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude;//+"&key="+getString(R.string.map_key);
						//String str="http://maps.google.com/maps?saddr=16.8,74.6&daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude;//+"&key="+getString(R.string.map_key);
						Uri gmmIntentUri = Uri.parse(str);
		
						Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
						mapIntent.setPackage("com.google.android.apps.maps");
						startActivity(mapIntent);
					}
				}
				return false;
			}
		});*/
		/*map.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker marker) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onMarkerDrag(Marker marker) {
				// TODO Auto-generated method stub
				String tempLat = "" + currMarker.getPosition().latitude;
				String tempLong = "" + currMarker.getPosition().longitude;
				// txtLatitude.setText(tempLat.substring(0,tempLat.indexOf(".")+3));
				// txtLongitude.setText(tempLong.substring(0,tempLong.indexOf(".")+3));
			}
		});*/
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				(LocationListener) this);
		}
		catch(Exception e)
		{
			//Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
			Log.e("=====",""+e);
		}
	}// end onCreate

	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		// Add a marker in Sydney, Australia, and move the camera.
		Log.e("DEBUG", "OMGOMG");
		LatLng sydney = new LatLng(-34, 151);
		map.addMarker(new MarkerOptions().position(sydney).title(
				"Marker in Sydney"));
		map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 * int status =
		 * GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext
		 * ());
		 * 
		 * if(status!=ConnectionResult.SUCCESS) { int requestCode = 10; Dialog
		 * dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
		 * requestCode); dialog.show(); } else
		 */
		{
			// 1: call webservice for getting address for merchant
			// 2: show all merchant on google map
			// 3: get current location of device .
			// 4: while onClick of any of merchant then show path from current
			// device location and clicked merchant.

			// this is step 2:
			// progress.setMessage("Merchants are loading");
			// progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// progress.setIndeterminate(true);

			// map.setOnMapClickListener(this);

			// System.out.println("map:"+map);
			// System.out.println("fragment:"+fragment);
			// System.out.println("mapFragment:"+mapFragment);
			// kop = map.addMarker(new
			// MarkerOptions().position(Kolhapur).title("Agent-Kolhapur Station"));
			// map.moveCamera(CameraUpdateFactory.newLatLngZoom(Kolhapur, 15));
			// shiroli_ = map.addMarker(new
			// MarkerOptions().position(shiroli).title("Agent-Shiroli MIDC"));
			// map.moveCamera(CameraUpdateFactory.newLatLngZoom(shiroli,8));
			// locationFinder=new FindCurrentLocation(MapActivity.this);
			// locationFinder.setMap(map);
			// locationFinder.getCurrentLocation();
			// map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
			// kop = map.addMarker(new
			// MarkerOptions().position().title("Agent-Kolhapur Station"));
			// String area=locationFinder.getCurrentArea();
			/*
			 * Log.e("==== area ====",area); Log.e("==== area ====",area);
			 * Log.e("==== area ====",area);
			 */
			/*
			 * if(area!=null &&
			 * !(area.equalsIgnoreCase("Failed to collect area information"))) {
			 * progress.dismiss(); }
			 */
		}

	}// end onResume

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.btn_back) {
			/*locManager.removeUpdates(this);*/
			Intent in = new Intent(this, LoginActivity.class);
			in.putExtra("var1", var1);
			   in.putExtra("var3", var3);
			startActivity(in);
			finish();
		}
		/*
		 * if(arg0.getId()==R.id.btAddLoc) {
		 * locList.add(currMarker.getPosition()); temp=map.addMarker(new
		 * MarkerOptions
		 * ().position(currMarker.getPosition()).title("Location "+marker
		 * .size()+1)); marker.add(temp);
		 * marker.get(marker.size()-1).setPosition(currMarker.getPosition());
		 * for(int i=0;i<locList.size()-1;i++) { Polyline
		 * line=map.addPolyline(new PolylineOptions()
		 * .add(locList.get(i),locList.get(i+1)) .width(5)
		 * .color(Color.parseColor("#FF0000"))); }
		 * currMarker.setPosition(curlatlng);
		 * center=CameraUpdateFactory.newLatLng(curlatlng);
		 * map.moveCamera(center); toggleDrag.setChecked(false);
		 * Toast.makeText(this, "Size Of Marker List is "+marker.size(),
		 * Toast.LENGTH_LONG).show(); } else if(arg0.getId()==R.id.btStart) {
		 * locManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE);
		 * locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,
		 * (LocationListener) this); } else if(arg0.getId()==R.id.btCalcArea) {
		 * String tempArea=""+CalculatePolygonArea(locList);
		 * tempArea=tempArea.substring(0,tempArea.indexOf(".")+3);
		 * tempArea=""+(Float.parseFloat(tempArea)*3.29)*1000;
		 * tempArea=tempArea.substring(0,tempArea.indexOf(".")+3);
		 * txtArea.setText(tempArea); currMarker.setDraggable(false); Polygon
		 * polygon=map.addPolygon(new
		 * PolygonOptions().addAll(locList).fillColor(
		 * Color.parseColor("#00FF00"))); locManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE); } else
		 * if(arg0.getId()==R.id.toglMap) { Toast.makeText(this,
		 * "toggleMap.isChecked()="+toggleMap.isChecked(),
		 * Toast.LENGTH_LONG).show(); if(toggleMap.isChecked()) {
		 * map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); } else {
		 * map.setMapType(GoogleMap.MAP_TYPE_TERRAIN); } } else
		 * if(arg0.getId()==R.id.toglDrag) { Toast.makeText(this,
		 * "toggleDrag.isChecked()="+toggleDrag.isChecked(),
		 * Toast.LENGTH_LONG).show(); if(toggleDrag.isChecked()) {
		 * locManager.removeUpdates(this); currMarker.setDraggable(true); } else
		 * { locManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE);
		 * locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,
		 * (LocationListener) this); currMarker.setDraggable(false); } }
		 */
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		curlatlng = new LatLng(location.getLatitude(), location.getLongitude());
		//Toast.makeText(this, "Set to currenr location" + currMarker, Toast.LENGTH_SHORT).show();
		
		if (currMarker == null) {
			//Toast.makeText(this, "In null If", Toast.LENGTH_SHORT).show();
			currMarker = map.addMarker(new MarkerOptions()
					.position(curlatlng)
					.title("Current Location")
					.icon(BitmapDescriptorFactory
							.fromResource(R.mipmap.current_location)));
		}
		else{
			currMarker.setPosition(curlatlng);
		}

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	public void onBackPressed() 
	{	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
		{@Override
			public void onClick(View v)

			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retvalweb);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
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

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		System.out
				.println("========================= end chkConnectivity ==================");
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("BalanceEnquiry	in chkConnectivity () state1 ---------"
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


			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		System.out
				.println("========================= end chkConnectivity ==================");
		return flag;
	}// end chkConnectivity

	class CallWebServiceGetBnkBrnMapInfo extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(LocateUs.this);

		
		JSONObject jsonObj = new JSONObject();
		

		protected void onPreExecute() {
			try{
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			jsonObj.put("CUSTID", custId);
			jsonObj.put("IMEINO", MBSUtils.getImeiNumber(LocateUs.this));
			jsonObj.put("SIMNO", MBSUtils.getSimNumber(obj));
			jsonObj.put("METHODCODE","44"); 
			//valuesToEncrypt[0] = custId;
			// valuesToEncrypt[1] = MBSUtils.getImeiNumber(LocateUs.this);
			}
		     catch (JSONException je) {
	                je.printStackTrace();
	            }
	            
	        
		}

		protected Void doInBackground(Void... arg0) {
			 String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "callWebservice";

				try 
				{
					String keyStr=CryptoClass.Function2();
					var2=CryptoClass.getKey(keyStr);
					SoapObject request = new SoapObject(value4, value7);
					request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
					request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
					request.addProperty("value3", var3);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);

					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1,var5.length() - 3);
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {
			// String[] xml_data = CryptoUtil.readXML(retVal,
						// new String[] { "BRNMAPS" });
						
					
						
						loadProBarObj.dismiss();
						 JSONObject jsonObj;
							try
							{
				
								String str=CryptoClass.Function6(var5,var2);
								jsonObj = new JSONObject(str.trim());
				               if (jsonObj.has("RESPCODE"))
								{
									respcode = jsonObj.getString("RESPCODE");
								}
								else
								{
									respcode="-1";
								}
								if (jsonObj.has("RETVAL"))
								{
									retvalweb = jsonObj.getString("RETVAL");
								}
								else
								{
									retvalweb = "";
								}
								if (jsonObj.has("RESPDESC"))
								{
									respdesc = jsonObj.getString("RESPDESC");
								}
								else
								{	
									respdesc = "";
								}
							} catch (JSONException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(respdesc.length()>0)
							{
								showAlert(respdesc);
							}
							else{
						if (retvalweb.indexOf("FAILED") > -1) {
							showAlert(getString(R.string.alert_134));
						} else {
							post_success(retvalweb);
						}}
		}// end onPostExecute

	}// end CallWebServiceGetBnkBrnMapInfo
	
	public 	void post_success(String retvalweb)
	{
		respcode="";
		respdesc="";
		double maxLat = 0.0, maxLong = 0.0, minLat = 0.0, minLong = 0.0;
		try {
			Log.e("onPostExecute", retvalweb);
			JSONArray jsArr = new JSONArray(retvalweb);
			int i = 0;
			for (; i < jsArr.length(); i++) {
				JSONObject json_data = jsArr.getJSONObject(i);
				String desc = json_data.getString("nm");
				String lat = json_data.getString("lat");
				String lng = json_data.getString("lng");
				double tempLat = Double.parseDouble(lat);
				double tempLong = Double.parseDouble(lng);
				
				maxLat = maxLat + tempLat;
				maxLong = maxLong + tempLong;
				Log.e("OMG" + i, desc + "=" + lat + "=" + lng);

				curlatlng = new LatLng(new Double(lat), new Double(lng));

				temp = map.addMarker(new MarkerOptions().position(
						curlatlng).title(desc));
				

				temp.showInfoWindow();
				marker.add(temp);
				// Log.e("THANK","GOD");
			}
			if (i > 0) {
				center = CameraUpdateFactory
						.newLatLng(new LatLng(new Double(maxLat / i),
								new Double(maxLong / i)));
				map.moveCamera(center);
				map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
			}
		} catch (Exception ex) {
			Log.e("OMG: Error", ex.toString());
			ex.printStackTrace();
		}		
	}
	
	//@Override
	/*protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//onDestroy();
		locManager.removeUpdates(this);
		finish();
	}*/
}
