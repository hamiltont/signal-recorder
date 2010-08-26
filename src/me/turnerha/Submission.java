package me.turnerha;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.wifi.WifiInfo;

public class Submission {
	private JSONObject me_;

	public Submission(Location l, WifiInfo wi) {
		me_ = new JSONObject();
		JSONObject loc = serialize(l);
		JSONObject info = serialize(wi);

		try {
			me_.put("location", loc);
			me_.put("WifiInfo", info);
		} catch (JSONException e) {
		}
	}

	public void submit() {
		Log.i(Log.mainTag, "Submitting update");
		Log.i(Log.mainTag, me_.toString());
	}

	private static JSONObject serialize(WifiInfo info) {
		JSONObject jLoc = new JSONObject();
		try {
			jLoc.put("rssi", info.getRssi());
			jLoc.put("speedInMbps", info.getLinkSpeed());
		} catch (JSONException jse) {
		}

		return jLoc;
	}

	private static JSONObject serialize(Location location) {
		JSONObject jLoc = new JSONObject();
		try {
			jLoc.put("accuracy", location.getAccuracy());

			jLoc.put("latitude", location.getLatitude());
			jLoc.put("longitude", location.getLongitude());
			jLoc.put("provider", location.getProvider());
			jLoc.put("time", location.getTime());
		} catch (JSONException jse) {
		}

		return jLoc;
	}
}
