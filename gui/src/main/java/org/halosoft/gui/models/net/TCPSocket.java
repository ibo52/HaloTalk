/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.models.net;

import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Generate an communication object to send and receive messages
 * @author ibrahim
 */
public class TCPSocket{

    public static final int DEFAULT_SERVER_PORT=50001;

    protected static final int DEFAULT_BUFFER_SIZE=65536;

    protected final String ip;//socket method may not return given ip
    protected final int port;//socket method may not return given port

    protected final byte[] INBuffer=new byte[DEFAULT_BUFFER_SIZE];

    private final AtomicBoolean alive=new AtomicBoolean(true);//connection state

    private DataInputStream IN;
    private DataOutputStream OUT;

    protected Socket sock;

    /*manage an already opened socket */

    public TCPSocket(TCPSocket s){

        this.sock=s.getSocket();

        this.ip=s.ip;
        this.port=s.port;

        this.IN=s.IN;
        this.OUT=s.OUT;
        
        alive.set( s.alive.get() );
    }

    public TCPSocket(Socket s){

        this.sock=s;
        this.port=s.getPort();

        ip=s.getInetAddress().getHostAddress();

        try {
            IN=new DataInputStream(sock.getInputStream());
            OUT=new DataOutputStream(sock.getOutputStream());

        } catch (IOException e) {
            alive.set(false);
        }

    }
    
    public TCPSocket(String ip){
        this(ip, DEFAULT_SERVER_PORT);
    }
    
    public TCPSocket(String ip, int port){

        this.sock=new Socket();

        this.ip=ip;
        this.port=port;

        try {
            sock.connect(new InetSocketAddress(ip, port), 1000);

            IN=new DataInputStream(sock.getInputStream());
            OUT=new DataOutputStream(sock.getOutputStream());

        } catch (SocketTimeoutException e) {
            alive.set( false );
            System.err.println("TCP Socket could not establish a connection in specified time: "+e.getMessage());

        } catch (IOException e) {
            alive.set( false );
            System.err.println("TCP Socket IO error: "+e.getMessage());
        }
    }

    public Socket getSocket(){
        return sock;
    }

    public void reconnect(int timeoutMillis) throws SocketTimeoutException, IOException{
        
        try{
        this.sock=new Socket();

        this.sock.connect(new InetSocketAddress(ip, this.port), timeoutMillis);

        IN=new DataInputStream( sock.getInputStream() );
        OUT=new DataOutputStream( sock.getOutputStream() );

        alive.set(true);
        
        }catch(Exception e){

            alive.set(false);
            throw e;
        }


    }
    /*General tcp standart: stream type receive */
    public byte[] receive() throws IOException{
        
        try{
        int readLimit=sock.getInputStream().read(INBuffer, 0, INBuffer.length);

        return Arrays.copyOf(INBuffer, readLimit);

        }catch(IOException e){
            alive.set(false);
            throw e;
        }
    }

    /*General tcp standart: Stream type send */
    public void send(byte[] data) throws IOException{

        try{
        sock.getOutputStream().write(data);

        }catch(IOException e){
            alive.set(false);
            throw e;
        }
    }
    /*special method: send data as java special utf-8 encoding */
    public void send(String message) throws IOException{

        try{
            OUT.writeUTF(message);

        }catch(IOException e){
            alive.set(false);
            throw e;
        }
    }

    /*special method: receive data as java special utf-8 encoding */
    public String readUTF() throws IOException{

        try{
            return IN.readUTF();
        }catch(IOException e){
            alive.set(false);
            throw e;
        }
    }
    
    public  void writeUTF(String message) throws IOException{

        try{
            OUT.writeUTF(message);
        }catch(IOException e){
            alive.set(false);
            throw e;
        }
    }
    /*return stream which wraps socket stream to easily manage write operations */
    public DataOutputStream getDataOutputStream(){
        return OUT;
    }
    /*return stream which wraps socket stream to easily manage read operations */
    public DataInputStream getDataInputStream(){
        return IN;
    }

    public void close() throws IOException{
        
        this.sock.close();
        this.alive.set(false);
    }

    public AtomicBoolean getAlive() {
        return alive;
    }
    
    public boolean isAlive() {
        return alive.get();
    }
}
