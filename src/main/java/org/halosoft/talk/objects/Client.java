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
import java.net.Socket;

/**
 *
 * @author ibrahim
 */
public class Client extends CommunicationObject{
    @Override
    public void initSocket(){
        
        try {

            var client=new Socket(this.getRemoteIp(), this.getRemotePort());

            this.setClientSocket(client);
            
            setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
            setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
        
        }catch (ConnectException ex) {
            System.out.println("Client could not connect to Address.\n"
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
        System.out.println("client start");
        
        var cli=new Client();
        
        cli.start();
        cli.join();
        System.out.println("cli will stop");
        cli.stop();
    }
    
}
