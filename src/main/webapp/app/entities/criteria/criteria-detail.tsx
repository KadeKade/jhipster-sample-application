import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './criteria.reducer';

export const CriteriaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const criteriaEntity = useAppSelector(state => state.criteria.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="criteriaDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.criteria.detail.title">Criteria</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{criteriaEntity.id}</dd>
          <dt>
            <span id="priority">
              <Translate contentKey="jhipsterSampleApplicationApp.criteria.priority">Priority</Translate>
            </span>
          </dt>
          <dd>{criteriaEntity.priority}</dd>
          <dt>
            <span id="criteriaActionType">
              <Translate contentKey="jhipsterSampleApplicationApp.criteria.criteriaActionType">Criteria Action Type</Translate>
            </span>
          </dt>
          <dd>{criteriaEntity.criteriaActionType}</dd>
          <dt>
            <span id="operator">
              <Translate contentKey="jhipsterSampleApplicationApp.criteria.operator">Operator</Translate>
            </span>
          </dt>
          <dd>{criteriaEntity.operator}</dd>
          <dt>
            <span id="criteriaDefinition">
              <Translate contentKey="jhipsterSampleApplicationApp.criteria.criteriaDefinition">Criteria Definition</Translate>
            </span>
          </dt>
          <dd>{criteriaEntity.criteriaDefinition}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.criteria.actionParameters">Action Parameters</Translate>
          </dt>
          <dd>
            {criteriaEntity.actionParameters
              ? criteriaEntity.actionParameters.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {criteriaEntity.actionParameters && i === criteriaEntity.actionParameters.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.criteria.criteriaSet">Criteria Set</Translate>
          </dt>
          <dd>{criteriaEntity.criteriaSet ? criteriaEntity.criteriaSet.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/criteria" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/criteria/${criteriaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CriteriaDetail;
