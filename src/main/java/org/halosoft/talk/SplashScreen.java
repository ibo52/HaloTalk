/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk;

import java.net.URISyntaxException;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author ibrahim
 */
public class SplashScreen {
    
    private FadeTransition splashTransition;
    private Scene scene;
    private Stage stage;
    
    private ProgressBar progressBar;
    final int SCENE_WIDTH=640;
    final int SCENE_HEIGHT=480;
    
    public SplashScreen(){
        try {
            Label greeter=new Label("HaloSoft Inc.    github.com/ibo52");
            greeter.setPadding(new Insets(10));
            
            ImageView img=new ImageView(App.class.
                    getResource("/images/logo-circle-512x512.png").toURI().toString());
            
            
            HBox imgBox=new HBox(img);
            imgBox.setAlignment(Pos.CENTER);
            imgBox.setFillHeight(true);
            
            ScrollPane scroll=new ScrollPane(imgBox);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);
            
            img.setPreserveRatio(true);
            img.fitWidthProperty().bind(scroll.widthProperty().multiply(.6));
            img.fitHeightProperty().bind(scroll.heightProperty().subtract(.6));
            
            progressBar=new ProgressBar();
            progressBar.setPrefSize(Float.MAX_VALUE, 24);
            
            BorderPane rootPane=new BorderPane();
            rootPane.setTop(greeter);
            rootPane.setCenter(scroll);
            rootPane.setBottom(progressBar);
            
            this.scene=new Scene(rootPane,SCENE_WIDTH,SCENE_HEIGHT);
            scene.getStylesheets().add(App.class.
                        getResource("stylesheet/dark-mode.css").toExternalForm());
            
            stage=new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
            
            //Load splash screen fade effect
            splashTransition = new FadeTransition(Duration.seconds(1), imgBox);
            splashTransition.setAutoReverse(true);
            splashTransition.setFromValue(0);
            splashTransition.setToValue(1);
            splashTransition.setCycleCount(Integer.MAX_VALUE);
            splashTransition.play();
            
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
    
    public Scene getScene(){
        return this.scene;
    }
    public Stage getStage(){
        return this.stage;
    }
    public ProgressBar getProgressBar(){
        return this.progressBar;
    }
    
    public Transition getTransition(){
        return this.splashTransition;
    }
}
