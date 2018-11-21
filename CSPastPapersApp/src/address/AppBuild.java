package address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import address.model.Database;
import address.model.ImportFile;
import address.model.Paper;
import address.model.Question;
import address.model.Subsubtopic;
import address.model.Subtopic;
import address.model.Topic;
import address.model.TopicStrand;
import address.util.FileUtil;
import address.util.StringUtil;
import au.com.bytecode.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * used for building the application (building database) -- not included in user package
 * @author snit71
 */
public class AppBuild {

	/**
     * main method (run by using class name -- AppBuild.main())
     * @param args 
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	
        //// SET UP DATABASE ////
        // syntax: jdbc:[subprotocol]:[subname]
    		// phpMyAdmin (http://www.phpmyadmin.co/index.php) server: sql12.freesqldatabase.com 3306
    		String user = "sql12228780";
		String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/" + user;
		String password = "he8DqRy8hb";
        Database db = new Database(url, user, password);
        
        // create list of topics
        ObservableList<Topic> topics = FXCollections.observableArrayList();
        
        // create list of papers
        ObservableList<Paper> papers = FXCollections.observableArrayList();
        
        // create tables in database
        createTables(db);
        
        
        //// IMPORT AND PARSE SUBJECT SYLLABUS GUIDE ////
        // parse table and convert to csv file using Tabula
        FileUtil.parseTable("files/subject-guide-extract.pdf", 
        		"files/subject-guide-extract.csv", 
        		"55,82,795,514", "CSV");
        
        // put CSV file information into TreeMap using CSVReader
        TreeMap<String, ArrayList<String>> csvMap = parseCSV();
        
        // parse topics list to create topic objects
        createTopics(topics, csvMap);
        
        
        //// IMPORT PAPER AND MARK SCHEME FILES ////
        String path = "files/import-files";
        File folder = new File(path);
        for (File file : folder.listFiles()) {
        		if (file.getName().contains(".pdf")) {
        			ImportFile im = new ImportFile(file);
        			im.processFile(papers, topics, db);
        		}
        }
        
        
        // add topics data to database
        addTopics(db, topics);
    }
	

	/**
	 * create tables in database
	 * @param db database
	 */
	public static void createTables(Database db) {
		
		LinkedHashMap<String, String> t = new LinkedHashMap<>();
	    t.put("topicID", "integer");
	    t.put("name", "text");
	    db.createTable("topics", 1, t);
	    
	    LinkedHashMap<String, String> st = new LinkedHashMap<>();
	    st.put("subtopicID", "integer");
	    st.put("topicID", "integer");
	    st.put("name", "text");
	    db.createTable("subtopics", 2, st);
	    
	    LinkedHashMap<String, String> sst = new LinkedHashMap<>();
	    sst.put("subsubtopicID", "integer");
	    sst.put("subtopicID", "integer");
	    sst.put("topicID", "integer");
	    sst.put("name", "text");
	    sst.put("startStrand", "text");
	    sst.put("endStrand", "text");
	    db.createTable("subsubtopics", 3, sst);
	    
	    LinkedHashMap<String, String> ts = new LinkedHashMap<>();
	    ts.put("strandID", "integer");
	    ts.put("subsubtopicID", "integer");
	    ts.put("subtopicID", "integer");
	    ts.put("topicID", "integer");
	    ts.put("statement", "text");
	    ts.put("notes", "text");
	    db.createTable("topicstrands", 4, ts);
	    
	    LinkedHashMap<String, String> tKey = new LinkedHashMap<>();
	    tKey.put("strandID", "integer");
	    tKey.put("subtopicID", "integer");
	    tKey.put("topicID", "integer");
	    tKey.put("keyword", "varchar(30)");
	    tKey.put("value", "integer");
	    db.createTable("topickeywords", 4, tKey);
	    
	    LinkedHashMap<String, String> tQu = new LinkedHashMap<>();
	    tQu.put("strandID", "integer");
	    tQu.put("subsubtopicID", "integer");
	    tQu.put("subtopicID", "integer");
	    tQu.put("topicID", "integer");
	    tQu.put("questionID", "integer");
	    tQu.put("subquestionID", "integer");
	    tQu.put("paperID", "varchar(30)");
	    db.createTable("topicquestions", 7, tQu);
	    
	    LinkedHashMap<String, String> p = new LinkedHashMap<>();
	    p.put("paperID", "varchar(30)");
	    p.put("year", "integer");
	    p.put("month", "varchar(30)");
	    p.put("number", "integer");
	    p.put("level", "varchar(30)");
	    db.createTable("papers", 1, p);
	    
	    LinkedHashMap<String, String> q = new LinkedHashMap<>();
	    q.put("questionID", "integer");
	    q.put("subquestionID", "integer");
	    q.put("paperID", "varchar(30)");
	    q.put("statement", "text");
	    q.put("visuals", "text"); //OLE object?
	    q.put("marks", "integer");
	    db.createTable("questions", 3, q);
	    
	    LinkedHashMap<String, String> ms = new LinkedHashMap<>();
	    ms.put("questionID", "integer");
	    ms.put("subquestionID", "integer");
	    ms.put("paperID", "varchar(30)");
	    ms.put("marks", "integer");
	    ms.put("header", "text");
	    ms.put("sampleAns", "text");
	    db.createTable("markschemes", 3, ms);
	    
	    LinkedHashMap<String, String> qKey = new LinkedHashMap<>();
	    qKey.put("questionID", "integer");
	    qKey.put("subquestionID", "integer");
	    qKey.put("paperID", "varchar(30)");
	    qKey.put("keyword", "varchar(30)");
	    qKey.put("value", "integer");
	    db.createTable("questionkeywords", 4, qKey);
		
	}
	
