/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.halosoft.talk.App;
import org.halosoft.talk.objects.BroadcastClient;
import org.halosoft.talk.objects.Broadcaster;
import org.halosoft.talk.objects.Client;
import org.halosoft.talk.objects.Server;
import org.halosoft.talk.objects.userObject;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class HostSelectorController implements Initializable {
    private Server server;              //to let others conect to you
    
    private Client connectorClient;     //to request connection from others
    private Broadcaster LANBroadcaster; //to browse for local devices that use this program
    
    @FXML
    private VBox usersBox;
    @FXML
    private BorderPane chatPanelLayout;
    @FXML
    private StackPane leftStackPane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        server=new Server();
        server.start();
        
        LANBroadcaster=new Broadcaster();
        LANBroadcaster.start();
        
        LANBrowser();
        
        this.appendUser(new userObject("ibram","mut",2,
                "bir tavuk dürüm bir de sen be gülüm",
        "0.0.0.0"));
        // TODO
    }
    
    public void bringChatScreen(userObject ctrlr_userData){
        try {
            if (this.chatPanelLayout.getCenter()!=null ) {
                ChatPanelController oldCtrlr=(ChatPanelController) this.chatPanelLayout.getCenter().getUserData();
                oldCtrlr.closeClient();
            }
            
            Parent chatPanel=App.loadFXML("chatPanel");
            ChatPanelController ctrlr=(ChatPanelController) chatPanel.getUserData();
            
            ctrlr.setContents(ctrlr_userData);
            
            connectorClient=new Client( ctrlr_userData.getID() );
            //connectorClient.start();
            ctrlr.setClient(connectorClient);
            
            chatPanelLayout.setCenter(chatPanel);
            this.server.getSocketOutputStream().writeUTF("deneme kontrol fgrom server");
            
        } catch (IOException ex) {
            System.out.println("hostSelector bringChatScreen:"+ex.getMessage());
        }
    }
    
    public StackPane getLeftStackPane(){
        return this.leftStackPane;
    }
    
    public void LANBrowser(){
        
        Thread browserThread=new Thread( new Runnable(){
           @Override
           public void run(){
               
               while( !Thread.currentThread().isInterrupted() ){
                   
                    for (int i = 66; i < 68; i++) {
                        
                         String host="192.168.1."+i;

                         try {
                             //check if there is proper network device with this ip address
                             if ( InetAddress.getByName(host).isReachable(50) ) {
                                 
                                 //check if host has this application
                                 BroadcastClient LANdiscover =new BroadcastClient(host);
                                 
                                 LANdiscover.start();
                                 
                                 //parse incoming user data
                                 String[] idt=new String(LANdiscover.getBuffer(), 0, LANdiscover.getBufferLength()).split(";");
                                 if (idt[0].equals("NO_RESPONSE") ) {
                                     System.out.println(host+" is up but does not use HaloTalk!");
                                     continue;
                                 }
                                 
                                 //if LANbrowser finds own broadcaster(itself) on LAN
                                 //just continue next iteration
                                 /*if ( idt[0].equals("NO_RESPONSE") || idt[0].equals(LANBroadcaster.getHostName()) ) {
                                     continue;
                                 }*/
                                 
                                 userObject userData=new userObject(idt[3],
                                         idt[4],Integer.parseInt(idt[1]),
                                         idt[2],host);
                                 Iterator iter=usersBox.getChildren().iterator();
                                 boolean appendFlag=true;
                                 
                                 while( iter.hasNext() ){
                                     
                                     Parent v=(Parent)iter.next();
                                     
                                     UserInfoBoxController ctrlr=(UserInfoBoxController)v.getUserData();
                                     
                                     if (host.equals(ctrlr.getID()) ) {
                                         appendFlag=false;
                                         updateUser(userData, v);
                                         break;
                                     }
                                 }
                                 if (appendFlag) {
                                    appendUser(userData);
                                 }
                             }
                         } catch (UnknownHostException ex) {
                             System.out.println(ex.getMessage());
                         } catch (IOException ex) {
                             System.out.println(ex.getMessage());
                         }
                    }
                    
                   try {
                       Thread.sleep(3000);
                   } catch (InterruptedException ex) {
                       LANBroadcaster.stop();
                       System.out.println("statEmitter stopped beacuse of interrupt");
                       ex.printStackTrace();
                   }
                }
           }
        });
        
        browserThread.setDaemon(true);
        browserThread.start();
    }
    
    public void updateUser(userObject userInfo, Parent Box){
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();

                ctrlr.setContents(ctrlr);

                FadeTransition ft=new FadeTransition();
                ft.setDuration(Duration.millis(300));
                ft.setFromValue(0.2);
                ft.setToValue(1);

                ft.play();
            }
        });
        
    }
    public void appendUser(userObject userData){
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                try {
                    Parent Box= App.loadFXML("userInfoBox");

                    UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();

                    ctrlr.setContents(userData);

                    usersBox.getChildren().add(Box);

                    TranslateTransition tt=new TranslateTransition();
                    tt.setDuration(Duration.millis(300));
                    tt.setNode(Box);
                    tt.setFromX(-100);
                    tt.setToX(0);

                    tt.play();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
    }
}
