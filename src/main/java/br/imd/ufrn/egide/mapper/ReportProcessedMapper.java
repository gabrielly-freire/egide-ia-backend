package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.ReportProcessedDTO;
import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportProcessedMapper {

    @Mapping(source = "report.id", target = "reportId")
    ReportProcessedDTO toDTO(ReportProcessedEntity entity);

    @Mapping(source = "reportId", target = "report.id")
    ReportProcessedEntity toEntity(ReportProcessedDTO dto);
}
