package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriteriaSetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriteriaSet.class);
        CriteriaSet criteriaSet1 = new CriteriaSet();
        criteriaSet1.setId(1L);
        CriteriaSet criteriaSet2 = new CriteriaSet();
        criteriaSet2.setId(criteriaSet1.getId());
        assertThat(criteriaSet1).isEqualTo(criteriaSet2);
        criteriaSet2.setId(2L);
        assertThat(criteriaSet1).isNotEqualTo(criteriaSet2);
        criteriaSet1.setId(null);
        assertThat(criteriaSet1).isNotEqualTo(criteriaSet2);
    }
}
