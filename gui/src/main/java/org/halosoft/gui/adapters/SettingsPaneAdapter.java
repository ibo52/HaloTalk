/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.adapters;

import java.io.IOException;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.halosoft.gui.App;
import org.halosoft.gui.controllers.setting.GeneralSettingController;
import org.halosoft.gui.interfaces.Controllable;

/**
 *
 * @author ibrahim
 */
public abstract class SettingsPaneAdapter implements Initializable, Controllable {
    
    @FXML
    protected StackPane rootPane;
    @FXML
    protected VBox settingsBox;
    @FXML
    protected Region undoButton;
    
    /**
     * Add setting to View of controllers, which is a container
     * there have to happen a View with same name of this setting
     * @param name  name of the setting, as well as the name of the View to load
     * @param contents  explanation of context of setting
     * @param img general image that defines this setting
     */
    protected final Node addSetting(String name, String contents, String CssClassOfSVG){

            try {
                // TODO
                Parent p=App.loadFXML("view/setting/generalSetting");
                GeneralSettingController ctrlr=(GeneralSettingController) p.getUserData();

                ctrlr.setSettingName(name);
                ctrlr.setSettingContents(contents);
                ctrlr.setSettingImage(CssClassOfSVG);
                
                this.settingsBox.getChildren().add(p);

                return p;
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, 
                        ex.getMessage(),ex);
                return null;
            }
    }
    public final StackPane getContainer(){
        return this.rootPane;
    }

    @Override
    public abstract void setParentController(Object controller);

    @Override
    public abstract Object getParentController();
    
    @Override
    public abstract void remove();
}
