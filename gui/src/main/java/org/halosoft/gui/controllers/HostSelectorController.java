/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.gui.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.halosoft.gui.App;
import org.halosoft.gui.controllers.setting.UserSettingsController;
import org.halosoft.gui.models.ObservableUser;
import org.halosoft.gui.models.User;
import org.halosoft.gui.models.net.BroadcastClient;
import org.halosoft.gui.models.net.Broadcaster;
import org.halosoft.gui.models.net.Server;
import org.halosoft.gui.models.net.LANBrowser;
import org.halosoft.gui.models.net.utils.NetworkDeviceManager;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class HostSelectorController implements Initializable {
    
    private final ScheduledExecutorService LANBrowserService;    //executor to run runnables on thread
    
    private final Server server;              //to let others conect to you
    
    protected final Broadcaster statusBroadcaster; //to emit info about your existence for LAN devices that use this program

    @FXML
    private VBox usersBox;
    @FXML
    private BorderPane chatPanelLayout;
    @FXML
    private StackPane leftStackPane;
    @FXML
    private HBox statusBar;
    @FXML
    private Label logo;
    @FXML
    private Button settingsButton;
    @FXML
    private Button searchButton;
    @FXML
    private StackPane statusBarStackPane;
    @FXML
    private HBox searchPane;
    @FXML
    private TextField searchField;
    @FXML
    private Region searchPaneUndoButton;
    @FXML
    private BorderPane rootPane;

    public HostSelectorController() {
        
        server=new Server();
        server.start();
        
        statusBroadcaster=new Broadcaster("0.0.0.0");
        statusBroadcaster.start();

        statusBroadcaster.getCallReachedProperty().addListener((obs, oldV, newV)->{

            try {

                String[] address=statusBroadcaster.getCallerAddressProperty().getValue().split(":");
                
                BroadcastClient forwardedCall=new BroadcastClient(address[0], Integer.valueOf(address[1]) );
                
                forwardedCall.send( "{'response':'reject ehbele'}".toString().getBytes() );
                
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        });
        
        LANBrowserService=Executors.newScheduledThreadPool(1);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
        initializeLANBrowser();

        this.appendUser(new ObservableUser("testing@127.0.0.1","loopback","user",2,
                "This is a loopback address for Testing purposes",
        "127.0.0.1"));
        /* 
        this.appendUser(new ObservableUser("testing@unreachable","unreachable","user",2,
                "This is a sample No use address for Testing purposes",
        "192.0.192.0"));
        */
        this.appendUser(new ObservableUser("testing@virtualbox","vbox","user",2,
                "This is a sample vbox address for Testing purposes",
        "192.168.122.48"));
        
    }
    
    /**
     * Places chat panel of specific user to screen
     * @param ctrlr_userData information about remote user
     */
    public synchronized void bringChatScreen(ObservableUser ctrlr_userData){
        try {
            if (this.chatPanelLayout.getCenter()!=null ) {
                ChatPanelController oldCtrlr=(ChatPanelController) this.chatPanelLayout.getCenter().getUserData();
                if (ctrlr_userData.getID().equals( oldCtrlr.getUserData().getID() )) {
                    return;
                }else{
                    oldCtrlr.remove();
                }
            }

            Parent chatPanel=App.loadFXML("view/chatPanel");
            ChatPanelController ctrlr=(ChatPanelController) chatPanel.getUserData();
            
            ctrlr.setParentController(this);
            
            ctrlr.setContents(ctrlr_userData);
            
            chatPanelLayout.setCenter(chatPanel);
                        
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error occured while loading chat screen",ex);
        }
    }
    
    public StackPane getLeftStackPane(){
        return this.leftStackPane;
    }
    
    /**
     * browse through the subnet of LAN for users of this program
     */ 
    private class LANBrowserInitializer implements Runnable{

        @Override
        public void run() {

                //get a device from manager and calculate network ID
                //NetworkDeviceManager manager=new NetworkDeviceManager();
                /*NetworkInterface ni=manager.getInterfaceDevice("virbr0");manager.getInterfaceDevices(
                        NetworkDeviceManager
                                .ConnectionType.WIRELESS).get(0);*/

                //check for every available network interface
                for (NetworkInterface ni : NetworkDeviceManager.getInterfaceDevices() ) {
                
                    String hostIdentity=NetworkDeviceManager
                        .calculateNetworkIdentity(ni);
                //if ni is loopback, pass scan
                if ( hostIdentity.startsWith("127.0") ) {

                        App.logger.log(Level.FINEST,"pass LAN scan since"
                            + " network interface is loopback");
                        continue;
                }
                InetAddress niAddress=ni.getInetAddresses().nextElement();

                /*System.out.println("Selected network interface:"
                        +ni.getName()+"-> "+niAddress.getHostName()+" in network Identity:"+hostIdentity);
                */
                ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()*2);
                
                int i=Integer.parseInt(hostIdentity.substring
                    (hostIdentity.lastIndexOf('.')+1
                            ,hostIdentity.length()) )+1;
                
                for (; i < 254; i++) {
                    
                    final int executorArgument=i;
                    String host=hostIdentity.substring(
                                0,
                                hostIdentity.lastIndexOf('.')+1);
                        
                        host+=+executorArgument;
                        LANBrowser task=new LANBrowser(host);

                        task.setOnSucceeded(e->{
                            ObservableUser userData=task.getValue();

                            //check if host address is not of this Broadcaster'(and not loopback)
                            if ( !NetworkDeviceManager.getIpV4Address(ni)
                            .equals( task.getArgument() ) ) {
                                
                                //check if remote did respond
                                if ( userData!=null ) {
                                    
                                    Iterator<Node> iter=usersBox.getChildren().iterator();
                                    boolean appendFlag=true;
                                    
                                    while( iter.hasNext() ){
                                        
                                        Parent v=(Parent)iter.next();
                                        
                                        UserInfoBoxController ctrlr=(UserInfoBoxController)v.getUserData();
                                        
                                        if (task.getArgument().equals(ctrlr.getUserData().getID()) ) {
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
                        });
                    executorService.execute(task);
                }
                
                executorService.shutdown();
            }
        }
        
    }
    
    private void initializeLANBrowser(){

        LANBrowserService.scheduleAtFixedRate(new LANBrowserInitializer(),
                0, 5, TimeUnit.SECONDS);

        //executorService.shutdown();
    }
    
    /**
     * updates specific user with new information data
     * @param userInfo new information data of user
     * @param Box Node contains information of that user
     */
    private void updateUser(ObservableUser userInfo, Parent Box){
        
        Platform.runLater( () -> {
            UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();
            
            ctrlr.setUserData(userInfo);
            
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
    private void appendUser(ObservableUser userData){
        
        
            try {
                Parent Box= App.loadFXML("view/userInfoBox");

                UserInfoBoxController ctrlr=(UserInfoBoxController) Box.getUserData();
                
                ctrlr.setUserData(userData);
                ctrlr.setParentController(this);
            
                Platform.runLater( () -> {    
                    usersBox.getChildren().add(Box);
                });
                
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, 
                        ex.getMessage(),ex);
            }
        
        
    }
    
    /**
     * update the broadcaster variables that contains personal 
     * information of current user
     * @param userData personal information to update with old
     */
    public void updateBroadcasterData(User userData){
        this.statusBroadcaster.getObservableUser().setContents(userData);
    }
    
    @FXML
    private void searchPaneUndoButtonMouseClicked(MouseEvent event) {
        this.searchButton.fireEvent(event);
    }
    
    @FXML
    private void searchButtonMouseClicked(MouseEvent event) {
        Node p=this.statusBarStackPane.getChildren().get(0);
        this.statusBarStackPane.getChildren().remove(p);
        this.statusBarStackPane.getChildren().add(p);
    }
    
    @FXML
    private void setttingsButtonMouseClicked(MouseEvent event) {
        try {
            Parent uSettings=App.loadFXML("view/setting/userSettings");
            UserSettingsController ctrlr=(UserSettingsController) uSettings.getUserData();
            ctrlr.setParentController(this);
            //remove all components on stackpane except first
            this.leftStackPane.getChildren().remove
                        (1, this.leftStackPane.getChildren().size());
            
            this.leftStackPane.getChildren().add(uSettings);
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        ex.getMessage(),ex);
        }
    }
}
