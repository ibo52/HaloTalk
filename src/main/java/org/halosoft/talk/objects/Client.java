/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

/**
 *
 * @author ibrahim
 */
public class Client extends CommunicationObject{
    @Override
    public void initSocket(){
        
        try {

            var client=new Socket(this.getRemoteIp(), this.getRemotePort());

            this.setClientSocket(client);
            
            setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
            setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
        
        }catch (ConnectException ex) {
            System.out.println(ex.getMessage());
            System.exit(ex.hashCode());
            
        
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(ex.hashCode());
        }

    }
    
    public Client(){
        super();
    }
    public Client(String ipAddr, int port){
        super(ipAddr, port);
    }
    @Override
    public void start(){
        
        //open sender/receiver threads
        Thread receiverThread=new Thread(new Runnable(){
            @Override
            public void run(){
                receiver();
            }
        });
        
        Thread senderThread= new Thread( new Runnable(){
            @Override
            public void run(){
                sender();
            }
        });
        
        receiverThread.setDaemon(true);
        senderThread.setDaemon(true);
        
        receiverThread.start();
        senderThread.start();
        
        try {
            senderThread.join();
            receiverThread.join();
            
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("client start");
        
        var cli=new Client();
        
        cli.start();
    }
    
}
