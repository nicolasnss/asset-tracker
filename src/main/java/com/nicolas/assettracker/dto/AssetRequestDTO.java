package com.nicolas.assettracker.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetRequestDTO {

    @NotBlank(message = "A tag de patrimônio é obrigatória")
    private String tagPatrimonio;

    @NotBlank(message = "O nome do ativo é obrigatório")
    private String nome;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    @NotBlank(message = "O status é obrigatório")
    private String status;

    private String descricao;

    private Long funcionarioId; // Note que o ID vem aqui agora!
}