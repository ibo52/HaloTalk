package org.halosoft.gui.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StageChanger {

    public static Stage change(Stage predecessor, Parent view){

        //create a new stage
        return StageChanger.change(predecessor, view, 400, 300);
    }

    public static Stage change(Stage predecessor, Parent view, int width, int height){

        //create a new stage
        Stage succesor = new Stage();
        Scene s=new Scene(view, width>0? width:400, height>0? height:300);
        succesor.setScene(  s );

        //handler will restore predecessor stage when succesor stage closed
        succesor.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event)->{

            predecessor.show();
        });

        predecessor.hide();
        succesor.show();

        return succesor;
    }
}
