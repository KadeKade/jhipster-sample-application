import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './criteria-parameter.reducer';

export const CriteriaParameterDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const criteriaParameterEntity = useAppSelector(state => state.criteriaParameter.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="criteriaParameterDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.detail.title">CriteriaParameter</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{criteriaParameterEntity.id}</dd>
          <dt>
            <span id="parameterName">
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.parameterName">Parameter Name</Translate>
            </span>
          </dt>
          <dd>{criteriaParameterEntity.parameterName}</dd>
          <dt>
            <span id="parameterValue">
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.parameterValue">Parameter Value</Translate>
            </span>
          </dt>
          <dd>{criteriaParameterEntity.parameterValue}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.criteria">Criteria</Translate>
          </dt>
          <dd>{criteriaParameterEntity.criteria ? criteriaParameterEntity.criteria.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/criteria-parameter" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/criteria-parameter/${criteriaParameterEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CriteriaParameterDetail;
