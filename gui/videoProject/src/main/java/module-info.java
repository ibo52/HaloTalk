module org.halosoft.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    
    opens org.halosoft.gui to javafx.fxml;
    opens org.halosoft.gui.controllers to javafx.fxml;
    exports org.halosoft.gui;
}
