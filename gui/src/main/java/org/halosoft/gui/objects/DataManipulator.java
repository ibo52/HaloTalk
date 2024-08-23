/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.gui.objects;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.halosoft.gui.App;

/**
 *
 * @author ibrahim
 */
public class DataManipulator {
    
    /**
     * turns value of big integer to hexadecimal string, which defined as
     * (size of next characters + next hex characters).
     * Example Biginteger(254) -> "\02fe"
     * @param bi 
     * @return specific format of hexadecimal string
     */
    public static String BigInterToHex(BigInteger bi){

        int bitLength=bi.bitLength()/4;//calculate half-bytes on hex data
        if (bi.bitLength()%4!=0) {
            bitLength++;
        }

        String hexCipher="\\%02x";//to concat half-byte size(bitLength)
        
        if( bitLength%2 != 0 ){//we have to have even number of hex chars
            bitLength++;
            hexCipher+="0";
        }

        hexCipher+="%x";//to concat hex val(BigInteger)

        return String.format( hexCipher, 
               bitLength , bi );
    }
    
    /**
     * Turns hexadecimal string of big size to BigInteger value
     * @param hex general hexadecimal string. Example: "ffd844d0a3"
     * @return 
     */
    public static BigInteger HexToBigInteger(String hex){
        return new BigInteger(hex, 16);

    }
    
    /**
     * turns array of bytes to hexadecimal string
     * @param data
     * @return 
     */
    public static String byteArrayToHex(byte[] data){
        return new String(data, StandardCharsets.UTF_8);
    }
    
    /**
     * Parses specific format of hexadecimal string to array of
     * general hexadecimal Strings
     * @param data specific format of hexadecimal strings: Example: "\02fe\04aa3e"
     * @return general hexadecimal strings. Example: "\02fe\04aa3e" -> {"fe", "aa3e"}
     */
    public static String[] parseHex(String data){
        List<String> parsed=new ArrayList<String>();
        
        while(data.length()>0){
            int idx=data.indexOf("\\");
            
            if (idx==-1) {
                break;
            }
            //next two chars defines length of hex
            int hexLength=Integer.decode( "0x"+data.substring(idx+1, idx+3) );
            idx+=3;
            parsed.add(data.substring(idx,idx+hexLength) );
            
            data=data.substring(idx+hexLength, data.length());
            
        }
        return parsed.toArray(new String[0]);
    }
    
    /**
     * turns every 2 chars of hexadecimal string to UTF-8 string, then
     * returns decoded message
     * @param hexMessage message to decode Example: "48454c4c4f"
     * @return UTF8 decoded message Example: "48454c4c4f" -> "HELLO"
     */
    public static String HexToString(String hexMessage){
        //turn all 2 string letter to byte of chars
        
        StringBuilder decode=new StringBuilder("");
        
        for (int i = 0; i < hexMessage.length(); i+=2) {
            
            String s=hexMessage.substring(i, i+2);

            try {
                decode.append(  new String
                (new byte[]{ Byte.valueOf(s, 16).byteValue() }, "UTF-8")  );
            } catch (UnsupportedEncodingException ex) {
                App.logger.log(Level.SEVERE, 
                        "Encoding not supported",ex);
            }
        }
        return decode.toString();
    }
}
