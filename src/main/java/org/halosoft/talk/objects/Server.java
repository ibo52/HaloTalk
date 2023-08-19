/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ibrahim
 */
public class Server extends CommunicationObject {
    
    private ServerSocket server;
    
    private Socket[] clients;
    
    public Server(){
        
        super();
        
    }
    public Server(String ipAddr, int port){
        super(ipAddr, port);
    }
    
    @Override
    public void initSocket(){
        clients=new Socket[1];
        
        try {
            server=new ServerSocket(50001, clients.length, InetAddress.getByName("127.0.0.1") );
       
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void start(){
        
        while(true){
                
            try {
                var client=server.accept();
                this.setClientSocket(client);
                
                setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
                setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
                
                System.out.printf("%s connected\n",client.getInetAddress().getHostAddress());
            
                setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
                setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
            
                
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
        } catch (IOException ex) {
                ex.printStackTrace();
        }

        }
        
    }
    public static void main(String[] args) {
        System.out.println("test server obj");
        
        var s=new Server();
        s.start();
    }
    
}
