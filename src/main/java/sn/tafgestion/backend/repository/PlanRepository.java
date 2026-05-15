package sn.tafgestion.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.tafgestion.backend.model.Plan;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByName(String name);
}