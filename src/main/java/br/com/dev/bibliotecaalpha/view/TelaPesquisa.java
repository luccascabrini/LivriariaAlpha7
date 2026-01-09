package br.com.dev.bibliotecaalpha.view;

import javax.swing.*;
import java.awt.*;

/**
 * Janela de diálogo (Modal) dedicada à pesquisa e filtragem de livros.
 * <p>
 * Permite que o usuário insira termos de busca. Esta tela não processa os dados diretamente,
 * mas delega a ação de filtragem para a {@link TelaListagem} principal.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaPesquisa extends JDialog {

    private final TelaListagem telaListagem;
    private JComboBox<String> cmbCampos;
    private JTextField txtValor;

    /**
     * Construtor da Tela de Pesquisa.
     *
     * @param telaListagem A janela principal (Listagem) que receberá o comando de filtro.
     */
    public TelaPesquisa(TelaListagem telaListagem) {
        super(telaListagem, "Pesquisar Livros", true);
        this.telaListagem = telaListagem;

        setSize(400, 180);
        setLocationRelativeTo(telaListagem);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        inicializarComponentes();
    }

    /**
     * Inicializa os componentes visuais (Campo de texto, Combo de seleção e Botões).
     * Configura o botão "Pesquisar" como padrão (acionado pelo Enter).
     */
    private void inicializarComponentes() {
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelForm.add(new JLabel("Pesquisar em:"));
        String[] campos = {"Todos os Campos", "Título", "Autores", "Editora", "ISBN"};
        cmbCampos = new JComboBox<>(campos);
        panelForm.add(cmbCampos);

        panelForm.add(new JLabel("Termo de busca:"));
        txtValor = new JTextField();
        panelForm.add(txtValor);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotoes = new JPanel();
        JButton btnPesquisar = new JButton("Pesquisar 🔍");
        JButton btnLimpar = new JButton("Limpar Filtro");

        btnPesquisar.addActionListener(e -> executarPesquisa());

        btnLimpar.addActionListener(e -> {
            telaListagem.aplicarFiltroExterno("");
            dispose();
        });

        panelBotoes.add(btnPesquisar);
        panelBotoes.add(btnLimpar);
        add(panelBotoes, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnPesquisar);
    }

    /**
     * Captura o termo digitado e aciona o método de filtro na tela principal.
     * Após aplicar o filtro, fecha a janela de pesquisa.
     */
    private void executarPesquisa() {
        String termo = txtValor.getText().trim();

        telaListagem.aplicarFiltroExterno(termo);
        dispose();
    }
}