	/**
	 * parse CSV file into TreeMap using CSV Reader
	 * @return TreeMap for CSV file (strand num; ArrayList = strand info)
	 */
	public static TreeMap<String, ArrayList<String>> parseCSV () {

		File csvTOutline = new File("files/subject-guide-extract.csv");
		TreeMap<String, ArrayList<String>> csvMap = new TreeMap<>();
        
        try {
        	
        		// edited from http://www.javainterviewpoint.com/how-to-read-and-parse-csv-file-in-java/
        		FileReader fileReader = new FileReader(csvTOutline);
        		CSVReader csvReader = new CSVReader(fileReader, ',', '"', 1);
        		
        		String[] csvTemp = null;
        		
        		while ((csvTemp = csvReader.readNext()) != null) {
        			
        			if (csvTemp[0].contains(".")) {		// make sure row is not a heading
        				
        				try {
            				ArrayList<String> strandContent = new ArrayList<>();
            				strandContent.add(csvTemp[1].replaceAll("\n", " "));
            				strandContent.add(csvTemp[3].replaceAll("\n", " "));
            				csvMap.put(csvTemp[0].replace(" ", "."), strandContent);
            			}
            			catch (Exception e) {
            				e.printStackTrace();
            			}
        				
        			}
        			
        		}
        		
        		csvReader.close();
        	
        }
        catch (Exception e) {
        		e.printStackTrace();
        }
		
        return csvMap;
        
	}
	
