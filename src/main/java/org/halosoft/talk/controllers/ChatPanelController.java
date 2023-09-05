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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.halosoft.talk.App;
import org.halosoft.talk.objects.Client;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ChatPanelController implements Initializable {
    private Client remoteClient;
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
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendButton;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO 
    }
    public void setClient(Client client){
        this.remoteClient=client;
        this.listenMessage();//listens the socketInputStream of remote end
    }
    public void closeClient(){
        this.remoteClient.stop();
    }
    public void setContents(String userName, Image image){
        this.userNameLabel.setText(userName);
        this.userImageView.setImage(image);
    }
    
    public void addMessage(String message, Pos pos){
        
        try {
            Parent msgBox= App.loadFXML( "messageBoxPanel" );
            MessageBoxPanelController msgController= (MessageBoxPanelController) msgBox.getUserData();
            
            msgController.setmsgBoxLayoutAlignment(pos);
            msgController.setMessage(message, pos);
            
            this.messageBoxLayout.getChildren().add(msgBox);
            
        } catch (IOException ex) {
            System.out.println("addMessage:"+ex.getMessage());
        }   
    }
    
    private void listenMessage(){
        /*listens for messages that comes from remote end*/
        Thread msgListener=new Thread( () -> {

            while ( !Thread.currentThread().isInterrupted() ){
                
                try {
                    String message=remoteClient.getSocketInputStream().readUTF();

                    addMessage(message, Pos.TOP_LEFT);
                    
                } catch (IOException ex) {
                    System.out.println("chatPanel client listenMessage:"+ex.getMessage());
                }
            }
        });
        msgListener.setDaemon(true);
        msgListener.start();
    }

    @FXML
    private void sendButtonMouseClicked(MouseEvent event) {
        
        String message=this.messageTextField.getText();
        
        if ( !message.equals(new String("") )) {
            
            this.addMessage(message, Pos.TOP_RIGHT);
            
            try {
                //send message to remote end
                this.remoteClient.getSocketOutputStream().writeUTF(message);
            } catch(NullPointerException ex){
                
                System.err.println("chatPanelcontroller:"
                        + "messsage could not send through client object:"
                        +ex.getMessage());
                
            }catch (IOException ex) {
                System.out.println("chatPanelcontroller sendButton:"+ex.getMessage());
            }
            
            
            this.messageTextField.setText("");
        }
    }
    
}
