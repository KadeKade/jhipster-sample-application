package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Criteria;
import com.mycompany.myapp.domain.enumeration.CriteriaDefinition;
import com.mycompany.myapp.domain.enumeration.CriteriaType;
import com.mycompany.myapp.domain.enumeration.Operator;
import com.mycompany.myapp.repository.CriteriaRepository;
import com.mycompany.myapp.repository.search.CriteriaSearchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CriteriaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CriteriaResourceIT {

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    private static final CriteriaType DEFAULT_CRITERIA_ACTION_TYPE = CriteriaType.POSITIVE;
    private static final CriteriaType UPDATED_CRITERIA_ACTION_TYPE = CriteriaType.NEGATIVE;

    private static final Operator DEFAULT_OPERATOR = Operator.NOT_EQUAL_TO;
    private static final Operator UPDATED_OPERATOR = Operator.EQUAL_TO;

    private static final CriteriaDefinition DEFAULT_CRITERIA_DEFINITION = CriteriaDefinition.CREDITWORTHINESS;
    private static final CriteriaDefinition UPDATED_CRITERIA_DEFINITION = CriteriaDefinition.SALARY_SUMM;

    private static final String ENTITY_API_URL = "/api/criteria";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/criteria";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Mock
    private CriteriaRepository criteriaRepositoryMock;

    @Autowired
    private CriteriaSearchRepository criteriaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCriteriaMockMvc;

    private Criteria criteria;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Criteria createEntity(EntityManager em) {
        Criteria criteria = new Criteria()
            .priority(DEFAULT_PRIORITY)
            .criteriaActionType(DEFAULT_CRITERIA_ACTION_TYPE)
            .operator(DEFAULT_OPERATOR)
            .criteriaDefinition(DEFAULT_CRITERIA_DEFINITION);
        return criteria;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Criteria createUpdatedEntity(EntityManager em) {
        Criteria criteria = new Criteria()
            .priority(UPDATED_PRIORITY)
            .criteriaActionType(UPDATED_CRITERIA_ACTION_TYPE)
            .operator(UPDATED_OPERATOR)
            .criteriaDefinition(UPDATED_CRITERIA_DEFINITION);
        return criteria;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        criteriaSearchRepository.deleteAll();
        assertThat(criteriaSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        criteria = createEntity(em);
    }

    @Test
    @Transactional
    void createCriteria() throws Exception {
        int databaseSizeBeforeCreate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        // Create the Criteria
        restCriteriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteria)))
            .andExpect(status().isCreated());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Criteria testCriteria = criteriaList.get(criteriaList.size() - 1);
        assertThat(testCriteria.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testCriteria.getCriteriaActionType()).isEqualTo(DEFAULT_CRITERIA_ACTION_TYPE);
        assertThat(testCriteria.getOperator()).isEqualTo(DEFAULT_OPERATOR);
        assertThat(testCriteria.getCriteriaDefinition()).isEqualTo(DEFAULT_CRITERIA_DEFINITION);
    }

    @Test
    @Transactional
    void createCriteriaWithExistingId() throws Exception {
        // Create the Criteria with an existing ID
        criteria.setId(1L);

        int databaseSizeBeforeCreate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCriteriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteria)))
            .andExpect(status().isBadRequest());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCriteria() throws Exception {
        // Initialize the database
        criteriaRepository.saveAndFlush(criteria);

        // Get all the criteriaList
        restCriteriaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(criteria.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].criteriaActionType").value(hasItem(DEFAULT_CRITERIA_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operator").value(hasItem(DEFAULT_OPERATOR.toString())))
            .andExpect(jsonPath("$.[*].criteriaDefinition").value(hasItem(DEFAULT_CRITERIA_DEFINITION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriteriaWithEagerRelationshipsIsEnabled() throws Exception {
        when(criteriaRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCriteriaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(criteriaRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriteriaWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(criteriaRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCriteriaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(criteriaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCriteria() throws Exception {
        // Initialize the database
        criteriaRepository.saveAndFlush(criteria);

        // Get the criteria
        restCriteriaMockMvc
            .perform(get(ENTITY_API_URL_ID, criteria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(criteria.getId().intValue()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY))
            .andExpect(jsonPath("$.criteriaActionType").value(DEFAULT_CRITERIA_ACTION_TYPE.toString()))
            .andExpect(jsonPath("$.operator").value(DEFAULT_OPERATOR.toString()))
            .andExpect(jsonPath("$.criteriaDefinition").value(DEFAULT_CRITERIA_DEFINITION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCriteria() throws Exception {
        // Get the criteria
        restCriteriaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCriteria() throws Exception {
        // Initialize the database
        criteriaRepository.saveAndFlush(criteria);

        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        criteriaSearchRepository.save(criteria);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());

        // Update the criteria
        Criteria updatedCriteria = criteriaRepository.findById(criteria.getId()).get();
        // Disconnect from session so that the updates on updatedCriteria are not directly saved in db
        em.detach(updatedCriteria);
        updatedCriteria
            .priority(UPDATED_PRIORITY)
            .criteriaActionType(UPDATED_CRITERIA_ACTION_TYPE)
            .operator(UPDATED_OPERATOR)
            .criteriaDefinition(UPDATED_CRITERIA_DEFINITION);

        restCriteriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCriteria.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCriteria))
            )
            .andExpect(status().isOk());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        Criteria testCriteria = criteriaList.get(criteriaList.size() - 1);
        assertThat(testCriteria.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCriteria.getCriteriaActionType()).isEqualTo(UPDATED_CRITERIA_ACTION_TYPE);
        assertThat(testCriteria.getOperator()).isEqualTo(UPDATED_OPERATOR);
        assertThat(testCriteria.getCriteriaDefinition()).isEqualTo(UPDATED_CRITERIA_DEFINITION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Criteria> criteriaSearchList = IterableUtils.toList(criteriaSearchRepository.findAll());
                Criteria testCriteriaSearch = criteriaSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCriteriaSearch.getPriority()).isEqualTo(UPDATED_PRIORITY);
                assertThat(testCriteriaSearch.getCriteriaActionType()).isEqualTo(UPDATED_CRITERIA_ACTION_TYPE);
                assertThat(testCriteriaSearch.getOperator()).isEqualTo(UPDATED_OPERATOR);
                assertThat(testCriteriaSearch.getCriteriaDefinition()).isEqualTo(UPDATED_CRITERIA_DEFINITION);
            });
    }

    @Test
    @Transactional
    void putNonExistingCriteria() throws Exception {
        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        criteria.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriteriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, criteria.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(criteria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCriteria() throws Exception {
        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        criteria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(criteria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCriteria() throws Exception {
        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        criteria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteria)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCriteriaWithPatch() throws Exception {
        // Initialize the database
        criteriaRepository.saveAndFlush(criteria);

        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();

        // Update the criteria using partial update
        Criteria partialUpdatedCriteria = new Criteria();
        partialUpdatedCriteria.setId(criteria.getId());

        partialUpdatedCriteria.priority(UPDATED_PRIORITY).operator(UPDATED_OPERATOR);

        restCriteriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCriteria.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCriteria))
            )
            .andExpect(status().isOk());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        Criteria testCriteria = criteriaList.get(criteriaList.size() - 1);
        assertThat(testCriteria.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCriteria.getCriteriaActionType()).isEqualTo(DEFAULT_CRITERIA_ACTION_TYPE);
        assertThat(testCriteria.getOperator()).isEqualTo(UPDATED_OPERATOR);
        assertThat(testCriteria.getCriteriaDefinition()).isEqualTo(DEFAULT_CRITERIA_DEFINITION);
    }

    @Test
    @Transactional
    void fullUpdateCriteriaWithPatch() throws Exception {
        // Initialize the database
        criteriaRepository.saveAndFlush(criteria);

        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();

        // Update the criteria using partial update
        Criteria partialUpdatedCriteria = new Criteria();
        partialUpdatedCriteria.setId(criteria.getId());

        partialUpdatedCriteria
            .priority(UPDATED_PRIORITY)
            .criteriaActionType(UPDATED_CRITERIA_ACTION_TYPE)
            .operator(UPDATED_OPERATOR)
            .criteriaDefinition(UPDATED_CRITERIA_DEFINITION);

        restCriteriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCriteria.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCriteria))
            )
            .andExpect(status().isOk());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        Criteria testCriteria = criteriaList.get(criteriaList.size() - 1);
        assertThat(testCriteria.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCriteria.getCriteriaActionType()).isEqualTo(UPDATED_CRITERIA_ACTION_TYPE);
        assertThat(testCriteria.getOperator()).isEqualTo(UPDATED_OPERATOR);
        assertThat(testCriteria.getCriteriaDefinition()).isEqualTo(UPDATED_CRITERIA_DEFINITION);
    }

    @Test
    @Transactional
    void patchNonExistingCriteria() throws Exception {
        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        criteria.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriteriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, criteria.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCriteria() throws Exception {
        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        criteria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCriteria() throws Exception {
        int databaseSizeBeforeUpdate = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        criteria.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(criteria)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Criteria in the database
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCriteria() throws Exception {
        // Initialize the database
        criteriaRepository.saveAndFlush(criteria);
        criteriaRepository.save(criteria);
        criteriaSearchRepository.save(criteria);

        int databaseSizeBeforeDelete = criteriaRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the criteria
        restCriteriaMockMvc
            .perform(delete(ENTITY_API_URL_ID, criteria.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Criteria> criteriaList = criteriaRepository.findAll();
        assertThat(criteriaList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCriteria() throws Exception {
        // Initialize the database
        criteria = criteriaRepository.saveAndFlush(criteria);
        criteriaSearchRepository.save(criteria);

        // Search the criteria
        restCriteriaMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + criteria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(criteria.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].criteriaActionType").value(hasItem(DEFAULT_CRITERIA_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operator").value(hasItem(DEFAULT_OPERATOR.toString())))
            .andExpect(jsonPath("$.[*].criteriaDefinition").value(hasItem(DEFAULT_CRITERIA_DEFINITION.toString())));
    }
}
