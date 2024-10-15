/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.halosoft.gui.models.net.utils.STP;
import org.halosoft.gui.models.net.utils.STP.DataType;
import org.json.JSONObject;

/**
 *
 * @author ibrahim
 * 
 * uses STP class for default datatype to send and receive which determined as json 
 */
public class BroadcastClient {

    private static final int DEFAULT_BUFFER_SIZE=65536;
    private static final int DEFAULT_REMOTE_PORT=50002;

    protected final DatagramSocket client;  //socket to communicate
    protected final DatagramPacket packetOUT;  //packet to send to destination ip and port
    protected final DatagramPacket packetIN;  //packet to read from destination ip and port

    private final String ip;              //ip of this client
    private final int port;               //remote port of server
    private final InetAddress address;
    
    private byte[] buffer;
    private int bufferLength;
    
    /**
     * initializes a DatagramSocket to connect local service
     * generally used for testing purposes
     */
    public BroadcastClient(){
        this("localhost");
    }

    public BroadcastClient(String remoteIpAddress){
        this(remoteIpAddress, DEFAULT_REMOTE_PORT);
    }
    
    /**
     * initializes a DatagramSocket to connect remote service
     * @param remoteIpAddress specific port for default remote ip
     */
    public BroadcastClient(String remoteIpAddress, int port){
        ip=remoteIpAddress;
        this.port=port;
        buffer=new byte[1024];

        //to initialize final variables of class
        DatagramPacket tempOUT=null;
        DatagramPacket tempIN=null;
        InetAddress tempAddress=null;
        DatagramSocket tempCli=null;

        try {
            tempAddress=InetAddress.getByName(ip);

            //byte buffer does not neccesary as send method binds a reference parameter everytime
            tempOUT=new DatagramPacket(new byte[0], 0, tempAddress, port);
            tempIN=new DatagramPacket(new byte[DEFAULT_BUFFER_SIZE], DEFAULT_BUFFER_SIZE);            
            
            tempCli=new DatagramSocket();

        } catch (UnknownHostException ex) {
            System.err.println(String.format(
                    "unknown ip parameter for InetAddress: %s ->%s",remoteIpAddress, ex));
        
        }catch (SocketException ex) {
            System.err.println(String.format(
                    "Error while initializing Datagram Socket: %s",ex ));
        }

        packetIN=tempIN;
        packetOUT=tempOUT;
        address=tempAddress;
        client=tempCli;
    }

    public void sendSTP(JSONObject message) throws IOException{

        byte[] data=STP.format(message.toString().getBytes(), DataType.JSON);

        this.send( data );
    }
/* 
    public void send(String message) throws IOException{

        this.send( message.getBytes() );
    }
*/
    public void send(byte[] data) throws IOException {

        packetOUT.setData(data);
        
        this.client.send(packetOUT);

    }

    public byte[] receive(int recvLimit) throws IOException{

        packetIN.setLength(recvLimit);

        this.client.receive(packetIN);

        packetIN.setLength(packetIN.getData().length);

        return Arrays.copyOfRange(packetIN.getData(), 0, recvLimit);
        
    }

    public byte[] receive() throws IOException{
        
        this.client.receive(packetIN);

        return Arrays.copyOfRange(packetIN.getData(), 0, packetIN.getLength());
        
    }

    public JSONObject receiveSTP() throws IOException{
        
        return STP.deformat( this.receive() );
        
    }

    public void setSoTimeout(int timeoutMillis) throws SocketException{

            this.client.setSoTimeout(timeoutMillis);
    }
    
    public void start(String messageToSend){
        buffer=messageToSend.getBytes();
        this.bufferLength=buffer.length;
        
        this.start();
    }
    /**
     * Automatic process to check for devices on LAN
     * 
     * sends request to service, and fill the buffer by incoming user data.
     * If service is down, then buffer fills with "NO_RESPONSE"
     */
    public void start(){
        
        try {        
            //send signal to notify server
            this.send(new byte[0]);

            client.setSoTimeout(600);//timeout for response
            
            JSONObject response=this.receiveSTP();

            System.out.println("received:"+ response.getJSONObject("data") );
            bufferLength=packetIN.getLength();
            
        }catch (SocketTimeoutException ex) {
            String msg="NO_RESPONSE";

            this.buffer=msg.getBytes();
            this.bufferLength=msg.length();
        
        } catch (IOException ex) {
            System.err.println(
                        "Error while managing broadcast server: "+ex);
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
        c.start("test message");
    }
    
}
