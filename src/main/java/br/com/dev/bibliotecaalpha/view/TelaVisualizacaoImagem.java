package br.com.dev.bibliotecaalpha.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Janela modal simples para visualização ampliada da imagem da capa.
 * <p>
 * Exibe a imagem em um fundo escuro. Se a imagem for maior que a janela,
 * ela é redimensionada proporcionalmente para caber.
 * A janela pode ser fechada clicando em qualquer lugar dela.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
public class TelaVisualizacaoImagem extends JDialog {

    /**
     * Construtor da tela de visualização.
     *
     * @param parent      A janela pai (pode ser JFrame ou JDialog) para centralização.
     * @param imagemBytes O array de bytes da imagem a ser exibida.
     */
    public TelaVisualizacaoImagem(Window parent, byte[] imagemBytes) {
        super(parent, "Visualização da Capa", ModalityType.APPLICATION_MODAL);

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(50, 50, 50));

        if (imagemBytes != null && imagemBytes.length > 0) {
            ImageIcon iconOriginal = new ImageIcon(imagemBytes);

            Image img = iconOriginal.getImage();
            int largura = iconOriginal.getIconWidth();
            int altura = iconOriginal.getIconHeight();

            if (largura > 480 || altura > 550) {
                img = img.getScaledInstance(480, -1, Image.SCALE_SMOOTH);
            }

            JLabel lblImagem = new JLabel(new ImageIcon(img));
            lblImagem.setHorizontalAlignment(SwingConstants.CENTER);
            add(lblImagem, BorderLayout.CENTER);
        } else {
            JLabel lblErro = new JLabel("Imagem indisponível", SwingConstants.CENTER);
            lblErro.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblErro.setForeground(Color.WHITE);
            add(lblErro, BorderLayout.CENTER);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                dispose();
            }
        });
    }
}