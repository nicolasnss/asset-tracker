package com.nicolas.assettracker.domain.service;

import com.nicolas.assettracker.api.dto.AssetRequestDTO;
import com.nicolas.assettracker.api.dto.AssetResponseDTO;
import com.nicolas.assettracker.domain.entity.Asset;
import com.nicolas.assettracker.domain.entity.Funcionario;
import com.nicolas.assettracker.domain.repository.AssetRepository;
import com.nicolas.assettracker.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final FuncionarioRepository funcionarioRepository;

    // 1. Salvar novo Ativo (Ajustado para setResponsavel)
    public Asset salvar(AssetRequestDTO dto) {
        Asset asset = Asset.builder() // Usando o @Builder que você tem na Entity
                .tagPatrimonio(dto.getTagPatrimonio())
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                .status(dto.getStatus())
                .descricao(dto.getDescricao())
                .build();

        if (dto.getFuncionarioId() != null) {
            Funcionario func = funcionarioRepository.findById(dto.getFuncionarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

            // Aqui estava o erro: o nome correto é setResponsavel
            asset.setResponsavel(func);
        }

        return assetRepository.save(asset);
    }

    // 2. Listar todos com Paginação
    public Page<AssetResponseDTO> listarTodos(Pageable pageable) {
        return assetRepository.findAll(pageable).map(this::toResponseDTO);
    }

    // 3. Buscar por Tag de Patrimônio
    public AssetResponseDTO buscarPorTag(String tag) {
        Asset asset = assetRepository.findByTagPatrimonio(tag)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrimônio não localizado: " + tag));
        return toResponseDTO(asset);
    }

    // 4. Gerar dados do Dashboard
    public Map<String, Long> gerarDashboard() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total_ativos", assetRepository.count());

        // Ajuste os nomes de status conforme o seu uso no banco
        stats.put("disponivel", assetRepository.countByStatus("Disponível"));
        stats.put("em_uso", assetRepository.countByStatus("Em Uso"));

        return stats;
    }

    // 5. Método Auxiliar: Converte Entidade para DTO de Resposta
    public AssetResponseDTO toResponseDTO(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setId(asset.getId());
        dto.setTagPatrimonio(asset.getTagPatrimonio());
        dto.setNome(asset.getNome());
        dto.setTipo(asset.getTipo());
        dto.setStatus(asset.getStatus());
        dto.setDescricao(asset.getDescricao());
        dto.setCreatedAt(asset.getCreatedAt());
        dto.setUpdatedAt(asset.getUpdatedAt());

        // Ajustado para getResponsavel()
        if (asset.getResponsavel() != null) {
            dto.setNomeResponsavel(asset.getResponsavel().getNome());
        } else {
            dto.setNomeResponsavel("Disponível no Estoque");
        }
        return dto;
    }
}