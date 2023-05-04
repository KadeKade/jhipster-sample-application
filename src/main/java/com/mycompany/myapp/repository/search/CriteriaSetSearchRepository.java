package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.CriteriaSet;
import com.mycompany.myapp.repository.CriteriaSetRepository;
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
 * Spring Data Elasticsearch repository for the {@link CriteriaSet} entity.
 */
public interface CriteriaSetSearchRepository extends ElasticsearchRepository<CriteriaSet, Long>, CriteriaSetSearchRepositoryInternal {}

interface CriteriaSetSearchRepositoryInternal {
    Stream<CriteriaSet> search(String query);

    Stream<CriteriaSet> search(Query query);

    void index(CriteriaSet entity);
}

class CriteriaSetSearchRepositoryInternalImpl implements CriteriaSetSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CriteriaSetRepository repository;

    CriteriaSetSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CriteriaSetRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<CriteriaSet> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<CriteriaSet> search(Query query) {
        return elasticsearchTemplate.search(query, CriteriaSet.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(CriteriaSet entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
