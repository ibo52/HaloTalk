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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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
import org.halosoft.talk.objects.ObservableUser;

/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserContactController implements Controllable,
        Initializable, Animateable{
    
    HostSelectorController parentController;
    private ObservableUser userData;
    
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
    @FXML
    private ImageView undoButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        this.rootPane.setTranslateY(Long.MAX_VALUE);//keep out of screen for start animation
        this.rootPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                UserContactController.this.startAnimation();
                UserContactController.this.rootPane.heightProperty()
                        .removeListener(this);
            }
        });
        
        
        this.rootPane.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode().equals(KeyCode.ESCAPE)) {
                    rootPane.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    stopAnimation();
                }
            }
        });
        
        userData=new ObservableUser();
        this.userID.textProperty().bind(this.userData.getNameProperty()
                .concat(" ").concat(this.userData.getSurNameProperty()));
        this.userHostName.textProperty().bind(this.userData.getHostNameProperty());
        this.userStatusText.textProperty().bind(this.userData.getStatusMessageProperty());
    }
    
    public void setStatus(int status){
        this.userData.setStatus(status);
        
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

    public void setUserData(ObservableUser userData){
        this.userData.setContents(userData);
    }
    
    public ObservableUser getUserData(){
        return this.userData;
    }
    
    @FXML
    private void sendMessageButtonMouseClicked(MouseEvent event) {
        
        //get host selector rootpane and its controller
        parentController.bringChatScreen(this.userData);
        
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
        
            final Duration duration=Duration.millis(300);
            this.rootPane.setVisible(true);
            //***---***
            TranslateTransition tt=new TranslateTransition();
            tt.setDuration(duration);
            
            tt.setFromY(-this.rootPane.getHeight());
            tt.setToY(0);
            //-------
            FadeTransition ft=new FadeTransition();
            ft.setDuration(duration.multiply(1.2));
            ft.setFromValue(0);
            ft.setToValue(1);
            
            ParallelTransition pt=new ParallelTransition(this.rootPane,
                    tt,ft);
            pt.play();
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

    @FXML
    private void undoButtonMouseClicked(MouseEvent event) {
        this.undoButton.setDisable(true);
        this.stopAnimation();
    }
    
    @Override
    public void setParentController(Object controller) {
        this.parentController=(HostSelectorController) controller;
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
