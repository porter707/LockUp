package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import csp.SecurePassword;
import db.Database;
import io.IO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sync.LockUpEngine;
import sync.folderWatch;

public class Controller implements Initializable{
	
	public Button AddFolder, RemoveFolder, ChangeKey, SetKey, GenerateKey, Back;
	public TextField Key;
	public Label KeyStatus, progress;
	public ProgressBar proBar;
	public ObservableList<String> data;
	public ListView<String> Folders = new ListView<String>();
	public ListView<String> Files = new ListView<String>();
    public List<String> FoldersList, FilesList;
    public Database db;
    public boolean runOnce = false;
    private Thread thread; 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db = new Database();
		try {
			db.startDatabase();
			setFolderView();
			updateProgress();
		    Folders.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent event) {
		            try {
						setFileView(Folders.getSelectionModel().getSelectedItem());
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }
		    });
		} catch (SQLException | IOException | BackingStoreException | InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
	}
	
	public void sceneChange(ActionEvent event) throws IOException, SQLException{
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
	public boolean SetKey() throws SQLException{
		if (Key.getText().length() == 32){
			db.updateUserTable(Key.getText());
			return true;
		}else{
			return false;
		}
	}
	
	public void selectFolder(ActionEvent event) throws SQLException{
		Stage stage = (Stage) AddFolder.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		File defaultDirectory = new File(IO.getUserDataDirectory());
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		if (selectedDirectory != null){
			String folderName = selectedDirectory.getName() + "Encrypted";
			String folderPath = selectedDirectory.getAbsolutePath() + "/" + selectedDirectory.getName() +"Encrypted";
			String lockupName = selectedDirectory.getName();
			String lockupPath = IO.getLockUpDirectory() + selectedDirectory.getName();
			boolean success = db.addFolderToTable(folderName, folderPath, lockupName, lockupPath);
			if (success == true){
				setFolderView();
				IO.newFolder(selectedDirectory.getName(), null);
				IO.newFolder(selectedDirectory.getName() + "Encrypted", selectedDirectory.getAbsolutePath());
				new folderWatch(folderName, folderPath, lockupName);
				new folderWatch(lockupName, lockupPath, lockupName);
			}
		}
	}
	
	public void removeFolder() throws SQLException{
		String folder = Folders.getSelectionModel().getSelectedItem();
		if (folder != null){
			db.removeFolderFromTable(folder);
			setFolderView();
			setFileView(null);
		}
	}
	
	public void setFolderView() throws SQLException{
		FoldersList = db.selectAllFolders();
		data = FXCollections.observableArrayList();
		data.addAll(FoldersList);
		Folders.getItems().clear();
		Folders.setItems(data);
	}
	
	public void setFileView(String folder) throws SQLException{
		if (folder != null){
			FilesList = db.selectAllFiles(folder.replace(" ", "_"));
			FilesList.removeAll(Arrays.asList(""));
			data = FXCollections.observableArrayList();
			if (FilesList.size() != 0){
				data.addAll(FilesList);
			}else{
				data.add("No Files in folder");
			}
			Files.getItems().clear();
			Files.setItems(data);
		}else{
			Files.getItems().clear();
		}
	}
	
	public void updateProgress(){
	    Task<Void> task = new Task<Void>() {
	        @Override public Void call() {
	          while (true) {
	            try {
	              Thread.sleep(3000);
	            } catch (InterruptedException e) {
	              e.printStackTrace();
	            }
	    		int file = LockUpEngine.getFile();
	    		int total = LockUpEngine.getTotal();
	    		if (file != 0 && total != 0){
	    			updateMessage("Processing File "+file+" of "+total+" . . .");
		            updateProgress(file, total);
	    		}else{
	    			updateMessage("LockUp is up to date");
		            updateProgress(1, 1);
	    		}
	          }
	        }
	      };
	      proBar.progressProperty().bind(task.progressProperty());
	      progress.textProperty().bind(task.messageProperty());
	      
	      if (thread == null){
	    	thread = new Thread(task);
	      	thread.setDaemon(true);
	      	thread.start();
	      }
	}
}
