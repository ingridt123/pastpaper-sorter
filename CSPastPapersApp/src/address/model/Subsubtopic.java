package address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Subsubopic object is created for each subsubtopic.
 * @author snit71
 */
public class Subsubtopic extends AbstractTopic {

	private int topic;                          		// topic id
    private int stopic;                         		// subtopic id
    private String startStrand;						// id of first strand
    private String endStrand;						// id of last strand
    private ObservableList<TopicStrand> tStrands;    // ObservableList of topic strands

    /**
     * constructor to create Subsubtopic object
     * @param tID topic ID
     * @param sID subtopic ID
     * @param id subsubtopic number
     * @param name subsubtopic name
     * @param start id of first strand
     * @param end id of last strand
     * @param s ObservableList of TopicStrand objects under subsubtopic
     */
    public Subsubtopic(int tID, int sID, int id, String name, String start, String end, ObservableList<TopicStrand> ts) {
        
    		super(id, name);
        topic = tID;
        stopic = sID;
        startStrand = start;
        endStrand = end;
        
        tStrands = ts;
        if (ts == null) {
        		tStrands = FXCollections.observableArrayList();
        }
        
    }
    
    /**
     * constructor to create Subsubtopic object
     * @param tID topic ID
     * @param sID subtopic ID
     * @param id subsubtopic number
     * @param name subsubtopic name
     * @param start id of first strand
     * @param end id of last strand
     */
    public Subsubtopic(int tID, int sID, int id, String name, String start, String end) {
        
    		super(id, name);
    		topic = tID;
        stopic = sID;
        startStrand = start;
        endStrand = end;
        tStrands = FXCollections.observableArrayList();
        
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
     * @return topic number
     */
    public int getSTopic() {
        return stopic;
    }
    
    /**
     * accessor method for id of first strand
     * @return id of first strand
     */
    public String getStartStrand() {
    		return startStrand;
    }
    
    /**
     * accessor method for id of last strand
     * @return id of last strand
     */
    public String getEndStrand() {
    		return endStrand;
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
     * @return full subsubtopic (e.g. User Focus (1.1.8 - 1.1.10))
     */
    @Override
    public String getFullTopic() {
        return getName() + " (" + startStrand + " - " + endStrand + ")";
    }
	
}
