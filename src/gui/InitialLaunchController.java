package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import csp.SecurePassword;
import db.Database;
import io.IO;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class InitialLaunchController implements Initializable{
	
	public Button GetStarted, SetKey, GenerateKey, AddFolder, Finish, KeyBack, FolderBack;
	public TextField Key;
	public Label KeyStatus;
	public Database db;
	public boolean keyUpdate = false;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db = new Database();
		try {
			db.startDatabase();
			db.createUserTable();
			db.createFolderTable();
			keyUpdate = db.getUserCount();
		} catch (SQLException | IOException | BackingStoreException | InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
	}
	
	public void sceneChange(ActionEvent event) throws IOException, SQLException{
		Stage stage = null;
		Parent root = null;
		boolean success = true;
		
		if (event.getSource() == GetStarted){
			stage = (Stage) GetStarted.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("setKey.fxml"));
		}else if(event.getSource() == SetKey){
			if (SetKey() == true){
				stage = (Stage) SetKey.getScene().getWindow();
				root = FXMLLoader.load(getClass().getResource("addFolder.fxml"));
			}else{
				success = false;
				KeyStatus.setText("Key doesn't meet the requirements");
			}
		}else if (event.getSource() == Finish){
			stage = (Stage) Finish.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("main.fxml"));
		}else if (event.getSource() == KeyBack){
			keyUpdate = true;
			stage = (Stage) KeyBack.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
		}else if (event.getSource() == FolderBack){
			stage = (Stage) FolderBack.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("setKey.fxml"));
		}
		if (success == true){
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}
	public void generateKey(){
		String password = SecurePassword.SecureRandomAlphaNumericString();
		Key.setText(password);
	}
	public boolean SetKey() throws SQLException{
		if (Key.getText().length() == 32){
			if (keyUpdate == false){
				db.addToUserTable(Key.getText());
			}else{
				db.updateUserTable(Key.getText());
			}
			return true;
		}else{
			return false;
		}
	}
	public void selectFolder(ActionEvent event) throws IOException, SQLException{
		Stage stage = (Stage) AddFolder.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		File defaultDirectory = new File(IO.getUserDataDirectory());
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		if (selectedDirectory != null){
			boolean success = db.addFolderToTable(selectedDirectory.getAbsolutePath());
			if (!success){
				System.out.println("Failed to add");
			}
		}
	}
	public void removeFolder(){
		
	}
}
