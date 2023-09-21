package org.halosoft.talk.controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */


import org.halosoft.talk.controllers.MessageBoxPanelController;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.halosoft.talk.App;
import org.halosoft.talk.interfaces.Controllable;
import org.halosoft.talk.objects.Client;
import org.halosoft.talk.objects.userObject;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ChatPanelController extends userObject implements Initializable,
        Controllable{
    
    private HostSelectorController parentController;
    
    private Client remoteClient;
    @FXML
    private BorderPane rootPane;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private VBox messageBoxLayout;
    @FXML
    private TextArea messageTextField;
    @FXML
    private Button sendButton;
    @FXML
    private HBox statusBar;
    @FXML
    private StackPane stackPane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO 
        this.messageTextField.addEventHandler(KeyEvent.KEY_PRESSED,
                (KeyEvent t) -> {
            if (t.getCode().equals(KeyCode.ENTER)) {
                
                sendButton.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0,MouseButton.PRIMARY, 1, true, true, true, true,
                        true, true, true, true, true, true, null));
            }
        });
        
        this.statusBar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t)->{

            Pane uContact;
            while( this.stackPane.getChildren().size()>0){
                Node n=this.stackPane.getChildren().get(0);
                this.stackPane.getChildren().remove(n);
            }
            try {
                uContact = (Pane) App.loadFXML("view/userContact");
                UserContactController ctrlr=(UserContactController) uContact.getUserData();
                ctrlr.setContents(this);
                
                ctrlr.setParentController(this.parentController);
                uContact.setPrefWidth(240);
                
                this.stackPane.getChildren().add(uContact);
                this.stackPane.setAlignment(uContact, Pos.TOP_LEFT);
                
            } catch (IOException ex) {
                System.out.println("bind ucontact popup to cahtpanel statusbar:"
                        +ex.getMessage());
            }
            
        });
    }
    
    /**
     * change remote client object of chat panel
     * @param client remote end to communicate
     */
    public void setClient(Client client){
        this.remoteClient=client;
        this.listenMessage();//listens the socketInputStream of remote end
    }
    
    /**
     * close communication socket
     */
    public void closeClient(){
        this.remoteClient.stop();
    }
    @Override
    public void setContents(userObject userData){
        super.setContents(userData);
        
        this.userNameLabel.setText( this.getName()+" "+this.getSurName() );
        this.userImageView.setImage(this.getImage());
    }
    
    /**
     * Add incoming or outgoing messages to panel.
     * @param message message to placed
     * @param pos message position on screen. left or right
     */
    public void addMessage(String message, Pos pos){
        
        try {
            Parent msgBox= App.loadFXML( "view/messageBoxPanel" );
            MessageBoxPanelController msgController= (MessageBoxPanelController) msgBox.getUserData();
            
            msgController.setmsgBoxLayoutAlignment(pos);
            msgController.setMessage(message, pos);
            
            this.messageBoxLayout.getChildren().add(msgBox);
            
        } catch (IOException ex) {
            System.err.println("addMessage:"+ex.getMessage());
        }   
    }
    
    /**
     * listens for messages that comes from remote end
     */
    private void listenMessage(){
        
        Thread msgListener=new Thread( () -> {

            while ( !Thread.currentThread().isInterrupted() ){
                
                try {
                    String message=remoteClient.getSocketInputStream().readUTF();
                    
                    Platform.runLater( () -> {
                        addMessage(message, Pos.TOP_LEFT);
                    });
                    
                    
                } catch ( SocketException ex) {
                    System.out.println("chatPanel client Socket:"+ex.getMessage());
                    this.remoteClient=null;
                    break;
                }catch (IOException ex) {
                    System.out.println("chatPanel client Socket:"+ex.getMessage());
                    this.remoteClient=null;
                    break;
                }
            }
        });
        msgListener.setDaemon(true);
        msgListener.setName("chatPanelMsgListener");
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
    
    @Override
    public void setParentController(Object ctrlr){
        this.parentController=(HostSelectorController) ctrlr;
    }

    @Override
    public Object getParentController() {
        return this.parentController;
    }

    @Override
    public void remove() {
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
    
}
