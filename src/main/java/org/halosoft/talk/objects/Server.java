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
import java.net.SocketException;

/**
 *
 * @author ibrahim
 */
public class Server extends CommunicationObject {
    
    private ServerSocket server;
    
    private Thread startThread;
    
    public Server(){
        
        super();
        
    }
    public Server(String ipAddr, int port){
        super(ipAddr, port);
    }
    
    @Override
    public void initSocket(){
        
        try {
            server=new ServerSocket(50001, 1, InetAddress.getByName("127.0.0.1") );
       
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public ServerSocket getServerSocket(){
        return this.server;
    }
    public void setServerSocket(ServerSocket server){
        this.server=server;
    }
    
    public Thread getStarterThread(){
        return this.startThread;
    }
    
    @Override
    public void start(){
        if ( startThread==null || !startThread.isAlive() ) {
            
            startThread=new Thread(new Runnable(){
            @Override
            public void run(){
                
                while( !startThread.isInterrupted() ){
                    
                    try {
                        var client=server.accept();
                        setClientSocket(client);

                        setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
                        setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );

                        System.out.printf("%s connected\n",client.getInetAddress().getHostAddress());

                        setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
                        setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );

                         //start sender/receiver threads
                        getReceiverThread().setDaemon(true);
                        getSenderThread().setDaemon(true);

                        getReceiverThread().start();
                        getSenderThread().start();

                        try {
                            getSenderThread().join();
                            getReceiverThread().join();

                        }
                        catch (InterruptedException ex) {
                            System.out.println("interrupted");
                        }

                    } catch (SocketException ex) {
                            System.err.println( ex.getMessage()
                            +":\tPossibly socket is closed. Starter thread will be interrupt");

                            startThread.interrupt();
                            break;

                    }catch (IOException ex) {
                            ex.printStackTrace();
                    }

                }

                }
                });

                startThread.setDaemon(true);

                startThread.start();
            
        } else if ( startThread.isAlive() ) {
            System.err.println("Server.start :server already started.");
            return;
        }
        
    }
    @Override
    public void stop(){
        super.stop();
        
        try {
            this.server.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("Test server obj");
        
        var s=new Server();

        s.start();
        
        try {
            s.getStarterThread().join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
    }
    
}
