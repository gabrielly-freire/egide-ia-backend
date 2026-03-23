package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.UserInfoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends GenericRepository<UserInfoEntity> {

    boolean existsUserInfoByEmail(String email);

    boolean existsUserInfoByUsername(String username);

}
