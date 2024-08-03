package org.ibo52.interfaces;

import java.net.InetAddress;

public interface SocketInterface{
    
    public int send(byte[] data);

    public byte[] receive();

    public InetAddress getInetAddress();

    public int getPort();

}