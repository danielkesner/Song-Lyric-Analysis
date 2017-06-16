package analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LyricAnalyzer {
	
	// Returns the top n most commonly-used words and their associated occurrences and relative frequency
	public ArrayList<Word> getMostCommonWords(HashMap<String,Integer> map, int n) {
		
		if (n > map.size())
			throw new RuntimeException("\nERROR: LyricAnalyzer.getMostCommonWords() called with n-value higher than number of words in Map.");
		
		ArrayList<Word> topWords = new ArrayList<Word>(n);
		Iterator<String> wordIterator = map.keySet().iterator();
		Iterator<Integer> occurrenceIterator = map.values().iterator();
		
		int totalOcc = getSumOfAllOccurrences(map);
		System.out.println("TotalOcc: " + totalOcc);
		
		for (int i = 0; i < n; i++) {
			
			String word = wordIterator.next().toLowerCase();
			int occurrences = occurrenceIterator.next();
			
			double relativeFreq = (double) occurrences/totalOcc;
			
			if (! word.equals("") && word != null && occurrences > 0) {
				if (word.contains(","))
					word = word.replaceAll(",", "");
				if (word.contains("."))
					word = word.replaceAll(",", "");
				
				topWords.add(new Word(word, occurrences, relativeFreq));
			}
		}
		
		return topWords;
	}
	
	private int getSumOfAllOccurrences(HashMap<String,Integer> map) {
		
		Iterator<Integer> occurrenceIterator = map.values().iterator();
		int sum = 0;
		
		while (occurrenceIterator.hasNext()) {
			sum += occurrenceIterator.next();
		}
		
		return sum;
	}
	
	public void printTopWords(ArrayList<Word> list) {
		
		DecimalFormat df = new DecimalFormat("00.###");
		
		for (Word w : list) {
			System.out.println(w.getWord() + "\t\t" + w.getOccurrences() + "\t" + df.format(w.getFreq() * 100) + "%");
		}
		
	}

	public static void main(String[] a) {
		
		HashMap<String,Integer> map = DataPreprocessor.createMap("src/data/test.txt");
		
		map = DataPreprocessor.sortMap(map);
		
		new LyricAnalyzer().printTopWords(new LyricAnalyzer().getMostCommonWords(map, 5));
	}

}
