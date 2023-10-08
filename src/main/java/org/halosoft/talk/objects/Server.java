/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import org.halosoft.talk.adapters.SocketHandlerAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 */
public class Server extends SocketHandlerAdapter {
    
    public static final HashMap<String,
            LinkedBlockingQueue<String>[]> clients=new HashMap<>();//ipc instead of write to file
    
    public static enum Queue{IN(0),OUT(1);
        private final int numeric;

        private Queue(int val){
            this.numeric=val;
        }
        public int getValue(){
            return this.numeric;
        }   
    };
    
    private ServerSocket server;
    
    private RSA rsa;
    private long[] REMOTE_KEY;
    
    public Server(String ipAddr, int port){
        
        super(ipAddr, port);

        this.rsa=new RSA();
        
        //indexes of LinkedBlockingQueue: 0 for IN , 1 for OUT messages
    }
    
    public Server(String ipAddr){
        this(ipAddr,50001);

    }
    
    public Server(){
        this("0.0.0.0");
    }
    
    /**
     * this method calls automatically from super class constructor
     */
    @Override
    public void initialize(){
        
        executorService=Executors.newCachedThreadPool();
        int MAX_ALLOWED_CLI=64;
        
        try {
            server=new ServerSocket(this.getRemotePort(), MAX_ALLOWED_CLI, 
                    InetAddress.getByName(this.getRemoteIp() ) );
       
        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while initializing server socket",ex);
        }
    }
    
    public ServerSocket getServerSocket(){
        return this.server;
    }
    
    @Override
    public void start(){
        
        if ( ((ThreadPoolExecutor)executorService).getActiveCount()<1 ) {
            
            executorService.execute( () -> {
                
                while ( !Thread.currentThread().isInterrupted() ) {
                    
                    try {
                        
                        Socket client1 = server.accept();
                        
                        /*System.out.printf("%s:%d connected\n",
                                client1.getInetAddress().getHostAddress(),
                                client1.getPort());*/
                        setSocketInputStream(new DataInputStream(new BufferedInputStream(
                        client1.getInputStream())));
                
                        setSocketOutputStream(new DataOutputStream(
                        client1.getOutputStream()));
                        
                        long[] CLI_KEY=handshake();
                        REMOTE_KEY=CLI_KEY;

                        ServerHandler serverHandler=new ServerHandler
                                                  (client1,CLI_KEY);
                        
                        this.executorService.execute(serverHandler);
                        
                    }catch (SocketException ex) {
                        App.logger.log(Level.FINER, 
                        "Possibly remote socket closed",ex);

                    }catch (IOException ex) {
                        App.logger.log(Level.SEVERE, 
                        "Error while managing Server",ex);
                            
                    }finally{
                        //this.stop();//server stop
                    }
                }
            });
            
        } else {
            App.logger.log(Level.INFO, 
                        "Server already started @"
                                + this.getRemoteIp()+":"+this.getRemotePort());
        }
        
    }
    
    
    private long[] handshake(){
        
        ByteBuffer outgoingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        outgoingPublicKey.asLongBuffer().put( this.rsa.getPublicKey() );
        
        ByteBuffer incomingPublicKey=ByteBuffer.allocate( Long.BYTES*2 );
        
        try {
            this.getSocketOutputStream().write( outgoingPublicKey.array() );
            
            int recv=this.getSocketInputStream().read(incomingPublicKey.array());
            
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
    public void stop(){
        
        this.executorService.shutdownNow();

        try {
            this.server.close();
            this.executorService.shutdownNow();

        } catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                        "Error while stopping server socket",ex);
        }
    }
    
    public static synchronized void saveQueuesToFile(){
        ExecutorService saveService=Executors.newFixedThreadPool(2);
        
        Server.clients.forEach((String key, LinkedBlockingQueue<String>[] value) -> {
            try {
                File savePath=Paths.get(App.class
                        .getResource("userBuffers/"+key).toURI()).toFile();
                
                ;
                saveService.execute(
                        new QueueSaver(savePath, "IN", value[0]));
                
                saveService.execute(
                        new QueueSaver(savePath, "OUT", value[1]));
                
                
            } catch (URISyntaxException ex) {
                App.logger.log(Level.WARNING, "Wrong URI given to "
                        + "path. Data will lost since it could not save",ex);
            }
        });
    }
    public static synchronized void saveQueueToFile(String keyId, Queue which){
        ExecutorService saveService=Executors.newSingleThreadExecutor();

            try {
                File savePath=Paths.get(App.class
                        .getResource("userBuffers/"+keyId).toURI()).toFile();
                
                String fileName=which.getValue()==0? "IN":"OUT";
                saveService.execute(
                        new QueueSaver(savePath, fileName, 
                                Server.getQueue(keyId, which)));
                
                
            } catch (URISyntaxException ex) {
                App.logger.log(Level.INFO, "Wrong URI given to "
                        + "path. Data could not be saved",ex);
            }
    }
    
    private static class QueueSaver implements Runnable{
        
        private BufferedWriter writer;
        private LinkedBlockingQueue<String> queue;
        private final String fname;
        private final String uname;
        
        public QueueSaver(File savePath, String fileName,
                LinkedBlockingQueue<String> queue){
            
            this.fname = fileName.equals("IN") ? "IN":"OUT";
            this.uname=savePath.getName();
            
            try {
                this.queue=queue;
                
                writer=Files.newBufferedWriter(
                        Paths.get(savePath.toString(), fileName));
                
            } catch (IOException ex) {
                App.logger.log(Level.WARNING, "Error while creating "
                        + "BufferedWriter "+this.fname+"for "+this.uname+
                        ". Data will lost since it could not save",ex);
            }
        }
        
        @Override
        public void run(){
            Thread.currentThread().setName(this.fname+" writer for "+this.uname);
                    
            try {
                if ( queue!=null ) {
                    
                        while( !queue.isEmpty() ){
                            
                            writer.write(queue.remove());
                            writer.newLine();
                        }  
                }
                writer.close();
            } catch (IOException ex) {
                App.logger.log(Level.WARNING, "Error occured"
                                + " while saving message queues of "+this.fname
                                + " for "+this.uname
                                +". Data will lost since it could not save",ex);
            }
        }
            
    }
    
    /**
     * initialize Desired queue of remote user in HashMap. If HashMap for 
     * the user is not initialized, it will be also initialized before queue.
     * @param ip user id of where queue to be mapped
     * @param which which queue of user to initialize
     */
    public static synchronized void initializeQueue(String ip,Queue which){
        
        if (Server.clients.get(ip)==null ) {
            Server.clients.put(ip, new LinkedBlockingQueue[2]);
        }
        if ( Server.clients.get(ip)[which.getValue()]==null ) {
            Server.clients.get(ip)[which.getValue()]=
                new LinkedBlockingQueue<String>();
        }
    }
    
    public static synchronized LinkedBlockingQueue<String>
                                    getQueue(String ip,Queue which){
                                        
        return Server.clients.get(ip)[which.getValue()];
    }
}
