package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CriteriaSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CriteriaSetRepositoryWithBagRelationships {
    Optional<CriteriaSet> fetchBagRelationships(Optional<CriteriaSet> criteriaSet);

    List<CriteriaSet> fetchBagRelationships(List<CriteriaSet> criteriaSets);

    Page<CriteriaSet> fetchBagRelationships(Page<CriteriaSet> criteriaSets);
}
