package br.com.dev.bibliotecaalpha.view;

import br.com.dev.bibliotecaalpha.facade.LivroFacade;
import br.com.dev.bibliotecaalpha.model.Livro;

import javax.swing.*;
import java.awt.*;

/**
 * Janela de diálogo (Modal) para confirmação de exclusão de registros.
 * <p>
 * Apresenta os dados do livro em modo somente leitura (Read-Only) e exige uma confirmação
 * explícita do usuário antes de remover o registro permanentemente do banco de dados.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaExclusao extends JDialog {

    private final LivroFacade facade;
    private final TelaListagem telaListagem;
    private final Livro livro;
    private JTextField txtIsbn, txtTitulo, txtAutores;

    /**
     * Construtor da Tela de Exclusão.
     *
     * @param telaListagem Janela de listagem (Parent) para atualização após a exclusão.
     * @param facade       Fachada para comunicação com o backend.
     * @param livro        O objeto Livro que será alvo da exclusão.
     */
    public TelaExclusao(TelaListagem telaListagem, LivroFacade facade, Livro livro) {
        super(telaListagem, "Excluir Livro", true);
        this.telaListagem = telaListagem;
        this.facade = facade;
        this.livro = livro;
        setSize(500, 300);
        setLocationRelativeTo(telaListagem);
        setLayout(new GridBagLayout());

        inicializarComponentes();
        preencherDadosReadOnly();
    }

    /**
     * Preenche os campos de texto com os dados do livro e desabilita a edição.
     * Isso garante que o usuário visualize exatamente o que está excluindo, sem poder alterar.
     */
    private void preencherDadosReadOnly() {
        txtIsbn.setText(livro.getIsbn());
        txtTitulo.setText(livro.getTitulo());
        txtAutores.setText(livro.getAutores());

        txtIsbn.setEditable(false);
        txtTitulo.setEditable(false);
        txtAutores.setEditable(false);
    }

    /**
     * Inicializa os componentes visuais, incluindo a mensagem de aviso em vermelho
     * e os botões de confirmação.
     */
    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblAviso = new JLabel("Você confirma a exclusão deste registro?");
        lblAviso.setForeground(Color.RED);
        lblAviso.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAviso.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblAviso, gbc);

        txtIsbn = new JTextField();
        txtTitulo = new JTextField();
        txtAutores = new JTextField();

        adicionarCampo("ISBN:", txtIsbn, 1, gbc);
        adicionarCampo("Título:", txtTitulo, 2, gbc);
        adicionarCampo("Autores:", txtAutores, 3, gbc);

        JPanel panelBotoes = new JPanel();
        JButton btnExcluir = new JButton("CONFIRMAR EXCLUSÃO");
        JButton btnCancelar = new JButton("Cancelar");

        btnExcluir.setBackground(new Color(200, 50, 50));
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnExcluir.addActionListener(e -> confirmarExclusao());
        btnCancelar.addActionListener(e -> dispose());

        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(panelBotoes, gbc);
    }

    /**
     * Método auxiliar para adicionar rótulos e campos ao layout.
     */
    private void adicionarCampo(String rotulo, JComponent campo, int linha, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        add(new JLabel(rotulo), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1;
        add(campo, gbc);
    }

    /**
     * Aciona o Facade para remover o livro do banco de dados.
     * Exibe mensagem de sucesso, atualiza a tabela da tela principal e fecha o diálogo.
     */
    private void confirmarExclusao() {
        try {
            facade.excluirLivro(livro.getId());
            JOptionPane.showMessageDialog(this, "Registro removido do sistema.");
            telaListagem.atualizarListagem();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
        }
    }
}