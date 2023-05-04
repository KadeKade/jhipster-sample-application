package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.AutomatedActionType;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AutomatedAction.
 */
@Entity
@Table(name = "automated_action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "automatedaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AutomatedAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AutomatedActionType type;

    @Column(name = "positive_action_definition")
    private String positiveActionDefinition;

    @Column(name = "negative_action_definition")
    private String negativeActionDefinition;

    @Column(name = "display_name_de")
    private String displayNameDe;

    @Column(name = "display_name_en")
    private String displayNameEn;

    @Column(name = "display_name_fr")
    private String displayNameFr;

    @Column(name = "display_name_it")
    private String displayNameIt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AutomatedAction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AutomatedActionType getType() {
        return this.type;
    }

    public AutomatedAction type(AutomatedActionType type) {
        this.setType(type);
        return this;
    }

    public void setType(AutomatedActionType type) {
        this.type = type;
    }

    public String getPositiveActionDefinition() {
        return this.positiveActionDefinition;
    }

    public AutomatedAction positiveActionDefinition(String positiveActionDefinition) {
        this.setPositiveActionDefinition(positiveActionDefinition);
        return this;
    }

    public void setPositiveActionDefinition(String positiveActionDefinition) {
        this.positiveActionDefinition = positiveActionDefinition;
    }

    public String getNegativeActionDefinition() {
        return this.negativeActionDefinition;
    }

    public AutomatedAction negativeActionDefinition(String negativeActionDefinition) {
        this.setNegativeActionDefinition(negativeActionDefinition);
        return this;
    }

    public void setNegativeActionDefinition(String negativeActionDefinition) {
        this.negativeActionDefinition = negativeActionDefinition;
    }

    public String getDisplayNameDe() {
        return this.displayNameDe;
    }

    public AutomatedAction displayNameDe(String displayNameDe) {
        this.setDisplayNameDe(displayNameDe);
        return this;
    }

    public void setDisplayNameDe(String displayNameDe) {
        this.displayNameDe = displayNameDe;
    }

    public String getDisplayNameEn() {
        return this.displayNameEn;
    }

    public AutomatedAction displayNameEn(String displayNameEn) {
        this.setDisplayNameEn(displayNameEn);
        return this;
    }

    public void setDisplayNameEn(String displayNameEn) {
        this.displayNameEn = displayNameEn;
    }

    public String getDisplayNameFr() {
        return this.displayNameFr;
    }

    public AutomatedAction displayNameFr(String displayNameFr) {
        this.setDisplayNameFr(displayNameFr);
        return this;
    }

    public void setDisplayNameFr(String displayNameFr) {
        this.displayNameFr = displayNameFr;
    }

    public String getDisplayNameIt() {
        return this.displayNameIt;
    }

    public AutomatedAction displayNameIt(String displayNameIt) {
        this.setDisplayNameIt(displayNameIt);
        return this;
    }

    public void setDisplayNameIt(String displayNameIt) {
        this.displayNameIt = displayNameIt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AutomatedAction)) {
            return false;
        }
        return id != null && id.equals(((AutomatedAction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AutomatedAction{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", positiveActionDefinition='" + getPositiveActionDefinition() + "'" +
            ", negativeActionDefinition='" + getNegativeActionDefinition() + "'" +
            ", displayNameDe='" + getDisplayNameDe() + "'" +
            ", displayNameEn='" + getDisplayNameEn() + "'" +
            ", displayNameFr='" + getDisplayNameFr() + "'" +
            ", displayNameIt='" + getDisplayNameIt() + "'" +
            "}";
    }
}
