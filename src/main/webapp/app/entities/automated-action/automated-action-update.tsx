import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAutomatedAction } from 'app/shared/model/automated-action.model';
import { AutomatedActionType } from 'app/shared/model/enumerations/automated-action-type.model';
import { getEntity, updateEntity, createEntity, reset } from './automated-action.reducer';

export const AutomatedActionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const automatedActionEntity = useAppSelector(state => state.automatedAction.entity);
  const loading = useAppSelector(state => state.automatedAction.loading);
  const updating = useAppSelector(state => state.automatedAction.updating);
  const updateSuccess = useAppSelector(state => state.automatedAction.updateSuccess);
  const automatedActionTypeValues = Object.keys(AutomatedActionType);

  const handleClose = () => {
    navigate('/automated-action');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...automatedActionEntity,
      ...values,
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
          type: 'AUTO_DECLINE',
          ...automatedActionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.automatedAction.home.createOrEditLabel" data-cy="AutomatedActionCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.home.createOrEditLabel">
              Create or edit a AutomatedAction
            </Translate>
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
                  id="automated-action-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.type')}
                id="automated-action-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {automatedActionTypeValues.map(automatedActionType => (
                  <option value={automatedActionType} key={automatedActionType}>
                    {translate('jhipsterSampleApplicationApp.AutomatedActionType.' + automatedActionType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.positiveActionDefinition')}
                id="automated-action-positiveActionDefinition"
                name="positiveActionDefinition"
                data-cy="positiveActionDefinition"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.negativeActionDefinition')}
                id="automated-action-negativeActionDefinition"
                name="negativeActionDefinition"
                data-cy="negativeActionDefinition"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.displayNameDe')}
                id="automated-action-displayNameDe"
                name="displayNameDe"
                data-cy="displayNameDe"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.displayNameEn')}
                id="automated-action-displayNameEn"
                name="displayNameEn"
                data-cy="displayNameEn"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.displayNameFr')}
                id="automated-action-displayNameFr"
                name="displayNameFr"
                data-cy="displayNameFr"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.automatedAction.displayNameIt')}
                id="automated-action-displayNameIt"
                name="displayNameIt"
                data-cy="displayNameIt"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/automated-action" replace color="info">
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

export default AutomatedActionUpdate;
