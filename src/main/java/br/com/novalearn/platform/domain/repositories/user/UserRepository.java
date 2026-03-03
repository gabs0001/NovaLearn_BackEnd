package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedFalse(Long id);
    Optional<User> findByEmailAndDeletedFalse(Email email);
    Page<User> findAllByDeletedFalse(Pageable pageable);
    boolean existsByEmailAndDeletedFalse(Email email);
    boolean existsByCpfAndDeletedFalse(Cpf cpf);
    boolean existsByEmailAndIdNotAndDeletedFalse(Email email, Long id);
    boolean existsByCpfAndIdNotAndDeletedFalse(Cpf cpf, Long id);
}