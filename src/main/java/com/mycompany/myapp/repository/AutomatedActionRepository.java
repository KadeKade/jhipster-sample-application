package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AutomatedAction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AutomatedAction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AutomatedActionRepository extends JpaRepository<AutomatedAction, Long> {}
