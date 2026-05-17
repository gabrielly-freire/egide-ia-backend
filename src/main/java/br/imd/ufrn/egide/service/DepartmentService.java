package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.DepartmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Interface de serviço de consulta de departamentos institucionais.
public interface DepartmentService {

    // Retorna o departamento pelo id; lança ResourceNotFoundException se não encontrado.
    DepartmentDTO get(Long id);

    // Retorna a listagem paginada de departamentos ativos.
    Page<DepartmentDTO> list(Pageable pageable);
}
