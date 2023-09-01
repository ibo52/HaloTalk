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

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class HostSelectorController implements Initializable {

    private Broadcaster LANBroadcaster;
    
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
        LANBroadcaster=new Broadcaster();
        LANBroadcaster.start();
        
        LANBrowser();
        //this.chatPanelLayout.addEventFilter(EventType.ROOT, eh);
        
        this.appendUser("sample host;1;sample status message", "127.0.0.1");
        // TODO
    }
    
    public void bringChatScreen(UserInfoBoxController userInfoBox_ctrlr){
        try {
            
            Parent chatPanel=App.loadFXML("chatPanel");
            ChatPanelController ctrlr=(ChatPanelController) chatPanel.getUserData();
            
            ctrlr.setContents(userInfoBox_ctrlr.getUserID(), userInfoBox_ctrlr.getImage());
            
            chatPanelLayout.setCenter(chatPanel);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
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
                   
                    for (int i = 1; i < 254; i++) {
                        
                         String host="192.168.1."+i;
                        
                         try {
                             //check if there is proper network device with this ip address
                             if ( InetAddress.getByName(host).isReachable(50) ) {
                                 
                                 //check if host has this application
                                 BroadcastClient LANdiscover =new BroadcastClient(host);
                                 
                                 LANdiscover.start();
                                 
                                 
                                 Iterator iter=usersBox.getChildren().iterator();
                                 boolean appendFlag=true;
                                 
                                 while( iter.hasNext() ){
                                     
                                     Parent v=(Parent)iter.next();
                                     
                                     UserInfoBoxController ctrlr=(UserInfoBoxController)v.getUserData();

                                     if (host.equals(ctrlr.getID()) ) {
                                         appendFlag=false;
                                         updateUser(new String(LANdiscover.getBuffer(), 0, LANdiscover.getBufferLength()), v);
                                         break;
                                     }
                                 }
                                 if (appendFlag) {
                                    appendUser(new String(LANdiscover.getBuffer(), 0, LANdiscover.getBufferLength() ), host);
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
    
    public void updateUser(String userInfo, Parent Box){
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();
                if ( !userInfo.equals(new String("NO_RESPONSE" ))) {

                    String[] parse=userInfo.split(";");

                    ctrlr.setUserName(parse[0]);
                    ctrlr.setStatus(Integer.parseInt(parse[1]));
                    ctrlr.setCustomStatus(parse[2]);
                    
                    FadeTransition ft=new FadeTransition();
                    ft.setDuration(Duration.millis(300));
                    ft.setFromValue(0.2);
                    ft.setToValue(1);
                    
                    ft.play();
                }
            }
        });
        
    }
    public void appendUser(String userInfo, String ipAddress){
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                try {
                    Parent Box= App.loadFXML("userInfoBox");

                    UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();

                    if ( !userInfo.equals(new String("NO_RESPONSE" ))) { 

                        String[] parse=userInfo.split(";");

                        ctrlr.setUserName(parse[0]);
                        ctrlr.setStatus(Integer.parseInt(parse[1]));
                        ctrlr.setCustomStatus(parse[2]);
                        
                        ctrlr.setID(ipAddress);
                        usersBox.getChildren().add(Box);
                        
                        TranslateTransition tt=new TranslateTransition();
                        tt.setDuration(Duration.millis(300));
                        tt.setNode(Box);
                        tt.setFromX(-100);
                        tt.setToX(0);
                        
                        tt.play();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
    }
}
