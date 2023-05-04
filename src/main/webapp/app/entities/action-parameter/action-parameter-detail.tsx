import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './action-parameter.reducer';

export const ActionParameterDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const actionParameterEntity = useAppSelector(state => state.actionParameter.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionParameterDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.actionParameter.detail.title">ActionParameter</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{actionParameterEntity.id}</dd>
          <dt>
            <span id="parameterName">
              <Translate contentKey="jhipsterSampleApplicationApp.actionParameter.parameterName">Parameter Name</Translate>
            </span>
          </dt>
          <dd>{actionParameterEntity.parameterName}</dd>
          <dt>
            <span id="parameterValue">
              <Translate contentKey="jhipsterSampleApplicationApp.actionParameter.parameterValue">Parameter Value</Translate>
            </span>
          </dt>
          <dd>{actionParameterEntity.parameterValue}</dd>
        </dl>
        <Button tag={Link} to="/action-parameter" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action-parameter/${actionParameterEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ActionParameterDetail;
