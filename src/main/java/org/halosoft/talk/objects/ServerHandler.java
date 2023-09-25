/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.halosoft.talk.App;

/**
 *
 * @author ibrahim
 * client handler for Server
 * writes incoming and outgoing messages to file
 */
public class ServerHandler extends SocketHandlerAdapter implements Runnable{
        
    private long[] REMOTE_KEY;
    
        private Path clientFile;
        
        
        public ServerHandler(Socket socket, long[] remoteKey){
            super(socket.getInetAddress().getHostAddress(),socket.getPort());
            
            try {
                clientFile=Paths.get(App.class
                        .getResource("userBuffers").getPath(),
                        this.remoteIp );

                Files.createDirectories(clientFile);//create path for specific user
                
                //create input and output files of socket streams
                Files.createFile(Paths.get(clientFile.toString(),"IN"));
                //Files.createFile(Paths.get(clientFile.toString(),"OUT"));
                this.client = socket;
            
                this.REMOTE_KEY=remoteKey;
                
                setSocketInputStream(new DataInputStream(new BufferedInputStream(
                        this.client.getInputStream())));
                
                setSocketOutputStream(new DataOutputStream(
                        this.client.getOutputStream()));
            
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    @Override
    public void start() {
        this.executorService.execute(
        new ClientInputWriter(this.clientFile, this.socketIn,
                this.remoteIp));
        
                /*this.executorService.execute(
        new ClientOutputSender(this.clientFile, this.socketOut,
                this.remoteIp));*/
    
        this.executorService.shutdown();
    }

    @Override
    public void stop() {
        
        try {
            this.executorService.shutdownNow();
            this.client.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
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
            private String ip;
            private DataInputStream in;
            private FileWriter writer;
            
            public ClientInputWriter(Path pathFile, DataInputStream sockIn
            ,String socketIp){
                this.in=sockIn;
                this.ip=socketIp;
                try {
                    writer=new FileWriter(Paths.get(
                            pathFile.toString(), "IN").toFile(),
                            true);
                    
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            }
            
            @Override
            public void run() {

                while( !Thread.currentThread().isInterrupted() ){

                    try {
                        String incomingData=this.in.readUTF();
                        System.out.println("Incoming to server:"+incomingData);
                        writer.write(incomingData);
                        writer.write("\n");
                        writer.flush();
                        
                    } catch ( IOException ex) {
                        
                        System.out.println("serverHandler for "+this.ip
                                +":"+ex.getMessage());  
                        
                        try {
                            writer.flush();
                            writer.close();
                        } catch (IOException ex1) {
                            System.out.println("another exception occured while"
                                    + " closing FileWriter"
                                    + "of serverHandler for "+this.ip
                                +":"+ex1.getMessage());
                        }
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

        }
        
        /**
         * Waits for data to be written to file, then sends the data through
         * socket Output Stream.
         * The file is accessible by other classes to write data to send.
         */
        private static class ClientOutputSender implements Runnable{
            private String ip;
            private DataOutputStream out;
            private BufferedReader dataSender;
            
            public ClientOutputSender(Path pathFile, DataOutputStream sockOut
            ,String socketIp){
                this.out=sockOut;
                this.ip=socketIp;
                try {
                    dataSender=new BufferedReader(
                            new FileReader(Paths.get(
                                    pathFile.toString(), "OUT").toFile()
                            ));
                    
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            }
            
            @Override
            public void run() {

                while( !Thread.currentThread().isInterrupted() ){

                    try {//wait until some class write data to file
                        while( !dataSender.ready() ){
                            Thread.sleep(300);
                        }
                        
                        this.out.writeUTF( this.dataSender.readLine() );
                        
                    } catch ( IOException ex) {
                        
                        System.out.println("serverHandler for "+this.ip
                                +":"+ex.getMessage());  
                        
                    }finally{
                        try {
                            this.dataSender.close();
                        } catch (IOException ex) {
                            System.out.println("another exception occured while"
                                    + " closing BufferedReader"
                                    + "of serverHandler for "+this.ip
                                +":"+ex.getMessage());
                        }
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

        }

        
}
