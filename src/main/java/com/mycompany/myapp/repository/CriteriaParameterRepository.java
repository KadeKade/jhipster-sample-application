package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CriteriaParameter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CriteriaParameter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriteriaParameterRepository extends JpaRepository<CriteriaParameter, Long> {}
