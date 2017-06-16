package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import web.*;
import log.*;

public class HTMLParser {
	
	private static String urlPrefix = "http://www.azlyrics.com";
	private static String songDataBeginningIdentifier = "<!-- start of song list -->";
	private static String songDataEndIdentifier = "<a id=\"";
	private static String endOfAllData = "<script type=\"text/javascript\">";
	private static String beginLyricsIdentifier = "Sorry about that";	// unique string present in comment before song data payload in HTML
	private static String endLyricsIdentifier = "<br><br>";
	private static String htmlRegex = "\\<.*?\\>";	// starts with "<", ends with ">"
	
	static ExceptionLogger log = new ExceptionLogger("/src/log/log.txt");
	
	// Takes a line of raw HTML like "<a href="../lyrics/kendricklamar/determined.html" target="_blank">Determined</a><br>"
	// Returns the full URL identified within the HTML string ("http://www.azlyrics.com/lyrics/kendricklamar/determined.html")
	public static String parseHTMLForURL(String rawHTML) throws IOException {
		char[] arr = rawHTML.toCharArray();
		int begin = 0, end;

		// If the text being passed starts with "<a id="", return null
		if (rawHTML.contains(songDataEndIdentifier))
			return null;

		try {

			// Increment pointer until it points to the 'l' in 'lyrics/artist'
			while (arr[begin++] != '/');
			end = begin;
			// Increment second pointer until it points to the end of the URL
			while (arr[++end] != '\"');

		} catch (ArrayIndexOutOfBoundsException arrex) {
			log.logExceptionToFile(arrex);
			return null;
		}

		// Return the fully-formed URL as a string
		return urlPrefix + "/" + rawHTML.substring(begin, end);
	}

	// Given a URL like "http://www.azlyrics.com/lyrics/kendricklamar/kingkunta.html", read the page's HTML,
	// extract the song lyrics, and write these song lyrics to concatenatedOutputFile
	public static void parseSongURLForLyrics(String url) throws IOException {

		BufferedReader read = null;
		FileWriter write = null;
		String line = null;
		File temp_buffer = null;
		File concatenatedOutputFile;
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");

		try {

			// Create a temporary file to store/manipulate HTML payload from url
			temp_buffer = new File("src/data/" + uuid + ".txt");	
			temp_buffer.createNewFile();
			
			concatenatedOutputFile = new File(URLReader.getArtist(url));
			if (! concatenatedOutputFile.exists())
				concatenatedOutputFile.createNewFile();

			read = new BufferedReader(new FileReader(temp_buffer)); // read from the temp file with raw HTML data of a single song
			write = new FileWriter(concatenatedOutputFile, true);	// append mode is TRUE

			URLReader.dumpRawHTMLToFile(url, temp_buffer.getAbsolutePath());	// read the HTML from url, write to temp_buffer

			// Skip down the file until you reach the line directly before where the lyrics begin
			while (! (line = read.readLine()).contains(beginLyricsIdentifier));

			// BufferedReader now points to the first line of lyrics; read until we've read all the lyrics
			while (! (line = read.readLine()).equals(endLyricsIdentifier)) {
				write.write(line.replaceAll(htmlRegex, System.lineSeparator()));	// strip out any HTML tags, just write the lyrics
			}

		} catch (Exception ex) {
			log.logExceptionToFile(ex);
		}

		finally {
			temp_buffer.deleteOnExit();
			read.close();
			write.close();
		}

	}
	
	// Takes HTML output from main artist page to generate list of all child URLs
	public static ArrayList<String> parseMainPageForChildURLs(String pathToPayload) throws IOException {

		BufferedReader readPayload = null;
		String line = null;
		ArrayList<String> urlList = new ArrayList<String>();

		try {

			readPayload = new BufferedReader(new FileReader(pathToPayload));

			// For each line of the HTML payload retrieved from the artist's main page listing all albums/songs
			while ((line = readPayload.readLine()) != null) {

				// Keep reading the file until we hit the identifier indicating the URLs are on the next line
				if (line.length() >= songDataBeginningIdentifier.length()
						&& line.substring(0, songDataBeginningIdentifier.length()).equals(songDataBeginningIdentifier)) {

					// Now skip three lines to get to the URLs for each song
					for (int i = 0; i < 3; i++) {
						readPayload.readLine();
					}

					// Now, each line until songDataEndIdentifier contains the URL of each song from a given album
					while (! (line = readPayload.readLine()).equals(endOfAllData)) {

						// Ensure that the line of text is not whitespace or wrong type of HTML
						if (!line.equals("") && parseHTMLForURL(line) != null)
							urlList.add(parseHTMLForURL(line));		
					}
				}

			}

		} catch (Exception ex) {
			log.logExceptionToFile(ex);
		}

		return urlList;
	}

}
