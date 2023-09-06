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
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.halosoft.talk.objects.userObject;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserContactController extends userObject implements Initializable {

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
    @FXML
    private Label userHostName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @Override
    public void setStatus(int status){
        super.setStatus(status);
        
        String statusStyle="-fx-background-color: %s; -fx-background-radius: 30% 30%;";
        
        Tooltip ttip=new Tooltip();
        
        switch(status){
            case 0:
                this.userImageBox.setStyle(statusStyle.replace("%s","gray"));
                ttip.setText("Offline");
                break;
                
            case 1:
                this.userImageBox.setStyle(statusStyle.replace("%s","red"));
                ttip.setText("Busy");
                break;
                
            case 2:
                this.userImageBox.setStyle(statusStyle.replace("%s","green"));
                ttip.setText("Online");
                break;
        }
        Tooltip.install(this.userImageBox, ttip);
   
    }
    @Override
    public void setContents(userObject userData){
        super.setContents(userData);

        this.userID.setText(this.getName());
        
        if ( !this.getSurName().equals("*") ) {
            
            this.userID.setText(this.userID.getText()+" "+this.getSurName());
        }
        
        this.userHostName.setText(this.getHostName());
        this.userImage.setImage(this.getImage());
        this.userStatusText.setText(this.getStatusMessage());
        this.setStatus(this.getStatus());
    }
  
    @Override
    public void setImage(Image img){
        super.setImage(img);
        this.userImage.setImage(img);
    }
    @Override
    public void setStatusMessage(String status){
        super.setStatusMessage(status);
        this.userStatusText.setText(status);
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
        st.setOnFinished((ActionEvent t) -> {
            hstCtrlr.getLeftStackPane().getChildren().remove(rootPane);
        });
        
        st.play();
    }
    
}
