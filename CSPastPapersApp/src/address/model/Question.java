package address.model;

import java.util.TreeMap;

import address.util.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * A Question object is created for each question
 * 
 * @author snit71
 */
public class Question {

	private int questionID;                          // question number
	private char subquestionID;						// subquestion letter ('\u0000' if not subquestion)
	private String paperID;							// paper ID
    private String statement;                       	// question statement
    private String visuals;                         	// question visuals
    private ObservableList<Question> subQu;			// ObservableList of subquestions
    private int marks;								// question number of marks
    private Markscheme mScheme;                      // Markscheme object of question
    private ObservableMap<String, Integer> keywords;	// ObservableMap of String objects of keywords related to the question
    private ObservableList<TopicStrand> relStrands;	// ObservableList of TopicStrand objects of topic strands related to the question
    
    /**
     * constructor to create Question object
     * @param qi question number
     * @param si subquestion letter
     * @param p paper ID
     * @param s question statement
     * @param v visuals
     * @param q ObservableList of subquestions
     * @param m question mark scheme (Markscheme object)
     * @param k ObservableMap of question keywords
     * @param r ObservableList of topic strands related to question
     */
    public Question(int qi, char si, String p, String s, String v, ObservableList<Question> q,
    			int m, Markscheme ms, ObservableMap<String, Integer> k, ObservableList<TopicStrand> r) {
        
        questionID = qi;
        subquestionID = si;
        paperID = p;
        statement = s;
        visuals = v;
        
        subQu = q;
        if (q == null) {
        		subQu = FXCollections.observableArrayList();
        }
        
        marks = m;
        
        mScheme = ms;
        if (ms == null) {
        		mScheme = new Markscheme(questionID, subquestionID, paperID, marks, "", "");
        }
        
        keywords = k;
        if (k == null) {
        		keywords = FXCollections.observableMap(new TreeMap<String, Integer>());
        }
        
        relStrands = r;
        if (r == null) {
        		relStrands = FXCollections.observableArrayList();
        }
        
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
     * accessor method for full question ID
     * @return full question ID
     */
    public String getFullQuestionID() {
    	
    		String id = paperID + "/" + questionID;
    		if ((int) subquestionID != 0) {
    			id += subquestionID;
    		}
    	
    		return id;
    }
    
    /**
     * accessor method for question statement
     * @return question statement
     */
    public String getStatement() {
        return statement;
    }
    
    /**
     * accessor method for question visuals
     * @return question visuals
     */
    public String getVisuals() {
    		return visuals;
    }
    
    /**
     * accessor method for subquestions
     * @return ObservableList of subquestions
     */
    public ObservableList<Question> getSubQus() {
        return subQu;
    }
    
    /**
     * accessor method for subquestions
     * @return subquestions
     */
    public Question getSubQu(char subquestionID) {
        
    		for (Question sq : subQu) {
    			if (sq.getSubquestionID() == subquestionID) {
    				return sq;
    			}
    		}
    		return null;
    	
    }
    
    /**
     * mutator method for subquestions (add subquestion)
     * @param qu Question object of subquestion
     */
    public void addSubQu(Question qu) {
        subQu.add(qu);
    }
    
    /**
     * mutator method for subquestions (add subquestion)
     * @param qi question number
     * @param si subquestion letter
     * @param p paper ID
     * @param s question statement
     * @param v question visuals
     * @param m marks
     * @param ms question mark scheme (Markscheme object)
     * @param k ObservableMap of question keywords
     * @param r ObservableList of topic strands related to question
     */
    public void addSubQu(int qi, char si, String p, String s, String v, int m, Markscheme ms, 
    		ObservableMap<String, Integer> k, ObservableList<TopicStrand> r) {
        
    		if (subQu == null) {
    			subQu = FXCollections.observableArrayList();
    		}
    	
        Question qu = new Question(qi, si, p, s, v, null, m, ms, k, r);
        subQu.add(qu);
        
    }
    
    /**
     * accessor method for question marks
     * @return question number of marks
     */
    public int getMarks() {
    		return marks;
    }
    
    /**
     * accessor method for question mark scheme
     * @return question mark scheme (Markscheme object)
     */
    public Markscheme getMS() {
        return mScheme;
    }
    
    /**
     * mutator method for question mark scheme
     * @param ms question mark scheme (Markscheme object)
     */
    public void setMS(Markscheme ms) {
        mScheme = ms;
    }
    
    /**
     * mutator method for question mark scheme
     * @param m question marks
     * @param h mark scheme header
     * @param s mark scheme sample answer
     */
    public void setMS(int m, String h, String s) {
        mScheme = new Markscheme(questionID, subquestionID, paperID, m, h, s);
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
     * accessor method for string of question keywords
     * @return string of keywords
     */
    public String getKeywordString() {
    		return StringUtil.getBracketString(keywords);
    }
    
    /**
     * mutator method for question keywords (add keyword)
     * @param k keyword to be added
     */
    public void addKeyword(String k) {
        keywords.put(k, 1);
    }
    
    /**
     * mutator method for question keywords (remove keyword)
     * @param k keyword to be added
     */
    public void rmKeyword(String k) {
        keywords.remove(k);
    }
    
    /**
     * accessor method for topic strands related to question
     * @return ObservableList of related topic strands
     */
    public ObservableList<TopicStrand> getRelStrands() {
        return relStrands;
    }
    
    /**
     * accessor method for string of strands related to question
     * @return string of strands
     */
    public String getRelStrandString() {
    	
    		TreeMap<String, Integer> temp = new TreeMap<>();
    		for (TopicStrand ts : relStrands) {
    			temp.put(ts.getFullNum(), null);
    		}
    		ObservableMap<String, Integer> strands = FXCollections.observableMap(temp);
    		return StringUtil.getBracketString(strands);

    }
    
    /**
     * mutator method for question topic strands (add strands)
     * @param strands ObservableList of related topic strands
     */
    public void addRelStrands(ObservableList<TopicStrand> strands) {
    		relStrands = strands;
    }
    
    /**
     * mutator method for question topic strands (add strand)
     * @param ts topic strand to be added
     */
    public void addRelStrand(TopicStrand ts) {
    	
    		if (relStrands == null) {
    			relStrands = FXCollections.observableArrayList();
    		}
    	
    		if (!relStrands.contains(ts)) {
    			relStrands.add(ts);
    		}
    }
    
    /**
     * accessor method for full question
     * @return full question
     */
    public String getFullQus() {
        
    		String beg = "" + questionID + ". ";
		if ((int) subquestionID != 0) {
			beg = "(" + subquestionID + ") ";
		}
		
		return beg + statement;
    	
    }
	
}
