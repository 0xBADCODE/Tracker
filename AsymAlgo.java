package tk.hackerrepublic.tracker;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.util.Base64;
import android.util.Log;

public class AsymAlgo {
	
	public AsymAlgo() {}

	private final String TAG = "AsymAlgo: ";
	
	private String sKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA0/WQCku1UwjvQheEL5+i"
						+ "SieG6Vt7yVSvo5yI+L7/QTffJvJmKVQusJN98eVM489TPXCb9VgT7KRFqnmKdF3D"
						+ "ccLqXGHc4eKylKfWm2SnjvmzBHj27uFcZXwHMAVMD3PbphrqqPJ8PIVKt2JXK+K6"
						+ "RUA+F3Pv6rYp3I0aClj4iZNxz8cR4ilQ63xWnLg57SVoIaE8q3YQ69JeTgmq9MZ8"
						+ "9Z0JLLLUlABisDLLOs/hNaRYfG9ldPhv63v8/5DdbRF9w/i/UOlp9lsFDdWbhDnc"
						+ "a4nBhsUnpAzMCdzKs5OTLIMGxKSB6HFqTz9CfrgOAtXRx3Klv8wzQiH4QGhpwBmm"
						+ "YE4xOTw9Bu2WyzxmFkmJuAT1L97hKe1QC1Go+MssECxmmEc+D2LE/UuGzDuk9vZb"
						+ "mZgk13RTXE6nfNNEOSXBPLQovxbMTS3hLe+BB5X1gSzHzA2GQNzPz08VEeF6p2uo"
						+ "7udhHUJKV4ysAKt3GbdCmrXOrD6FzeYRPWVaftNgiGO3jwPf1ahKx23ZlOwUbEnU"
						+ "ucLyWKCWaWaK5EyJ2uSkxRyLhKzgchw5lggUGhAsVR9CBsUq0GQKp/Az5H6mMw2y"
	                    + "S9d7loeWQKXAw86hHB56QmW1TgoRgI/tA3e/OVa2auN1GrNZnvce5gILVuCGpQ60"
	                    + "e6HuMgU0ien10v3mCzp+YPsCAwEAAQ==";
	
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;

    private byte[] 	encryptedBytes = null,
    				decryptedBytes = null;
    
    protected void getKey(){
        try{
        	Log.d(TAG, "Setting up RSA encryption key");
        	byte[] bKey = sKey.getBytes("UTF-8");
        	
            bKey = Base64.decode(bKey, Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(bKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(X509publicKey);
            
        //	Log.d(TAG + "PUBLIC KEY: ", publicKey.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    protected void generateKeys() {

	    // Generate key pair for 1024-bit RSA encryption
	    Log.d(TAG, "Generating RSA keypair");
	    try {
	        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	        kpg.initialize(1024);
	        KeyPair kp = kpg.genKeyPair();
	        publicKey = kp.getPublic();
	        privateKey = kp.getPrivate();
	    //	Log.d(TAG + "PUBLIC KEY: ", publicKey.toString());
	    //	Log.d(TAG + "PRIVATE KEY: ", privateKey.toString());
	    } catch (Exception e) {
	        Log.d(TAG, "RSA keypair error");
	        e.printStackTrace();
	    }
    }

    protected byte[] encrypt(byte[] data){
	    try {
	        Cipher c = Cipher.getInstance("RSA/ECB/NoPadding");
	        c.init(Cipher.ENCRYPT_MODE, publicKey);
	        encryptedBytes = c.doFinal(data);
	    } catch (Exception e) {
	        Log.d(TAG, "RSA encryption error");
	        e.printStackTrace();
	    }
	    return encryptedBytes;
    }
    
    protected byte[] decrypt(byte[] data){
	    try {
	        Cipher c = Cipher.getInstance("RSA/ECB/NoPadding");
	        c.init(Cipher.DECRYPT_MODE, privateKey);
	        decryptedBytes = c.doFinal(data);
	    } catch (Exception e) {
	        Log.d(TAG, "RSA decryption error");
	        e.printStackTrace();
	    }
	    return decryptedBytes;
    }
}