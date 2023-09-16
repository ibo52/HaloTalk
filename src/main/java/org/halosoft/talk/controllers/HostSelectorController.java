/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
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
import org.halosoft.talk.objects.NetworkDeviceManager;
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
        /*
        this.appendUser(new userObject("ibram","mut",2,
                "bir tavuk dürüm bir de sen be gülüm",
        "0.0.0.0"));
        */
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
                        
        } catch (IOException ex) {
            System.out.println("hostSelector bringChatScreen:"+ex.getMessage());
        }
    }
    
    public StackPane getLeftStackPane(){
        return this.leftStackPane;
    }
    
    private void LANBrowser(){
        
        Thread browserThread=new Thread( new Runnable(){
            
           @Override
           public void run(){
               
               NetworkDeviceManager m=new NetworkDeviceManager();
               NetworkInterface ni=m.getInterfaceDevices(
                       NetworkDeviceManager
                               .ConnectionType.WIRELESS).get(0);
               
               //System.out.println("selected ni:"+ni.getName()+" ");

               while( !Thread.currentThread().isInterrupted() ){
                   
                   String hostIdentity=NetworkDeviceManager
                           .calculateNetworkIdentity(ni);
                   
                    for (int i = 1; i < 254; i++) {
                        String host=hostIdentity.substring( 
                                0, 
                                hostIdentity.lastIndexOf('.')+1);
                        
                        host+=+i;
                        
                         try {
                             //check if there is proper network device with this ip address
                             if ( !InetAddress.getByName(host).isLoopbackAddress()
                                     & InetAddress.getByName(host).isReachable(50) ) {
                                 
                                 //check if host has this application
                                 BroadcastClient LANdiscover =new BroadcastClient(host);
                                 
                                 LANdiscover.start();
                                 
                                 //parse incoming user data
                                 String[] idt=new String(LANdiscover.getBuffer(), 0, LANdiscover.getBufferLength()).split(";");
                                 
                                  //if LANbrowser finds own broadcaster(itself) on LAN
                                 //just continue next iteration
                                 if ( idt[0].equals("NO_RESPONSE") || idt[0].equals(LANBroadcaster.getHostName()) ) {
                                     continue;
                                 }
                                 
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
                             System.out.println("LANbrowser()"+ex.getMessage());
                         } catch (IOException ex) {
                             System.out.println("LANbrowser()"+ex.getMessage());
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
    
    private void updateUser(userObject userInfo, Parent Box){
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();

                ctrlr.setContents(ctrlr);

                FadeTransition ft=new FadeTransition();
                ft.setNode(Box);
                ft.setDuration(Duration.millis(300));
                ft.setFromValue(0.2);
                ft.setToValue(1);

                ft.play();
            }
        });
        
    }
    private void appendUser(userObject userData){
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {

                try {
                    Parent Box= App.loadFXML("userInfoBox");

                    UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();

                    ctrlr.setContents(userData);

                    usersBox.getChildren().add(Box);

                    ctrlr.startAnimation();
                    
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
    }
}
