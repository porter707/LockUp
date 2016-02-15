package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import csp.SecurePassword;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Controller implements Initializable{
	
	public Button AddFolder, RemoveFolder, ChangeKey, SetKey, GenerateKey, Back;
	public TextField Key;
	public Label KeyStatus;
	public ListView<String> Folders, Files;
    public List<String> FoldersList, FilesList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		//setFolderView();
		//setFileView();
	}
	
	public void sceneChange(ActionEvent event) throws IOException{
		Stage stage = null;
		Parent root = null;
		boolean success = true;
		
		if (event.getSource() == ChangeKey){
			stage = (Stage) ChangeKey.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("changeKey.fxml"));
		}else if (event.getSource() == SetKey){
			if (SetKey() == true){
				stage = (Stage) SetKey.getScene().getWindow();
				root = FXMLLoader.load(getClass().getResource("main.fxml"));
			}else{
				success = false;
				KeyStatus.setText("Key doesn't meet the requirements");
			}
		}else if (event.getSource() == Back){
			stage = (Stage) Back.getScene().getWindow();
			root = FXMLLoader.load(getClass().getResource("main.fxml"));
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
		chooser.setTitle("JavaFX Projects");
		//File defaultDirectory = new File("c:/dev/javafx");
		//chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		//System.out.println(selectedDirectory);
	}
	public void removeFolder(){
		
	}
	public void setFolderView(){
        FoldersList = Arrays.asList("Mega", "Dropbox", "Google Drive");

        Folders.setItems(FXCollections.observableList(FoldersList));
	}
	public void setFileView(){
        FilesList = Arrays.asList("image01.jpg", "CV.doc", "Image-2.png");

        Files.setItems(FXCollections.observableList(FilesList));
	}
	public void updateFolderView(){
		
	}
	public void updateFileView(){
		
	}
}
