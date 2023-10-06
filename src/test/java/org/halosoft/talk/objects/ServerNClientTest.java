/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.halosoft.talk.objects;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ibrahim
 */
public class ServerNClientTest {
    
    public ServerNClientTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void serverNClient(){
        System.out.println("\tServer And Client communication test");
        String expected="hello from client";
        
        Server s=new Server("0.0.0.0");
        s.start();
        
        Client c=new Client("127.0.0.1");
        c.start();
        
        try {
            c.getSocketOutputStream().writeUTF(expected);
            
            while( Server.clients.get("127.0.0.1") ==null){
                Thread.sleep(100);
            }
            String received=Server.clients.get("127.0.0.1")[0].take();
            
            assertEquals(expected,received);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        c.stop();
        //s.stop();
    }
}
