import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAutomatedAction } from 'app/shared/model/automated-action.model';
import { searchEntities, getEntities } from './automated-action.reducer';

export const AutomatedAction = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');

  const automatedActionList = useAppSelector(state => state.automatedAction.entities);
  const loading = useAppSelector(state => state.automatedAction.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(searchEntities({ query: search }));
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="automated-action-heading" data-cy="AutomatedActionHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.home.title">Automated Actions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/automated-action/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.home.createLabel">Create new Automated Action</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('jhipsterSampleApplicationApp.automatedAction.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {automatedActionList && automatedActionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.type">Type</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.positiveActionDefinition">
                    Positive Action Definition
                  </Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.negativeActionDefinition">
                    Negative Action Definition
                  </Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameDe">Display Name De</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameEn">Display Name En</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameFr">Display Name Fr</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.displayNameIt">Display Name It</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {automatedActionList.map((automatedAction, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/automated-action/${automatedAction.id}`} color="link" size="sm">
                      {automatedAction.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`jhipsterSampleApplicationApp.AutomatedActionType.${automatedAction.type}`} />
                  </td>
                  <td>{automatedAction.positiveActionDefinition}</td>
                  <td>{automatedAction.negativeActionDefinition}</td>
                  <td>{automatedAction.displayNameDe}</td>
                  <td>{automatedAction.displayNameEn}</td>
                  <td>{automatedAction.displayNameFr}</td>
                  <td>{automatedAction.displayNameIt}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/automated-action/${automatedAction.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/automated-action/${automatedAction.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/automated-action/${automatedAction.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="jhipsterSampleApplicationApp.automatedAction.home.notFound">No Automated Actions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default AutomatedAction;
