/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.halosoft.gui.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author ibrahim
 */
public interface Connectible {
    
    /**
     * initialize sockets or other structures of 'Connectible'
     */
    public void initialize();
    
    /**
     * start sockets or other structures of 'Connectible'
     */
    abstract void start();
    
    /**
     * start sockets or other structures of 'Connectible'
     */
    abstract void stop();
    //---
    abstract String getRemoteIp();
    
    abstract void setRemoteIp(String ip);
    
    abstract int getRemotePort();
    
    abstract void setRemotePort(int port);
    
    abstract Socket getClientSocket();
    
    abstract void setClientSocket(Socket cli);
    
    abstract void setSocketInputStream(DataInputStream in);
    
    abstract DataInputStream getSocketInputStream();
    
    abstract void setSocketOutputStream(DataOutputStream out);
    
    abstract DataOutputStream getSocketOutputStream();
}
