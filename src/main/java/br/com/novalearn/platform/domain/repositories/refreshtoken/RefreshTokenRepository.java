package br.com.novalearn.platform.domain.repositories.refreshtoken;


import br.com.novalearn.platform.domain.entities.refreshtoken.RefreshToken;
import br.com.novalearn.platform.domain.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
    void deleteAllByUserId(Long userId);
}