module org.halosoft.talk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens org.halosoft.talk to javafx.fxml;
    exports org.halosoft.talk;
}
