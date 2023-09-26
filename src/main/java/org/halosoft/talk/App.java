package org.halosoft.talk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/hostSelector"), 840, 480);
        stage.getIcons().add(new Image(App.class.getResource(
                "/images/app-logo.png").toString()));
        stage.setTitle("HaloTalk: simple LAN messenger");
        scene.getStylesheets().add(App.class.
                getResource("stylesheet/default-style.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        
        Parent p= fxmlLoader.load();
        Object ctrlr=fxmlLoader.getController();
        
        p.setUserData(ctrlr);

        return p;
    }
    
    @Override
    public void stop(){
        
        Platform.exit();
        System.exit(0);
        
    }
    public static void main(String[] args) {
        launch();
    }

}