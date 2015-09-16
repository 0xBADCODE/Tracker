package tk.hackerrepublic.tracker;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class AlgoAES {
	
	public AlgoAES() {}
	
	private final String TAG = "AlgoAES: ";
	
	private final String sKey = "ZDI1OTY3ZDhlZWI2YTJmMTBiMzNlNWU3ZDI5ZDgyNDNiOWZjZWI4ZA==";
	private byte[] ivBytes = { 0x4c, 0x5b, 0x58, 0x5f, 0x59, 0x4a, 0x48, 0x40, 
								0x4e, 0x59, 0x42, 0x5d, 0x48, 0x44, 0x4f, 0x4e };

	private SecretKey secretKey = null;
	
	private byte[] encryptedBytes = null;
    private byte[] decryptedBytes = null;
	
    protected void generateKey() {
    	
        // Generate secretKey for 128-bit AES encryption
        try {
        	Log.d(TAG, "Generating AES encryption key");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(String.valueOf(System.currentTimeMillis()).getBytes("UTF-8"));
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            secretKey = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        //	Log.d(TAG + "AES KEY: ", new String(secretKey.getEncoded()));
        } catch (Exception e) {
            Log.d(TAG, "Error generating key");
            e.printStackTrace();
        }
    }

    protected void getKey(){
        try{
        	Log.d(TAG, "Setting up AES encryption key");
        	byte[] bKey = sKey.getBytes("UTF-8");

        	bKey = Base64.decode(bKey, Base64.DEFAULT);
        //	Log.d(TAG + "sKey: ", new String(bKey));
        	
        	MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        	bKey = sha1.digest(bKey);
        	bKey = Arrays.copyOf(bKey, 16);
        	
        	secretKey = new SecretKeySpec(bKey, "AES");
            
        //	Log.d(TAG + "SHA1: ", new String(bKey));
        //	Log.d(TAG + "AES KEY: ", new String(secretKey.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected byte[] encrypt(byte[] data) {
    	
        try {
        	IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            encryptedBytes = c.doFinal(data);
        } catch (Exception e) {
            Log.d(TAG, "AES encryption error");
            e.printStackTrace();
        }
        
        return encryptedBytes;
    }

    protected byte[] decrypt(byte[] data) {
        
        try {
        	IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, iv);
            decryptedBytes = c.doFinal(data);
        } catch (Exception e) {
            Log.d(TAG, "AES decryption error");
            e.printStackTrace();
        }
        
        return decryptedBytes;
    }
}
