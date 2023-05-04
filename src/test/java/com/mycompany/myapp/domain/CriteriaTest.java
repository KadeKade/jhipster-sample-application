package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriteriaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Criteria.class);
        Criteria criteria1 = new Criteria();
        criteria1.setId(1L);
        Criteria criteria2 = new Criteria();
        criteria2.setId(criteria1.getId());
        assertThat(criteria1).isEqualTo(criteria2);
        criteria2.setId(2L);
        assertThat(criteria1).isNotEqualTo(criteria2);
        criteria1.setId(null);
        assertThat(criteria1).isNotEqualTo(criteria2);
    }
}
