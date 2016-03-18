package gui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.Database;
import io.IO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import preferences.LockUpPreferences;
import sync.LockUpEngine;
import sync.folderWatch;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(Main.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
        	Pane page;
    		IO io = new IO();
    		Database db = new Database();
    		io.initialSetup();
        	LockUpPreferences pref = new LockUpPreferences();
        	Boolean firstLaunch = pref.getFirstLaunch();
        	if (firstLaunch == true){
        		page = FXMLLoader.load(Main.class.getResource("welcome.fxml"));
        	}else{
        		page = FXMLLoader.load(Main.class.getResource("main.fxml"));
        	}
            Scene scene = new Scene(page);
            scene.getStylesheets().add("/gui/style.css");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle("LockUp");
            db.startDatabase();
            db.createUserTable();
            db.createFolderTable();
            List<String> resultSet = db.selectFoldersToWatch();
            primaryStage.show();
            LockUpEngine LUE = new LockUpEngine();
            LUE.start();
            for(int i = 0; i < resultSet.size(); i++){
            	String[] parts = resultSet.get(i).split(",");
            	@SuppressWarnings("unused")
				folderWatch fw1 = new folderWatch(parts[0], parts[1], parts[2].replace(" ", "_"));
            	@SuppressWarnings("unused")
				folderWatch fw2 = new folderWatch(parts[2], parts[3], parts[2].replace(" ", "_"));
            }
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                try {
					db.closeDatabase();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                   Platform.exit();
                   System.exit(0);
                }
             });
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}