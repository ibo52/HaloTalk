/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import org.halosoft.talk.interfaces.Connectible;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javafx.application.Platform;

/**
 *
 * @author ibrahim
 */
public class Server extends CommunicationObject implements Connectible {
    
    private ServerSocket server;
    private Map<String, Socket> clients;
    
    private ExecutorService executorService;
    
    private RSA rsa;
    private long[] REMOTE_KEY;
    
    public Server(){
        super();
        this.initialize();
        this.rsa=new RSA();
    }
    public Server(String ipAddr){
        super(ipAddr);
        this.initialize();
        this.rsa=new RSA();
    }
    public Server(String ipAddr, int port){
        
        super(ipAddr, port);
        this.initialize();
        this.rsa=new RSA();
    }
    
    @Override
    public void initialize(){
        clients=new HashMap<>();
        executorService=Executors.newCachedThreadPool();
        
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
    
    @Override
    public void start(){
        
        if ( ((ThreadPoolExecutor)executorService).getActiveCount()<1 ) {
            
            executorService.execute( () -> {
                
                while ( !Thread.currentThread().isInterrupted() ) {
                    
                    try {
                        
                        java.net.Socket client1 = server.accept();
                        
                        clients.put(client1.getInetAddress().getHostAddress(), client1);
                        
                        setClientSocket(client1);
                        
                        setSocketInputStream(new DataInputStream(new BufferedInputStream(client1.getInputStream())));
                        
                        setSocketOutputStream(new DataOutputStream(client1.getOutputStream()));
                        //System.out.printf("%s connected\n",client.getInetAddress().getHostAddress());
                        
                        long[] CLI_KEY=handshake();
                        REMOTE_KEY=CLI_KEY;
                        
                    }catch (SocketException ex) {
                        System.err.println( ex.getMessage()
                                +":\tPossibly some remote socket is closed.");

                    }catch (IOException ex) {
                        System.err.println("Server listen:"+ex.getMessage());
                        stop();//server stop
                            
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
        super.stopCommunicationThreads();
        this.executorService.shutdownNow();
        System.out.println("server stop invoke");
        try {
            this.server.close();
            this.executorService.shutdownNow();

        } catch (IOException ex) {
            System.err.println("Server stop:"+ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        System.out.println("server test");
        Server s=new Server("0.0.0.0",50001);
        s.start();
        
        System.out.println("server done");
    }
}
