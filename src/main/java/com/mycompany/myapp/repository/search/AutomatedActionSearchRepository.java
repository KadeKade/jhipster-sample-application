package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.AutomatedAction;
import com.mycompany.myapp.repository.AutomatedActionRepository;
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
 * Spring Data Elasticsearch repository for the {@link AutomatedAction} entity.
 */
public interface AutomatedActionSearchRepository
    extends ElasticsearchRepository<AutomatedAction, Long>, AutomatedActionSearchRepositoryInternal {}

interface AutomatedActionSearchRepositoryInternal {
    Stream<AutomatedAction> search(String query);

    Stream<AutomatedAction> search(Query query);

    void index(AutomatedAction entity);
}

class AutomatedActionSearchRepositoryInternalImpl implements AutomatedActionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final AutomatedActionRepository repository;

    AutomatedActionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, AutomatedActionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<AutomatedAction> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<AutomatedAction> search(Query query) {
        return elasticsearchTemplate.search(query, AutomatedAction.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(AutomatedAction entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
