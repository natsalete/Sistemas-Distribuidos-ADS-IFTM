<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.poo2.prj_web_sd.servlets.Pet"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cadastro de Pets</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome para ícones -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .card {
            margin-bottom: 20px;
        }
        .btn-action {
            margin-right: 5px;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <div class="row">
            <div class="col-12">
                <h1 class="mb-4 text-primary">
                    <i class="fas fa-paw me-2"></i>Cadastro de Pets
                </h1>
            </div>
        </div>
        
        <%
            // Verificar se estamos em modo de edição
            boolean modoEdicao = request.getAttribute("modoEdicao") != null && (boolean)request.getAttribute("modoEdicao");
            Pet petEditar = null;
            int indexEditar = -1;
            
            if (modoEdicao) {
                petEditar = (Pet)request.getAttribute("petEditar");
                indexEditar = (int)request.getAttribute("indexEditar");
            }
        %>
        
        <!-- Formulário de cadastro/edição -->
        <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="<%= modoEdicao ? "fas fa-edit" : "fas fa-plus" %> me-2"></i>
                    <%= modoEdicao ? "Editar Pet" : "Novo Pet" %>
                </h5>
            </div>
            <div class="card-body">
                <form action="GerenciarDados" method="post">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="nome" class="form-label">Nome:</label>
                            <input type="text" class="form-control" id="nome" name="nome" 
                                   value="<%= modoEdicao ? petEditar.getNome() : "" %>" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="raca" class="form-label">Raça:</label>
                            <input type="text" class="form-control" id="raca" name="raca" 
                                   value="<%= modoEdicao ? petEditar.getRaca() : "" %>" required>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="porte" class="form-label">Porte:</label>
                            <input type="text" class="form-control" id="porte" name="porte" 
                                   value="<%= modoEdicao ? petEditar.getPorte() : "" %>" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="data_nasc" class="form-label">Data Nascimento:</label>
                            <input type="text" class="form-control" id="data_nasc" name="data_nasc" 
                                   value="<%= modoEdicao ? petEditar.getDataNasc() : "" %>" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="cor" class="form-label">Cor:</label>
                            <input type="text" class="form-control" id="cor" name="cor" 
                                   value="<%= modoEdicao ? petEditar.getCor() : "" %>" required>
                        </div>
                    </div>
                    
                    <%
                        if (modoEdicao) {
                            // Campos ocultos para edição
                    %>
                            <input type="hidden" name="indexEditar" value="<%= indexEditar %>">
                            <input type="hidden" name="acao" value="Atualizar">
                            <div class="d-flex">
                                <button type="submit" class="btn btn-success">
                                    <i class="fas fa-save me-1"></i> Atualizar
                                </button>
                                <a href="GerenciarDados?acao=Listar" class="btn btn-secondary ms-2">
                                    <i class="fas fa-times me-1"></i> Cancelar
                                </a>
                            </div>
                    <%
                        } else {
                            // Novo cadastro
                    %>
                            <input type="hidden" name="acao" value="Salvar">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-1"></i> Salvar
                            </button>
                    <%
                        }
                    %>
                </form>
            </div>
        </div>
        
        <!-- Botão para listar todos os pets -->
        <form action="GerenciarDados" method="post" class="mb-4">
            <input type="hidden" name="acao" value="Listar">
            <button type="submit" class="btn btn-info text-white">
                <i class="fas fa-list me-1"></i> Listar Todos os Pets
            </button>
        </form>
        
        <!-- Exibição do pet selecionado -->
        <%
            Pet pet = (Pet)request.getAttribute("pet");
            if(pet != null) {
        %>
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-info text-white">
                    <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Detalhes do Pet</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>Nome:</strong> <%= pet.getNome() %></p>
                            <p><strong>Raça:</strong> <%= pet.getRaca() %></p>
                            <p><strong>Porte:</strong> <%= pet.getPorte() %></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Data Nascimento:</strong> <%= pet.getDataNasc() %></p>
                            <p><strong>Cor:</strong> <%= pet.getCor() %></p>
                        </div>
                    </div>
                </div>
            </div>
        <%
            }
        %>
        
        <!-- Listagem de todos os pets -->
        <%
            List<Pet> lstPets = (List<Pet>)request.getAttribute("lstPets");
            if(lstPets != null && !lstPets.isEmpty()) {
        %>
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="fas fa-list me-2"></i>Lista de Pets</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Nome</th>
                                    <th>Raça</th>
                                    <th>Porte</th>
                                    <th>Data Nasc</th>
                                    <th>Cor</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for(int i = 0; i < lstPets.size(); i++) {
                                        Pet p = lstPets.get(i);
                                %>
                                    <tr>
                                        <td><%= i+1 %></td>
                                        <td><%= p.getNome() %></td>
                                        <td><%= p.getRaca() %></td>
                                        <td><%= p.getPorte() %></td>
                                        <td><%= p.getDataNasc() %></td>
                                        <td><%= p.getCor() %></td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="GerenciarDados?acao=Mostra-<%= i+1 %>" class="btn btn-sm btn-info text-white btn-action" title="Visualizar">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="GerenciarDados?acao=Editar-<%= i+1 %>" class="btn btn-sm btn-warning text-dark btn-action" title="Editar">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href="GerenciarDados?acao=Excluir-<%= i+1 %>" class="btn btn-sm btn-danger btn-action" 
                                                   onclick="return confirm('Tem certeza que deseja excluir este pet?')" title="Excluir">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer text-muted">
                    <small>Total de pets: <%= lstPets.size() %></small>
                </div>
            </div>
        <%
            } else if(request.getAttribute("lstPets") != null) {
        %>
            <div class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i> Nenhum pet cadastrado no sistema.
            </div>
        <%
            }
        %>
    </div>
    
    <!-- Bootstrap Bundle com Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>