/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 */
public class ObservableUser extends userObject {
    
    protected SimpleStringProperty nameProperty;
    protected SimpleStringProperty surnameProperty;
    protected SimpleStringProperty hostnameProperty;
    protected SimpleStringProperty statusProperty;
    protected SimpleStringProperty statusMessageProperty;
    protected SimpleStringProperty ipAddressProperty;
    
    protected final Properties props;

    public ObservableUser() {
        
        this("unknown@empty" ,"name","surname",2,
                "Heyyo! I am using HaloTalk",
                "127.0.0.1");
    }
    
    public ObservableUser(String hostName, String name, String surname, int status,
            String StatusMessage, String ipAddress){
        
        super(hostName, name, surname, status, StatusMessage, ipAddress);
        
        this.nameProperty = new SimpleStringProperty(this.name);
        this.surnameProperty = new SimpleStringProperty(this.surname);
        this.hostnameProperty = new SimpleStringProperty(this.hostName);
        this.statusProperty = new SimpleStringProperty(String.valueOf(this.status));
        this.statusMessageProperty = new SimpleStringProperty(this.statusMessage);
        this.ipAddressProperty = new SimpleStringProperty(this.ipAddress);
        
        this.props=new Properties();
        initProperties();
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
    
    private final void initProperties(){
            
        props.put("NAME", this.nameProperty.get());
        props.put("SURNAME", this.surnameProperty.get());
        props.put("STATUS", this.statusProperty.get() );
        props.put("STATUS_MESSAGE", this.statusMessageProperty.get());
        props.put("IMAGE", "/images/icons/person.png");
        //props.put("HOSTNAME", this.getHostName());
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
    public void setContents(userObject userData) {
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
    
    public static void savePropertiesToFile(Properties props
            ,String PackageRelativePath,String fileName) {
        
        if (props==null) {
            return;
        }
        if (fileName==null) {
            fileName="broadcaster";
        }
        if (PackageRelativePath==null) {
            PackageRelativePath="settings";
        }
        
        try {
                File fosPath=new File( Paths.get(App.class.
                        getResource(PackageRelativePath).toURI()).toString()
                        ,fileName.trim()+".properties" );
                
                BufferedWriter fos=Files.newBufferedWriter(fosPath.toPath(), 
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                props.store(
                fos
                ,"#Auto generated properties for broadcaster by ObservableUser");
                fos.flush();
            } catch (URISyntaxException ex1) {
                App.logger.log(Level.WARNING, "URI parameter"
                    + " for Path is wrong",ex1);
                
            } catch (IOException ex1) {
                App.logger.log(Level.WARNING, "Error when creating "
                    + "BufferedWriter with Files",ex1);
            }
    }
    
    public static ObservableUser readFromProperties(String packageRelativePath){
        Properties props=new Properties();
        ObservableUser user;
        
        try {
            props.load(App.class
                    .getResourceAsStream(packageRelativePath));
            
            user=new ObservableUser( "empty@loadfromstatic",
                    props.getProperty("NAME")
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
