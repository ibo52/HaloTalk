/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import javafx.scene.layout.VBox;
import org.halosoft.talk.App;
import org.halosoft.talk.objects.Client;
import org.halosoft.talk.objects.Server;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class HostSelectorController implements Initializable {

    private Server server;
    
    @FXML
    private VBox usersBox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //server=new Server();
        System.out.println("server start");
        LANBrowser();
        
        // TODO
    }
    public void appendLANUser(Parent p){
        this.usersBox.getChildren().add(p);
    }
    public void LANBrowser(){
        
        Thread t=new Thread( new Runnable(){
           @Override
           public void run(){
               
               while( !Thread.currentThread().isInterrupted() ){
                    for (int i = 100; i < 255; i++) {
                         String host="192.168.1."+i;
                         try {
                             if ( InetAddress.getByName(host).isReachable(166) ) {
                                 
                                 System.out.println(host+"("
                                         +InetAddress.getByName(host).getCanonicalHostName()
                                 +") is up");
                                 
                                
                                 Platform.runLater(new Runnable(){
                                    @Override
                                    public void run() {

                                        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("userInfoBox.fxml"));
                                        try {
                                            Parent Box= fxmlLoader.load();
                                            UserInfoBoxController ctrlr=fxmlLoader.getController();

                                            ctrlr.setUserName(host);

                                            appendLANUser(Box);
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
  
                                 
                                 
                             }
                         } catch (UnknownHostException ex) {
                             ex.printStackTrace();
                         } catch (IOException ex) {
                             ex.printStackTrace();
                         }
                    }
                    
                   try {
                       Thread.sleep(3000);
                   } catch (InterruptedException ex) {
                       ex.printStackTrace();
                   }
                }
           }
        });
        
        t.setDaemon(true);
        t.start();
    }
}
