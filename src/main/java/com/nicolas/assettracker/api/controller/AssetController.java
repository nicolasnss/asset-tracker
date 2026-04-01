package com.nicolas.assettracker.api.controller;

import com.nicolas.assettracker.api.dto.AssetRequestDTO;
import com.nicolas.assettracker.api.dto.AssetResponseDTO;
import com.nicolas.assettracker.domain.entity.Asset;
<<<<<<< HEAD
=======
import com.nicolas.assettracker.domain.repository.AssetRepository;
>>>>>>> ef8d7a4 (fix: configurando CORS global e preparando endpoint de dashboard)
import com.nicolas.assettracker.domain.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

<<<<<<< HEAD
    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<AssetResponseDTO> cadastrar(@RequestBody @Valid AssetRequestDTO dto) {
        Asset assetSalvo = assetService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.toResponseDTO(assetSalvo));
=======
    private final AssetRepository assetRepository;
    private final AssetService assetService;

    @GetMapping
    public Page<AssetResponseDTO> listar(Pageable pageable) {
        // Converte a página de Entities para uma página de DTOs de resposta
        return assetRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @PostMapping
    public ResponseEntity<AssetResponseDTO> cadastrar(@Valid @RequestBody AssetRequestDTO dto) {
        Asset salvo = assetService.salvarAtivo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
>>>>>>> ef8d7a4 (fix: configurando CORS global e preparando endpoint de dashboard)
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AssetRequestDTO dto) {
        Asset assetAtualizado = assetService.atualizar(id, dto);
        return ResponseEntity.ok(assetService.toResponseDTO(assetAtualizado));
    }

<<<<<<< HEAD
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        assetService.deletar(id);
        return ResponseEntity.noContent().build();
    }
=======
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return assetService.getDashboardStats();
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
>>>>>>> ef8d7a4 (fix: configurando CORS global e preparando endpoint de dashboard)

    @GetMapping
    public ResponseEntity<Page<AssetResponseDTO>> listar(Pageable pageable) {
        Page<AssetResponseDTO> pagina = assetService.listarTodos(pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/patrimonio/{tag}")
    public ResponseEntity<AssetResponseDTO> buscarPorPatrimonio(@PathVariable String tag) {
        return ResponseEntity.ok(assetService.buscarPorTag(tag));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> obterDashboard() {
        // Agora o Angular conseguirá chamar este endpoint sem erro de CORS
        return ResponseEntity.ok(assetService.gerarDashboard());
    }
}