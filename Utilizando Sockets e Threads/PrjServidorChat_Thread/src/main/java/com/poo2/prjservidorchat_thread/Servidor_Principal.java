package com.poo2.prjservidorchat_thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author natsa
 */
public class Servidor_Principal extends Thread {
    
    private static Vector clientes;
    private Socket conexao;
    private String meuNome;

    public static void main(String[] args) throws IOException {
        clientes = new Vector();
        try {
            ServerSocket ss= new ServerSocket(2222);
              
            while(true){
                System.out.println("Esperando um clinete realizar a conexao...");
                Socket con = ss.accept();
                System.out.println("Conexao realizada!!!");
                  
                Thread t = new Servidor_Principal(con);
                  
                t.start();
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public Servidor_Principal(Socket ss){
        conexao = ss;
    }
    
    @Override
    public void run(){
        try {
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(conexao.getInputStream()));
            PrintStream saida = new PrintStream(conexao.getOutputStream());
            
            meuNome = entrada.readLine();
            
            if(meuNome == null){
                return;
            }
            
            clientes.add(saida);
            
            String linha = entrada.readLine();
            
            while(linha != null && !(linha.trim().equals(""))){
                enviarParaTodos(saida, " falou: ", linha);
                linha = entrada.readLine();
            }
            
            enviarParaTodos(saida, " saiu ", " do chat");
          
            clientes.remove(saida);
            conexao.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void enviarParaTodos(PrintStream saida, String acao, String linha){
        Enumeration e = clientes.elements();
        
        while(e.hasMoreElements()){
            PrintStream chat = (PrintStream) e.nextElement();
            if(chat != saida){
                chat.println(meuNome+acao+linha);
            }
        }
    }
}
