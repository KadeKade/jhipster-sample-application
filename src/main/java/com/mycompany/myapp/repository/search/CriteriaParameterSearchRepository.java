package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.CriteriaParameter;
import com.mycompany.myapp.repository.CriteriaParameterRepository;
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
 * Spring Data Elasticsearch repository for the {@link CriteriaParameter} entity.
 */
public interface CriteriaParameterSearchRepository
    extends ElasticsearchRepository<CriteriaParameter, Long>, CriteriaParameterSearchRepositoryInternal {}

interface CriteriaParameterSearchRepositoryInternal {
    Stream<CriteriaParameter> search(String query);

    Stream<CriteriaParameter> search(Query query);

    void index(CriteriaParameter entity);
}

class CriteriaParameterSearchRepositoryInternalImpl implements CriteriaParameterSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CriteriaParameterRepository repository;

    CriteriaParameterSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CriteriaParameterRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<CriteriaParameter> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<CriteriaParameter> search(Query query) {
        return elasticsearchTemplate.search(query, CriteriaParameter.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(CriteriaParameter entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
