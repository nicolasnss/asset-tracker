package com.nicolas.assettracker.domain.repository;

import com.nicolas.assettracker.domain.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByStatus(String status);
<<<<<<< HEAD
    Optional<Asset> findByTagPatrimonio(String tagPatrimonio);
=======
>>>>>>> ef8d7a4 (fix: configurando CORS global e preparando endpoint de dashboard)
    long countByStatus(String status);
}
