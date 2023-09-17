/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import org.halosoft.talk.interfaces.Animateable;
import org.halosoft.talk.objects.userObject;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserInfoBoxController extends userObject implements Initializable,
        Animateable{

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
        setStatus(this.getStatus());
        setID(this.getID());
        // TODO
    }   
    
    @Override
    public void setContents(userObject userData){
        super.setContents(userData);
        
        this.userID.setText(this.getHostName());
        this.userImage.setImage(this.getImage());
        this.customStatusLabel.setText(this.getStatusMessage());
        this.setStatus(this.getStatus());
        
    }
    @Override
    public void setName(String name){
        super.setName(name);
        this.userID.setText(name);
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
        ttip.setText(ttip.getText()+":"+this.getName()+" "+this.getSurName());
        Tooltip.install(this.rootPane, ttip);
        //Tooltip.install(this.userImageBox, ttip);
        
        
    }
    
    @Override
    public void setStatusMessage(String status){
        super.setStatusMessage(status);
        customStatusLabel.setText(status);
    }

    @Override
    public void setImage(Image img){
        super.setImage(img);
        this.userImage.setImage(img);
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
            
            imageDetailsController.setContents(this);
            
            //remove all components on stackpane except first
            while ( leftStackPane.getChildren().size()>1 ) {
                //node to be removed
                Node n=leftStackPane.getChildren().get(1);

                leftStackPane.getChildren().remove(n);
            }
            
            leftStackPane.getChildren().add(imageDetails);
            
            //scale transition for fancy effect
            imageDetailsController.startAnimation();
            
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
        event.consume();
    }  

    @Override
    public void startAnimation() {
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(Duration.millis(300));
        tt.setNode(this.rootPane);

        tt.setFromX(-100);
        tt.setToX(0);

        tt.play();
    }

    @Override
    public void stopAnimation() {
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(Duration.millis(300));
        tt.setNode(this.rootPane);

        tt.setFromX(0);
        tt.setToX(this.rootPane.getHeight());

        tt.play();
    }
}
