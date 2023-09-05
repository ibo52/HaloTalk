/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.halosoft.talk.App;
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
    @FXML
    private Button sendMessageButton;
    @FXML
    private Button showDetailsButton;
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
    public Image getImage(){
        return this.userImage.getImage();
    }

    public void setUserID(String userID) {
        this.userID.setText( userID );
    }
    public String getUserID(){
        return this.userID.getText();
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

    @FXML
    private void showDetailsButtonMouseClicked(MouseEvent event) {
        
        try {
            //load contact pane fxml
            Parent uContact=App.loadFXML("userContact");
            UserContactController ctrlr=(UserContactController) uContact.getUserData();
            
            //set contents of contact
            ctrlr.setImage(this.getImage());
            ctrlr.setUserID(this.getUserID());
            ctrlr.setCustomStatusText("heyyo! i am using haloTalk");
            
            //get host selector rootpane and its controller
            Parent hostSelector=this.rootPane.getParent().getParent();
            HostSelectorController hstCtrlr=(HostSelectorController) hostSelector.getUserData();
            
            hstCtrlr.getLeftStackPane().getChildren().remove(this.rootPane);
            
            hstCtrlr.getLeftStackPane().getChildren().add(uContact);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
