/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class MessageBoxPanelController implements Initializable {


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
        
        //this.msgTextArea.prefWidthProperty().bind( this.msgBoxLayout.widthProperty().multiply(0.8) );
    }
    
    public void setMessage(String message, Pos pos){
        this.msgTextArea.setText(message);
        this.msgTextArea.setAlignment(pos);
        
    }
    public void setmsgBoxLayoutAlignment(Pos pos){
        this.msgBoxLayout.setAlignment(pos);
    }
    
}
