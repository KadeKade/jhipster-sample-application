package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CriteriaParameter;
import com.mycompany.myapp.repository.CriteriaParameterRepository;
import com.mycompany.myapp.repository.search.CriteriaParameterSearchRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CriteriaParameterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CriteriaParameterResourceIT {

    private static final String DEFAULT_PARAMETER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PARAMETER_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETER_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/criteria-parameters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/criteria-parameters";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriteriaParameterRepository criteriaParameterRepository;

    @Autowired
    private CriteriaParameterSearchRepository criteriaParameterSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCriteriaParameterMockMvc;

    private CriteriaParameter criteriaParameter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriteriaParameter createEntity(EntityManager em) {
        CriteriaParameter criteriaParameter = new CriteriaParameter()
            .parameterName(DEFAULT_PARAMETER_NAME)
            .parameterValue(DEFAULT_PARAMETER_VALUE);
        return criteriaParameter;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriteriaParameter createUpdatedEntity(EntityManager em) {
        CriteriaParameter criteriaParameter = new CriteriaParameter()
            .parameterName(UPDATED_PARAMETER_NAME)
            .parameterValue(UPDATED_PARAMETER_VALUE);
        return criteriaParameter;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        criteriaParameterSearchRepository.deleteAll();
        assertThat(criteriaParameterSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        criteriaParameter = createEntity(em);
    }

    @Test
    @Transactional
    void createCriteriaParameter() throws Exception {
        int databaseSizeBeforeCreate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        // Create the CriteriaParameter
        restCriteriaParameterMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isCreated());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        CriteriaParameter testCriteriaParameter = criteriaParameterList.get(criteriaParameterList.size() - 1);
        assertThat(testCriteriaParameter.getParameterName()).isEqualTo(DEFAULT_PARAMETER_NAME);
        assertThat(testCriteriaParameter.getParameterValue()).isEqualTo(DEFAULT_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void createCriteriaParameterWithExistingId() throws Exception {
        // Create the CriteriaParameter with an existing ID
        criteriaParameter.setId(1L);

        int databaseSizeBeforeCreate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCriteriaParameterMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCriteriaParameters() throws Exception {
        // Initialize the database
        criteriaParameterRepository.saveAndFlush(criteriaParameter);

        // Get all the criteriaParameterList
        restCriteriaParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(criteriaParameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].parameterName").value(hasItem(DEFAULT_PARAMETER_NAME)))
            .andExpect(jsonPath("$.[*].parameterValue").value(hasItem(DEFAULT_PARAMETER_VALUE)));
    }

    @Test
    @Transactional
    void getCriteriaParameter() throws Exception {
        // Initialize the database
        criteriaParameterRepository.saveAndFlush(criteriaParameter);

        // Get the criteriaParameter
        restCriteriaParameterMockMvc
            .perform(get(ENTITY_API_URL_ID, criteriaParameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(criteriaParameter.getId().intValue()))
            .andExpect(jsonPath("$.parameterName").value(DEFAULT_PARAMETER_NAME))
            .andExpect(jsonPath("$.parameterValue").value(DEFAULT_PARAMETER_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingCriteriaParameter() throws Exception {
        // Get the criteriaParameter
        restCriteriaParameterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCriteriaParameter() throws Exception {
        // Initialize the database
        criteriaParameterRepository.saveAndFlush(criteriaParameter);

        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        criteriaParameterSearchRepository.save(criteriaParameter);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());

        // Update the criteriaParameter
        CriteriaParameter updatedCriteriaParameter = criteriaParameterRepository.findById(criteriaParameter.getId()).get();
        // Disconnect from session so that the updates on updatedCriteriaParameter are not directly saved in db
        em.detach(updatedCriteriaParameter);
        updatedCriteriaParameter.parameterName(UPDATED_PARAMETER_NAME).parameterValue(UPDATED_PARAMETER_VALUE);

        restCriteriaParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCriteriaParameter.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCriteriaParameter))
            )
            .andExpect(status().isOk());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        CriteriaParameter testCriteriaParameter = criteriaParameterList.get(criteriaParameterList.size() - 1);
        assertThat(testCriteriaParameter.getParameterName()).isEqualTo(UPDATED_PARAMETER_NAME);
        assertThat(testCriteriaParameter.getParameterValue()).isEqualTo(UPDATED_PARAMETER_VALUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CriteriaParameter> criteriaParameterSearchList = IterableUtils.toList(criteriaParameterSearchRepository.findAll());
                CriteriaParameter testCriteriaParameterSearch = criteriaParameterSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCriteriaParameterSearch.getParameterName()).isEqualTo(UPDATED_PARAMETER_NAME);
                assertThat(testCriteriaParameterSearch.getParameterValue()).isEqualTo(UPDATED_PARAMETER_VALUE);
            });
    }

    @Test
    @Transactional
    void putNonExistingCriteriaParameter() throws Exception {
        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        criteriaParameter.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriteriaParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, criteriaParameter.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCriteriaParameter() throws Exception {
        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        criteriaParameter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCriteriaParameter() throws Exception {
        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        criteriaParameter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaParameterMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCriteriaParameterWithPatch() throws Exception {
        // Initialize the database
        criteriaParameterRepository.saveAndFlush(criteriaParameter);

        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();

        // Update the criteriaParameter using partial update
        CriteriaParameter partialUpdatedCriteriaParameter = new CriteriaParameter();
        partialUpdatedCriteriaParameter.setId(criteriaParameter.getId());

        partialUpdatedCriteriaParameter.parameterName(UPDATED_PARAMETER_NAME);

        restCriteriaParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCriteriaParameter.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCriteriaParameter))
            )
            .andExpect(status().isOk());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        CriteriaParameter testCriteriaParameter = criteriaParameterList.get(criteriaParameterList.size() - 1);
        assertThat(testCriteriaParameter.getParameterName()).isEqualTo(UPDATED_PARAMETER_NAME);
        assertThat(testCriteriaParameter.getParameterValue()).isEqualTo(DEFAULT_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateCriteriaParameterWithPatch() throws Exception {
        // Initialize the database
        criteriaParameterRepository.saveAndFlush(criteriaParameter);

        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();

        // Update the criteriaParameter using partial update
        CriteriaParameter partialUpdatedCriteriaParameter = new CriteriaParameter();
        partialUpdatedCriteriaParameter.setId(criteriaParameter.getId());

        partialUpdatedCriteriaParameter.parameterName(UPDATED_PARAMETER_NAME).parameterValue(UPDATED_PARAMETER_VALUE);

        restCriteriaParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCriteriaParameter.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCriteriaParameter))
            )
            .andExpect(status().isOk());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        CriteriaParameter testCriteriaParameter = criteriaParameterList.get(criteriaParameterList.size() - 1);
        assertThat(testCriteriaParameter.getParameterName()).isEqualTo(UPDATED_PARAMETER_NAME);
        assertThat(testCriteriaParameter.getParameterValue()).isEqualTo(UPDATED_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingCriteriaParameter() throws Exception {
        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        criteriaParameter.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriteriaParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, criteriaParameter.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCriteriaParameter() throws Exception {
        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        criteriaParameter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCriteriaParameter() throws Exception {
        int databaseSizeBeforeUpdate = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        criteriaParameter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriteriaParameterMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(criteriaParameter))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CriteriaParameter in the database
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCriteriaParameter() throws Exception {
        // Initialize the database
        criteriaParameterRepository.saveAndFlush(criteriaParameter);
        criteriaParameterRepository.save(criteriaParameter);
        criteriaParameterSearchRepository.save(criteriaParameter);

        int databaseSizeBeforeDelete = criteriaParameterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the criteriaParameter
        restCriteriaParameterMockMvc
            .perform(delete(ENTITY_API_URL_ID, criteriaParameter.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CriteriaParameter> criteriaParameterList = criteriaParameterRepository.findAll();
        assertThat(criteriaParameterList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(criteriaParameterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCriteriaParameter() throws Exception {
        // Initialize the database
        criteriaParameter = criteriaParameterRepository.saveAndFlush(criteriaParameter);
        criteriaParameterSearchRepository.save(criteriaParameter);

        // Search the criteriaParameter
        restCriteriaParameterMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + criteriaParameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(criteriaParameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].parameterName").value(hasItem(DEFAULT_PARAMETER_NAME)))
            .andExpect(jsonPath("$.[*].parameterValue").value(hasItem(DEFAULT_PARAMETER_VALUE)));
    }
}
