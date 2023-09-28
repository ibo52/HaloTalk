/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.image.Image;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 */
public class Broadcaster extends userObject {
    
    private DatagramSocket server;
    private final int port;
    
    private ExecutorService executorService;
    
    /**
     * initializes DatagramSocket as a broadcast server.
     * @param ipAddr ip address to bind broadcast server
     */
    public Broadcaster(String ipAddr){
        this.port=50002;
        this.initProperties();
        
        executorService=Executors.newSingleThreadExecutor();

        try {
            this.server=new DatagramSocket(null);
            this.server.bind(new InetSocketAddress(ipAddr, port) );
            this.server.setBroadcast(true);

        } catch (SocketException ex) {
            
            System.out.println(this.getClass()+":"+ex.getMessage());
        }        
    }
    
    private void initProperties(){
        try {
            Properties p=new Properties();
            p.load(App.class.getResourceAsStream("/broadcaster.properties"));
            
            this.setContents(this.hostName, 
                    p.getProperty("NAME"), p.getProperty("SURNAME"),
                    Integer.parseInt(p.getProperty("STATUS")),
                    p.getProperty("STATUS_MESSAGE"),
                    this.ipAddress, new Image( App.class
                            .getResourceAsStream(p.getProperty("IMAGE")) )
            );

        } catch(NullPointerException ex){
            
        }catch (IOException ex) {
            ex.printStackTrace();
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
                    String data="";
                    switch(buffer.toString()){
                        
                        case "HNAME":
                            data=String.valueOf(getHostName());
                            break;
                        case "STAT":
                            data=String.valueOf(getStatus() );
                            break;
                            
                        case "CSTAT":
                            data=String.valueOf(getStatusMessage() );
                            break;
                            
                        default:
                            data=getHostName();
                            data+=";"+getStatus();
                            data+=";"+getStatusMessage();
                            data+=";"+getName();
                            data+=";"+getSurName();
                            //data+=";"+getImage();
                            break;
                    }
                    buffer=data.getBytes();
                    
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
            System.out.println(this.getClass().getName()+" join():"+ex.getMessage());
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
