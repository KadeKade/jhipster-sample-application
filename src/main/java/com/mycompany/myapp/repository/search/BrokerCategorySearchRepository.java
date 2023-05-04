package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.BrokerCategory;
import com.mycompany.myapp.repository.BrokerCategoryRepository;
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
 * Spring Data Elasticsearch repository for the {@link BrokerCategory} entity.
 */
public interface BrokerCategorySearchRepository
    extends ElasticsearchRepository<BrokerCategory, Long>, BrokerCategorySearchRepositoryInternal {}

interface BrokerCategorySearchRepositoryInternal {
    Stream<BrokerCategory> search(String query);

    Stream<BrokerCategory> search(Query query);

    void index(BrokerCategory entity);
}

class BrokerCategorySearchRepositoryInternalImpl implements BrokerCategorySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final BrokerCategoryRepository repository;

    BrokerCategorySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, BrokerCategoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<BrokerCategory> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<BrokerCategory> search(Query query) {
        return elasticsearchTemplate.search(query, BrokerCategory.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(BrokerCategory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
