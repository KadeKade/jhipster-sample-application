package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.Criteria;
import com.mycompany.myapp.repository.CriteriaRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Criteria} entity.
 */
public interface CriteriaSearchRepository extends ElasticsearchRepository<Criteria, Long>, CriteriaSearchRepositoryInternal {}

interface CriteriaSearchRepositoryInternal {
    Stream<Criteria> search(String query);

    Stream<Criteria> search(Query query);

    void index(Criteria entity);
}

class CriteriaSearchRepositoryInternalImpl implements CriteriaSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CriteriaRepository repository;

    CriteriaSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CriteriaRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Criteria> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Criteria> search(Query query) {
        return elasticsearchTemplate.search(query, Criteria.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Criteria entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
