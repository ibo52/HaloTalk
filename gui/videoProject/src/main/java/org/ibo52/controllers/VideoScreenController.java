package org.ibo52.controllers;
import java.net.DatagramPacket;

import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ibo52.models.net.VideoClient;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.*;
public class VideoScreenController implements Initializable {

    @SuppressWarnings("unused")
    private ExecutorService service;

    private VideoClient videoClient;

    @FXML
    private HBox CallButtonsPane;

    @FXML
    private Region closeVideoButton;

    @FXML
    private Button endCallButton;

    @FXML
    private Button hideVideoButton;

    @FXML
    private HBox imageContainerPane;

    @FXML
    private ImageView imagePane;

    @FXML
    private Button muteSoundButton;

    @FXML
    private StackPane root;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        this.imagePane.fitWidthProperty().bind(this.root.widthProperty());
        this.imagePane.fitHeightProperty().bind(this.root.heightProperty());

        service=Executors.newFixedThreadPool(2);

        CallButtonsPane.setOpacity(0);

        this.CallButtonsPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e->{
            toggleCallButtonsPane(300, true);
        });
        this.CallButtonsPane.addEventHandler(MouseEvent.MOUSE_EXITED, e->{
            toggleCallButtonsPane(300, false);
        });

        try {
            videoClient=new VideoClient("localhost");

            Thread t=frameListenerThread();
            t.start();
        } catch (SocketException e1) {
            e1.printStackTrace();
            System.exit(e1.hashCode());
        }

        this.endCallButton.setOnMouseClicked( event->{

            this.videoClient.send("SOCK_CLOSE_REQUEST".getBytes() );
            this.videoClient.stop();
            
            Platform.exit();
        });
    }

    private Thread frameListenerThread(){
        Thread t=new Thread(()->{

            String msg="hellow from javaFX cli";
            try {
                videoClient.send(new DatagramPacket(msg.getBytes(), msg.length()));
            } catch (IOException e1) {

                e1.printStackTrace();
            }

            videoClient.start();

            while ( !Thread.currentThread().isInterrupted() ) {

                ByteArrayOutputStream frame=videoClient.getFrame();

                while ( frame==null ) {
                    try {

                        Thread.sleep(1);

                        frame=videoClient.getFrame();

                    } catch (InterruptedException e1) {

                        e1.printStackTrace();
                    }
                }
                try {
                //System.out.println("render incoming phase  ");
                //System.out.println("buffer render: leng:"+frame.size());
                renderImage( new ByteArrayInputStream(frame.toByteArray()) );

                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        t.setDaemon(true);
        return t;
    }

    public void toggleCallButtonsPane(double duration, boolean show){

        FadeTransition fade=new FadeTransition(Duration.millis(duration));

        fade.setFromValue(CallButtonsPane.getOpacity());
        fade.setToValue(show? 1:0);
        fade.setNode(this.CallButtonsPane);

        fade.play();
    }


    private void renderImage(InputStream stream){

        try {

            this.imagePane.setImage(new Image(stream));
                        
        } catch (Exception e) {

            e.printStackTrace();
            System.exit(0);
        }
    }

}
