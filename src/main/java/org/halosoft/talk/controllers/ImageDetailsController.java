/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
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
    private ScrollPane userImageBox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //this.rootPane.maxWidthProperty().bind();
        this.userImage.fitWidthProperty().bind(this.userImageBox.widthProperty());
        this.userImage.fitHeightProperty().bind(this.userImageBox .heightProperty());
        this.userImageBox.prefWidthProperty().bind(this.rootPane.prefWidthProperty());
        this.userImageBox.prefHeightProperty().bind(this.rootPane.prefHeightProperty());

    }
    
    public void initRootProperty(DoubleProperty width){
        this.rootPane.prefWidthProperty().bind(width);
        this.rootPane.prefHeightProperty().bind(width);
    }
    public void initRootProperty(DoubleBinding width){
        this.rootPane.prefWidthProperty().bind(width);
        this.rootPane.prefHeightProperty().bind(width);
    }
    public void initRootProperty(ReadOnlyDoubleProperty width){
        this.rootPane.prefWidthProperty().bind(width);
        this.rootPane.prefHeightProperty().bind(width);
    }
    
    public void setUserImage(Image img){
        this.userImage.setImage(img);
    }

    public void setUserID(String userID) {
        this.userID.setText( userID );
    }
}
