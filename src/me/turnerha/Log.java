package me.turnerha;

public class Log {
	public static final String mainTag = "signal-reporter";
	private static StringBuilder builder_ = new StringBuilder();

	// TODO - add in the ability to turn logging off globally, and the ability
	// to check these values

	public static void d(String tag, String message) {
		String msg = buildMessage(tag, message);
		android.util.Log.d(mainTag, msg);
	}

	public static void e(String tag, String message) {
		String msg = buildMessage(tag, message);
		android.util.Log.e(mainTag, msg);
	}

	public static void i(String tag, String message) {
		String msg = buildMessage(tag, message);
		android.util.Log.i(mainTag, msg);
	}

	public static void v(String tag, String message) {
		String msg = buildMessage(tag, message);
		android.util.Log.v(mainTag, msg);
	}

	public static void w(String tag, String message) {
		String msg = buildMessage(tag, message);
		android.util.Log.w(mainTag, msg);
	}

	private static String buildMessage(String tag, String msg) {
		final StringBuilder s = new StringBuilder(tag);
		s.append(": ").append(msg);
		return s.toString();
	}
}
