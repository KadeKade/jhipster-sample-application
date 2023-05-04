import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './automated-action.reducer';

export const AutomatedActionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const automatedActionEntity = useAppSelector(state => state.automatedAction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="automatedActionDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.detail.title">AutomatedAction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.type">Type</Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.type}</dd>
          <dt>
            <span id="positiveActionDefinition">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.positiveActionDefinition">
                Positive Action Definition
              </Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.positiveActionDefinition}</dd>
          <dt>
            <span id="negativeActionDefinition">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.negativeActionDefinition">
                Negative Action Definition
              </Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.negativeActionDefinition}</dd>
          <dt>
            <span id="displayNameDe">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameDe">Display Name De</Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.displayNameDe}</dd>
          <dt>
            <span id="displayNameEn">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameEn">Display Name En</Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.displayNameEn}</dd>
          <dt>
            <span id="displayNameFr">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameFr">Display Name Fr</Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.displayNameFr}</dd>
          <dt>
            <span id="displayNameIt">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameIt">Display Name It</Translate>
            </span>
          </dt>
          <dd>{automatedActionEntity.displayNameIt}</dd>
        </dl>
        <Button tag={Link} to="/automated-action" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/automated-action/${automatedActionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AutomatedActionDetail;
