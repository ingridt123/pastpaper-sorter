package address.model;

import address.util.SearchUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Connects to the web-based relational database.
 * 
 * @author snit71
 */
public class Database {

	private Connection con;         // connection to database
    private ResultSet rs;           // table of data returned by SQL statement
    private String url;             // database URL
    private String user;            // database username
    private String password;        // database password
    
    /**
     * constructor to create Database object to begin communicating with database
     * (login on phpMyAdmin -- sql12.freesqldatabase.com 3306)
     * @param l database URL
     * @param u database username
     * @param p database password
     */
    public Database(String l, String u, String p) {
        
        con = null;
        rs = null;
        url = l;
        user = u;
        password = p;
        
        connectDB();
        
    }
    
    /**
     * creates connection to web-based relational database
     */
    public void connectDB() {
        
        try {
            // establish connection
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);		// to allow multiple open ResultSets
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);			// exit if cannot establish connection with database
        }
        
    }
    
    /**
     * execute SQL query
     * @param query SQL query
     * @return ResultSet of query results (only for SELECT statement)
     */
    public ResultSet exQuery(String query) {
        
        try {
            Statement st = con.createStatement();		// create statement
            rs = st.executeQuery(query);				// execute query
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return rs;
        
    }
    
    /**
     * execute SQL update
     * @param update SQL update
     */
    public void exUpdate(String update) {
    	
    		try {
    			Statement st = con.createStatement();		// create statement
            st.executeUpdate(update);					// execute update
            con.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    	
    }
    
    /**
     * accessor method for database URL
     * @return database URL
     */
    public String getURL() {
        return url;
    }
    
    /**
     * accessor method for database username
     * @return database username
     */
    public String getUser() {
        return user;
    }
    
    /**
     * create database table
     * syntax: CREATE TABLE [table_name] ([column_names]);
     * @param name database table name
     * @param id number of primary IDs in table
     * @param cols LinkedHashMap of database table column names
     */
    public void createTable(String name, int ids, LinkedHashMap<String, String> cols) {
        
        if (ids > cols.values().size()) {
            System.err.println("Invalid number of primary keys");
            return;
        }
        
        String create = "CREATE TABLE " + name + "(";
        
        for (Map.Entry<String, String> c : cols.entrySet()) {
        	
            create += c.getKey() + " " + c.getValue().toUpperCase() + ", ";
            
        }
        
        int count = 0;
        create += "PRIMARY KEY(";
        for (Map.Entry<String, String> c : cols.entrySet()) {
            
            create = create + c.getKey();
            count++;
            
            if (count >= ids) {
                break;
            }
            else {
                create = create + ", ";
            }
        }
        
        create += "));";
        
        exUpdate(create);
        
    }
    
    /**
     * insert data into database table (all columns)
     * syntax: INSERT INTO [table_name] VALUES ([values]);
     * @param name database table name
     * @param values ArrayList of values
     */
    public void insert(String name, ArrayList<String> values) {
        
        String insert = "INSERT INTO " + name + " VALUES (";
        
        // add values
        for (String v : values) {
        		insert = insertValues(insert, v);
        }
        
        // add end bracket
        insert = insertEndBracket(insert);
        
        exUpdate(insert);
        
    }
    
    /**
     * insert data into database table (some columns)
     * syntax: INSERT INTO [table_name]([column_names]) VALUES ([values_of_columns]);
     * @param name database table name
     * @param values LinkedHashMap of column names and values
     */
    public void insert(String name, LinkedHashMap<String, String> values) {
        
        String insert = "INSERT INTO " + name + "(";
        
        // add column names
        for (String v : values.keySet()) {
            insert += v + ", ";
        }
        
        insert += ") VALUES (";
        
        // add values
        for (String v : values.values()) {
        		insert = insertValues(insert, v);
        }
        
        // add end bracket
        insert = insertEndBracket(insert);
        
        exUpdate(insert);
        
    }
    
    /**
     * returns value statement with value (differentiates between integer and string)
     * @param insert insert statement
     * @param v value string
     * @return insert statement with value
     */
    private String insertValues(String insert, String v) {
    	
    		try {							// if integer, directly add to insert
			Integer.parseInt(v);
			insert += v + ", ";
		}
		catch (Exception e) {			// if not integer, also add quotation marks
			insert += "\"" + v + "\"" + ", ";
		}
    	
    		return insert;
    		
    }
    
    /**
     * returns insert statement with end bracket
     * @param insert insert statement
     * @return insert statement with end bracket -- ");"
     */
    private String insertEndBracket(String insert) {
    	
    		// remove last comma + add bracket
    		return insert.substring(0,insert.length()-2) + ");";
    		
    }

    /**
     * query data from database table
     * syntax: SELECT [column_name] FROM [table_name];
     * @param name database table name
     * @param col ArrayList of column(s) or "*" if all columns selected
     * @param end add special conditions
     * @return ResultSet of query results
     */
    public ResultSet select(String name, ArrayList<String> col, String end) {
        
        String select = "SELECT ";
        for (String c : col) {
        		select += c + ", ";
        }
        select = select.substring(0,select.length()-2) + " FROM " + name + " " + end;
        
        ResultSet result = exQuery(select);
        
        return result;
        
    }
    
    /**
     * query data from database table (all columns)
     * syntax: SELECT * FROM [table_name];
     * @param name database table name
     * @param end add special conditions
     * @return ResultSet of query results
     */
    public ResultSet selectAll(String name, String end) {
        
        ArrayList<String> col = new ArrayList<>();
        col.add("*");
        
        return select(name, col, end);

    }
    
    /**
     * create topic objects from queried data
     * @return ObservableList of Topic objects
     * @throws Exception
     */
    public ObservableList<Topic> reTopics() throws Exception {
        
        // query topics data
        ResultSet t = selectAll("topics", "");
        
        // create Topic objects and add to ArrayList
        ObservableList<Topic> topics = FXCollections.observableArrayList();
        while (t.next()) {
            // get subtopics
        		ObservableList<Subtopic> subtopics = reSubtopics(t.getInt("topicID"));
            
            // add topic
            Topic top = new Topic(t.getInt("topicID"), t.getString("name"), subtopics);
            topics.add(top);
        }
        
        return topics;
        
    }
    
    /**
     * create subtopic objects under topic
     * @param topicID topic number
     * @return ObservableList of subtopic objects under topic
     * @throws Exception 
     */
    private ObservableList<Subtopic> reSubtopics(int topicID) throws Exception {
        
        // query subtopics data
        ResultSet s = selectAll("subtopics", "WHERE topicID = " + topicID);
        
        // create Subtopic objects and add to ArrayList
        ObservableList<Subtopic> subtopics = FXCollections.observableArrayList();
        while (s.next()) {
            // get subsubtopics
            ObservableList<Subsubtopic> sstopics = reSStopics(s.getInt("topicID"), 
                    s.getInt("subtopicID"));
            
            // add subtopic
            Subtopic subtop = new Subtopic(s.getInt("topicID"), s.getInt("subtopicID"), 
                    s.getString("name"), sstopics);
            subtopics.add(subtop);
        }
        
        return subtopics;
        
    }
    
    /**
     * create subsubtopic objects under topic
     * @param topicID topic number
     * @param subtopID subtopic number
     * @return ObservableList of subsubtopic objects under subtopic
     * @throws Exception 
     */
    private ObservableList<Subsubtopic> reSStopics(int topicID, int subtopID) 
            throws Exception {
        
        // query subsubtopics data
        ResultSet ss = selectAll("subsubtopics", "WHERE topicID = " + topicID + 
                " AND subtopicID = " + subtopID);
        
        // create Subsubtopic objects and add to ArrayList
        ObservableList<Subsubtopic> sstopics = FXCollections.observableArrayList();
        while (ss.next()) {
            // get strands
            ObservableList<TopicStrand> strand = reStrands(ss.getInt("topicID"), 
                    ss.getInt("subtopicID"), ss.getInt("subsubtopicID"), 
                    ss.getString("startStrand"), ss.getString("endStrand"));
            
            // add subsubtopic
            Subsubtopic sstop = new Subsubtopic(ss.getInt("topicID"), ss.getInt("subtopicID"), 
                    ss.getInt("subsubtopicID"), ss.getString("name"), ss.getString("startStrand"), 
                    ss.getString("endStrand"), strand);
            sstopics.add(sstop);
        }
        
        return sstopics;
        
    }
    
    /**
     * create topic strand objects under topic
     * @param topicID topic number
     * @param subtopID subtopic number
     * @param sstopID subsubtopic number
     * @param start start strand
     * @param end end strand
     * @return ObservableList of topic strand objects under subsubtopic
     * @throws Exception 
     */
    private ObservableList<TopicStrand> reStrands(int topicID, int subtopID, 
            int sstopID, String start, String end) throws Exception {
        
        // query topic strands data
        String cond = "WHERE topicID = " + topicID + " AND subtopicID = " + subtopID;
        ResultSet ts = selectAll("topicstrands", cond + " AND subsubtopicID = " + sstopID);
        ResultSet tk = selectAll("topickeywords", cond);
        
        // create TopicStrand objects and add to ArrayList
        ObservableList<TopicStrand> strands = FXCollections.observableArrayList();
        while (ts.next()) {
            // add topic strand
            TopicStrand strand = new TopicStrand(ts.getInt("topicID"), ts.getInt("subtopicID"),
                    ts.getInt("subsubtopicID"), ts.getInt("strandID"), 
                    ts.getString("statement"), ts.getString("notes"), null, null);
            strands.add(strand);
        }
        
        // find topic strand object and add keywords
        while (tk.next()) {
            String key = tk.getString("keyword");
            for (TopicStrand t : strands) {
                if (t.getNum() == tk.getInt("strandID")) {
                    t.addKeyword(key);
                    break;
                }
            }
        }
        
        return strands;
        
    }
    
    /**
     * create paper objects
     * @return ObservableList of paper objects
     * @throws Exception 
     */
    public ObservableList<Paper> rePapers() throws Exception {
        
        // query paper data
        ResultSet p = selectAll("papers", "");
        
        // create Paper objects and add to ArrayList
        ObservableList<Paper> papers = FXCollections.observableArrayList();
        while (p.next()) {
            // get questions
            ObservableList<Question> qus = reQus(p.getString("paperID"));
            
            // add paper
            Paper pap = new Paper(p.getString("paperID"), p.getInt("year"), 
            		p.getString("month"), p.getInt("number"), p.getString("level"), qus);
            papers.add(pap);
        }
        
        return papers;
        
    }
    
    /**
     * create question objects under paper
     * @param paperID paper ID
     * @return ObservableList of question objects under paper
     * @throws Exception 
     */
    private ObservableList<Question> reQus(String paperID) throws Exception  {
        
        // query question and mark scheme data
        String cond1 = "WHERE paperID = \"" + paperID + "\"";
        ResultSet q = selectAll("questions", cond1);
        
        // create Paper objects and add to ArrayList
        ObservableList<Question> questions = FXCollections.observableArrayList();
        while (q.next()) {
            
        		char subquestionID = 0;
            if (q.getInt("subquestionID") != 0) {
            		subquestionID = (char) (q.getInt("subquestionID"));
            }
        	
            // get mark scheme
        		String cond2 = cond1 + " AND questionID = " + q.getInt("questionID");
            ResultSet m = selectAll("markschemes", cond2);
            
            Markscheme ms = null;
            if (m.next()) {
            		ms = new Markscheme(m.getInt("questionID"), subquestionID,  
                		m.getString("paperID"), m.getInt("marks"), m.getString("header"), 
                		m.getString("sampleAns"));
            }
            		
            // get related keywords
            ResultSet k = selectAll("questionkeywords", cond2);
            TreeMap<String, Integer> temp = new TreeMap<>();
            while (k.next()) {
            		temp.put(k.getString("keyword"), k.getInt("value"));
            }
            ObservableMap<String, Integer> keywords = FXCollections.observableMap(temp);
            
            // add question
            Question qu = new Question(q.getInt("questionID"), subquestionID, 
            		q.getString("PaperID"), q.getString("statement"), q.getString("visuals"), 
            		null, q.getInt("marks"), ms, keywords, null);
            questions.add(qu);
        }
        
        return questions;
        
    }
    
    /**
     * add related topic strands to Question objects
     * @param papers ObservableList of papers
     * @param topics ObservableList of topics
     * @return 
     */
    public void linkQu(ObservableList<Paper> papers, ObservableList<Topic> topics) throws Exception {	
    	
        // loop through papers
        for (Paper p : papers) {
            
        		// loop through questions in each paper
            for (Question q : p.getQuestions()) {
                
        			// get ResultSet of topic strands for question
        			String cond = "WHERE paperID = \"" + p.getPaperID() + 
        					"\" AND questionID = " + q.getQuestionID() + 
        					" AND subquestionID = " + q.getSubquestionIDInt();
            		ResultSet t = selectAll("topicquestions", cond);
            	
            		// add topic strands to question
                while (t.next()) {
                	
                		Topic topic = SearchUtil.searchTopic(topics, "num", Integer.toString(t.getInt("topicID")));
                	
                		// get topic strand and add to question
        				TopicStrand ts = topic.getTStrand(t.getInt("subtopicID"), t.getInt("subsubtopicID"), t.getInt("strandID"));
        				q.addRelStrand(ts);
                	
                }
                
            }
            
        }
        
    }
    
    /**
     * add related questions to TopicStrand objects
     * @param papers ArrayList of papers
     * @param topics ArrayList of topics
     */
    public void linkTS(ObservableList<Paper> papers, ObservableList<Topic> topics) 
    		throws Exception {
        
        // loop through topics
    		for (Topic t : topics) {
    			
    			// loop through subtopics
    			for (Subtopic st : t.getSubtopics()) {
    				
    				// loop through subsubtopics
    				for (Subsubtopic sst: st.getSubsubtopics()) {
    					
    					// loop through topic strands
    					for (TopicStrand ts : sst.getTStrands()) {
    						
    						// get ResultSet of topic strands for question
	    	        			String cond = "WHERE topicID = " + t.getNum() + 
	    	        					" AND subtopicID = " + st.getNum() + 
	    	        					" AND subsubtopicID = " + sst.getNum() +
	    	        					" AND strandID = " + ts.getNum();
	    	            		ResultSet qu = selectAll("topicquestions", cond);
    						
	    	            		while (qu.next()) {
	    	            			
	    	            			Paper p = SearchUtil.searchPaper(papers, qu.getString("paperID"));
	    	            			
	    	            			// get question and add to topic strand
            					Question q = p.getQuestion(qu.getInt("questionID"));
            					ts.addRelQus(q);
	    	            			
	    	            		}
	    	            		
    					}
    					
    				}
    				
    			}
    			
    		}
        
    }
	
}