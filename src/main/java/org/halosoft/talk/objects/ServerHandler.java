/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import org.halosoft.talk.adapters.SocketHandlerAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 * client handler for Server
 * writes incoming and outgoing messages to file
 */
public class ServerHandler extends SocketHandlerAdapter implements Runnable{
        
    private long[] REMOTE_KEY;
    
        private File clientFile;
        
        
        public ServerHandler(Socket socket, long[] remoteKey){
            super(socket.getInetAddress().getHostAddress(),socket.getPort());
             
            try {
                LinkedBlockingQueue[] messageQueues=
                {new LinkedBlockingQueue<String>(), new LinkedBlockingQueue<String>()};
                
                Server.clients.put(remoteIp, messageQueues);
                
                clientFile=new File(Paths.get(App.class
                        .getResource("userBuffers").toURI()).toString(),
                        this.remoteIp );

                Files.createDirectories(clientFile.toPath());//create path for specific user
                
                //Files.createFile(Paths.get(clientFile.toString(),"OUT"));
                this.client = socket;
            
                this.REMOTE_KEY=remoteKey;
                
                setSocketInputStream(new DataInputStream(new BufferedInputStream(
                        this.client.getInputStream())));
                
                setSocketOutputStream(new DataOutputStream(
                        this.client.getOutputStream()));
            
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, 
                        "Error while initializing files and client "
                                + "of ServerHandler",ex);
            } catch(URISyntaxException ex){
                App.logger.log(Level.WARNING, 
                        "Wrong URI given to Paths.get() method while "
                                + "initializing file",ex);
            }
        }

    @Override
    public void start() {
        this.executorService.execute(
        new ClientInputWriter(this.clientFile, this.socketIn,
                this.remoteIp));
        
                this.executorService.execute(
        new ClientOutputSender(this.clientFile, this.socketOut,
                this.remoteIp));
    
        this.executorService.shutdown();
    }

    @Override
    public void stop() {
        
        try {
            this.executorService.shutdownNow();
            this.client.close();
            
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while closing client socket",ex);
        }
    }
    
    @Override
    public void run() {
        this.start();
    }
        
        /**
         * Listens for socket Input Stream and writes that data to file.
         * The file is accessible by other classes to read incoming data
         */
        private static class ClientInputWriter implements Runnable{
            private final String ip;
            private final DataInputStream in;
            private final File userBufferPath;
            private Scanner unreadIN;
            
            public ClientInputWriter(File pathFile, DataInputStream sockIn
            ,String socketIp){
                this.in=sockIn;
                this.ip=socketIp;
                this.userBufferPath=pathFile;
                
            }
            
            private final void loadUnreadMessages(LinkedBlockingQueue queue){
                
                try {
                    this.unreadIN=new Scanner(Paths.get(
                            userBufferPath.toString(),"IN"));
                    
                    while(this.unreadIN.hasNext()){
                        queue.add(this.unreadIN.nextLine());
                    }
                    this.unreadIN.close();
                    
                    Files.delete(Paths.get(
                            userBufferPath.toString(),"IN"));
                    
                }catch (FileNotFoundException ex){
                    App.logger.log(Level.FINEST,"No unread messages"
                                + " of IN for "+this.ip+". Pass to load",ex);
                } catch (IOException ex) {
                    App.logger.log(Level.FINE,"Error while trying to access "
                                + "IN file of "+this.ip +". Pass to load unread messages",ex);
                }
            }
            
            @Override
            public void run() {

                var incomingsQueue=Server.clients.get(ip)[0];
                
                this.loadUnreadMessages(incomingsQueue);
                
                while( !Thread.currentThread().isInterrupted() ){

                    try {
                        
                        String incomingData=this.in.readUTF();
                        incomingsQueue.add(incomingData);
                        

                    } catch(NullPointerException ex){
                        App.logger.log(Level.FINE,"possibly socket "
                                + "InStream is null",ex);
                        break;
                    
                    } catch(EOFException ex){
                        App.logger.log(Level.FINE, 
                        "EOF reached. Possibly socket In Stream is null",ex);
                        break;
                    }
                    catch ( IOException ex) {
                        
                        App.logger.log(Level.SEVERE, 
                        "Error while writing incoming messages to file",ex);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

        }
        
        /**
         * Waits for data to be written to file, then forwards the data to
         * queue to send through socket Output Stream.
         * The queue is static on Server class, and accessible by other classes 
         * to write data to send.
         */
        private static class ClientOutputSender implements Runnable{
            private String ip;
            private DataOutputStream out;
            private final File userBufferPath;
            private BufferedReader unreadOUT;
            
            public ClientOutputSender(File pathFile, DataOutputStream sockOut
            ,String socketIp){
                this.out=sockOut;
                this.ip=socketIp;
                this.userBufferPath=pathFile;
            }
            
            private final void loadUnsendMessages(LinkedBlockingQueue queue){
                
                ExecutorService service=Executors.newSingleThreadExecutor();
                
                service.execute(()->{
                    
                    try {//create if do not exists
                        Files.createFile(Paths.get(
                                userBufferPath.toString(),"OUT"));
                    } catch(FileAlreadyExistsException ex){
                    
                    } catch (IOException ex) {
                        App.logger.log(Level.SEVERE,"Error while creating "
                        + "OUT file of "+this.ip +". return;",ex);
                        return;
                    }
                    
                    try {//open bind to BufferedReader in case of other classes may write data
                        this.unreadOUT=Files.newBufferedReader(Paths.get(
                                this.userBufferPath.toString(),"OUT"));
                    } catch (IOException ex) {
                        App.logger.log(Level.SEVERE,"Error while processing "
                        + "OUT file of "+this.ip +". Pass to load unsend messages",ex);
                        return;
                    }
                    //Check for if other classes wrote something
                    while( !Thread.currentThread().isInterrupted() ){
                        try{
                            
                            while( !this.unreadOUT.ready() ){
                                Thread.sleep(12000);
                            }
                            queue.add(this.unreadOUT.readLine());

                        } catch (IOException ex) {
                            App.logger.log(Level.SEVERE,"Error while processing "
                                        + "OUT file of "+this.ip +". Pass to load unsend messages",ex);
                            break;
                        } catch (InterruptedException ex) {
                            App.logger.log(Level.FINE,"interrupted: "
                                        + "OUT file processing of "+this.ip ,ex);
                            break;
                        }

                    }

                    try {
                        Files.delete(Paths.get(
                                userBufferPath.toString(),"OUT"));

                    } catch (IOException ex) {
                        App.logger.log(Level.SEVERE,"Error while deleting "
                                        + "OUT file of "+this.ip ,ex);
                    }
                });
            }
            
            @Override
            public void run() {
                
                var outgoingsQueue=Server.clients.get(ip)[1];
                
                this.loadUnsendMessages(outgoingsQueue);//runs in thread
                
                while( !Thread.currentThread().isInterrupted() ){

                    try {//wait until some class write data to file
                            
                        this.out.writeUTF( outgoingsQueue.take() );
                        
                    } catch ( IOException ex) {
                        
                        App.logger.log(Level.SEVERE, 
                        "Error while sending messages from OUT file to socket",ex); 
                        break;
                        
                    } catch (InterruptedException ex) {
                        App.logger.log(Level.WARNING, 
                        "interrupted while waiting to get message"
                                + "from outgoingsQueue",ex);
                        break;
                    }
                }
            }

        }

        
}
