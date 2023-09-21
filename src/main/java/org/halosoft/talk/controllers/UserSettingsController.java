/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import org.halosoft.talk.App;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserSettingsController implements Initializable {


    @FXML
    private ScrollPane rootPane;
    @FXML
    private VBox settingsBox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.addSetting("Account", "User data, preferences",
                new Image(App.class.getResource
        ("/images/settings/account.png").toString()));
        
        this.addSetting("Preferences", "chat preferences, themes",
                new Image(App.class.getResource
        ("/images/settings/messenger.png").toString()));
        
        this.addSetting("Notifications", "message, call sounds",
                new Image(App.class.getResource
        ("/images/settings/notification.png").toString()));
    }   
    
    private void addSetting(String name, String contents, Image img){
        
        Platform.runLater(()->{
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
        });
    }
    

}
