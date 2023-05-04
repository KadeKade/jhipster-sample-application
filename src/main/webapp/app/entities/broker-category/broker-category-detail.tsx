import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './broker-category.reducer';

export const BrokerCategoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const brokerCategoryEntity = useAppSelector(state => state.brokerCategory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="brokerCategoryDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.brokerCategory.detail.title">BrokerCategory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{brokerCategoryEntity.id}</dd>
          <dt>
            <span id="displayName">
              <Translate contentKey="jhipsterSampleApplicationApp.brokerCategory.displayName">Display Name</Translate>
            </span>
          </dt>
          <dd>{brokerCategoryEntity.displayName}</dd>
        </dl>
        <Button tag={Link} to="/broker-category" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/broker-category/${brokerCategoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BrokerCategoryDetail;
