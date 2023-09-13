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
import java.nio.ByteBuffer;
import javafx.application.Platform;

/**
 *
 * @author ibrahim
 */
public class Client extends CommunicationObject implements Connectible{
    private RSA rsa;
    
    private int[] REMOTE_KEY;
    

    @Override
    public void initialize(){
        
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
        this.initialize();
        this.rsa=new RSA();
    }
    public Client(String ipAddr, int port){
        super(ipAddr, port);
        this.initialize();
        this.rsa=new RSA();
    }
    public Client(String ipAddr){
        super(ipAddr);
        this.initialize();
        this.rsa=new RSA();
    }
    
    public int[] handshake(){
        ByteBuffer outgoingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        outgoingPublicKey.asLongBuffer().put( this.rsa.getPublicKey() );
        
        ByteBuffer incomingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        try {
            
            
            int recv=this.getSocketInputStream().read(incomingPublicKey.array());
            
            System.out.println("public key reached:"+incomingPublicKey.getInt()+","
            +incomingPublicKey.getInt());
            
            //send this public key
            this.getSocketOutputStream().write( outgoingPublicKey.array() );
            
        } catch (IOException ex) {
            System.out.println("Handshake Failed:"+ex.getMessage());
            Platform.exit();
        }
        
        return incomingPublicKey.asIntBuffer().array();
    }
    
    @Override
    public void start(){
        //start sender/receiver threads
        super.startCommunicationThreads();
        
        //overrided for later possible implements
    }
    
    public void stop(){
        super.stopCommunicationThreads();
        
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
