package sn.tafgestion.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.tafgestion.backend.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(
            String name, Pageable pageable);

    Optional<Product> findByReference(String reference);

    boolean existsByReference(String reference);

    List<Product> findByActiveTrueAndStockLessThan(Integer threshold);

    @Query("SELECT MAX(p.id) FROM Product p")
    Long findMaxId();
}
