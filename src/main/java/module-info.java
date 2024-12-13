module com.evolvlabs {
    requires lombok;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.apache.commons.csv;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires org.apache.commons.io;
    requires io.github.kamilszewc.javaansitextcolorizer;
    requires commons.math3;
    requires java.desktop;

    opens com.evolvlabs.enigmamachine to javafx.fxml;
    exports com.evolvlabs.enigmamachine;
    exports com.evolvlabs.enigmabackend;
    exports com.evolvlabs.enigmaDecriptor;
}