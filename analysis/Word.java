package analysis;

public class Word {
	
	String word;
	int occurrences;
	double frequency;
	
	public Word(String word, int occurences) {
		this.word  = word;
		this.occurrences = occurences;
	}
	
	public Word(String word, int occurrences, double freq) {
		this.word = word;
		this.occurrences = occurrences;
		this.frequency = freq;
	}
	
	public String getWord() {
		return word;
	}
	
	public int getOccurrences() {
		return occurrences;
	}
	
	public double getFreq() {
		return frequency;
	}

}
