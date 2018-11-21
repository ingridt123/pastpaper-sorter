package address.view;

import java.io.File;

import address.model.Database;
import address.model.ImportFile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportFilesController extends AbstractController {
	
	@FXML private Label fileName;
	
	private ImportFile addedFile;
	private Database database;
	
	
	/**
	 * constructor
	 * (called before initialize() method, no access to @FXML fields referring to 
	 * components defined in FXML file)
	 */
	public ImportFilesController() {
		
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
		
		database = getAppRun().getDB();
		
	}
	
	/**
	 * called when user clicks on "choose file" button
	 */
	@FXML
	private void handleChooseFile() {
		
		// choose file from directory
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showOpenDialog(getAppRun().getPrimaryStage());
		if (selectedFile != null) {
			addedFile = new ImportFile(selectedFile);
			fileName.setText(selectedFile.getName());
		}
		
	}
	
	/**
	 * called when user clicks on "trash" button
	 */
	@FXML
	private void handleTrash() {
		
		addedFile = null;
		fileName.setText("No file chosen");
		
	}
	
	/**
	 * called when user clicks on "upload file" button
	 * @throws Exception
	 */
	@FXML
	private void handleUploadFile() throws Exception {
		
		// imports file
		if (addedFile == null) {
			message(AlertType.ERROR, "File Error", "No file chosen.");
		}
		else if (addedFile.getName().contains("/")) {
			message(AlertType.ERROR, "File Error", "File name cannot contain \"/\"");
		}
		else {
			int headerErr = addedFile.processFile(getPapers(), getTopics(), database);
			if (headerErr != 0) {
				String message = "";
				if (headerErr == -1) {
		    			message = "The file uploaded is from the wrong subject.";
		    		}
		    		else if (headerErr == -2) {
		    			message = "The file uploaded is in the wrong language.";
		    		}
		    		else if (headerErr == -3) {
		    			message = "The corresponding paper of this mark scheme has not been imported";
		    		}
				message(AlertType.ERROR, "File Error", message);
			}
			else {
				// alert user to reopen application to update data
				message(AlertType.WARNING, "Database Update!", "Please reopen this application to update application data.");
			}
			
			fileName.setText("No file chosen");
		}
		
		// set addedFile to null
		addedFile = null;
		
	}
	
	/**
	 * creates error message
	 * from @link http://code.makery.ch/library/javafx-8-tutorial/
	 * 
	 * @param message error message
	 */
	@FXML
	public void message(AlertType type, String title, String message) {
		
		Alert error = new Alert(type);
		error.initOwner(getAppRun().getPrimaryStage());
		error.setTitle(title);
		error.setHeaderText("");
		error.setContentText(message);
		
		error.showAndWait();
		
	}
	
}
