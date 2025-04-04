module com.meuprojeto {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.meuprojeto to javafx.fxml;
    exports com.meuprojeto;
}
