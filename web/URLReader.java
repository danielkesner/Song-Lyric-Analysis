package web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import log.*;
import parsers.*;

public class URLReader {
	
	static ExceptionLogger log = new ExceptionLogger("/src/log/log.txt");
	static boolean caughtException = false;

	// Generic method that dumps the raw HTML output of a URL to a file specified by pathToOutputFile
	public static void dumpRawHTMLToFile(String url, String pathToOutputFile) throws IOException {

		Scanner read = null;
		String data = null;
		FileWriter fw = null;

		try {

			read = new Scanner(new URL(url).openStream(), "UTF-8");
			data = read.useDelimiter("\\A").next();
			fw = new FileWriter(new File(pathToOutputFile));

			fw.write(data);

		} catch (Exception ex) {

			log.logExceptionToFile(ex);
			caughtException = true;

		} finally {
			read.close();
			fw.close();
		}

	}

	// Given a URL of the form "http://www.azlyrics.com/k/kendricklamar.html", return the Artist
	public static String getArtist(String artistURL) {
		int start = artistURL.length() - 1;
		char[] arr = artistURL.toCharArray();

		while (arr[--start] != '/');	// Decrement start until we hit '/'

		return artistURL.substring(start+1, artistURL.length()-5);
	}

	// Given a url to the artist's main page (i.e. http://www.azlyrics.com/k/kendricklamar.html)
	// get the links to all songs listed, and for each song populate concatenatedOutputFile with those lyrics
	public static void writeAllSongsToFile(String artistURL) throws IOException {

		String artistPath = "src/data/" + getArtist(artistURL) + "_final.txt";
		long start, stop;
		start = System.currentTimeMillis();

		// Grab the raw HTML from artist's main page, store it in ArrayList
		dumpRawHTMLToFile(artistURL, artistPath);
		ArrayList<String> urlList = HTMLParser.parseMainPageForChildURLs(artistPath);

		for (String songURL : urlList) {
			HTMLParser.parseSongURLForLyrics(songURL);
		}

		stop = System.currentTimeMillis();
		System.out.println("Finished writing " + urlList.size() + " songs from artist " + getArtist(artistURL) + " in " 
				+ ((stop-start)/1000) + " seconds.");
		
		if (caughtException)
			System.out.println("NOTE: At least one exception was caught while data was read and processed from the web; it's likely that at least some"
					+ " data was not correctly processed. Check /src/log/log.txt for more information.");

	}

	// Syntax:
	// [--writeAll | -w] -u <url-to-main-artist-page> 
	// java URLReader -w -u http://www.azlyrics.com/k/kendricklamar.html
	public static void main(String[] args) throws Exception {

		if (args.length < 1 || args.length > 3) {
			System.err.println("ERROR: Invalid number of arguments. Check syntax and try again.");
			System.exit(-1);
		}
		
		if (args[0].equals("--writeAll") || args[0].equals("-w")) {
			writeAllSongsToFile(args[2]);
		}
		
	//	writeAllSongsToFile("http://www.azlyrics.com/k/kendricklamar.html");

	}

}
