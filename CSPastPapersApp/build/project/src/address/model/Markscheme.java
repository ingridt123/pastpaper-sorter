package address.model;

/**
 * A Markscheme object is created for each mark scheme
 * 
 * @author snit71
 */
public class Markscheme {

	private int questionID;			// question number
	private char subquestionID;		// subquestion letter
	private String paperID; 			// paper ID
	private int marks;				// question marks
    private String header;			// mark scheme header
    private String sampleAns;		// mark scheme sample answer
    
    /**
     * constructor to create Markscheme object
     * @param qi question id
     * @param si subquestion id
     * @param p paper id
     * @param m question marks
     * @param h mark scheme header
     * @param s mark scheme sample answer
     */
    public Markscheme(int qi, char si, String p, int m, String h, String s) {
        
    		questionID = qi;
    		subquestionID = si;
    		paperID = p;
        marks = m;
        header = h;
        sampleAns = s;
        
    }
    
    /**
     * accessor method for question number
     * @return question number
     */
    public int getQuestionID() {
        return questionID;
    }
    
    /**
     * accessor method for subquestion letter
     * @return subquestion letter
     */
    public char getSubquestionID() {
        return subquestionID;
    }
    
    /**
     * accessor method for subquestion number
     * @return subquestion number
     */
    public int getSubquestionIDInt() {
    	
	    	int sqID = 0;
        if ((int) subquestionID != 0) {
        		sqID = (int) (subquestionID + 96);
        }
        return sqID;
        
    }
    
    /**
     * accessor method for paper ID
     * @return paper ID
     */
    public String getPaperID() {
        return paperID;
    }
    
    /**
     * accessor method for number of marks
     * @return number of marks
     */
    public int getMarks() {
        return marks;
    }
    
    /**
     * accessor method for mark scheme header
     * @return mark scheme header
     */
    public String getHeader() {
        return header;
    }
    
    /**
     * mutator method for mark scheme header
     * @param h mark scheme header
     */
    public void setHeader(String h) {
        header = h;
    }
    
    /**
     * accessor method for mark scheme sample answer
     * @return mark scheme sample answer
     */
    public String getSampleAns() {
        return sampleAns;
    }
    
    /**
     * mutator method for mark scheme sample answer
     * @param ans mark scheme sample answer
     */
    public void setSampleAns(String ans) {
        sampleAns = ans;
    }
    
    /**
     * accessor method for mark scheme answer
     * @return mark scheme answer
     */
    public String getFullAns() {
        
        String fullAns = header + "\n\n" + sampleAns;
        
        return fullAns;
        
    }
	
}
