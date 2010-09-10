package me.turnerha.db;

import java.util.Random;

import me.turnerha.Log;
import android.content.Context;

public class TestPopulateCellQueue {

	public void run(Context context) {
		Random r = new Random(1234);

		Log.i("Starting population of queue");
		for (int i = 0; i < 100; i++) {
			CellularSignalRecord csr = new CellularSignalRecord(r.nextInt(100),
					r.nextDouble(), r.nextDouble(), r.nextDouble(), -1);
			CellularUploadQueue.enqueue(csr, context);
		}
		Log.i("Done with population of queue");

		Log.i("Size is", CellularUploadQueue.size(context));
	}
}
