package prjclientesocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author natsa
 */
public class PrjClienteSocket {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        try{
            Socket con = new Socket("192.168.54.195", 2222);
            
            BufferedReader entrada = new BufferedReader(new InputStreamReader(con.getInputStream()));
            
            PrintStream saida = new PrintStream(con.getOutputStream());
            
            String linha;
            
            BufferedReader entradaTeclado = new BufferedReader( new InputStreamReader(System.in));
            
            while(true){
                System.out.println(">");
                
                linha = entradaTeclado.readLine();
                
                saida.println(linha);
                
                linha = entrada.readLine();
                
                if(linha == null){
                    System.out.println("Conexao encerrada!");
                    break;
                }
                System.out.println(linha);
            }
        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
