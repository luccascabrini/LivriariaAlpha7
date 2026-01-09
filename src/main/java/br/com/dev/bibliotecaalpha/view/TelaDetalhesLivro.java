package br.com.dev.bibliotecaalpha.view;

import br.com.dev.bibliotecaalpha.model.Livro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Janela modal para visualização dos detalhes de um livro (Read-Only).
 * <p>
 * Esta classe exibe todas as informações do livro selecionado, incluindo a capa
 * e a lista de livros semelhantes, utilizando componentes não editáveis para garantir a integridade da visualização.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaDetalhesLivro extends JDialog {

    /**
     * Constrói a janela de detalhes e preenche os campos com os dados do livro.
     *
     * @param parent A tela pai (geralmente TelaListagem) para centralização.
     * @param livro  O objeto contendo os dados a serem exibidos.
     */
    public TelaDetalhesLivro(Frame parent, Livro livro) {
        super(parent, "Detalhes do Livro", true);
        setSize(700, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 10, 15);

        int linha = 0;
        adicionarCampo(panelPrincipal, gbc, "ID:", String.valueOf(livro.getId()), linha++);
        adicionarCampo(panelPrincipal, gbc, "ISBN:", livro.getIsbn(), linha++);
        adicionarCampo(panelPrincipal, gbc, "Título:", livro.getTitulo(), linha++);
        adicionarCampo(panelPrincipal, gbc, "Autor(es):", livro.getAutores(), linha++);
        adicionarCampo(panelPrincipal, gbc, "Editora:", livro.getEditora(), linha++);
        adicionarCampo(panelPrincipal, gbc, "Publicação:", livro.getDataPublicacao(), linha++);

        String semelhantes = livro.getLivrosSemelhantes() != null ? livro.getLivrosSemelhantes() : "N/D";
        adicionarCampo(panelPrincipal, gbc, "Semelhantes:", semelhantes, linha++);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 7;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel lblCapa = new JLabel();
        lblCapa.setPreferredSize(new Dimension(160, 230));
        lblCapa.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        lblCapa.setHorizontalAlignment(SwingConstants.CENTER);
        lblCapa.setText("Sem Capa");

        if (livro.getCapaImagem() != null && livro.getCapaImagem().length > 0) {
            ImageIcon icon = new ImageIcon(livro.getCapaImagem());
            Image img = icon.getImage().getScaledInstance(160, 230, Image.SCALE_SMOOTH);
            lblCapa.setIcon(new ImageIcon(img));
            lblCapa.setText("");
        }

        panelPrincipal.add(lblCapa, gbc);

        JScrollPane scroll = new JScrollPane(panelPrincipal);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel panelSul = new JPanel();
        JButton btnFechar = new JButton("Fechar");
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFechar.addActionListener(e -> dispose());
        panelSul.add(btnFechar);
        add(panelSul, BorderLayout.SOUTH);
    }

    /**
     * Método auxiliar para adicionar um par Rótulo/Valor ao layout.
     * <p>
     * Utiliza {@link JTextArea} transparente em vez de JLabel para o valor,
     * permitindo que textos longos (como títulos grandes) quebrem de linha automaticamente.
     * </p>
     *
     * @param panel  O painel onde os componentes serão adicionados.
     * @param gbc    As restrições de layout (GridBag).
     * @param rotulo O texto do label (ex: "Título:").
     * @param valor  O conteúdo a ser exibido.
     * @param linha  O índice da linha no grid onde o campo será posicionado.
     */
    private void adicionarCampo(JPanel panel, GridBagConstraints gbc, String rotulo, String valor, int linha) {

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRotulo.setForeground(UIManager.getColor("Label.disabledForeground"));
        panel.add(lblRotulo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextArea txtValor = new JTextArea(valor);
        txtValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtValor.setLineWrap(true);
        txtValor.setWrapStyleWord(true);
        txtValor.setEditable(false);
        txtValor.setOpaque(false);
        txtValor.setBorder(null);
        txtValor.setForeground(UIManager.getColor("Label.foreground"));

        panel.add(txtValor, gbc);
    }
}