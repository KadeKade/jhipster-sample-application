import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './criteria-set.reducer';

export const CriteriaSetDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const criteriaSetEntity = useAppSelector(state => state.criteriaSet.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="criteriaSetDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.detail.title">CriteriaSet</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{criteriaSetEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.name">Name</Translate>
            </span>
          </dt>
          <dd>{criteriaSetEntity.name}</dd>
          <dt>
            <span id="priority">
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.priority">Priority</Translate>
            </span>
          </dt>
          <dd>{criteriaSetEntity.priority}</dd>
          <dt>
            <span id="insurerId">
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.insurerId">Insurer Id</Translate>
            </span>
          </dt>
          <dd>{criteriaSetEntity.insurerId}</dd>
          <dt>
            <span id="lobId">
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.lobId">Lob Id</Translate>
            </span>
          </dt>
          <dd>{criteriaSetEntity.lobId}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.automatedAction">Automated Action</Translate>
          </dt>
          <dd>{criteriaSetEntity.automatedAction ? criteriaSetEntity.automatedAction.id : ''}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.brokerCategories">Broker Categories</Translate>
          </dt>
          <dd>
            {criteriaSetEntity.brokerCategories
              ? criteriaSetEntity.brokerCategories.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {criteriaSetEntity.brokerCategories && i === criteriaSetEntity.brokerCategories.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/criteria-set" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/criteria-set/${criteriaSetEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CriteriaSetDetail;
