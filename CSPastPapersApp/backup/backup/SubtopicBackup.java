package address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Subtopic object is created for each subtopic.
 * 
 * @author snit71
 */
public class SubtopicBackup {

	private int topic;                              		// topic id
    private int num;                                		// subtopic number
    private String name;                            		// subtopic name
    private ObservableList<Subsubtopic> subsubtopics;    // ObservableList of subsubtopics

    /**
     * constructor to create Subtopic object
     * @param tID topicID
     * @param id subtopic number
     * @param n subtopic name
     * @param s ObservableList of subsubtopics under subtopic
     */
    public SubtopicBackup(int tID, int id, String n, ObservableList<Subsubtopic> s) {
        
        topic = tID;
        num = id;
        name = n;
        subsubtopics = s;
        
    }
    
    /**
     * constructor to create Subtopic object
     * @param tID topicID
     * @param id subtopic number
     * @param n subtopic name
     */
    public SubtopicBackup(int tID, int id, String n) {
        
        topic = tID;
        num = id;
        name = n;
        subsubtopics = FXCollections.observableArrayList();
        
    }
    
    /**
     * accessor method for subtopic number
     * @return subtopic number
     */
    public int getNum() {
        return num;
    }
    
    /**
     * accessor method for subtopic name
     * @return subtopic name
     */
    public String getName() {
        return name;
    }
    
    /**
     * accessor method for subsubtopics under subtopic
     * @return ObservableList of Subsubtopic objects under subtopic
     */
    public ObservableList<Subsubtopic> getSubsubtopics() {
        return subsubtopics;
    }
    
    /**
     * mutator method for subsubtopics under subtopic
     * @param ObservableList of Subsubtopic objects under subtopic
     */
    public void addSubsubtopics(ObservableList<Subsubtopic> sst) {
    		subsubtopics = sst;
    }
    
    /**
     * mutator method for subsubtopics under subtopic (add subsubtopic)
     * @param sst Subsubtopic object
     */
    public void addSubsubtopic(Subsubtopic sst) {
    		subsubtopics.add(sst);
    }
    
    /**
     * accessor method for full subtopic
     * @return full subtopic
     */
    public String getFullSubtopic() {
    	
    		if (num <= 7) {
			
    			return "" + topic + "." + num + " - " + name;
		}
    		
		char tID = (char) (num + 57);
        return "" + tID + "." + num + " - " + name;
    }
	
}
