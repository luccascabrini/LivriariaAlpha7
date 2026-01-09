package br.com.dev.bibliotecaalpha.view;

import br.com.dev.bibliotecaalpha.facade.ExportacaoFacade;
import br.com.dev.bibliotecaalpha.facade.LivroFacade;
import br.com.dev.bibliotecaalpha.facade.ImportacaoFacade;
import br.com.dev.bibliotecaalpha.model.Livro;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Tela Principal da aplicação (Main Window).
 * <p>
 * Gerencia a navegação principal através de abas (Dashboard vs Listagem),
 * exibe a tabela de livros com funcionalidades de filtro e ordenação,
 * e atua como ponto de entrada para as operações de CRUD.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaListagem extends JFrame {

    private final LivroFacade livroFacade;
    private final ImportacaoFacade importacaoFacade;
    private final ExportacaoFacade exportacaoFacade;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private TableRowSorter<DefaultTableModel> ordenar;

    private JPanel panelAvisoFiltro;
    private JLabel lblTextoFiltro;

    private CardLayout cardLayoutCentral;
    private JPanel panelConteudoCentral;

    private TelaDashboard dashboard;

    /**
     * Construtor da Tela Principal.
     * Configura as propriedades da janela, ícone, tamanho e inicializa os componentes.
     *
     * @param livroFacade Instância da fachada para comunicação com o backend.
     */
    public TelaListagem(LivroFacade livroFacade, ImportacaoFacade importacaoFacade, ExportacaoFacade exportacaoFacade) {
        this.livroFacade = livroFacade;
        this.importacaoFacade = importacaoFacade;
        this.exportacaoFacade = exportacaoFacade;

        setTitle("Biblioteca Alpha - Gestão de Acervo");

        try {
            java.net.URL urlIcone = getClass().getResource("/app_icon.png");
            if (urlIcone != null) {
                Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(urlIcone);
                this.setIconImage(iconeTitulo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        inicializarComponentes();
        atualizarListagem();
    }

    /**
     * Inicializa todos os componentes visuais da tela.
     * <p>
     * Configura:
     * <ul>
     * <li>Abas (Dashboard e Gerenciar Acervo).</li>
     * <li>Header com botão de Tema e Pesquisa.</li>
     * <li>Tabela de dados com ordenação.</li>
     * <li>Botões de ação (CRUD, Importar, Exportar).</li>
     * </ul>
     * </p>
     */
    private void inicializarComponentes() {

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        this.dashboard = new TelaDashboard(livroFacade);

        tabbedPane.addTab("Visão Geral", dashboard);

        JPanel panelAbaListagem = new JPanel(new BorderLayout());

        JPanel panelTopoGeral = new JPanel();
        panelTopoGeral.setLayout(new BoxLayout(panelTopoGeral, BoxLayout.Y_AXIS));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(60, 63, 65));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Acervo de Livros");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        JPanel panelDireitaHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelDireitaHeader.setOpaque(false);

        JButton btnTema = new JButton("☾");
        btnTema.putClientProperty("JButton.buttonType", "toolBarButton");
        btnTema.setFocusable(false);
        btnTema.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnTema.setToolTipText("Alternar Tema");
        btnTema.setForeground(FlatLaf.isLafDark() ? Color.YELLOW : Color.WHITE);

        btnTema.addActionListener(e -> {
            try {
                if (FlatLaf.isLafDark()) {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    btnTema.setForeground(Color.WHITE);
                } else {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    btnTema.setForeground(Color.YELLOW);
                }

                SwingUtilities.updateComponentTreeUI(this);

                if (this.dashboard != null) {
                    this.dashboard.aplicarTema();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JButton btnAbrirPesquisa = new JButton("Pesquisar Livros");
        btnAbrirPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAbrirPesquisa.setBackground(new Color(80, 83, 85));
        btnAbrirPesquisa.setForeground(Color.WHITE);
        btnAbrirPesquisa.setFocusPainted(false);
        btnAbrirPesquisa.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)), BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        btnAbrirPesquisa.addActionListener(e -> {
            TelaPesquisa telaPesquisa = new TelaPesquisa(this);
            telaPesquisa.setVisible(true);
        });

        panelDireitaHeader.add(btnTema);
        panelDireitaHeader.add(btnAbrirPesquisa);
        panelHeader.add(panelDireitaHeader, BorderLayout.EAST);
        panelTopoGeral.add(panelHeader);

        panelAvisoFiltro = new JPanel(new BorderLayout());
        panelAvisoFiltro.setBackground(new Color(255, 245, 200));
        panelAvisoFiltro.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 200, 100)), BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        lblTextoFiltro = new JLabel("Filtro Ativo: ");
        lblTextoFiltro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTextoFiltro.setForeground(new Color(100, 80, 0));
        lblTextoFiltro.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnLimparFiltro = new JButton("X");
        btnLimparFiltro.setToolTipText("Limpar filtro");
        btnLimparFiltro.setContentAreaFilled(false);
        btnLimparFiltro.setBorderPainted(false);
        btnLimparFiltro.setForeground(new Color(200, 50, 50));
        btnLimparFiltro.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLimparFiltro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimparFiltro.addActionListener(e -> aplicarFiltroExterno(""));

        panelAvisoFiltro.add(lblTextoFiltro, BorderLayout.CENTER);
        panelAvisoFiltro.add(btnLimparFiltro, BorderLayout.EAST);
        panelAvisoFiltro.setVisible(false);

        panelTopoGeral.add(panelAvisoFiltro);
        panelAbaListagem.add(panelTopoGeral, BorderLayout.NORTH);

        cardLayoutCentral = new CardLayout();
        panelConteudoCentral = new JPanel(cardLayoutCentral);

        String[] colunas = {"ID", "ISBN", "Título", "Data de Publicação", "Autores", "Editora"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return String.class;
            }
        };

        tabela = new JTable(modeloTabela);
        ordenar = new TableRowSorter<>(modeloTabela);
        tabela.setRowSorter(ordenar);

        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(230, 230, 230));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(new Color(235, 235, 235));
        tabela.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelConteudoCentral.add(scrollPane, "TABELA");

        JPanel panelVazio = new JPanel(new GridBagLayout());
        panelVazio.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblIconeTexto = new JLabel("?");
        lblIconeTexto.setFont(new Font("Segoe UI", Font.BOLD, 60));
        lblIconeTexto.setForeground(new Color(200, 200, 200));
        panelVazio.add(lblIconeTexto, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel lblOps = new JLabel("Nenhum livro encontrado");
        lblOps.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblOps.setForeground(new Color(100, 100, 100));
        panelVazio.add(lblOps, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblInstrucao = new JLabel("Tente ajustar os termos da sua pesquisa.");
        lblInstrucao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstrucao.setForeground(new Color(150, 150, 150));
        panelVazio.add(lblInstrucao, gbc);

        panelConteudoCentral.add(panelVazio, "VAZIO");

        panelAbaListagem.add(panelConteudoCentral, BorderLayout.CENTER);

        JPanel panelBotoes = new JPanel();
        panelBotoes.setBackground(new Color(245, 245, 245));
        panelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnIncluir = new JButton("Incluir");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnVerCapa = new JButton("Ver Capa");
        JButton btnImportar = new JButton("Importar CSV");
        JButton btnExportar = new JButton("Exportar CSV");
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnVisualizar = new JButton("Visualizar Detalhes");

        btnVisualizar.addActionListener(e -> {
            Livro livro = obterLivroSelecionado();
            if (livro != null) {
                new TelaDetalhesLivro(this, livro).setVisible(true);
            }
        });

        btnIncluir.addActionListener(e -> {
            TelaCadastro tela = new TelaCadastro(this, livroFacade);
            tela.setVisible(true);
            dashboard.atualizarDados();
        });

        btnEditar.addActionListener(e -> {
            Livro livro = obterLivroSelecionado();
            if (livro != null) {
                TelaEdicao tela = new TelaEdicao(this, livroFacade, livro);
                tela.setVisible(true);
            }
        });

        btnExcluir.addActionListener(e -> {
            Livro livro = obterLivroSelecionado();
            if (livro != null) {
                TelaExclusao tela = new TelaExclusao(this, livroFacade, livro);
                tela.setVisible(true);
                dashboard.atualizarDados();
            }
        });

        btnVerCapa.addActionListener(e -> visualizarCapaSelecionada());

        btnImportar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecione o arquivo CSV para importar");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos CSV", "csv"));

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String caminho = fileChooser.getSelectedFile().getAbsolutePath();
                    importacaoFacade.importarLivros(caminho);

                    atualizarListagem();
                    dashboard.atualizarDados();

                    JOptionPane.showMessageDialog(this, "Importação realizada com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao importar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnExportar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Relatório de Livros");
            fileChooser.setSelectedFile(new java.io.File("relatorio_livros.csv"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String caminho = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!caminho.toLowerCase().endsWith(".csv")) {
                        caminho += ".csv";
                    }

                    exportacaoFacade.exportarLivros(caminho);

                    JOptionPane.showMessageDialog(this, "Arquivo exportado com sucesso em:\n" + caminho);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAtualizar.addActionListener(e -> atualizarListagem());

        panelBotoes.add(btnIncluir);
        panelBotoes.add(btnVisualizar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnVerCapa);
        panelBotoes.add(Box.createRigidArea(new Dimension(10, 0))); // Espaçador
        panelBotoes.add(btnImportar);
        panelBotoes.add(btnExportar);
        panelBotoes.add(btnAtualizar);

        panelAbaListagem.add(panelBotoes, BorderLayout.SOUTH);

        tabbedPane.addTab("Gerenciar Acervo", panelAbaListagem);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                dashboard.atualizarDados();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Exibe a imagem da capa do livro selecionado em uma janela modal.
     */
    private void visualizarCapaSelecionada() {
        Livro livro = obterLivroSelecionado();
        if (livro != null) {
            byte[] capa = livro.getCapaImagem();
            if (capa != null && capa.length > 0) {
                new TelaVisualizacaoImagem(this, capa).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Este livro não possui capa cadastrada.", "Sem Capa", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Helper para obter o objeto {@link Livro} selecionado na tabela.
     * Converte o índice da view para o índice do model (necessário devido à ordenação).
     *
     * @return O objeto Livro selecionado ou null se nenhum estiver selecionado.
     */
    private Livro obterLivroSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela.");
            return null;
        }
        if (tabela.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Selecione apenas UM livro para esta operação.");
            return null;
        }
        try {
            int linhaModel = tabela.convertRowIndexToModel(linha);
            Long id = (Long) modeloTabela.getValueAt(linhaModel, 0);
            return livroFacade.buscarLivroPorId(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar dados: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Aplica um filtro de texto na tabela de listagem.
     * <p>
     * Chamado externamente pela {@link TelaPesquisa}. Exibe uma barra amarela
     * indicando que o filtro está ativo.
     * </p>
     *
     * @param termo O texto a ser filtrado (Regex Case Insensitive). Se vazio, limpa o filtro.
     */
    public void aplicarFiltroExterno(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            ordenar.setRowFilter(null);
            panelAvisoFiltro.setVisible(false);
            cardLayoutCentral.show(panelConteudoCentral, "TABELA");
        } else {
            ordenar.setRowFilter(RowFilter.regexFilter("(?i)" + termo));
            lblTextoFiltro.setText("Exibindo resultados para: \"" + termo + "\"");
            panelAvisoFiltro.setVisible(true);

            if (tabela.getRowCount() == 0) {
                cardLayoutCentral.show(panelConteudoCentral, "VAZIO");
            } else {
                cardLayoutCentral.show(panelConteudoCentral, "TABELA");
            }
        }
    }

    /**
     * Recarrega todos os dados da tabela buscando do banco de dados via Facade.
     * Atualiza também o Dashboard se disponível.
     */
    public void atualizarListagem() {
        modeloTabela.setRowCount(0);
        List<Livro> livros = livroFacade.buscarTodos();

        for (Livro livro : livros) {
            modeloTabela.addRow(new Object[]{livro.getId(), livro.getIsbn(), livro.getTitulo(), livro.getDataPublicacao(), livro.getAutores(), livro.getEditora()});
        }

        if (tabela.getRowCount() > 0) {
            cardLayoutCentral.show(panelConteudoCentral, "TABELA");
        }

        if (this.dashboard != null) {
            this.dashboard.atualizarDados();
        }
    }
}