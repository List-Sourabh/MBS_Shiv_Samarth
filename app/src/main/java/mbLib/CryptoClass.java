package mbLib;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;

public class CryptoClass 
{
	public static Map<String, Object> Function1() throws Exception 
	{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("private", privateKey);
        keys.put("public", publicKey);
        return keys;
    }
	
	public static String Function2() throws Exception 
	{
		try
		{
			KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			Key keyStr= keyGenerator.generateKey();
			return new String(Base64.encodeBase64(keyStr.getEncoded()));
		}
		catch(NoSuchAlgorithmException e)
		{
			return null;
		}
		catch(InvalidParameterException e)
		{
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static SecretKeySpec getKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            byte[] key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static String Function3(String plainText, PrivateKey privateKey) throws Exception 
    {
    	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return new String(Base64.encodeBase64(cipher.doFinal(plainText.getBytes())));
    }
	
	public static String Function4(String encryptedText, PrivateKey privateKey) throws Exception 
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(encryptedText.getBytes())));
    }

    
    
    public static String Function5(String strToEncrypt, SecretKeySpec secretKey)
	{
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return new String(Base64.encodeBase64(cipher.doFinal(strToEncrypt.getBytes())));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
	
	public static String Function6(String strToDecrypt,SecretKeySpec secretKey)
	{
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt.getBytes())));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
            return("{\"RESPCODE\":\"1\",\"RESPDESC\":\"Server Not Found\"}");
        }
       // return null;
    }
    public static PrivateKey getPrivateKey() {
        try {
            String keyStr="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";
            byte key[]=keyStr.getBytes();
            byte[] byteKey = Base64.decodeBase64(key);
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
            return privKey;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
