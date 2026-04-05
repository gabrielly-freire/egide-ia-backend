package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.DepartmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    DepartmentDTO get(Long id);

    Page<DepartmentDTO> list(Pageable pageable);
}
