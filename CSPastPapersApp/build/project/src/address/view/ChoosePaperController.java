package address.view;

import address.model.Paper;
import address.model.Question;
import address.util.SearchUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ChoosePaperController extends AbstractController {
	
	@FXML private ListView<String> paperList;
	@FXML private GridPane paperGrid;
	
	private ObservableList<String> paperNames;
	
	/**
	 * constructor
	 * (called before initialize() method, no access to @FXML fields referring to 
	 * components defined in FXML file)
	 */
	public ChoosePaperController() {
		
		super();
		paperNames = FXCollections.observableArrayList();
		
	}
	
	/**
	 * initializes the controller class 
	 * (automatically called after FXML file has been loaded)
	 */
	@FXML
	public void initialize() {
		
	}
	
	/**
	 * method for setting up tab
	 */
	@Override
	public void setUpTab() {
		
		for (Paper p : getPapers()) {
			paperNames.add(p.getPaperID());
		}
		paperList.setItems(paperNames);
		
		// listen for selection changes and show the person details when changed.
	    paperList.getSelectionModel().selectedItemProperty().addListener(
	    		(observable, oldPaper, newPaper) -> showPaperDetails(newPaper));
	}
	
	/**
	 * show paper questions
	 * @param newPaper
	 */
	@FXML
	public void showPaperDetails(String newPaper) {
		
		paperGrid.getChildren().clear();
		
		// find new paper
		Paper p = SearchUtil.searchPaper(getPapers(), newPaper);
		
		for (Question q : p.getQuestions()) {
			
			int firstIndex = paperGrid.getChildren().size();
			boolean isSubQu = ((int) q.getSubquestionID() != 0);
			
			// question paper ID
			if (!isSubQu) {
				insertText(paperGrid, q.getPaperID(), Color.BLACK, 0, firstIndex, 3, 1);
				firstIndex++;
			}
			
			// question statement
			int col = 0;
			int colSpan = 2;
			if (isSubQu) {
				col = 1;
				colSpan = 1;
			}
			insertText(paperGrid, q.getFullQus(), Color.BLACK, col, firstIndex, colSpan, 1);
			
			// question marks (hyperlink)
			if (q.getMarks() != -1) {
				
				Hyperlink marks = new Hyperlink("[" + q.getMarks() + "]");
				paperGrid.add(marks, 2, firstIndex, 1, 1);
				firstIndex++;
				marks.setOnAction((event) -> {
					int linkIndex = paperGrid.getChildren().indexOf(marks);
					handleMarks(paperGrid, linkIndex);
				});
			}
			
			// question visuals
			if (!q.getVisuals().isEmpty()) {
				String[] visuals = q.getVisuals().split("\n");
				for (int i = 0; i < visuals.length; i++) {
					insertText(paperGrid, visuals[i], Color.BLACK, 0, firstIndex, 2, 1);
					firstIndex++;
				}
			}
			
			// related strands
			if (getIsTeacher()) {
				insertText(paperGrid, q.getRelStrandString(), Color.LIGHTGRAY, 0, firstIndex, 2, 1);
				firstIndex++;
				
				insertText(paperGrid, q.getKeywordString(), Color.LIGHTGRAY, 0, firstIndex, 2, 1);
				firstIndex++;
			}
			
			addGrid(paperGrid, firstIndex);
			
		}
		
	}
	
}
