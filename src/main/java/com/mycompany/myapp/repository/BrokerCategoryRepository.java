package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BrokerCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BrokerCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BrokerCategoryRepository extends JpaRepository<BrokerCategory, Long> {}
