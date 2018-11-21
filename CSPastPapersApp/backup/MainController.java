package address.view;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController extends AbstractController {

	// controllers
	@FXML private ChooseTopicsController chooseTopicsController;
	@FXML private ChoosePaperController choosePaperController;
	@FXML private ImportFilesController importFilesController;
	
//	@FXML private TabPane tabs;
	
	/**
	 * constructor
	 * (called before initialize() method, no access to @FXML fields referring to 
	 * components defined in FXML file)
	 */
	public MainController() {
		
	}
	
	/**
	 * initializes the controller class 
	 * (automatically called after FXML file has been loaded)
	 */
	@FXML
	public void initialize() {
		
		
		
		chooseTopicsController.injectMainController(this);
		choosePaperController.injectMainController(this);
		importFilesController.injectMainController(this);
		
	}
	
	/**
	 * method for setting up tab
	 */
	@Override
	public void setUpTab() {
		
//		tabs.selectionModelProperty().addListener(
//			(observable, oldTab, newTab) -> switchTab(newTab));
		
	}
	
	
	public void switchTabs() {
		
		
		
	}
	
}
