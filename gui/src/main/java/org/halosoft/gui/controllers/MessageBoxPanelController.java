/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.halosoft.gui.interfaces.Animateable;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.util.Duration;
/**
 * FXML Controller class
 * Message boxes to use on chat panel
 * @author ibrahim
 */
public class MessageBoxPanelController implements Initializable, Animateable{


    @FXML
    private HBox msgBoxLayout;
    @FXML
    private Label msgTextArea;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.msgTextArea.maxWidthProperty().bind( this.msgBoxLayout.widthProperty().multiply(0.9) );
        
        this.msgBoxLayout.setTranslateX(Long.MAX_VALUE);
        this.msgBoxLayout.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                MessageBoxPanelController.this.startAnimation();
                MessageBoxPanelController.this.msgBoxLayout.widthProperty()
                        .removeListener(this);
            }
        });
    }
    
    public void setMessage(String message, Pos pos){
        this.msgTextArea.setText(message);
        this.msgTextArea.setAlignment(pos);
        
    }
    public void setmsgBoxLayoutAlignment(Pos pos){
        this.msgBoxLayout.setAlignment(pos);
    }

    @Override
    public void startAnimation() {
            
            TranslateTransition tt=new TranslateTransition();
            tt.setDuration(Duration.millis(300));
            tt.setNode(this.msgBoxLayout);

            tt.setFromX(this.msgBoxLayout.getWidth());
            tt.setToX(0);

            tt.play();
    }

    @Override
    public void stopAnimation() {
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(Duration.millis(300));
        tt.setNode(this.msgBoxLayout);

        tt.setFromX(0);
        tt.setToX(this.msgBoxLayout.getHeight());

        tt.play();
    }
    
}
