package analysis;

/******
 * DataPreprocessor.java: Massage the data into a form that we
 * can actually use to draw conclusions. Grab it from a text
 * file, copy it to a tree, and then analyze the lyrics
 * in Analysis.java.
 * 
 * Methods:
 * 
 * createMap(): Take the text file and transform it to a 
 * HashMap of key,val pairs where key is the word and value is 
 * the number of occurrences. Returns the map when done.
 * 
 * sortValues(unsortedMap): Sort the unsorted HashMap
 * by value, return a HashMap<String,Integer> that is sorted
 * where the first mapping is the one with the highest value.
 * 
 * trimArray(arr[]): Given the output array of n most
 * common words, remove all "boring" words. 
 * 
 * void printArray: Shorthand method to print contents of Str[].
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class DataPreprocessor {

	protected static HashMap<String, Integer> wordOccurences = new HashMap<String, Integer>();
	protected static HashSet<String> filteredWords = new HashSet<String>(25);
	private final static String filteredWordsPath = "src/data/filtered_words.csv"; 
	protected String artist;

	/////////////////////////////////////////////////////
	////////////// 		Methods		    /////////////////
	/////////////////////////////////////////////////////

	/* Reads lyrics file line by line, tokenizes each line, filters unwanted words/whitespace,
	 * hashes each unique word and its associated frequency */
	public static HashMap<String,Integer> createMap(String filePath) {
		BufferedReader read;
		String line = null;
		
		if (! new File(filteredWordsPath).exists())
			throw new RuntimeException("ERROR: System cannot find filtered words csv file in /src/data. createMap() will fail without this file,"
					+ " so execution has been stopped.");

		try {
			
			// Create a set of all words we want to ignore/filter out
			createFilteredWordsSet(filteredWordsPath);

			read = new BufferedReader(new FileReader(filePath));
			String[] words = new String[getMaxWords(filePath)];

			while ((line = read.readLine()) != null) {

				words = filter(line);

				for (String word : words) {
					if (! filteredWords.contains(word)) {
						if (! wordOccurences.containsKey(word)) {
							wordOccurences.put(word, 1);
						}
						else {
							int oldValue = wordOccurences.get(word);
							wordOccurences.remove(word);
							wordOccurences.put(word, oldValue+1);
						}
					}
				}
			}
			read.close();
		} catch (IOException ioe) {

		}
		return wordOccurences;
	}

	// Given a line of song lyrics from a file, remove words we don't want,
	// remove attached commas/other chars, return a split String[] of that line
	private static String[] filter(String line) {
		String[] splitLine = line.split("\\s+");
		ArrayList<String> tokensToKeep = new ArrayList<String>(splitLine.length);

		// Filter out blank lines
		if (! line.equals("")) {
			for (String token : splitLine) {
				// If the word isn't one we pre-selected to filter out
				if (! filteredWords.contains(token)) {
					// Filters out strings like "[Chorus]", "(Artist)", etc.
					if (! ( token.contains("[") || token.contains("]")
							|| token.contains("(") || token.contains(")"))) {
						// Guarantees all tokens in HashMap do not have attached commas/brackets
						// Insert all tokens as lower case to simplify duplicate detection
						tokensToKeep.add(token.toLowerCase().replaceAll(",", ""));
					}
				}
			}
		}
		return tokensToKeep.toArray(new String[tokensToKeep.size()]);
	}

	private static void createFilteredWordsSet(String path) throws IOException {

		BufferedReader readFile = new BufferedReader(new FileReader(path));
		String line = readFile.readLine();
		String[] aux = line.split(",");

		for (String word : aux) {
			filteredWords.add(word);
		}

		readFile.close();
	}

	// Sorts the map with most commonly occurring words first
	// so that HashMap.iterator.keyset returns highest-occurring words first
	@SuppressWarnings("all")
	public static HashMap<String, Integer> sortMap(HashMap<String,Integer> unsortedMap) {

		LinkedList list = new LinkedList(unsortedMap.entrySet());

		// o2.compareTo(o1) sorts in ascending order; reverse for descending 
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put((String) entry.getKey(), (Integer) entry.getValue());

		}
		return sortedMap;
	}

	// Reads a file, returns the maximum number of words on a given line
	private static int getMaxWords(String path) {
		int best = 0;
		String[] tmp = new String[30];
		String line = null;

		try {
			
			BufferedReader br = new BufferedReader(new FileReader(path));
			line = br.readLine();
			tmp = line.split("\\s+");
			best = tmp.length;	// Initialize best to # words in first line

			// For each line, count the number of words; if more than best, overwrite
			while ((line = br.readLine()) != null) {
				tmp = line.split("\\s+");
				if (tmp.length > best) 
					best = tmp.length;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return best;
	}

	// Quick shorthand to print contents of output array
	public void printArray(String[] arr) {
		for (int i = 0; i<arr.length; i++) {
			System.out.println(arr[i]);
		}
	}

	public static void main(String[] a) throws Exception {
		
		HashMap<String,Integer> h = createMap("src/data/test.txt");
		h = sortMap(h);

		System.out.println("Size of Test map is: " + h.size());
		
		Set<String> s = h.keySet();
		Iterator<String> keys = s.iterator();
		Collection<Integer> s2 = h.values();
		Iterator<Integer> values = s2.iterator();

		while (keys.hasNext()) {
			System.out.println("Word: " + keys.next() + ", Frequency: " + values.next());
		}

	}
}