package log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionLogger {
	
	private String pathToExceptionFile = null;

	public ExceptionLogger(String pathToFile) {
		pathToExceptionFile = pathToFile;
	}
	
	// Writes stack trace from an exception to a file
	// FAILS if pathToExceptionFile has not been defined (i.e. if a ExceptionLogger object has not been instantiated)
	public void logExceptionToFile(Exception e) throws IOException {
		
		if (null == pathToExceptionFile)
			throw new RuntimeException("ERROR: ExceptionLogger.logExceptionToFile() called before instantiating ExceptionLogger object.");
		
		FileWriter fw = new FileWriter(pathToExceptionFile, true);
		PrintWriter pw = new PrintWriter(fw);
			
		e.printStackTrace(pw);
		pw.write(System.lineSeparator());	
	}
	
}
