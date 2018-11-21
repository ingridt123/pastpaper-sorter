package address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Subsubopic object is created for each subsubtopic.
 * @author snit71
 */
public class SubsubtopicBackup {

	private int topic;                          		// topic id
    private int stopic;                         		// subtopic id
    private int num;                            		// subsubtopic number
    private String name;                        		// subsubtopic name
    private ObservableList<TopicStrand> tStrands;    // ObservableList of topic strands

    /**
     * constructor to create Subsubtopic object
     * @param tID topic ID
     * @param sID subtopic ID
     * @param id subsubtopic number
     * @param n subsubtopic name
     * @param s ObservableList of TopicStrand objects under subsubtopic
     */
    public SubsubtopicBackup(int tID, int sID, int id, String n, ObservableList<TopicStrand> ts) {
        
        topic = tID;
        stopic = sID;
        num = id;
        name = n;
        tStrands = ts;
        
    }
    
    /**
     * constructor to create Subsubtopic object
     * @param tID topic ID
     * @param sID subtopic ID
     * @param id subsubtopic number
     * @param n subsubtopic name
     */
    public SubsubtopicBackup(int tID, int sID, int id, String n) {
        
        topic = tID;
        stopic = sID;
        num = id;
        name = n;
        tStrands = FXCollections.observableArrayList();
        
    }
    
    /**
     * accessor method for subsubtopic number
     * @return subsubtopic number
     */
    public int getNum() {
        return num;
    }
    
    /**
     * accessor method for subsubtopic name
     * @return subsubtopic name
     */
    public String getName() {
        return name;
    }
    
    /**
     * accessor method for topic strands under subsubtopic
     * @return ArrayList of TopicStrand objects under subsubtopic
     */
    public ObservableList<TopicStrand> getTStrands() {
        return tStrands;
    }
    
    /**
     * mutator method for topic strands under subsubtopic
     * @param ObservableList of TopicStrand objects under subsubtopic
     */
    public void addTStrands(ObservableList<TopicStrand> ts) {
    		tStrands = ts;
    }
    
    /**
     * mutator method for topic strands under subsubtopic (add topic strands)
     * @param ts TopicStrand object
     */
    public void addTStrand(TopicStrand ts) {
    		tStrands.add(ts);
    }
    
    /**
     * accessor method for full subsubtopic
     * @return full subsubtopic
     */
    public String getFullSubsubtopic() {
        return name + "(" + tStrands.get(0).getFullNum() + " - " + 
                tStrands.get(1).getFullNum() + ")";
    }
	
}
