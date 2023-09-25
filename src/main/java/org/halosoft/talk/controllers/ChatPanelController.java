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
    
    private BufferedReader remoteIn;
    private FileWriter chatHistory;
    
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
    
    private void initChatHistory(){
        try {
            this.chatHistory=new FileWriter(new File(new File(
                            App.class.getResource("userBuffers").getPath(),
                            this.remoteClient.getRemoteIp()),"HIST" )
                    ,true);
        } catch (FileNotFoundException ex) {
            try {
                System.out.println("no Conversation history file found. Skipping..");
                new File(new File(
                        App.class.getResource("userBuffers").getPath(),
                        this.remoteClient.getRemoteIp()),"HIST" )
                        .createNewFile();
                this.initChatHistory();
                
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void initMessages(){
        try {
            BufferedReader reader=new BufferedReader(new FileReader(new File(new File(
                            App.class.getResource("userBuffers").getPath(),
                            this.remoteClient.getRemoteIp()),"HIST" )));
            
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
            System.out.println("no hist file:"+ex.getMessage()+"\nskipping");
        } catch (IOException ex) {
            System.out.println("error whle read chat history:"+ex.getMessage());
        }
    }
    /**
     * close communication socket
     */
    public void close(){
        try {
            this.remoteClient.stop();
            this.remoteIn.close();
            this.chatHistory.close();
            
            //delete remote end's socket IN file on close requested
            new File(new File(
                            App.class.getResource("userBuffers").getPath(),
                            this.remoteClient.getRemoteIp()),"IN" )
                    .delete();
            
        } catch (IOException ex) { 
            System.out.println(""+ex.getMessage());;
        }
    }
    @Override
    public void setContents(userObject userData){
        super.setContents(userData);
        
        //connect to desired remote end according to userData
        remoteClient=new Client( this.getID() );
        this.initMessages();//if there is communication history, load to screen
        this.initChatHistory();
        this.listenMessage();
        
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
            ex.printStackTrace();
        }
    }
    /**
     * listens for messages that comes from remote end
     */
    private void listenMessage(){
        
        Thread msgListener=new Thread( () -> {
            
            //wait until there is socket in file exists
            while(true){
                try {//access senrver socketIn file for that remote end
                    FileReader clientInFile=new FileReader( new File(new File(
                            App.class.getResource("userBuffers").getPath(),
                            this.remoteClient.getRemoteIp()),"IN" ) );
                    this.remoteIn=new BufferedReader(clientInFile);
                    break;

                } catch (FileNotFoundException ex) {

                    try {
                        System.out.println("there is NO file to read"+
                               new File(new File(
                            App.class.getResource("userBuffers").getPath(),
                            this.remoteClient.getRemoteIp()),"IN" ).getPath() );
                        Thread.sleep(300);
                    } catch (InterruptedException ex1) {
                        ex1.printStackTrace();
                    }
                }
            }

            while ( !Thread.currentThread().isInterrupted() ){
                
                try {
                    while (!this.remoteIn.ready() ){
                        Thread.sleep(300);
                    }
                    String message=this.remoteIn.readLine();
                    Platform.runLater( ()->{
                        addMessage(message, Pos.TOP_LEFT);
                    });
                    //save message history to file
                    this.saveToFile(message, Pos.TOP_LEFT);
                    
                } catch ( SocketException ex) {
                    System.out.println("chatPanel client Socket:"+ex.getMessage());
                    this.remoteClient=null;
                    break;
                    
                }catch (IOException ex) {
                    System.out.println("chatPanel client Socket:"+ex.getMessage());
                    this.remoteClient=null;
                    break;
                    
                } catch (InterruptedException ex) {
                    System.out.println("chatPanel Client listen:"+ex.getMessage());
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
            //save message history to file
            this.saveToFile(message, Pos.TOP_RIGHT);
            
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
        this.close();//close files and sockets
        
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
    
}
