package br.imd.ufrn.egide.ia.mappers;

import br.imd.ufrn.egide.ia.dtos.UserInfoDTO;
import br.imd.ufrn.egide.ia.models.UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    UserInfoDTO toUserInfoDTO(UserInfo userInfo);

    UserInfo toUserInfo(UserInfoDTO userInfoDTO);
}
