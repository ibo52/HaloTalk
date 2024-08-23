package org.ibo52.models.net;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Date;

public class RTP {
    static byte PAYLOAD_TYPE_JPEG=26;

    protected byte VPXCC;//'V'ersion: 2 bits, 'P'adding:1 bit, e'X'tension: 1 bit, 'C'SRC count: 4 bits
    protected byte MPT;//'M'arker: 1 bit, 'P'ayload 'T'ype: 7 bits
    protected short sequence_number;//should be randomized to make known-plaintext attacks on S(ecure)RTP more difficult
    protected int timestamp;//Used by the receiver to play back the received samples at appropriate time and interval
    protected int ssrc;//Synchronization source identiﬁer uniquely identiﬁes the source of a stream
    protected int csrc;//Contributing source IDs enumerate contributing sources to a stream that has been generated from multiple sources

    public RTP(){
        sequence_number=(short)(new Date().getTime());

        //first 2 bit for the RTP version(curernt version 2)
        //padding:0, extension:0, csrc count:0
        //thus 1000 0000 as binary representation(0x80H)
        VPXCC=(byte)128;
        //skip P, which indicates presence of a padding
        //skip X, which indicates presence of an extension

        //payload 26: JPEG image format to send
        //reference: https://en.wikipedia.org/wiki/RTP_payload_formats
        MPT=PAYLOAD_TYPE_JPEG;

        timestamp=0;//Used by the receiver to play back the received samples at appropriate time and interval
        ssrc=0;//Synchronization source identiﬁer uniquely identiﬁes the source of a stream
        csrc=0;
    }

    private byte[] getRTPHeader(){
        return new byte[]{
            this.VPXCC,
            this.MPT,

            (byte)(this.sequence_number<<8 & 0xff),
            (byte)(this.sequence_number & 0xff),

            (byte)(this.timestamp<<24 & 0xff),
            (byte)(this.timestamp<<16 & 0xff),
            (byte)(this.timestamp<<8 & 0xff),
            (byte)(this.timestamp & 0xff),

            (byte)(this.csrc<<24 & 0xff),
            (byte)(this.csrc<<16 & 0xff),
            (byte)(this.csrc<<8 & 0xff),
            (byte)(this.csrc & 0xff),

            (byte)(this.ssrc<<24 & 0xff),
            (byte)(this.ssrc<<16 & 0xff),
            (byte)(this.ssrc<<8 & 0xff),
            (byte)(this.ssrc & 0xff),
        };
    }

