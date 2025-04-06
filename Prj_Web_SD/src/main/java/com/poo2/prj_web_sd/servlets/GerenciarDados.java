/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.poo2.prj_web_sd.servlets;

import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author natsa
 */
@WebServlet(name = "GerenciarDados", urlPatterns = {"/GerenciarDados"})
public class GerenciarDados extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String acao = request.getParameter("acao");
        String parteAcao[] = acao.split("-");
        int index = 0;
        if(parteAcao.length > 1){
            if(parteAcao[0].equals("Mostra")) {
                acao = "Mostra_Obj";
                index = Integer.parseInt(parteAcao[1].trim())-1;
            } else if(parteAcao[0].equals("Editar")) {
                acao = "Editar_Obj";
                index = Integer.parseInt(parteAcao[1].trim())-1;
            } else if(parteAcao[0].equals("Excluir")) {
                acao = "Excluir_Obj";
                index = Integer.parseInt(parteAcao[1].trim())-1;
            }
        }
        System.out.println(acao);
        System.out.println(index);
        try{
            switch(acao){
                case "Salvar":
                    salvarPets(request, response);
                    break;
                case "Atualizar":
                    atualizarPet(request, response);
                    break;
                case "Listar":
                    getPets(request, response);
                    break;
                case "Mostra_Obj":
                    getPets(request, response, index);
                    break;
                case "Editar_Obj":
                    editarPet(request, response, index);
                    break;
                case "Excluir_Obj":
                    excluirPet(request, response, index);
                    break;
            }
        }catch(Exception ex){
            throw new ServletException(ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    
     private void salvarPets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       Pet pet = new Pet();
       pet.setNome(request.getParameter("nome"));
       pet.setRaca(request.getParameter("raca"));
       pet.setPorte(request.getParameter("porte"));
       pet.setDataNasc(request.getParameter("data_nasc"));
       pet.setCor(request.getParameter("cor"));
       
       // Passa o ServletContext para a classe GravarArquivo
       GravarArquivo gravarArq = new GravarArquivo();
       gravarArq.escreverArq(pet, getServletContext());
       
       String path = "/TestePaginaServlet.jsp";
       RequestDispatcher dispatcher = request.getRequestDispatcher(path);
       dispatcher.forward(request, response);
    }
    
    private void getPets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Passa o ServletContext para a classe BuscarArquivo
        BuscarArquivo buscarArq = new BuscarArquivo();
        List<Pet> lstPets = buscarArq.lerArq(getServletContext());
        
        //System.out.println(lstPets.size());
        request.setAttribute("lstPets", lstPets);
        String path = "/TestePaginaServlet.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
    
    private void getPets(HttpServletRequest request, HttpServletResponse response, int index)
            throws ServletException, IOException {
        // Passa o ServletContext para a classe BuscarArquivo
        BuscarArquivo buscarArq = new BuscarArquivo();
        List<Pet> lstPets = buscarArq.lerArq(getServletContext());
        
        Pet pet = lstPets.get(index);
        request.setAttribute("pet", pet);
        String path = "/TestePaginaServlet.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
    
    private void editarPet(HttpServletRequest request, HttpServletResponse response, int index)
            throws ServletException, IOException {
        // Buscar a lista atual de pets
        BuscarArquivo buscarArq = new BuscarArquivo();
        List<Pet> lstPets = buscarArq.lerArq(getServletContext());
        
        // Obter o pet a ser editado
        Pet pet = lstPets.get(index);
        
        // Adicionar o pet e o índice ao request para o formulário de edição
        request.setAttribute("petEditar", pet);
        request.setAttribute("indexEditar", index);
        request.setAttribute("modoEdicao", true);
        
        // Redirecionar para a página JSP
        String path = "/TestePaginaServlet.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
    
    private void atualizarPet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obter o índice do pet a ser atualizado
        int index = Integer.parseInt(request.getParameter("indexEditar"));
        
        // Buscar a lista atual de pets
        BuscarArquivo buscarArq = new BuscarArquivo();
        List<Pet> lstPets = buscarArq.lerArq(getServletContext());
        
        // Atualizar os dados do pet no índice especificado
        Pet pet = lstPets.get(index);
        pet.setNome(request.getParameter("nome"));
        pet.setRaca(request.getParameter("raca"));
        pet.setPorte(request.getParameter("porte"));
        pet.setDataNasc(request.getParameter("data_nasc"));
        pet.setCor(request.getParameter("cor"));
        
        // Reescrever todo o arquivo com a lista atualizada
        reescreverArquivo(lstPets);
        
        // Redirecionar para a listagem
        request.setAttribute("lstPets", lstPets);
        String path = "/TestePaginaServlet.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
    
    private void excluirPet(HttpServletRequest request, HttpServletResponse response, int index)
            throws ServletException, IOException {
        // Buscar a lista atual de pets
        BuscarArquivo buscarArq = new BuscarArquivo();
        List<Pet> lstPets = buscarArq.lerArq(getServletContext());
        
        // Remover o pet com o índice especificado
        lstPets.remove(index);
        
        // Reescrever todo o arquivo com a lista atualizada
        reescreverArquivo(lstPets);
        
        // Redirecionar para a listagem
        request.setAttribute("lstPets", lstPets);
        String path = "/TestePaginaServlet.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
    
    private void reescreverArquivo(List<Pet> lstPets) throws IOException {
        Gson gson = new Gson();
        String dataDir = getServletContext().getRealPath("/WEB-INF/data");
        
        // Criar o diretório se não existir
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File f = new File(dataDir + File.separator + "Objetos2.txt");
        
        // Sobrescrever o arquivo (false = não append)
        try (FileWriter fw = new FileWriter(f, false)) {
            for (Pet pet : lstPets) {
                String petJson = gson.toJson(pet);
                fw.write(petJson + "\n");
            }
        }
    }
}
