/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.halosoft.gui.App;

/**
 *
 * @author ibrahim
 */
public class VideoClient {
    
    private DatagramSocket server;
    private final int port;
    
    private ExecutorService executorService;
    
    /**
     * initializes DatagramSocket as a broadcast server.
     * @param ipAddr ip address to bind broadcast server
     */
    public VideoClient(String ipAddr){
        this.port=50005;
        
        executorService=Executors.newSingleThreadExecutor();

        try {
            this.server=new DatagramSocket();
            //this.server.connect(new InetSocketAddress(ipAddr, port) );

        } catch (SocketException ex) {
            
            App.logger.log(Level.SEVERE, 
                    "Could not initialize socket",ex);
        }        
    }
    
    public VideoClient(){

        this( "0.0.0.0" );
    }
    
    /**
     * starts a thread in server to listen incoming requests.
     * Returns user data according to request type
     */
    public void start(){
        
        executorService.execute( () -> {
            
            while ( !Thread.currentThread().isInterrupted() ){
                try {
                    byte[] buffer=new byte[256*1024];
                    System.out.println("started video cli");
                    //notify remote end for video request
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    server.send(new DatagramPacket(new byte[]{65,66, 67},3, InetAddress.getByName("0.0.0.0"),this.port) );//abc
                   
                    System.out.println("ser");
                    //wait for viode data from remote end
                    server.receive(request);
                    
                    
                    InetAddress remoteCli=request.getAddress();
                    int remotePort=request.getPort();

                    System.out.println("received from:"+remoteCli.getHostAddress()+":"+remotePort);
                    System.out.print("data:");
                    
                    for (int i = 0; i < 10; i++) {
                        System.out.print( String.format("%08X",buffer[i]) );
                    }
                    
                    
                } catch (IOException ex) {
                    App.logger.log(Level.SEVERE,"Video stream could not"
                            + " received",ex);
                    System.exit(ex.hashCode());
                }
                
            }
        });
        
        executorService.shutdown();
    }
    
    /**
     * if invoked, main thread will be blocked until listener interrupted
     */
    public void join(){
        try {
            while( !executorService.isTerminated() ){
                Thread.sleep(300);
            }
        } catch (InterruptedException ex) {
            App.logger.log(Level.FINEST,"join Interrupted",ex);
        }
    }
    
    /**
     * closes the broadcast server
     */
    public void stop(){
        executorService.shutdownNow();
        this.server.close();
        
        System.out.println("video client stopped");
    }
    
    public static void main(String[] args) {
        VideoClient c=new VideoClient();
        
        c.start();
        
        c.join();
        
    }
    
}
