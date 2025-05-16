module org.quantum.solutions.quantumsolutions {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens org.quantum.solutions.quantumsolutions to javafx.fxml;
    exports org.quantum.solutions.quantumsolutions;
    exports org.quantum.solutions.quantumsolutions.controller;
    opens org.quantum.solutions.quantumsolutions.controller to javafx.fxml;
    exports org.quantum.solutions.quantumsolutions.model;
    opens org.quantum.solutions.quantumsolutions.model to javafx.fxml;
    exports org.quantum.solutions.quantumsolutions.service;
    opens org.quantum.solutions.quantumsolutions.service to javafx.fxml;
}