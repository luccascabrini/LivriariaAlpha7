package br.com.dev.bibliotecaalpha.view;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.facade.LivroFacade;
import br.com.dev.bibliotecaalpha.model.Livro;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

/**
 * Janela de diálogo para cadastro de novos livros.
 * <p>
 * Permite o preenchimento manual dos dados ou o preenchimento automático via busca por ISBN (OpenLibrary).
 * Suporta também o upload manual de imagem de capa do computador local.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaCadastro extends JDialog {

    private final LivroFacade facade;
    private final TelaListagem telaListagem;

    private Livro livro;

    private JTextField txtIsbn, txtTitulo, txtAutores, txtEditora, txtDataPublicacao;
    private JLabel lblCapa;
    private JTextArea txtSemelhantes;

    private byte[] imagemAtualBytes = null;

    /**
     * Construtor da Tela de Cadastro.
     *
     * @param telaListagem Janela de listagem (Parent) que chamou este diálogo.
     * @param facade       Fachada para comunicação com o backend.
     */
    public TelaCadastro(TelaListagem telaListagem, LivroFacade facade) {
        super(telaListagem, "Cadastro de Livro", true);
        this.telaListagem = telaListagem;
        this.facade = facade;

        setSize(650, 550);
        setLocationRelativeTo(telaListagem);
        setLayout(new GridBagLayout());

        inicializarComponentes();
    }

    /**
     * Inicializa e organiza os componentes visuais usando GridBagLayout.
     */
    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIsbn = new JTextField(15);
        txtTitulo = new JTextField(20);
        txtAutores = new JTextField(20);
        txtEditora = new JTextField(20);
        txtDataPublicacao = new JTextField(10);

        txtSemelhantes = new JTextArea(4, 20);
        txtSemelhantes.setLineWrap(true);
        txtSemelhantes.setWrapStyleWord(true);
        JScrollPane scrollSemelhantes = new JScrollPane(txtSemelhantes);

        JButton btnBuscarApi = new JButton("Buscar na Web 🌍");
        btnBuscarApi.addActionListener(e -> buscarNaApi());

        lblCapa = new JLabel("Sem Capa");
        lblCapa.setPreferredSize(new Dimension(120, 180));
        lblCapa.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblCapa.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnUpload = new JButton("📁 Carregar Capa");
        btnUpload.addActionListener(e -> selecionarImagemDoComputador());

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; add(txtIsbn, gbc);
        gbc.gridx = 2; add(btnBuscarApi, gbc);

        adicionarCampo("Título:", txtTitulo, 1, gbc);
        adicionarCampo("Autores:", txtAutores, 2, gbc);
        adicionarCampo("Editora:", txtEditora, 3, gbc);
        adicionarCampo("Data Publicação:", txtDataPublicacao, 4, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Semelhantes:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollSemelhantes, gbc);

        GridBagConstraints gbcCapa = new GridBagConstraints();
        gbcCapa.gridx = 3;
        gbcCapa.gridy = 0;
        gbcCapa.gridheight = 6;
        gbcCapa.insets = new Insets(8, 20, 0, 8);
        gbcCapa.anchor = GridBagConstraints.NORTH;
        add(lblCapa, gbcCapa);

        gbcCapa.gridy = 6;
        gbcCapa.gridheight = 1;
        gbcCapa.fill = GridBagConstraints.HORIZONTAL;
        gbcCapa.insets = new Insets(5, 20, 8, 8);
        add(btnUpload, gbcCapa);

        JPanel panelBotoes = new JPanel();
        JButton btnSalvar = new JButton("Confirmar Cadastro");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(60, 120, 60));
        btnSalvar.setForeground(Color.WHITE);

        btnSalvar.addActionListener(e -> salvarLivro());
        btnCancelar.addActionListener(e -> dispose());

        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        add(panelBotoes, gbc);
    }

    /**
     * Método auxiliar para adicionar labels e campos de texto ao layout.
     */
    private void adicionarCampo(String rotulo, JComponent campo, int linha, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        add(new JLabel(rotulo), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(campo, gbc);
    }

    /**
     * Coleta os dados dos campos, valida e envia para o Facade salvar.
     */
    private void salvarLivro() {
        try {
            if (this.livro == null) {
                this.livro = new Livro();
            }

            this.livro.setTitulo(txtTitulo.getText());
            this.livro.setIsbn(txtIsbn.getText());
            this.livro.setAutores(txtAutores.getText());
            this.livro.setEditora(txtEditora.getText());
            this.livro.setDataPublicacao(txtDataPublicacao.getText());
            this.livro.setLivrosSemelhantes(txtSemelhantes.getText());

            if (this.imagemAtualBytes != null) {
                this.livro.setCapaImagem(this.imagemAtualBytes);
            }

            facade.salvarLivro(this.livro);

            JOptionPane.showMessageDialog(this, "Livro salvo com sucesso!");

            telaListagem.atualizarListagem();
            dispose();

        } catch (ServiceException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Executa a busca na API externa em uma Thread separada para não travar a interface.
     */
    private void buscarNaApi() {
        String isbn = txtIsbn.getText().trim();
        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um ISBN para buscar!");
            return;
        }

        lblCapa.setText("Buscando...");
        lblCapa.setIcon(null);

        new Thread(() -> {
            try {

                Livro livroEncontrado = facade.buscarNaApiExterna(isbn);
                byte[] capaBytes = facade.buscarCapaPorIsbn(isbn);

                SwingUtilities.invokeLater(() -> {
                    if (livroEncontrado != null) {
                        txtTitulo.setText(livroEncontrado.getTitulo());
                        txtAutores.setText(livroEncontrado.getAutores());
                        txtEditora.setText(livroEncontrado.getEditora());
                        txtDataPublicacao.setText(livroEncontrado.getDataPublicacao());

                        if (this.livro == null) this.livro = new Livro();
                        this.livro = livroEncontrado;
                    }

                    this.imagemAtualBytes = capaBytes;
                    atualizarPreviewImagem(capaBytes);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    lblCapa.setText("Erro/Sem Capa");
                    JOptionPane.showMessageDialog(this, "Aviso: " + ex.getMessage());
                });
            }
        }).start();
    }

    /**
     * Abre o seletor de arquivos do sistema operacional para upload de imagem local.
     */
    private void selecionarImagemDoComputador() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione a Capa");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens (JPG, PNG)", "jpg", "png", "jpeg"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File arquivo = fileChooser.getSelectedFile();
                byte[] bytes = Files.readAllBytes(arquivo.toPath());

                this.imagemAtualBytes = bytes;
                atualizarPreviewImagem(bytes);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao ler arquivo: " + ex.getMessage());
            }
        }
    }

    /**
     * Atualiza o componente JLabel com a imagem selecionada ou baixada.
     *
     * @param bytes Array de bytes da imagem.
     */
    private void atualizarPreviewImagem(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            ImageIcon icon = new ImageIcon(bytes);
            Image img = icon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH);
            lblCapa.setIcon(new ImageIcon(img));
            lblCapa.setText("");
        } else {
            lblCapa.setIcon(null);
            lblCapa.setText("Sem Capa");
        }
    }
}