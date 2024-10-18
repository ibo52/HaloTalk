module org.halosoft.gui {
    
    requires java.base;
    requires java.desktop;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive java.logging;
    requires org.halosoft.notifications;
    requires org.json;

    requires org.halosoft.database;
    requires javafx.graphics;

    opens org.halosoft.gui.controllers to javafx.fxml;
    opens org.halosoft.gui.controllers.setting to javafx.fxml;
    opens org.halosoft.gui to javafx.fxml;
    opens org.halosoft.gui.adapters to javafx.fxml; 
    exports org.halosoft.gui;
}
