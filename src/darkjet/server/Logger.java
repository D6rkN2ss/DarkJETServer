package darkjet.server;


public final class Logger {
	private Logger() {
		
	}
	
	public final static int NO = 0;
	public final static int FATAL = 1;
	public final static int ERROR = 2;
	public final static int WARNING = 3;
	public final static int INFO = 4;
	public final static int VERBOSE = 5;
	public final static int DEBUG = 6;
		
	public static final String getLevelName(int level) {
		switch( level ) {
			case 1:
				return "FATAL";
			case 2:
				return "ERROR";
			case 3:
				return "WARNING";
			case 4:
				return "INFO";
			case 5:
				return "VERBOSE";
			case 6:
				return "DEBUG";
			default:
				return "UNKNOWN";
		}
	}
	
	public static int LEVEL = DEBUG;

	public static final String getNameofClass(String s) {
		return s.substring(s.lastIndexOf(".") + 1, s.length());
	}
	
	public static final String getCaller() {
		return getNameofClass( Thread.currentThread().getStackTrace()[3].getClassName() );
	}
	
	public static final void print(int level, String s, Object... format) {
		if( LEVEL < level ) {
			return;
		}
		System.out.println( "[" + getLevelName(level) + "] [" + getCaller() + "] " + String.format(s, format) );
	}
}