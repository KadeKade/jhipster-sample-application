package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriteriaParameterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriteriaParameter.class);
        CriteriaParameter criteriaParameter1 = new CriteriaParameter();
        criteriaParameter1.setId(1L);
        CriteriaParameter criteriaParameter2 = new CriteriaParameter();
        criteriaParameter2.setId(criteriaParameter1.getId());
        assertThat(criteriaParameter1).isEqualTo(criteriaParameter2);
        criteriaParameter2.setId(2L);
        assertThat(criteriaParameter1).isNotEqualTo(criteriaParameter2);
        criteriaParameter1.setId(null);
        assertThat(criteriaParameter1).isNotEqualTo(criteriaParameter2);
    }
}
