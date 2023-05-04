package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.ActionParameter;
import com.mycompany.myapp.repository.ActionParameterRepository;
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
 * Spring Data Elasticsearch repository for the {@link ActionParameter} entity.
 */
public interface ActionParameterSearchRepository
    extends ElasticsearchRepository<ActionParameter, Long>, ActionParameterSearchRepositoryInternal {}

interface ActionParameterSearchRepositoryInternal {
    Stream<ActionParameter> search(String query);

    Stream<ActionParameter> search(Query query);

    void index(ActionParameter entity);
}

class ActionParameterSearchRepositoryInternalImpl implements ActionParameterSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ActionParameterRepository repository;

    ActionParameterSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ActionParameterRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ActionParameter> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<ActionParameter> search(Query query) {
        return elasticsearchTemplate.search(query, ActionParameter.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ActionParameter entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
