package tk.hackerrepublic.tracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GeoLocate extends Service implements LocationListener {
	
	private final Context mContext;

    protected GeoLocate(Context context) {
        this.mContext = context;
        
        getLoc();
    }

	protected boolean 	isGPSEnabled = false,
						isNetworkEnabled = false,
						canGetLocation = false,
						isRunning = true;
 
    Location location = null;
    private double 	latitude,
    				longitude,
    				timestamp,
    				altitude;
    private float 	accuracy,
    				bearing,
    				speed;
    private String 	strLatitude,
    				strLongitude;
    
    private static final long 	MIN_DISTANCE = 5,
    							MIN_TIME = 3000;
 
    private LocationManager locationManager;
    
    private Location getLocation() {
        try {
        	locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                this.canGetLocation = false;
            } else {
            	this.canGetLocation = true;
            	//try network first
            	if (isNetworkEnabled) {
            //		Log.d("Network status: ", "Network enabled");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME,
                            MIN_DISTANCE, this);
                    if (locationManager != null) {
              //      	Log.d("Network status: ", "Updating...");
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                        	this.latitude = location.getLatitude();
                            this.longitude = location.getLongitude();
                            this.timestamp = location.getTime();
                            if (location.hasAccuracy())
                            	this.accuracy = location.getAccuracy();
                            this.strLatitude = Location.convert(latitude, 2);
                            this.strLongitude = Location.convert(longitude, 2);
                       //     Log.d("Network location: ", "[" + strLatitude + "|" + strLongitude + "]");
                        }
                    }
            	} else Log.d("Network status: ", "Network disabled");
             	if (isGPSEnabled) {
             // 	Log.d("GPS status: ", "GPS enabled");
                    location = null;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME,
                                MIN_DISTANCE, this);
                        if (locationManager != null) {
                    //  	Log.d("GPS status: ", "Updating...");
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                this.latitude = location.getLatitude();
                                this.longitude = location.getLongitude();
                                this.timestamp = location.getTime();
                                if (location.hasAccuracy())
                                	this.accuracy = location.getAccuracy();
                                if (location.hasBearing())
                                	this.bearing = location.getBearing();
                                if (location.hasAltitude())
                                	this.altitude = location.getAltitude();
                                if (location.hasSpeed())
                                	this.speed = location.getSpeed();
                                this.strLatitude = Location.convert(latitude, 2);
                                this.strLongitude = Location.convert(longitude, 2);
                         //       Log.d("GPS location: ", "[" + strLatitude + "|" + strLongitude + "]");
                            }
                        }
                    }
                } else Log.d("GPS status: ", "GPS disabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
    
    protected void getLoc(){
		getLocation();
    }
    
    protected double getLatitude(){
		if(canGetLocation)
            return this.latitude;
		else return 0;
    }
 
    protected double getLongitude(){
        if(canGetLocation)
            return this.longitude;
        else return 0;
    }
    
    protected double getTimestamp(){
    	if(canGetLocation)
    		return this.timestamp;
    	else return 0;
    }
    
    protected float getAccuracy(){
    	if(canGetLocation)
    		return this.accuracy;
    	else return 0;
    }
    
    protected float getBearing(){
    	if(canGetLocation)
    		return this.bearing;
    	else return 0;
    }
    
    protected float getSpeed(){
    	if(canGetLocation)
    		return this.speed;
    	else return 0;
    }
    
    protected double getAltitude(){
    	if(canGetLocation)
    		return this.altitude;
    	else return 0;
    }  
  	
    protected String getStrLatitude(){
    	if(canGetLocation)
    		return this.strLatitude;
    	else return null;
    }
   	
    protected String getStrLongitude(){
    	if(canGetLocation)
    		return this.strLongitude;
    	else return null;
    }
   	
    protected void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GeoLocate.this);
        }
        Log.d("GPS status: ", "Stopped");
    }
    
    @Override
	public void onLocationChanged(Location location) {
    	getLoc();
    }

    @Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onProviderDisabled(String arg0) {	
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}
