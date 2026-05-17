package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.DepartmentDTO;
import br.imd.ufrn.egide.entity.DepartmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
// Mapper MapStruct para conversão entre DepartmentEntity e DepartmentDTO.
public interface DepartmentMapper {

    DepartmentDTO toDTO(DepartmentEntity entity);

    DepartmentEntity toEntity(DepartmentDTO dto);
}
