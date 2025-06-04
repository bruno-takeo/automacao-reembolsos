package com.meuprojeto.ui;

import com.meuprojeto.model.ClienteReembolso;
import com.meuprojeto.model.ReembolsoInfo;
import com.meuprojeto.service.ReembolsoService;
import com.meuprojeto.util.ArquivoUtil;
import com.meuprojeto.util.ClienteUtil;
import com.meuprojeto.util.ExcelUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.ParseException;
import java.util.List;

public class GerarReembolsoFrame extends JFrame {

    private final JButton selecionarClienteButton;
    private final JButton selecionarDiretorioButton;
    private final JTextField pastaTextField;
    private final JTextField nomePlanilhaPdfField;
    private final JTextField nomeReembolsoPdfField;
    private final JButton gerarButton;
    private final MenuPrincipalFrame menuPrincipal;
    private final JTextField periodoField;

    private File pastaSelecionada;
    private ClienteReembolso clienteSelecionado;

    public GerarReembolsoFrame(MenuPrincipalFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;

        setTitle("Gerador de Reembolso");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                menuPrincipal.setVisible(true);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel caminhoPanel = new JPanel(new FlowLayout());
        pastaTextField = new JTextField(35);
        pastaTextField.setEditable(false);

        selecionarClienteButton = new JButton("Selecionar Cliente");
        selecionarClienteButton.addActionListener(this::selecionarCliente);

        selecionarDiretorioButton = new JButton("Selecionar Pasta Manualmente");
        selecionarDiretorioButton.addActionListener(this::selecionarDiretorio);

        caminhoPanel.add(new JLabel("Pasta:"));
        caminhoPanel.add(pastaTextField);
        caminhoPanel.add(selecionarClienteButton);
        caminhoPanel.add(selecionarDiretorioButton);

        JPanel nomeArquivosPanel = new JPanel(new FlowLayout());
        nomePlanilhaPdfField = new JTextField("planilha.pdf", 15);
        nomeReembolsoPdfField = new JTextField("reembolso_final.pdf", 15);
        periodoField = new JTextField(15);

        nomeArquivosPanel.add(new JLabel("Nome da planilha PDF:"));
        nomeArquivosPanel.add(nomePlanilhaPdfField);
        nomeArquivosPanel.add(new JLabel("Nome do PDF final:"));
        nomeArquivosPanel.add(nomeReembolsoPdfField);
        nomeArquivosPanel.add(new JLabel("Período:"));
        nomeArquivosPanel.add(periodoField);

        gerarButton = new JButton("Gerar Reembolso");
        gerarButton.addActionListener(this::gerarReembolso);

        JPanel botaoPanel = new JPanel(new FlowLayout());
        botaoPanel.add(gerarButton);

        panel.add(caminhoPanel);
        panel.add(nomeArquivosPanel);
        panel.add(botaoPanel);

        add(panel, BorderLayout.CENTER);
    }

    private void selecionarCliente(ActionEvent e) {
        ClienteReembolso selecionado = ClienteUtil.selecionarCliente(this);
        if (selecionado != null) {
            this.clienteSelecionado = selecionado;

            JFileChooser chooser = new JFileChooser(new File(clienteSelecionado.getCaminhoBase()));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int resultado = chooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File escolhido = chooser.getSelectedFile();
                // Valida se está dentro do diretório do cliente
                if (!escolhido.getAbsolutePath().startsWith(clienteSelecionado.getCaminhoBase())) {
                    JOptionPane.showMessageDialog(this, "A pasta selecionada deve estar dentro do diretório do cliente.");
                    return;
                }

                this.pastaSelecionada = escolhido;
                pastaTextField.setText(escolhido.getAbsolutePath());
            }
        }
    }

    private void selecionarDiretorio(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            this.pastaSelecionada = fileChooser.getSelectedFile();
            this.clienteSelecionado = null; // reset
            pastaTextField.setText(pastaSelecionada.getAbsolutePath());
        }
    }

    private void gerarReembolso(ActionEvent e) {
        if (pastaSelecionada == null || !pastaSelecionada.exists() || !pastaSelecionada.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente ou diretório válido.");
            return;
        }

        String nomePlanilha = nomePlanilhaPdfField.getText().trim();
        String nomeReembolso = nomeReembolsoPdfField.getText().trim();

        if (!nomePlanilha.toLowerCase().endsWith(".pdf")) {
            nomePlanilha += ".pdf";
        }

        if (!nomeReembolso.toLowerCase().endsWith(".pdf")) {
            nomeReembolso += ".pdf";
        }

        try {
            List<ReembolsoInfo> infos = ReembolsoService.lerArquivosValidos(pastaSelecionada);
            if (infos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum arquivo válido encontrado.");
                return;
            }

            File excelTemp = new File(pastaSelecionada, "planilha-temp.xlsx");
            File planilhaPDF = new File(pastaSelecionada, nomePlanilha);
            File novoPDF = new File(pastaSelecionada, nomeReembolso);

            String nomeEmpresa = clienteSelecionado != null ? clienteSelecionado.getNomeEmpresa() : "";
            String periodoInformado = periodoField.getText().trim();
            ExcelUtil.gerarPlanilhaExcel(excelTemp, infos, nomeEmpresa, periodoInformado);
            ArquivoUtil.converterExcelParaPDFComLibreOffice(excelTemp, planilhaPDF);
            ReembolsoService.consolidarPDFsOrdenados(infos, novoPDF);

            JOptionPane.showMessageDialog(this, "Reembolso gerado com sucesso!");

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ler os nomes dos arquivos. Verifique o padrão.");
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar reembolso: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
