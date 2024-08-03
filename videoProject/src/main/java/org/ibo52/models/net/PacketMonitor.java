package org.ibo52.models.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.*;
public class PacketMonitor {

    private List<ByteArrayOutputStream> store;
    
    long losses;
    long transmitted;

    public PacketMonitor(){
        losses=0;
        transmitted=0;
        
        store=Collections.synchronizedList( new LinkedList<ByteArrayOutputStream>());
        
    }

    public void rearrangeIncomingPacket(ByteArrayOutputStream packet){
        
        synchronized(store){
            int packetSeqNum=RTP.getSequenceNumber(packet.toByteArray());

            if(store.size()>0){

                int diff=packetSeqNum-RTP.getSequenceNumber(store.get(store.size()-1).toByteArray());

                losses+=diff;

                if(diff>1 ){
                    
                    System.out.println("packet losses:");
                    for (int i = 0; i < diff; i++) {
                        System.out.print(i+",");
                    }System.out.println();
                }
            }
            int i=0;
            for (i = store.size(); i>0; i--) {

                int currSeqNum=RTP.getSequenceNumber(store.get(i-1).toByteArray());

                if(currSeqNum>packetSeqNum)
                    continue;
            }
            store.add(i, packet);

            if( store.size()>128)
                store.remove(0);
        }
    }

    public void rearrangeIncomingPacket(byte[] packet){
        
        synchronized(store){
            int packetSeqNum=RTP.getSequenceNumber(packet);
            int packetStoreIdx=0;

            if(store.size()>0){

                int diff=packetSeqNum-RTP.getSequenceNumber(store.get(store.size()-1).toByteArray());
                losses+=diff;

                if(diff >1 ){
                    
                    System.out.println("packet losses:"+diff+"  stoe size:"+store.size());
                }
            
                
                for (packetStoreIdx = store.size(); packetStoreIdx>0; packetStoreIdx--) {
                    
                    int currSeqNum=RTP.getSequenceNumber(store.get(packetStoreIdx-1).toByteArray());

                    if(currSeqNum>packetSeqNum)
                        continue;
                    else
                        break;
                }
            }
            var p=new ByteArrayOutputStream();
            try {
                p.write(packet);
            } catch (IOException e) {
                
                e.printStackTrace();
            }
            
            store.add(packetStoreIdx, p);

            if( store.size()>128)
                store.remove(0);
        }
    }
    public ByteArrayOutputStream getNextPacket(){
       
        synchronized(store){

            return store.remove(0);
        }
    }

    public int getStoreSize(){

        synchronized(store){
            return store.size();
        }
    }
}
