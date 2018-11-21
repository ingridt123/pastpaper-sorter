package address.view;

import address.model.Question;
import address.model.Subsubtopic;
import address.model.Subtopic;
import address.model.Topic;
import address.model.TopicStrand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ChooseTopicsController extends AbstractController {
	
	@FXML private TreeView<String> tree;
	@FXML private GridPane topicsGrid;
	
	private ObservableList<TopicStrand> chosenStrands;
	
	private String curMode;
	
	/**
	 * constructor
	 * (called before initialize() method, no access to @FXML fields referring to 
	 * components defined in FXML file)
	 */
	public ChooseTopicsController() {
		
		// set default mode to show keywords
		curMode = "keywords";
		
	}
	
	/**
	 * initializes the controller class 
	 * (automatically called after FXML file has been loaded)
	 */
	@FXML
	public void initialize() {
		
		// create and set root node (will not be shown)
		TreeItem<String> dummyRoot = new TreeItem<>("dummy");
		dummyRoot.setExpanded(false);
		tree.setRoot(dummyRoot);
		tree.setShowRoot(false);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
	}
		
	/**
	 * method for setting up tab
	 */
	@Override
	public void setUpTab() {
		
		chosenStrands = FXCollections.observableArrayList();
		
		// add children
		setTree();
		
		// listen for selection changes and show the person details when changed.
	    tree.getSelectionModel().selectedItemProperty().addListener(
	    		(observable, oldTopics, newTopics) -> {
	    			if (curMode.equals("questions")) {
		    			handleGetQuestions();
		    		}
		    		else {
		    			curMode = "keywords";
		    			handleGetKeywords();
		    		}
	    		});
	}
	
	
	/**
	 * set up TreeView
	 */
	private void setTree() {
		
		for (Topic t : getTopics()) {
			
			TreeItem<String> topic = new TreeItem<>(t.getFullTopic());
			tree.getRoot().getChildren().add(topic);
			
			for (Subtopic st : t.getSubtopics()) {
				
				TreeItem<String> stopic = new TreeItem<>(st.getFullTopic());
				topic.getChildren().add(stopic);
				
				for (Subsubtopic sst : st.getSubsubtopics()) {
				
					TreeItem<String> sstopic = new TreeItem<>(sst.getFullTopic());
					stopic.getChildren().add(sstopic);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * called when user clicks on "check all" hyperlink
	 */
	@FXML
	private void handleCheckAll() {
		
		tree.getSelectionModel().selectAll();
		
	}
	
	/**
	 * called when user clicks on "get keywords" button
	 */
	@FXML
	private void handleGetKeywords() {		
		
		handleButton("keywords");
		
		// display information for each topic strand
		for (TopicStrand ts : chosenStrands) {
			
			int firstIndex = topicsGrid.getChildren().size();
			
			// topic number
			insertText(topicsGrid, ts.getFullNum(), Color.BLACK, 0, firstIndex, 1, 1);
			
			// topic statement
			insertText(topicsGrid, ts.getStatement(), Color.BLACK, 1, firstIndex, 2, 1);
			firstIndex++;
			
			// topic notes (if exists)
			if (!ts.getNotes().isEmpty()) {
				insertText(topicsGrid, ts.getNotes(), Color.BLUEVIOLET, 1, firstIndex, 2, 1);
				firstIndex++;
			}
			
			// topic keywords
			insertText(topicsGrid, ts.getKeywordString(), Color.LIGHTGRAY, 1, firstIndex, 2, 1);
			firstIndex++;
			
			topicsGrid.add(new Text(), 0, firstIndex, 3, 1);
			
		}
		
	}
	
	/**
	 * called when user clicks on "get questions" button
	 */
	@FXML
	private void handleGetQuestions() {
		
		handleButton("questions");

		ObservableList<Question> displayedQuestions = FXCollections.observableArrayList();
		
		// display information for questions related to chosen topic strands
		for (TopicStrand ts : chosenStrands) {
			
			for (Question q : ts.getRelQus()) {
				
				if (!displayedQuestions.contains(q)) {
					
					displayedQuestions.add(q);
					
					int firstIndex = topicsGrid.getChildren().size();
					
					// question paper ID
					insertText(topicsGrid, q.getPaperID(), Color.BLACK, 0, firstIndex, 3, 1);
					firstIndex++;
					
					// question statement
					String statement = q.getFullQus();
					if ((int) q.getSubquestionID() != 0) {
						statement = q.getSubquestionID() + statement;
					}
					insertText(topicsGrid, statement, Color.BLACK, 0, firstIndex, 2, 1);
					
					// question marks (hyperlink)
					Hyperlink marks = new Hyperlink("[" + q.getMarks() + "]");
					topicsGrid.add(marks, 2, firstIndex, 1, 1);
					firstIndex++;
					marks.setOnAction((event) -> {
					int linkIndex = topicsGrid.getChildren().indexOf(marks);
					handleMarks(topicsGrid, linkIndex);
					});
					
					// question visuals
					if (!q.getVisuals().isEmpty()) {
						insertText(topicsGrid, q.getVisuals(), Color.BLACK, 0, firstIndex, 3, 1);
						firstIndex++;
					}
					
					// related strands
					insertText(topicsGrid, q.getRelStrandString(), Color.LIGHTGRAY, 0, firstIndex, 3, 1);
					firstIndex++;
						
					// related keywords
					insertText(topicsGrid, q.getKeywordString(), Color.LIGHTGRAY, 0, firstIndex, 3, 1);
					firstIndex++;
					
					addGrid(topicsGrid, firstIndex);
					
				}
				
			}
			
		}
		
		if (displayedQuestions.isEmpty()) {
			insertText(topicsGrid, "No related questions.", Color.BLACK, 0, 0, 3, 1);
		}
		
	}
	
	
	private void handleButton(String mode) {
		
		curMode = mode;
		
		topicsGrid.getChildren().clear();
		// TODO add scroll to top?
		
		compileStrandsList();
		
	}
	
	/**
	 * compile ObservableList of chosen topic strands
	 */
	private void compileStrandsList() {
		
		ObservableList<TreeItem<String>> chosenItems = tree.getSelectionModel().getSelectedItems();
		chosenStrands.clear();
		
		for (TreeItem<String> i : chosenItems) {
			
			int indent = tree.getTreeItemLevel(i);
			String name = i.getValue();
			
			for (Topic t : getTopics()) {
				
				// find correct topic -- if TreeItem is a topic (indent = 1)
				if (indent != 1 || t.getFullTopic().equals(name)) {
					
					for (Subtopic st : t.getSubtopics()) {
						
						// find correct subtopic -- if TreeItem is a subtopic (indent = 2)
						if (indent != 2 || st.getFullTopic().equals(name)) {
							
							for (Subsubtopic sst : st.getSubsubtopics()) {
								
								// find correct subsubtopic -- if TreeItem is a subsubtopic (indent = 3)
								if (indent != 3 || sst.getFullTopic().equals(name)) {
									
									for (TopicStrand ts : sst.getTStrands()) {
										
										chosenStrands.add(ts);
										
									}
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}