module org.ibo52 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.ibo52 to javafx.fxml;
    opens org.ibo52.controllers to javafx.fxml;
    exports org.ibo52;
}
