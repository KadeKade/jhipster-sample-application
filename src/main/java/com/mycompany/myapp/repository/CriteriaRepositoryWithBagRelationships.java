package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Criteria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CriteriaRepositoryWithBagRelationships {
    Optional<Criteria> fetchBagRelationships(Optional<Criteria> criteria);

    List<Criteria> fetchBagRelationships(List<Criteria> criteria);

    Page<Criteria> fetchBagRelationships(Page<Criteria> criteria);
}
