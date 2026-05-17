package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.DepartmentDTO;
import br.imd.ufrn.egide.entity.DepartmentEntity;
import br.imd.ufrn.egide.mapper.DepartmentMapper;
import br.imd.ufrn.egide.repository.DepartmentRepository;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
// Implementação de DepartmentService; realiza consultas de departamentos com mapeamento para DTO.
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    // Busca departamento pelo id e converte para DTO; lança ResourceNotFoundException se inativo ou inexistente.
    @Override
    public DepartmentDTO get(Long id) {
        DepartmentEntity entity = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));

        return departmentMapper.toDTO(entity);
    }

    // Retorna página de departamentos ativos mapeada para DTOs.
    @Override
    public Page<DepartmentDTO> list(Pageable pageable) {
        Page<DepartmentEntity> departments = departmentRepository.findAllPage(pageable);
        return departments.map(departmentMapper::toDTO);
    }
}
