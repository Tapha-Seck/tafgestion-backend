package sn.tafgestion.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.tafgestion.backend.model.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Page<Client> findByActiveTrue(Pageable pageable);

    Page<Client> findByActiveTrueAndNameContainingIgnoreCase(
            String name, Pageable pageable);

    Optional<Client> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByEmail(String email);

    @Query("SELECT MAX(c.id) FROM Client c")
    Long findMaxId();
}