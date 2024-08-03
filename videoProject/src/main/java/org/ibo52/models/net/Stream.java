package org.ibo52.models.net;
import java.io.*;   


public class Stream {

    public static class StreamBuffer{
        ByteArrayOutputStream data;//to store bytes
        boolean isComplete;// to indicate if data transmission completed

        public StreamBuffer(){
            this.data=new ByteArrayOutputStream();
            this.isComplete=false;
        }

        public StreamBuffer(ByteArrayOutputStream s){
            this.data=s;
            this.isComplete=false;
        }

        public void setComplete(boolean val){
            this.isComplete=val;
        }
        public boolean getComplete(){
            return isComplete;
        }

        public ByteArrayOutputStream getData(){
            return this.data;
        }

    }

    public static class Type{

        public class JPEG{
            public static final int SOF=0xFFD8;
            public static final int EOF=0xFFD9;
        }

        public static boolean isEqual(int type, byte[] data){
            
        int dataValue=0;

        for (int i = 0;i< data.length; i++) {

            dataValue|= (data[data.length-1-i] & 0xFF)<<i*8;
        }

        return Integer.compare(dataValue, type)==0;
    }
}
    
}
