package br.imd.ufrn.egide.domain;

import br.imd.ufrn.egide.enums.Role;
import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String email;
    private String name;
    private String username;
    private String password;
    private Role role;
}
