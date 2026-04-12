package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.ReportAiAnalysedDTO;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportAiAnalysedMapper {

    @Mapping(source = "report.id", target = "reportId")
    ReportAiAnalysedDTO toDTO(ReportAiAnalysedEntity entity);

    @Mapping(source = "reportId", target = "report.id")
    @Mapping(target = "report.reportAiAnalysed", ignore = true)
    @Mapping(target = "report.reportProcessed", ignore = true)
    ReportAiAnalysedEntity toEntity(ReportAiAnalysedDTO dto);
}
