package org.halosoft.gui.interfaces;

import java.net.InetAddress;

public interface SocketInterface{
    
    public int send(byte[] data);

    public byte[] receive();

    public InetAddress getInetAddress();

    public int getPort();

}