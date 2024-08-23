/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.halosoft.gui.App;

/**
 *
 * @author ibrahim
 */
public class BroadcastClient {
    
    private DatagramSocket client;  //socket to communicate
    private String ip;              //ip of this client
    private int port;               //remote port of server
    private InetAddress address;
    
    private byte[] buffer;
    private int bufferLength;
    
    /**
     * initializes a DatagramSocket to connect local service
     * generally used for testing purposes
     */
    public BroadcastClient(){
        this("localhost");
    }
    
    /**
     * initializes a DatagramSocket to connect remote service
     * @param remoteIpAddress specific port for default remote ip
     */
    public BroadcastClient(String remoteIpAddress, int port){
        ip=remoteIpAddress;
        this.port=port;
        
        try {
            address=InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            App.logger.log(Level.SEVERE, 
                        "unknown ip parameter for InetAddress",ex);
        }
        
        try {
            this.client=new DatagramSocket();
        
        }catch (SocketException ex) {
                App.logger.log(Level.SEVERE, 
                        "Error while initializing Datagram Socket",ex);
        }
        
        buffer = new byte[1024];
    }
    
    public BroadcastClient(String remoteIpAddress){
        this(remoteIpAddress, 50002);
    }
    
    
    public void start(String messageToSend){
        buffer=messageToSend.getBytes();
        this.bufferLength=buffer.length;
        
        this.start();
    }
    /**
     * sends request to service, and fill the buffer by incoming user data.
     * If service is down, then buffer fills with "NO_RESPONSE"
     */
    public void start(){
        
        try {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
        
            //send signal to notify server
            client.send(request);

            client.setSoTimeout(600);//timeout for response
            
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            client.receive(response);

            //System.out.println("received:"+new String(buffer, 0, response.getLength()));
            bufferLength=response.getLength();
            
        }catch (SocketTimeoutException ex) {
            String msg="NO_RESPONSE";

            this.buffer=msg.getBytes();
            this.bufferLength=msg.length();
        
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while managing broadcast server",ex);
        }

    }
    
    public byte[] getBuffer(){
        return buffer;
    }
    
    public int getBufferLength(){
        return this.bufferLength;
    }
    
    public static void main(String[] args) {
        var c=new BroadcastClient();
        c.start();
    }
    
}
