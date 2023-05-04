package com.mycompany.myapp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.CriteriaParameter;
import com.mycompany.myapp.repository.CriteriaParameterRepository;
import com.mycompany.myapp.repository.search.CriteriaParameterSearchRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.CriteriaParameter}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriteriaParameterResource {

    private final Logger log = LoggerFactory.getLogger(CriteriaParameterResource.class);

    private static final String ENTITY_NAME = "criteriaParameter";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriteriaParameterRepository criteriaParameterRepository;

    private final CriteriaParameterSearchRepository criteriaParameterSearchRepository;

    public CriteriaParameterResource(
        CriteriaParameterRepository criteriaParameterRepository,
        CriteriaParameterSearchRepository criteriaParameterSearchRepository
    ) {
        this.criteriaParameterRepository = criteriaParameterRepository;
        this.criteriaParameterSearchRepository = criteriaParameterSearchRepository;
    }

    /**
     * {@code POST  /criteria-parameters} : Create a new criteriaParameter.
     *
     * @param criteriaParameter the criteriaParameter to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criteriaParameter, or with status {@code 400 (Bad Request)} if the criteriaParameter has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criteria-parameters")
    public ResponseEntity<CriteriaParameter> createCriteriaParameter(@RequestBody CriteriaParameter criteriaParameter)
        throws URISyntaxException {
        log.debug("REST request to save CriteriaParameter : {}", criteriaParameter);
        if (criteriaParameter.getId() != null) {
            throw new BadRequestAlertException("A new criteriaParameter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CriteriaParameter result = criteriaParameterRepository.save(criteriaParameter);
        criteriaParameterSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/criteria-parameters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /criteria-parameters/:id} : Updates an existing criteriaParameter.
     *
     * @param id the id of the criteriaParameter to save.
     * @param criteriaParameter the criteriaParameter to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criteriaParameter,
     * or with status {@code 400 (Bad Request)} if the criteriaParameter is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criteriaParameter couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criteria-parameters/{id}")
    public ResponseEntity<CriteriaParameter> updateCriteriaParameter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriteriaParameter criteriaParameter
    ) throws URISyntaxException {
        log.debug("REST request to update CriteriaParameter : {}, {}", id, criteriaParameter);
        if (criteriaParameter.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criteriaParameter.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!criteriaParameterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CriteriaParameter result = criteriaParameterRepository.save(criteriaParameter);
        criteriaParameterSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, criteriaParameter.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /criteria-parameters/:id} : Partial updates given fields of an existing criteriaParameter, field will ignore if it is null
     *
     * @param id the id of the criteriaParameter to save.
     * @param criteriaParameter the criteriaParameter to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criteriaParameter,
     * or with status {@code 400 (Bad Request)} if the criteriaParameter is not valid,
     * or with status {@code 404 (Not Found)} if the criteriaParameter is not found,
     * or with status {@code 500 (Internal Server Error)} if the criteriaParameter couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criteria-parameters/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CriteriaParameter> partialUpdateCriteriaParameter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriteriaParameter criteriaParameter
    ) throws URISyntaxException {
        log.debug("REST request to partial update CriteriaParameter partially : {}, {}", id, criteriaParameter);
        if (criteriaParameter.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criteriaParameter.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!criteriaParameterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CriteriaParameter> result = criteriaParameterRepository
            .findById(criteriaParameter.getId())
            .map(existingCriteriaParameter -> {
                if (criteriaParameter.getParameterName() != null) {
                    existingCriteriaParameter.setParameterName(criteriaParameter.getParameterName());
                }
                if (criteriaParameter.getParameterValue() != null) {
                    existingCriteriaParameter.setParameterValue(criteriaParameter.getParameterValue());
                }

                return existingCriteriaParameter;
            })
            .map(criteriaParameterRepository::save)
            .map(savedCriteriaParameter -> {
                criteriaParameterSearchRepository.save(savedCriteriaParameter);

                return savedCriteriaParameter;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, criteriaParameter.getId().toString())
        );
    }

    /**
     * {@code GET  /criteria-parameters} : get all the criteriaParameters.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criteriaParameters in body.
     */
    @GetMapping("/criteria-parameters")
    public List<CriteriaParameter> getAllCriteriaParameters() {
        log.debug("REST request to get all CriteriaParameters");
        return criteriaParameterRepository.findAll();
    }

    /**
     * {@code GET  /criteria-parameters/:id} : get the "id" criteriaParameter.
     *
     * @param id the id of the criteriaParameter to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criteriaParameter, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criteria-parameters/{id}")
    public ResponseEntity<CriteriaParameter> getCriteriaParameter(@PathVariable Long id) {
        log.debug("REST request to get CriteriaParameter : {}", id);
        Optional<CriteriaParameter> criteriaParameter = criteriaParameterRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(criteriaParameter);
    }

    /**
     * {@code DELETE  /criteria-parameters/:id} : delete the "id" criteriaParameter.
     *
     * @param id the id of the criteriaParameter to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criteria-parameters/{id}")
    public ResponseEntity<Void> deleteCriteriaParameter(@PathVariable Long id) {
        log.debug("REST request to delete CriteriaParameter : {}", id);
        criteriaParameterRepository.deleteById(id);
        criteriaParameterSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/criteria-parameters?query=:query} : search for the criteriaParameter corresponding
     * to the query.
     *
     * @param query the query of the criteriaParameter search.
     * @return the result of the search.
     */
    @GetMapping("/_search/criteria-parameters")
    public List<CriteriaParameter> searchCriteriaParameters(@RequestParam String query) {
        log.debug("REST request to search CriteriaParameters for query {}", query);
        return StreamSupport.stream(criteriaParameterSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
