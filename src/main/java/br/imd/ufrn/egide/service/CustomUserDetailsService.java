package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Implementação de UserDetailsService para integração do Spring Security com o repositório de usuários.
// Utilizado pelo DaoAuthenticationProvider durante a autenticação via username e senha.
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoRepository repository;

    // Carrega o UserDetails (UserInfoEntity) pelo username; lança UsernameNotFoundException se não encontrado.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfoEntity user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return user;
    }
}
