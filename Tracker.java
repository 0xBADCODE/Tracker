/* Tracker 1.0
 * Written by Xeon June 2014
 */

package tk.hackerrepublic.tracker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;

public class Tracker extends Service {
	public Tracker() {}
	
	protected PowerManager.WakeLock mWakeLock;

	private Socket socket;
	private static final int SERVERPORT = 4444;
	private static final String SERVER_IP = "81.174.129.87";
	private OutputStream nos;
	
	private Time time = new Time();
	private DecimalFormat df = new DecimalFormat("#00.0##");
	
	protected static AlgoAES aesenc;
	protected static AsymAlgo enc;
	protected static GeoLocate geolocate;
	protected boolean trackerStarted = false;
	
	private byte[] databytes;
	
	private String createPacket(){
		
		time.set((long) geolocate.getTimestamp());
		
		String	latitude = geolocate.getStrLatitude(),
				longitude = geolocate.getStrLongitude();
		
		double	altitude = geolocate.getAltitude();
		
		float 	accuracy = geolocate.getAccuracy(),
				bearing = geolocate.getBearing(),
				speed = geolocate.getSpeed();

		return		time.format2445()
				+ 	"," + latitude
				+ 	"," + longitude
				+	"," + Float.toString(accuracy)
				+	"," + df.format(altitude)
				+	"," + Float.toString(speed)
				+	"," + Float.toString(bearing)
				+	",";
	}
	
	private void setupNetwork(){
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
			socket = new Socket(serverAddr, SERVERPORT);
			socket.setKeepAlive(true);
			nos = socket.getOutputStream();
			} catch (UnknownHostException e0) {
				e0.printStackTrace();
				} catch (IOException e0) {
					e0.printStackTrace();
					}
	}
	
	Thread datatrd = new Thread(new Runnable(){
		
		@Override
		public void run(){
			setupNetwork();
			while(!Thread.interrupted()){
				try {
					synchronized(this){
						this.wait(3000);
						}
					}
					catch(InterruptedException e1){
						e1.printStackTrace();
					}
				if (trackerStarted)
					SendData(createPacket());
				else geolocate.getLoc();
			}
		}
	});
		
	private void SendData(String datastr) {
		
		try {
			databytes = datastr.getBytes("UTF-8");
			
//			databytes = enc.encrypt(databytes);  // RSA encrypt
//			databytes = enc.decrypt(databytes); // RSA

//			databytes = aesenc.encrypt(databytes);  // AES encrypt
//			databytes = aesenc.decrypt(databytes); // AES
			
			databytes = Base64.encode(databytes, Base64.NO_PADDING); // BASE64 encode

		} catch (UnsupportedEncodingException e1) {
			Log.d("DataStream: ", "Encoding error");
			e1.printStackTrace();
		}
		
		try {
            if (socket.isConnected()) {
                Log.d("DataStream: ", "SendData: " + datastr);
                nos.write(databytes);
                nos.flush();
                
            } else {
                Log.d("DataStream: ", "Socket is closed");
            }
        } catch (Exception e) {
            Log.d("DataStream: ", "Data send failed. Exception.");
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
            setupNetwork();
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		geolocate = new GeoLocate(this);
		
		enc = new AsymAlgo();
		enc.getKey();
	//	enc.generateKeys(); //generate RSA key pair test
		
		aesenc = new AlgoAES();
		aesenc.getKey();
	//	aesenc.generateKey(); //generate AES key test
		
        Log.d("Tracker: ", "Initializing geolocation");
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mWakeLock");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (geolocate.canGetLocation) {
			trackerStarted = true;
        	Log.d("Tracker: ", "Can get location: Ready");
        	this.mWakeLock.acquire();
		}
        else {
        	Log.d("Tracker: ", "Failed to initialize geolocation");
        }
		datatrd.start();
		Log.d("Tracker: ", "Service started.");
	}

	@Override
	public void onDestroy() {
		geolocate.stopUsingGPS();
		Log.d("Tracker: ", "Service stopped.");
		if (mWakeLock.isHeld())
			this.mWakeLock.release();
		datatrd.interrupt();
		
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
	}
}