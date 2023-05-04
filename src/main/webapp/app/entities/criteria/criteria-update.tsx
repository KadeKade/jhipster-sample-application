import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IActionParameter } from 'app/shared/model/action-parameter.model';
import { getEntities as getActionParameters } from 'app/entities/action-parameter/action-parameter.reducer';
import { ICriteriaSet } from 'app/shared/model/criteria-set.model';
import { getEntities as getCriteriaSets } from 'app/entities/criteria-set/criteria-set.reducer';
import { ICriteria } from 'app/shared/model/criteria.model';
import { CriteriaType } from 'app/shared/model/enumerations/criteria-type.model';
import { Operator } from 'app/shared/model/enumerations/operator.model';
import { CriteriaDefinition } from 'app/shared/model/enumerations/criteria-definition.model';
import { getEntity, updateEntity, createEntity, reset } from './criteria.reducer';

export const CriteriaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const actionParameters = useAppSelector(state => state.actionParameter.entities);
  const criteriaSets = useAppSelector(state => state.criteriaSet.entities);
  const criteriaEntity = useAppSelector(state => state.criteria.entity);
  const loading = useAppSelector(state => state.criteria.loading);
  const updating = useAppSelector(state => state.criteria.updating);
  const updateSuccess = useAppSelector(state => state.criteria.updateSuccess);
  const criteriaTypeValues = Object.keys(CriteriaType);
  const operatorValues = Object.keys(Operator);
  const criteriaDefinitionValues = Object.keys(CriteriaDefinition);

  const handleClose = () => {
    navigate('/criteria');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getActionParameters({}));
    dispatch(getCriteriaSets({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...criteriaEntity,
      ...values,
      actionParameters: mapIdList(values.actionParameters),
      criteriaSet: criteriaSets.find(it => it.id.toString() === values.criteriaSet.toString()),
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
          criteriaActionType: 'POSITIVE',
          operator: 'NOT_EQUAL_TO',
          criteriaDefinition: 'CREDITWORTHINESS',
          ...criteriaEntity,
          actionParameters: criteriaEntity?.actionParameters?.map(e => e.id.toString()),
          criteriaSet: criteriaEntity?.criteriaSet?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.criteria.home.createOrEditLabel" data-cy="CriteriaCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.criteria.home.createOrEditLabel">Create or edit a Criteria</Translate>
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
                  id="criteria-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteria.priority')}
                id="criteria-priority"
                name="priority"
                data-cy="priority"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteria.criteriaActionType')}
                id="criteria-criteriaActionType"
                name="criteriaActionType"
                data-cy="criteriaActionType"
                type="select"
              >
                {criteriaTypeValues.map(criteriaType => (
                  <option value={criteriaType} key={criteriaType}>
                    {translate('jhipsterSampleApplicationApp.CriteriaType.' + criteriaType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteria.operator')}
                id="criteria-operator"
                name="operator"
                data-cy="operator"
                type="select"
              >
                {operatorValues.map(operator => (
                  <option value={operator} key={operator}>
                    {translate('jhipsterSampleApplicationApp.Operator.' + operator)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteria.criteriaDefinition')}
                id="criteria-criteriaDefinition"
                name="criteriaDefinition"
                data-cy="criteriaDefinition"
                type="select"
              >
                {criteriaDefinitionValues.map(criteriaDefinition => (
                  <option value={criteriaDefinition} key={criteriaDefinition}>
                    {translate('jhipsterSampleApplicationApp.CriteriaDefinition.' + criteriaDefinition)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteria.actionParameters')}
                id="criteria-actionParameters"
                data-cy="actionParameters"
                type="select"
                multiple
                name="actionParameters"
              >
                <option value="" key="0" />
                {actionParameters
                  ? actionParameters.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="criteria-criteriaSet"
                name="criteriaSet"
                data-cy="criteriaSet"
                label={translate('jhipsterSampleApplicationApp.criteria.criteriaSet')}
                type="select"
              >
                <option value="" key="0" />
                {criteriaSets
                  ? criteriaSets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/criteria" replace color="info">
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

export default CriteriaUpdate;
