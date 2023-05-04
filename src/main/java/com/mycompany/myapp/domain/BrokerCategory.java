package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BrokerCategory.
 */
@Entity
@Table(name = "broker_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "brokercategory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BrokerCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @ManyToMany(mappedBy = "brokerCategories")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "criterias", "automatedAction", "brokerCategories" }, allowSetters = true)
    private Set<CriteriaSet> criteriaSets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BrokerCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public BrokerCategory displayName(String displayName) {
        this.setDisplayName(displayName);
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<CriteriaSet> getCriteriaSets() {
        return this.criteriaSets;
    }

    public void setCriteriaSets(Set<CriteriaSet> criteriaSets) {
        if (this.criteriaSets != null) {
            this.criteriaSets.forEach(i -> i.removeBrokerCategories(this));
        }
        if (criteriaSets != null) {
            criteriaSets.forEach(i -> i.addBrokerCategories(this));
        }
        this.criteriaSets = criteriaSets;
    }

    public BrokerCategory criteriaSets(Set<CriteriaSet> criteriaSets) {
        this.setCriteriaSets(criteriaSets);
        return this;
    }

    public BrokerCategory addCriteriaSets(CriteriaSet criteriaSet) {
        this.criteriaSets.add(criteriaSet);
        criteriaSet.getBrokerCategories().add(this);
        return this;
    }

    public BrokerCategory removeCriteriaSets(CriteriaSet criteriaSet) {
        this.criteriaSets.remove(criteriaSet);
        criteriaSet.getBrokerCategories().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BrokerCategory)) {
            return false;
        }
        return id != null && id.equals(((BrokerCategory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BrokerCategory{" +
            "id=" + getId() +
            ", displayName='" + getDisplayName() + "'" +
            "}";
    }
}
