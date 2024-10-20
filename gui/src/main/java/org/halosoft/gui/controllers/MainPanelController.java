/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class MainPanelController implements Initializable {

    //private final BroadcastClient videoClient;
    //private final VideoBroadcaster videoServer;

    @FXML
    private Button endCallButton;
    @FXML
    private Button closeViewButton;
    @FXML
    private ImageView imageBox;
    @FXML
    private BorderPane rootPane;
    @FXML
    private ScrollPane imageBoxLayout;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button showTabPane;

    public MainPanelController() {

        //this.videoClient=new BroadcastClient("",50003);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        imageBox.fitWidthProperty().bind(imageBoxLayout.widthProperty());
        imageBox.fitHeightProperty().bind(imageBoxLayout.heightProperty());
        imageBox.setImage( new Image( this.getClass().getClassLoader().getResource("images/logo-circle-512x512.png").toString() ) );

        showTabPane.setOnMouseClicked(event->{
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration( Duration.millis(300) );
            slide.setNode(tabPane);

            if (tabPane.getBoundsInParent().getMinX() >= rootPane.getWidth()) {
                //show by sliding to left
                slide.setToX(0);
                tabPane.setPrefWidth(-1);

            }
            else{//hide by sliding to right
                slide.setToX(tabPane.getWidth());
                tabPane.setPrefWidth(0);

            }
            slide.play();
        });
    }

    /**
     * get image buffer by client, then draw to canvas
     */
    /*
    private void getImageData(){
        this.videoClient.start("STREAM");//get video data from remote

        BufferedImage buffImg=null;//ImageIO.read(new ByteArrayInputStream(this.videoClient.getBuffer()));

        WritableImage wImg=new WritableImage(buffImg.getWidth(), buffImg.getHeight());
        PixelWriter pw=wImg.getPixelWriter();

        //write bufferedImage to javafx image
        for (int w = 0; w < buffImg.getWidth(); w++) {
            for (int h = 0; h < buffImg.getHeight(); h++) {
                pw.setArgb(w, h, buffImg.getRGB(w, h));
            }
        }
        imageBox.setImage(wImg);
    }*/

}
