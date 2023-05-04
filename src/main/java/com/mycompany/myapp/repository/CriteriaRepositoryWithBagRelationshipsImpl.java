package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Criteria;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CriteriaRepositoryWithBagRelationshipsImpl implements CriteriaRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Criteria> fetchBagRelationships(Optional<Criteria> criteria) {
        return criteria.map(this::fetchActionParameters);
    }

    @Override
    public Page<Criteria> fetchBagRelationships(Page<Criteria> criteria) {
        return new PageImpl<>(fetchBagRelationships(criteria.getContent()), criteria.getPageable(), criteria.getTotalElements());
    }

    @Override
    public List<Criteria> fetchBagRelationships(List<Criteria> criteria) {
        return Optional.of(criteria).map(this::fetchActionParameters).orElse(Collections.emptyList());
    }

    Criteria fetchActionParameters(Criteria result) {
        return entityManager
            .createQuery(
                "select criteria from Criteria criteria left join fetch criteria.actionParameters where criteria is :criteria",
                Criteria.class
            )
            .setParameter("criteria", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Criteria> fetchActionParameters(List<Criteria> criteria) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, criteria.size()).forEach(index -> order.put(criteria.get(index).getId(), index));
        List<Criteria> result = entityManager
            .createQuery(
                "select distinct criteria from Criteria criteria left join fetch criteria.actionParameters where criteria in :criteria",
                Criteria.class
            )
            .setParameter("criteria", criteria)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
