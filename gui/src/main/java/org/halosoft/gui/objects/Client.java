/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.logging.Level;

import org.halosoft.gui.App;

/**
 *
 * @author ibrahim
 */
public class Client extends CommunicationObject{
    private RSA rsa;
    
    private long[] REMOTE_KEY;
    private boolean connected;

    @Override
    public void initialize(){
        this.rsa=new RSA();
        
        this.client=new Socket();
        
        try {
            this.client.connect(new InetSocketAddress(
                    this.remoteIp,
                    this.remotePort ),
                    300);
            
            setSocketInputStream( new DataInputStream(new BufferedInputStream( client.getInputStream() ) ) );
            setSocketOutputStream( new DataOutputStream( client.getOutputStream() ) );
            
            long[] CLI_KEY=handshake();
            REMOTE_KEY=CLI_KEY;
            
        }catch( SocketTimeoutException ex ){
            App.logger.log(Level.INFO,"Server does not respond."
                    + " Socket Output will be stay directed to OUT file");
            
        }catch (ConnectException ex) {
            App.logger.log(Level.WARNING,"Client could not connect to "
                    + "Address:"+this.getRemoteIp()+" ("+ex.toString()+").\n"
                    + "Possibly server is down, or not accessible "
                            + "due to firewall configurations.\n"
                            + "Socket output will be stay directed to OUT file\n"
                            );
            
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE,"Error while"
                    + " initializing socket",ex);
            System.exit(ex.hashCode());
        }
    }
    
    /**
     * if object's socket could not establish connection on initialization,
     * this method will help to re-establish connection to remote
     */
    public final boolean reconnect(){
        if(  !this.client.isConnected() ){
            
            try {
                Socket temp=new Socket();
                temp.connect(new InetSocketAddress(
                        this.remoteIp,
                        this.remotePort ),
                        300);
                
                this.setClientSocket(temp);
                
                getSocketOutputStream().close();
                
                setSocketInputStream( new DataInputStream( temp.getInputStream() ) );
                setSocketOutputStream( new DataOutputStream( temp.getOutputStream() ) );

                long[] CLI_KEY=handshake();
                REMOTE_KEY=CLI_KEY;
                
                App.logger.log(Level.INFO,"Server re-connection successfull."
                        + " Socket Input/Output redirected to remote");
                
            }catch( SocketTimeoutException ex ){

            }catch (ConnectException ex) {

            } catch (IOException ex) {
                App.logger.log(Level.SEVERE,"Error while"
                        + " reconnecting socket",ex);
                System.exit(ex.hashCode());
            }
        }
        return this.client.isConnected();
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
        super.stopCommunicationThreads();
        this.executorService.shutdownNow();
    }
    
    /**
     * send message through this clients OutputStream by securing
     * sending process. If there is exception on OutputStream, it will be
     * forwarded to a file to avoid the lost of messages
     * @param message desired message to send
     */
    public void send(String message){
        try {
            this.socketOut.writeUTF(message+System.lineSeparator());
        
        } catch(NullPointerException ex){
            App.logger.log(Level.INFO,"possibly socket "
                    + "streams are null. Socket output will be "
                    + "forwarded to file",ex);
            
            this.connected=false;
            this.send(message);
            
        } catch(SocketException ex){
            App.logger.log(Level.INFO, 
            "Unexpected close of remote socket occured. ("+ex.toString()
                    + ")\nSocket output will be "
                    + "forwarded to file");
            
            this.connected=false;
            this.send(message);
            
        } catch ( IOException ex) {

            App.logger.log(Level.SEVERE, 
            "Error while writing messages to send through "
                    + "socket outputstream",ex);
        }
    }
    
    public String receive(){
        
        try {
            if ( this.socketIn!=null ) {
                return this.socketIn.readUTF();
            }
            
            } catch(NullPointerException ex){
                App.logger.log(Level.FINE,"possibly socket "
                        + "InStream is null",ex);
                this.connected=false;

            } catch(EOFException ex){
                App.logger.log(Level.FINE, 
                "EOF reached. Possibly socket In Stream is closed and null",ex);
                this.connected=false;
                
            }catch(SocketException ex){
                App.logger.log(Level.FINE, 
                "Unexpected close of remote socket occured",ex);
                this.connected=false;
            }
            catch ( IOException ex) {

                App.logger.log(Level.SEVERE, 
                "Error while writing incoming messages to file",ex);
                this.connected=false;
                Thread.currentThread().interrupt();
            }
        
        return null;
    }
}
