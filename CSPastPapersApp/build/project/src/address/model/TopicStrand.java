package address.model;

import java.util.TreeMap;

import address.util.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * A TopicStrand object is created for each topic strand.
 * 
 * @author snit71
 */
public class TopicStrand {

	private int topic;                          	// topic id
    private int stopic;                         	// subtopic id
    private int sstopic;                        	// subsubtopic id
    private int num;                            	// topic strand number
    private String statement;                   	// topic strand statement
    private String notes;                       	// topic strand notes
    private ObservableMap<String, Integer> keywords;	// ObservableMap of related keywords
    private ObservableList<Question> questions; 		// ObservableSet of related questions

    /**
     * constructor to create TopicStrand object
     * @param tID topic ID
     * @param sID subtopic ID
     * @param ssID subsubtopicID
     * @param id topic strand number
     * @param s topic strand statement
     * @param n topic strand notes
     * @param k ObservableMap of related keywords
     * @param q ObservableList of related questions
     */
    public TopicStrand(int tID, int sID, int ssID, int id, String s, String n, 
    		ObservableMap<String, Integer> k, ObservableList<Question> q) {
        
        topic = tID;
        stopic = sID;
        sstopic = ssID;
        
        num = id;
        statement = s;
        notes = n;
        
        keywords = k;
        if (k == null) {
        		keywords = FXCollections.observableMap(new TreeMap<String, Integer>());
        }
        
        questions = q;
        if (q == null) {
        		questions = FXCollections.observableArrayList();
        }
        
    }
    
    /**
     * constructor to create TopicStrand object
     * @param tID topic ID
     * @param sID subtopic ID
     * @param ssID subsubtopicID
     * @param id topic strand number
     * @param s topic strand statement
     * @param n topic strand notes
     */
    public TopicStrand(int tID, int sID, int ssID, int id, String s, String n) {
        
        topic = tID;
        stopic = sID;
        sstopic = ssID;
        num = id;
        statement = s;
        notes = n;
        keywords = FXCollections.observableMap(new TreeMap<String, Integer>());
        questions = FXCollections.observableArrayList();
        
    }
    
    /**
    * accessor method for topic number
    * @return topic number
    */
   public int getTopic() {
       return topic;
   }
   
   /**
    * accessor method for subtopic number
    * @return subtopic number
    */
   public int getSTopic() {
       return stopic;
   }
   
   /**
    * accessor method for subsubtopic number
    * @return subsubtopic number
    */
   public int getSSTopic() {
       return sstopic;
   }
    
    /**
     * accessor method for topic strand number
     * @return topic strand number
     */
    public int getNum() {
        return num;
    }
    
    /**
     * accessor method for topic strand full number
     * @return full number (topic.subtopic.subsubtopic)
     */
    public String getFullNum() {
    	
    		String topicStr = Integer.toString(topic);
		if (topic > 7) {
			char topicChar = (char) (topic + 57);
			topicStr = Character.toString(topicChar);
		}
    	
        return "" + topicStr + "." + stopic + "." + num;
    }
    
    /**
     * accessor method for topic strand statement
     * @return topic strand number
     */
    public String getStatement() {
        return statement;
    }
    
    /**
     * accessor method for topic strand notes
     * @return topic strand notes
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * accessor method for keywords related to topic strand
     * @return ObservableMap of related keywords
     */
    public ObservableMap<String, Integer> getKeywords() {
        return keywords;
    }
    
    /**
     * mutator method for keywords related to topic strand (add keywords)
     * @param Observable of keywords
     */
    public void addKeywords(ObservableMap<String, Integer> k) {
        keywords = k;
    }
    
    /**
     * accessor method for string of keywords related to topic strand
     * @return string of keywords
     */
    public String getKeywordString() {
    		return StringUtil.getBracketString(keywords);
    }
    
    /**
     * mutator method for keywords related to topic strand (add keyword)
     * @param k keyword to be added
     */
    public void addKeyword(String k) {
    		keywords.put(k, 1);
    }
    
    /**
     * mutator method for keywords related to topic strand (remove keyword)
     * @param k keyword to be added
     */
    public void rmKeyword(String k) {
        keywords.remove(k);
    }
    
    /**
     * accessor method for questions related to topic strand
     * @return ObservableSet of related keywords
     */
    public ObservableList<Question> getRelQus() {
        return questions;
    }
    
    /**
     * mutator method for questions related to topic strand (add question)
     * @param qu question to be added
     */
    public void addRelQus(Question qu) {
    	
	    	if (!questions.contains(qu)) {
    			questions.add(qu);
    		}
    		
    }
    
    /**
     * accessor method for topic strand (with statement)
     * @return  topic strand
     */
    public String getStrand() {
        return getFullNum() + ": " + statement;
    }
    
    /**
     * accessor method for full topic strand
     * @return full topic strand
     */
    public String getFullStrand() {
        return getFullNum() + ": " + statement + "\n" + notes + "\n(" + keywords + ")";
    }
	
}
