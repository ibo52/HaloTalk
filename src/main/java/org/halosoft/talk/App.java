package org.halosoft.talk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        
        SplashScreen splashScreen=new SplashScreen();
        
        Task loadTask=new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                try {
                    this.updateProgress(20, 100);
                    stage.getIcons().add(new Image(App.class.getResource(
                            "/images/app-logo.png").toURI().toString()));

                    this.updateProgress(35, 100);
                    stage.setTitle("HaloTalk: simple LAN messenger");

                    scene = new Scene(loadFXML("view/hostSelector"), 840, 480);
                    this.updateProgress(85, 100);

                    scene.getStylesheets().add(App.class.
                            getResource("stylesheet/default-style.css").toExternalForm());

                    Platform.runLater(()->{
                        stage.setScene(scene);
                    });
                    this.updateProgress(100, 100);

                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded(); 

                splashScreen.getTransition().stop();
                splashScreen.getStage().close();

                stage.show();
            }
        };
        
        splashScreen.getProgressBar().progressProperty()
                .bind(loadTask.progressProperty());
        
        Thread MainWindowLoadService=new Thread(loadTask);
        MainWindowLoadService.setDaemon(true);
        MainWindowLoadService.start();
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