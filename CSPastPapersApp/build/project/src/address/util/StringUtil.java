package address.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class StringUtil {

	/**
	 * remove leading spaces of string
	 * edited from https://stackoverflow.com/questions/16974267/how-to-remove-only-trailing-spaces-of-a-string-in-java-and-keep-leading-spaces
	 * @param str specified string
	 * @return specified string without leading spaces
	 */
	public static String removeLeadSpace(String str) {
		return str.replaceFirst("\\s++$", "");
	}
	
	/**
	 * gets keywords by removing common words from list
	 * @param str given string
	 * @return ObservableMap of keywords
	 * @throws Exception
	 */
    public static ObservableMap<String, Integer> getKeywords(String str) throws Exception {
        
    		TreeSet<String> commonWords = new TreeSet<>();
    		TreeMap<String, Integer> keywords = new TreeMap<>();
    		String[] keyArray = str.trim().split("\\s+");
    		
    		// add words in str to TreeMap (sorted)
    		for (int i = 0; i < keyArray.length; i++) {
    			String word = keyArray[i].replaceAll("[!\\\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]\\^_`{|}~]", "").trim().toLowerCase();
    			if (!word.equals("") && !word.matches("(.*)\\d(.*)")) {
	    			if (keywords.containsKey(word)) {
	    				keywords.replace(word, keywords.get(word)+1);
	    			}
	    			else {
	    				keywords.put(word, 1);
	    			}
    			}
    		}
    	
    		try {

    			// add all common words to a set
    			File file = new File("files/common-words.txt");
        		FileReader fileReader = new FileReader(file);
	    		BufferedReader bufferedReader = new BufferedReader(fileReader);
	    		
	    		String word = "";
	    		while((word = bufferedReader.readLine()) != null) {
	    			commonWords.add(word);
	    		}
    			
	    		// remove all common words in list of keywords
	    		for (String w : commonWords) {
	    			keywords.remove(w);
	    		}
	    		
	    		// remove special words in syllabus
	    		keywords.remove("aim");
	    		keywords.remove("int");
	    		keywords.remove("myp");
	    		keywords.remove("link");
	    		keywords.remove("se");
	    		keywords.remove("tok");
	    		
	    		bufferedReader.close();
	    		
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		return FXCollections.observableMap(keywords);
    	
    }
    
    /**
     * accessor method for string of words from ObservableList
     * @param words ObservableMap of words
     * @return string of words in format (string, string...)
     */
    public static String getBracketString (ObservableMap<String, Integer> words) {
    		return "(" + String.join(", ", words.keySet()) + ")";
    }
	
}