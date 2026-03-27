package com.nicolas.assettracker.api.controller;

import com.nicolas.assettracker.api.dto.AssetRequestDTO;
import com.nicolas.assettracker.api.dto.AssetResponseDTO;
import com.nicolas.assettracker.domain.entity.Asset;
import com.nicolas.assettracker.domain.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<AssetResponseDTO> cadastrar(@RequestBody @Valid AssetRequestDTO dto) {
        Asset assetSalvo = assetService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.toResponseDTO(assetSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AssetRequestDTO dto) {
        Asset assetAtualizado = assetService.atualizar(id, dto);
        return ResponseEntity.ok(assetService.toResponseDTO(assetAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        assetService.deletar(id);
        return ResponseEntity.noContent().build();
    }

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