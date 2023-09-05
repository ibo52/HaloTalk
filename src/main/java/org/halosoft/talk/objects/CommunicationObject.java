/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
    
    private BufferedReader localIn;     //to read from keyboard
 
    private Thread receiver;            //get messages from remote end
    private Thread sender;              //send messages to remote end
    
    
    
    public CommunicationObject(){
        //initialize remote socket
        this("127.0.0.1",50001);
    }
    public CommunicationObject(String ipAddr){
        //initialize remote socket
        this(ipAddr,50001);
    }
    public CommunicationObject(String ipAddr, int port){
        //initialize remote socket
        this.remoteIp=ipAddr;
        this.remotePort=port;
        
        initSocket();
        initThreads();

        localIn = new BufferedReader(
        new InputStreamReader(System.in));
    }
    public void initSocket(){
    }
    
    private void initThreads(){

        receiver=new Thread(new Runnable(){
            @Override
            public void run(){
                receiver();
            }
        });
        
        sender= new Thread( new Runnable(){
            @Override
            public void run(){
                sender();
            }
        });
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
    public DataInputStream getSocketInputStream(){
        return this.socketIn;
    }
    public void setSocketOutputStream(DataOutputStream out){
        this.socketOut=out;
    }
    public DataOutputStream getSocketOutputStream(){
        return this.socketOut;
    }
    public Thread getSenderThread(){
        return sender;
    }
    public Thread getReceiverThread(){
        return receiver;
    }
    //-----------------
    public void receiver(){
        
        while ( !receiver.isInterrupted() ){
            
            try {
                String message=socketIn.readUTF();
                System.out.printf("<%s:%d> :%s\n",client.getInetAddress().getHostAddress(), client.getPort(),message );
            
            } catch( EOFException ex ){
                System.err.println("receiver EOFException:"
                        + "\tPossiby remote end closed the connection."
                        + " Stop will be invoked");
                this.stop();
                break;
                
            }catch (IOException ex) {
                System.out.println("receiver:"+ex.getMessage());
                
            }
            
        }
    }
    public void sender(){
        while ( !sender.isInterrupted() ){
            
            try {
                
                while( !localIn.ready() ){//wait for if input buffer is ready
                    Thread.sleep(200);
                    
                    if( client.isClosed() ){
                        break;
                    }
                }
                String message=localIn.readLine();
                
                socketOut.writeUTF(message);
                //System.out.println("<you>:"+message);
            
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage()
                +":\tSender thread");
                break;
                
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void start(){
        this.receiver.setDaemon(true);
        this.sender.setDaemon(true);
        
        this.receiver.start();
        this.sender.start();
    }
    
    public void stop(){
        
        this.receiver.interrupt();
        this.sender.interrupt();
        
        try {
            this.client.close();
            
            socketOut.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (NullPointerException ex) {
            System.err.println("Socket.close:Socket is already 'NULL'");
        }

        Thread.currentThread().interrupt();
    }
    
    public void join(){
        try {
            //waits for the threads of this class to done or interrupt
            this.sender.join();
            this.receiver.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
