package gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(Main.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
        	Pane page;
        	Boolean firstLaunch = true;
        	if (firstLaunch == true){
        		page = FXMLLoader.load(Main.class.getResource("welcome.fxml"));
        	}else{
        		page = FXMLLoader.load(Main.class.getResource("main1.fxml"));
        	}
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle("LockUp");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}