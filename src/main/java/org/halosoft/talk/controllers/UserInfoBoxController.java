/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserInfoBoxController implements Initializable {


    @FXML
    private ImageView userImage;
    @FXML
    private Label userID;
    @FXML
    private Label lastSeenLabel;
    @FXML
    private Label unreadMessageCountLabel;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    public void setUserName(String name){
        this.userID.setText(name);
    }
    
}
