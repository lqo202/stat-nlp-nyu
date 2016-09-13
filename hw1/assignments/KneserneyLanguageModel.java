package nlp.assignments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nlp.langmodel.LanguageModel;
import nlp.util.Counter;
import nlp.util.CounterMap;

/**
 * A dummy language model -- uses empirical unigram counts, plus a single
 * ficticious count for unknown words.
 */
class KneserNeyLanguageModel implements LanguageModel {

	static final String START = "<S>";
	static final String STOP = "</S>";
	static final String UNKNOWN = "*UNKNOWN*";
	static final double delta = 0.75;

	Counter<String> wordCounter = new Counter<String>();
	CounterMap<String, String> bigramCounter = new CounterMap<String, String>();

	public double getmaxBigramProbability(String previousWord, String word) {
		double bigramCount = bigramCounter.getCount(previousWord, word);
		return Math.max(bigramCount - delta,0);

	}
	
	public double getTotalBigramCount(){
		//Counter<String> UnigramComposed = bigramCounter.getCounter(word);
		//System.out.println(word + UnigramComposed + UnigramComposed.totalCount());
		double sum = 0.0;
		for (String word : bigramCounter.keySet()){
			//Total number of different bigrams
			//sum += bigramCounter.getCounter(word).size();
			//Total number of bigrams
			sum += bigramCounter.getCounter(word).totalCount();
		}
		//return UnigramComposed.size();
		//return UnigramComposed.totalCount();
		return sum;
	}
	
	public double getBigramCount(String previousWord){
		Counter<String> UnigramComposed = bigramCounter.getCounter(previousWord);
		
		//System.out.println(word + UnigramComposed + UnigramComposed.totalCount());
		double sum = 0.0;
		for (String word : UnigramComposed.keySet()){
			sum += bigramCounter.getCounter(word).size();
		}
		//return UnigramComposed.size();
		//return UnigramComposed.totalCount();
		return sum;
	}

	public double getLambdaCalculation(String previousWord, String word){
		//double totalCountWord =   wordCounter.getCount(previousWord);
		double totalCountWord = wordCounter.getCount(previousWord);
		
		if (totalCountWord == 0) {
			//System.out.println("UNKNOWN Word: " + word);
			totalCountWord = wordCounter.getCount(UNKNOWN);
		}
		
		double totalBigramWord = bigramCounter.getCounter(previousWord).totalCount();

		
		//double totalCountPrevious = getTotalBigramProbability(previousWord);
		return (delta *totalBigramWord/(totalCountWord));
	}
	
	public double getProbContinuation(String previousWord, String word){
		double totalCountBigrams = wordCounter.getCount(previousWord);
		//double totalCountBigrams = getTotalBigramCount();
		//double sum = 0.0;
		//for (String key : wordCounter.keySet()){
		//	if (bigramCounter.getCounter(key).containsKey(word)) {
		//			sum += bigramCounter.getCounter(key).getCount(word);
		//	}
		//}
		
		double sum = bigramCounter.getCount(previousWord, word) ;
		//if (sum==0) {
		//	sum = 1;
		//}
		//if (totalCountBigrams==0){
		//	totalCountBigrams =1;
		//}
		//System.out.println(sum/ totalCountBigrams);
		return sum/totalCountBigrams;
	}
	
	public double getBigramProbability(String previousWord, String word){
		//double bigramCount = Math.abs(bigramCounter.getCount(previousWord, word));
		//double totalCountWord = wordCounter.getCount(previousWord);
		//double totalCountBigrams = getTotalBigramCount();
		double totalCountWord = bigramCounter.getCounter(previousWord).totalCount();

		
		double calculates  = getmaxBigramProbability(previousWord, word)/totalCountWord +
		getLambdaCalculation(previousWord, word)*getProbContinuation(previousWord, word);
		
		//System.out.println(previousWord+ " "+ word+" " +calculates+ " "+getProbContinuation(previousWord, word) );
		//if (calculates ==0 ){
		//	System.out.println(previousWord+ " "+ word+" " +calculates);
		//}
		
		return calculates;
	}
	
	public double getSentenceProbability(List<String> sentence) {
		List<String> stoppedSentence = new ArrayList<String>(sentence);
		stoppedSentence.add(0, START);
		stoppedSentence.add(STOP);
		double probability = 1.0;
		String previousWord = stoppedSentence.get(0);
		for (int i = 1; i < stoppedSentence.size(); i++) {
			String word = stoppedSentence.get(i);
			probability *= getBigramProbability(previousWord, word);

				//System.out.println(previousWord+ " "+ word+" " +probability);
			
			previousWord = word;
			
		}
		return probability;
	}

	String generateWord() {
		double sample = Math.random();
		double sum = 0.0;
		for (String word : wordCounter.keySet()) {
			sum += wordCounter.getCount(word);
			if (sum > sample) {
				return word;
			}
		}
		return UNKNOWN;
	}

	public List<String> generateSentence() {
		List<String> sentence = new ArrayList<String>();
		String word = generateWord();
		while (!word.equals(STOP)) {
			sentence.add(word);
			word = generateWord();
		}
		return sentence;
	}

	public KneserNeyLanguageModel(
			Collection<List<String>> sentenceCollection) {
		for (List<String> sentence : sentenceCollection) {
			List<String> stoppedSentence = new ArrayList<String>(sentence);
			stoppedSentence.add(0, START);
			stoppedSentence.add(STOP);
			String previousWord = stoppedSentence.get(0);
			for (int i = 1; i < stoppedSentence.size(); i++) {
				String word = stoppedSentence.get(i);
				wordCounter.incrementCount(word, 1.0);
				bigramCounter.incrementCount(previousWord, word, 1.0);
				previousWord = word;
			}
		}
		wordCounter.incrementCount(UNKNOWN, 1.0);
	}

	
	public void getmassdistribution() {
		System.out.println("Prob Mass of the language:") ;
		System.out.println(wordCounter.toString(20));
		
	}
	
}
