package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import csp.SecurePassword;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	public void sceneChange(ActionEvent event) throws IOException{
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
	public boolean SetKey(){
		if (Key.getText().length() == 32){
			return true;
		}else{
			return false;
		}
	}
	public void selectFolder(ActionEvent event) throws IOException{
		Stage stage = (Stage) AddFolder.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		File defaultDirectory = new File(IO.getUserDataDirectory());
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
	}
	public void removeFolder(){
		
	}
}
