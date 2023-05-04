package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AutomatedActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AutomatedAction.class);
        AutomatedAction automatedAction1 = new AutomatedAction();
        automatedAction1.setId(1L);
        AutomatedAction automatedAction2 = new AutomatedAction();
        automatedAction2.setId(automatedAction1.getId());
        assertThat(automatedAction1).isEqualTo(automatedAction2);
        automatedAction2.setId(2L);
        assertThat(automatedAction1).isNotEqualTo(automatedAction2);
        automatedAction1.setId(null);
        assertThat(automatedAction1).isNotEqualTo(automatedAction2);
    }
}
