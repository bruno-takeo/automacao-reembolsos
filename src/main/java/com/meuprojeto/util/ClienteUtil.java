package com.meuprojeto.util;

import com.meuprojeto.model.ClienteReembolso;

import javax.swing.*;
import java.util.List;

public class ClienteUtil {

    public static ClienteReembolso selecionarCliente(JFrame parent) {
        List<ClienteReembolso> clientes = ClienteReembolsoUtil.carregarClientes();

        if (clientes == null || clientes.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Nenhum cliente cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        JComboBox<ClienteReembolso> comboBox = new JComboBox<>(clientes.toArray(new ClienteReembolso[0]));
        comboBox.setSelectedIndex(0); // Seleciona o primeiro por padrão (opcional)

        int resultado = JOptionPane.showConfirmDialog(
                parent,
                comboBox,
                "Selecione um cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            return (ClienteReembolso) comboBox.getSelectedItem();
        }

        return null;
    }
}
