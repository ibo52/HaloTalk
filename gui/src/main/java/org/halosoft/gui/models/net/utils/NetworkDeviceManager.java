/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;

import org.halosoft.gui.App;

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
        
        devices = new HashMap<String, ArrayList<NetworkInterface>>();
        
        for (String connType : ConnectionTypeKeyWords) {
            devices.put(connType, new ArrayList<NetworkInterface>());
        }
        
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
            App.logger.log(Level.SEVERE, 
                        "Error while parsing interfaces by their types",ex);
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

    public static ArrayList<NetworkInterface> getInterfaceDevices(){
        

        try {
            return Collections.list( NetworkInterface.getNetworkInterfaces() );
        
        } catch (SocketException e) {

            return new ArrayList<NetworkInterface>();
        }

    }

    /**
     * Get desired network interface on machine of specified type
     * @return desired device if exists, null otherwise
     */
    public NetworkInterface getInterfaceDevice(String deviceName){

        try {
            for (NetworkInterface iface : Collections.list(
                NetworkInterface.getNetworkInterfaces())) {
                    
                if( iface.getName().equals(deviceName))
                return iface;
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        

        return null;
        
    }
    
    public static String IpV4ToString(byte[] addr){
        StringBuilder s=new StringBuilder();

        for (byte b : addr) {
            s.append(Byte.toUnsignedInt(b)+".");
        }

        return s.substring(0, s.length()-1);// remeove extra '.' at last
    }

    /**
     * Calculate network identity by first ipv4 address found in networkInterface
     * which ip address resides
     * @param address
     * @return
     * @throws SocketException
     */
    public static byte[] calculateNetworkIdentity(Inet4Address address) throws SocketException{
        
        var ni = NetworkInterface.getByInetAddress(address);

        for (InterfaceAddress addr : ni.getInterfaceAddresses()){
                
            if ( addr.getAddress().equals( address ) ) {

                return NetworkDeviceManager.
                calculateNetworkIdentity(address, addr.getNetworkPrefixLength());
            }
        }
        return null;
    }

    /**
     * Calculate default network identity for given subnet prefix
     * @param address ip address to calculate
     * @param subnetPrefixLength subnet mask obtained from networkDeviceManager
     * @return network identity as subnet mask applied to ip address
     */
    public static byte[] calculateNetworkIdentity(Inet4Address address, short subnetPrefixLength){

        if( address.isLoopbackAddress() )
            return new byte[]{-1,0,0,0};//assumed default id for localhost

        byte[] identity = {-1,-1,-1,-1};
        
        //network byte order: the highest order byte of the address is in addr[0].
        byte ip[]=address.getAddress();

        int mask = -1 << (32 - subnetPrefixLength);//all bits set to 1
        
        for (int i = 0; i < identity.length; i++) {
            
            identity[i] = (byte)(ip[i] & ( (mask >>(24 - 8*i)) & 0xFF));
        }

        return identity;
    }
    
    /**
     * Calculates network identity(for IPv4) by address of given network interface.
     * which this computer resides in
     * 
     * @param ni Desired type of interface device
     * @return network identity if machine is connected to internet, 
     * or returns loopback identity(127.0.0.0 generally)
     */
    public static String calculateNetworkIdentity(NetworkInterface ni){
       /*Returns network identity of sub network*/
        try {

            // Skip loopback and down interfaces
            if (ni.isLoopback() || !ni.isUp()) {
                return "255.0.0.0";
            }

            for (InterfaceAddress addr : ni.getInterfaceAddresses()){
                
                if ( addr.getAddress() instanceof Inet4Address ) {
 
                    byte[] id = NetworkDeviceManager.
                    calculateNetworkIdentity( (Inet4Address)addr.getAddress(), addr.getNetworkPrefixLength());
                    
                    return NetworkDeviceManager.IpV4ToString(id);

                }
            }

        }catch (SocketException e) {
            System.err.println(String.format("[%s]: Localhost could not resolved: %s",
            NetworkDeviceManager.class.getName(), e) );
        }

        return null;
    }
    
    /**
     * finds the first occurence of ip address which is instance of Inet4Address
     * @param ni desired network interface to get ip
     * @return ip address string as ipv4, if not found returns ""(empty string)
     */
    public static String getIpV4Address(NetworkInterface ni){
        String addr="";
        
        Enumeration<InetAddress> addresses=ni.getInetAddresses();
        
        while(addresses.hasMoreElements()){
            InetAddress a=addresses.nextElement();
            
            if (a instanceof Inet4Address) {
                addr=a.getHostAddress();
                break;
            }
        }
        return addr;
    }
    
    /**
     * finds the first occurence of ip address which is instance of Inet6Address
     * @param ni desired network interface to get ip
     * @return ip address string as ipv6, if not found returns ""(empty string)
     */
    public static String getIpV6Address(NetworkInterface ni){
        String addr="";
        
        Enumeration<InetAddress> addresses=ni.getInetAddresses();
        
        while(addresses.hasMoreElements()){
            InetAddress a=addresses.nextElement();
            
            if (a instanceof Inet6Address) {
                addr=a.getHostAddress();
                break;
            }
        }
        return addr;
    }
}
