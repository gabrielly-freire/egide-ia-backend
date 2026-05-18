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
// Implementação de UserInfoService; gerencia criação, leitura, atualização e exclusão de usuários.
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final DepartmentRepository departmentRepository;
    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    // Cria novo usuário verificando unicidade de email e username; associa ao departamento informado.
    // A senha é codificada com BCrypt antes da persistência.
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

    // Retorna o usuário pelo id como DTO; lança ResourceNotFoundException se não encontrado ou inativo.
    public UserInfoDTO get(Long id) {
        UserInfoEntity user = userInfoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado"));
        return userInfoMapper.toUserInfoDTO(user);
    }

    // Retorna a listagem paginada de usuários ativos mapeada para DTOs.
    public Page<UserInfoDTO> list(Pageable pageable) {
        Page<UserInfoEntity> users = userInfoRepository.findAllPage(pageable);
        return users.map(userInfoMapper::toUserInfoDTO);
    }

    // Atualiza todos os dados do usuário; re-criptografa a senha e atualiza o departamento.
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

    // Realiza soft-delete do usuário; regras adicionais de exclusão estão pendentes (TODO no código).
    public void delete(Long id) {
        userInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));

        userInfoRepository.deleteById(id);
    }

    // Retorna a entidade de usuário pelo id; lança ResourceNotFoundException se não encontrado.
    public UserInfoEntity findById(Long id) {
        return userInfoRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

}
