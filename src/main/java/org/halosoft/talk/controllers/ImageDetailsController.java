/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ImageDetailsController implements Initializable {


    @FXML
    private ImageView userImage;
    @FXML
    private Label userID;
    @FXML
    private BorderPane rootPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private ScrollPane userImagePane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        this.userImage.fitWidthProperty().bind(this.userImagePane.widthProperty());
        this.userImage.fitHeightProperty().bind(this.userImagePane .heightProperty());
    }  
    
    public void setUserImage(Image img){
        this.userImage.setImage(img);
    }

    public void setUserID(String userID) {
        this.userID.setText( userID );
    }
    
}
