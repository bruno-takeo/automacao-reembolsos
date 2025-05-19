package com.meuprojeto.ui;

import com.meuprojeto.model.ClienteReembolso;
import com.meuprojeto.util.ClienteReembolsoUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CadastroClienteFrame extends JFrame {

    private JTextField txtNomeEmpresa;
    private JTextField txtDiretorioBase;
    private JButton btnSelecionarDiretorio;
    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnFechar;
    private JList<String> listaClientes;
    private DefaultListModel<String> listModel;

    private List<ClienteReembolso> clientes;
    private JFrame janelaPai;

    public CadastroClienteFrame(JFrame janelaPai) {
        this.janelaPai = janelaPai;
        setTitle("Cadastro de Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
        carregarClientesNaLista();

        // Exibe a janela pai novamente ao fechar esta
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (janelaPai != null) {
                    janelaPai.setVisible(true);
                }
            }
        });
    }

    private void initComponents() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(painelPrincipal);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome da empresa
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Nome da Empresa:"), gbc);

        txtNomeEmpresa = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        painelFormulario.add(txtNomeEmpresa, gbc);

        // Diretório base
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Diretório Base:"), gbc);

        txtDiretorioBase = new JTextField();
        txtDiretorioBase.setEditable(false);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painelFormulario.add(txtDiretorioBase, gbc);

        btnSelecionarDiretorio = new JButton("Selecionar...");
        gbc.gridx = 2;
        gbc.weightx = 0;
        painelFormulario.add(btnSelecionarDiretorio, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");
        btnFechar = new JButton("Fechar");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnFechar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        listModel = new DefaultListModel<>();
        listaClientes = new JList<>(listModel);
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPaneLista = new JScrollPane(listaClientes);
        scrollPaneLista.setPreferredSize(new Dimension(200, 0));
        painelPrincipal.add(scrollPaneLista, BorderLayout.WEST);

        btnSelecionarDiretorio.addActionListener(e -> selecionarDiretorio());
        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnFechar.addActionListener(e -> dispose());

        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarClienteSelecionado();
            }
        });
    }

    private void selecionarDiretorio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = chooser.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            txtDiretorioBase.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void carregarClientesNaLista() {
        clientes = ClienteReembolsoUtil.carregarClientes();
        listModel.clear();
        for (ClienteReembolso c : clientes) {
            listModel.addElement(c.getNomeEmpresa());
        }
    }

    private void limparCampos() {
        listaClientes.clearSelection();
        txtNomeEmpresa.setText("");
        txtDiretorioBase.setText("");
    }

    private void carregarClienteSelecionado() {
        int idx = listaClientes.getSelectedIndex();
        if (idx >= 0 && idx < clientes.size()) {
            ClienteReembolso c = clientes.get(idx);
            txtNomeEmpresa.setText(c.getNomeEmpresa());
            txtDiretorioBase.setText(c.getCaminhoBase());
        }
    }

    private void salvarCliente() {
        String nome = txtNomeEmpresa.getText().trim();
        String dir = txtDiretorioBase.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da empresa.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o diretório base.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ClienteReembolso cliente = new ClienteReembolso(nome, dir);
        ClienteReembolsoUtil.adicionarOuAtualizarCliente(cliente);
        carregarClientesNaLista();
        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        limparCampos();
    }

    private void excluirCliente() {
        int idx = listaClientes.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nome = listModel.get(idx);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma exclusão do cliente \"" + nome + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ClienteReembolsoUtil.removerCliente(nome);
            carregarClientesNaLista();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Cliente excluído.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
