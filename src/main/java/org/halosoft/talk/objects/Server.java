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
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javafx.application.Platform;

/**
 *
 * @author ibrahim
 */
public class Server extends SocketHandlerAdapter {
    
    private ServerSocket server;
    
    private RSA rsa;
    private long[] REMOTE_KEY;
    
    public Server(String ipAddr, int port){
        
        super(ipAddr, port);

        this.rsa=new RSA();
    }
    
    public Server(String ipAddr){
        this(ipAddr,50001);

    }
    
    public Server(){
        this("0.0.0.0");
    }
    
    /**
     * this method calls automatically from super class constructor
     */
    @Override
    public void initialize(){
        
        executorService=Executors.newCachedThreadPool();
        int MAX_ALLOWED_CLI=64;
        
        try {
            server=new ServerSocket(this.getRemotePort(), MAX_ALLOWED_CLI, 
                    InetAddress.getByName(this.getRemoteIp() ) );
       
        } catch (IOException ex) {
            System.out.println("ServerSocket init:"+ex.getMessage());
        }
    }
    
    public ServerSocket getServerSocket(){
        return this.server;
    }
    
    @Override
    public void start(){
        
        if ( ((ThreadPoolExecutor)executorService).getActiveCount()<1 ) {
            
            executorService.execute( () -> {
                
                while ( !Thread.currentThread().isInterrupted() ) {
                    
                    try {
                        
                        Socket client1 = server.accept();
                        
                        /*System.out.printf("%s:%d connected\n",
                                client1.getInetAddress().getHostAddress(),
                                client1.getPort());*/
                        setSocketInputStream(new DataInputStream(new BufferedInputStream(
                        client1.getInputStream())));
                
                        setSocketOutputStream(new DataOutputStream(
                        client1.getOutputStream()));
                        
                        long[] CLI_KEY=handshake();
                        REMOTE_KEY=CLI_KEY;
                        
                        ServerHandler serverHandler=new ServerHandler
                                                  (client1,CLI_KEY);
                        
                        this.executorService.execute(serverHandler);
                        
                    }catch (SocketException ex) {
                        System.err.println( ex.getMessage()
                                +":\tPossibly some remote socket is closed.");

                    }catch (IOException ex) {
                        System.err.println("Server listen:"+ex.getMessage());
                            
                    }finally{
                        //this.stop();//server stop
                    }
                }
            });
            
        } else {
            System.err.println("Server start :server already started @"
                    +this.getRemoteIp()+":"+this.getRemotePort());
        }
        
    }
    
    
    private long[] handshake(){
        
        ByteBuffer outgoingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        outgoingPublicKey.asLongBuffer().put( this.rsa.getPublicKey() );
        
        ByteBuffer incomingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        
        try {
            this.getSocketOutputStream().write( outgoingPublicKey.array() );
            
            int recv=this.getSocketInputStream().read(incomingPublicKey.array());
            
        } catch (IOException ex) {
            System.err.println(this.getClass().getName()+"->Handshake Failed:"+ex.getMessage());
            Platform.exit();
        }
        
        long[] remoteKey=new long[2];
        incomingPublicKey.asLongBuffer().get(remoteKey);
        return remoteKey;
    }
    
    @Override
    public void stop(){
        
        this.executorService.shutdownNow();
        System.out.println("server stop invoke");
        try {
            this.server.close();
            this.executorService.shutdownNow();

        } catch (IOException ex) {
            System.err.println("Server stop:"+ex.getMessage());
        }
    }
}
