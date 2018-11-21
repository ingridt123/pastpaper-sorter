package address.model;

/**
 * AbstractTopic is an abstract class that is inherited by the Topic, Subtopic and
 * Subsubtopic classes.
 * 
 * @author snit71
 */
public abstract class AbstractTopic {

	private int num;                           		// topic number
    private String name;                        		// topic name
    
    /**
     * constructor to create AbstractTopic object
     * @param id topic number
     * @param n topic name
     */
    public AbstractTopic(int id, String n) {
    	
    		num = id;
        name = n;
    	
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
     * abstract accessor method for full topic
     * @return full topic
     */
    abstract public String getFullTopic();
    
}
