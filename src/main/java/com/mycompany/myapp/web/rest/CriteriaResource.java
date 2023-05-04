package com.mycompany.myapp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Criteria;
import com.mycompany.myapp.repository.CriteriaRepository;
import com.mycompany.myapp.repository.search.CriteriaSearchRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Criteria}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriteriaResource {

    private final Logger log = LoggerFactory.getLogger(CriteriaResource.class);

    private static final String ENTITY_NAME = "criteria";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriteriaRepository criteriaRepository;

    private final CriteriaSearchRepository criteriaSearchRepository;

    public CriteriaResource(CriteriaRepository criteriaRepository, CriteriaSearchRepository criteriaSearchRepository) {
        this.criteriaRepository = criteriaRepository;
        this.criteriaSearchRepository = criteriaSearchRepository;
    }

    /**
     * {@code POST  /criteria} : Create a new criteria.
     *
     * @param criteria the criteria to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criteria, or with status {@code 400 (Bad Request)} if the criteria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criteria")
    public ResponseEntity<Criteria> createCriteria(@RequestBody Criteria criteria) throws URISyntaxException {
        log.debug("REST request to save Criteria : {}", criteria);
        if (criteria.getId() != null) {
            throw new BadRequestAlertException("A new criteria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Criteria result = criteriaRepository.save(criteria);
        criteriaSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/criteria/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /criteria/:id} : Updates an existing criteria.
     *
     * @param id the id of the criteria to save.
     * @param criteria the criteria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criteria,
     * or with status {@code 400 (Bad Request)} if the criteria is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criteria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criteria/{id}")
    public ResponseEntity<Criteria> updateCriteria(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Criteria criteria
    ) throws URISyntaxException {
        log.debug("REST request to update Criteria : {}, {}", id, criteria);
        if (criteria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criteria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!criteriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Criteria result = criteriaRepository.save(criteria);
        criteriaSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, criteria.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /criteria/:id} : Partial updates given fields of an existing criteria, field will ignore if it is null
     *
     * @param id the id of the criteria to save.
     * @param criteria the criteria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criteria,
     * or with status {@code 400 (Bad Request)} if the criteria is not valid,
     * or with status {@code 404 (Not Found)} if the criteria is not found,
     * or with status {@code 500 (Internal Server Error)} if the criteria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criteria/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Criteria> partialUpdateCriteria(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Criteria criteria
    ) throws URISyntaxException {
        log.debug("REST request to partial update Criteria partially : {}, {}", id, criteria);
        if (criteria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criteria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!criteriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Criteria> result = criteriaRepository
            .findById(criteria.getId())
            .map(existingCriteria -> {
                if (criteria.getPriority() != null) {
                    existingCriteria.setPriority(criteria.getPriority());
                }
                if (criteria.getCriteriaActionType() != null) {
                    existingCriteria.setCriteriaActionType(criteria.getCriteriaActionType());
                }
                if (criteria.getOperator() != null) {
                    existingCriteria.setOperator(criteria.getOperator());
                }
                if (criteria.getCriteriaDefinition() != null) {
                    existingCriteria.setCriteriaDefinition(criteria.getCriteriaDefinition());
                }

                return existingCriteria;
            })
            .map(criteriaRepository::save)
            .map(savedCriteria -> {
                criteriaSearchRepository.save(savedCriteria);

                return savedCriteria;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, criteria.getId().toString())
        );
    }

    /**
     * {@code GET  /criteria} : get all the criteria.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criteria in body.
     */
    @GetMapping("/criteria")
    public List<Criteria> getAllCriteria(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Criteria");
        if (eagerload) {
            return criteriaRepository.findAllWithEagerRelationships();
        } else {
            return criteriaRepository.findAll();
        }
    }

    /**
     * {@code GET  /criteria/:id} : get the "id" criteria.
     *
     * @param id the id of the criteria to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criteria, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criteria/{id}")
    public ResponseEntity<Criteria> getCriteria(@PathVariable Long id) {
        log.debug("REST request to get Criteria : {}", id);
        Optional<Criteria> criteria = criteriaRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(criteria);
    }

    /**
     * {@code DELETE  /criteria/:id} : delete the "id" criteria.
     *
     * @param id the id of the criteria to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criteria/{id}")
    public ResponseEntity<Void> deleteCriteria(@PathVariable Long id) {
        log.debug("REST request to delete Criteria : {}", id);
        criteriaRepository.deleteById(id);
        criteriaSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/criteria?query=:query} : search for the criteria corresponding
     * to the query.
     *
     * @param query the query of the criteria search.
     * @return the result of the search.
     */
    @GetMapping("/_search/criteria")
    public List<Criteria> searchCriteria(@RequestParam String query) {
        log.debug("REST request to search Criteria for query {}", query);
        return StreamSupport.stream(criteriaSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
