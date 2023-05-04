package com.mycompany.myapp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.AutomatedAction;
import com.mycompany.myapp.repository.AutomatedActionRepository;
import com.mycompany.myapp.repository.search.AutomatedActionSearchRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.AutomatedAction}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AutomatedActionResource {

    private final Logger log = LoggerFactory.getLogger(AutomatedActionResource.class);

    private static final String ENTITY_NAME = "automatedAction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AutomatedActionRepository automatedActionRepository;

    private final AutomatedActionSearchRepository automatedActionSearchRepository;

    public AutomatedActionResource(
        AutomatedActionRepository automatedActionRepository,
        AutomatedActionSearchRepository automatedActionSearchRepository
    ) {
        this.automatedActionRepository = automatedActionRepository;
        this.automatedActionSearchRepository = automatedActionSearchRepository;
    }

    /**
     * {@code POST  /automated-actions} : Create a new automatedAction.
     *
     * @param automatedAction the automatedAction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new automatedAction, or with status {@code 400 (Bad Request)} if the automatedAction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/automated-actions")
    public ResponseEntity<AutomatedAction> createAutomatedAction(@RequestBody AutomatedAction automatedAction) throws URISyntaxException {
        log.debug("REST request to save AutomatedAction : {}", automatedAction);
        if (automatedAction.getId() != null) {
            throw new BadRequestAlertException("A new automatedAction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AutomatedAction result = automatedActionRepository.save(automatedAction);
        automatedActionSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/automated-actions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /automated-actions/:id} : Updates an existing automatedAction.
     *
     * @param id the id of the automatedAction to save.
     * @param automatedAction the automatedAction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated automatedAction,
     * or with status {@code 400 (Bad Request)} if the automatedAction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the automatedAction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/automated-actions/{id}")
    public ResponseEntity<AutomatedAction> updateAutomatedAction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AutomatedAction automatedAction
    ) throws URISyntaxException {
        log.debug("REST request to update AutomatedAction : {}, {}", id, automatedAction);
        if (automatedAction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, automatedAction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!automatedActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AutomatedAction result = automatedActionRepository.save(automatedAction);
        automatedActionSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, automatedAction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /automated-actions/:id} : Partial updates given fields of an existing automatedAction, field will ignore if it is null
     *
     * @param id the id of the automatedAction to save.
     * @param automatedAction the automatedAction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated automatedAction,
     * or with status {@code 400 (Bad Request)} if the automatedAction is not valid,
     * or with status {@code 404 (Not Found)} if the automatedAction is not found,
     * or with status {@code 500 (Internal Server Error)} if the automatedAction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/automated-actions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AutomatedAction> partialUpdateAutomatedAction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AutomatedAction automatedAction
    ) throws URISyntaxException {
        log.debug("REST request to partial update AutomatedAction partially : {}, {}", id, automatedAction);
        if (automatedAction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, automatedAction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!automatedActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AutomatedAction> result = automatedActionRepository
            .findById(automatedAction.getId())
            .map(existingAutomatedAction -> {
                if (automatedAction.getType() != null) {
                    existingAutomatedAction.setType(automatedAction.getType());
                }
                if (automatedAction.getPositiveActionDefinition() != null) {
                    existingAutomatedAction.setPositiveActionDefinition(automatedAction.getPositiveActionDefinition());
                }
                if (automatedAction.getNegativeActionDefinition() != null) {
                    existingAutomatedAction.setNegativeActionDefinition(automatedAction.getNegativeActionDefinition());
                }
                if (automatedAction.getDisplayNameDe() != null) {
                    existingAutomatedAction.setDisplayNameDe(automatedAction.getDisplayNameDe());
                }
                if (automatedAction.getDisplayNameEn() != null) {
                    existingAutomatedAction.setDisplayNameEn(automatedAction.getDisplayNameEn());
                }
                if (automatedAction.getDisplayNameFr() != null) {
                    existingAutomatedAction.setDisplayNameFr(automatedAction.getDisplayNameFr());
                }
                if (automatedAction.getDisplayNameIt() != null) {
                    existingAutomatedAction.setDisplayNameIt(automatedAction.getDisplayNameIt());
                }

                return existingAutomatedAction;
            })
            .map(automatedActionRepository::save)
            .map(savedAutomatedAction -> {
                automatedActionSearchRepository.save(savedAutomatedAction);

                return savedAutomatedAction;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, automatedAction.getId().toString())
        );
    }

    /**
     * {@code GET  /automated-actions} : get all the automatedActions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of automatedActions in body.
     */
    @GetMapping("/automated-actions")
    public List<AutomatedAction> getAllAutomatedActions() {
        log.debug("REST request to get all AutomatedActions");
        return automatedActionRepository.findAll();
    }

    /**
     * {@code GET  /automated-actions/:id} : get the "id" automatedAction.
     *
     * @param id the id of the automatedAction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the automatedAction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/automated-actions/{id}")
    public ResponseEntity<AutomatedAction> getAutomatedAction(@PathVariable Long id) {
        log.debug("REST request to get AutomatedAction : {}", id);
        Optional<AutomatedAction> automatedAction = automatedActionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(automatedAction);
    }

    /**
     * {@code DELETE  /automated-actions/:id} : delete the "id" automatedAction.
     *
     * @param id the id of the automatedAction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/automated-actions/{id}")
    public ResponseEntity<Void> deleteAutomatedAction(@PathVariable Long id) {
        log.debug("REST request to delete AutomatedAction : {}", id);
        automatedActionRepository.deleteById(id);
        automatedActionSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/automated-actions?query=:query} : search for the automatedAction corresponding
     * to the query.
     *
     * @param query the query of the automatedAction search.
     * @return the result of the search.
     */
    @GetMapping("/_search/automated-actions")
    public List<AutomatedAction> searchAutomatedActions(@RequestParam String query) {
        log.debug("REST request to search AutomatedActions for query {}", query);
        return StreamSupport.stream(automatedActionSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
