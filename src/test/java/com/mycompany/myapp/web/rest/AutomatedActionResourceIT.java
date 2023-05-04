package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.AutomatedAction;
import com.mycompany.myapp.domain.enumeration.AutomatedActionType;
import com.mycompany.myapp.repository.AutomatedActionRepository;
import com.mycompany.myapp.repository.search.AutomatedActionSearchRepository;
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
 * Integration tests for the {@link AutomatedActionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AutomatedActionResourceIT {

    private static final AutomatedActionType DEFAULT_TYPE = AutomatedActionType.AUTO_DECLINE;
    private static final AutomatedActionType UPDATED_TYPE = AutomatedActionType.AUTO_OFFER;

    private static final String DEFAULT_POSITIVE_ACTION_DEFINITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITIVE_ACTION_DEFINITION = "BBBBBBBBBB";

    private static final String DEFAULT_NEGATIVE_ACTION_DEFINITION = "AAAAAAAAAA";
    private static final String UPDATED_NEGATIVE_ACTION_DEFINITION = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME_DE = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME_DE = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME_EN = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME_FR = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME_FR = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME_IT = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME_IT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/automated-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/automated-actions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AutomatedActionRepository automatedActionRepository;

    @Autowired
    private AutomatedActionSearchRepository automatedActionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAutomatedActionMockMvc;

    private AutomatedAction automatedAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AutomatedAction createEntity(EntityManager em) {
        AutomatedAction automatedAction = new AutomatedAction()
            .type(DEFAULT_TYPE)
            .positiveActionDefinition(DEFAULT_POSITIVE_ACTION_DEFINITION)
            .negativeActionDefinition(DEFAULT_NEGATIVE_ACTION_DEFINITION)
            .displayNameDe(DEFAULT_DISPLAY_NAME_DE)
            .displayNameEn(DEFAULT_DISPLAY_NAME_EN)
            .displayNameFr(DEFAULT_DISPLAY_NAME_FR)
            .displayNameIt(DEFAULT_DISPLAY_NAME_IT);
        return automatedAction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AutomatedAction createUpdatedEntity(EntityManager em) {
        AutomatedAction automatedAction = new AutomatedAction()
            .type(UPDATED_TYPE)
            .positiveActionDefinition(UPDATED_POSITIVE_ACTION_DEFINITION)
            .negativeActionDefinition(UPDATED_NEGATIVE_ACTION_DEFINITION)
            .displayNameDe(UPDATED_DISPLAY_NAME_DE)
            .displayNameEn(UPDATED_DISPLAY_NAME_EN)
            .displayNameFr(UPDATED_DISPLAY_NAME_FR)
            .displayNameIt(UPDATED_DISPLAY_NAME_IT);
        return automatedAction;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        automatedActionSearchRepository.deleteAll();
        assertThat(automatedActionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        automatedAction = createEntity(em);
    }

    @Test
    @Transactional
    void createAutomatedAction() throws Exception {
        int databaseSizeBeforeCreate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        // Create the AutomatedAction
        restAutomatedActionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isCreated());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        AutomatedAction testAutomatedAction = automatedActionList.get(automatedActionList.size() - 1);
        assertThat(testAutomatedAction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAutomatedAction.getPositiveActionDefinition()).isEqualTo(DEFAULT_POSITIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getNegativeActionDefinition()).isEqualTo(DEFAULT_NEGATIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getDisplayNameDe()).isEqualTo(DEFAULT_DISPLAY_NAME_DE);
        assertThat(testAutomatedAction.getDisplayNameEn()).isEqualTo(DEFAULT_DISPLAY_NAME_EN);
        assertThat(testAutomatedAction.getDisplayNameFr()).isEqualTo(DEFAULT_DISPLAY_NAME_FR);
        assertThat(testAutomatedAction.getDisplayNameIt()).isEqualTo(DEFAULT_DISPLAY_NAME_IT);
    }

    @Test
    @Transactional
    void createAutomatedActionWithExistingId() throws Exception {
        // Create the AutomatedAction with an existing ID
        automatedAction.setId(1L);

        int databaseSizeBeforeCreate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAutomatedActionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isBadRequest());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAutomatedActions() throws Exception {
        // Initialize the database
        automatedActionRepository.saveAndFlush(automatedAction);

        // Get all the automatedActionList
        restAutomatedActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(automatedAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].positiveActionDefinition").value(hasItem(DEFAULT_POSITIVE_ACTION_DEFINITION)))
            .andExpect(jsonPath("$.[*].negativeActionDefinition").value(hasItem(DEFAULT_NEGATIVE_ACTION_DEFINITION)))
            .andExpect(jsonPath("$.[*].displayNameDe").value(hasItem(DEFAULT_DISPLAY_NAME_DE)))
            .andExpect(jsonPath("$.[*].displayNameEn").value(hasItem(DEFAULT_DISPLAY_NAME_EN)))
            .andExpect(jsonPath("$.[*].displayNameFr").value(hasItem(DEFAULT_DISPLAY_NAME_FR)))
            .andExpect(jsonPath("$.[*].displayNameIt").value(hasItem(DEFAULT_DISPLAY_NAME_IT)));
    }

    @Test
    @Transactional
    void getAutomatedAction() throws Exception {
        // Initialize the database
        automatedActionRepository.saveAndFlush(automatedAction);

        // Get the automatedAction
        restAutomatedActionMockMvc
            .perform(get(ENTITY_API_URL_ID, automatedAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(automatedAction.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.positiveActionDefinition").value(DEFAULT_POSITIVE_ACTION_DEFINITION))
            .andExpect(jsonPath("$.negativeActionDefinition").value(DEFAULT_NEGATIVE_ACTION_DEFINITION))
            .andExpect(jsonPath("$.displayNameDe").value(DEFAULT_DISPLAY_NAME_DE))
            .andExpect(jsonPath("$.displayNameEn").value(DEFAULT_DISPLAY_NAME_EN))
            .andExpect(jsonPath("$.displayNameFr").value(DEFAULT_DISPLAY_NAME_FR))
            .andExpect(jsonPath("$.displayNameIt").value(DEFAULT_DISPLAY_NAME_IT));
    }

    @Test
    @Transactional
    void getNonExistingAutomatedAction() throws Exception {
        // Get the automatedAction
        restAutomatedActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAutomatedAction() throws Exception {
        // Initialize the database
        automatedActionRepository.saveAndFlush(automatedAction);

        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        automatedActionSearchRepository.save(automatedAction);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());

        // Update the automatedAction
        AutomatedAction updatedAutomatedAction = automatedActionRepository.findById(automatedAction.getId()).get();
        // Disconnect from session so that the updates on updatedAutomatedAction are not directly saved in db
        em.detach(updatedAutomatedAction);
        updatedAutomatedAction
            .type(UPDATED_TYPE)
            .positiveActionDefinition(UPDATED_POSITIVE_ACTION_DEFINITION)
            .negativeActionDefinition(UPDATED_NEGATIVE_ACTION_DEFINITION)
            .displayNameDe(UPDATED_DISPLAY_NAME_DE)
            .displayNameEn(UPDATED_DISPLAY_NAME_EN)
            .displayNameFr(UPDATED_DISPLAY_NAME_FR)
            .displayNameIt(UPDATED_DISPLAY_NAME_IT);

        restAutomatedActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAutomatedAction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAutomatedAction))
            )
            .andExpect(status().isOk());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        AutomatedAction testAutomatedAction = automatedActionList.get(automatedActionList.size() - 1);
        assertThat(testAutomatedAction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testAutomatedAction.getPositiveActionDefinition()).isEqualTo(UPDATED_POSITIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getNegativeActionDefinition()).isEqualTo(UPDATED_NEGATIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getDisplayNameDe()).isEqualTo(UPDATED_DISPLAY_NAME_DE);
        assertThat(testAutomatedAction.getDisplayNameEn()).isEqualTo(UPDATED_DISPLAY_NAME_EN);
        assertThat(testAutomatedAction.getDisplayNameFr()).isEqualTo(UPDATED_DISPLAY_NAME_FR);
        assertThat(testAutomatedAction.getDisplayNameIt()).isEqualTo(UPDATED_DISPLAY_NAME_IT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AutomatedAction> automatedActionSearchList = IterableUtils.toList(automatedActionSearchRepository.findAll());
                AutomatedAction testAutomatedActionSearch = automatedActionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAutomatedActionSearch.getType()).isEqualTo(UPDATED_TYPE);
                assertThat(testAutomatedActionSearch.getPositiveActionDefinition()).isEqualTo(UPDATED_POSITIVE_ACTION_DEFINITION);
                assertThat(testAutomatedActionSearch.getNegativeActionDefinition()).isEqualTo(UPDATED_NEGATIVE_ACTION_DEFINITION);
                assertThat(testAutomatedActionSearch.getDisplayNameDe()).isEqualTo(UPDATED_DISPLAY_NAME_DE);
                assertThat(testAutomatedActionSearch.getDisplayNameEn()).isEqualTo(UPDATED_DISPLAY_NAME_EN);
                assertThat(testAutomatedActionSearch.getDisplayNameFr()).isEqualTo(UPDATED_DISPLAY_NAME_FR);
                assertThat(testAutomatedActionSearch.getDisplayNameIt()).isEqualTo(UPDATED_DISPLAY_NAME_IT);
            });
    }

    @Test
    @Transactional
    void putNonExistingAutomatedAction() throws Exception {
        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        automatedAction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAutomatedActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, automatedAction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isBadRequest());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAutomatedAction() throws Exception {
        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        automatedAction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAutomatedActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isBadRequest());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAutomatedAction() throws Exception {
        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        automatedAction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAutomatedActionMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAutomatedActionWithPatch() throws Exception {
        // Initialize the database
        automatedActionRepository.saveAndFlush(automatedAction);

        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();

        // Update the automatedAction using partial update
        AutomatedAction partialUpdatedAutomatedAction = new AutomatedAction();
        partialUpdatedAutomatedAction.setId(automatedAction.getId());

        partialUpdatedAutomatedAction.negativeActionDefinition(UPDATED_NEGATIVE_ACTION_DEFINITION);

        restAutomatedActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAutomatedAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAutomatedAction))
            )
            .andExpect(status().isOk());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        AutomatedAction testAutomatedAction = automatedActionList.get(automatedActionList.size() - 1);
        assertThat(testAutomatedAction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAutomatedAction.getPositiveActionDefinition()).isEqualTo(DEFAULT_POSITIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getNegativeActionDefinition()).isEqualTo(UPDATED_NEGATIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getDisplayNameDe()).isEqualTo(DEFAULT_DISPLAY_NAME_DE);
        assertThat(testAutomatedAction.getDisplayNameEn()).isEqualTo(DEFAULT_DISPLAY_NAME_EN);
        assertThat(testAutomatedAction.getDisplayNameFr()).isEqualTo(DEFAULT_DISPLAY_NAME_FR);
        assertThat(testAutomatedAction.getDisplayNameIt()).isEqualTo(DEFAULT_DISPLAY_NAME_IT);
    }

    @Test
    @Transactional
    void fullUpdateAutomatedActionWithPatch() throws Exception {
        // Initialize the database
        automatedActionRepository.saveAndFlush(automatedAction);

        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();

        // Update the automatedAction using partial update
        AutomatedAction partialUpdatedAutomatedAction = new AutomatedAction();
        partialUpdatedAutomatedAction.setId(automatedAction.getId());

        partialUpdatedAutomatedAction
            .type(UPDATED_TYPE)
            .positiveActionDefinition(UPDATED_POSITIVE_ACTION_DEFINITION)
            .negativeActionDefinition(UPDATED_NEGATIVE_ACTION_DEFINITION)
            .displayNameDe(UPDATED_DISPLAY_NAME_DE)
            .displayNameEn(UPDATED_DISPLAY_NAME_EN)
            .displayNameFr(UPDATED_DISPLAY_NAME_FR)
            .displayNameIt(UPDATED_DISPLAY_NAME_IT);

        restAutomatedActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAutomatedAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAutomatedAction))
            )
            .andExpect(status().isOk());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        AutomatedAction testAutomatedAction = automatedActionList.get(automatedActionList.size() - 1);
        assertThat(testAutomatedAction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testAutomatedAction.getPositiveActionDefinition()).isEqualTo(UPDATED_POSITIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getNegativeActionDefinition()).isEqualTo(UPDATED_NEGATIVE_ACTION_DEFINITION);
        assertThat(testAutomatedAction.getDisplayNameDe()).isEqualTo(UPDATED_DISPLAY_NAME_DE);
        assertThat(testAutomatedAction.getDisplayNameEn()).isEqualTo(UPDATED_DISPLAY_NAME_EN);
        assertThat(testAutomatedAction.getDisplayNameFr()).isEqualTo(UPDATED_DISPLAY_NAME_FR);
        assertThat(testAutomatedAction.getDisplayNameIt()).isEqualTo(UPDATED_DISPLAY_NAME_IT);
    }

    @Test
    @Transactional
    void patchNonExistingAutomatedAction() throws Exception {
        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        automatedAction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAutomatedActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, automatedAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isBadRequest());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAutomatedAction() throws Exception {
        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        automatedAction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAutomatedActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isBadRequest());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAutomatedAction() throws Exception {
        int databaseSizeBeforeUpdate = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        automatedAction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAutomatedActionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(automatedAction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AutomatedAction in the database
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAutomatedAction() throws Exception {
        // Initialize the database
        automatedActionRepository.saveAndFlush(automatedAction);
        automatedActionRepository.save(automatedAction);
        automatedActionSearchRepository.save(automatedAction);

        int databaseSizeBeforeDelete = automatedActionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the automatedAction
        restAutomatedActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, automatedAction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AutomatedAction> automatedActionList = automatedActionRepository.findAll();
        assertThat(automatedActionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(automatedActionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAutomatedAction() throws Exception {
        // Initialize the database
        automatedAction = automatedActionRepository.saveAndFlush(automatedAction);
        automatedActionSearchRepository.save(automatedAction);

        // Search the automatedAction
        restAutomatedActionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + automatedAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(automatedAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].positiveActionDefinition").value(hasItem(DEFAULT_POSITIVE_ACTION_DEFINITION)))
            .andExpect(jsonPath("$.[*].negativeActionDefinition").value(hasItem(DEFAULT_NEGATIVE_ACTION_DEFINITION)))
            .andExpect(jsonPath("$.[*].displayNameDe").value(hasItem(DEFAULT_DISPLAY_NAME_DE)))
            .andExpect(jsonPath("$.[*].displayNameEn").value(hasItem(DEFAULT_DISPLAY_NAME_EN)))
            .andExpect(jsonPath("$.[*].displayNameFr").value(hasItem(DEFAULT_DISPLAY_NAME_FR)))
            .andExpect(jsonPath("$.[*].displayNameIt").value(hasItem(DEFAULT_DISPLAY_NAME_IT)));
    }
}
