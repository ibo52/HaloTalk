/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.halosoft.talk.objects;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ibrahim
 */
public class DataManipulator {
    
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
    
    public static String byteArrayToHex(byte[] data){
        String dec=new String(data, StandardCharsets.UTF_8);
        
        
        
        return ";";
    }
    
    public static String[] parseHex(String data){
        List<String> parsed=new ArrayList();
        System.out.println("data is:"+data);
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
}
