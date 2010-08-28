package me.turnerha;

import me.turnerha.db.CellularSignalRecord;
import me.turnerha.db.CellularUploadQueue;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;

public class CellularUploadTask extends AsyncTask<Context, Integer, Void> {

	/**
	 * Uploads objects from the queue one by one. After every upload this will
	 * check if it has been canceled and stop itself if it has. When faced with
	 * a choice, this method will attempt to use as few resources as possible,
	 * leaning towards stopping the upload if required.
	 */
	@Override
	protected Void doInBackground(Context... params) {
		Context context = params[0];
		if (context == null) {
			Log.e("Context parameter cannot be null! Aborting . . .");
			return null;
		}

		Debug.startMethodTracing("signal-recorder");
		android.util.Log.i("signal-recorder", "Starting trace");
		CellularSignalRecord record = null;
		while (null != (record = CellularUploadQueue.getNext(context))) {
			if (isCancelled()) {
				Log.d("Cancel request detected. Cancelling");
				Debug.stopMethodTracing();
				return null;
			}

			boolean wasUploaded = uploadMeasurement(record);
			if (wasUploaded)
				CellularUploadQueue.remove(context, record);
		}
		
		Debug.stopMethodTracing();
		android.util.Log.i("signal-recorder", "Finished trace");

		Log.i("Finished uploading all CellularSignalRecord records");
		return null;
	}

	/**
	 * Uploads a Cell measurement. Mocked right now
	 * 
	 * @return true if measurement was uploaded, false otherwise
	 */
	private boolean uploadMeasurement(CellularSignalRecord record) {
		Log.v("Uploading record:", record);

		//if (Math.random() > 0.9d) {
		//	Log.v("Upload failed");
		//	return false;
		//}

		Log.v("Upload succeeded");
		return true;
	}
}
