/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import org.halosoft.gui.App;

import java.util.concurrent.ExecutorService;

/**
 *
 * @author ibrahim
 * 
 * manages remote client requests and forwards to handler
 */
public class Server {
    
    private static final int MAX_ALLOWED_CLI=16;
        
    private final ServerSocket server;

    private final ExecutorService service=Executors.newCachedThreadPool();
    
    public Server(String ip, int port){
        
        ServerSocket tempSock=null;
        try {

            tempSock=new ServerSocket(port, MAX_ALLOWED_CLI, 
                        InetAddress.getByName( ip ) );

        } catch (IOException e) {
            System.err.println("Server Initialize socket IO: "+e.getMessage() );
        }
        server=tempSock;
    }
    
    public Server(String ip){

        this(ip, TCPSocket.DEFAULT_SERVER_PORT);

    }

    public Server(int port){
        
        this(null, port);

    }
    
    public Server(){
        this("0.0.0.0");
    }
    
    public ServerSocket getServerSocket(){
        return server;
    }
    
    public void start(){
        
        if ( ((ThreadPoolExecutor)service).getActiveCount()<1 ) {
            
            service.execute( () -> {
                
                while ( !Thread.currentThread().isInterrupted() ) {
                    
                    try {
                        
                        Socket client1 = server.accept();
                         
                        /*System.out.printf("%s:%d connected\n",
                                client1.getInetAddress().getHostAddress(),
                                client1.getPort());
                        */
                        //handle the client connections
                        ServerHandler handler=new ServerHandler
                                                  (client1);
                        
                        this.service.execute(handler);
                        
                    }catch (SocketException ex) {
                        App.logger.log(Level.FINER, 
                        "Possibly remote socket closed",ex);

                    }catch (IOException ex) {
                        App.logger.log(Level.SEVERE, 
                        "Error while managing Server",ex);
                            
                    }finally{
                        //this.stop();//server stop
                    }
                }
            });
            
        } else {
            App.logger.log(Level.INFO, 
                        "Server already started @"
                                + server.getInetAddress().getHostAddress()+":"+server.getLocalPort());
        }
        
    }
    
    public void stop(){
        
        this.service.shutdownNow();

        try {
            this.server.close();

        } catch (IOException ex) {
            
        }
    }   
}
