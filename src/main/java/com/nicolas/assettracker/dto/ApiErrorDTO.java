package com.nicolas.assettracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiErrorDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private List<String> details; // Para mostrar erros de validação de campos
}