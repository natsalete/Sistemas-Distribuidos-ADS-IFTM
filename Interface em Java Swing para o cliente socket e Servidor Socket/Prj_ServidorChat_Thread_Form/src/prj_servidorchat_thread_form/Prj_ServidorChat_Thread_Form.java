package prj_servidorchat_thread_form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author natsa
 */
public class Prj_ServidorChat_Thread_Form extends JFrame{
    
    private JTextArea logArea;
    private JScrollPane scrollPane;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JButton clearLogButton;
    private static Vector clientes;
    private ServerSocket serverSocket;
    private int serverPort = 2222;
    private boolean running = false;
    private Thread serverThread;

    public Prj_ServidorChat_Thread_Form() {
        initComponents();
        clientes = new Vector();
        startServer();
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
                    // Registra a mensagem no log
                    logMessage("MENSAGEM", nomeCliente + ": " + linha);
                    
                    // Envia para todos os clientes
                    enviarParaTodos(saida, " : ", linha);
                    
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
                new Prj_ServidorChat_Thread_Form().setVisible(true);
            }
        });
    }
}
