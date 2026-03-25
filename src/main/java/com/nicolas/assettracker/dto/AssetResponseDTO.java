package com.nicolas.assettracker.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssetResponseDTO {
    private Long id;
    private String tagPatrimonio;
    private String nome;
    private String tipo;
    private String status;
    private String descricao;

    // Em vez de mandar o objeto Funcionario inteiro,
    // mandamos apenas o nome do responsável para simplificar a vida de quem lê.
    private String nomeResponsavel;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}