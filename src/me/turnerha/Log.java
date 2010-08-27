package me.turnerha;

public class Log {
	public static final String mainTag = "signal-reporter";

	private static final boolean LOG_CLASS_NAME = true;
	private static final boolean LOG_METHOD_NAME = false;
	private static final boolean LOG_LINE_NUMBER = false;

	private static final boolean LOG_V = true;
	private static final boolean LOG_I = true;
	private static final boolean LOG_D = true;
	private static final boolean LOG_W = true;
	private static final boolean LOG_E = true;

	@SuppressWarnings("all")
	public static void d(Object... message) {
		if (LOG_D == false)
			return;

		final String msg = buildMessage(message);
		android.util.Log.d(mainTag, msg);
	}

	@SuppressWarnings("all")
	public static void e(Object... message) {
		if (LOG_E == false)
			return;

		final String msg = buildMessage(message);
		android.util.Log.e(mainTag, msg);
	}

	@SuppressWarnings("all")
	public static void i(Object... message) {
		if (LOG_I == false)
			return;

		final String msg = buildMessage(message);
		android.util.Log.i(mainTag, msg);
	}

	@SuppressWarnings("all")
	public static void v(Object... message) {
		if (LOG_V == false)
			return;

		final String msg = buildMessage(message);
		android.util.Log.v(mainTag, msg);
	}

	@SuppressWarnings("all")
	public static void w(Object... message) {
		if (LOG_W == false)
			return;

		final String msg = buildMessage(message);
		android.util.Log.w(mainTag, msg);
	}

	private static String buildMessage(Object... msg) {
		final StringBuilder s = new StringBuilder();

		if (LOG_CLASS_NAME | LOG_LINE_NUMBER | LOG_METHOD_NAME) {
			// Append the class name, the method name, and the line
			StackTraceElement[] steArray = Thread.currentThread()
					.getStackTrace();

			// 0 and 1 are getThreadStackTrace and getStackTrace, respectively
			// 2 is Log.buildMessage
			// 3 is Log.i/w/d/e/v
			// 4 is therefore the stack trace element for class that called Log
			StackTraceElement callerSTE = steArray[4];
			if (LOG_CLASS_NAME) {
				String className = callerSTE.getClassName();
				className = className.substring(className.lastIndexOf('.') + 1);
				s.append(className).append(": ");
			}
			if (LOG_METHOD_NAME)
				s.append(callerSTE.getMethodName()).append(": ");
			if (LOG_LINE_NUMBER)
				s.append("Line ").append(callerSTE.getLineNumber())
						.append(": ");
		}

		for (Object str : msg)
			s.append(str.toString()).append(' ');

		return s.toString();
	}
}
