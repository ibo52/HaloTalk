module org.halosoft.talk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.logging;

    opens org.halosoft.talk.controllers to javafx.fxml;
    opens org.halosoft.talk.controllers.setting to javafx.fxml;
    opens org.halosoft.talk to javafx.fxml;
    exports org.halosoft.talk;
}
