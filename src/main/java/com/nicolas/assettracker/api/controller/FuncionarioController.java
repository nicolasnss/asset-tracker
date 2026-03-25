package com.nicolas.assettracker.api.controller;

import com.nicolas.assettracker.api.dto.FuncionarioRequestDTO;
import com.nicolas.assettracker.api.dto.FuncionarioResponseDTO;
import com.nicolas.assettracker.domain.entity.Funcionario;
import com.nicolas.assettracker.domain.repository.FuncionarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    @GetMapping
    public List<FuncionarioResponseDTO> listar() {
        return funcionarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> cadastrar(@Valid @RequestBody FuncionarioRequestDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setMatricula(dto.getMatricula());
        funcionario.setEmail(dto.getEmail());
        funcionario.setDepartamento(dto.getDepartamento());

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    private FuncionarioResponseDTO toResponseDTO(Funcionario funcionario) {
        FuncionarioResponseDTO response = new FuncionarioResponseDTO();
        response.setId(funcionario.getId());
        response.setNome(funcionario.getNome());
        response.setMatricula(funcionario.getMatricula());
        response.setDepartamento(funcionario.getDepartamento());
        return response;
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado");
        }

        // O JPA impedirá a exclusão se houver ativos vinculados (Constraint de FK)
        try {
            funcionarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível excluir um funcionário que possui ativos vinculados. Realize a devolução primeiro.");
        }
    }
}