/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 */
public class Client extends CommunicationObject{
    private RSA rsa;
    
    private long[] REMOTE_KEY;

    @Override
    public void initialize(){
        this.rsa=new RSA();
        try {
            
            Socket client=new Socket();

            client.connect(new InetSocketAddress(
                    this.remoteIp,
                    this.remotePort ),
                    300);
            
            this.setClientSocket(client);
            
            setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
            setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
            
            long[] CLI_KEY=handshake();
            REMOTE_KEY=CLI_KEY;
            
        }catch( SocketTimeoutException ex ){
            try {
                App.logger.log(Level.FINEST,"Server does not respond."
                        + " Socket Output will be forward to OUT file",ex);
                
                //forward output to userBuffers file of OUT if no connection established
                File userBufferPath=new File(App.class.
                        getResource("userBuffers").toURI().toString(),
                        this.getRemoteIp());
                //create directories
                Files.createDirectories(userBufferPath.toPath());
                //create file and set socket output to that file
                this.setSocketOutputStream(new DataOutputStream(
                        new FileOutputStream(Paths.get(
                                userBufferPath.toString(),"OUT").toFile(),true )));
                
            } catch (IOException ex1) {
                App.logger.log(Level.FINEST,"Error while redirecting Socket"
                        + "outPut to a file",ex1);
                System.exit(ex1.hashCode());
                
            } catch (URISyntaxException ex1) {
                App.logger.log(Level.FINEST,"Error while redirecting Socket"
                        + "outPut to a file: Wrong URI given to path",ex1);
                System.exit(ex1.hashCode());
            }
            
        }catch (ConnectException ex) {
            System.err.println("Client could not connect to Address:"+this.getRemoteIp()+".\n"
                    + "Possibly server is down or not accessible due to firewall.\n"
                    + "Returned error is:"+ex.getMessage());
            System.exit(ex.hashCode());
            
        } catch (IOException ex) {
            System.err.println(this.getClass().getName()+" initialize:"+ex.getMessage());

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
    
    private long[] handshake(){
        ByteBuffer outgoingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        
        outgoingPublicKey.asLongBuffer().put( this.rsa.getPublicKey() );
        
        ByteBuffer incomingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );

        try {
            int recv=this.getSocketInputStream().read(incomingPublicKey.array());
            
            //send this public key
            this.getSocketOutputStream().write( outgoingPublicKey.array() );
            
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "handshake phase failed",ex);
            System.exit(ex.hashCode());
        }
        
        long[] remoteKey=new long[2];
        incomingPublicKey.asLongBuffer().get(remoteKey);
        return remoteKey;
    }
    
    @Override
    public void start(){
        //start sender/receiver threads
        super.startCommunicationThreads();
        
        //overrided for later possible implements
    }
    
    public void stop(){
        this.executorService.shutdownNow();
        super.stopCommunicationThreads();
        
        //overrided for later possible implements
    }
}