	/**
	 * parse topics list to create topic objects
	 * @param topics ObservableList of topics
	 * @param csvMap
	 */
	public static void createTopics(ObservableList<Topic> topics, TreeMap<String, ArrayList<String>> csvMap) {
		
		File tList = new File("files/topics.txt");
        try {
        		
        		FileReader fileReader = new FileReader(tList);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = "";
			Topic prevTopic = null;
			Subtopic prevStopic = null;
			int sstCount = 0;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Top") || line.contains("Opt")) {			// topic
					
					String idStr = line.substring(4,5);
					int id = -1;
					try {
						id = Integer.parseInt(idStr);
					}
					catch (Exception e) {
						id = idStr.charAt(0) - 57;
					}
							
					String name = line.substring(8);
					
					Topic topic = new Topic(id, name);
					topics.add(topic);
					prevTopic = topic;
					
				}
				else if (line.contains("-")) {				// subtopic 
					
					int num = Integer.parseInt(line.substring(2,3));
					String name = line.substring(6);
					
					Subtopic stopic = new Subtopic(prevTopic.getNum(), num, name);
					prevTopic.addSubtopic(stopic);
					prevStopic = stopic;
					
					sstCount = 0;
					
				}
				else if (!line.trim().equals("")) {					// subsubtopic + strands
					
					String name = line.substring(0, line.indexOf("(")-1);
					String strandsTemp = line.substring(line.indexOf('(')+1, line.indexOf(')'));
					String[] strands = strandsTemp.split(", ");
					
					Subsubtopic sstopic = new Subsubtopic(prevTopic.getNum(), prevStopic.getNum(), sstCount, name, strands[0], strands[strands.length-1]);
					prevStopic.addSubsubtopic(sstopic);
					sstCount++;
					
					for (int i = 0; i < strands.length; i++) {
						String strandName = strands[i].trim();
						// String[] strandArray = strands[i].split(".");		bug with Eclipse
						String[] strandArray = {strandName.substring(0,strandName.indexOf(".")),
								strandName.substring(strandName.indexOf(".")+1,strandName.lastIndexOf(".")),
								strandName.substring(strandName.lastIndexOf(".")+1,strandName.length())};
						ArrayList<String> strandContent = csvMap.get(strandName);
						TopicStrand strand = new TopicStrand(prevTopic.getNum(), prevStopic.getNum(), sstopic.getNum(), Integer.parseInt(strandArray[2]), strandContent.get(0), strandContent.get(1));
						ObservableMap<String, Integer> keywords = StringUtil.getKeywords(strandContent.get(0) + " " + strandContent.get(1));
						strand.addKeywords(keywords);
						sstopic.addTStrand(strand);
					}
					
				}
			}
        	
			bufferedReader.close();
			
        }
        catch (Exception e) {
        		e.printStackTrace();
        }
		
	}
	
	/**
	 * add topic objects to database
	 * @param db database
	 * @param topics ObservableList of topics
	 */
	public static void addTopics (Database db, ObservableList<Topic> topics) {

		for (Topic topic : topics) {
        	
    		ArrayList<String> tInfo = new ArrayList<>(
    				Arrays.asList(Integer.toString(topic.getNum()), topic.getName()));
    		db.insert("topics", tInfo);
    	
    		for (Subtopic stopic : topic.getSubtopics()) {
    			
    			ArrayList<String> stInfo = new ArrayList<>(
        				Arrays.asList(Integer.toString(stopic.getNum()), 
        						Integer.toString(stopic.getTopic()), 
        						stopic.getName()));
        		db.insert("subtopics", stInfo);
    			
    			for (Subsubtopic sstopic : stopic.getSubsubtopics()) {
    				
    				ArrayList<String> sstInfo = new ArrayList<>(
            				Arrays.asList(Integer.toString(sstopic.getNum()), 
            						Integer.toString(sstopic.getSTopic()), 
            						Integer.toString(sstopic.getTopic()),
            						sstopic.getName(),
            						sstopic.getStartStrand(),
            						sstopic.getEndStrand()));
            		db.insert("subsubtopics", sstInfo);
    				
    				for (TopicStrand tstrand : sstopic.getTStrands()) {
    					
    					ArrayList<String> tsInfo = new ArrayList<>(
                				Arrays.asList(Integer.toString(tstrand.getNum()), 
                						Integer.toString(tstrand.getSSTopic()), 
                						Integer.toString(tstrand.getSTopic()), 
                						Integer.toString(tstrand.getTopic()),
                						tstrand.getStatement(),
                						tstrand.getNotes()));
                		db.insert("topicstrands", tsInfo);
    					
                		for (Map.Entry<String, Integer> key : tstrand.getKeywords().entrySet()) {
                			
                			ArrayList<String> tsKey = new ArrayList<>(
                    				Arrays.asList(Integer.toString(tstrand.getNum()), 
                    						Integer.toString(tstrand.getSTopic()), 
                    						Integer.toString(tstrand.getTopic()),
                    						key.getKey(), 
                    						Integer.toString(key.getValue())));
                			db.insert("topickeywords", tsKey);
                			
                		}
                		
                		for (Question qu : tstrand.getRelQus()) {
                			
                			ArrayList<String> tsQu = new ArrayList<>(
                					Arrays.asList(Integer.toString(tstrand.getNum()),
                							Integer.toString(tstrand.getSSTopic()),
                							Integer.toString(tstrand.getSTopic()),
                							Integer.toString(tstrand.getTopic()),
                							Integer.toString(qu.getQuestionID()),
                							Integer.toString(qu.getSubquestionIDInt()),
                							qu.getPaperID()));
                			db.insert("topicquestions", tsQu);
                			
                		}
                		
    				}
    				
    			}
    			
    		}
    	
    }
		
	}
	
}


