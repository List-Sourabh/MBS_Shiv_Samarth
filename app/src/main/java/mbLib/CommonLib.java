package mbLib;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import list.shivsamarth_mbs.MainActivity;
import list.shivsamarth_mbs.OTPActivity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.text.format.Formatter;

public class CommonLib 
{
	int flag = 0;
	Activity act;
	
	public CommonLib(MainActivity act)
	{
		this.act=act;
	}
	
	public CommonLib(OTPActivity act1)
	{
		this.act=act1;
	}
	
	public int chkConnectivity() 
	{
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try 
		{
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			if (state1) 
			{
				switch (state) 
				{
					case CONNECTED:
						if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) 
						{}
					break;
					case DISCONNECTED:
						flag = 1;
					break;
					default:
						flag = 1;
					break;
				}
			} 
			else 
			{
				flag = 1;
			}
		} 
		catch (NullPointerException ne) 
		{
			flag = 1;
		} catch (Exception e) 
		{
			flag = 1;
		}
		return flag;
	}
	
	public String getLocalIpAddress() 
	{
		String ip="";
	    try 
	    {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
	        {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
	            {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) 
	                {
	                	ip = Formatter.formatIpAddress(inetAddress.hashCode());
	                    return ip;
	                }
	            }
	        }
	    } 
	    catch (SocketException ex) 
	    {
	    }
	    return ip;
	}
	
}
