package tk.hackerrepublic.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	protected static TextView gpsText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("GU: ", "Loading handler...");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gpsText = (TextView) this.findViewById(R.id.textView);
		gpsText.setText("Booting...");
	}
	
	public void startService(View view) {
		Log.d("GU: ", "Starting tracker...");
		startService(new Intent(this, Tracker.class));
		gpsText.setText("Tracker started.");
	}

	public void stopService(View view) {
		Log.d("GU: ", "Stopping tracker...");
		stopService(new Intent(this, Tracker.class));
		gpsText.setText("Tracker stopped.");
	}
}

