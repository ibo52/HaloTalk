/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.halosoft.talk.App;
import org.halosoft.talk.interfaces.Animateable;
import org.halosoft.talk.interfaces.Controllable;
import org.halosoft.talk.objects.ObservableUser;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ImageDetailsController implements Controllable,
        Initializable, Animateable{
    
    private HostSelectorController parentController;
    private ObservableUser userData;
    
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
    @FXML
    private ImageView undoButton;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //this.rootPane.maxWidthProperty().bind();
        
        this.userImage.fitWidthProperty().bind(this.userImageBox.widthProperty());
        this.userImage.fitHeightProperty().bind(this.userImageBox .heightProperty());
        this.userImageBox.prefWidthProperty().bind(this.rootPane.widthProperty());
        this.userImageBox.prefHeightProperty().bind(this.rootPane.heightProperty().multiply(0.92));
        
        this.rootPane.setVisible(false);
        this.startAnimation();
        
        userData=new ObservableUser();
        this.userID.textProperty().bind(this.userData.getIDProperty());
    }
    
    /**
     * defines maximum width of node. Used to call from Parent node
     * to set maximum size to its parents.
     * @param width 
     */
    public void initRootProperty(DoubleBinding width){
        this.rootPane.prefWidthProperty().bind(width);
        this.rootPane.prefHeightProperty().bind(width);
    }
    
    /**
     * defines maximum width of node. Used to call from Parent node
     * to set maximum size to its parents.
     * @param width 
     */
    public void initRootProperty(ReadOnlyDoubleProperty width){
        this.rootPane.prefWidthProperty().bind(width);
        this.rootPane.prefHeightProperty().bind(width);
    }

    @FXML
    private void sendMessageButtonMouseClicked(MouseEvent event) {
        
        //get host selector rootpane and its controller
        parentController.bringChatScreen(this.userData);
        
        this.stopAnimation();
    }

    @FXML
    private void showDetailsButtonMouseClicked(MouseEvent event) {
        
        try {
            //load contact pane fxml
            Pane uContact=(Pane) App.loadFXML("view/userContact");
            UserContactController ctrlr=(UserContactController) uContact.getUserData();
            
            uContact.setMaxSize(Long.MAX_VALUE, Long.MAX_VALUE);
            //set contents of contact
            ctrlr.setUserData(this.userData);
            ctrlr.setParentController(this.parentController);
            
            //get host selector rootpane and its controller
            parentController.getLeftStackPane().getChildren().remove(this.rootPane);
            
            parentController.getLeftStackPane().getChildren().add(uContact);
            
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        ex.getMessage(),ex);
        }
    }
    
    public void setUserData(ObservableUser userData){
        this.userData.setContents(userData);
    }
    
    public ObservableUser getUserData(){
        return this.userData;
    }

    @Override
    public void startAnimation() {
        Platform.runLater(()->{
            this.rootPane.setVisible(true);
            
            ScaleTransition st=new ScaleTransition();
            st.setNode(this.rootPane);
            st.setDuration(Duration.millis(300));
            
            st.setFromX(0);
            st.setFromY(0);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });   
    }

    @Override
    public void stopAnimation() {
        
        //get host selector rootpane and its controller

        ScaleTransition st=new ScaleTransition();
        st.setDuration(Duration.millis(300));
        st.setNode(this.rootPane);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(0);
        st.setToY(0);
        st.setOnFinished((ActionEvent t) -> {
            parentController.getLeftStackPane().getChildren().remove(rootPane);
        });
        
        st.play();
    }

    @FXML
    private void undoButtonMouseClicked(MouseEvent event) {
        this.undoButton.setDisable(true);
        this.remove();
    }
    
    @Override
    public void setParentController(Object controller) {
        this.parentController=(HostSelectorController) controller;
    }

    @Override
    public Object getParentController() {
        return this.parentController;
    }

    @Override
    public void remove() {
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
}
