package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SQLRestriction(value = "active = true")
public class UserInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
