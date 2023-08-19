/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author ibrahim
 */
public class CommunicationObject {
    private Socket client;              //for connected client
    
    private String remoteIp;            //ip of remote end
    private int remotePort;             //port to enter to remote end
    
    private DataInputStream socketIn;   //incoming messages from remote
    private DataOutputStream socketOut; //outgoing messages to remote
    
    private Scanner localIn;            //to read from keyboard
 
    public CommunicationObject(){
        //initialize remote socket
        this.remoteIp="127.0.0.1";
        this.remotePort=50001;
        
        initSocket();
        
        localIn=new Scanner(System.in);
    }
    public CommunicationObject(String ipAddr, int port){
        //initialize remote socket
        this.remoteIp=ipAddr;
        this.remotePort=port;
        
        initSocket();
        
        localIn=new Scanner(System.in);
    }
    public void initSocket(){
        System.out.println("CommunicationObject init socket..");
    }
    
    public String getRemoteIp(){
        return this.remoteIp;
    }
    public void setRemoteIp(String ip){
        this.remoteIp=ip;
    }
    
    public int getRemotePort(){
        return this.remotePort;
    }
    public void setRemotePort(int port){
        this.remotePort=port;
    }
    
    public Socket getClientSocket(){
        return this.client;
    }
    public void setClientSocket(Socket cli){
        this.client=cli;
    }
    
    public void setSocketInputStream(DataInputStream in){
        this.socketIn=in;
    }
    public void setSocketOutputStream(DataOutputStream out){
        this.socketOut=out;
    }
    //-----------------
    public void receiver(){
        
        while ( true ){
            
            try {

                System.out.printf("<%s:%d> :%s\n",client.getInetAddress().getHostAddress(), client.getPort(),socketIn.readUTF() );
            
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            
        }
    }
    public void sender(){
        while (true){
            
            try {
                String message=localIn.nextLine();
                
                socketOut.writeUTF(message);
                System.out.println("<you>:"+message);
            
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void start(){
        
    }
}
