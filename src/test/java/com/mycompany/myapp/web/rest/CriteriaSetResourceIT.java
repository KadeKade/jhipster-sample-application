package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CriteriaSet;
import com.mycompany.myapp.repository.CriteriaSetRepository;
import com.mycompany.myapp.repository.search.CriteriaSetSearchRepository;
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
 * Integration tests for the {@link CriteriaSetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CriteriaSetResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    private static final Long DEFAULT_INSURER_ID = 1L;
    private static final Long UPDATED_INSURER_ID = 2L;

    private static final Long DEFAULT_LOB_ID = 1L;
    private static final Long UPDATED_LOB_ID = 2L;

    private static final String ENTITY_API_URL = "/api/criteria-sets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/criteria-sets";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriteriaSetRepository criteriaSetRepository;

    @Mock
    private CriteriaSetRepository criteriaSetRepositoryMock;

    @Autowired
    private CriteriaSetSearchRepository criteriaSetSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCriteriaSetMockMvc;

    private CriteriaSet criteriaSet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriteriaSet createEntity(EntityManager em) {
        CriteriaSet criteriaSet = new CriteriaSet()
            .name(DEFAULT_NAME)
            .priority(DEFAULT_PRIORITY)
            .insurerId(DEFAULT_INSURER_ID)
            .lobId(DEFAULT_LOB_ID);
        return criteriaSet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriteriaSet createUpdatedEntity(EntityManager em) {
        CriteriaSet criteriaSet = new CriteriaSet()
            .name(UPDATED_NAME)
            .priority(UPDATED_PRIORITY)
            .insurerId(UPDATED_INSURER_ID)
            .lobId(UPDATED_LOB_ID);
        return criteriaSet;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        criteriaSetSearchRepository.deleteAll();
        assertThat(criteriaSetSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        criteriaSet = createEntity(em);
    }

    @Test
    @Transactional
    void createCriteriaSet() throws Exception {
        int databaseSizeBeforeCreate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        // Create the CriteriaSet
        restCriteriaSetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteriaSet)))
            .andExpect(status().isCreated());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        CriteriaSet testCriteriaSet = criteriaSetList.get(criteriaSetList.size() - 1);
        assertThat(testCriteriaSet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCriteriaSet.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testCriteriaSet.getInsurerId()).isEqualTo(DEFAULT_INSURER_ID);
        assertThat(testCriteriaSet.getLobId()).isEqualTo(DEFAULT_LOB_ID);
    }

    @Test
    @Transactional
    void createCriteriaSetWithExistingId() throws Exception {
        // Create the CriteriaSet with an existing ID
        criteriaSet.setId(1L);

        int databaseSizeBeforeCreate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCriteriaSetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteriaSet)))
            .andExpect(status().isBadRequest());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCriteriaSets() throws Exception {
        // Initialize the database
        criteriaSetRepository.saveAndFlush(criteriaSet);

        // Get all the criteriaSetList
        restCriteriaSetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(criteriaSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].insurerId").value(hasItem(DEFAULT_INSURER_ID.intValue())))
            .andExpect(jsonPath("$.[*].lobId").value(hasItem(DEFAULT_LOB_ID.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriteriaSetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(criteriaSetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCriteriaSetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(criteriaSetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriteriaSetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(criteriaSetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCriteriaSetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(criteriaSetRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCriteriaSet() throws Exception {
        // Initialize the database
        criteriaSetRepository.saveAndFlush(criteriaSet);

        // Get the criteriaSet
        restCriteriaSetMockMvc
            .perform(get(ENTITY_API_URL_ID, criteriaSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(criteriaSet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY))
            .andExpect(jsonPath("$.insurerId").value(DEFAULT_INSURER_ID.intValue()))
            .andExpect(jsonPath("$.lobId").value(DEFAULT_LOB_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingCriteriaSet() throws Exception {
        // Get the criteriaSet
        restCriteriaSetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCriteriaSet() throws Exception {
        // Initialize the database
        criteriaSetRepository.saveAndFlush(criteriaSet);

        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        criteriaSetSearchRepository.save(criteriaSet);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());

        // Update the criteriaSet
        CriteriaSet updatedCriteriaSet = criteriaSetRepository.findById(criteriaSet.getId()).get();
        // Disconnect from session so that the updates on updatedCriteriaSet are not directly saved in db
        em.detach(updatedCriteriaSet);
        updatedCriteriaSet.name(UPDATED_NAME).priority(UPDATED_PRIORITY).insurerId(UPDATED_INSURER_ID).lobId(UPDATED_LOB_ID);

        restCriteriaSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCriteriaSet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCriteriaSet))
            )
            .andExpect(status().isOk());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        CriteriaSet testCriteriaSet = criteriaSetList.get(criteriaSetList.size() - 1);
        assertThat(testCriteriaSet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCriteriaSet.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCriteriaSet.getInsurerId()).isEqualTo(UPDATED_INSURER_ID);
        assertThat(testCriteriaSet.getLobId()).isEqualTo(UPDATED_LOB_ID);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CriteriaSet> criteriaSetSearchList = IterableUtils.toList(criteriaSetSearchRepository.findAll());
                CriteriaSet testCriteriaSetSearch = criteriaSetSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCriteriaSetSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testCriteriaSetSearch.getPriority()).isEqualTo(UPDATED_PRIORITY);
                assertThat(testCriteriaSetSearch.getInsurerId()).isEqualTo(UPDATED_INSURER_ID);
                assertThat(testCriteriaSetSearch.getLobId()).isEqualTo(UPDATED_LOB_ID);
            });
    }

    @Test
    @Transactional
    void putNonExistingCriteriaSet() throws Exception {
        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        criteriaSet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriteriaSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, criteriaSet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(criteriaSet))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCriteriaSet() throws Exception {
        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        criteriaSet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(criteriaSet))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCriteriaSet() throws Exception {
        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        criteriaSet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaSetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteriaSet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCriteriaSetWithPatch() throws Exception {
        // Initialize the database
        criteriaSetRepository.saveAndFlush(criteriaSet);

        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();

        // Update the criteriaSet using partial update
        CriteriaSet partialUpdatedCriteriaSet = new CriteriaSet();
        partialUpdatedCriteriaSet.setId(criteriaSet.getId());

        partialUpdatedCriteriaSet.priority(UPDATED_PRIORITY).lobId(UPDATED_LOB_ID);

        restCriteriaSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCriteriaSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCriteriaSet))
            )
            .andExpect(status().isOk());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        CriteriaSet testCriteriaSet = criteriaSetList.get(criteriaSetList.size() - 1);
        assertThat(testCriteriaSet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCriteriaSet.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCriteriaSet.getInsurerId()).isEqualTo(DEFAULT_INSURER_ID);
        assertThat(testCriteriaSet.getLobId()).isEqualTo(UPDATED_LOB_ID);
    }

    @Test
    @Transactional
    void fullUpdateCriteriaSetWithPatch() throws Exception {
        // Initialize the database
        criteriaSetRepository.saveAndFlush(criteriaSet);

        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();

        // Update the criteriaSet using partial update
        CriteriaSet partialUpdatedCriteriaSet = new CriteriaSet();
        partialUpdatedCriteriaSet.setId(criteriaSet.getId());

        partialUpdatedCriteriaSet.name(UPDATED_NAME).priority(UPDATED_PRIORITY).insurerId(UPDATED_INSURER_ID).lobId(UPDATED_LOB_ID);

        restCriteriaSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCriteriaSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCriteriaSet))
            )
            .andExpect(status().isOk());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        CriteriaSet testCriteriaSet = criteriaSetList.get(criteriaSetList.size() - 1);
        assertThat(testCriteriaSet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCriteriaSet.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCriteriaSet.getInsurerId()).isEqualTo(UPDATED_INSURER_ID);
        assertThat(testCriteriaSet.getLobId()).isEqualTo(UPDATED_LOB_ID);
    }

    @Test
    @Transactional
    void patchNonExistingCriteriaSet() throws Exception {
        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        criteriaSet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriteriaSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, criteriaSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteriaSet))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCriteriaSet() throws Exception {
        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        criteriaSet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteriaSet))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCriteriaSet() throws Exception {
        int databaseSizeBeforeUpdate = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        criteriaSet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaSetMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(criteriaSet))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CriteriaSet in the database
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCriteriaSet() throws Exception {
        // Initialize the database
        criteriaSetRepository.saveAndFlush(criteriaSet);
        criteriaSetRepository.save(criteriaSet);
        criteriaSetSearchRepository.save(criteriaSet);

        int databaseSizeBeforeDelete = criteriaSetRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the criteriaSet
        restCriteriaSetMockMvc
            .perform(delete(ENTITY_API_URL_ID, criteriaSet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CriteriaSet> criteriaSetList = criteriaSetRepository.findAll();
        assertThat(criteriaSetList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaSetSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCriteriaSet() throws Exception {
        // Initialize the database
        criteriaSet = criteriaSetRepository.saveAndFlush(criteriaSet);
        criteriaSetSearchRepository.save(criteriaSet);

        // Search the criteriaSet
        restCriteriaSetMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + criteriaSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(criteriaSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].insurerId").value(hasItem(DEFAULT_INSURER_ID.intValue())))
            .andExpect(jsonPath("$.[*].lobId").value(hasItem(DEFAULT_LOB_ID.intValue())));
    }
}
