/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import org.halosoft.gui.App;
import org.halosoft.gui.interfaces.UDPSocket;
import org.halosoft.gui.models.ObservableUser;
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
public class Broadcaster extends UDPSocket {
    
    private final ObservableUser userProfile=new ObservableUser();
        
    private ExecutorService executorService;
    
    /**
     * initializes DatagramSocket as a broadcast server.
     * @param ipAddr ip address to bind broadcast server
     */
    public Broadcaster(String ipAddr, int port){

        super(ipAddr, port, true);
        
        executorService=Executors.newSingleThreadExecutor();
    }
    
    public Broadcaster(){

        this( "localhost", DEFAULT_SERVER_PORT);
    }
    
    /**
     * starts a thread in server to listen incoming requests.
     * Returns user data according to request type
     */
    public void start(){
        
        executorService.execute( () -> {
            
            while ( !Thread.currentThread().isInterrupted() ){
                try {                    
                    //wait until notify from user request
                    //DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    byte[] request=this.receive();
                    
                    /*
                    InetAddress remoteCli=packetIN.getAddress();
                    int remotePort=packetIN.getPort();
                    */

                    //System.out.println("remote request from:"+remoteCli.getHostName());
                    //System.out.println( String.format("requested: %s", new String(buffer,0, request.getLength())) );
                    JSONObject response=new JSONObject();

                    switch( new String(request) ){
                        
                        case "HNAME":
                            response.append("HNAME",String.valueOf(userProfile.getHostName()) );
                            break;
                            
                        case "STAT":
                            response.append("STAT",userProfile.getStatus() );
                            break;
                            
                        case "CSTAT":
                            response.append("CSTAT", userProfile.getStatusMessage() );
                            break;
                            
                        case "IMG":
                            
                            break;
                            
                        default:
                            
                            response.putOpt("HNAME",String.valueOf(userProfile.getHostName()) );
                            response.putOpt("STAT",userProfile.getStatus() );
                            response.putOpt("CSTAT", String.valueOf(userProfile.getStatusMessage()) );
                            response.putOpt("NAME", String.valueOf(userProfile.getName()) );
                            response.putOpt("SURNAME", String.valueOf(userProfile.getSurName()) );
                            break;
                    }

                    //System.out.println("response len:"+data.toString().length()+" "+data.toString());
                    this.send( response.toString().getBytes() );
                    
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
        this.sock.close();
        
        System.out.println("broadcaster stopped");
    }

    public static void main(String[] args) {
        System.out.println("Broadcaster Test");
        var s=new Broadcaster();
        
        s.start();
        
        s.join();
    }
    
}
