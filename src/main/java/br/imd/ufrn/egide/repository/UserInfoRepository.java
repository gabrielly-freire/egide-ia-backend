package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends GenericRepository<UserInfoEntity> {

    Optional<UserInfoEntity> findByUsername(String username);

    boolean existsUserInfoByEmail(String email);

    boolean existsUserInfoByUsername(String username);

    List<UserInfoEntity> findAllByRoleIn(List<Role> roles);

}
