package org.halosoft.gui.controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */


import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.halosoft.database.QueryResultSet;
import org.halosoft.database.TalkDBProperties;
import org.halosoft.gui.App;
import org.halosoft.gui.interfaces.Controllable;
import org.halosoft.gui.models.ObservableUser;
import org.halosoft.gui.models.net.ServerHandler;
import org.halosoft.gui.models.net.TCPSocket;
import org.halosoft.gui.utils.StageChanger;


/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ChatPanelController implements Controllable,
        Initializable{
    
    private ObservableUser userData;
    
    private HostSelectorController parentController;
        
    private final ExecutorService executorService=Executors.newCachedThreadPool();

    private ServerHandler remoteClientHandler;
    //private DataOutputStream Out;//instead of client, we use Server's  queue
    
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
    @FXML
    private HBox bottomMessageBorder;

    private Text MessageText; //to autosize messageTextfield

    @FXML
    private Button videoCallButton;
    //ref:https://stackoverflow.com/questions/18588765/how-can-i-make-a-textarea-stretch-to-fill-the-content-expanding-the-parent-in-t
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //bind send button event to enter key
        this.messageTextField.addEventHandler(KeyEvent.KEY_PRESSED,
                (KeyEvent t) -> {
            if (t.getCode().equals(KeyCode.ENTER)) {
                
                sendButton.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0,MouseButton.PRIMARY, 1, true, true, true, true,
                        true, true, true, true, true, true, null));
            }
        });
        //when user image clicked, open contact
        this.statusBar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t)->{

            Pane uContact;
            ////remove all components on stackpane except first
            this.stackPane.getChildren().remove(1, this.stackPane.getChildren().size());
            
            try {
                uContact = (Pane) App.loadFXML("view/userContact");
                UserContactController ctrlr=(UserContactController) uContact.getUserData();
                ctrlr.setUserData(this.userData);
                
                ctrlr.setParentController(this.parentController);
                uContact.setPrefWidth(240);
                
                this.stackPane.getChildren().add(uContact);
                StackPane.setAlignment(uContact, Pos.TOP_LEFT);
                
            } catch (IOException ex) {

                App.logger.log(Level.SEVERE, 
                        "Error while managing View creation of user contact",ex);
            }
        });
        
        //to autosize TextArea, we use Text object
        MessageText=new Text();
        MessageText.textProperty().bind(this.messageTextField.textProperty());
        MessageText.wrappingWidthProperty().bind(
                this.messageTextField.widthProperty().subtract(10));
        
        this.MessageText.textProperty().addListener((obs,o,n)->{
            
            StackPane p=new StackPane(MessageText);
            p.layout();
            
            this.messageTextField.setPrefHeight(MessageText.getLayoutBounds()
                    .getHeight()+10);
        });
        
        
        //let max message text area cover %33 of all chat screen
        this.bottomMessageBorder.maxHeightProperty()
                .bind(this.rootPane.heightProperty().divide(3));
        this.messageTextField.maxHeightProperty().bind(
                this.bottomMessageBorder.maxHeightProperty());

        //TODO: TEST change new stage to video chat screen and init call
        videoCallButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t)->{

            try {
                StageChanger.change(
                    (Stage)this.rootPane.getScene().getWindow(),
                    App.loadFXML("view/WifiCall"), 
                    200, 300);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * close communication socket
     */
    public void close(){
        
            this.executorService.shutdownNow();
            
            this.remoteClientHandler.stop();
              
    }
    
    public void setContents(ObservableUser userData){

            this.userData=userData;

            this.userNameLabel.textProperty().bind(this.userData.getNameProperty());
            
            this.remoteClientHandler=new ServerHandler(
                new TCPSocket( this.userData.getID()), false );
            //start receiver and sender threads
            this.executorService.execute(remoteClientHandler);

            //TODO: EN SON check connection drop(e.g remote server is down), then attempt to reconnect
            this.executorService.execute(()->{

                while ( !Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000);

                        if( !remoteClientHandler.isConnectionUp() ){
                            System.out.println("connection down retry ");

                            remoteClientHandler.reconnect();
                            remoteClientHandler.getConnectionDropped().set(false);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("checker done catch");
                    } catch ( IOException e) {
                        System.out.println("socket could not reconnect");
                    }
                }System.out.println("checker done while loop");
            });
            //TODO: attempt to resconnect socket if remote server shut down

            
            this.listenMessage();
            
            this.userImageView.setImage(this.userData.getImage());

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
            App.logger.log(Level.SEVERE, 
                        "Error while adding View of message box to chat panel",ex);
        }   
    }
    
    /**
     * listens for messages that comes from remote end
     */
    private void listenMessage(){

        //Check if any message db created by server handler
        executorService.execute(() -> {
            
            try {
                //open DB to listen incoming messages            
                var userDatabase=remoteClientHandler.getConnector();

                //init last 10 messages
                QueryResultSet initMessages=userDatabase.query(TalkDBProperties.getMessagesDesc(10, 0));
                for (int i = 0; i < initMessages.getRecordCount(); i++) {

                    LinkedList<String> record=initMessages.getLastRecord();

                    String message=record.get( initMessages.indexOf("message") );
                    int id=Integer.parseInt( record.get( initMessages.indexOf("id") ) );
                    int senderId=Integer.parseInt( record.get( initMessages.indexOf("senderId") ) );

                    Platform.runLater( ()->{
                        addMessage(message, senderId==1? Pos.TOP_LEFT:Pos.TOP_RIGHT);

                        userDatabase.query(TalkDBProperties.updateMessageQueue("completed=1", "id="+id ));
                    }); 
                }

                //listen DB perically for new messges
                while ( !Thread.currentThread().isInterrupted() ){

                    
                    QueryResultSet messageList=userDatabase.query(TalkDBProperties.getUnreadMessages());

                    for (int i = 0; i < messageList.getRecordCount(); i++) {

                        LinkedList<String> record=messageList.getNextRecord();

                        String message=record.get( messageList.indexOf("message") );
                        int id=Integer.parseInt( record.get( messageList.indexOf("id") ) );

                        Platform.runLater( ()->{
                            addMessage(message, Pos.TOP_LEFT);

                            userDatabase.query(TalkDBProperties.updateMessageQueue("completed=1", "id="+id ));
                        }); 
                    }
                    
                    Thread.sleep(300);
                }
            } catch (InterruptedException ex) {
                App.logger.log(Level.FINE, "chat panel messaging error",ex);
            }
        });
    
    }

    @FXML
    private void sendButtonMouseClicked(MouseEvent event) {
        
        String message=this.messageTextField.getText();
        message=message.trim();
        if ( !message.isBlank() ) {
            
            this.addMessage(message, Pos.TOP_RIGHT);
            //TODO: save message to database
            
            //send message to remote end
            
            //TODO: add message to db, then serverhandler auto sends it
            /*
            try {
                this.remoteClientHandler.writeUTF(message);

            }catch(IOException e){
                
            }
             */
            //message sending managed by Server, we just need to add message to databse
            remoteClientHandler.getConnector().query(
                TalkDBProperties.insertIntoMessageQueue(0, message, 0));
                
            this.messageTextField.setText("");
            
        }
    }
    
    public ObservableUser getUserData(){
        return this.userData;
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
    public void remove() {
        this.close();//close files and sockets
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
}
