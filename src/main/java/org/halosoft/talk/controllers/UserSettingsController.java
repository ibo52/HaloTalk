/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.halosoft.talk.App;
import org.halosoft.talk.adapters.SettingsPaneAdapter;
import org.halosoft.talk.interfaces.Controllable;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserSettingsController extends SettingsPaneAdapter
        implements Initializable, Controllable{
    
    private HostSelectorController parentController;
    
    @FXML
    protected StackPane rootPane;
    @FXML
    private VBox settingsBox;
    @FXML
    private ImageView undoButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Node account=this.addSetting("Account", "User data, preferences",
                new Image(App.class.getResource
        ("/images/icons/manage_accounts.png").toString()));
        
        account.setOnMouseClicked((eh)->{
            try {
                Parent view=App.loadFXML("view/setting/account");

                this.rootPane.getChildren().add(view);
            
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, "Error while loading View: account"
                        , ex);
            }
        });
        
            Node prefs=this.addSetting("Preferences", "chat preferences, themes",
                new Image(App.class.getResource
        ("/images/icons/chat.png").toString()));
            
            prefs.setOnMouseClicked((eh)->{
            try {
                Parent view=App.loadFXML("view/setting/preferences");

                this.rootPane.getChildren().add(view);
            
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, "Error while loading View: preferences"
                        , ex);
            }
        });
        /*
        this.addSetting("Notifications", "message, call sounds",
                new Image(App.class.getResource
        ("/images/icons/notification.png").toString()));
        */
    }
    
    /**
     * Add setting to View of controllers, which is a container
     * there have to happen a View with same name of this setting
     * @param name  name of the setting, as well as the name of the View to load
     * @param contents  explanation of context of setting
     * @param img general image that defines this setting
     */
    
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
    
    public void remove(){
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
    

}
