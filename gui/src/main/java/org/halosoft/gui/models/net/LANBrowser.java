package org.halosoft.gui.models.net;

import org.halosoft.gui.models.ObservableUser;
import org.json.JSONObject;

import javafx.concurrent.Task;
 
/**
 * browse through the subnet of LAN for users of this program
 */ 
public class LANBrowser extends Task<ObservableUser>{

    private final String ipAddr;

    public LANBrowser(String ip){
        ipAddr=ip;
    }

    public String getArgument(){
        return ipAddr;
    }

    @Override
    protected ObservableUser call() throws Exception {
        
                            //check if host has this application
                            BroadcastClient LANdiscover =new BroadcastClient(ipAddr);
                            
                            //parse incoming user data
                            JSONObject response=LANdiscover.start();
                            
                            //check if remote did respond
                            if ( !response.isEmpty() ) {
                                    
                                return new ObservableUser(
                                    response.optString("HNAME"),
                                    response.optString("NAME"),
                                    response.optString("SURNAME"),
                                    response.optInt("STAT"),
                                    response.optString("CSTAT"),
                                        ipAddr);


                            }else {return null;}
    } 
}
    