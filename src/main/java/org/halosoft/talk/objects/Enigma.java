/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

/**
 *
 * @author ibrahim
 * 
 * data Cypher object for secure network communication
 */
public class Enigma {
    
    public Enigma(){
        
    }
    
    public static byte[] encrypt(byte[] data, int[] key){
        /*SUM(data^e mod n) formula ciphers the ascii message*/
        
        byte[] ciphered=new byte[data.length];
        
        for(int i=0; i<data.length; i++){
            ciphered[i]=(byte) (Math.pow( data[i] , key[1]) % key[0]);
        }
        
        return ciphered;
        
    }
    
    public static byte[] decrypt(byte[] data, int[] key){
        /*as c represents encrypted_message: c^d mod n formula
        decyphers the message by ascii table*/
        
        byte[] deciphered=new byte[data.length];
        
        for(int i=0; i<data.length; i++){
            deciphered[i]=(byte) (Math.pow( data[i] , key[1]) % key[0]);
        }
        
        return deciphered;
        
    }
}
