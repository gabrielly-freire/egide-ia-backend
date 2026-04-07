package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(source = "userInfo.id", target = "userInfoId")
    ReportDTO toDTO(ReportEntity entity);

    @Mapping(source = "userInfoId", target = "userInfo.id")
    ReportEntity toEntity(ReportDTO dto);
}
