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
    
    protected Properties props;

    public ObservableUser() {

        this.nameProperty = new SimpleStringProperty(this.name);
        this.surnameProperty = new SimpleStringProperty(this.surname);
        this.hostnameProperty = new SimpleStringProperty(this.hostName);
        this.statusProperty = new SimpleStringProperty(String.valueOf(this.status));
        this.statusMessageProperty = new SimpleStringProperty(this.statusMessage);
        this.ipAddressProperty = new SimpleStringProperty(this.ipAddress);
        
        this.initProperties();      
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
    }
    
    private final void initProperties(){
        this.props=new Properties();
        try {
            this.props.load(App.class.getResourceAsStream(
                    "settings/broadcaster.properties"));
            
            this.name=props.getProperty("NAME");
            this.surname=props.getProperty("SURNAME");
            this.status=Integer.parseInt(props.getProperty("STATUS"));
            this.statusMessage=props.getProperty("STATUS_MESSAGE");
            
            this.nameProperty.set(name);
            this.surnameProperty.set(surname);
            this.statusProperty.set(String.valueOf(status));
            this.statusMessageProperty.set(statusMessage);
            
        } catch(NullPointerException ex){
            
            props.put("NAME", this.getName());
            props.put("SURNAME", this.getSurName());
            props.put("STATUS", String.valueOf(this.getStatus()) );
            props.put("STATUS_MESSAGE", this.getStatusMessage());
            props.put("IMAGE", "/images/icons/person.png");
            //props.put("HOSTNAME", this.getHostName());
            
            this.savePropertiesToFile();
            
        }catch (IOException ex) {
            App.logger.log(Level.WARNING, "Error intializing"
                    + " properties file",ex);
        }
    }

    @Override
    public void setID(String ip) {
        super.setID(ip);
        this.ipAddressProperty.set(ip);
    }
    
    @Override
    public void setName(String name) {
        super.setName(name);
        this.nameProperty.set(name);
        
        props.setProperty("NAME", name);
        this.savePropertiesToFile();
    }

    @Override
    public void setSurname(String surname) {
        super.setSurname(surname);
        this.surnameProperty.set(surname);

        props.setProperty("SURNAME", surname);
        this.savePropertiesToFile();

    }

    @Override
    public void setHostName(String name) {
        super.setHostName(name);
        this.hostnameProperty.set(name);
    }

    @Override
    public void setStatus(int status) {
        super.setStatus(status);
        this.statusProperty.set(String.valueOf(status));

        props.setProperty("STATUS", String.valueOf(status) );
        this.savePropertiesToFile();
    }

    @Override
    public void setStatusMessage(String status) {
        super.setStatusMessage(status);
        this.statusMessageProperty.set(status);

        props.setProperty("STATUS_MESSAGE",status);
        this.savePropertiesToFile();
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
        super.setContents(userData); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        
        this.nameProperty.set(name);
        this.surnameProperty.set(surname);
        this.hostnameProperty.set(hostName);
        this.ipAddressProperty.set(ipAddress);
        this.statusMessageProperty.set(statusMessage);
        this.statusProperty.set(String.valueOf(status));
        
        props.setProperty("NAME", this.getName());
        props.setProperty("SURNAME", this.getSurName());
        props.setProperty("STATUS", String.valueOf(this.getStatus()) );
        props.setProperty("STATUS_MESSAGE", this.getStatusMessage());
        
        this.savePropertiesToFile();
    }
    
    @Override
    public void setContents(String hostName, String name, String surname, int status, String StatusMessage, String ipAddress, Image img) {
        super.setContents(hostName, name, surname, status, StatusMessage, ipAddress, img); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        
        this.nameProperty.set(name);
        this.surnameProperty.set(surname);
        this.hostnameProperty.set(hostName);
        this.ipAddressProperty.set(ipAddress);
        this.statusMessageProperty.set(StatusMessage);
        this.statusProperty.set(String.valueOf(status));
        
        props.setProperty("NAME", this.getName());
        props.setProperty("SURNAME", this.getSurName());
        props.setProperty("STATUS", String.valueOf(this.getStatus()) );
        props.setProperty("STATUS_MESSAGE", this.getStatusMessage());
        
        this.savePropertiesToFile();
        
    }

    private final void savePropertiesToFile() {
        
        if (this.props==null) {
            return;
        }
        try {
                File fosPath=new File( Paths.get(App.class.
                        getResource("settings").toURI()).toString()
                        ,"broadcaster.properties" );
                
                BufferedWriter fos=Files.newBufferedWriter(fosPath.toPath(), 
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                this.props.store(
                fos
                ,"#Auto generated properties for broadcaster by ObservableUser");
                fos.flush();
            } catch (URISyntaxException ex1) {
                App.logger.log(Level.WARNING, "URI parameter"
                    + " for path is wrong",ex1);
            } catch (IOException ex1) {
                App.logger.log(Level.WARNING, "Error when creating "
                    + "BufferedWriter with Files",ex1);
            }
    }
}
