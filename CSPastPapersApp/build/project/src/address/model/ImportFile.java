package address.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import address.util.FileUtil;
import address.util.SearchUtil;
import address.util.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * An ImportFile object is created for each imported file
 * 
 * @author snit71
 */
public class ImportFile {
	
	private String name;							// name of file
    private File tFile;                         	// target file (converted)
    private String relPath;						// relative path
    private boolean isPaper;                    	// paper / mark scheme
    private String paperID;						// paper ID
    private int year;                        	// last two digits of paper year
    private String month;                      	// paper month
    private int num;                        		// paper number
    private String level;                      	// paper course level
    private ObservableList<String> text;			// store parsed text
    
    private static ObservableList<Paper> papers = null;
    private static ObservableList<Topic> topics = null;
    private static Database db = null;
    
    /**
     * constructor to create ImportFile object
     * @param f source file (original)
     */
    public ImportFile(File f) {
        
    		name = f.getName().substring(0, f.getName().indexOf("."));
        tFile = new File("files/import-files/" + name + ".txt");
        relPath = FileUtil.getRelativePath(f);
        
        // default values (from header information after parsing file)
        isPaper = false;
        paperID = "";
        year = -1;
        month = "";
        num = -1;
        level = "";
        
        text = FXCollections.observableArrayList();
        
    }
    
    /**
     * accessor method for file name
     * @return
     */
    public String getName() {
    		return name;
    }
    
    /**
     * process imported file
     * @param papers ObservableList of papers
     * @param topics ObservableList of topics
     * @param db database
     * @throws Exception
     */
    public int processFile(ObservableList<Paper> p, ObservableList<Topic> t, Database d) throws Exception {
    	
    		papers = p;
		topics = t;
		
		db = d;
    	
    		if (papers != null && topics != null && db != null) {
    		
    			// convert to txt file
        		FileUtil.convertToTXT(name, relPath);
        		
        		// parses information from file and adds to database
        		int headerErr = parse();
        		return headerErr;
    			
    		}
    		
    		return 0;
    	
    }
    
