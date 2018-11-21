package address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Paper object is created for each paper
 * 
 * @author snit71
 */
public class Paper {

	private String paperID;						// paper ID (YMM/NL)
	private int year;                           	// last two digits of paper year
    private String month;                       	// paper month (M/N)
    private int num;                            	// paper number (1/2/3)
    private String level;                       	// paper course level (S/H)
    private ObservableList<Question> questions;	// ObservableList of questions in paper
    
    /**
     * constructor to create Paper object
     * @param p paper ID
     * @param y paper year
     * @param m paper month
     * @param n paper number
     * @param l paper course level
     * @param qu ObservableList of paper questions
     */
    public Paper(String p, int y, String m, int n, String l, ObservableList<Question> qu) {
        
    		paperID = p;
        year = y;
        month = m;
        num = n;
        level = l;
        
        questions = qu;
        if (qu == null) {
        		questions = FXCollections.observableArrayList();
        }
        
    }
    
    /**
     * accessor method for paper ID
     * @return paper ID
     */
    public String getPaperID() {
        return paperID;
    }
    
    /**
     * accessor method for paper year.
     * @return last two digits of paper year
     */
    public int getYear() {
        return year;
    }
    
    /**
     * accessor method for paper month
     * @return paper month
     */
    public String getMonth() {
        return month;
    }
    
    /**
     * returns the paper date
     * @return paper date in form MDD
     */
    public String getDate() {
        return month + year;
    }
    
    /**
     * accessor method for paper number
     * @return paper number
     */
    public int getNum() {
        return num;
    }
    
    /**
     * accessor method for paper course level
     * @return paper course level
     */
    public String getLevel() {
        return level;
    }
    
    /**
     * accessor method for paper questions
     * @return ObservableList of paper questions
     */
    public ObservableList<Question> getQuestions() {
        return questions;
    }
    
    /**
     * accessor method for paper questions (only for main questions, not subquestions)
     * @return question
     */
    public Question getQuestion(int questionID) {
        
    		for (Question q : questions) {
    			if (q.getQuestionID() == questionID) {
    				return q;
    			}
    		}
    		
    		return null;
    	
    }
    
    /**
     * mutator method for paper questions
     * @param ObservableList of paper questions
     */
    public void setQuestions(ObservableList<Question> qu) {
        questions = qu;
    }
    
    /**
     * mutator method for paper questions (add question)
     * @param paper question
     */
    public void addQuestion(Question qu) {
    	
    		if (questions == null) {
    			questions = FXCollections.observableArrayList();
    		}
        questions.add(qu);
    }
	
}