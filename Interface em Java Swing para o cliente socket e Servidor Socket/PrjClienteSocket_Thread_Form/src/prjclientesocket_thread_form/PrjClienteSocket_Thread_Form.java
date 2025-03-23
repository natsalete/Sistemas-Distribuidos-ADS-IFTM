package prjclientesocket_thread_form;

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
import java.net.Socket;

/**
 *
 * @author natsa
 */
public class PrjClienteSocket_Thread_Form extends JFrame {

   private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private PrintStream saida;
    private Socket conexao;
    private boolean conectado = false;
    private String meuNome;
    
    public PrjClienteSocket_Thread_Form() {
        initComponents();
        solicitarNomeEConectar();
    }
    
    private void initComponents() {
        setTitle("Chat Cliente");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        // Área de chat (não editável)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Painel inferior com campo de texto e botão
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        
        // Adiciona ação ao botão de enviar
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });
        
        // Também envia ao pressionar Enter no campo de texto
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });
        
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        
        // Adiciona componentes ao frame
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Lida com o fechamento da janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar();
            }
        });
    }
    
    private void solicitarNomeEConectar() {
        meuNome = JOptionPane.showInputDialog(this, "Digite seu nome:");
        
        if (meuNome == null || meuNome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome inválido. Fechando aplicação.");
            System.exit(0);
            return;
        }
        
        String serverIP = JOptionPane.showInputDialog(this, "Digite o IP do servidor:", "127.0.0.1");
        
        if (serverIP == null) {
            System.exit(0);
            return;
        }
        
        conectarAoServidor(serverIP, 2222);
    }
    
    private void conectarAoServidor(String serverIP, int porta) {
        try {
            conexao = new Socket(serverIP, porta);
            saida = new PrintStream(conexao.getOutputStream());
            saida.println(meuNome);
            
            // Inicia thread para receber mensagens
            Thread receptorMensagens = new Thread(new ReceptorMensagens(conexao));
            receptorMensagens.start();
            
            conectado = true;
            appendToChatArea("Conectado ao servidor com sucesso!");
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Erro ao conectar ao servidor: " + ex.getMessage(), 
                    "Erro de Conexão", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void enviarMensagem() {
        if (!conectado) {
            JOptionPane.showMessageDialog(this, "Você não está conectado ao servidor.");
            return;
        }
        
        String mensagem = messageField.getText().trim();
        if (!mensagem.isEmpty()) {
            saida.println(mensagem);
            appendToChatArea("Você: " + mensagem);
            messageField.setText("");
        }
    }
    
    private void desconectar() {
        if (conectado && conexao != null) {
            try {
                saida.println("");  // Envia mensagem vazia para sinalizar a saída
                conexao.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // Método para adicionar texto à área de chat com scroll automático
    public void appendToChatArea(String texto) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatArea.append(texto + "\n");
                // Rola automaticamente para o final
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        });
    }
    
    // Classe interna para receber mensagens em uma thread separada
    private class ReceptorMensagens implements Runnable {
        private Socket socket;
        
        public ReceptorMensagens(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String linha;
                
                while (true) {
                    linha = entrada.readLine();
                    
                    if (linha == null) {
                        appendToChatArea("Conexão encerrada pelo servidor.");
                        break;
                    }
                    
                    appendToChatArea(linha);
                }
                
            } catch (IOException ex) {
                if (!socket.isClosed()) {
                    appendToChatArea("Erro ao receber mensagem: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } finally {
                conectado = false;
            }
        }
    }
    
    // Método main para iniciar a aplicação
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PrjClienteSocket_Thread_Form().setVisible(true);
            }
        });
    }
    
}
