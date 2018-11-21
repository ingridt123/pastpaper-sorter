package address;

import static java.util.Arrays.asList;

import java.util.List;

import address.model.Database;
import address.model.Paper;
import address.model.Topic;
import address.view.ChoosePaperController;
import address.view.ChooseTopicsController;
import address.view.ImportFilesController;
import address.view.TeacherDialogueController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author snit71
 */
public class AppRun extends Application {
	
	private Stage primaryStage;
	private TabPane rootLayout;
	private Stage dialogStage;
	
	// tab names
	private static final List<String> TABNAMES = asList("ChooseTopics", 
													   "ChoosePaper", 
													   "ImportFiles");
	
	private Database db;
	private boolean isTeacher;
	private ObservableList<Topic> topics;
	private ObservableList<Paper> papers;
	
	/**
	 * constructor
	 */
	public AppRun() {
		
		//// APPLICATION SETUP ////
		// set up database (syntax: jdbc:mysql://[host_name]:[port]/[dbname])
		// phpMyAdmin (http://www.phpmyadmin.co/index.php) server: sql12.freesqldatabase.com 3306
		String user = "sql12228780";
		String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/" + user;
		String password = "he8DqRy8hb";
		db = new Database(url, user, password);
	
	    // create list of topics and papers + link questions and strands
		try {
			topics = db.reTopics();
			papers = db.rePapers();
			db.linkQu(papers, topics);
			db.linkTS(papers, topics);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/**
     * main method
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	
	    // launch GUI
		launch(args);
		
    }
	
    /**
     * this method is automatically called when the application is launched from main method
     * edited from Code.Makery JavaFX 8 Tutorial (http://code.makery.ch/library/javafx-8-tutorial/)
     * 
     * @param primaryStage primary stage of application
     */
    @Override
    public void start(Stage primaryStage) {
        
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("CSPastPapersApp");
		
		initBlankRootLayout();
		showTeacherDialog();
		initRootLayout();
		loadTabs();
		
    }
    
    /**
     * initializes the blank root layout
     * edited from Code.Makery JavaFX 8 Tutorial (http://code.makery.ch/library/javafx-8-tutorial/)
     */
    private void initBlankRootLayout() {
    	
    		try {
    			
    			// load root layout from FXML file
    			FXMLLoader loader = new FXMLLoader();
    			loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
    			rootLayout = (TabPane) loader.load();
    			
    			// show the scene containing root layout
    			Scene scene = new Scene(rootLayout);
    			primaryStage.setScene(scene);
    			primaryStage.show();
    			
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	
    }
    
    /**
     * load teacher dialog
     * edited from Code.Makery JavaFX 8 Tutorial (http://code.makery.ch/library/javafx-8-tutorial/)
     */
    private void showTeacherDialog() {

    		try {
    			
    			// load dialog from FXML file
    			FXMLLoader loader = new FXMLLoader();
    			loader.setLocation(getClass().getResource("view/TeacherDialogue.fxml"));
    			AnchorPane dialog = (AnchorPane) loader.load();
    			
    			// create dialog stage
    			dialogStage = new Stage();
    			dialogStage.setTitle("Enter Teacher Code");
    			dialogStage.setResizable(false);
    			dialogStage.setOnCloseRequest(e -> e.consume());		// edited from https://stackoverflow.com/questions/8341305/how-to-remove-javafx-stage-buttons-minimize-maximize-close
    			dialogStage.initModality(Modality.WINDOW_MODAL);
    	        dialogStage.initOwner(primaryStage);
    	        
    	        // set dialog scene
    	        Scene dialogScene = new Scene(dialog);
    	        dialogStage.setScene(dialogScene);
    	        
    	        // give controller access to main app
    	        TeacherDialogueController controller = loader.getController();
    			controller.setApp(this);
    	        
    	        // show dialog and wait until user presses a button
    	        dialogStage.showAndWait();
    			
    		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * initializes the root layout (student / teacher)
     * edited from Code.Makery JavaFX 8 Tutorial (http://code.makery.ch/library/javafx-8-tutorial/)
     */
    private void initRootLayout() {
    	
    		try {
    			
    			// load root layout from FXML file
    			FXMLLoader loader = new FXMLLoader();
    			if (isTeacher) {
    				loader.setLocation(getClass().getResource("view/RootLayoutTeacher.fxml"));
    			}
    			else {
    				loader.setLocation(getClass().getResource("view/RootLayoutStudent.fxml"));
    			}
    			rootLayout = (TabPane) loader.load();
    			
    			// show the scene containing root layout
    			Scene scene = new Scene(rootLayout);
    			primaryStage.setScene(scene);
    			primaryStage.show();
    			
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	
    }
    
    /**
     * load tabs inside the root layout
     */
    private void loadTabs() {
    	
    		try {
    			
    			for (int i = 0; i < rootLayout.getTabs().size(); i++) {
    				
    				// load tab from FXML file
    				FXMLLoader loader = new FXMLLoader();
        			loader.setLocation(getClass().getResource("view/" + TABNAMES.get(i) + ".fxml"));
        			AnchorPane tab = (AnchorPane) loader.load();
        			
        			// set tabs onto root layout
        			rootLayout.getTabs().get(i).setContent(tab);
    				
        			// give controllers access to the main app
        			if (i == 0) {
        				ChooseTopicsController controller = loader.getController();
            			controller.setApp(this);
        			}
        			else if (i == 1) {
        				ChoosePaperController controller = loader.getController();
            			controller.setApp(this);
        			}
        			else if (i == 2) {
            			ImportFilesController controller = loader.getController();
            			controller.setApp(this);
        			}
        			
    			}
    			
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	
    }

    /**
     * returns the main stage
     * @return main stage
     */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
     * returns the dialog stage
     * @return dialog stage
     */
	public Stage getDialogStage() {
		return dialogStage;
	}
	
	/**
     * returns database
     * @return database
     */
	public Database getDB() {
		return db;
	}
	
	/**
     * sets whether this is teacher version
     * @param t true for teacher version
     */
	public void setIsTeacher(boolean t) {
		isTeacher = t;
	}
	
	/**
     * returns whether this is teacher version
     * @return true for teacher version
     */
	public boolean getIsTeacher() {
		return isTeacher;
	}
	
	/**
     * returns papers
     * @return ObservableList of papers
     */
	public ObservableList<Paper> getPapers() {
		return papers;
	}
	
	/**
     * returns topics
     * @return ObservableList of topics
     */
	public ObservableList<Topic> getTopics() {
		return topics;
	}
	
}