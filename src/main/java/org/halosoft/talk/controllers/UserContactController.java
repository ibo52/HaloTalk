/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserContactController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox userImageBox;
    @FXML
    private ImageView userImage;
    @FXML
    private Label userID;
    @FXML
    private Label userStatusText;
    @FXML
    private Button sendMessageButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public String getUserID(){
        return this.userID.getText();
    }
    public void setUserID(String id){
        this.userID.setText(id);
    }
    public Image getImage(){
        return this.userImage.getImage();
    }
    public void setImage(Image img){
        this.userImage.setImage(img);
    }
    
    @FXML
    private void sendMessageButtonMouseClicked(MouseEvent event) {
        
        //get host selector rootpane and its controller
        Parent hostSelector=this.rootPane.getParent().getParent();
        HostSelectorController hstCtrlr=(HostSelectorController) hostSelector.getUserData();
        hstCtrlr.bringChatScreen(this);
        
        ScaleTransition st=new ScaleTransition();
        st.setDuration(Duration.millis(300));
        st.setNode(this.rootPane);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(0);
        st.setToY(0);
        st.setOnFinished(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                hstCtrlr.getLeftStackPane().getChildren().remove(rootPane);
            }
        });
        
        st.play();
    }
    
}
