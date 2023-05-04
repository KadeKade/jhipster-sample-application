package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BrokerCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BrokerCategory.class);
        BrokerCategory brokerCategory1 = new BrokerCategory();
        brokerCategory1.setId(1L);
        BrokerCategory brokerCategory2 = new BrokerCategory();
        brokerCategory2.setId(brokerCategory1.getId());
        assertThat(brokerCategory1).isEqualTo(brokerCategory2);
        brokerCategory2.setId(2L);
        assertThat(brokerCategory1).isNotEqualTo(brokerCategory2);
        brokerCategory1.setId(null);
        assertThat(brokerCategory1).isNotEqualTo(brokerCategory2);
    }
}
