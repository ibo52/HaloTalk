/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.halosoft.talk.App;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserSettingsController implements Initializable {


    @FXML
    private StackPane rootPane;
    @FXML
    private VBox settingsBox;
    @FXML
    private ImageView undoButton;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.addSetting("Account", "User data, preferences",
                new Image(App.class.getResource
        ("/images/icons/manage_accounts.png").toString()));
        
        this.addSetting("Preferences", "chat preferences, themes",
                new Image(App.class.getResource
        ("/images/icons/chat.png").toString()));
        
        this.addSetting("Notifications", "message, call sounds",
                new Image(App.class.getResource
        ("/images/icons/notification.png").toString()));
    }   
    
    private void addSetting(String name, String contents, Image img){
        
            try {
                // TODO
                Parent p=App.loadFXML("view/generalSetting");
                GeneralSettingController ctrlr=(GeneralSettingController) p.getUserData();

                ctrlr.setSettingName(name);
                ctrlr.setSettingContents(contents);
                ctrlr.setSettingImage(img);
                
                this.settingsBox.getChildren().add(p);

            } catch (IOException ex) {
                System.err.println("Error while initializing settings:"+ex.getMessage());
            }
    }
    
    private void remove(){
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }

    @FXML
    private void undoButtonMouseClicked(MouseEvent event) {
        this.undoButton.setDisable(true);
        this.remove();
    }
    

}
