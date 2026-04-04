package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportDTO toDTO(ReportEntity entity);
    ReportEntity toEntity(ReportDTO dto);
}