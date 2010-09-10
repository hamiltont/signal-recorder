package me.turnerha;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class CellularUploadService extends Service {

	private CellularUploadTask backgroundTask_;

	public void onCreate() {
		super.onCreate();
		
		// Open Database here
		backgroundTask_ = new CellularUploadTask();

		Log.d("Creating the CellularUploadTask");
	}

	// TODO - There are multiple potential ways to handle the service being
	// killed while it is started. Read the docs on START_STICKY and friends and
	// see which method makes the most sense here
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// Ensure a single instance of the upload thread has been created and
		// started
		if (backgroundTask_.getStatus() == AsyncTask.Status.PENDING) {
			Log.d("Starting the CellularUploadTask");
			backgroundTask_.execute(this.getApplicationContext());
		}

		return 0;
	}

	public void onDestroy() {
		super.onDestroy();

		// Note that this is likely being called in the main event thread from a
		// call to stopService. We should wrap things up as fast as possible. I
		// am not sure how to abort a currently running internet request
		backgroundTask_.cancel(false);
		Log.d("Cancelling CellularUploadTask (no interruption allowed)");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
