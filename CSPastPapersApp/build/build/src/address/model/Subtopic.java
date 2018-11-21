package address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Subtopic object is created for each subtopic.
 * 
 * @author snit71
 */
public class Subtopic extends AbstractTopic {

	private int topic;                              		// topic id
    private ObservableList<Subsubtopic> subsubtopics;    // ObservableList of subsubtopics

    /**
     * constructor to create Subtopic object
     * @param tID topicID
     * @param id subtopic number
     * @param n subtopic name
     * @param s ObservableList of subsubtopics under subtopic
     */
    public Subtopic(int tID, int id, String n, ObservableList<Subsubtopic> s) {
        
    		super(id, n);
        topic = tID;
        subsubtopics = s;
        
    }
    
    /**
     * constructor to create Subtopic object
     * @param tID topicID
     * @param id subtopic number
     * @param n subtopic name
     */
    public Subtopic(int tID, int id, String n) {
        
    		super (id, n);
    	
        topic = tID;
        subsubtopics = FXCollections.observableArrayList();
        
    }
    
    /**
     * accessor method for topic number
     * @return topic number
     */
    public int getTopic() {
        return topic;
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
     * @return full subtopic (e.g. 1.1 - Systems in Organizations)
     */
    @Override
    public String getFullTopic() {
    	
    		int num = getNum();
    		String name = getName();
    	
    		if (num <= 7) {
    			return "" + topic + "." + num + " - " + name;
		}
    		
		char tID = (char) (num + 57);
        return "" + tID + "." + num + " - " + name;
    }
	
}
