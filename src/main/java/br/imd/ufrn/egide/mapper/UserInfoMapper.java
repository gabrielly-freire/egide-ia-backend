package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.UserInfoDTO;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// Mapper MapStruct para conversão entre UserInfoEntity e UserInfoDTO.
public interface UserInfoMapper {

    @Mapping(source = "department.id", target = "departmentId")
    UserInfoDTO toUserInfoDTO(UserInfoEntity userInfo);

    @Mapping(source = "departmentId", target = "department.id")
    UserInfoEntity toUserInfoEntity(UserInfoDTO userInfoDTO);
}
