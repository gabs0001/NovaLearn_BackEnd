package br.com.novalearn.platform.domain.entities.refreshtoken;

import br.com.novalearn.platform.core.exception.auth.TokenExpiredException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "T_SINL_REFRESH_TOKENS",
        indexes = {
                @Index(name = "IDX_SINL_REFRESH_TOKENS_HASH", columnList = "txt_token_hash"),
                @Index(name = "IDX_SINL_REFRESH_TOK_USER", columnList = "cod_user"),
                @Index(name = "IDX_SINL_REFRESH_TOK_EXPIRES", columnList = "dat_expires")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.REFRESH_TOKENS_SEQ,
        sequenceName = DatabaseSequences.REFRESH_TOKENS_SEQ,
        allocationSize = 1
)
public class RefreshToken extends BaseEntity {
    @Id
    @Column(name = "cod_refresh_token", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.REFRESH_TOKENS_SEQ)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_user", nullable = false)
    private User user;

    @Column(name = "txt_token_hash", nullable = false, length = 200)
    private String tokenHash;

    @Column(name = "dat_expires", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "dat_revoked")
    private LocalDateTime revokedAt;

    protected RefreshToken() {}

    public static RefreshToken create(User user, String tokenHash, LocalDateTime expiresAt, Long actorId, TimeProvider time) {
        RefreshToken token = new RefreshToken();

        if(user == null) throw new ValidationException("User is required.");
        token.user = user;

        if(tokenHash == null || tokenHash.isBlank()) throw new ValidationException("Token hash is required.");
        token.tokenHash = tokenHash;

        if(expiresAt == null) throw new ValidationException("Expire date is required.");
        token.expiresAt = expiresAt;

        token.activate();
        token.markAsNotDeleted();
        token.setCreatedAt(time.now());
        token.setCreatedBy(actorId);

        return token;
    }

    public void assertUsable(LocalDateTime now) {
        if(revoked) throw new TokenExpiredException("Refresh token revoked.");
        if(expiresAt.isBefore(now)) throw new TokenExpiredException("Refresh token expired.");
        //ensureActive();
        ensureNotDeleted();
    }

    public void rotate(String newHash, LocalDateTime newExpiresAt, Long actorId, LocalDateTime time) {
        assertUsable(time);
        this.tokenHash = newHash;
        this.expiresAt = newExpiresAt;
        auditUpdate(actorId, time);
    }

    public void revoke(Long actorId, LocalDateTime time) {
        ensureNotDeleted();
        if(this.revoked) return;
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
        auditUpdate(actorId, time);
    }

    public boolean isExpired(LocalDateTime now) { return expiresAt.isBefore(now); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getTokenHash() { return tokenHash; }
    public boolean isRevoked() { return revoked; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    @Override
    public String toString() {
        return "RefreshToken{" +
                ", userId=" + (user != null ? user.getId() : null) +
                ", revoked=" + revoked +
                ", expiresAt=" + expiresAt +
                '}';
    }
}