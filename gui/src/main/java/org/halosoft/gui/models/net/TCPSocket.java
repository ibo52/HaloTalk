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
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Generate an communication object to send and receive messages
 * @author ibrahim
 */
public class TCPSocket{

    public static final int DEFAULT_SERVER_PORT=50001;

    protected static final int DEFAULT_BUFFER_SIZE=65536;

    protected final String ip;

    protected final byte[] INBuffer=new byte[DEFAULT_BUFFER_SIZE];

    private final DataInputStream IN;
    private final DataOutputStream OUT;

    protected final Socket sock;

    /*manage an already opened socket */

    public TCPSocket(TCPSocket s){

        this.sock=s.getSocket();
        this.ip=s.ip;
        this.IN=s.IN;
        this.OUT=s.OUT;
    }

    public TCPSocket(Socket s){

        this.sock=s;

        ip=s.getInetAddress().getHostAddress();

        DataInputStream tempIN=null;
        DataOutputStream tempOUT=null;
        try {
            tempIN=new DataInputStream(sock.getInputStream());
            tempOUT=new DataOutputStream(sock.getOutputStream());

        } catch (IOException e) {
        }
        IN=tempIN;
        OUT=tempOUT;
    }
    
    public TCPSocket(String ip){
        this(ip, DEFAULT_SERVER_PORT);
    }
    
    public TCPSocket(String ip, int port){

        this.sock=new Socket();

        DataInputStream tempIN=null;
        DataOutputStream tempOUT=null;

        this.ip=ip;

        try {
            sock.connect(new InetSocketAddress(ip, port), 1000);

            tempIN=new DataInputStream(sock.getInputStream());
            tempOUT=new DataOutputStream(sock.getOutputStream());

        } catch (SocketTimeoutException e) {
            System.err.println("TCP Socket could not establish a connection in specified time: "+e.getMessage());

        } catch (IOException e) {
            System.err.println("TCP Socket IO error: "+e.getMessage());
        }

        IN=tempIN;
        OUT=tempOUT;

        
    }

    public Socket getSocket(){
        return sock;
    }
    /*General tcp standart: stream type receive */
    public byte[] receive() throws IOException{
        
        int readLimit=sock.getInputStream().read(INBuffer, 0, INBuffer.length);

        return Arrays.copyOf(INBuffer, readLimit);
    }

    /*General tcp standart: Stream type send */
    public void send(byte[] data) throws IOException{

        sock.getOutputStream().write(data);
    }
    /*special method: send data as java special utf-8 encoding */
    public void send(String message) throws IOException{

        OUT.writeUTF(message);
    }

    /*special method: receive data as java special utf-8 encoding */
    public String readUTF() throws IOException{
        return IN.readUTF();
    }
    
    public  void writeUTF(String message) throws IOException{
        OUT.writeUTF(message);;
    }
    /*return stream which wraps socket stream to easily manage write operations */
    public DataOutputStream getDataOutputStream(){
        return OUT;
    }
    /*return stream which wraps socket stream to easily manage read operations */
    public DataInputStream getDataInputStream(){
        return IN;
    }
}
