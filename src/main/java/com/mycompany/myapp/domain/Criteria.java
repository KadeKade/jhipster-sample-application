package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.CriteriaDefinition;
import com.mycompany.myapp.domain.enumeration.CriteriaType;
import com.mycompany.myapp.domain.enumeration.Operator;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Criteria.
 */
@Entity
@Table(name = "criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "criteria")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Criteria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "priority")
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_action_type")
    private CriteriaType criteriaActionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator")
    private Operator operator;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_definition")
    private CriteriaDefinition criteriaDefinition;

    @OneToMany(mappedBy = "criteria")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "criteria" }, allowSetters = true)
    private Set<CriteriaParameter> criteriaParameters = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_criteria__action_parameters",
        joinColumns = @JoinColumn(name = "criteria_id"),
        inverseJoinColumns = @JoinColumn(name = "action_parameters_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "criterias" }, allowSetters = true)
    private Set<ActionParameter> actionParameters = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "criterias", "automatedAction", "brokerCategories" }, allowSetters = true)
    private CriteriaSet criteriaSet;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Criteria id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public Criteria priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public CriteriaType getCriteriaActionType() {
        return this.criteriaActionType;
    }

    public Criteria criteriaActionType(CriteriaType criteriaActionType) {
        this.setCriteriaActionType(criteriaActionType);
        return this;
    }

    public void setCriteriaActionType(CriteriaType criteriaActionType) {
        this.criteriaActionType = criteriaActionType;
    }

    public Operator getOperator() {
        return this.operator;
    }

    public Criteria operator(Operator operator) {
        this.setOperator(operator);
        return this;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public CriteriaDefinition getCriteriaDefinition() {
        return this.criteriaDefinition;
    }

    public Criteria criteriaDefinition(CriteriaDefinition criteriaDefinition) {
        this.setCriteriaDefinition(criteriaDefinition);
        return this;
    }

    public void setCriteriaDefinition(CriteriaDefinition criteriaDefinition) {
        this.criteriaDefinition = criteriaDefinition;
    }

    public Set<CriteriaParameter> getCriteriaParameters() {
        return this.criteriaParameters;
    }

    public void setCriteriaParameters(Set<CriteriaParameter> criteriaParameters) {
        if (this.criteriaParameters != null) {
            this.criteriaParameters.forEach(i -> i.setCriteria(null));
        }
        if (criteriaParameters != null) {
            criteriaParameters.forEach(i -> i.setCriteria(this));
        }
        this.criteriaParameters = criteriaParameters;
    }

    public Criteria criteriaParameters(Set<CriteriaParameter> criteriaParameters) {
        this.setCriteriaParameters(criteriaParameters);
        return this;
    }

    public Criteria addCriteriaParameters(CriteriaParameter criteriaParameter) {
        this.criteriaParameters.add(criteriaParameter);
        criteriaParameter.setCriteria(this);
        return this;
    }

    public Criteria removeCriteriaParameters(CriteriaParameter criteriaParameter) {
        this.criteriaParameters.remove(criteriaParameter);
        criteriaParameter.setCriteria(null);
        return this;
    }

    public Set<ActionParameter> getActionParameters() {
        return this.actionParameters;
    }

    public void setActionParameters(Set<ActionParameter> actionParameters) {
        this.actionParameters = actionParameters;
    }

    public Criteria actionParameters(Set<ActionParameter> actionParameters) {
        this.setActionParameters(actionParameters);
        return this;
    }

    public Criteria addActionParameters(ActionParameter actionParameter) {
        this.actionParameters.add(actionParameter);
        actionParameter.getCriterias().add(this);
        return this;
    }

    public Criteria removeActionParameters(ActionParameter actionParameter) {
        this.actionParameters.remove(actionParameter);
        actionParameter.getCriterias().remove(this);
        return this;
    }

    public CriteriaSet getCriteriaSet() {
        return this.criteriaSet;
    }

    public void setCriteriaSet(CriteriaSet criteriaSet) {
        this.criteriaSet = criteriaSet;
    }

    public Criteria criteriaSet(CriteriaSet criteriaSet) {
        this.setCriteriaSet(criteriaSet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Criteria)) {
            return false;
        }
        return id != null && id.equals(((Criteria) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Criteria{" +
            "id=" + getId() +
            ", priority=" + getPriority() +
            ", criteriaActionType='" + getCriteriaActionType() + "'" +
            ", operator='" + getOperator() + "'" +
            ", criteriaDefinition='" + getCriteriaDefinition() + "'" +
            "}";
    }
}
