package br.imd.ufrn.egide.ia.services;

import br.imd.ufrn.egide.ia.dtos.UserInfoDTO;
import br.imd.ufrn.egide.ia.mappers.UserInfoMapper;
import br.imd.ufrn.egide.ia.models.UserInfo;
import br.imd.ufrn.egide.ia.repositories.UserInfoRepository;
import br.imd.ufrn.egide.ia.utils.exceptions.BusinessException;
import br.imd.ufrn.egide.ia.utils.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;

    public UserInfoDTO save(UserInfoDTO userInfo) {
        if (userInfoRepository.existsUserInfoByEmail(userInfo.email())){
            throw new BusinessException("Já existe um usuário com este email.", HttpStatus.CONFLICT);
        }

        if (userInfoRepository.existsUserInfoByUsername(userInfo.username())){
            throw new BusinessException("Já existe um usuário com este username.", HttpStatus.CONFLICT);
        }

        UserInfo user = userInfoMapper.toUserInfo(userInfo);
        user = userInfoRepository.save(user);
        return userInfoMapper.toUserInfoDTO(user);
    }

    public UserInfoDTO get(Long id) {
        UserInfo user = userInfoRepository.findById(id).orElseThrow(
                () ->  new ResourceNotFoundException("Usuário não encontrado"));
        return userInfoMapper.toUserInfoDTO(user);
    }

    public Page<UserInfoDTO> list(Pageable pageable) {
        Page<UserInfo> users = userInfoRepository.findAllPage(pageable);
        return users.map(userInfoMapper::toUserInfoDTO);
    }

    public UserInfoDTO update(Long id, UserInfoDTO userInfo) {
        userInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));

        UserInfo user = userInfoMapper.toUserInfo(userInfo);
        user.setId(id);
        user = userInfoRepository.save(user);
        return userInfoMapper.toUserInfoDTO(user);
    }

    public void delete(Long id) {
        userInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));
        //TODO: regras de exclusão -> quando é possível excluir ??

        userInfoRepository.deleteById(id);
    }

}
