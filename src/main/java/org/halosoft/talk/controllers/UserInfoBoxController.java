/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.halosoft.talk.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.halosoft.talk.App;
import org.halosoft.talk.interfaces.Animateable;
import org.halosoft.talk.interfaces.Controllable;
import org.halosoft.talk.objects.BroadcastClient;
import org.halosoft.talk.objects.ObservableUser;
/**
 * FXML Controller class
 *
 * @author ibrahim
 */
public class UserInfoBoxController implements Controllable,
        Initializable, Animateable{

    private HostSelectorController parentController;
    private ObservableUser userData;
    
    @FXML
    private ImageView userImage;
    @FXML
    private Label userID;
    @FXML
    private Label lastSeenLabel;
    @FXML
    private Label unreadMessageCountLabel;
    @FXML
    private VBox userImageBox;
    @FXML
    private Label customStatusLabel;
    @FXML
    private BorderPane rootPane;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.userData=new ObservableUser();
        this.userID.textProperty().bind(this.userData.getHostNameProperty());
        this.customStatusLabel.textProperty().bind(this.userData.getStatusMessageProperty());
        
        setStatus(this.userData.getStatus());
        
        //start animation when width>0
        this.rootPane.setTranslateX(Long.MAX_VALUE);//keep out of screen for start animation
        this.rootPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override  
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                UserInfoBoxController.this.startAnimation();
                UserInfoBoxController.this.rootPane.widthProperty()
                        .removeListener(this);
            }
        });
        // TODO
        
        this.userUpdater();
    }   
    
    /**
     * check for remote user status(on/off line)
     */
    private void userUpdater(){
        ScheduledService checkRemoteStatusService=new ScheduledService<Void>(){
            @Override
            protected Task<Void> createTask() {
                
                Task task=new Task<Void>(){
                    @Override
                    protected Void call() throws Exception {

                        BroadcastClient cli=new BroadcastClient(userData.getID());
                        cli.start("STAT");

                        String status=new String(cli.getBuffer(), 0, 
                                cli.getBufferLength());

                        if (status.equals("NO_RESPONSE")) {
                            setStatus(0);
                        }else{
                            setStatus(Integer.valueOf(status));
                        }
                        return null;
                    }

                };
                return task;
            }
            
        };
        
        checkRemoteStatusService.setPeriod(Duration.seconds(3));
        checkRemoteStatusService.start();
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
        ttip.setText(ttip.getText()+":"+this.userData.getName()
                +" "+this.userData.getSurName());
        Tooltip.install(this.rootPane, ttip);
        //Tooltip.install(this.userImageBox, ttip);
        
        
    }
    
    public void setUserData(ObservableUser data){
        this.userData.setContents(data);
    }
    public ObservableUser getUserData(){
        return this.userData;
    }
    
    private void showUserInfoDetails(){
        
        try {
            Parent imageDetails= App.loadFXML("view/imageDetails");
            ImageDetailsController imageDetailsController=(ImageDetailsController) imageDetails.getUserData();
            
            //set imageView and add to left stackpane
            StackPane leftStackPane=parentController.getLeftStackPane();
            
            //set max size of child at parent
            imageDetailsController.initRootProperty(leftStackPane.widthProperty().multiply(0.8));
            
            imageDetailsController.setUserData(this.userData);
            
            //remove all components on stackpane except first
            Node n=leftStackPane.getChildren().get(0);
            leftStackPane.getChildren().clear();
            leftStackPane.getChildren().add(n);
            
            imageDetailsController.setParentController(parentController);
            leftStackPane.getChildren().add(imageDetails);
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void rootPaneMouseClicked(MouseEvent event) {
        
        //get hostselector rootpane and its controller
        parentController.bringChatScreen( this.userData );
    }

    @FXML
    private void userImageBoxMouseClicked(MouseEvent event) {
        this.showUserInfoDetails();
        event.consume();
    }  

    @Override
    public void startAnimation() {

            TranslateTransition tt=new TranslateTransition();
            
            tt.setDuration(Duration.millis(300));
            tt.setNode(this.rootPane);

            tt.setFromX(-this.rootPane.getWidth());
            tt.setToX(0);
            tt.play();

    }

    @Override
    public void stopAnimation() {
        TranslateTransition tt=new TranslateTransition();
        tt.setDuration(Duration.millis(300));
        tt.setNode(this.rootPane);

        tt.setFromX(0);
        tt.setToX(this.rootPane.getWidth());
        
        tt.setOnFinished((ActionEvent t) -> {
            this.remove();
        });

        tt.play();
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
