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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ibrahim
 */
public class Server extends CommunicationObject {
    
    private ServerSocket server;
    private Map<String, Socket> clients;
    
    private Thread startThread;
    
    public Server(){
        
        super();
        
    }
    public Server(String ipAddr){
        super(ipAddr);
    }
    public Server(String ipAddr, int port){
        super(ipAddr, port);
    }
    
    @Override
    public void initSocket(){
        clients=new HashMap<>();
        
        try {
            server=new ServerSocket(this.getRemotePort(), 1, InetAddress.getByName(this.getRemoteIp() ) );
       
        } catch (IOException ex) {
            System.out.println("ServerSocket init:"+ex.getMessage());
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
                        clients.put(client.getInetAddress().getHostAddress(), client);
                         
                        setClientSocket(client);

                        setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
                        setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );

                        System.out.printf("%s connected\n",client.getInetAddress().getHostAddress());

                        //start sender/receiver threads
                        /*
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
                        */

                    } catch (SocketException ex) {
                            System.err.println( ex.getMessage()
                            +":\tPossibly some remote socket is closed.");
                            
                            //startThread.interrupt();
                            //break;

                    }catch (IOException ex) {
                            System.err.println("Server listen:"+ex.getMessage());
                            startThread.interrupt();
                            stop();//server stop
                            
                    }

                }

            }
        });

        startThread.setDaemon(true);
        
        startThread.start();
            
        } else if ( startThread.isAlive() ) {
            System.err.println("Server start :server already started @"
                    +this.getRemoteIp()+":"+this.getRemotePort());
        }
        
    }
    @Override
    public void stop(){
        super.stop();
        System.out.println("server stop invoke");
        try {
            this.server.close();
            this.startThread.interrupt();

        } catch (IOException ex) {
            System.err.println("Server stop:"+ex.getMessage());
        }
    }
    @Override
    public void join(){
        super.join();
        
        try {
            this.startThread.join();
        } catch (InterruptedException ex) {
            System.err.println("Server join threads:"+ex.getMessage());
        }
    }
    public static void main(String[] args) {
        System.out.println("server test");
        Server s=new Server("192.168.1.66",50001);
        s.start();
        s.join();
        
        System.out.println("server done");
    }
}
