import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICriteria } from 'app/shared/model/criteria.model';
import { getEntities as getCriteria } from 'app/entities/criteria/criteria.reducer';
import { ICriteriaParameter } from 'app/shared/model/criteria-parameter.model';
import { getEntity, updateEntity, createEntity, reset } from './criteria-parameter.reducer';

export const CriteriaParameterUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const criteria = useAppSelector(state => state.criteria.entities);
  const criteriaParameterEntity = useAppSelector(state => state.criteriaParameter.entity);
  const loading = useAppSelector(state => state.criteriaParameter.loading);
  const updating = useAppSelector(state => state.criteriaParameter.updating);
  const updateSuccess = useAppSelector(state => state.criteriaParameter.updateSuccess);

  const handleClose = () => {
    navigate('/criteria-parameter');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCriteria({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...criteriaParameterEntity,
      ...values,
      criteria: criteria.find(it => it.id.toString() === values.criteria.toString()),
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
          ...criteriaParameterEntity,
          criteria: criteriaParameterEntity?.criteria?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.criteriaParameter.home.createOrEditLabel" data-cy="CriteriaParameterCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.home.createOrEditLabel">
              Create or edit a CriteriaParameter
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
                  id="criteria-parameter-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaParameter.parameterName')}
                id="criteria-parameter-parameterName"
                name="parameterName"
                data-cy="parameterName"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.criteriaParameter.parameterValue')}
                id="criteria-parameter-parameterValue"
                name="parameterValue"
                data-cy="parameterValue"
                type="text"
              />
              <ValidatedField
                id="criteria-parameter-criteria"
                name="criteria"
                data-cy="criteria"
                label={translate('jhipsterSampleApplicationApp.criteriaParameter.criteria')}
                type="select"
              >
                <option value="" key="0" />
                {criteria
                  ? criteria.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/criteria-parameter" replace color="info">
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

export default CriteriaParameterUpdate;
