package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.UserInfoDTO;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Interface de serviço para gerenciamento de usuários do sistema.
public interface UserInfoService {

    // Cria novo usuário; valida unicidade de email e username; criptografa a senha com BCrypt.
    UserInfoDTO save(UserInfoDTO userInfo);

    // Atualiza todos os dados de um usuário existente; re-criptografa a senha.
    UserInfoDTO update(Long id, UserInfoDTO userInfo);

    // Realiza soft-delete do usuário pelo id.
    void delete(Long id);

    // Retorna o usuário pelo id em formato DTO; lança ResourceNotFoundException se não encontrado.
    UserInfoDTO get(Long id);

    // Retorna a listagem paginada de usuários ativos.
    Page<UserInfoDTO> list(Pageable pageable);

    // Retorna a entidade do usuário pelo id; utilizado internamente por outros services.
    UserInfoEntity findById(Long id);
}
