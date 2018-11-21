package address.view;

import address.AppRun;
import address.model.Markscheme;
import address.model.Paper;
import address.model.Question;
import address.model.Topic;
import address.util.SearchUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public abstract class AbstractController {

	// reference to main application
	private AppRun appRun;
	
	private ObservableList<Topic> topics;
	private ObservableList<Paper> papers;
	private boolean isTeacher;
	
	/**
	 * constructor to create AbstractController object
	 */
	public AbstractController() {
		
	}
	
	/**
	 * abstract method for initializing the controller class
	 */
	@FXML public abstract void initialize();
	
	/**
	 * called by main application to give a reference back to itself
	 * @param app main application
	 */
	public void setApp(AppRun app) {
				
		appRun = app;
		topics = appRun.getTopics();
		papers = appRun.getPapers();
		isTeacher = appRun.getIsTeacher();
		
		setUpTab();
		
	}
	
//	/**
//	 * injects main controller
//	 * @param main
//	 */
//	public void injectMainController (MainController main) {
//		mainController = main;
//	}
	
	/**
	 * accessor method for app run
	 * @return app run
	 */
	public AppRun getAppRun() {
		return appRun;
	}
	
	/**
	 * accessor method for topics ObservableList
	 * @return topics ObservableList
	 */
	public ObservableList<Topic> getTopics() {
		return topics;
	}
	
	/**
	 * accessor method for papers ObservableList
	 * @return papers ObservableList
	 */
	public ObservableList<Paper> getPapers() {
		return papers;
	}
	
	/**
	 * accessor method for isTeacher boolean
	 * @return isTeacher boolean
	 */
	public boolean getIsTeacher() {
		return isTeacher;
	}
	
	/**
	 * abstract method for setting up tab
	 */
	public abstract void setUpTab();
	
	/**
	 * add gridPane to intervening lines between questions
	 * @param gridPane
	 * @param firstIndex
	 */
	public void addGrid(GridPane gridPane, int firstIndex) {
		
		GridPane addGrid = new GridPane();
		
		addGrid.setGridLinesVisible(false);
		addGrid.setStyle("-fx-border-radius:2");
		addGrid.setStyle("-fx-background-radius:2");
		double colWidth = gridPane.getColumnConstraints().get(1).getPrefWidth();
		addGrid.getColumnConstraints().add(new ColumnConstraints(colWidth));
		
		addGrid.add(new Text(), 0, 0, 1, 1);
		
		gridPane.add(addGrid, 1, firstIndex, 1, 1);
		
	}
	
	/**
	 * inserts text into GridPane
	 * @param gridPane 
	 * @param str text to be inserted
	 * @param color color of text
	 * @param col column number
	 * @param row row number
	 * @param colSpan column span
	 * @param rowSpan row span
	 */
    public void insertText(GridPane gridPane, String str, Color color, int col, int row, 
    		int colSpan, int rowSpan) {
		
		Text text = new Text(str);
		text.setFill(color);
		gridPane.add(text, col, row, colSpan, rowSpan);
		
		double width = 0.0;
		for (int i = 0; i < colSpan; i++) {			
			width += gridPane.getColumnConstraints().get(col+i).getPrefWidth();
		}
		text.setWrappingWidth(width);
		
	}
	
    /**
	 * called when user clicks on [marks] link
	 * @param gridPane
	 * @param linkIndex index of hyperlink
	 */
	@FXML
	public void handleMarks(GridPane gridPane, int linkIndex) {
		
		// check if mark scheme is currently displayed
		GridPane check = (GridPane) gridPane.getChildren().get(linkIndex+3);
		if (check.getChildren().size() <= 1) {
			
			// get corresponding Question object
			Text textPaperID = (Text) gridPane.getChildren().get(linkIndex-2);
			String paperID = textPaperID.getText();
			Text textQuestion = (Text) gridPane.getChildren().get(linkIndex-1);
			String question = textQuestion.getText();
			
			Paper p = SearchUtil.searchPaper(getAppRun().getPapers(), paperID);
			Question qu = null;
			for (Question q : p.getQuestions()) {
				
				if (q.getFullQus().equals(question)) {
					qu = q;
					break;
				}
				
				for (Question sq : q.getSubQus()) {
					
					if (sq.getFullQus().equals(question)) {
						qu = sq;
						break;
					}
					
				}
				
			}
			
			// get and display mark scheme
			if (qu != null) {
				
				Markscheme ms = qu.getMS();
				
				// check if mark scheme has been imported
				int addIndex = 0;
				check.getChildren().clear();
				check.add(new Text(), 0, addIndex, 1, 1);
				addIndex++;
				
				if (ms.getSampleAns().isEmpty()) {
					insertText(check, "Mark scheme not available.", Color.MEDIUMTURQUOISE, 0, addIndex, 1, 1);
				}
				else {
					
					if (!ms.getHeader().isEmpty()) {
						insertText(check, ms.getHeader(), Color.MEDIUMTURQUOISE, 0, addIndex, 1, 1);
						addIndex++;
						
						check.add(new Text(), 0, addIndex, 1, 1);
						addIndex++;
					}
					
					insertText(check, ms.getSampleAns(), Color.MEDIUMTURQUOISE, 0, addIndex, 1, 1);
					
				}
				
				addIndex++;
				check.add(new Text(), 0, addIndex, 1, 1);
				addIndex++;
				check.add(new Text(), 0, addIndex, 1, 1);
				
			}
			
		}
		else {
			
			check.getChildren().clear();
			check.add(new Text(), 0, 0, 1, 1);
			
		}
		
		
	}
    
}
