package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CriteriaSet;
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
public class CriteriaSetRepositoryWithBagRelationshipsImpl implements CriteriaSetRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CriteriaSet> fetchBagRelationships(Optional<CriteriaSet> criteriaSet) {
        return criteriaSet.map(this::fetchBrokerCategories);
    }

    @Override
    public Page<CriteriaSet> fetchBagRelationships(Page<CriteriaSet> criteriaSets) {
        return new PageImpl<>(
            fetchBagRelationships(criteriaSets.getContent()),
            criteriaSets.getPageable(),
            criteriaSets.getTotalElements()
        );
    }

    @Override
    public List<CriteriaSet> fetchBagRelationships(List<CriteriaSet> criteriaSets) {
        return Optional.of(criteriaSets).map(this::fetchBrokerCategories).orElse(Collections.emptyList());
    }

    CriteriaSet fetchBrokerCategories(CriteriaSet result) {
        return entityManager
            .createQuery(
                "select criteriaSet from CriteriaSet criteriaSet left join fetch criteriaSet.brokerCategories where criteriaSet is :criteriaSet",
                CriteriaSet.class
            )
            .setParameter("criteriaSet", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<CriteriaSet> fetchBrokerCategories(List<CriteriaSet> criteriaSets) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, criteriaSets.size()).forEach(index -> order.put(criteriaSets.get(index).getId(), index));
        List<CriteriaSet> result = entityManager
            .createQuery(
                "select distinct criteriaSet from CriteriaSet criteriaSet left join fetch criteriaSet.brokerCategories where criteriaSet in :criteriaSets",
                CriteriaSet.class
            )
            .setParameter("criteriaSets", criteriaSets)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
