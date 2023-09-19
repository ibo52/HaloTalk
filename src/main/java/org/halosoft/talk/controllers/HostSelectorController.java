/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    @FXML
    private HBox statusBar;
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
        
        this.appendUser(new userObject("testing@127.0.0.1","test","user",2,
                "bir tavuk dürüm bir de sen be gülüm"
                +"asddddddddddddddddddddddddddddddddddd",
        "127.0.0.1"));
        
        // TODO
    }
    
    /**
     * Places chat panel of specific user to screen
     * @param ctrlr_userData information about remote user
     */
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
    
    /**
     * discovers the LAN for users of this program
     */
    private void LANBrowser(){
        
        ExecutorService browserService=Executors.newSingleThreadExecutor();
        
        browserService.execute( () -> {
            
            while( !Thread.currentThread().isInterrupted() ){
                
                if ( !NetworkDeviceManager.checkForConnectivity() ) {
                    System.err.println("No internet connection. Sleep for 3000 ms");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    continue;
                }
                //get a device from manager and calculate network ID
                NetworkDeviceManager manager=new NetworkDeviceManager();
                NetworkInterface ni=manager.getInterfaceDevices(
                        NetworkDeviceManager
                                .ConnectionType.WIRELESS).get(0);
                
                System.out.println("selected ni:"+ni.getName()+" ");
                
                String hostIdentity=NetworkDeviceManager
                        .calculateNetworkIdentity(ni);
                
                ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()*2);
                
                int i=Integer.parseInt(hostIdentity.substring
                    (hostIdentity.lastIndexOf('.')+1
                            ,hostIdentity.length())  );
                
                for (; i < 254; i++) {
                    
                    final int executorArgument=i;
                    executorService.execute( ()->{
                        
                        String host=hostIdentity.substring(
                                0,
                                hostIdentity.lastIndexOf('.')+1);
                        
                        host+=+executorArgument;
                        
                        try {
                            //check if there is proper network device with this ip address
                            if ( !InetAddress.getByName(host)
                                    .isLoopbackAddress() ) {
                                
                                //check if host has this application
                                BroadcastClient LANdiscover =new BroadcastClient(host);
                                
                                LANdiscover.start();
                                
                                //parse incoming user data
                                String[] idt=new String(LANdiscover.getBuffer(), 0, LANdiscover.getBufferLength()).split(";");
                                
                                //if LANbrowser finds own broadcaster(itself)
                                //on LAN, dont add to userInfoPanel
                                if ( !idt[0].equals("NO_RESPONSE") &
                                        !idt[0].equals(LANBroadcaster.getHostName()) ) {
                                    
                                    userObject userData=new userObject(idt[0], idt[3],
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
                            }
                        } catch (UnknownHostException ex) {
                            System.out.println("LANbrowser()"+ex.getMessage());
                        }
                        
                    });
                }
                executorService.shutdown();
                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    LANBroadcaster.stop();
                    System.out.println("statEmitter stopped beacuse of interrupt");
                    ex.printStackTrace();
                }
            }
        });
        browserService.shutdown();
    }
    
    private void garbageUserCollector(){
        ExecutorService s=Executors.newSingleThreadExecutor();
        
        s.execute( ()->{
            Iterator iter=usersBox.getChildren().iterator();

            while( iter.hasNext() ){

                Parent v=(Parent)iter.next();

                UserInfoBoxController ctrlr=(UserInfoBoxController)v.getUserData();

                BroadcastClient cli=new BroadcastClient(ctrlr.getID());
                
                String[] received=new String(cli.getBuffer(), 0, 
                        cli.getBufferLength()).split(";");
                
                if (received[0].equals("NO_RESPONSE")) {
                    ctrlr.stopAnimation();
                    this.usersBox.getChildren().remove(v);
                }
            }
        });
        
    }
    
    /**
     * updates specific user with new information data
     * @param userInfo new information data of user
     * @param Box Node contains information of that user
     */
    private void updateUser(userObject userInfo, Parent Box){
        
        Platform.runLater( () -> {
            UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();
            
            ctrlr.setContents(userInfo);
            
            FadeTransition ft=new FadeTransition();
            ft.setNode(Box);
            ft.setDuration(Duration.millis(300));
            ft.setFromValue(0.2);
            ft.setToValue(1);
            
            ft.play();
        });
        
    }
    
    /**
     * Creates a node with user information, appends it to screen
     * @param userInfo information data of user
     */
    private void appendUser(userObject userData){
        
        Platform.runLater( () -> {
            try {
                Parent Box= App.loadFXML("userInfoBox");

                UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();
                
                ctrlr.setContents(userData);
                ctrlr.setParentController(this);
                
                usersBox.getChildren().add(Box);
                
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });
        
    }
}
