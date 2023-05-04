package com.mycompany.myapp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.ActionParameter;
import com.mycompany.myapp.repository.ActionParameterRepository;
import com.mycompany.myapp.repository.search.ActionParameterSearchRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ActionParameter}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActionParameterResource {

    private final Logger log = LoggerFactory.getLogger(ActionParameterResource.class);

    private static final String ENTITY_NAME = "actionParameter";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionParameterRepository actionParameterRepository;

    private final ActionParameterSearchRepository actionParameterSearchRepository;

    public ActionParameterResource(
        ActionParameterRepository actionParameterRepository,
        ActionParameterSearchRepository actionParameterSearchRepository
    ) {
        this.actionParameterRepository = actionParameterRepository;
        this.actionParameterSearchRepository = actionParameterSearchRepository;
    }

    /**
     * {@code POST  /action-parameters} : Create a new actionParameter.
     *
     * @param actionParameter the actionParameter to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actionParameter, or with status {@code 400 (Bad Request)} if the actionParameter has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/action-parameters")
    public ResponseEntity<ActionParameter> createActionParameter(@RequestBody ActionParameter actionParameter) throws URISyntaxException {
        log.debug("REST request to save ActionParameter : {}", actionParameter);
        if (actionParameter.getId() != null) {
            throw new BadRequestAlertException("A new actionParameter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActionParameter result = actionParameterRepository.save(actionParameter);
        actionParameterSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/action-parameters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /action-parameters/:id} : Updates an existing actionParameter.
     *
     * @param id the id of the actionParameter to save.
     * @param actionParameter the actionParameter to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionParameter,
     * or with status {@code 400 (Bad Request)} if the actionParameter is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actionParameter couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/action-parameters/{id}")
    public ResponseEntity<ActionParameter> updateActionParameter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionParameter actionParameter
    ) throws URISyntaxException {
        log.debug("REST request to update ActionParameter : {}, {}", id, actionParameter);
        if (actionParameter.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionParameter.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionParameterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionParameter result = actionParameterRepository.save(actionParameter);
        actionParameterSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actionParameter.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /action-parameters/:id} : Partial updates given fields of an existing actionParameter, field will ignore if it is null
     *
     * @param id the id of the actionParameter to save.
     * @param actionParameter the actionParameter to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionParameter,
     * or with status {@code 400 (Bad Request)} if the actionParameter is not valid,
     * or with status {@code 404 (Not Found)} if the actionParameter is not found,
     * or with status {@code 500 (Internal Server Error)} if the actionParameter couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/action-parameters/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ActionParameter> partialUpdateActionParameter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionParameter actionParameter
    ) throws URISyntaxException {
        log.debug("REST request to partial update ActionParameter partially : {}, {}", id, actionParameter);
        if (actionParameter.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionParameter.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionParameterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActionParameter> result = actionParameterRepository
            .findById(actionParameter.getId())
            .map(existingActionParameter -> {
                if (actionParameter.getParameterName() != null) {
                    existingActionParameter.setParameterName(actionParameter.getParameterName());
                }
                if (actionParameter.getParameterValue() != null) {
                    existingActionParameter.setParameterValue(actionParameter.getParameterValue());
                }

                return existingActionParameter;
            })
            .map(actionParameterRepository::save)
            .map(savedActionParameter -> {
                actionParameterSearchRepository.save(savedActionParameter);

                return savedActionParameter;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actionParameter.getId().toString())
        );
    }

    /**
     * {@code GET  /action-parameters} : get all the actionParameters.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actionParameters in body.
     */
    @GetMapping("/action-parameters")
    public List<ActionParameter> getAllActionParameters() {
        log.debug("REST request to get all ActionParameters");
        return actionParameterRepository.findAll();
    }

    /**
     * {@code GET  /action-parameters/:id} : get the "id" actionParameter.
     *
     * @param id the id of the actionParameter to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionParameter, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/action-parameters/{id}")
    public ResponseEntity<ActionParameter> getActionParameter(@PathVariable Long id) {
        log.debug("REST request to get ActionParameter : {}", id);
        Optional<ActionParameter> actionParameter = actionParameterRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actionParameter);
    }

    /**
     * {@code DELETE  /action-parameters/:id} : delete the "id" actionParameter.
     *
     * @param id the id of the actionParameter to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/action-parameters/{id}")
    public ResponseEntity<Void> deleteActionParameter(@PathVariable Long id) {
        log.debug("REST request to delete ActionParameter : {}", id);
        actionParameterRepository.deleteById(id);
        actionParameterSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/action-parameters?query=:query} : search for the actionParameter corresponding
     * to the query.
     *
     * @param query the query of the actionParameter search.
     * @return the result of the search.
     */
    @GetMapping("/_search/action-parameters")
    public List<ActionParameter> searchActionParameters(@RequestParam String query) {
        log.debug("REST request to search ActionParameters for query {}", query);
        return StreamSupport.stream(actionParameterSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
