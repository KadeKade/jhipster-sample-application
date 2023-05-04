package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ActionParameter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ActionParameter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionParameterRepository extends JpaRepository<ActionParameter, Long> {}
