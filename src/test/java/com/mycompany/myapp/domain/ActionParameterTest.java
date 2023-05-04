package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionParameterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActionParameter.class);
        ActionParameter actionParameter1 = new ActionParameter();
        actionParameter1.setId(1L);
        ActionParameter actionParameter2 = new ActionParameter();
        actionParameter2.setId(actionParameter1.getId());
        assertThat(actionParameter1).isEqualTo(actionParameter2);
        actionParameter2.setId(2L);
        assertThat(actionParameter1).isNotEqualTo(actionParameter2);
        actionParameter1.setId(null);
        assertThat(actionParameter1).isNotEqualTo(actionParameter2);
    }
}
