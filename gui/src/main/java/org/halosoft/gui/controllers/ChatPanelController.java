package org.halosoft.gui.controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */


import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.halosoft.database.QueryResultSet;
import org.halosoft.database.SQLiteConnector;
import org.halosoft.database.SQLiteDatabaseManager;
import org.halosoft.database.TalkDBProperties;
import org.halosoft.gui.App;
import org.halosoft.gui.interfaces.Controllable;
import org.halosoft.gui.objects.Client;
import org.halosoft.gui.objects.ObservableUser;


/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ChatPanelController implements Controllable,
        Initializable{
    
    private ObservableUser userData;
    
    private HostSelectorController parentController;
    
    private SQLiteConnector userDatabase;
    
    private ExecutorService executorService;
    private Client remoteCli;
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
        
        executorService=Executors.newCachedThreadPool();
    }
    
    /**
     * close communication socket
     */
    public void close(){
        try {
            this.executorService.shutdownNow();
            
            this.remoteCli.stop();
            
        } catch(NullPointerException ex){
            App.logger.log(Level.FINE,"Socket is possibly null."
                    + " Pass closing",ex);
            
        }
    }
    
    public void setContents(ObservableUser userData){

            this.userData=userData;

            this.userNameLabel.textProperty().bind(this.userData.getNameProperty());
            
            this.remoteCli=new Client(this.userData.getID());
            
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
        
        executorService.execute(()->{
            //also listen for client socketIn
            
            while( !Thread.currentThread().isInterrupted() ){
                String message=this.remoteCli.receive();
                
                if (message==null  || message.equals("SHUTDOWN")) {
                    break;
                }

                System.out.println("received from server to cli:"+message);
                Platform.runLater( ()->{
                    addMessage(message.trim(), Pos.TOP_LEFT);
                });
                //TODO: save message to database
                //userDatabase.query(TalkDBProperties.insertIntoMessageQueue(1, message));  
                
            }
        });
        
        executorService.execute(() -> {
            
            try {
            
                //wait for ServerHandler to initialize message database,
                Path dbfile=Paths.get(TalkDBProperties.DEFAULT_STORAGE_PATH, TalkDBProperties.nameTheDB(remoteCli.getRemoteIp()) );
                while ( !SQLiteDatabaseManager.databaseExists( dbfile ) ) {

                    System.err.println(String.format("database file:%s not exists. waiting..",dbfile.toString()));
                    Thread.sleep(300);
                }

            
                userDatabase=SQLiteDatabaseManager.openDatabase(dbfile);
                
                while ( !Thread.currentThread().isInterrupted() ){

                    
                    QueryResultSet messageList=userDatabase.query(TalkDBProperties.getUnreadMessages());

                    for (int i = 0; i < messageList.getRecordCount(); i++) {

                        Platform.runLater( ()->{
                            addMessage(messageList.getNextRecord().getLast(), Pos.TOP_LEFT);
                        }); 
                    }
                }
            } catch (InterruptedException | NoSuchFileException ex) {
                App.logger.log(Level.INFO,"chat panel messaging error",ex);
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
            this.remoteCli.send(message);

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
