/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;

import org.halosoft.gui.App;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

/**
 *
 * @author ibrahim
 */
public class ObservableUser extends User {
    
    protected final SimpleStringProperty nameProperty=new SimpleStringProperty();
    protected final SimpleStringProperty surnameProperty=new SimpleStringProperty();
    protected final SimpleStringProperty hostnameProperty=new SimpleStringProperty();
    protected final SimpleStringProperty statusProperty=new SimpleStringProperty();
    protected final SimpleStringProperty statusMessageProperty=new SimpleStringProperty();
    protected final SimpleStringProperty ipAddressProperty=new SimpleStringProperty();
    
    protected final Properties props=new Properties();

    public ObservableUser() {
        
        try {

            props.load(App.class.getResourceAsStream("settings/"
                    + "broadcaster.properties"));
        
        this.nameProperty.set(props.getProperty("NAME"));
        this.surnameProperty.set(props.getProperty("SURNAME"));
        this.hostnameProperty.set(this.hostName);
        this.statusProperty.set(props.getProperty("STATUS"));
        this.statusMessageProperty.set(props.getProperty("STATUS_MESSAGE"));
        this.ipAddressProperty.set(this.ipAddress);

        } catch (IOException ex) {
            App.logger.log(Level.WARNING,"User Data could not load "
                                    + "from disk. Pass",ex);
        }
        
        initListeners();
        
 
    }
    
    public ObservableUser(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress){
        
        super(hostName, name, surname, status, StatusMessage, ipAddress);
        
        this.nameProperty.set(this.name);
        this.surnameProperty.set(this.surname);
        this.hostnameProperty.set(this.hostName);
        this.statusProperty.set(String.valueOf(this.status));
        this.statusMessageProperty.set(this.statusMessage);
        this.ipAddressProperty.set(this.ipAddress);
        
        initListeners();
    }
    
    private final void initListeners(){
        
        this.nameProperty.addListener((Observable il)->{
            this.name=this.nameProperty.get();
            props.setProperty("NAME", name);
        });
        
        this.surnameProperty.addListener((Observable il)->{
            this.surname=this.surnameProperty.get();
            props.setProperty("SURNAME", surname);
        });
        
        this.hostnameProperty.addListener((Observable il)->{
            this.hostName=this.hostnameProperty.get();
            
        });
        
        this.statusProperty.addListener((Observable il)->{
            this.status=Integer.parseInt(this.statusProperty.get());
            props.setProperty("STATUS", String.valueOf(status) );

        });
        
        this.statusMessageProperty.addListener((Observable il)->{
            this.statusMessage=this.statusMessageProperty.get();
            props.setProperty("STATUS_MESSAGE", statusMessage);
        });
        
        this.ipAddressProperty.addListener((Observable il)->{
            this.ipAddress=this.ipAddressProperty.get();
        });
    }

    @Override
    public void setID(String ip) {
        this.ipAddressProperty.set(ip);
    }
    
    @Override
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    @Override
    public void setSurname(String surname) {
        this.surnameProperty.set(surname);
    }

    @Override
    public void setHostName(String name) {
        this.hostnameProperty.set(name);
    }

    @Override
    public void setStatus(int status) {
        this.statusProperty.set(String.valueOf(status));
    }

    @Override
    public void setStatusMessage(String status) {
        this.statusMessageProperty.set(status);
    }
    
    public SimpleStringProperty getNameProperty(){
        return this.nameProperty;
    }
    
    public SimpleStringProperty getSurNameProperty(){
        return this.surnameProperty;
    }
    
    public SimpleStringProperty getHostNameProperty(){
        return this.hostnameProperty;
    }
    
    public SimpleStringProperty getIDProperty(){
        return this.ipAddressProperty;
    }
    
    public SimpleStringProperty getStatusMessageProperty(){
        return this.statusMessageProperty;
    }
    
    public SimpleStringProperty getStatusProperty(){
        return this.statusProperty;
    }
    
    @Override
    public void setContents(User userData) {
        //super.setContents(userData); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        
        this.nameProperty.set(userData.getName());
        this.surnameProperty.set(userData.getSurName());
        this.hostnameProperty.set(userData.getHostName());
        this.ipAddressProperty.set(userData.getID());
        this.statusMessageProperty.set(userData.getStatusMessage());
        this.statusProperty.set(String.valueOf(userData.getStatus()));
    }
    
    @Override
    public void setContents(String hostName, String name, String surname, int status, String StatusMessage, String ipAddress, Image img) {
        //super.setContents(hostName, name, surname, status, StatusMessage, ipAddress, img); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        
        this.nameProperty.set(name);
        this.surnameProperty.set(surname);
        this.hostnameProperty.set(hostName);
        this.ipAddressProperty.set(ipAddress);
        this.statusMessageProperty.set(StatusMessage);
        this.statusProperty.set(String.valueOf(status));
    }
    
    public Properties getProperties(){
        return this.props;
    }

    public static ObservableUser readFromProperties(String packageRelativePath){
        Properties props=new Properties();
        ObservableUser user;
        
        try {
            props.load(App.class
                    .getResourceAsStream(packageRelativePath));
            
            String name=props.getProperty("NAME");
            String hostname=name+"@"+InetAddress.getLocalHost().getHostName();
            
            user=new ObservableUser( hostname,
                    name
                    , props.getProperty("SURNAME"),
                    Integer.parseInt(props.getProperty("STATUS")),
            props.getProperty("STATUS_MESSAGE"),
                    "0.0.0.0");
            return user;
            
        } catch (IOException ex) {
            App.logger.log(Level.FINEST, "Error while loading properties"
                    + " file for given path:"+packageRelativePath,ex);
            return null;
        }
    }
}
