package org.halosoft.talk.controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.halosoft.talk.controllers.MessageBoxPanelController;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
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
import org.halosoft.talk.App;
import org.halosoft.talk.objects.Client;
import org.halosoft.talk.interfaces.Controllable;
import org.halosoft.talk.objects.ObservableUser;
import org.halosoft.talk.objects.Server;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class ChatPanelController implements Controllable,
        Initializable{
    
    private ObservableUser userData;
    
    private HostSelectorController parentController;
    
    private File HISTPath;

    private FileWriter chatHistory;
    
    private ExecutorService executorService;
    
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
    @FXML
    private HBox bottomMessageBorder;

    private Text MessageText; //to autosize messageTextfield
    //ref:https://stackoverflow.com/questions/18588765/how-can-i-make-a-textarea-stretch-to-fill-the-content-expanding-the-parent-in-t
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO 
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
            this.stackPane.getChildren().clear();
            
            try {
                uContact = (Pane) App.loadFXML("view/userContact");
                UserContactController ctrlr=(UserContactController) uContact.getUserData();
                ctrlr.setUserData(this.userData);
                
                ctrlr.setParentController(this.parentController);
                uContact.setPrefWidth(240);
                
                this.stackPane.getChildren().add(uContact);
                this.stackPane.setAlignment(uContact, Pos.TOP_LEFT);
                
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
        this.userData=new ObservableUser();
        this.userNameLabel.textProperty().bind(this.userData.getNameProperty());
    }
    
    private void initChatHistoryWriter(){
        try {
            this.chatHistory=new FileWriter(new File( 
                  HISTPath, "HIST" ), true);
            
        } catch (FileNotFoundException ex) {
            try {
                
                Files.createFile(new File(HISTPath, "HIST").toPath());
                
                this.chatHistory=new FileWriter(new File( 
                  HISTPath, "HIST" ), true);
                
            } catch (IOException ex1) {
                App.logger.log(Level.SEVERE, 
                        "Error while creating HIST file",ex1);
            }
        } catch (IOException ex) {
            App.logger.log(Level.FINEST, 
                        "No HIST file found. Will create one",ex);
        }
    }
    
    private void initMessages(){
        try {
            BufferedReader reader=new BufferedReader(new FileReader(
                    Paths.get(HISTPath.toString(),
                            "HIST" ).toFile()));
            
            String line;
            while( (line=reader.readLine()) !=null){
                
                String[] list=line.split(":");
                if (list[0].equals("you")) {
                    this.addMessage(list[1], Pos.TOP_RIGHT);

                }else{
                    this.addMessage(list[1], Pos.TOP_LEFT);

                }
            }
        } catch (FileNotFoundException ex) {
            App.logger.log(Level.FINEST,
                    "No conversation history file found. Skipping");
        
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while reading HIST file",ex);
        }
    }
    /**
     * close communication socket
     */
    public void close(){
        try {
            this.executorService.shutdownNow();
            
            this.remoteClient.stop();
            this.chatHistory.close();
            
        } catch(NullPointerException ex){
            App.logger.log(Level.FINE,"Socket is possibly null."
                    + " Pass closing",ex);
            
        } catch (IOException ex) { 
            App.logger.log(Level.SEVERE, 
                        "Error while closing communication mediums"
                                + " of chatPanel View",ex);
        }
    }
    
    public void setContents(ObservableUser userData){
        try {
            this.userData.setContents(userData);
            
            //connect to desired remote end according to userData
            remoteClient=new Client( this.userData.getID() );
            try {
                HISTPath=new File(Paths.get(App.class.
                        getResource("userBuffers").toURI()).toString(),
                        this.remoteClient.getRemoteIp());
            
            Files.createDirectories(HISTPath.toPath());
            } catch (URISyntaxException ex) {
                App.logger.log(Level.WARNING, 
                        "Error while making directories of user buffer",ex);
            }
            
            this.initMessages();//if there is communication history, load to screen
            this.initChatHistoryWriter();
            this.listenMessage();
            
            this.userImageView.setImage(this.userData.getImage());
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while setting user contents and variables",ex);
        }
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
    
    //use to save message to chatHistory
    private void saveToFile(String message, Pos pos){
        try{
            if (pos.equals(Pos.TOP_LEFT)) {
                this.chatHistory.write("remote:"+message);
            }else{
                this.chatHistory.write("you:"+message);
            }
            this.chatHistory.write("\n");
            this.chatHistory.flush();
            
        }catch(IOException ex){
            App.logger.log(Level.SEVERE, 
                        "Error while saving chat history to file",ex);
        }
    }
    /**
     * listens for messages that comes from remote end
     */
    private void listenMessage(){
        
        executorService.execute(() -> {
            
            LinkedBlockingQueue<String> incomingsQueue;
            //wait for ServerHandler to initialize message queue
            while (  (incomingsQueue=Server.clients
                    .get(this.userData.getID())[0])  ==  null  ){
                try {
                    Thread.sleep(1000);
                    
                } catch (InterruptedException ex) {
                    App.logger.log(Level.SEVERE, 
                        "interrupted while setting variable"
                                + " 'incomingsQueue'",ex);
                    break;
                }
            }

            while ( !Thread.currentThread().isInterrupted() ){
                
                try {
                    
                    String message=incomingsQueue.take();
                    
                    Platform.runLater( ()->{
                        addMessage(message, Pos.TOP_LEFT);
                    });
                    //save message history to file
                    this.saveToFile(message, Pos.TOP_LEFT);
                    
                } catch(NullPointerException ex){
                    App.logger.log(Level.SEVERE, 
                        "incomingsQueue is possibly null",ex);
                    break;
                    
                } catch (InterruptedException ex) {
                    App.logger.log(Level.FINEST, 
                        "interrupted while getting messages from "
                                + "incomingsQueue",ex);
                    
                    break;
                }
            }
        });
    }

    @FXML
    private void sendButtonMouseClicked(MouseEvent event) {
        
        String message=this.messageTextField.getText();
        message=message.trim();
        if ( !message.isBlank() ) {
            
            this.addMessage(message, Pos.TOP_RIGHT);
            //save message history to file
            this.saveToFile(message, Pos.TOP_RIGHT);
            
            try {
                //send message to remote end
                this.remoteClient.getSocketOutputStream().writeUTF(message);
            } catch(NullPointerException ex){

                App.logger.log(Level.SEVERE, 
                        "Messsage could not send through client object",ex);
                
            }catch (IOException ex) {
                App.logger.log(Level.SEVERE, 
                        "Error while sending message to remote end",ex);
            }
            
            
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
