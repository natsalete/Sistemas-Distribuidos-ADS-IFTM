package com.poo2.prjservidorchat_thread_form_filtro;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 *
 * @author natsa
 */
public class PrjServidorChat_Thread_Form_Filtro extends JFrame{
    
    private JTextArea logArea;
    private JScrollPane scrollPane;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JButton clearLogButton;
    private JButton manageFiltersButton;
    private JButton viewFilteredButton;
    private static Vector<PrintStream> clientes;
    private ServerSocket serverSocket;
    private int serverPort = 2222;
    private boolean running = false;
    private Thread serverThread;
    private List<String> filterWords;
    private static final String FILTER_FILE_PATH = "filter_words.txt";
    private static final String FILTERED_MESSAGES_LOG = "filtered_messages.txt";

    public PrjServidorChat_Thread_Form_Filtro() {
         // Load filter words
        filterWords = loadFilterWords();
        
        // Show initial filter word dialog
        if (filterWords.isEmpty()) {
            showFilterWordDialog();
        }
        
        initComponents();
        clientes = new Vector();
        startServer();
    }
    
    private List<String> loadFilterWords() {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILTER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            // First-time run, no file exists yet
        } catch (IOException e) {
            logMessage("ERRO", "Erro ao carregar palavras de filtro: " + e.getMessage());
        }
        return words;
    }

    private void saveFilterWords() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILTER_FILE_PATH))) {
            for (String word : filterWords) {
                writer.println(word);
            }
        } catch (IOException e) {
            logMessage("ERRO", "Erro ao salvar palavras de filtro: " + e.getMessage());
        }
    }
    
    private void showFilterWordDialog() {
        JDialog dialog = new JDialog(this, "Configurar Palavras de Filtro", true);
        dialog.setLayout(new BorderLayout());
        
        JTextArea wordArea = new JTextArea(10, 30);
        wordArea.setLineWrap(true);
        wordArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(wordArea);
        
        // Mostrar palavras existentes
        wordArea.setText(String.join("\n", filterWords));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Salvar Palavras");
        saveButton.addActionListener(e -> {
            // Atualiza a lista de palavras filtradas
            filterWords = Arrays.stream(wordArea.getText().split("\n"))
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            saveFilterWords();
            logMessage("SISTEMA", "Palavras de filtro atualizadas: " + filterWords.size() + " palavras");
            dialog.dispose();
        });
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showFilteredMessagesDialog() {
        JDialog dialog = new JDialog(this, "Mensagens Filtradas", true);
        dialog.setLayout(new BorderLayout());
        
        JTextArea messagesArea = new JTextArea(20, 50);
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messagesArea);
        
        // Carregar mensagens do arquivo
        try (BufferedReader reader = new BufferedReader(new FileReader(FILTERED_MESSAGES_LOG))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            messagesArea.setText(content.toString());
            messagesArea.setCaretPosition(0); // Rola para o topo
        } catch (FileNotFoundException e) {
            messagesArea.setText("Nenhuma mensagem filtrada encontrada.");
        } catch (IOException e) {
            messagesArea.setText("Erro ao carregar mensagens: " + e.getMessage());
        }
        
        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void initComponents() {
        setTitle("Monitor do Servidor de Chat");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // Área de log (não editável)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Painel de status
        statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel leftStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Status: Desconectado");
        clientCountLabel = new JLabel("Clientes: 0");
        leftStatusPanel.add(statusLabel);
        leftStatusPanel.add(Box.createHorizontalStrut(20));
        leftStatusPanel.add(clientCountLabel);

        JPanel rightStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearLogButton = new JButton("Limpar Log");
        clearLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
            }
        });
        rightStatusPanel.add(clearLogButton);

        // Adiciona botões para gerenciar filtros e ver mensagens filtradas
        manageFiltersButton = new JButton("Gerenciar Filtros");
        manageFiltersButton.addActionListener(e -> showFilterWordDialog());
        rightStatusPanel.add(manageFiltersButton);
        
        viewFilteredButton = new JButton("Ver Mensagens Filtradas");
        viewFilteredButton.addActionListener(e -> showFilteredMessagesDialog());
        rightStatusPanel.add(viewFilteredButton);

        statusPanel.add(leftStatusPanel, BorderLayout.WEST);
        statusPanel.add(rightStatusPanel, BorderLayout.EAST);

        // Adiciona componentes ao frame
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Lida com o fechamento da janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopServer();
            }
        });
    }
    
     // New method to log filtered messages
    private void logFilteredMessage(String sender, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILTERED_MESSAGES_LOG, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            writer.println(timestamp + " | " + sender + ": " + message);
        } catch (IOException e) {
            logMessage("ERRO", "Erro ao registrar mensagem filtrada: " + e.getMessage());
        }
    }

    
    private void startServer() {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(serverPort);
                    running = true;
                    updateStatus("Servidor iniciado na porta " + serverPort);
                    logMessage("SISTEMA", "Servidor iniciado na porta " + serverPort);

                    while (running) {
                        try {
                            logMessage("SISTEMA", "Aguardando conexão de cliente...");
                            Socket conexao = serverSocket.accept();
                            logMessage("SISTEMA", "Nova conexão aceita: " + conexao.getInetAddress().getHostAddress());

                            // Inicia thread para lidar com o cliente
                            Thread clienteThread = new ClienteHandler(conexao);
                            clienteThread.start();
                        } catch (IOException e) {
                            if (running) {
                                logMessage("ERRO", "Erro ao aceitar conexão: " + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    logMessage("ERRO", "Erro ao iniciar servidor: " + e.getMessage());
                    updateStatus("Erro no servidor");
                }
            }
        });
        serverThread.start();
    }

    private void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logMessage("SISTEMA", "Servidor encerrado");
                updateStatus("Servidor desconectado");
            }
        } catch (IOException e) {
            logMessage("ERRO", "Erro ao encerrar servidor: " + e.getMessage());
        }
    }

    // Método para adicionar mensagem ao log com timestamp
    private void logMessage(String tipo, String mensagem) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logArea.append("[" + timestamp + "] [" + tipo + "] " + mensagem + "\n");
                // Rola automaticamente para o final
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }

    // Atualiza o status do servidor na interface
    private void updateStatus(String status) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText("Status: " + status);
                clientCountLabel.setText("Clientes: " + clientes.size());
            }
        });
    }

    // Verifica se uma mensagem contém alguma das palavras filtradas
    private boolean containsFilteredWord(String message) {
        // Divide a mensagem em palavras
         String[] messageWords = message.toLowerCase().split("\\W+");

        for (String word : filterWords) {
            // Compara cada palavra da mensagem com as palavras filtradas
            for (String messageWord : messageWords) {
                if (messageWord.equals(word)) {
                    return true;
                }
            }
         }
        return false;
    }

    // Classe interna para lidar com clientes conectados
    private class ClienteHandler extends Thread {
        private Socket conexao;
        private PrintStream saida;
        private String nomeCliente;

        public ClienteHandler(Socket conexao) {
            this.conexao = conexao;
        }

        @Override
        public void run() {
            try {
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(conexao.getInputStream()));
                saida = new PrintStream(conexao.getOutputStream());

                // Lê o nome do cliente
                nomeCliente = entrada.readLine();
                if (nomeCliente == null) {
                    conexao.close();
                    return;
                }

                clientes.add(saida);
                updateStatus("Servidor conectado");

                // Registra entrada do cliente
                logMessage("CONEXÃO", "Cliente '" + nomeCliente + "' conectou-se");
                enviarParaTodos(saida, " entrou ", "no chat");

                String linha = entrada.readLine();
                while (linha != null && !(linha.trim().equals(""))) {
                    final String linhaFinal = linha; // Criando uma cópia final

                    // Usa o novo método para verificar palavras filtradas
                    if (containsFilteredWord(linhaFinal)) {
                        logFilteredMessage(nomeCliente, linhaFinal);
                        logMessage("FILTRADO", "Mensagem filtrada de " + nomeCliente + ": " + linhaFinal);
                        saida.println("AVISO: Sua mensagem contém palavras não permitidas.");
                    } else {
                        logMessage("MENSAGEM", nomeCliente + ": " + linhaFinal);
                        enviarParaTodos(saida, " : ", linhaFinal);
                    }

                    linha = entrada.readLine();
                }

                // Cliente desconectou
                logMessage("DESCONEXÃO", "Cliente '" + nomeCliente + "' desconectou-se");
                enviarParaTodos(saida, " saiu ", "do chat");

                clientes.remove(saida);
                conexao.close();
                updateStatus("Servidor conectado");
                
            } catch (IOException e) {
                logMessage("ERRO", "Erro com cliente " + nomeCliente + ": " + e.getMessage());
                
                if (saida != null) {
                    clientes.remove(saida);
                    updateStatus("Servidor conectado");
                }
            }
        }

        // Método para enviar mensagem para todos os outros clientes
        public void enviarParaTodos(PrintStream saida, String acao, String linha) {
            Enumeration e = clientes.elements();
            
            while (e.hasMoreElements()) {
                PrintStream chat = (PrintStream) e.nextElement();
                if (chat != saida) {
                    chat.println(nomeCliente + acao + linha);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PrjServidorChat_Thread_Form_Filtro().setVisible(true);
            }
        });
    }
}