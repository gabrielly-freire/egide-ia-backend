package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.DepartmentDTO;
import br.imd.ufrn.egide.entity.DepartmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDTO toDTO(DepartmentEntity entity);

    DepartmentEntity toEntity(DepartmentDTO dto);
}
