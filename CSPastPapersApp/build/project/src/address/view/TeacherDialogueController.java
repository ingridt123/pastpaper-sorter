package address.view;

import address.AppRun;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TeacherDialogueController {

	@FXML TextField codeEnter;
	@FXML Label errorMsg;
	@FXML Button student;
	@FXML Button teacher;
	
	String code = "hellocis";
	
	// reference to main application
	private AppRun appRun;
	
	/**
	 * constructor
	 * (called before initialize() method, no access to @FXML fields referring to 
	 * components defined in FXML file)
	 */
	public TeacherDialogueController() {
		
	}
	
	/**
	 * initializes the controller class 
	 * (automatically called after FXML file has been loaded)
	 */
	@FXML
	public void initialize() {
		
	}
	
	/**
	 * called by main application to give a reference back to itself
	 * @param app main application
	 */
	public void setApp(AppRun app) {
				
		appRun = app;
		
	}
	
	/**
	 * called when user clicks on "i am a student" button
	 */
	@FXML
	private void handleStudent() {
		
		appRun.setIsTeacher(false);
		appRun.getDialogStage().close();
		
	}
	
	/**
	 * called when user clicks on "i am a teacher" button
	 */
	@FXML
	private void handleTeacher() {
		
		if (codeEnter.getText().isEmpty()) {
			errorMsg.setText("Teacher code cannot be empty.");
		}
		else if (!codeEnter.getText().equals(code)) {
			errorMsg.setText("Invalid teacher code.");
		}
		else {
			errorMsg.setText("");
			appRun.setIsTeacher(true);
			appRun.getDialogStage().close();
		}
		
	}
	
}
