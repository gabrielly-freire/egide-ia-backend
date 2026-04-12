package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.UserInfoDTO;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInfoService {

    UserInfoDTO save(UserInfoDTO userInfo);

    UserInfoDTO update(Long id, UserInfoDTO userInfo);

    void delete(Long id);

    UserInfoDTO get(Long id);

    Page<UserInfoDTO> list(Pageable pageable);

    UserInfoEntity findById(Long id);
}
