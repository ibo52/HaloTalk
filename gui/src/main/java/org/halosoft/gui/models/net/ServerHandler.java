/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.net.SocketException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import org.halosoft.gui.App;
import org.halosoft.database.QueryResultSet;
import org.halosoft.database.SQLiteConnector;
import org.halosoft.database.SQLiteDatabaseManager;
import org.halosoft.database.TalkDBProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author ibrahim
 * client handler for Server
 * writes incoming and outgoing messages to file
 */
public class ServerHandler extends TCPSocket implements Runnable{
        
    private final ExecutorService service=Executors.newCachedThreadPool();
    private SQLiteConnector database;
    private boolean runSocketRunnables=true;

    private final AtomicBoolean connectionDropped=new AtomicBoolean(false);

    private final void __constructor__DBinit(){

        String remoteIp=this.ip;
        try {

            Path dbPath=Paths.get(TalkDBProperties.DEFAULT_STORAGE_PATH,
                                    TalkDBProperties.nameTheDB(remoteIp));
            //TODO: rearrange or review getresource method to be able to work with jar file
            //(create new)/(open existing) databse
            this.database=SQLiteDatabaseManager.databaseExists(
                dbPath)?
                SQLiteDatabaseManager.openDatabase(dbPath)
                :
                SQLiteDatabaseManager.createDatabaseFromFile(
                    dbPath, SQLiteDatabaseManager.class.getResourceAsStream("/tables.sql") );


            //store sender ip on table
            database.query(TalkDBProperties.insertIntoSender(remoteIp));
        
        } catch (FileAlreadyExistsException ex) {
            App.logger.log(Level.SEVERE, "A database already exists on traget path",ex);

        }catch (IOException ex) {
            App.logger.log(Level.SEVERE, 
                    "Error while initializing DB and client "
                            + "of ServerHandler",ex);
        }
    }

    public ServerHandler(TCPSocket sepecificSock, boolean runSocketWriter){
        this(sepecificSock);

        //caht panel does not require a writer, since writing made by Server's writer
        this.runSocketRunnables=runSocketWriter;
    }

    public ServerHandler(TCPSocket specificSock){

        super(specificSock);

        __constructor__DBinit();
    }
        
    public ServerHandler(Socket socket){

            super(socket);

            __constructor__DBinit();
            
        }

    private void start() throws IOException {

        String remoteIp=this.ip;

        
            this.service.execute(
            new SocketReadManager(this.database, getDataInputStream(),
                remoteIp, connectionDropped));

        if(runSocketRunnables){
            this.service.execute(
            new SocketWriteManager(this.database, getDataOutputStream(),
                remoteIp, connectionDropped));}
        
        this.service.shutdown();
    }

    public void stop() {

        this.service.shutdownNow();
        this.connectionDropped.set(true);
        try {
            this.getSocket().close();
            
        } catch (IOException ex) {
            
        }
    }

    public boolean isConnectionUp(){
        return !this.connectionDropped.get();
    }
    public AtomicBoolean getConnectionDropped(){
        return this.connectionDropped;
    }
    
    @Override
    public void run() {

        try {
            this.start();

        } catch (IOException e) {
            App.logger.log(Level.WARNING, "Socket IO error when starting the server up", e);
        }
    }
        
    public SQLiteConnector getConnector(){
        return database;
    }
    /**
     * Listens for socket Input Stream and writes that data to sqlite database.
     */
    private static class SocketReadManager implements Runnable{

            private final String ip;
            private final DataInputStream in;
            private final SQLiteConnector userDatabase;
            private AtomicBoolean dropNotifier;
            
            public SocketReadManager(SQLiteConnector database, DataInputStream sockIn
            ,String socketIp, AtomicBoolean dropnotifier){

                this.in= sockIn;
                this.ip=socketIp;
                this.userDatabase=database; 
                this.dropNotifier=dropnotifier;
            }
            
            @Override
            public void run() {

                while( !Thread.currentThread().isInterrupted() ){

                    try {
                        
                        String incomingData=this.in.readUTF();
                        
                        if ( incomingData.isEmpty() ){//end of stream reached
                            continue;
                        }
                        else if ( incomingData.equals("SHUTDOWN")) {//remote close req
                            break;
                        }
                        
                        //System.out.println(String.format("incomingData len:%d -> %s", incomingData.length(), incomingData));
                        //opt: senderId is 0 if ip is loopback
                        userDatabase.query(
                            TalkDBProperties.insertIntoMessageQueue(ip.equals("127.0.0.1")? 0:1, incomingData, 0));

                    } catch(NullPointerException ex){
                        App.logger.log(Level.FINE,"possibly socket "
                                + "InStream is null",ex);
                        break;
                    
                    } catch(EOFException ex){
                        App.logger.log(Level.FINE, 
                        "EOF reached. Possibly socket In Stream is null",ex);
                        break;
                        
                    }catch(SocketException ex){
                        App.logger.log(Level.FINE, 
                        "Unexpected close of remote socket occured",ex);
                        break;
                    }
                    catch ( IOException ex) {
                        
                        App.logger.log(Level.SEVERE, 
                        "Error while writing incoming messages to file",ex);
                        break;
                    }
                }
                dropNotifier.set(true);
            }

        }
        
    /**
     * Waits for data to be written to database file, then forwards the data to
     * socket Output Stream.
     */
    private static class SocketWriteManager implements Runnable{

            private DataOutputStream out;
            private final SQLiteConnector userDatabase;
            private AtomicBoolean dropNotifier;
            
            public SocketWriteManager(SQLiteConnector database, DataOutputStream sockOut
            ,String socketIp, AtomicBoolean dropnotifier){

                this.out= sockOut;
                this.userDatabase=database;
                this.dropNotifier=dropnotifier;
            }
            
            @Override
            public void run() {
 
                while( !Thread.currentThread().isInterrupted() ){
                        
                        QueryResultSet result=userDatabase.query(
                                TalkDBProperties.getUnsentMessages());
                        
                        try{//try to send message through socket
                            
                            for (int i = 0; i < result.getRecordCount(); i++) {

                                LinkedList<String> datalist=result.getNextRecord();

                                String message = datalist.get(result.indexOf("message"));
                                int id=Integer.parseUnsignedInt(
                                    datalist.get(result.indexOf("id")));

                                this.out.writeUTF(message);

                                userDatabase.query(TalkDBProperties.updateMessageQueue(
                                    "completed=1", "id="+id ));
                            }
                            Thread.sleep(300);
                            
                        } catch ( IOException | InterruptedException ex) {
                        //in case of an error, return message back to queue
                            App.logger.log(Level.FINE, 
                            "Socket OUT stream: "+ex.getMessage()); 
                            break;
                        }
                }
                dropNotifier.set(true);
            }

        }
}
