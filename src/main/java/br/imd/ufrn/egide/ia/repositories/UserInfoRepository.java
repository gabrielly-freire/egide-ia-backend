package br.imd.ufrn.egide.ia.repositories;

import br.imd.ufrn.egide.ia.models.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends GenericRepository<UserInfo> {

    boolean existsUserInfoByEmail(String email);

    boolean existsUserInfoByUsername(String username);

}
