package com.nicolas.assettracker.api.controller;

import com.nicolas.assettracker.domain.entity.Asset;
import com.nicolas.assettracker.domain.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
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
    public List<Asset> listar() {
        return assetRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Asset> adicionar(@RequestBody Asset asset) {
        Asset savedAsset = assetRepository.save(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAsset);
    }
}
