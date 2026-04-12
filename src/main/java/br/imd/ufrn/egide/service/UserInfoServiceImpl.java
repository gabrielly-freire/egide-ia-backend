package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.UserInfoDTO;
import br.imd.ufrn.egide.entity.DepartmentEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.mapper.UserInfoMapper;
import br.imd.ufrn.egide.repository.DepartmentRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final DepartmentRepository departmentRepository;
    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    public UserInfoDTO save(UserInfoDTO userInfo) {
        if (userInfoRepository.existsUserInfoByEmail(userInfo.email())) {
            throw new BusinessException("Já existe um usuário com este email.", HttpStatus.CONFLICT);
        }

        if (userInfoRepository.existsUserInfoByUsername(userInfo.username())) {
            throw new BusinessException("Já existe um usuário com este username.", HttpStatus.CONFLICT);
        }

        DepartmentEntity department = departmentRepository.findById(userInfo.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));

        UserInfoEntity user = userInfoMapper.toUserInfoEntity(userInfo);
        user.setDepartment(department);
        user.setPassword(passwordEncoder.encode(userInfo.password()));
        user = userInfoRepository.save(user);
        return userInfoMapper.toUserInfoDTO(user);
    }

    public UserInfoDTO get(Long id) {
        UserInfoEntity user = userInfoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado"));
        return userInfoMapper.toUserInfoDTO(user);
    }

    public Page<UserInfoDTO> list(Pageable pageable) {
        Page<UserInfoEntity> users = userInfoRepository.findAllPage(pageable);
        return users.map(userInfoMapper::toUserInfoDTO);
    }

    public UserInfoDTO update(Long id, UserInfoDTO userInfo) {
        userInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));

        DepartmentEntity department = departmentRepository.findById(userInfo.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));

        UserInfoEntity user = userInfoMapper.toUserInfoEntity(userInfo);
        user.setId(id);
        user.setDepartment(department);
        user.setPassword(passwordEncoder.encode(userInfo.password()));
        user = userInfoRepository.save(user);
        return userInfoMapper.toUserInfoDTO(user);
    }

    public void delete(Long id) {
        userInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));
        //TODO: regras de exclusão -> quando é possível excluir ??

        userInfoRepository.deleteById(id);
    }

    public UserInfoEntity findById(Long id) {
        return userInfoRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

}
