/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.halosoft.talk.App;
import org.halosoft.talk.interfaces.Connectible;

/**
 *
 * @author ibrahim
 */
public class SocketHandlerAdapter implements Connectible {
    
    protected Socket client;              //for connected client
    
    protected String remoteIp;            //ip of remote end
    protected int remotePort;             //port to enter to remote end
    
    protected DataInputStream socketIn;   //incoming messages from remote
    protected DataOutputStream socketOut; //outgoing messages to remote
    
    protected BufferedReader localIn;     //to read from keyboard
    protected ExecutorService executorService;//for receiver and sender runablesS
    
    public SocketHandlerAdapter(String ipAddr, int port){
        this.remoteIp=ipAddr;
        this.remotePort=port;
        
        localIn = new BufferedReader(
        new InputStreamReader(System.in));
        
        executorService=Executors.newCachedThreadPool();

        this.initialize();
    }
    
    /**
     * method to initialize required components of subclass.
     * will be called on constructor of this adapter class, which means
     * this method "will be executed 'before' subclass constructor"
     */
    @Override
    public void initialize() {}

    @Override
    public void start() {}

    @Override
    public void stop() {
        try {
            this.client.close();
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while closingv client socket",ex);
        }
    }

    @Override
    public String getRemoteIp(){
        return this.remoteIp;
    }
    
    @Override
    public void setRemoteIp(String ip){
        this.remoteIp=ip;
    }
    
    @Override
    public int getRemotePort(){
        return this.remotePort;
    }
    
    @Override
    public void setRemotePort(int port){
        this.remotePort=port;
    }
    
    @Override
    public Socket getClientSocket(){
        return this.client;
    }
    
    @Override
    public void setClientSocket(Socket cli){
        this.client=cli;
    }
    
    
    @Override
    public void setSocketInputStream(DataInputStream in){
        this.socketIn=in;
    }
    
    @Override
    public DataInputStream getSocketInputStream(){
        return this.socketIn;
    }
    
    @Override
    public void setSocketOutputStream(DataOutputStream out){
        this.socketOut=out;
    }
    
    @Override
    public DataOutputStream getSocketOutputStream(){
        return this.socketOut;
    }
    
}
