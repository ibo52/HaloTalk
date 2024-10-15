/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.halosoft.gui.interfaces.UDPSocket;

import org.json.JSONObject;

/**
 *
 * @author ibrahim
 * 
 * uses STP class for default datatype to send and receive which determined as json 
 */
public class BroadcastClient extends UDPSocket{
    
    /**
     * initializes a DatagramSocket to connect local service
     * generally used for testing purposes
     */
    public BroadcastClient(){
        this("localhost");
    }

    public BroadcastClient(String remoteIpAddress){
        this(remoteIpAddress, UDPSocket.DEFAULT_SERVER_PORT);
    }
    
    /**
     * initializes a DatagramSocket to connect remote service
     * @param remoteIpAddress specific port for default remote ip
     */
    public BroadcastClient(String remoteIpAddress, int port){

        super(remoteIpAddress, port, false);
    }
/*
    public void sendSTP(JSONObject message) throws IOException{

        byte[] data=STP.format(message.toString().getBytes(), DataType.JSON);

        this.send( data );
    }

    public JSONObject receiveSTP() throws IOException{
        
        return STP.deformat( this.receive() );
        
    }
    */
    public JSONObject start(String data){
        
        this.packetOUT.setData( data.getBytes() );
        
        return this.start();
    }

    public JSONObject start(JSONObject data){
        
        this.packetOUT.setData( data.toString().getBytes() );
        
        return this.start();
    }
    /**
     * Automatic process to check for devices on LAN
     * 
     * sends request to service, and fill the buffer by incoming user data.
     * If service is down, then buffer fills with "NO_RESPONSE"
     */
    public JSONObject start(){

        JSONObject retval=new JSONObject();
        
        try {        
            //send signal to notify server
            this.send( this.packetOUT.getData() );

            this.sock.setSoTimeout(600);//timeout for response
            
            retval=new JSONObject( new String( this.receive() ) );

            System.out.println("received:"+ retval );
            
        }catch (SocketTimeoutException ex) {
            //err: add timeout info to JSONObject or interpret empty json as no response
                    
        } catch (IOException ex) {
            System.err.println(
                        "Error while managing broadcast server: "+ex);
        }

        return retval;
    }
    
    public static void main(String[] args) {

        var c=new BroadcastClient();
        c.start();
    }
    
}
