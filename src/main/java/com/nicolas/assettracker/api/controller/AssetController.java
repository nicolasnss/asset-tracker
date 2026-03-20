package com.nicolas.assettracker.api.controller;

import com.nicolas.assettracker.domain.entity.Asset;
import com.nicolas.assettracker.domain.repository.AssetRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetRepository assetRepository;

    @GetMapping
    public Page<Asset> listar(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    @PostMapping
    // O @Valid garante que o objeto 'asset' seja validado antes de entrar no método
    public ResponseEntity<Asset> adicionar(@Valid @RequestBody Asset asset) {
        Asset savedAsset = assetRepository.save(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAsset);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> buscarPorId(@PathVariable Long id) {
        return assetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    // Também validamos no PUT para evitar que alguém "limpe" um nome que já existia
    public ResponseEntity<Asset> atualizar(@PathVariable Long id, @Valid @RequestBody Asset asset) {
        if (!assetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        asset.setId(id);
        Asset savedAsset = assetRepository.save(asset);
        return ResponseEntity.ok(savedAsset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!assetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        assetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public List<Asset> filtrarPorStatus(@PathVariable String status) {
        return assetRepository.findByStatus(status);
    }
}