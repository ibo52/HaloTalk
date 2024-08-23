package org.ibo52.models.net;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.util.Arrays;
import org.ibo52.interfaces.SocketInterface;
import java.util.concurrent.*;
/*
 * RTP based Video client
 */
public class VideoClient extends DatagramSocket implements SocketInterface{

    protected InetAddress remoteIp;
    protected int remotePort;
    protected RTP rtpHeader;
    PacketMonitor monitor;

    protected ExecutorService service;

    public VideoClient(String ip) throws SocketException{

        try {
            monitor=new PacketMonitor();
            
            service=Executors.newSingleThreadExecutor();

            rtpHeader=new RTP();

            remoteIp=InetAddress.getByName(ip);
            remotePort=50005;//v4l2-camera-photo server port

            this.connect(remoteIp, remotePort);

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int send(byte[] data) {
        //return packets as rtp by default
        try{
            return this.rtpHeader.send(this, data);
        
        }catch(PortUnreachableException e){
            System.err.println("Possibly connection drop: "+e.getMessage());
        
        }catch(IOException e){
            System.err.println("Possibly connection error: "+e.getMessage());
        }
        return -1;
    }

    @Override
    public byte[] receive() {

        byte[] recv =null;
        try{
            recv = this.rtpHeader.receive(this);
        
        }catch(PortUnreachableException e){
            System.err.println("Remote connection dropped: "+e.getMessage());
        
        }catch(IOException e){
            System.err.println("Connection error: "+e.getMessage());
        }
        return recv;

    }

    public void start(){
        service.execute(new Runnable() {

            @Override
            public void run() {
                
                startListening();
            }
            
        });
    }

    public void join(){

        while ( !service.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        service.shutdown();

        this.close();
    }
    private void startListening() {

        try {

            //byte[] recv={0};
            while( true /*Integer.compareUnsigned(size, completed)>0*/ ){

                byte[] recv=receive();

                if(recv==null){
                    System.out.println("Connection dropped. Exit");
                    break;
                }
                //if incoming data is not RTP but raw, then it tells us arguments
                else if( recv.length>17 && new String(recv,0,18).equals("SOCK_CLOSE_REQUEST") ){
                    //System.out.println("Remote close request received");
                    break;
                
                }else{
                    Thread t=new Thread(()->{

                        monitor.rearrangeIncomingPacket(recv);
                    });
                    t.setDaemon(true);t.start();
                }
            }
            
            //System.out.println("received RTP header:");
            //System.out.println(RTP.arrayToString(recv));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream getFrame(){

        if(monitor.getStoreSize()==0){
            return null;
        }

        ByteArrayOutputStream frame=new ByteArrayOutputStream();
        byte[] packet= monitor.getNextPacket().toByteArray();
        
        try{
            

            while( !Stream.Type.isEqual(Stream.Type.JPEG.SOF, Arrays.copyOfRange(packet, 16, 18) ) ){
                
                System.out.printf("not JPEG SOF: %01X %01X ",packet[16], packet[17]);
                System.out.println("throw packet #"+RTP.getSequenceNumber(packet));
                
                if( monitor.getStoreSize()>1 ){
                    packet=monitor.getNextPacket().toByteArray();
                    
                }else{
                    Thread.sleep(33);
                }
            }
            
            while( !Stream.Type.isEqual(Stream.Type.JPEG.EOF, Arrays.copyOfRange(packet, packet.length-2, packet.length) ) ){
                
                // TODO FFD9 bulamıyor bu yüzden görüntüyü basamayıp belleği şişiriyor.allttaki if kodu geçici bir test çözümü
                
                if( monitor.getStoreSize()>1 ){

                    frame.write(Arrays.copyOfRange(packet, 16, packet.length));

                    packet=monitor.getNextPacket().toByteArray();
                    
                }else{
                    System.out.println("store empty wwait for recv");
                    Thread.sleep(33);
                }
            }

            frame.write(Arrays.copyOfRange(packet, 16, packet.length));

        }catch (Exception e) {

            e.printStackTrace();System.exit(e.hashCode());
        }
        //System.out.printf("getframe()-> SOF: %01X%01X   EOF: %01X%01X ",frame.toByteArray()[0], frame.toByteArray()[1],
        //frame.toByteArray()[frame.size()-2], frame.toByteArray()[frame.size()-1]);System.out.println();
        return frame;
    }
}
