package com.poo2.prj_web_sd.servlets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletContext;

/**
 *
 * @author natsa
 */
public class BuscarArquivo {
    
    public List lerArq(ServletContext context) throws IOException {
        Gson gson = new Gson();
        Pet p;
        List<Pet> lstPet = new ArrayList<Pet>();
        
        // Usar um caminho relativo dentro da aplicação web
        String dataDir = context.getRealPath("/WEB-INF/data");
        
        // Criar o diretório se não existir
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File f = new File(dataDir + File.separator + "Objetos2.txt");
        
        // Criar o arquivo se não existir
        if (!f.exists()) {
            f.createNewFile();
            return lstPet; // Retorna lista vazia se o arquivo acabou de ser criado
        }
        
        try (FileReader fr = new FileReader(f);
             BufferedReader br = new BufferedReader(fr)) {
            
            String linha = "";
            while(br.ready()){
                linha = br.readLine();
                if (linha != null && !linha.trim().isEmpty()) {
                    p = gson.fromJson(linha, Pet.class);
                    lstPet.add(p);
                }
            }
        } // Os recursos são fechados automaticamente
        
        return lstPet;
    }
}