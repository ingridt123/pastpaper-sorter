package address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Topic object is created for each topic.
 * 
 * @author snit71
 */
public class TopicBackup {

	private int num;                           		// topic number
    private String name;                        		// topic name
    private ObservableList<Subtopic> subtopics;      // ObservableList of subtopics

    /**
     * constructor to create Topic object
     * @param id topic number
     * @param n topic name
     * @param s ObservableList of Subtopic objects under topic
     */
    public TopicBackup(int id, String n, ObservableList<Subtopic> s) {
        
        num = id;
        name = n;
        subtopics = s;
        
    }
    
    /**
     * constructor to create Topic object
     * @param id topic number
     * @param n topic name
     */
    public TopicBackup(int id, String n) {
        
        num = id;
        name = n;
        subtopics = FXCollections.observableArrayList();
        
    }
    
    /**
     * accessor method for topic number
     * @return topic number
     */
    public int getNum() {
        return num;
    }
    
    /**
     * accessor method for topic name
     * @return topic name
     */
    public String getName() {
        return name;
    }
    
    /**
     * accessor method for subtopics under topic
     * @return ObservableList of Subtopic objects under topic
     */
    public ObservableList<Subtopic> getSubtopics() {
        return subtopics;
    }
    
    /**
     * mutator method for subtopics under topic
     * @param ObservableList of Subtopic objects under topic
     */
    public void addSubtopics(ObservableList<Subtopic> st) {
    		subtopics = st;
    }
    
    /**
     * mutator method for subtopics under topic (add subtopic)
     * @param Subtopic object
     */
    public void addSubtopic(Subtopic st) {
    		subtopics.add(st);
    }
    
    /**
     * accessor method for subtopic under topic
     * @param id subtopic number
     * @return subtopic
     */
    public Subtopic getSubtopic(int id) {
        
    		for (Subtopic st : subtopics) {
    			
    			if (st.getNum() == id) {
    				return st;
    			}
    			
    		}
    		
    		return null;
    }
    
    /**
     * accessor method for subsubtopic under topic
     * @param subtopicID subtopic number
     * @param subsubtopicID subsubtopic number
     * @return subsubtopic
     */
    public Subsubtopic getSubsubtopic(int subtopicID, int subsubtopicID) {
        
    		Subtopic st = getSubtopic(subtopicID);
    		for (Subsubtopic sst : st.getSubsubtopics()) {
    			
    			if (sst.getNum() == subsubtopicID) {
    				return sst;
    			}
    			
    		}
    		
    		return null;
    }
    
    /**
     * accessor method for topic strand under topic
     * @param subtopicID subtopic number
     * @param subsubtopicID subsubtopic number
     * @param strandID topic strand number
     * @return topic strand
     */
    public TopicStrand getTStrand(int subtopicID, int subsubtopicID, int strandID) {
        
    		Subsubtopic sst = getSubsubtopic(subtopicID, subsubtopicID);
    		for (TopicStrand ts : sst.getTStrands()) {
    			
    			if (ts.getNum() == strandID) {
    				return ts;
    			}
    			
    		}
    		
    		return null;
    }
    
    /**
     * accessor method for full topic
     * @return full topic
     */
    public String getFullTopic() {
    	
    		if (num <= 7) {
    			
    			return "Topic " + num + " - " + name;
    		}
    	
    		char id = (char) (num + 57); 
    		return "Option " + id + " - " + name;
    }
	
}
