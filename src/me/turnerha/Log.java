package me.turnerha;

public class Log {
	public static final String mainTag = "signal-reporter";

	private static final boolean LOG_CLASS_NAME = true;
	private static final boolean LOG_METHOD_NAME = true;
	private static final boolean LOG_LINE_NUMBER = true;

	// TODO - add in the ability to turn logging off globally, and the ability
	// to check these value

	public static void d(Object... message) {
		final String msg = buildMessage(message);
		android.util.Log.d(mainTag, msg);
	}

	public static void e(Object... message) {
		final String msg = buildMessage(message);
		android.util.Log.e(mainTag, msg);
	}

	public static void i(Object... message) {
		final String msg = buildMessage(message);
		android.util.Log.i(mainTag, msg);
	}

	public static void v(Object... message) {
		final String msg = buildMessage(message);
		android.util.Log.v(mainTag, msg);
	}

	public static void w(Object... message) {
		final String msg = buildMessage(message);
		android.util.Log.w(mainTag, msg);
	}

	private static String buildMessage(Object... msg) {
		final StringBuilder s = new StringBuilder();

		if (LOG_CLASS_NAME | LOG_LINE_NUMBER | LOG_METHOD_NAME) {
			// Append the class name, the method name, and the line
			StackTraceElement[] steArray = Thread.currentThread()
					.getStackTrace();

			/*
			 * 
			 * I am leaving this commented out for now to show that it would be
			 * a cool way to do this ;) However, it is a lot faster to just know
			 * the correct index into the steArray and to retrieve the data from
			 * there. If someone changes the Log class, however, then they may
			 * need to update this method index. I just wanted to get this into
			 * revision control ;)
			 * 
			 * // The first two trace elements are always getStackTrace and 
			 * // getThreadStackTrace, so we just skip them
			 * 
			 * final String logClassName = Log.class.getCanonicalName(); 
			 * for (int i = 2;; i++) 
			 * { 
			 * 		StackTraceElement ste = steArray[i]; 
			 * 		String className = ste.getClassName();
			 * 
			 * 		if (false == logClassName.equalsIgnoreCase(className)) 
			 * 		{ 
			 * 			// This is the ste from the class that called Log 
			 * 			// Append the class name, the method name, and the line here 
			 * 			
			 * 			break; 
			 * 		} 
			 * }
			 */

			// The first two trace elements are always getStackTrace and
			// getThreadStackTrace, so we just skip them.
			// The third element is the call to Log.buildMessage, and the
			// fourth is the call to Log.i/w/d/e/v.
			// The fifth element is therefore the stack trace element for
			// class that called the logger. Zero-indexed array, so 4th ;)
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

	/**
	 * ==================================== 
	 * Stuff below is for compatibility purposes so someone does not have to change their entire code base to use
	 * this. Uncomment to use - I don't like it because in a project set up to use the above system (e.g Log.i("message") )
	 * having these methods below results in a method call to Log.i(String, Object...), which then calls thru. This is 
	 * annoying. If you uncomment this then you must take the line "StackTraceElement callerSTE = steArray[4];" to say 5
	 * instead of 4, because this call adds one to the count
	 * ====================================
	 */
/*
	public static void d(String tag, Object... message) {
		d((Object) tag, message);
	}

	public static void e(String tag, Object... message) {
		e((Object) tag, message);
	}

	public static void i(String tag, Object... message) {
		i((Object) tag, message);
	}

	public static void v(String tag, Object... message) {
		v((Object) tag, message);
	}

	public static void w(String tag, Object... message) {
		w((Object) tag, message);
	}
*/
}
