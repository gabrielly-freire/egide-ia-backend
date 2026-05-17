package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "user_info")
@SQLRestriction(value = "active = true")
// Entidade de usuário do sistema; implementa UserDetails para integração com Spring Security.
// A autoridade é construída como "ROLE_<role>" para compatibilidade com @PreAuthorize("hasRole(...)").
// O @SQLRestriction filtra usuários com active = false automaticamente em todas as queries JPA.
public class UserInfoEntity extends BaseEntity implements UserDetails {

    @Column(unique = true)
    private String email;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @OneToMany(mappedBy = "userInfo")
    private List<ReportEntity> reports;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
