/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import org.halosoft.talk.objects.Client;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class HostSelectorController implements Initializable {

    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Button confirmButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        LANBrowser();
        
        // TODO
    }
    public void LANBrowser(){
        
        Thread t=new Thread( new Runnable(){
           @Override
           public void run(){
               
               while(true){
                    for (int i = 0; i < 255; i++) {
                         String host="192.168.253."+i;

                         try {
                             if ( InetAddress.getByName(host).isReachable(166) ) {
                                 
                                 System.out.println(host+"("
                                         +InetAddress.getByName(host).getCanonicalHostName()
                                 +") is up");
                                 
                                 
                                 try{
                                    if( !choiceBox.getItems().contains(host) ){

                                        choiceBox.getItems().add(host);
                                    }
                                 }catch(IndexOutOfBoundsException ex){
                                     choiceBox.getItems().add(host);
                                 }
                                 
                                 
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

    @FXML
    private void confirmButtonOnMouseClicked(MouseEvent event) {
        
        String selection=choiceBox.getSelectionModel().getSelectedItem();
        
        if ( selection!=null) {
            System.out.println("selected:"+selection);
        }
        
    }
    
}
