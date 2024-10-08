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
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import org.halosoft.gui.App;
import org.halosoft.gui.adapters.SocketHandlerAdapter;
import org.halosoft.database.QueryResultSet;
import org.halosoft.database.SQLiteConnector;
import org.halosoft.database.SQLiteDatabaseManager;
import org.halosoft.database.TalkDBProperties;

/**
 *
 * @author ibrahim
 * client handler for Server
 * writes incoming and outgoing messages to file
 */
public class ServerHandler extends SocketHandlerAdapter implements Runnable{
            
        private SQLiteConnector database;
        
        
        public ServerHandler(Socket socket, long[] remoteKey){

            super(socket.getInetAddress().getHostAddress(),socket.getPort());
            
            try {

                //TODO: rearrange or review getresource method to be able to work with jar file
                //(create new)/(open existing) databse
                this.database=SQLiteDatabaseManager.databaseExists(
                    Paths.get(TalkDBProperties.DEFAULT_STORAGE_PATH,TalkDBProperties.nameTheDB(remoteIp)))?
                    SQLiteDatabaseManager.openDatabase(Paths.get(TalkDBProperties.DEFAULT_STORAGE_PATH, TalkDBProperties.nameTheDB(remoteIp)))
                    :
                    SQLiteDatabaseManager.createDatabaseFromFile(
                        TalkDBProperties.DEFAULT_STORAGE_PATH, TalkDBProperties.nameTheDB(remoteIp), SQLiteDatabaseManager.class.getResourceAsStream("/tables.sql") );


                //store sender ip on table
                database.query(TalkDBProperties.insertIntoSender(remoteIp));

                this.client = socket;
                            
                setSocketInputStream(new DataInputStream(new BufferedInputStream(
                        this.client.getInputStream())));
                
                setSocketOutputStream(new DataOutputStream(
                        this.client.getOutputStream()));
            
            } catch (IOException ex) {
                App.logger.log(Level.SEVERE, 
                        "Error while initializing DB and client "
                                + "of ServerHandler",ex);
            }
        }

    @Override
    public void start() {
        this.executorService.execute(
        new SocketReadManager(this.database, this.socketIn,
                this.remoteIp));
        
                this.executorService.execute(
        new SocketWriteManager(this.database, this.socketOut,
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
         * Listens for socket Input Stream and writes that data to sqlite database.
         */
        private static class SocketReadManager implements Runnable{

            private final String ip;
            private final DataInputStream in;
            private final SQLiteConnector userDatabase;
            
            public SocketReadManager(SQLiteConnector database, DataInputStream sockIn
            ,String socketIp){

                this.in=sockIn;
                this.ip=socketIp;
                this.userDatabase=database; 
            }
            
            @Override
            public void run() {

                while( !Thread.currentThread().isInterrupted() ){

                    try {
                        
                        String incomingData=this.in.readUTF();
                        if (incomingData.equals("SHUTDOWN")) {
                            break;
                        }

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
            }

        }
        
        /**
         * Waits for data to be written to database file, then forwards the data to
         * socket Output Stream.
         */
        private static class SocketWriteManager implements Runnable{

            private DataOutputStream out;
            private final SQLiteConnector userDatabase;
            
            public SocketWriteManager(SQLiteConnector database, DataOutputStream sockOut
            ,String socketIp){

                this.out=sockOut;
                this.userDatabase=database;
            }
            
            @Override
            public void run() {
                
                int lastMessageId=0;

                while( !Thread.currentThread().isInterrupted() ){
                        
                        QueryResultSet result=userDatabase.query(
                            new StringBuilder(
                                TalkDBProperties.getUnsentMessages())
                                .append(" AND id>"+lastMessageId).toString());
                        
                        try{//try to send message through socket
                            
                            for (int i = 0; i < result.getRecordCount(); i++) {

                                LinkedList<String> datalist=result.getNextRecord();

                                lastMessageId= Integer.parseUnsignedInt(datalist.get(0));

                                String message = datalist.get(2);

                                this.out.writeUTF( message );

                                userDatabase.query(TalkDBProperties.updateMessageQueue(
                                    "completed=1", "id="+lastMessageId));
                            }
                            Thread.sleep(300);
                            
                        } catch ( IOException | InterruptedException ex) {
                        //in case of an error, return message back to queue
                            App.logger.log(Level.SEVERE, 
                            "Error while sending messages from OUT file to socket",ex); 
                            break;
                        }
                        
                }
            }

        }
}
