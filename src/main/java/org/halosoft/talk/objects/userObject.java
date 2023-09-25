/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javafx.scene.image.Image;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 * 
 * Provides general information about an user
 */
public class userObject {
    private String name;
    private String surname;
    private String hostName;
    
    private int status;
    private String statusMessage;
    
    private String ipAddress;
    private Image image;
    
    
    
    public userObject(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress, Image img){
        this.hostName=hostName;
        this.name=name;
        this.surname=surname;
        this.status=status;
        this.statusMessage=StatusMessage;
        this.ipAddress=ipAddress;
        
        this.image=img;
        
    }
    public userObject(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress){
        
        this(hostName, name, surname, status, StatusMessage, ipAddress,
                new Image(App.class.getResource(
                        "/images/icons/person.png").toString())
        );   
    }
    public userObject(){
        this("unknown" ,System.getenv("USERNAME"),"*",2,
                "Heyyo! I am using HaloTalk",
                "127.0.0.1");
        
        try {
            this.hostName=this.name+"@"+InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {}
    }
    
    /**
     * 
     * @return ip address of user
     */
    public String getID(){
        return this.ipAddress;
    }
    public void setID(String ip){
        this.ipAddress=ip;
    }
    
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    
    public String getSurName(){
        return this.surname;
    }
    public void setSurname(String surname){
        this.surname=surname;
    }
    
    public String getHostName(){
        return this.hostName;
    }
    public void setHostName(String name){
        this.hostName=name;
    }
    
    public int getStatus(){
        return this.status;
    }
    public void setStatus(int status){
        this.status=status;
    }
    
    public String getStatusMessage(){
        return this.statusMessage;
    }
    public void setStatusMessage(String status){
        this.statusMessage=status;
    }
    
    public Image getImage(){
        return this.image;
    }
    public void setImage(Image img){
        this.image=img;
    }
    
    public void setContents(userObject userData){
        this.setHostName(userData.getHostName());
        this.setID(userData.getID());
        this.setImage(userData.getImage());
        this.setName(userData.getName());
        this.setSurname(userData.getSurName());
        this.setStatus(userData.getStatus());
        this.setStatusMessage(userData.getStatusMessage());
    }
    
}
