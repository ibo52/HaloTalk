/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import org.halosoft.gui.App;
import org.json.JSONObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 *
 * @author ibrahim
 */
public class Broadcaster extends ObservableUser {
    
    private DatagramSocket server;
    private final int port;
    
    private ExecutorService executorService;
    
    /**
     * initializes DatagramSocket as a broadcast server.
     * @param ipAddr ip address to bind broadcast server
     */
    public Broadcaster(String ipAddr){
        this.port=50002;
        
        executorService=Executors.newSingleThreadExecutor();

        try {
            this.server=new DatagramSocket(null);
            this.server.bind(new InetSocketAddress(ipAddr, port) );
            this.server.setBroadcast(true);

        } catch (SocketException ex) {
            
            App.logger.log(Level.SEVERE, 
                    "Could not initialize socket",ex);
        }        
    }
    
    public Broadcaster(){

        this( "localhost" );
    }
    
    /**
     * starts a thread in server to listen incoming requests.
     * Returns user data according to request type
     */
    public void start(){
        
        executorService.execute( () -> {
            
            while ( !Thread.currentThread().isInterrupted() ){
                try {
                    byte[] buffer=new byte[1024];
                    
                    //wait until notify from user request
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    server.receive(request);
                    
                    
                    InetAddress remoteCli=request.getAddress();
                    int remotePort=request.getPort();

                    //System.out.println("remote request from:"+remoteCli.getHostName());
                    JSONObject data=new JSONObject();
                    switch( new String(buffer,0, request.getLength()) ){
                        
                        case "HNAME":
                            data.append("HNAME",String.valueOf(getHostName()) );
                            break;
                            
                        case "STAT":
                            data.append("STAT",getStatus() );
                            break;
                            
                        case "CSTAT":
                            data.append("CSTAT", String.valueOf(getStatusMessage()) );
                            break;
                            
                        case "IMG":
                            int d;
                            while ((d=this.imageInputStream.read() )!=-1) {
                                data.append("IMG",d);
                            }
                            break;
                            
                        default:
                            
                            data.putOpt("HNAME",String.valueOf(getHostName()) );
                            data.putOpt("STAT",getStatus() );
                            data.putOpt("CSTAT", String.valueOf(getStatusMessage()) );
                            data.putOpt("NAME", String.valueOf(getName()) );
                            data.putOpt("SURNAME", String.valueOf(this.getSurName()) );
                            break;
                    }
                    buffer=data.toString().getBytes();
                    
                    DatagramPacket response=new DatagramPacket(buffer, buffer.length, remoteCli,remotePort);
                    server.send(response);
                    
                } catch (IOException ex) {
                    System.err.println(this.getClass().getName()
                            +"listener Server :"+ex.getMessage());
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
        
        System.out.println("broadcaster stopped");
    }

    public static void main(String[] args) {
        System.out.println("Broadcaster Test");
        var s=new Broadcaster();
        
        s.start();
        
        s.join();
    }
    
}
