/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.math.BigInteger;

/**
 *
 * @author ibrahim
 * 
 * data cypher/decipher object for secure network communication
 */
public class Enigma {
    
    /**
     * encrypts data by given key
     * @param data desired message to cipher
     * @param key magic numbers to cipher given data
     * @return specific format of hexadecimal string
     */
    public static String encrypt(int[] data, long[] key){
        /*SUM(data^e mod n) formula ciphers the ascii message*/
        
        StringBuilder ciphered=new StringBuilder("");

        for(int i=0; i<data.length; i++){

            BigInteger bi=new BigInteger( Integer.toString(data[i] ) );

            bi=bi.modPow(new BigInteger( Long.toString( key[1] ) ),
                    new BigInteger( Long.toString( key[0] ) ) );
            

            ciphered.append(DataManipulator.BigInterToHex(bi) );
        }
        return ciphered.toString();
        
    }
    
    /**
     * decrypts data by given key
     * @param data enigmatic series of numbers
     * @param key magic numbers to decode given data
     * @return specific format of hexadecimal string
     */
    public static String decrypt(int[] data, long[] key){
        /*as c represents encrypted_message: c^d mod n formula
        decyphers the message by ascii table*/
        
        return Enigma.encrypt(data, key);
        
    }
    
    /**
     * encrypts data by given key
     * @param data desired message to cipher
     * @param key magic numbers to cipher given data
     * @return specific format of hexadecimal string
     */
    public static String encrypt(String textData, long[] key){
        /*SUM(data^e mod n) formula ciphers the ascii message*/
        //Encrypts general text message to special hex as (\datalen+data)
        StringBuilder ciphered=new StringBuilder("");
        
        for(int i=0; i<textData.length(); i++){

            BigInteger bi=new BigInteger( 
                    textData.substring(i, i+1).getBytes() );

            bi=bi.modPow(new BigInteger( Long.toString( key[1] ) ),
                    new BigInteger( Long.toString( key[0] ) ) );

            ciphered.append(DataManipulator.BigInterToHex(bi) );
        }

        return ciphered.toString();
        
    }
    
    /**
     * decrypts data by given key
     * @param data enigmatic series of specific formatter hexadecimal string
     * @param key magic numbers to decode given data
     * @return specific format of hexadecimal string
     */
    public static String decrypt(String DataManipulatorHexData, long[] key){
        //Decrypts special hex message to special to string
        StringBuilder deciphered=new StringBuilder("");
        String[] data=DataManipulator.parseHex(DataManipulatorHexData);
        
        for(String d:data){
            
            BigInteger bi=new BigInteger( d, 16 );

            bi=bi.modPow(new BigInteger( Long.toString( key[1] ) ),
                    new BigInteger( Long.toString( key[0] ) ) );
            
            deciphered.append(bi.toString(16));
        }
        return deciphered.toString();
    }
    
    /**
     * encrypts data by given key
     * @param data desired message to cipher
     * @param key magic numbers to cipher given data
     * @return specific format of hexadecimal string
     */
    public static String encrypt(byte[] data, long[] key){
        /*SUM(data^e mod n) formula ciphers the ascii message*/
        
        StringBuilder ciphered=new StringBuilder("");

        for(int i=0; i<data.length; i++){

            BigInteger bi=new BigInteger( Integer.toString(data[i] ) );

            bi=bi.modPow(new BigInteger( Long.toString( key[1] ) ),
                    new BigInteger( Long.toString( key[0] ) ) );
            

            ciphered.append(DataManipulator.BigInterToHex(bi) );
        }
        return ciphered.toString();
        
    }
}
