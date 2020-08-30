package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.JobHistoryService;
import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.repository.JobHistoryRepository;
import com.mycompany.myapp.repository.search.JobHistorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link JobHistory}.
 */
@Service
@Transactional
public class JobHistoryServiceImpl implements JobHistoryService {

    private final Logger log = LoggerFactory.getLogger(JobHistoryServiceImpl.class);

    private final JobHistoryRepository jobHistoryRepository;

    private final JobHistorySearchRepository jobHistorySearchRepository;

    public JobHistoryServiceImpl(JobHistoryRepository jobHistoryRepository, JobHistorySearchRepository jobHistorySearchRepository) {
        this.jobHistoryRepository = jobHistoryRepository;
        this.jobHistorySearchRepository = jobHistorySearchRepository;
    }

    @Override
    public JobHistory save(JobHistory jobHistory) {
        log.debug("Request to save JobHistory : {}", jobHistory);
        JobHistory result = jobHistoryRepository.save(jobHistory);
        jobHistorySearchRepository.save(result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobHistory> findAll(Pageable pageable) {
        log.debug("Request to get all JobHistories");
        return jobHistoryRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<JobHistory> findOne(Long id) {
        log.debug("Request to get JobHistory : {}", id);
        return jobHistoryRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete JobHistory : {}", id);
        jobHistoryRepository.deleteById(id);
        jobHistorySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of JobHistories for query {}", query);
        return jobHistorySearchRepository.search(queryStringQuery(query), pageable);    }
}
