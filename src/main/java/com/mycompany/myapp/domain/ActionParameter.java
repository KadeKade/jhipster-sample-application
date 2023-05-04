package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ActionParameter.
 */
@Entity
@Table(name = "action_parameter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "actionparameter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ActionParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "parameter_name")
    private String parameterName;

    @Column(name = "parameter_value")
    private String parameterValue;

    @ManyToMany(mappedBy = "actionParameters")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "criteriaParameters", "actionParameters", "criteriaSet" }, allowSetters = true)
    private Set<Criteria> criterias = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ActionParameter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public ActionParameter parameterName(String parameterName) {
        this.setParameterName(parameterName);
        return this;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return this.parameterValue;
    }

    public ActionParameter parameterValue(String parameterValue) {
        this.setParameterValue(parameterValue);
        return this;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public Set<Criteria> getCriterias() {
        return this.criterias;
    }

    public void setCriterias(Set<Criteria> criteria) {
        if (this.criterias != null) {
            this.criterias.forEach(i -> i.removeActionParameters(this));
        }
        if (criteria != null) {
            criteria.forEach(i -> i.addActionParameters(this));
        }
        this.criterias = criteria;
    }

    public ActionParameter criterias(Set<Criteria> criteria) {
        this.setCriterias(criteria);
        return this;
    }

    public ActionParameter addCriterias(Criteria criteria) {
        this.criterias.add(criteria);
        criteria.getActionParameters().add(this);
        return this;
    }

    public ActionParameter removeCriterias(Criteria criteria) {
        this.criterias.remove(criteria);
        criteria.getActionParameters().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionParameter)) {
            return false;
        }
        return id != null && id.equals(((ActionParameter) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionParameter{" +
            "id=" + getId() +
            ", parameterName='" + getParameterName() + "'" +
            ", parameterValue='" + getParameterValue() + "'" +
            "}";
    }
}
