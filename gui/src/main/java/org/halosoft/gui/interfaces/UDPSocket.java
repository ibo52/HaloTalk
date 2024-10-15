package org.halosoft.gui.interfaces;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public abstract class UDPSocket {

    public static final int DEFAULT_BUFFER_SIZE=65536;
    public static final int DEFAULT_SERVER_PORT=50002;

    protected final DatagramPacket packetOUT;  //packet to send to destination ip and port
    protected final DatagramPacket packetIN;  //packet to read from destination ip and port
    protected final DatagramSocket sock;

    public UDPSocket(String ip, int port, boolean isServer){

        DatagramPacket tempIN=null;
        DatagramPacket tempOUT=null;
        DatagramSocket tempSock=null;

        try{
        //byte buffer does not neccesary as send method binds a reference parameter everytime
        tempIN=new DatagramPacket(new byte[DEFAULT_BUFFER_SIZE], DEFAULT_BUFFER_SIZE);
        tempOUT=new DatagramPacket(new byte[0], 0, InetAddress.getByName(ip), port);

        
            if (isServer){
                tempSock=new DatagramSocket(null);
                tempSock.bind(new InetSocketAddress(ip, port) );
                tempSock.setBroadcast(true);
                

            }else{
                tempSock=new DatagramSocket();
                //byte buffer does not neccesary as send method binds a reference parameter everytime
            }

        }catch (UnknownHostException ex) {
            System.err.println(String.format(
                    "unknown ip parameter for InetAddress: %s ->%s",ip, ex));
        
        }catch (SocketException ex) {
            System.err.println(String.format(
                    "Error while initializing Datagram Socket: %s",ex ));
        }

        packetIN=tempIN;
        packetOUT=tempOUT;
        sock=tempSock;
    }

    public void send(byte[] data) throws IOException {

        packetOUT.setData(data);
        
        this.sock.send(packetOUT);

    }

    public byte[] receive() throws IOException{
        
        this.sock.receive(packetIN);

        return Arrays.copyOfRange(packetIN.getData(), 0, packetIN.getLength());
        
    }

    public byte[] receive(int recvLimit) throws IOException{

        packetIN.setLength(recvLimit);

        this.sock.receive(packetIN);

        packetIN.setLength(packetIN.getData().length);

        return Arrays.copyOfRange(packetIN.getData(), 0, recvLimit);
        
    }

    public DatagramSocket getSocket(){
        return sock;
    }

    public byte[] getReceived(){
        
        return Arrays.copyOfRange(packetIN.getData(), 0, packetIN.getLength());
    }

    /**
     * 
     * @return the length of the data received or the length of the receive buffer.
     */
    public int getRecvLen(){
        return packetIN.getLength();
    }

    /**
     * 
     * @return the length of the data to be sent or the length of the send buffer
     */
    public int getSendLen(){
        return packetIN.getLength();
    }
    
}
