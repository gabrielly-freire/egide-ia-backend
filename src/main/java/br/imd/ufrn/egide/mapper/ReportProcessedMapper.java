package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.ReportProcessedDTO;
import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// Mapper MapStruct para conversão entre ReportProcessedEntity e ReportProcessedDTO.
public interface ReportProcessedMapper {

    @Mapping(source = "report.id", target = "reportId")
    ReportProcessedDTO toDTO(ReportProcessedEntity entity);

    @Mapping(source = "reportId", target = "report.id")
    @Mapping(target = "report.reportAiAnalysed", ignore = true)
    @Mapping(target = "report.reportProcessed", ignore = true)
    ReportProcessedEntity toEntity(ReportProcessedDTO dto);
}
