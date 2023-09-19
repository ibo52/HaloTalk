/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.halosoft.talk.interfaces.Animateable;
import org.halosoft.talk.interfaces.Controllable;
import org.halosoft.talk.objects.userObject;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserContactController extends userObject implements Initializable,
        Animateable, Controllable{
    
    HostSelectorController parentController;
    
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox userImageBox;
    @FXML
    private ImageView userImage;
    @FXML
    private Label userID;
    @FXML
    private Label userStatusText;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Label userHostName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.rootPane.addEventHandler(KeyEvent.KEY_PRESSED,
                (KeyEvent t) -> {
            if (t.getCode().equals(KeyCode.ESCAPE)) {
                stopAnimation();
            }
        });
        
        this.rootPane.setVisible(false);
        this.startAnimation();
    }
    
    @Override
    public void setStatus(int status){
        super.setStatus(status);
        
        String statusStyle="-fx-background-color: %s; -fx-background-radius: 30% 30%;";
        
        Tooltip ttip=new Tooltip();
        
        switch(status){
            case 0:
                this.userImageBox.setStyle(statusStyle.replace("%s","gray"));
                ttip.setText("Offline");
                break;
                
            case 1:
                this.userImageBox.setStyle(statusStyle.replace("%s","red"));
                ttip.setText("Busy");
                break;
                
            case 2:
                this.userImageBox.setStyle(statusStyle.replace("%s","green"));
                ttip.setText("Online");
                break;
        }
        Tooltip.install(this.userImageBox, ttip);
   
    }
    @Override
    public void setContents(userObject userData){
        super.setContents(userData);

        this.userID.setText(this.getName());
        
        if ( !this.getSurName().equals("*") ) {
            
            this.userID.setText(this.userID.getText()+" "+this.getSurName());
        }
        
        this.userHostName.setText(this.getHostName());
        this.userImage.setImage(this.getImage());
        this.userStatusText.setText(this.getStatusMessage());
        this.setStatus(this.getStatus());
    }
  
    @Override
    public void setImage(Image img){
        super.setImage(img);
        this.userImage.setImage(img);
    }
    @Override
    public void setStatusMessage(String status){
        super.setStatusMessage(status);
        this.userStatusText.setText(status);
    }
    
    @FXML
    private void sendMessageButtonMouseClicked(MouseEvent event) {
        
        //get host selector rootpane and its controller
        System.out.println("->"+this.parentController);
        parentController.bringChatScreen(this);
        
        ScaleTransition st=new ScaleTransition();
        st.setDuration(Duration.millis(300));
        st.setNode(this.rootPane);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(0);
        st.setToY(0);
        st.setOnFinished((ActionEvent t) -> {
            remove();
        });
        
        st.play();
    }

    @Override
    public void startAnimation() {
        
        Platform.runLater( ()->{
            final Duration duration=Duration.millis(300);
            this.rootPane.setVisible(true);
            //***---***
            TranslateTransition tt=new TranslateTransition();
            tt.setDuration(duration);

            int height=(int) (this.rootPane.getHeight()<=0?
                    this.rootPane.getScene().getHeight():this.rootPane.getScene().getHeight());

            tt.setFromY(-height);
            tt.setToY(0);
            //-------
            FadeTransition ft=new FadeTransition();
            ft.setDuration(duration.multiply(1.2));
            ft.setFromValue(0);
            ft.setToValue(1);
            
            ParallelTransition pt=new ParallelTransition(this.rootPane,
                    tt,ft);
            pt.play();
        });
    }

    @Override
    public void stopAnimation() {
        
        final Duration duration=Duration.millis(300);
        //---Translate effect
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(duration);

        int height=(int) (this.rootPane.getHeight()<=0?
                this.rootPane.getScene().getHeight():this.rootPane.getScene().getHeight());

        tt.setFromY(0);
        tt.setToY(height);
        //---Fade effect
        FadeTransition ft=new FadeTransition();
        ft.setDuration(duration);
        ft.setFromValue(1);
        ft.setToValue(0);

        ParallelTransition pt=new ParallelTransition(this.rootPane,
                tt,ft);

        pt.setOnFinished((ActionEvent t) -> {
        remove();
        });
        pt.play();
    }
    
    @Override
    public void setParentController(Object ctrlr){
        this.parentController=(HostSelectorController) ctrlr;
    }

    @Override
    public Object getParentController() {
        return this.parentController;
    }

    @Override
    public void remove() {
        ((Pane)this.rootPane.getParent()).getChildren().remove(this.rootPane);
    }
}
