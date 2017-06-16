package parsers;

public class ArgumentParser {
	
	// [--writeAll | -w] -u <url-to-main-artist-page> 
	public static String getURL(String[] args) {
		if (args[1].equals("-u"))
			return args[2];
		return null;
	}

}
