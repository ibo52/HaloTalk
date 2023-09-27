/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author ibrahim
 * Manager for network interfaces on machine.
 * Provides list of those parsed interfaces according
 * to wanted connection type(Wireless, wired, loopback) of network.
 */
public class NetworkDeviceManager {
    
    public static enum ConnectionType{WIRELESS, ETHERNET, LOOPBACK};
    
    private final HashMap<String, ArrayList<NetworkInterface> > devices;
    
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
            
            //if no wireless device, replace arraylist with loopbacks one
            if (  this.devices.get(this.ConnectionTypeKeyWords[0]).isEmpty()  ) {
                this.devices.replace(this.ConnectionTypeKeyWords[0]
                        ,this.devices.get(this.ConnectionTypeKeyWords[2]));
            }
            //if no ethernet device, replace arraylist with loopbacks one
            if (  this.devices.get(this.ConnectionTypeKeyWords[1]).isEmpty()  ) {
                this.devices.replace(this.ConnectionTypeKeyWords[1]
                        ,this.devices.get(this.ConnectionTypeKeyWords[2]));
            }
            
        } catch (SocketException ex) {
            System.out.println(this.getClass().getName()+":"+ex.getMessage());
        }
    }
    
    /**
     * Get network interfaces on machine of specified type
     * @param connectionType specific type of connection that interface operates
     * @return desired type of devices list if machine connected to internet by
     * these devices, or else returns ArrayList of size 1 which contains loopback
     */
    public ArrayList<NetworkInterface> getInterfaceDevices(ConnectionType connectionType){

        switch(connectionType){
            
            case WIRELESS:
                return this.devices.get(this.ConnectionTypeKeyWords[0]);
                
            case ETHERNET:
                return this.devices.get(this.ConnectionTypeKeyWords[1]);
                
            case LOOPBACK:
                return this.devices.get(this.ConnectionTypeKeyWords[2]);
            
            default:
                //return wifi if possible, or else it will be loopback automatically
                return this.devices.get(this.ConnectionTypeKeyWords[0]);
        }
    }
    
    /**
     * Calculates network identity by address of given network interface.
     * @param ni Desired type of interface device
     * @return network identity if machine is connected to internet, 
     * or returns loopback identity(127.0.0.0 generally)
     */
    public static String calculateNetworkIdentity(NetworkInterface ni){
       /*Returns network identity of sub network*/
        InetAddress local=null;
        try {
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
        }
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
    
    /**
     * checks for connectivity by looking network interfaces list.
     * If there is 1 interface in list, which is loopback, that means you
     * simply do not have an interface that connected to internet.
     * @return true if there is internet connection, else false
     */
    public static boolean checkForConnectivity(){

        try {

            ArrayList<NetworkInterface> interfaces = Collections.list(
                    NetworkInterface.getNetworkInterfaces());
            
            if ( interfaces.size()>1 ) {
                return true;
            }
            return false;
            
        } catch (SocketException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
