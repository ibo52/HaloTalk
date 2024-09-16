package org.halosoft.gui.models.net;

import org.halosoft.gui.App;
import org.halosoft.gui.controllers.UserInfoBoxController;
import org.halosoft.gui.models.ObservableUser;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.logging.Level;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.concurrent.FutureTask;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
 
/**
 * browse through the subnet of LAN for users of this program
 */ 
public class ELANBrowser extends Task<ObservableUser>{

    private final String ipAddr;

    public ELANBrowser(String ip){
        ipAddr=ip;
    }

    public String getArgument(){
        return ipAddr;
    }

    @Override
    protected ObservableUser call() throws Exception {
        
                            //check if host has this application
                            BroadcastClient LANdiscover =new BroadcastClient(ipAddr);
                            
                            LANdiscover.start();
                            
                            //parse incoming user data
                            String idt=new String(LANdiscover.getBuffer(), 0, LANdiscover.getBufferLength());
                            
                            //check if remote did respond
                            if ( !idt.equals("NO_RESPONSE") ) {
                                
                                JSONObject data=new JSONObject(idt);
    
                                return new ObservableUser(
                                        data.optString("HNAME"),
                                        data.optString("NAME"),
                                        data.optString("SURNAME"),
                                        data.optInt("STAT"),
                                        data.optString("CSTAT"),
                                        ipAddr);


                            }else {return null;}
    } 
}
    