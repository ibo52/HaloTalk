/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.halosoft.talk.interfaces.Animateable;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class GeneralSettingController implements Initializable, Animateable {


    @FXML
    private Label settingContents;
    @FXML
    private ImageView settingImage;
    @FXML
    private Label settingName;
    @FXML
    private HBox rootPane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //start animation when width>0
        this.rootPane.setTranslateX(Long.MAX_VALUE);//keep out of screen for start animation
        this.rootPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                GeneralSettingController.this.startAnimation();
                GeneralSettingController.this.rootPane.heightProperty()
                        .removeListener(this);
            }
        });
    }    
    
    @FXML
    private void settingPaneMouseClicked(MouseEvent event) {
    }
    
    public void setSettingName(String setttingName){
        this.settingName.setText(setttingName);
    }
    
    public void setSettingContents(String setttingContents){
        this.settingContents.setText(setttingContents);
    }
    
    public void setSettingImage(Image settingImage){
        this.settingImage.setImage(settingImage);
    }

    @Override
    public void startAnimation() {
            
            TranslateTransition tt=new TranslateTransition();
            tt.setDuration(Duration.millis(300));
            tt.setNode(this.rootPane);
            
            tt.setFromX(-this.rootPane.getWidth());
            tt.setToX(0);
            tt.play();
        
    }

    @Override
    public void stopAnimation() {
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(Duration.millis(300));
        tt.setNode(this.rootPane);

        tt.setFromX(0);
        tt.setToX(this.rootPane.getWidth());
        
        tt.setOnFinished((ActionEvent t) -> {
            ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
        });

        tt.play();
    }

}
