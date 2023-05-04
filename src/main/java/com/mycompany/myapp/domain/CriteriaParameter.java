package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CriteriaParameter.
 */
@Entity
@Table(name = "criteria_parameter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "criteriaparameter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CriteriaParameter implements Serializable {

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

    @ManyToOne
    @JsonIgnoreProperties(value = { "criteriaParameters", "actionParameters", "criteriaSet" }, allowSetters = true)
    private Criteria criteria;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CriteriaParameter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public CriteriaParameter parameterName(String parameterName) {
        this.setParameterName(parameterName);
        return this;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return this.parameterValue;
    }

    public CriteriaParameter parameterValue(String parameterValue) {
        this.setParameterValue(parameterValue);
        return this;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public Criteria getCriteria() {
        return this.criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public CriteriaParameter criteria(Criteria criteria) {
        this.setCriteria(criteria);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CriteriaParameter)) {
            return false;
        }
        return id != null && id.equals(((CriteriaParameter) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriteriaParameter{" +
            "id=" + getId() +
            ", parameterName='" + getParameterName() + "'" +
            ", parameterValue='" + getParameterValue() + "'" +
            "}";
    }
}
