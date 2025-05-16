/*package com.meuprojeto.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

public class InterfaceSeletor extends Application {

    private ComboBox<String> clienteComboBox = new ComboBox<>();
    private ComboBox<String> anoComboBox = new ComboBox<>();
    private ComboBox<String> mesComboBox = new ComboBox<>();
    private Label caminhoLabel = new Label();
    private File pastaBase;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Selecionar Cliente / Ano / Mês");

        Button escolherPastaButton = new Button("Selecionar pasta base (clientes)");
        escolherPastaButton.setOnAction(e -> escolherPasta(primaryStage));

        clienteComboBox.setPromptText("Selecione o cliente");
        clienteComboBox.setOnAction(e -> atualizarAnos());

        anoComboBox.setPromptText("Selecione o ano");
        anoComboBox.setOnAction(e -> atualizarMeses());

        mesComboBox.setPromptText("Selecione o mês");
        mesComboBox.setOnAction(e -> atualizarCaminho());

        VBox vbox = new VBox(10, escolherPastaButton, clienteComboBox, anoComboBox, mesComboBox, caminhoLabel);
        vbox.setPadding(new Insets(15));

        primaryStage.setScene(new Scene(vbox, 500, 300));
        primaryStage.show();
    }

    private void escolherPasta(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione a pasta 'clientes'");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null && selectedDirectory.isDirectory()) {
            this.pastaBase = selectedDirectory;
            atualizarClientes();
        }
    }

    private void atualizarClientes() {
        clienteComboBox.getItems().clear();
        anoComboBox.getItems().clear();
        mesComboBox.getItems().clear();
        caminhoLabel.setText("");

        File[] clientes = pastaBase.listFiles(File::isDirectory);
        if (clientes != null) {
            Arrays.stream(clientes).map(File::getName).forEach(clienteComboBox.getItems()::add);
        }
    }

    private void atualizarAnos() {
        anoComboBox.getItems().clear();
        mesComboBox.getItems().clear();
        caminhoLabel.setText("");

        File clienteDir = new File(pastaBase, clienteComboBox.getValue());
        File[] anos = clienteDir.listFiles(File::isDirectory);
        if (anos != null) {
            Arrays.stream(anos).map(File::getName).forEach(anoComboBox.getItems()::add);
        }
    }

    private void atualizarMeses() {
        mesComboBox.getItems().clear();
        caminhoLabel.setText("");

        File anoDir = new File(pastaBase, clienteComboBox.getValue() + File.separator + anoComboBox.getValue());
        File[] meses = anoDir.listFiles(File::isDirectory);
        if (meses != null) {
            Arrays.stream(meses).map(File::getName).forEach(mesComboBox.getItems()::add);
        }
    }

    private void atualizarCaminho() {
        if (pastaBase == null || clienteComboBox.getValue() == null || anoComboBox.getValue() == null || mesComboBox.getValue() == null) return;
        String caminho = pastaBase + File.separator + clienteComboBox.getValue()
                + File.separator + anoComboBox.getValue()
                + File.separator + mesComboBox.getValue();
        caminhoLabel.setText("Caminho selecionado: " + caminho);
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/