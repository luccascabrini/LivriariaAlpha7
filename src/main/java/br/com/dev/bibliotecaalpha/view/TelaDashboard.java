package br.com.dev.bibliotecaalpha.view;

import br.com.dev.bibliotecaalpha.facade.LivroFacade;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Painel principal do Dashboard da aplicação.
 * <p>
 * Responsável por apresentar uma visão geral do sistema (KPIs),
 * como total de livros, últimos cadastros e editoras.
 * Também gerencia a alternância de temas (Claro/Escuro).
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaDashboard extends JPanel {

    private final LivroFacade facade;

    private JLabel lblTotalLivros, lblUltimoLivro, lblTotalEditoras;
    private JLabel lblTituloGeral, lblSubtituloGeral;
    private JLabel lblDecoracao;
    private JButton btnTema;

    private JPanel card1, card2, card3;

    /**
     * Construtor padrão do Dashboard.
     * <p>
     * Inicializa a interface gráfica, carrega os dados do banco via Facade
     * e aplica o tema visual inicial.
     * </p>
     *
     * @param facade Instância da fachada para comunicação com o backend.
     */
    public TelaDashboard(LivroFacade facade) {
        this.facade = facade;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 30, 30, 30));

        criarCabecalho();
        criarCards();

        lblDecoracao = new JLabel("Sistema Alpha - Gestão de Acervo", SwingConstants.CENTER);
        lblDecoracao.setFont(new Font("Segoe UI", Font.BOLD, 40));
        add(lblDecoracao, BorderLayout.SOUTH);

        atualizarDados();
        aplicarTema();
    }

    /**
     * Monta a seção superior do painel (Header).
     * Contém o Título, Subtítulo e o Botão de troca de tema.
     */
    private void criarCabecalho() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);

        lblTituloGeral = new JLabel("Dashboard");
        lblTituloGeral.setFont(new Font("Segoe UI", Font.BOLD, 32));

        lblSubtituloGeral = new JLabel("Visão geral do seu acervo");
        lblSubtituloGeral.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        panelTextos.add(lblTituloGeral);
        panelTextos.add(lblSubtituloGeral);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoes.setOpaque(false);

        btnTema = new JButton("☾");
        btnTema.putClientProperty("JButton.buttonType", "toolBarButton");
        btnTema.setFocusable(false);
        btnTema.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        btnTema.setToolTipText("Alternar Tema");
        btnTema.setBorderPainted(false);
        btnTema.setContentAreaFilled(false);
        btnTema.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnTema.addActionListener(e -> alternarTema());

        panelBotoes.add(btnTema);

        panelHeader.add(panelTextos, BorderLayout.WEST);
        panelHeader.add(panelBotoes, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);
    }

    /**
     * Cria e organiza os Cards estatísticos (KPIs) no centro da tela.
     */
    private void criarCards() {
        JPanel panelCards = new JPanel(new GridLayout(1, 3, 20, 0));
        panelCards.setOpaque(false);
        panelCards.setBorder(new EmptyBorder(40, 0, 0, 0));

        card1 = criarCardEstrutura("Total de Livros", "📚", new Color(66, 133, 244));
        lblTotalLivros = (JLabel) card1.getClientProperty("valor");

        card2 = criarCardEstrutura("Último Adicionado", "🆕", new Color(15, 157, 88));
        lblUltimoLivro = (JLabel) card2.getClientProperty("valor");

        card3 = criarCardEstrutura("Status / Editoras", "🏢", new Color(244, 180, 0));
        lblTotalEditoras = (JLabel) card3.getClientProperty("valor");

        panelCards.add(card1);
        panelCards.add(card2);
        panelCards.add(card3);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(panelCards, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    /**
     * Método auxiliar para construir o design de um Card individual.
     * <p>
     * Configura cores, ícones, bordas arredondadas e adiciona um Listener
     * para efeito de "brilho" ao passar o mouse (Hover).
     * </p>
     *
     * @param titulo   Texto pequeno no topo do card.
     * @param icone    Emoji ou caractere representativo.
     * @param corFundo Cor de fundo base do card.
     * @return O painel configurado.
     */
    private JPanel criarCardEstrutura(String titulo, String icone, Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corFundo, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {

                card.setBackground(corFundo.brighter());
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 1),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {

                card.setBackground(corFundo);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corFundo, 1),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(new Color(255, 255, 255, 220));

        JLabel lblIcon = new JLabel(icone);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblIcon.setForeground(Color.WHITE);

        topo.add(lblTit, BorderLayout.WEST);
        topo.add(lblIcon, BorderLayout.EAST);

        JLabel lblVal = new JLabel("...");
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblVal.setForeground(Color.WHITE);
        lblVal.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(topo, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);

        card.putClientProperty("valor", lblVal);
        card.setPreferredSize(new Dimension(200, 130));
        return card;
    }

    /**
     * Alterna o tema da aplicação entre Claro (FlatLightLaf) e Escuro (FlatDarkLaf).
     * Atualiza a árvore de componentes visuais após a troca.
     */
    private void alternarTema() {
        try {
            if (FlatLaf.isLafDark()) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }

            Window janela = SwingUtilities.getWindowAncestor(this);
            SwingUtilities.updateComponentTreeUI(janela);
            aplicarTema();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Ajusta cores específicas de componentes que não obedecem automaticamente
     * a troca de tema global (como cores de fonte personalizadas e marca d'água).
     */
    public void aplicarTema() {
        boolean isDark = FlatLaf.isLafDark();

        this.setBackground(UIManager.getColor("Panel.background"));

        if (isDark) {
            lblTituloGeral.setForeground(Color.WHITE);
            lblSubtituloGeral.setForeground(new Color(200, 200, 200));
            btnTema.setForeground(Color.YELLOW);

            lblDecoracao.setForeground(new Color(0, 0, 0, 255));
        } else {
            lblTituloGeral.setForeground(new Color(50, 50, 50));
            lblSubtituloGeral.setForeground(new Color(100, 100, 100));
            btnTema.setForeground(new Color(50, 50, 50));

            lblDecoracao.setForeground(new Color(0, 0, 0, 255));
        }

        this.repaint();
    }

    /**
     * Busca os dados atualizados no Facade e atualiza os textos dos Cards.
     */
    public void atualizarDados() {
        try {
            lblTotalLivros.setText(String.valueOf(facade.getTotalLivros()));

            String ultimo = facade.getUltimoLivro();
            if (ultimo.length() > 14) ultimo = ultimo.substring(0, 11) + "...";
            lblUltimoLivro.setText(ultimo);

            lblTotalEditoras.setText(String.valueOf(facade.getTotalEditoras()));
        } catch (Exception e) {
        }
    }
}