/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.halosoft.gui.App;

import javafx.scene.image.Image;

/**
 *
 * @author ibrahim
 * 
 * Provides general information about an user
 */
public class User {
    protected String name;
    protected String surname;
    protected String hostName;
    
    protected int status;
    protected String statusMessage;
    
    protected String ipAddress;
    protected Image image;
    
    protected BufferedReader imageInputStream;
    
    
    public User(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress, Image img){
        this.hostName=hostName;
        this.name=name;
        this.surname=surname;
        this.status=status;
        this.statusMessage=StatusMessage;
        this.ipAddress=ipAddress;
        
        this.image=img;
        /*try {
            this.imageInputStream=Files.newBufferedReader(
                    Paths.get(URI.create(img.getUrl()) ));

        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while initializing BufferedReader of image",ex);
        }*/
        imageInputStream=null;
    }
    public User(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress){
        
        this(hostName, name, surname, status, StatusMessage, ipAddress,
                new Image(App.class.getResourceAsStream(
                        "/images/icons/person.png"))
        );   
    }
    public User(){
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
    
    public void setContents(User userData){
        this.setHostName(userData.getHostName());
        this.setID(userData.getID());
        this.setImage(userData.getImage());
        this.setName(userData.getName());
        this.setSurname(userData.getSurName());
        this.setStatus(userData.getStatus());
        this.setStatusMessage(userData.getStatusMessage());
    }
    
    public void setContents(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress, Image img){
        this.hostName=hostName;
        this.name=name;
        this.surname=surname;
        this.status=status;
        this.statusMessage=StatusMessage;
        this.ipAddress=ipAddress;
        
        this.image=img;
        
    }
    
}