    public int send(DatagramSocket socket, byte[] data, byte payloadType){

        //payload type: change last 7 bits of MPT, preserve first bit
        this.MPT= (byte)( (this.MPT & 128) | (payloadType & 127) );

        byte[] buffer=new byte[data.length+16];//16 bytes reserved for rtp packet header + data

        //copy RTP header(16 bytes)
        System.arraycopy(this.getRTPHeader(), 0, buffer, 0, 16);

        //copy data
        System.arraycopy(data, 0, buffer, 16, data.length);

        //prepare daatgram packet
        DatagramPacket packet =new DatagramPacket(buffer, buffer.length);

        try {
            socket.send(packet);

            this.sequence_number++;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet.getLength();
    }

    public int send(DatagramSocket socket, byte[] data) throws IOException{

        byte[] buffer=new byte[data.length+16];//16 bytes reserved for rtp packet header + data

        //copy RTP header(16 bytes)
        System.arraycopy(this.getRTPHeader(), 0, buffer, 0, 16);

        //copy data
        System.arraycopy(data, 0, buffer, 16, data.length);

        //prepare daatgram packet
        DatagramPacket packet =new DatagramPacket(buffer, buffer.length);

        socket.send(packet);

        this.sequence_number++;

        return packet.getLength();
    }

    public byte[] receive(DatagramSocket socket) throws IOException{

        //throw new UnsupportedOperationException("not implemented yet");
        //maximum size of an incoming UDP packet
        DatagramPacket packet=new DatagramPacket(new byte[65535], 65535);
        
        socket.receive(packet);

        return Arrays.copyOf(packet.getData(), packet.getLength());

    }

    public String toString(){

        return String.format(String.join(""
               ,"\tRTP Header\n"
               ,"\t- 0th byte ----------------\n"
               ,"\tVersion:            %25d\n"
              , "\tPadding:            %25s\n"
              , "\tExtension:          %25s\n"
               ,"\tContributors count: %25d\n"
               ,"\tMarker:             %25s\n"
               ,"\tPayload Type:       %25s\n"
              , "\tSequence number:    %25s\n"
              , "\t- 4th byte ----------------\n"
              , "\ttimestamp:               %20s\n"
             ,  "\tSync. source id(you):    %20s\n"
             ,  "\tContributing source IDs: %20s\n")
               ,VPXCC>>6 & 0x2,					    //version
               (VPXCC>>5 & 0x1)==0x1? "yes":"no",	//pad
               (VPXCC>>4 & 0x1)==0x1? "yes":"no",	//eXtension
               VPXCC & 0x0f,			    		//CC

               (MPT>>7&0x1)==0x1? "yes":"no",		//marker
               (MPT & 0x7f)==PAYLOAD_TYPE_JPEG? "JPEG":"unknwn/othr",		//payload type
               Integer.toUnsignedString( Short.toUnsignedInt(sequence_number) ),

               Integer.toUnsignedString( timestamp ),
               Integer.toUnsignedString( ssrc ),
               Integer.toUnsignedString( csrc )
               );
    }

    public static String arrayToString(byte[] data){

        if(data.length<16){
            System.out.println("Data have to be minimum 16 bytes long (which is sizeof RTP header) !");
            return "";
        }

        return String.format(String.join(""
               ,"\tRTP Header\n"
               ,"\t- 0th byte ----------------\n"
               ,"\tVersion:            %25d\n"
              , "\tPadding:            %25s\n"
              , "\tExtension:          %25s\n"
               ,"\tContributors count: %25d\n"
               ,"\tMarker:             %25s\n"
               ,"\tPayload Type:       %25s\n"
              , "\tSequence number:    %25s\n"
              , "\t- 4th byte ----------------\n"
              , "\ttimestamp:               %20s\n"
             ,  "\tSync. source id(you):    %20s\n"
             ,  "\tContributing source IDs: %20s\n")
               ,data[0]>>6 & 0x2,					    //version
               (data[0]>>5 & 0x1)==0x1? "yes":"no",	//pad
               (data[0]>>4 & 0x1)==0x1? "yes":"no",	//eXtension
               data[0] & 0x0f,			    		//CC

               (data[1]>>7 & 0x1)==0x1? "yes":"no",		//marker
               (data[1] & 0x7f)==PAYLOAD_TYPE_JPEG? "JPEG":"unknwn/othr",		//payload type
               Integer.toUnsignedString( (data[3] & 0xFF)<<8 | (data[2] & 0xFF) ),//sequence num

               Integer.toUnsignedString( (data[4] & 0xFF)<<24 | (data[5] & 0xFF)<<16
                | (data[6] & 0xFF)<<8 | (data[7] & 0xFF) ) ,//timestamp
               Integer.toUnsignedString( (data[8] & 0xFF)<<24 | (data[9] & 0xFF)<<16
                | (data[10] & 0xFF)<<8 | (data[11] & 0xFF) ) , //ssrc
               Integer.toUnsignedString( (data[12] & 0xFF)<<24 | (data[13] & 0xFF)<<16
                | (data[14] & 0xFF)<<8 | (data[15] & 0xFF) )
               );
    }

    public static int getSequenceNumber(byte[] rtpData){
        
        if ( rtpData.length>=16){

            int seqNum=(rtpData[3] & 0xFF)<<8 | (rtpData[2] & 0xFF);

            return seqNum;
        }
        return -1;
    }

}
