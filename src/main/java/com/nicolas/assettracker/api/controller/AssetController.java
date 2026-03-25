package com.nicolas.assettracker.api.controller;

import com.nicolas.assettracker.api.dto.AssetRequestDTO;
import com.nicolas.assettracker.api.dto.AssetResponseDTO;
import com.nicolas.assettracker.domain.entity.Asset;
import com.nicolas.assettracker.domain.entity.Funcionario;
import com.nicolas.assettracker.domain.repository.AssetRepository;
import com.nicolas.assettracker.domain.repository.FuncionarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetRepository assetRepository;
    private final FuncionarioRepository funcionarioRepository;

    @GetMapping
    public Page<AssetResponseDTO> listar(Pageable pageable) {
        // Converte a página de Entities para uma página de DTOs de resposta
        return assetRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @PostMapping
    public ResponseEntity<AssetResponseDTO> cadastrar(@Valid @RequestBody AssetRequestDTO dto) {
        Asset asset = new Asset();
        asset.setTagPatrimonio(dto.getTagPatrimonio());
        asset.setNome(dto.getNome());
        asset.setTipo(dto.getTipo());
        asset.setStatus(dto.getStatus());
        asset.setDescricao(dto.getDescricao());

        if (dto.getFuncionarioId() != null) {
            Funcionario responsavel = funcionarioRepository.findById(dto.getFuncionarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
            asset.setResponsavel(responsavel);
        }

        Asset salvo = assetRepository.save(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<AssetResponseDTO> devolver(@PathVariable Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo não encontrado"));

        asset.setResponsavel(null);
        asset.setStatus("DISPONIVEL");

        Asset atualizado = assetRepository.save(asset);
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    // Método auxiliar (Privado) para evitar repetir código de conversão
    private AssetResponseDTO toResponseDTO(Asset asset) {
        AssetResponseDTO response = new AssetResponseDTO();
        response.setId(asset.getId());
        response.setTagPatrimonio(asset.getTagPatrimonio());
        response.setNome(asset.getNome());
        response.setTipo(asset.getTipo());
        response.setStatus(asset.getStatus());
        response.setDescricao(asset.getDescricao());
        response.setCreatedAt(asset.getCreatedAt());
        response.setUpdatedAt(asset.getUpdatedAt());

        if (asset.getResponsavel() != null) {
            response.setNomeResponsavel(asset.getResponsavel().getNome());
        }
        return response;
    }
}