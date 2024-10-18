/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.gui.controllers.setting;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableHashMap;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.halosoft.gui.App;
import org.halosoft.gui.adapters.SettingsPaneAdapter;
import org.halosoft.gui.controllers.HostSelectorController;
import org.halosoft.gui.interfaces.Animateable;
import org.halosoft.gui.models.PropertiesManager;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class PreferencesController extends SettingsPaneAdapter
        implements Animateable {

    private ObservableMap<String,String> themeMap;
    
    private Properties styleProperties;
    
    @FXML
    private StackPane rootPane;
    @FXML
    private VBox settingsBox;
    @FXML
    private Region undoButton;
    private Object parentController;
    @FXML
    private HBox themeEditBox;
    @FXML
    private ComboBox<String> themeEditComboBox;
    @FXML
    private Button themeEditApplyButton;
    @FXML
    private ScrollPane settingsBoxRootPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        styleProperties=new Properties();
        
        Node theme=this.addSetting("Theme", "Default style for interface."
                + " White, Dark mode", "manageAccountImage");
        
        themeMap=observableHashMap();
        
        themeMap.put("Default", "stylesheet/default-style.css");
        themeMap.put("White", "stylesheet/default-style.css");
        themeMap.put("Dark mode", "stylesheet/dark-mode.css");
        
        this.themeEditComboBox.setItems(observableArrayList(themeMap.keySet()));
        
        this.themeEditApplyButton.setOnMouseClicked((eh)->{
            String e=this.themeEditComboBox.getSelectionModel().getSelectedItem();
            
            String style=App.class.getResource( this.themeMap.get(e) ).toString();

            this.rootPane.getScene().getRoot().getStylesheets().clear();
            this.rootPane.getScene().getRoot().getStylesheets().add(style);
            styleProperties.setProperty("DEFAULT_STYLESHEET", 
                this.themeMap.get(e));
            
            PropertiesManager.saveToFile(styleProperties
                    , "settings/application");
            
            this.bringNodeToFrontOfStack(this.settingsBox);
        });
        
        theme.setOnMouseClicked((eh)->{
            this.bringNodeToFrontOfStack(this.themeEditBox);
            
        });
    }
    
    private void bringNodeToFrontOfStack(Node n){
        
        Node beSwapped;
        if( (beSwapped=this.rootPane.getChildren().remove(0)).equals(n)){
            
            this.rootPane.getChildren().add(n);
        }
        else{
            this.rootPane.getChildren().add(0, beSwapped);
        }
    }
    
    @FXML
    private void undoButtonMouseClicked(MouseEvent event) {
        this.undoButton.setDisable(true);
        this.stopAnimation();
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
    public void remove(){
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }

    @Override
    public void startAnimation() {
    }

    @Override
    public void stopAnimation() {
        
        final Duration duration=Duration.millis(300);
        //---Translate effect
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(duration);

        double width=this.rootPane.getWidth();
        
        tt.setFromX(0);
        tt.setToX(width);
        //---Fade effect
        FadeTransition ft=new FadeTransition();
        ft.setDuration(duration);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt=new ParallelTransition(this.rootPane,
                tt,ft);

        pt.setOnFinished((ActionEvent t) -> {
        remove();
        });
        pt.play();
    }

}
