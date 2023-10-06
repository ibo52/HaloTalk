/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import org.halosoft.talk.adapters.SocketHandlerAdapter;
import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Level;
import org.halosoft.talk.App;

/**
 * Generate an communication object to send and receive messages
 * @author ibrahim
 */
public class CommunicationObject extends SocketHandlerAdapter{
    
    public CommunicationObject(String ipAddr, int port){
        super(ipAddr, port);
    }
    
    public CommunicationObject(){
        this("0.0.0.0", 50001);
    }
    
    public CommunicationObject(String ipAddr){
        this(ipAddr,50001);
    }
    
    private class Receiver implements Runnable{
        
        @Override
        public void run(){
            
            while ( !Thread.currentThread().isInterrupted() ){
            
                try {
                    String message=socketIn.readUTF();
                    System.out.printf("<%s:%d> :%s\n",client.getInetAddress().getHostAddress(), client.getPort(),message );

                } catch( EOFException ex ){

                    App.logger.log(Level.SEVERE, 
                              "Receiver EOF reached:"
                            + "Possiby remote end closed the connection:\n"
                            + "\tclient will be closed\n"
                            + "\treceiver/sender thraeds will be interrupted"
                            ,ex);
                    break;

                } catch (IOException ex) {
                    App.logger.log(Level.SEVERE, 
                        "Error while managing Receiver",ex);
                }
            
            }
            
        }
    }
    
    private class Sender implements Runnable{
        
        @Override
        public void run(){
            
            while ( !Thread.currentThread().isInterrupted() ){
            
            try {
                
                while( !localIn.ready() ){//wait for if input buffer is ready
                    Thread.sleep(200);
                    
                    if( client.isClosed() ){
                        break;
                    }
                }
                String message=localIn.readLine();
                
                socketOut.writeUTF(message);
                //System.out.println("<you>:"+message);
            
            } catch (InterruptedException ex) {
                App.logger.log(Level.FINEST, 
                        "Sender interrupted",ex);
                break;
                
            }catch (IOException ex) {
                App.logger.log(Level.SEVERE, 
                        "Error while managing Sender",ex);
            }
        }
            
        }
    }
    
    public void startCommunicationThreads(){
        
        executorService.execute(new Sender());
        executorService.execute(new Receiver());
    }
    
    public void stopCommunicationThreads(){
        
        executorService.shutdownNow();
        
        try {
            this.socketOut.writeUTF("SHUTDOWN");//send close signal
            
            this.client.close();
            
            socketOut.close();
            
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while stopping client socket",ex);
        }
        catch (NullPointerException ex) {
            App.logger.log(Level.FINEST,"Socket is already null",ex);
        }

        Thread.currentThread().interrupt();
    }
}
