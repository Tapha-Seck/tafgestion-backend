package sn.tafgestion.backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.tafgestion.backend.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.userId = :userId")
    void deleteByUserId(UUID userId);
}