package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.BaseEntity;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

// Repositório genérico base que implementa soft-delete e consultas filtradas por active = true.
// Todas as operações de exclusão marcam o campo active = false em vez de remover o registro do banco.
// As queries sobrescritas garantem que registros inativos sejam excluídos dos resultados automaticamente.
@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    @Override
    @Transactional
    default void deleteById(@NotNull Long id) {
        Optional<T> entity = findById(id);
        if (entity.isEmpty()) {
            throw new BusinessException("A entidade com id: " + id + "não foi encontrada.", HttpStatus.BAD_REQUEST);
        }

        entity.get().setActive(false);
        save(entity.get());
    }

    @Override
    @Transactional
    default void delete(T obj) {
        obj.setActive(false);
        save(obj);
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> arg0) {
        arg0.forEach(
                entity -> deleteById(entity.getId()));
    }

    @Query("select e from #{#entityName} e where e.active = true")
    List<T> findAll();

    @Query("select e from #{#entityName} e where e.active = true")
    Page<T> findAllPage(Pageable pageable);

    @Query("select e from #{#entityName} e where e.id = ?1 and e.active = true")
    Optional<T> findById(Long id);

}

