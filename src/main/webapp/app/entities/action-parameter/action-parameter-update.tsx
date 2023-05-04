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
import { IActionParameter } from 'app/shared/model/action-parameter.model';
import { getEntity, updateEntity, createEntity, reset } from './action-parameter.reducer';

export const ActionParameterUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const criteria = useAppSelector(state => state.criteria.entities);
  const actionParameterEntity = useAppSelector(state => state.actionParameter.entity);
  const loading = useAppSelector(state => state.actionParameter.loading);
  const updating = useAppSelector(state => state.actionParameter.updating);
  const updateSuccess = useAppSelector(state => state.actionParameter.updateSuccess);

  const handleClose = () => {
    navigate('/action-parameter');
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
      ...actionParameterEntity,
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
          ...actionParameterEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.actionParameter.home.createOrEditLabel" data-cy="ActionParameterCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.actionParameter.home.createOrEditLabel">
              Create or edit a ActionParameter
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
                  id="action-parameter-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.actionParameter.parameterName')}
                id="action-parameter-parameterName"
                name="parameterName"
                data-cy="parameterName"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.actionParameter.parameterValue')}
                id="action-parameter-parameterValue"
                name="parameterValue"
                data-cy="parameterValue"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/action-parameter" replace color="info">
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

export default ActionParameterUpdate;
