package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BrokerCategory;
import com.mycompany.myapp.repository.BrokerCategoryRepository;
import com.mycompany.myapp.repository.search.BrokerCategorySearchRepository;
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
 * Integration tests for the {@link BrokerCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BrokerCategoryResourceIT {

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/broker-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/broker-categories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BrokerCategoryRepository brokerCategoryRepository;

    @Autowired
    private BrokerCategorySearchRepository brokerCategorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBrokerCategoryMockMvc;

    private BrokerCategory brokerCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BrokerCategory createEntity(EntityManager em) {
        BrokerCategory brokerCategory = new BrokerCategory().displayName(DEFAULT_DISPLAY_NAME);
        return brokerCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BrokerCategory createUpdatedEntity(EntityManager em) {
        BrokerCategory brokerCategory = new BrokerCategory().displayName(UPDATED_DISPLAY_NAME);
        return brokerCategory;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        brokerCategorySearchRepository.deleteAll();
        assertThat(brokerCategorySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        brokerCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createBrokerCategory() throws Exception {
        int databaseSizeBeforeCreate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        // Create the BrokerCategory
        restBrokerCategoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isCreated());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BrokerCategory testBrokerCategory = brokerCategoryList.get(brokerCategoryList.size() - 1);
        assertThat(testBrokerCategory.getDisplayName()).isEqualTo(DEFAULT_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void createBrokerCategoryWithExistingId() throws Exception {
        // Create the BrokerCategory with an existing ID
        brokerCategory.setId(1L);

        int databaseSizeBeforeCreate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrokerCategoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBrokerCategories() throws Exception {
        // Initialize the database
        brokerCategoryRepository.saveAndFlush(brokerCategory);

        // Get all the brokerCategoryList
        restBrokerCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brokerCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)));
    }

    @Test
    @Transactional
    void getBrokerCategory() throws Exception {
        // Initialize the database
        brokerCategoryRepository.saveAndFlush(brokerCategory);

        // Get the brokerCategory
        restBrokerCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, brokerCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(brokerCategory.getId().intValue()))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME));
    }

    @Test
    @Transactional
    void getNonExistingBrokerCategory() throws Exception {
        // Get the brokerCategory
        restBrokerCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBrokerCategory() throws Exception {
        // Initialize the database
        brokerCategoryRepository.saveAndFlush(brokerCategory);

        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        brokerCategorySearchRepository.save(brokerCategory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());

        // Update the brokerCategory
        BrokerCategory updatedBrokerCategory = brokerCategoryRepository.findById(brokerCategory.getId()).get();
        // Disconnect from session so that the updates on updatedBrokerCategory are not directly saved in db
        em.detach(updatedBrokerCategory);
        updatedBrokerCategory.displayName(UPDATED_DISPLAY_NAME);

        restBrokerCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBrokerCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBrokerCategory))
            )
            .andExpect(status().isOk());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrokerCategory testBrokerCategory = brokerCategoryList.get(brokerCategoryList.size() - 1);
        assertThat(testBrokerCategory.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BrokerCategory> brokerCategorySearchList = IterableUtils.toList(brokerCategorySearchRepository.findAll());
                BrokerCategory testBrokerCategorySearch = brokerCategorySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBrokerCategorySearch.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingBrokerCategory() throws Exception {
        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        brokerCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrokerCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brokerCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBrokerCategory() throws Exception {
        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        brokerCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrokerCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBrokerCategory() throws Exception {
        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        brokerCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrokerCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brokerCategory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBrokerCategoryWithPatch() throws Exception {
        // Initialize the database
        brokerCategoryRepository.saveAndFlush(brokerCategory);

        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();

        // Update the brokerCategory using partial update
        BrokerCategory partialUpdatedBrokerCategory = new BrokerCategory();
        partialUpdatedBrokerCategory.setId(brokerCategory.getId());

        partialUpdatedBrokerCategory.displayName(UPDATED_DISPLAY_NAME);

        restBrokerCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrokerCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBrokerCategory))
            )
            .andExpect(status().isOk());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrokerCategory testBrokerCategory = brokerCategoryList.get(brokerCategoryList.size() - 1);
        assertThat(testBrokerCategory.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void fullUpdateBrokerCategoryWithPatch() throws Exception {
        // Initialize the database
        brokerCategoryRepository.saveAndFlush(brokerCategory);

        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();

        // Update the brokerCategory using partial update
        BrokerCategory partialUpdatedBrokerCategory = new BrokerCategory();
        partialUpdatedBrokerCategory.setId(brokerCategory.getId());

        partialUpdatedBrokerCategory.displayName(UPDATED_DISPLAY_NAME);

        restBrokerCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrokerCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBrokerCategory))
            )
            .andExpect(status().isOk());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        BrokerCategory testBrokerCategory = brokerCategoryList.get(brokerCategoryList.size() - 1);
        assertThat(testBrokerCategory.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingBrokerCategory() throws Exception {
        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        brokerCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrokerCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, brokerCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBrokerCategory() throws Exception {
        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        brokerCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrokerCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBrokerCategory() throws Exception {
        int databaseSizeBeforeUpdate = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        brokerCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrokerCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(brokerCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BrokerCategory in the database
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBrokerCategory() throws Exception {
        // Initialize the database
        brokerCategoryRepository.saveAndFlush(brokerCategory);
        brokerCategoryRepository.save(brokerCategory);
        brokerCategorySearchRepository.save(brokerCategory);

        int databaseSizeBeforeDelete = brokerCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the brokerCategory
        restBrokerCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, brokerCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BrokerCategory> brokerCategoryList = brokerCategoryRepository.findAll();
        assertThat(brokerCategoryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(brokerCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBrokerCategory() throws Exception {
        // Initialize the database
        brokerCategory = brokerCategoryRepository.saveAndFlush(brokerCategory);
        brokerCategorySearchRepository.save(brokerCategory);

        // Search the brokerCategory
        restBrokerCategoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + brokerCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brokerCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)));
    }
}
