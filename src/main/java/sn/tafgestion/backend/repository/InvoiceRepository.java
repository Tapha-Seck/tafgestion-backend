package sn.tafgestion.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.tafgestion.backend.model.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByNumber(String number);

    boolean existsByNumber(String number);

    Page<Invoice> findByStatus(String status, Pageable pageable);

    Page<Invoice> findByClientId(Long clientId, Pageable pageable);

    Page<Invoice> findByDateBetween(
            LocalDate start, LocalDate end, Pageable pageable);

    Page<Invoice> findByStatusAndDateBetween(
            String status, LocalDate start,
            LocalDate end, Pageable pageable);

    @Query("SELECT MAX(i.id) FROM Invoice i")
    Long findMaxId();

    @Query("SELECT COALESCE(SUM(i.totalTtc), 0) " +
            "FROM Invoice i WHERE i.status = 'PAID' " +
            "AND i.date BETWEEN :start AND :end")
    BigDecimal sumPaidBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(i.totalTtc), 0) " +
            "FROM Invoice i WHERE i.status = 'PENDING' " +
            "AND i.date BETWEEN :start AND :end")
    BigDecimal sumPendingBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}