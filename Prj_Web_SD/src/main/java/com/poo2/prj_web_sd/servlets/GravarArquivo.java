package com.poo2.prj_web_sd.servlets;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletContext;

/**
 *
 * @author natsa
 */
public class GravarArquivo {
    
    public void escreverArq(Pet pet, ServletContext context) {
        // Usar um caminho relativo dentro da aplicação web
        String dataDir = context.getRealPath("/WEB-INF/data");
        
        // Criar o diretório se não existir
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File f = new File(dataDir + File.separator + "Objetos2.txt");
        Gson gson = new Gson();
        String msg = gson.toJson(pet);
        
        try (FileWriter fw = new FileWriter(f, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println(msg);
            // Recursos fechados automaticamente pelo try-with-resources
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}