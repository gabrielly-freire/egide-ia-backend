package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.UserInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends GenericRepository<UserInfoEntity> {

    Optional<UserInfoEntity> findByUsername(String username);

    boolean existsUserInfoByEmail(String email);

    boolean existsUserInfoByUsername(String username);

}
