import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAutomatedAction } from 'app/shared/model/automated-action.model';
import { getEntities as getAutomatedActions } from 'app/entities/automated-action/automated-action.reducer';
import { IBrokerCategory } from 'app/shared/model/broker-category.model';
import { getEntities as getBrokerCategories } from 'app/entities/broker-category/broker-category.reducer';
import { ICriteriaSet } from 'app/shared/model/criteria-set.model';
import { getEntity, updateEntity, createEntity, reset } from './criteria-set.reducer';

export const CriteriaSetUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const automatedActions = useAppSelector(state => state.automatedAction.entities);
  const brokerCategories = useAppSelector(state => state.brokerCategory.entities);
  const criteriaSetEntity = useAppSelector(state => state.criteriaSet.entity);
  const loading = useAppSelector(state => state.criteriaSet.loading);
  const updating = useAppSelector(state => state.criteriaSet.updating);
  const updateSuccess = useAppSelector(state => state.criteriaSet.updateSuccess);

  const handleClose = () => {
    navigate('/criteria-set');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAutomatedActions({}));
    dispatch(getBrokerCategories({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...criteriaSetEntity,
      ...values,
      brokerCategories: mapIdList(values.brokerCategories),
      automatedAction: automatedActions.find(it => it.id.toString() === values.automatedAction.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...criteriaSetEntity,
          automatedAction: criteriaSetEntity?.automatedAction?.id,
          brokerCategories: criteriaSetEntity?.brokerCategories?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.criteriaSet.home.createOrEditLabel" data-cy="CriteriaSetCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.home.createOrEditLabel">Create or edit a CriteriaSet</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="criteria-set-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaSet.name')}
                id="criteria-set-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaSet.priority')}
                id="criteria-set-priority"
                name="priority"
                data-cy="priority"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaSet.insurerId')}
                id="criteria-set-insurerId"
                name="insurerId"
                data-cy="insurerId"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaSet.lobId')}
                id="criteria-set-lobId"
                name="lobId"
                data-cy="lobId"
                type="text"
              />
              <ValidatedField
                id="criteria-set-automatedAction"
                name="automatedAction"
                data-cy="automatedAction"
                label={translate('jhipsterSampleApplicationApp.criteriaSet.automatedAction')}
                type="select"
              >
                <option value="" key="0" />
                {automatedActions
                  ? automatedActions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaSet.brokerCategories')}
                id="criteria-set-brokerCategories"
                data-cy="brokerCategories"
                type="select"
                multiple
                name="brokerCategories"
              >
                <option value="" key="0" />
                {brokerCategories
                  ? brokerCategories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/criteria-set" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CriteriaSetUpdate;
