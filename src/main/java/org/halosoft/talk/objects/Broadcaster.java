/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author ibrahim
 */
public class Broadcaster {
    
    private DatagramSocket server;
    
    private String hostName;
    int port;
    
    private int userStatus;
    private String customStatus;
    
    Thread starter;
    
    public Broadcaster(){
        this.port=50002;
        this.userStatus=2;
        this.customStatus="heyyo! I am using HaloTalk.";
                
        try {
            this.server=new DatagramSocket(this.port);
            this.server.setBroadcast(true);
            
            this.hostName=System.getenv("USERNAME")+"@"+InetAddress.getLocalHost().getHostName();
            
        } catch (SocketException | UnknownHostException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    public void start(){
        
        starter=new Thread(new Runnable(){
            @Override
            public void run() {
                
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
                                data=String.valueOf(getCustomStatus() );
                                break;
                                
                            default:
                                data=getHostName();
                                data+=";"+getStatus();
                                data+=";"+getCustomStatus();
                                break;
                        }
                        buffer=data.getBytes();
                        
                        DatagramPacket response=new DatagramPacket(buffer, buffer.length, remoteCli,remotePort);
                        server.send(response);
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                }
            }
            
        });
        
        starter.start();
    }
    
    public void join(){
        try {
            starter.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void stop(){
        this.starter.interrupt();
        this.server.close();
        
        System.out.println("stop exectued on broadcaster");
    }
    
    public String getHostName(){
        return this.hostName;
    }
    public void setCustomStatus(String statusMessage){
        this.customStatus=statusMessage;
    }
    public String getCustomStatus(){
        return this.customStatus;
    }
    
    public void setStatus(int status){
        this.userStatus=status;
    }
    public int getStatus(){
        return this.userStatus;
    }
    
    public static void main(String[] args) {
        var s=new Broadcaster();
        
        s.start();
        
        s.join();
    }
    
}
