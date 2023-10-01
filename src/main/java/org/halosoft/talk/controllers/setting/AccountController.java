/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers.setting;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.halosoft.talk.objects.ObservableUser;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class AccountController extends ObservableUser implements Initializable {
    
    @FXML
    private StackPane rootPane;
    @FXML
    private VBox userImageBox;
    @FXML
    private ImageView userImage;
    @FXML
    private ImageView userImageEditButton;
    @FXML
    private ImageView undoButton;
    @FXML
    private HBox nameEditBox;
    @FXML
    private ImageView settingImage;
    @FXML
    private Label userName;
    @FXML
    private HBox statusEditBox;
    @FXML
    private ImageView settingImage1;
    @FXML
    private HBox aboutEditBox;
    @FXML
    private ImageView settingImage11;
    @FXML
    private Label userStatus;
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
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        this.editFieldBox.prefWidthProperty().bind((
                    this.rootPane.widthProperty()).subtract(40));
        this.editFieldBox.prefHeightProperty().bind(
                this.editField.heightProperty().add(10));
        
        this.userAbout.textProperty().bind(statusMessageProperty);
        this.userName.textProperty().bind(nameProperty);
        this.userStatus.textProperty().bind(this.statusProperty);
        
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
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }

    @FXML
    private void nameEditBoxMouseClicked(MouseEvent event) {
        this.editField.setText("");
        this.setEditableField("Type your name", (eh)->{
            
            String text=this.editField.getText().trim();
            if ( !text.isBlank() ) {
                
                this.setName(text);
            }
            
        });
    }
    
    @FXML
    private void statusEditBoxMouseClicked(MouseEvent event) {
        this.editField.setText("");
        this.setEditableField("Select a status", (eh)->{
            
            String text=this.editField.getText().trim();
            if ( !text.isBlank() ) {

            }
            
        });
    }

    @FXML
    private void aboutEditBoxMouseClicked(MouseEvent event) {
        this.editField.setText("");
        this.setEditableField("Tell about yourself", (eh)->{
            
            String text=this.editField.getText().trim();
            if ( !text.isBlank() ) {
                
                this.setStatusMessage(text);
            }
            
        });
    }
    
}
