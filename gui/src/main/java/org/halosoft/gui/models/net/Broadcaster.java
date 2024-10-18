/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import org.halosoft.gui.App;
import org.halosoft.gui.interfaces.UDPSocket;
import org.halosoft.gui.models.ObservableUser;
import org.halosoft.gui.models.User;
import org.halosoft.gui.models.net.WifiCallManager.CallType;
import org.halosoft.gui.models.net.WifiCallManager.WifiCallRespondTask;
import org.halosoft.gui.utils.StageChanger;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.net.InetAddress;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 *
 * @author ibrahim
 */
public class Broadcaster extends UDPSocket {

    protected final SimpleBooleanProperty isCallReached =new SimpleBooleanProperty(false);
    protected final SimpleStringProperty callerAddress=new SimpleStringProperty("");

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

    public Broadcaster(String ip){

        this( ip, DEFAULT_SERVER_PORT);
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
            int a=0;
            while ( !Thread.currentThread().isInterrupted() ){
                try {                    
                    //wait until notify from user request
                    //DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    byte[] incoming=this.receive();
                    
                    //type parameter of incoming request
                    String typeParam="";
                    try{
                        JSONObject request=new JSONObject( new String( incoming ) );
                        typeParam=request.optString("type");

                    }catch(JSONException e){
                        //data is not json. So it is a string as which request made from old client class
                        //this clause is for backward compatibility with older version of this class
                        typeParam=new String(incoming);
                    }
                    //System.out.println("incom"+request.toString());
                    
                    InetAddress remoteCli=packetIN.getAddress();
                    int remotePort=packetIN.getPort();

                    //set server's datagrampacket destination properties
                    this.packetOUT.setAddress(remoteCli);
                    this.packetOUT.setPort(remotePort);

                    //System.out.println("remote request from:"+remoteCli+":"+remotePort);
                    
                    //System.out.println( String.format("requested: %s", new String(buffer,0, request.getLength())) );
                    JSONObject response=new JSONObject();

                    switch( typeParam.toUpperCase() ){
                        
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

                        case "CALL":

                        //option 1: since we know ip and port of remote socket
                        //we can pass the params to runnable
                            /*WifiCallRespondTask call=WifiCallManager.respond(
                                remoteCli.getHostAddress(), remotePort, CallType.AUDIO); */

                            if( userProfile.getStatus()==User.Status.BUSY.ordinal() ){
                                //only one call at a time. Reject other ones
                                this.send("{'response':'reject ulan'}".getBytes());
                                break;
                            }
                            
                            this.callerAddress.set(String.format("%s:%s", remoteCli.getHostAddress(), remotePort));
                            this.isCallReached.set(true);
                            this.userProfile.setStatus(User.Status.BUSY.ordinal());
                            break;
                                                        
                        default:
                            
                            response.putOpt("HNAME",String.valueOf(userProfile.getHostName()) );
                            response.putOpt("STAT",userProfile.getStatus() );
                            response.putOpt("CSTAT", String.valueOf(userProfile.getStatusMessage()) );
                            response.putOpt("NAME", String.valueOf(userProfile.getName()) );
                            response.putOpt("SURNAME", String.valueOf(userProfile.getSurName()) );
                            break;
                    }

                    if (typeParam.equals("CALL")){
                        continue;
                    }
                    //System.out.println("response len:"+response.toString().length()+" "+response.toString());
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


    public SimpleBooleanProperty getCallReachedProperty(){
        return isCallReached;
    }

    public SimpleStringProperty getCallerAddressProperty(){
        return callerAddress;
    }

    public ObservableUser getObservableUser(){

        return userProfile;
    }

    public static void main(String[] args) {
        System.out.println("Broadcaster Test");
        var s=new Broadcaster();
        
        s.start();
        
        s.join();
    }
    
}
