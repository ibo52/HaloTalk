/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.halosoft.talk.App;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserInfoBoxController implements Initializable {
    
    private String id;

    @FXML
    private ImageView userImage;
    @FXML
    private Label userID;
    @FXML
    private Label lastSeenLabel;
    @FXML
    private Label unreadMessageCountLabel;
    @FXML
    private VBox userImageBox;
    @FXML
    private Label customStatusLabel;
    @FXML
    private BorderPane rootPane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setStatus(0);
        setID("user");
        // TODO
    }   
    
    public void setUserName(String name){
        this.userID.setText(name);
    }
    
    public void setStatus(int status){
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
        
        Tooltip.install(this.rootPane, ttip);
        //Tooltip.install(this.userImageBox, ttip);
        
        
    }
    
    public void setCustomStatus(String status){
        customStatusLabel.setText(status);
    }
    
    public String getID(){
        return this.id;
    }
    public void setID(String id){
        this.id=id;
    }
    public String getUserID(){
        return this.userID.getText();
    }
    public Image getImage(){
        return this.userImage.getImage();
    }
    
    private void showUserInfoDetails(){
        
        try {
            Parent imageDetails=App.loadFXML("imageDetails");
            ImageDetailsController imageDetailsController=(ImageDetailsController) imageDetails.getUserData();
            
            //get hostSelector rootPane and its controls
            Parent p=this.rootPane.getParent().getParent().getParent().getParent().getParent().getParent();
            HostSelectorController ctrlr=(HostSelectorController) p.getUserData();
            
            //set imageView and add to left stackpane
            StackPane leftStackPane=ctrlr.getLeftStackPane();
            
            //set max size of child at parent
            imageDetailsController.initRootProperty(leftStackPane.widthProperty().multiply(0.8));
            
            imageDetailsController.setUserID(this.getUserID());
            //imageDetailsController.setUserImage(this.getImage());
            leftStackPane.getChildren().add(imageDetails);
            
            //bind width and height to parent
            
            //scale transition for fancy effect
            ScaleTransition st=new ScaleTransition();
            st.setNode(imageDetails);
            st.setDuration(Duration.millis(900));
            
            st.setFromX(0);
            st.setFromY(0);
            st.setToX(1);
            st.setToY(1);
            st.play();
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void rootPaneMouseClicked(MouseEvent event) {
        
        //get hostselector rootpane and its controller
        Parent p=this.rootPane.getParent().getParent().getParent().getParent().getParent().getParent();
        
        HostSelectorController ctrlr=(HostSelectorController) p.getUserData();

        ctrlr.bringChatScreen((UserInfoBoxController) this.rootPane.getUserData());
    }

    @FXML
    private void userImageBoxMouseClicked(MouseEvent event) {
        this.showUserInfoDetails();
    }
    
}
