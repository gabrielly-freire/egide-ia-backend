package br.imd.ufrn.egide.mapper;

import br.imd.ufrn.egide.dto.UserInfoDTO;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    UserInfoDTO toUserInfoDTO(UserInfoEntity userInfo);

    UserInfoEntity toUserInfoEntity(UserInfoDTO userInfoDTO);
}
