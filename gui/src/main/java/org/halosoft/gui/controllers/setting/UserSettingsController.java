/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.gui.controllers.setting;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.halosoft.gui.App;
import org.halosoft.gui.adapters.SettingsPaneAdapter;
import org.halosoft.gui.controllers.HostSelectorController;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserSettingsController extends SettingsPaneAdapter{
    
    private HostSelectorController parentController;
    
    @FXML
    protected StackPane rootPane;
    @FXML
    private VBox settingsBox;
    @FXML
    private Region undoButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Node account=this.addSetting("Account", "User data, preferences",
               "manageAccountImage");
        
        account.setOnMouseClicked((eh)->{
            try {
                Parent view=App.loadFXML("view/setting/account");
                AccountController ctrlr=(AccountController) view.getUserData();
                
                ctrlr.setParentController(this.parentController);
                
                this.rootPane.getChildren().add(view);
            
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, "Error while loading View: account"
                        , ex);
            }
        });
        
        Node prefs=this.addSetting("Preferences", "chat preferences, themes",
               "forumImage");
            
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
