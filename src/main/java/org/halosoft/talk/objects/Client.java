/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author ibrahim
 */
public class Client extends CommunicationObject{
    
    @Override
    public void initSocket(){
        
        try {
            
            Socket client=new Socket();

            client.connect(new InetSocketAddress(
                    this.getRemoteIp(),
                    this.getRemotePort() ),
                    300);
            
            this.setClientSocket(client);
            
            setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
            setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
        
        }catch( SocketTimeoutException ex ){
            System.out.println("Server does not respond:"+ex.getMessage());
            
        }catch (ConnectException ex) {
            System.out.println("Client could not connect to Address:"+this.getRemoteIp()+".\n"
                    + "Possibly server is down or not accessible due to firewall.\n"
                    + "Returned error is:"+ex.getMessage());
            System.exit(ex.hashCode());
            
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(ex.hashCode());
        }
    }
    
    public Client(){
        super();
    }
    public Client(String ipAddr, int port){
        super(ipAddr, port);
    }
    public Client(String ipAddr){
        super(ipAddr);
    }
    @Override
    public void start(){
        //start sender/receiver threads
        super.start();
        
        //overrided for later possible implements
    }
    
    public void stop(){
        super.stop();
        
        //overrided for later possible implements
    } 
    public static void main(String[] args) {
        System.out.println("test cli");
        Client c=new Client("192.168.1.66");
        c.start();
        
        try {
            c.getSocketOutputStream().writeUTF("hello from client");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        c.stop();
        System.out.println("client done");
    }
}
