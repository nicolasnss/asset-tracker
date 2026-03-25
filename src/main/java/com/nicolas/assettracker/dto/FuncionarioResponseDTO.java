package com.nicolas.assettracker.api.dto;

import lombok.Data;

@Data
public class FuncionarioResponseDTO {
    private Long id;
    private String nome;
    private String matricula;
    private String departamento;
    // Note: Ocultamos o e-mail se não for necessário na listagem geral por privacidade
}