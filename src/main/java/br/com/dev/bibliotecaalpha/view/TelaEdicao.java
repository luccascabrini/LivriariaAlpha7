package br.com.dev.bibliotecaalpha.view;

import br.com.dev.bibliotecaalpha.facade.LivroFacade;
import br.com.dev.bibliotecaalpha.model.Livro;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;

/**
 * Janela de diálogo (Modal) para edição de livros existentes.
 * <p>
 * Carrega os dados atuais do livro nos campos de texto e permite a alteração
 * de informações textuais e da imagem de capa. Atualiza a listagem principal
 * ao concluir a operação.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaEdicao extends JDialog {

    private final LivroFacade facade;
    private final TelaListagem telaListagem;
    private final Livro livroAtual;

    private JTextArea txtSemelhantes;
    private JTextField txtIsbn, txtTitulo, txtAutores, txtEditora, txtDataPublicacao;
    private JLabel lblCapa;

    private byte[] imagemAtualBytes;

    /**
     * Construtor da Tela de Edição.
     * @param telaListagem    A janela de listagem que invocou a edição (para atualização posterior).
     * @param facade          A fachada para comunicação com o banco de dados.
     * @param livroParaEditar O objeto Livro original que será modificado.
     */
    public TelaEdicao(TelaListagem telaListagem, LivroFacade facade, Livro livroParaEditar) {
        super(telaListagem, "Editar Livro", true);
        this.telaListagem = telaListagem;
        this.facade = facade;
        this.livroAtual = livroParaEditar;

        this.imagemAtualBytes = livroAtual.getCapaImagem();

        setSize(650, 550);
        setLocationRelativeTo(telaListagem);
        setLayout(new GridBagLayout());

        inicializarComponentes();
        preencherDadosIniciais();
    }

    /**
     * Preenche os campos de texto e a imagem com os dados do livro recebido no construtor.
     */
    private void preencherDadosIniciais() {
        txtIsbn.setText(livroAtual.getIsbn());
        txtTitulo.setText(livroAtual.getTitulo());
        txtAutores.setText(livroAtual.getAutores());
        txtEditora.setText(livroAtual.getEditora());
        txtDataPublicacao.setText(livroAtual.getDataPublicacao());
        txtSemelhantes.setText(livroAtual.getLivrosSemelhantes());

        atualizarPreviewImagem(this.imagemAtualBytes);
    }

    /**
     * Configura o layout (GridBagLayout) e inicializa os componentes visuais.
     * Inclui a configuração do evento de clique na imagem para visualização ampliada.
     */
    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSemelhantes = new JTextArea(4, 20);
        txtSemelhantes.setLineWrap(true);
        txtSemelhantes.setWrapStyleWord(true);
        JScrollPane scrollSemelhantes = new JScrollPane(txtSemelhantes);

        txtIsbn = new JTextField(15);
        txtTitulo = new JTextField(20);
        txtAutores = new JTextField(20);
        txtEditora = new JTextField(20);
        txtDataPublicacao = new JTextField(10);

        lblCapa = new JLabel("Sem Capa");
        lblCapa.setPreferredSize(new Dimension(120, 180));
        lblCapa.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblCapa.setHorizontalAlignment(SwingConstants.CENTER);
        lblCapa.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblCapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (imagemAtualBytes != null) {
                    new TelaVisualizacaoImagem(TelaEdicao.this, imagemAtualBytes).setVisible(true);
                }
            }
        });

        JButton btnUpload = new JButton("Alterar Capa 📁");
        btnUpload.addActionListener(e -> selecionarImagemDoComputador());

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; add(txtIsbn, gbc);

        adicionarCampo("Título:", txtTitulo, 1, gbc);
        adicionarCampo("Autores:", txtAutores, 2, gbc);
        adicionarCampo("Editora:", txtEditora, 3, gbc);
        adicionarCampo("Data Pub.:", txtDataPublicacao, 4, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Semelhantes:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollSemelhantes, gbc);

        GridBagConstraints gbcCapa = new GridBagConstraints();
        gbcCapa.gridx = 3; gbcCapa.gridy = 0;
        gbcCapa.gridheight = 6;
        gbcCapa.insets = new Insets(8, 20, 0, 8);
        gbcCapa.anchor = GridBagConstraints.NORTH;
        add(lblCapa, gbcCapa);

        gbcCapa.gridy = 6; gbcCapa.gridheight = 1;
        gbcCapa.fill = GridBagConstraints.HORIZONTAL;
        gbcCapa.insets = new Insets(5, 20, 8, 8);
        add(btnUpload, gbcCapa);

        JPanel panelBotoes = new JPanel();
        JButton btnSalvar = new JButton("Salvar Alterações");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(70, 130, 180));
        btnSalvar.setForeground(Color.WHITE);

        btnSalvar.addActionListener(e -> salvarAlteracoes());
        btnCancelar.addActionListener(e -> dispose());

        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        add(panelBotoes, gbc);
    }

    /**
     * Método auxiliar para adicionar labels e campos ao layout.
     */
    private void adicionarCampo(String rotulo, JComponent campo, int linha, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        add(new JLabel(rotulo), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(campo, gbc);
    }

    /**
     * Abre o seletor de arquivos para substituir a imagem de capa atual.
     */
    private void selecionarImagemDoComputador() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File arquivo = fileChooser.getSelectedFile();
                this.imagemAtualBytes = Files.readAllBytes(arquivo.toPath());
                atualizarPreviewImagem(this.imagemAtualBytes);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    /**
     * Atualiza o componente JLabel com a nova imagem selecionada (redimensionada).
     *
     * @param bytes Array de bytes da imagem.
     */
    private void atualizarPreviewImagem(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            ImageIcon icon = new ImageIcon(bytes);
            Image img = icon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH);
            lblCapa.setIcon(new ImageIcon(img));
            lblCapa.setText("");
            lblCapa.setToolTipText("Clique para ampliar");
        } else {
            lblCapa.setIcon(null);
            lblCapa.setText("Sem Capa");
            lblCapa.setToolTipText(null);
        }
    }

    /**
     * Coleta os dados editados, atualiza o objeto Livro e persiste via Facade.
     * Fecha a janela e atualiza a listagem ao finalizar.
     */
    private void salvarAlteracoes() {
        try {
            livroAtual.setIsbn(txtIsbn.getText());
            livroAtual.setTitulo(txtTitulo.getText());
            livroAtual.setAutores(txtAutores.getText());
            livroAtual.setEditora(txtEditora.getText());
            livroAtual.setDataPublicacao(txtDataPublicacao.getText());
            livroAtual.setLivrosSemelhantes(txtSemelhantes.getText());

            livroAtual.setCapaImagem(this.imagemAtualBytes);

            facade.salvarLivro(livroAtual);
            JOptionPane.showMessageDialog(this, "Livro atualizado!");
            telaListagem.atualizarListagem();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}