/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author ibrahim
 */
public class NetworkDeviceManager {
    
    public static enum ConnectionType{WIRELESS, ETHERNET, LOOPBACK};
    
    private HashMap<String, ArrayList<NetworkInterface> > devices;
    
    private final static String osName=System.getProperty("os.name").toLowerCase();
    
    private String[] ConnectionTypeKeyWords;
    
    public NetworkDeviceManager(){
        
        if ( osName.contains("windows") ) {
            ConnectionTypeKeyWords=new String[]{"wlan", "eth", "lo"};
            
        }else if ( osName.contains("linux") ){
            ConnectionTypeKeyWords=new String[]{"wl", "en", "lo"};
        }
        
        devices = new HashMap();
        devices.put(ConnectionTypeKeyWords[0], new ArrayList());
        devices.put(ConnectionTypeKeyWords[1], new ArrayList());
        devices.put(ConnectionTypeKeyWords[2], new ArrayList());
        
        this.parseInterfaces();
    }
    
    private void parseInterfaces(){

        try {

            ArrayList<NetworkInterface> interfaces = Collections.list(
                    NetworkInterface.getNetworkInterfaces());
            
            for (NetworkInterface i: interfaces) {
                
                for (String type: ConnectionTypeKeyWords) {
                    
                    if (i.getName().contains(type)) {
                        
                        this.devices.get(type).add(i);
                        continue;
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println(this.getClass().getName()+":"+ex.getMessage());
        }
    }
    
    public ArrayList<NetworkInterface> getInterfaceDevices(ConnectionType connectionType){

        switch(connectionType){
            
            case WIRELESS:
                return this.devices.get(this.ConnectionTypeKeyWords[0]);
                
            case ETHERNET:
                return this.devices.get(this.ConnectionTypeKeyWords[1]);
                
            case LOOPBACK:
                return this.devices.get(this.ConnectionTypeKeyWords[2]);
            
            default:
                return null;
        }
    }

    public static String calculateNetworkIdentity(NetworkInterface ni){
       /*Returns network identity of sub network*/
        InetAddress local=null;
        int subnetMask=-1;

        for (InterfaceAddress addr:ni.getInterfaceAddresses()){
            if (addr.getNetworkPrefixLength()<=24) {
                
                local=addr.getAddress();
                subnetMask=addr.getNetworkPrefixLength();
                break;
            }
        }

       //logical AND ip with subnet mask to calculate network identity address
       int addressToBitwise=ByteBuffer.wrap(
               local.getAddress() ).getInt() &(-1<< (32-subnetMask) );
       
       byte[] calc=ByteBuffer.allocate(4).putInt(addressToBitwise).array();

       String host= Byte.toUnsignedInt(calc[0])+"."
                   +Byte.toUnsignedInt(calc[1])+"."
                   +Byte.toUnsignedInt(calc[2])+"."
                   +Byte.toUnsignedInt(calc[3]);
       //return calculated identity address
       return host;
    }
}