//try {
//	
//	FileReader fileReader = new FileReader(csvTOutline);
//	BufferedReader bufferedReader = new BufferedReader(fileReader);
//	
//	String line = "";
//	while((line = bufferedReader.readLine()) != null) {
//		
//		String[] lineTemp = line.split(",");
//		
//		for (int i = 0; i < lineTemp.length; i++) {
//			
//			// don't get empty strings or single digit numbers (assessment objective)
//			if (lineTemp[i].length() <= 2) {
//				csvOutline.add(lineTemp[i].trim());
//			}
//			
//		}
//		
//		// remove headings
//		ArrayList<String> rm = new ArrayList<>();
//		rm.add("Assessment statement");
//		rm.add("Obj");
//		rm.add("Teacher’s notes");
//		csvOutline.removeAll(rm);
//		
//	}
//	
//	bufferedReader.close();
//	
//}
//catch (Exception e) {
//	e.printStackTrace();
//}



////create topic objects
//// convert pdf file to txt file using Zamzar
//File txtTOutline = new File("CSPastPapersApp/files/subject-guide-extract.txt");
//FileUtil.convertFile(apiKey, sOutline, txtTOutline);
//
//// put txt file information into ArrayList
//ArrayList<String> txtOutline = new ArrayList<>();
//try {
//	
//	FileReader fileReader = new FileReader(txtTOutline);
//	BufferedReader bufferedReader = new BufferedReader(fileReader);
//	
//	// only add to ArrayList if not header, footer or title heading
//	String line = "";
//	while((line = bufferedReader.readLine()) != null) {
//		if (!line.equals("") && !line.contains("Computer science guide") && 
//				!line.contains("Syllabus content") && 
//				!line.contains("Assessment statement")) {
//			txtOutline.add(line);
//		}
//	}
//	
//	bufferedReader.close();
//	
//}
//catch (Exception e) {
//	e.printStackTrace();
//}
//
//// create topic, subtopic and subsubtopic objects 
//for (String txt : txtOutline) {
//	
//		int prevTID = -1;
//		int prevSID = -1;
//		int ssTopicCount = 0;
//	
//		if (txt.contains("Topic") || txt.contains("Option")) {	// topic
//			
//			int start = txt.indexOf('—') + 1;
//			int end = txt.indexOf('(') - 1;
//			
//			int num = -1;
//			String numStr = txt.substring(start-2,start-1);
//			try {
//				num = Integer.parseInt(numStr);
//			}
//			catch (Exception e) {
//				num = numStr.charAt(0) - 57;
//			}
//			String name = txt.substring(start, end);
//			
//			Topic topic = new Topic(num, name);
//			topics.add(topic);
//			
//			prevTID = num;
//			prevSID = -1;
//			ssTopicCount = 0;
//			
//		}
//		else if (txt.contains("hours")) {						// subtopic
//			
//			int sID = Integer.parseInt(txt.substring(2,txt.indexOf(" ")));
//			
//			int start = txt.indexOf(" ") + 1;
//			int end = txt.indexOf("(") - 1;
//			String name = txt.substring(start, end);
//			
//			Topic topic = SearchUtil.searchTopic(topics, "num", Integer.toString(prevTID));
//			if (topic != null) {
//				Subtopic stopic = new Subtopic(prevTID, sID, name);
//				topic.addSubtopic(stopic);
//			}
//			
//			prevSID = sID;
//			ssTopicCount = 0;
//			
//		}
//		else if (!txt.substring(0,1).equals(" ")) {				// subsubtopic
//			
//			Topic topic = SearchUtil.searchTopic(topics, "num", Integer.toString(prevTID));
//			if (topic != null) {
//				Subtopic stopic = topic.getSubtopic(prevSID);
//				Subsubtopic sstopic = new Subsubtopic(prevTID, prevSID, ssTopicCount, txt.trim());
//				stopic.addSubsubtopic(sstopic);
//			}
//			
//			ssTopicCount++;
//			
//		}
//	
//}