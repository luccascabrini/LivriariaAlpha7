package br.com.dev.bibliotecaalpha;

import br.com.dev.bibliotecaalpha.facade.ExportacaoFacade;
import br.com.dev.bibliotecaalpha.facade.ImportacaoFacade;
import br.com.dev.bibliotecaalpha.facade.LivroFacade;
import br.com.dev.bibliotecaalpha.view.TelaListagem;
import com.formdev.flatlaf.FlatDarkLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Ponto de entrada (Entry Point) da aplicação Biblioteca Alpha.
 * <p>
 * Esta classe é responsável por inicializar o contexto do Spring Boot e,
 * em seguida, lançar a interface gráfica (Swing) na Thread de Eventos (EDT).
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@SpringBootApplication
public class BibliotecaAlphaApplication {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaAlphaApplication.class);

    /**
     * Método principal que executa a aplicação.
     * <p>
     * O fluxo de inicialização segue os seguintes passos:
     * <ol>
     * <li>Configura o tema visual (FlatLaf Dark).</li>
     * <li>Inicia o Spring Boot no modo Desktop (sem servidor Web embutido e com suporte a telas).</li>
     * <li>Obtém o Facade do contexto de injeção de dependências.</li>
     * <li>Abre a janela principal {@link TelaListagem}.</li>
     * </ol>
     * </p>
     *
     * @param args Argumentos de linha de comando (opcionais).
     */
    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("Erro fatal não tratado na Thread {}: {}", t.getName(), e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Ocorreu um erro crítico: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        });

        try {
            FlatDarkLaf.setup();
            log.info("Tema visual configurado com sucesso.");
        } catch (Exception e) {
            log.error("Falha ao iniciar tema visual: {}", e.getMessage());
        }

        try {
            ConfigurableApplicationContext context = new SpringApplicationBuilder(BibliotecaAlphaApplication.class)
                    .headless(false)
                    .web(WebApplicationType.NONE)
                    .run(args);

            log.info("Contexto Spring Boot inicializado.");

            EventQueue.invokeLater(() -> {
                LivroFacade livroFacade = context.getBean(LivroFacade.class);
                ImportacaoFacade importacaoFacade = context.getBean(ImportacaoFacade.class);
                ExportacaoFacade exportacaoFacade = context.getBean(ExportacaoFacade.class);

                TelaListagem tela = new TelaListagem(livroFacade, importacaoFacade, exportacaoFacade);
                tela.setVisible(true);
                log.info("Interface gráfica iniciada.");
            });

        } catch (Exception e) {
            log.error("Erro fatal ao iniciar aplicação", e);
        }
    }
}