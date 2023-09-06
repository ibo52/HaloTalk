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
public class Broadcaster extends userObject {
    
    private DatagramSocket server;
    int port;
    
    Thread starter;
    
    public Broadcaster(){
        this.port=50002;
                
        try {
            this.server=new DatagramSocket(this.port);
            this.server.setBroadcast(true);

        } catch (SocketException ex) {
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

    public static void main(String[] args) {
        System.out.println("Broadcaster Test");
        var s=new Broadcaster();
        
        s.start();
        
        s.join();
    }
    
}
