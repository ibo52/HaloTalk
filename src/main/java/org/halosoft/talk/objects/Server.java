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
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;

/**
 *
 * @author ibrahim
 */
public class Server extends CommunicationObject implements Connectible {
    
    private ServerSocket server;
    private Map<String, Socket> clients;
    
    private Thread startThread;
    
    private RSA rsa;
    private int[] REMOTE_KEY;
    
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
                        
                        int[] CLI_KEY=handshake();
                        REMOTE_KEY=CLI_KEY;
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
    
    
    public int[] handshake(){
        ByteBuffer outgoingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        outgoingPublicKey.asLongBuffer().put( this.rsa.getPublicKey() );
        
        ByteBuffer incomingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        try {
            this.getSocketOutputStream().write( outgoingPublicKey.array() );
            
            int recv=this.getSocketInputStream().read(incomingPublicKey.array());
            System.out.println("public key reached:"+incomingPublicKey.getInt()+","
            +incomingPublicKey.getInt());
            
        } catch (IOException ex) {
            System.out.println("Handshake Failed:"+ex.getMessage());
            Platform.exit();
        }
        return incomingPublicKey.asIntBuffer().array();
    }
    
    @Override
    public void stop(){
        super.stopCommunicationThreads();
        System.out.println("server stop invoke");
        try {
            this.server.close();
            this.startThread.interrupt();

        } catch (IOException ex) {
            System.err.println("Server stop:"+ex.getMessage());
        }
    }
    
    public void join(){
        super.joinCommunicationThreads();
        
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
