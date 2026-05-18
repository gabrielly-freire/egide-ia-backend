package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// Mapper MapStruct para conversão entre ReportEntity e ReportDTO.
public interface ReportMapper {

    @Mapping(source = "userInfo.id", target = "userInfoId")
    @Mapping(source = "ouvidor.id", target = "ouvidorId")
    @Mapping(source = "ouvidor.name", target = "ouvidorName")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    ReportDTO toDTO(ReportEntity entity);

    @Mapping(source = "userInfoId", target = "userInfo.id")
    @Mapping(source = "ouvidorId", target = "ouvidor.id")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "files", ignore = true)
    @Mapping(target = "protocolNumber", ignore = true)
    @Mapping(target = "denouncedUser", ignore = true)
    @Mapping(target = "phase3NotifiedAt", ignore = true)
    @Mapping(target = "repassCount", ignore = true)
    @Mapping(target = "reportProcessed", ignore = true)
    @Mapping(target = "reportAiAnalysed", ignore = true)
    @Mapping(target = "preliminaryReport", ignore = true)
    @Mapping(target = "defense", ignore = true)
    @Mapping(target = "finalReport", ignore = true)
    @Mapping(target = "appealReport", ignore = true)
    @Mapping(target = "appeals", ignore = true)
    ReportEntity toEntity(ReportDTO dto);

}
