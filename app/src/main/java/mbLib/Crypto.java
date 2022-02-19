package mbLib;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import android.util.Log;
public class Crypto
{//Crypto
	 
	public static void ListSupportedAlgorithms() 
	 {
		        String result = "";
		    	
		        // get all the providers
		        Provider[] providers = Security.getProviders();
		        FileWriter f=null;
		        PrintWriter pw=null;
		        try
		        {
		        	 f=new FileWriter(new File("E:\\Algo.txt")); 
		        	 pw=new PrintWriter(f);
				        for (int p = 0; p < providers.length; p++) 
				       {
				            // get all service types for a specific provider
				            Set<Object> ks = providers[p].keySet();
				            Set<String> servicetypes = new TreeSet<String>();
				            for (Iterator<Object> it = ks.iterator(); it.hasNext();) 
			                            {
				                String k = it.next().toString();
				                k = k.split(" ")[0];
				                if (k.startsWith("Alg.Alias."))
				                    k = k.substring(10);
		
				                servicetypes.add(k.substring(0, k.indexOf('.')));
				            }
		
				            // get all algorithms for a specific service type
				            int s = 1;
				            for (Iterator<String> its = servicetypes.iterator(); its.hasNext();) 
					{
				                String stype = its.next();
				                Set<String> algorithms = new TreeSet<String>();
				                for (Iterator<Object> it = ks.iterator(); it.hasNext();) 
			                               {
				                String k = it.next().toString();
				                k = k.split(" ")[0];
				                if (k.startsWith(stype + "."))
				                    algorithms.add(k.substring(stype.length() + 1));
				                else if (k.startsWith("Alg.Alias." + stype +"."))
				                    algorithms.add(k.substring(stype.length() + 11));
				              }
		
				            int a = 1;
				            for (Iterator<String> ita = algorithms.iterator(); ita.hasNext();) 
			                           {
				                result += ("[P#" + (p + 1) + ":" + providers[p].getName() + "]" +
				                           "[S#" + s + ":" + stype + "]" +
				                           "[A#" + a + ":" + ita.next() + "]\n");
				                a++;
				            }
				    		
				            s++;
				        }
				    }
		        }//try
		        catch(Exception e)		        
		        {
		        	System.out.println("Exception E:"+e);
		        }
		    System.out.println(result);
		    pw.println(result);
		    pw.flush();
		    
		    }	   
		
	//***************************

	public static byte[] encrypt(String strToEncrypt, Key key1)
	{
		try
		{
			// Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());	
			Cipher C = Cipher.getInstance("AES/ECB/NOPADDING");//,"BC");
			C.init(Cipher.ENCRYPT_MODE, key1);
			byte[] encrptedStr = C.doFinal(strToEncrypt.getBytes());
			return encrptedStr;
		}
		catch (NoSuchAlgorithmException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"NoSuchAlgorithmException In encrypt():"+e.toString());
			return null;
		}
		catch (InvalidKeyException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"InvalidKeyException In encrypt():"+e.toString());
			return null;
		}
	
		catch (NoSuchPaddingException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"NoSuchPaddingException In encrypt():"+e.toString());
			return null;
		}
		catch (NullPointerException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"NullPointerException In encrypt():"+e.toString());
			return null;
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"Exception In encrypt():"+e.toString());
			return null;
		}	
	}
	
	public static byte[] generateMAC(String strToEncrypt,Key key1) 
	{
		try
		{
			//Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			Mac mac = Mac.getInstance("HMAC-MD5");//,"BC");
			mac.init(key1);
			return mac.doFinal(strToEncrypt.getBytes());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("Crypto Class"+"Exception in generateMAC():"+e);
			return null;
		}
	}
	
	public static String decrypt(byte[] strToDecrypt,Key key1)
	{
		try
		{
			//Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			System.out.println("***********decrypt()************");
			Cipher C = Cipher.getInstance("AES/ECB/NOPADDING");//,"BC");
			C.init(Cipher.DECRYPT_MODE,key1);
			return new String(C.doFinal(strToDecrypt));
		}
		catch (NoSuchAlgorithmException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"NoSuchAlgorithmException In decrypt():"+e.toString());
			return null;
		}
		catch (InvalidKeyException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"InvalidKeyException In decrypt():"+e.toString());
			return null;
		}
		
		catch (NoSuchPaddingException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"NoSuchPaddingException In decrypt():"+e.toString());
			return null;
		}
		catch (NullPointerException e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"NullPointerException In decrypt():"+e.toString());
			return null;
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Crypto Class :"+"Exception In decrypt():"+e.toString());
			return null;
		}	
		
	}

	
	public static KeyGenerator getKeyGenerator(String algorithm)
	{
		
			Log.i("=== in KeyGenerator ====","1111");
			try
			{
				Log.i("=== in KeyGenerator ====","2222");
				KeyGenerator keyGenerator=KeyGenerator.getInstance(algorithm);//,"BC");
				Log.i("=== in KeyGenerator ====", "3333");
				keyGenerator.init(128);
				//keyGenerator.init(Cipher.WRAP_MODE);
				return keyGenerator;
			}
			catch(NoSuchAlgorithmException e)
			{
				Log.i("== LoginActivity.getKey():NoSuchAlgorithmException ==",e.getMessage());
				return null;
			}
			
			catch(InvalidParameterException e)
			{
				Log.i("== LoginActivity.getKey():InvalidParameterException(chk init(-))==",e.getMessage());
				return null;
			}
			catch(Exception e)
			{
				Log.i("== LoginActivity.getKey():Exception ==",e.getMessage());
				return null;
			}
	}//end getKeyGenerator	
	

}//end class
