package org.halosoft.talk.controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */


import org.halosoft.talk.controllers.MessageBoxPanelController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.halosoft.talk.App;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ChatPanelController implements Initializable {


    @FXML
    private BorderPane rootPane;
    @FXML
    private GridPane headerBarLayout;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private VBox messageBoxLayout;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        /*
        addMessage("deneme kontrol sereseracvasdmaskf"
                + "2. satır"
                + "ve 3. astır"
                + "sdfks",Pos.TOP_LEFT);
        
        addMessage("asdfskiaerjgpğsdşsdıofdjvfoperufsdılfjsdılfg"
                + "sdoşfjasıdfjğawefjaıofjweıofjısdoğfjsdofjadsı",Pos.TOP_RIGHT);
        
        addMessage("esrar-eltaraf-elkalemmityye",Pos.TOP_RIGHT);
        
        addMessage("tamamt",Pos.TOP_LEFT);
        */

    }
    
    public void setContents(String userName, Image image){
        this.userNameLabel.setText(userName);
        this.userImageView.setImage(image);
    }
    
    public void addMessage(String message, Pos pos){
        
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("messageBoxPanel.fxml"));
        try {
            Parent msgBox= fxmlLoader.load();
            MessageBoxPanelController msgController=fxmlLoader.getController();
            
            msgController.setmsgBoxLayoutAlignment(pos);
            msgController.setMessage(message, pos);
            
            this.messageBoxLayout.getChildren().add(msgBox);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
        
    }
    
}
