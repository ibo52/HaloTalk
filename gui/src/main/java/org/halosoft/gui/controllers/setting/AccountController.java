/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.gui.controllers.setting;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.halosoft.gui.controllers.HostSelectorController;
import org.halosoft.gui.interfaces.Animateable;
import org.halosoft.gui.interfaces.Controllable;
import org.halosoft.gui.models.ObservableUser;
import org.halosoft.gui.models.PropertiesManager;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class AccountController implements Initializable, Controllable,
        Animateable{
    
    private final ObservableUser userData;
    private HostSelectorController parentController;
    
    @FXML
    private StackPane rootPane;
    @FXML
    private VBox userImageBox;
    @FXML
    private ImageView userImage;
    @FXML
    private Region userImageEditButton;
    @FXML
    private Region undoButton;
    @FXML
    private HBox nameEditBox;
    @FXML
    private Label userName;
    @FXML
    private HBox statusEditBox;
    @FXML
    private HBox aboutEditBox;
    @FXML
    private ComboBox<String> userStatus;
    @FXML
    private Label userAbout;
    @FXML
    private HBox editFieldBox;
    @FXML
    private TextField editField;
    @FXML
    private Button editButton;
    @FXML
    private ScrollPane accountSettingsPane;

    public AccountController() {
        this.userData =ObservableUser
            .readFromProperties("settings/broadcaster.properties");
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.editFieldBox.prefWidthProperty().bind((
                    this.rootPane.widthProperty()).subtract(40));
        this.editFieldBox.prefHeightProperty().bind(
                this.editField.heightProperty().add(10));
        
        this.userAbout.textProperty().bind(this.userData.getStatusMessageProperty());
        this.userName.textProperty().bind(this.userData.getNameProperty()
        .concat(" ").concat(this.userData.getSurNameProperty()));

        this.userStatus.setItems(observableArrayList("Seen as Offline"
                ,"Busy","Online"));
        
        this.userData.getStatusProperty().bind( this.userStatus
                        .getSelectionModel().selectedIndexProperty().asString());
        
        this.userStatus.getSelectionModel().selectedIndexProperty()
                .addListener((il)->{
                    
                    this.parentController.updateBroadcasterData(userData);
                    //save to file when status changed
                    PropertiesManager.saveToFile(
                        this.userData.getProperties(), 
                        "settings/broadcaster.properties");
                });
        
        //start animation when width>0
        this.rootPane.setTranslateX(Long.MAX_VALUE);//keep out of screen for start animation
        this.rootPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override  
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                AccountController.this.startAnimation();
                AccountController.this.rootPane.widthProperty()
                        .removeListener(this);
            }
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
    
    private void setEditableField(String prompt, 
            EventHandler<? super MouseEvent> buttonApplyEvent){
        this.editField.setPromptText(prompt);
        this.editButton.setOnMouseClicked(buttonApplyEvent);
        
        this.bringNodeToFrontOfStack(this.editFieldBox);
    }
    
    @FXML
    private void undoButtonMouseClicked(MouseEvent event) {
        this.undoButton.setDisable(true);
        this.stopAnimation();
    }

    @FXML
    private void nameEditBoxMouseClicked(MouseEvent event) {
        this.editField.setText("");
        this.setEditableField("Type your name", (eh)->{
            
            String text=this.editField.getText().trim();
            if ( !text.isBlank() ) {
                
                int surnameIdx=text.lastIndexOf(" ");
                if (surnameIdx>0) {
                    this.userData.setName(
                      text.substring(0,surnameIdx));
                    this.userData.setSurname(
                    text.substring(surnameIdx,text.length()));
                }else{
                    this.userData.setName(text);
                    this.userData.setSurname("*");
                }
                
                this.parentController.updateBroadcasterData(userData);
                
                PropertiesManager.saveToFile(
                        this.userData.getProperties(), 
                        "settings/broadcaster.properties");
            }
            
        });
    }

    @FXML
    private void aboutEditBoxMouseClicked(MouseEvent event) {
        this.editField.setText("");
        this.setEditableField("Tell about yourself", (eh)->{
            
            String text=this.editField.getText().trim();
            if ( !text.isBlank() ) {
                
                this.userData.setStatusMessage(text);
                
                this.parentController.updateBroadcasterData(userData);
                
                PropertiesManager.saveToFile(
                        this.userData.getProperties(), 
                        "settings/broadcaster.properties");
            }
            
        });
    }
    
    public void setUserData(ObservableUser data){
        this.userData.setContents(data);
    }

    @Override
    public void setParentController(Object controller) {
        this.parentController=(HostSelectorController)controller;
    }

    @Override
    public Object getParentController() {
        return this.parentController;
    }

    @Override
    public void remove() {
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
    
    @Override
    public void startAnimation() {
        
        final Duration duration=Duration.millis(300);
        //---Translate effect
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(duration);

        double width=this.rootPane.getWidth();
        
        tt.setFromX(width);
        tt.setToX(0);
        //---Fade effect
        FadeTransition ft=new FadeTransition();
        ft.setDuration(duration);
        ft.setFromValue(0);
        ft.setToValue(1);

        ParallelTransition pt=new ParallelTransition(this.rootPane,
                tt,ft);
        pt.play();
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
