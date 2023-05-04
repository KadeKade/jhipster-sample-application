package com.mycompany.myapp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.BrokerCategory;
import com.mycompany.myapp.repository.BrokerCategoryRepository;
import com.mycompany.myapp.repository.search.BrokerCategorySearchRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.BrokerCategory}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BrokerCategoryResource {

    private final Logger log = LoggerFactory.getLogger(BrokerCategoryResource.class);

    private static final String ENTITY_NAME = "brokerCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BrokerCategoryRepository brokerCategoryRepository;

    private final BrokerCategorySearchRepository brokerCategorySearchRepository;

    public BrokerCategoryResource(
        BrokerCategoryRepository brokerCategoryRepository,
        BrokerCategorySearchRepository brokerCategorySearchRepository
    ) {
        this.brokerCategoryRepository = brokerCategoryRepository;
        this.brokerCategorySearchRepository = brokerCategorySearchRepository;
    }

    /**
     * {@code POST  /broker-categories} : Create a new brokerCategory.
     *
     * @param brokerCategory the brokerCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new brokerCategory, or with status {@code 400 (Bad Request)} if the brokerCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/broker-categories")
    public ResponseEntity<BrokerCategory> createBrokerCategory(@RequestBody BrokerCategory brokerCategory) throws URISyntaxException {
        log.debug("REST request to save BrokerCategory : {}", brokerCategory);
        if (brokerCategory.getId() != null) {
            throw new BadRequestAlertException("A new brokerCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BrokerCategory result = brokerCategoryRepository.save(brokerCategory);
        brokerCategorySearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/broker-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /broker-categories/:id} : Updates an existing brokerCategory.
     *
     * @param id the id of the brokerCategory to save.
     * @param brokerCategory the brokerCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brokerCategory,
     * or with status {@code 400 (Bad Request)} if the brokerCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the brokerCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/broker-categories/{id}")
    public ResponseEntity<BrokerCategory> updateBrokerCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BrokerCategory brokerCategory
    ) throws URISyntaxException {
        log.debug("REST request to update BrokerCategory : {}, {}", id, brokerCategory);
        if (brokerCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brokerCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!brokerCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BrokerCategory result = brokerCategoryRepository.save(brokerCategory);
        brokerCategorySearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, brokerCategory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /broker-categories/:id} : Partial updates given fields of an existing brokerCategory, field will ignore if it is null
     *
     * @param id the id of the brokerCategory to save.
     * @param brokerCategory the brokerCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brokerCategory,
     * or with status {@code 400 (Bad Request)} if the brokerCategory is not valid,
     * or with status {@code 404 (Not Found)} if the brokerCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the brokerCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/broker-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BrokerCategory> partialUpdateBrokerCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BrokerCategory brokerCategory
    ) throws URISyntaxException {
        log.debug("REST request to partial update BrokerCategory partially : {}, {}", id, brokerCategory);
        if (brokerCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brokerCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!brokerCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BrokerCategory> result = brokerCategoryRepository
            .findById(brokerCategory.getId())
            .map(existingBrokerCategory -> {
                if (brokerCategory.getDisplayName() != null) {
                    existingBrokerCategory.setDisplayName(brokerCategory.getDisplayName());
                }

                return existingBrokerCategory;
            })
            .map(brokerCategoryRepository::save)
            .map(savedBrokerCategory -> {
                brokerCategorySearchRepository.save(savedBrokerCategory);

                return savedBrokerCategory;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, brokerCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /broker-categories} : get all the brokerCategories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of brokerCategories in body.
     */
    @GetMapping("/broker-categories")
    public List<BrokerCategory> getAllBrokerCategories() {
        log.debug("REST request to get all BrokerCategories");
        return brokerCategoryRepository.findAll();
    }

    /**
     * {@code GET  /broker-categories/:id} : get the "id" brokerCategory.
     *
     * @param id the id of the brokerCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the brokerCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/broker-categories/{id}")
    public ResponseEntity<BrokerCategory> getBrokerCategory(@PathVariable Long id) {
        log.debug("REST request to get BrokerCategory : {}", id);
        Optional<BrokerCategory> brokerCategory = brokerCategoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(brokerCategory);
    }

    /**
     * {@code DELETE  /broker-categories/:id} : delete the "id" brokerCategory.
     *
     * @param id the id of the brokerCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/broker-categories/{id}")
    public ResponseEntity<Void> deleteBrokerCategory(@PathVariable Long id) {
        log.debug("REST request to delete BrokerCategory : {}", id);
        brokerCategoryRepository.deleteById(id);
        brokerCategorySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/broker-categories?query=:query} : search for the brokerCategory corresponding
     * to the query.
     *
     * @param query the query of the brokerCategory search.
     * @return the result of the search.
     */
    @GetMapping("/_search/broker-categories")
    public List<BrokerCategory> searchBrokerCategories(@RequestParam String query) {
        log.debug("REST request to search BrokerCategories for query {}", query);
        return StreamSupport.stream(brokerCategorySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