    /**
     * parses information from txt files and adds to database
     * @return 0 if successfully parsed file
     * @throws Exception
     */
    private int parse() throws Exception {
    		
    		try {
    			
    			FileReader fileReader = new FileReader(tFile);
    			BufferedReader bufferedReader = new BufferedReader(fileReader);
    			
    			String line = "";
    			while((line = bufferedReader.readLine()) != null) {
    				if (!line.equals("")) {
    					text.add(line);
    				}
    			}
    			
    			bufferedReader.close();
    			
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		// file validation using header of paper / mark scheme
    		int headerErr = parseHeader();
    		if (headerErr != 0) {
			return headerErr;
    		}
    		
    		// parse according to paper / mark scheme
    		if (isPaper) {
    			Paper p = parsePaper();
    			papers.add(p);
    		}
    		else {
    			parseMS();
    		}
    	
    		return 0;
    		
    }
    
    /**
     * parse header for information
     * header format: monthyear/_/subject/levelnum/language/timezone/_(/markscheme)
     * e.g. M16/4/COMSC/SP1/ENG/TZ0/XX(/M)
     * 
     * @return validity of file (-1 if wrong subject
     * 							-2 if wrong language, 
     * 							-3 if corresponding paper hasn't been imported)
     */
    private int parseHeader() {
    		
    		String h = text.get(0);
    		String[] header = h.split("/");
    		
    		if (!header[2].equals("COMSC")) {			// wrong subject
    			return -1;
    		}
    		else if (!header[4].equals("ENG")) {			// wrong language
    			return -2;
    		}
    		
    		// check if paper (default is false)
    		if (header.length == 7) {
    			isPaper = true;
    		}
    		
    		// get year
    		year = Integer.parseInt(header[0].substring(1));
    		
    		// get month
    		String tempMonth = header[0].substring(0,1);
    		if (tempMonth.equals("M")) {
    			month = "5";
    		}
    		else if (tempMonth.equals("N")) {
    			month = "11";
    		}
    		
    		// get num
    		num = Integer.parseInt(header[3].substring(2));
    		
    		// get level
    		String tempLevel = header[3].substring(0,1);
    		if (tempLevel.equals("S")) {
    			level = "1";
    		}
    		else if (tempLevel.equals("H")) {
    			level = "2";
    		}
    		paperID = header[0] + "/" + header[3] + "/" + header[5];
    		
    		// check if corresponding paper has been imported
    		if (!isPaper && SearchUtil.searchPaper(papers, paperID) == null) {
    			return -3;
    		}
    		
    		return 0;
    		
    }
	
    /**
     * parse paper from txt file
     * @param db database
     * @return parsed paper
     * @throws Exception
     */
	private Paper parsePaper() throws Exception {
		
//		int index = text.indexOf("Section A");
		Pattern p1 = Pattern.compile("\\A\\s*Section A\\s*\\z");
		Matcher m1;
		int index = 0;
		for (String t : text) {
			m1 = p1.matcher(t);
			if (m1.find()) {
				index = text.indexOf(t);
			}
		}
		
		Paper paper = new Paper(paperID, year, month, num, level, null);
		ObservableList<Question> questions = FXCollections.observableArrayList();
		Question prevQu = null;
		
		int line = index + 2;
		while (line < text.size()) {

			String t = text.get(line);
			Pattern p2 = Pattern.compile("\\([a-z]\\)");
			Matcher m2 = p2.matcher(t);
			
			// is start of question if first character is a digit
			if (Character.isDigit(t.charAt(0)) || m2.find()) {
				
				// find full question without visuals + marks -- last occurrence of '.'
				// if current line doesn't contain [marks] AND next line contains '.'
				Pattern p3 = Pattern.compile("\\[\\d\\]\\Z");
				Matcher m3 = p3.matcher(t);
//				Pattern p4 = Pattern.compile("\\w\\.");
//				Matcher m4 = p4.matcher(text.get(line+1));
				while (!m3.find()) {
					line++;
					t = t.trim() + " " + text.get(line).trim();
					m3 = p3.matcher(t);
					if (line+1 < text.size()) {
//						m4 = p4.matcher(text.get(line+1));
					}
				}
				t = t.replaceAll("\t", " ");
				
				// get question number -- either number. or (letter)
				Pattern p5 = Pattern.compile("\\d\\.|\\([a-z]\\)");
				Matcher m5 = p5.matcher(t);
				m5.find();
				int endIndex = m5.end();
				String id = t.substring(0,endIndex).replaceAll("[\\.\\(\\)]", "");
				t = t.substring(endIndex).trim();
				
				// get question statement
				endIndex = t.lastIndexOf('.')+1;
				String statement = t.substring(0, endIndex);
				t = t.substring(endIndex).trim();
				
				// find visuals + marks -- must end with [marks] or next line (letter)
				if (!t.contains("[")) {
					Pattern p6 = Pattern.compile("\\[\\d\\]\\Z");
					Matcher m6 = p6.matcher(t);
					Pattern p7 = Pattern.compile("\\([a-z]\\)");
					Matcher m7 = p7.matcher(text.get(line+1)); // TODO if (line+1 < text.size())??
					
					while (!m6.find() && !m7.find()) {
						t = t.trim() + "\n" + text.get(line);
						line++;
						m6 = p6.matcher(t);
						if (line+1 < text.size()) {
							m7 = p7.matcher(text.get(line+1));
						}
					}
					t = t.trim();
				}
				
				// get question marks (-1 if has subquestions)
				int marks = -1;
				try {
					int startIndex = t.indexOf("[");
					marks = Integer.parseInt(t.substring(startIndex,t.length()).replaceAll("[\\[\\]]", ""));
					t = t.substring(0,startIndex).replaceAll("\\s+$", "");
				}
				catch (Exception e) {
				}
				
				// get question visuals
				String visuals = t;
				
				// question keywords
				ObservableMap<String, Integer> keywords = StringUtil.getKeywords(statement);
				
				Question qu = null;
				
				// create Question and add to Paper or prevQu ObservableList
				try {
					Integer.parseInt(id);				// check if question
					qu = new Question(Integer.parseInt(id), (char) 0, paperID, statement, visuals, null, marks, null, keywords, null);
					questions.add(qu);
					prevQu = qu;	
				}
				catch (NumberFormatException e) {		// subquestion
					qu = new Question(prevQu.getQuestionID(), id.charAt(0), paperID, statement, visuals, null, marks, null, keywords, null);
					prevQu.addSubQu(qu);
				}
				
				// question-topic relationships
				ObservableList<TopicStrand> relStrands = getRelStrands(qu);
				qu.addRelStrands(relStrands);
				
			}
			
			line++;
			
		}
		
		paper.setQuestions(questions);
		addToDB(paper, null);
		return paper;
		
	}
	
	/**
	 * parse mark scheme from txt file
	 * @param db database
	 * @param papers ObservableList of papers
	 * @throws Exception 
	 */
	private void parseMS() throws Exception {
		
		Pattern p1 = Pattern.compile("\\A\\s*Section A\\s*\\z");
		Matcher m1;
		int index = 0;
		for (String t : text) {
			m1 = p1.matcher(t);
			if (m1.find()) {
				index = text.indexOf(t);
				break;
			}
		}
		Question prevQu = null;
		
		int line = index + 2;
		while (line < text.size()) {
			
			String t = text.get(line);
			Pattern p2 = Pattern.compile("\\([a-z]\\)");
			Matcher m2 = p2.matcher(t);
			
			// is start of question if first character is a digit
			if (Character.isDigit(t.charAt(0)) || m2.find()) { 
				
				// find full question -- must end with [marks] or next line (letter)
				Pattern p3 = Pattern.compile("\\[\\d\\]\\Z");
				Matcher m3 = p3.matcher(t);
				Pattern p4 = Pattern.compile("\\([a-z]\\)");
				Matcher m4 = p4.matcher(text.get(line+2));
				
				while (!m3.find() && !m4.find()) {
					line++;
					t = t.trim() + "\n" + text.get(line).trim();
					m3 = p3.matcher(t);
					if (line+2 < text.size()) {
						m4 = p4.matcher(text.get(line+2));
					}
				}
				t = t.trim();
				
				// get question number -- number.
				Pattern p5 = Pattern.compile("\\d\\.|\\([a-z]\\)");
				Matcher m5 = p5.matcher(t);
				m5.find();
				int endIndex = m5.end();
				String id = t.substring(0,endIndex).replaceAll("[\\.\\(\\)]", "");
				t = t.substring(endIndex).trim();
				
				// get mark scheme header -- starts with Award and ends with . / first "\n"
				String header = "";
				if (t.contains("Award")) {
					endIndex = t.indexOf("\n");
					header = t.substring(0,endIndex).trim();
					t = t.substring(endIndex).trim();
				}
				
				// get mark scheme marks
				int startIndex = t.indexOf("[");
				t = t.substring(0,startIndex).replaceAll("\\s+$", "");
				
				// get mark scheme sample answer
				String sampleAns = t;
				
				// add to question's mark scheme
				for (Paper paper : papers) {
					// get the right paper then find question to add mark scheme
					if (paper.getYear() == year && paper.getMonth().equals(month) 
							&& paper.getNum() == num && paper.getLevel().equals(level)) {
						
						Markscheme ms = null;
						try {
							Question qu = paper.getQuestion(Integer.parseInt(id));	// check if question/subquestion
							if (qu != null) {
								ms = qu.getMS();
								prevQu = qu;
							}
						}
						catch (NumberFormatException e) {
							ms = prevQu.getSubQu(id.charAt(0)).getMS();
						}
						
						if (ms != null) {
							ms.setHeader(header);
							ms.setSampleAns(sampleAns);
							addToDB(null, ms);
						}						
						
						break;
					}
					
				}
				
			}
			
			line++;
			
		}
		
	}
    
    /**
     * insert imported files info into database
     * @param paper
     * @param ms
     * @throws Exception 
     */
    private void addToDB(Paper paper, Markscheme ms) throws Exception {
    	
        if (isPaper) {
        		
        		// create ArrayList for values
        		ArrayList<String> pValues = new ArrayList<>();
        		pValues.add(paperID);									// paper ID
        		pValues.add(Integer.toString(year));						// paper year
        		pValues.add(month);										// paper month
        		pValues.add(Integer.toString(num));						// paper number
        		pValues.add(paperID.substring(paperID.length()-1));		// paper level (1/2)
        		
        		// insert paper into database
        		db.insert("papers", pValues);
        		
        		// loop through questions in paper
        		for (Question q : paper.getQuestions()) {
        			
        			// create ArrayList for values 
        			ArrayList<String> qValues = getQValues(q, paperID);
        			
        			// insert question into database 
        			db.insert("questions", qValues);
        			
        			// add subquestions (if question has subquestions)
        			if (q.getSubQus() != null) {
        				
        				for (Question s : q.getSubQus()) {

        					// create ArrayList for values
        					ArrayList<String> sValues = getQValues(s, paperID);
        					
        					// insert subquestion into database
        					db.insert("questions", sValues);
        					
        				}
        			}
        			
        			// add question keywords (if question has keywords)
        			if (q.getKeywords() != null) {
        				
        				for (Map.Entry<String, Integer> k : q.getKeywords().entrySet()) {
        					
        					// create ArrayList for values
        					ArrayList<String> kValues = new ArrayList<>();
        					kValues.add(Integer.toString(q.getQuestionID()));
        					String sqID = "0";
        					if ((char) q.getSubquestionID() != 0) {
        						sqID = Integer.toString(q.getSubquestionIDInt());
        					}
        					kValues.add(sqID);
        					kValues.add(paperID);
        					kValues.add(k.getKey());
        					kValues.add(Integer.toString(k.getValue()));
        					
        					// insert keyword into database
        					db.insert("questionkeywords", kValues);
        					
        				}
        			}
        			
        		}
        		
        }
        else {
        		
        		// create ArrayList for values
        		ArrayList<String> mValues = new ArrayList<>();
        		mValues.add(Integer.toString(ms.getQuestionID()));
        		mValues.add(Integer.toString(ms.getSubquestionIDInt()));
        		mValues.add(paperID);
        		mValues.add(Integer.toString(ms.getMarks()));
        		mValues.add(ms.getHeader());
        		mValues.add(ms.getSampleAns());
        	
        		// insert mark scheme into database
        		db.insert("markschemes", mValues);
        		
        }
        
    }
    
    /**
     * get values of question needed to insert into database
     * @param qu Question object
     * @param paperID paper ID
     * @return ArrayList of values for question
     * @throws Exception 
     */
    private ArrayList<String> getQValues(Question qu, String paperID) throws Exception {
    	
    		ArrayList<String> qValues = new ArrayList<>();
    		
		qValues.add(Integer.toString(qu.getQuestionID()));		// question ID
		String sqID = "0";
		if ((int) qu.getSubquestionID() != 0) {
			char tempChar = qu.getSubquestionID();
			int tempInt = (int) (tempChar - 96);
			if (tempInt > 0 ) {
				sqID = Integer.toString(tempInt);
			}
		}
		qValues.add(sqID);										// subquestion ID
		qValues.add(paperID);									// paper ID
		qValues.add(qu.getStatement());							// question statement
		qValues.add(qu.getVisuals());							// question visuals
		qValues.add(Integer.toString(qu.getMarks()));			// question marks
		
		return qValues;
    	
    }
    
    /**
     * gets related strands of question + links topic strands to questions
     * @param qu question
     * @return ObservableList of related topic strands
     */
    private ObservableList<TopicStrand> getRelStrands(Question qu) {
    	
    		ObservableMap<String, Integer> keywords = qu.getKeywords();
    		TreeMap<Integer, ArrayList<TopicStrand>> probabilities = new TreeMap<>(Collections.reverseOrder());
    	
    		// compare question and topic strand keywords
    		ObservableList<Topic> tempTopics = FXCollections.observableArrayList();
    		if (qu.getPaperID().contains("P1")) {			// paper 1
    			for (int i = 0; i < 7; i++) {
    				tempTopics.add(topics.get(i));
    			}
    		}
    		else if (qu.getPaperID().contains("P2")) {		// paper 2
    			for (int i = 7; i < 11; i++) {
    				tempTopics.add(topics.get(i));
    			}
    		}
    		
		// loop through the topic strands
		for (Topic t : tempTopics) {
			for (Subtopic st : t.getSubtopics()) {
				for (Subsubtopic sst : st.getSubsubtopics()) {
					for (TopicStrand ts : sst.getTStrands()) {
						
						// probability (add up values of matching question and topic strand keywords)
						int prob = 0;
						
						for (Map.Entry<String, Integer> tKey : ts.getKeywords().entrySet()) {
							
							// find match between topic and question keywords
							if (keywords.containsKey(tKey.getKey())) {
								prob += keywords.get(tKey.getKey()) + tKey.getValue();
							}
							
						}
						
						if (prob != 0) {
							if (probabilities.containsKey(prob)) {
								probabilities.get(prob).add(ts);
							}
							else {
								ArrayList<TopicStrand> temp = new ArrayList<>();
								temp.add(ts);
								probabilities.put(prob, temp);
							}
						}
						
					}
				}
			}
			
		}
    		
		// first add topic strand(s) of highest probability to tempSet
		ObservableList<TopicStrand> relStrands = FXCollections.observableArrayList();
		while (probabilities.size() > 0 && relStrands.size() < 1) {
			
			for (TopicStrand ts : probabilities.firstEntry().getValue()) {
				relStrands.add(ts);
				ts.addRelQus(qu);
			}
			probabilities.remove(probabilities.firstEntry().getKey());
			
		}
		
    		return relStrands;
    	
    }
    
    
}