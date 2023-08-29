/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
    
    public BroadcastClient(){
        this("localhost");
    }
    
    public BroadcastClient(String remoteIpAddress){
        ip=remoteIpAddress;
        port=50002;
        
        try {
            address=InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        
        try {
            this.client=new DatagramSocket();
        
        }catch (SocketException ex) {
                ex.printStackTrace();
        }
        
        buffer = new byte[1024];
    }
    
    
    public void start(){
        
        try {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
        
            //send signal to notify server
            client.send(request);
            
            client.setSoTimeout(100);//timeout for response
            
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            client.receive(response);
            
            //System.out.println("received:"+new String(buffer, 0, response.getLength()));
            bufferLength=response.getLength();
            
        }catch (SocketTimeoutException ex) {
            String msg="NO_RESPONSE";
            
            this.buffer=msg.getBytes();
            this.bufferLength=msg.length();
        
        } catch (IOException ex) {
            ex.printStackTrace();
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
