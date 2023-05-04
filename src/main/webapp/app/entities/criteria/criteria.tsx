import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICriteria } from 'app/shared/model/criteria.model';
import { searchEntities, getEntities } from './criteria.reducer';

export const Criteria = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');

  const criteriaList = useAppSelector(state => state.criteria.entities);
  const loading = useAppSelector(state => state.criteria.loading);

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
      <h2 id="criteria-heading" data-cy="CriteriaHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.criteria.home.title">Criteria</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.criteria.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/criteria/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.criteria.home.createLabel">Create new Criteria</Translate>
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
                  placeholder={translate('jhipsterSampleApplicationApp.criteria.home.search')}
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
        {criteriaList && criteriaList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.priority">Priority</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.criteriaActionType">Criteria Action Type</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.operator">Operator</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.criteriaDefinition">Criteria Definition</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.actionParameters">Action Parameters</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteria.criteriaSet">Criteria Set</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {criteriaList.map((criteria, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/criteria/${criteria.id}`} color="link" size="sm">
                      {criteria.id}
                    </Button>
                  </td>
                  <td>{criteria.priority}</td>
                  <td>
                    <Translate contentKey={`jhipsterSampleApplicationApp.CriteriaType.${criteria.criteriaActionType}`} />
                  </td>
                  <td>
                    <Translate contentKey={`jhipsterSampleApplicationApp.Operator.${criteria.operator}`} />
                  </td>
                  <td>
                    <Translate contentKey={`jhipsterSampleApplicationApp.CriteriaDefinition.${criteria.criteriaDefinition}`} />
                  </td>
                  <td>
                    {criteria.actionParameters
                      ? criteria.actionParameters.map((val, j) => (
                          <span key={j}>
                            <Link to={`/action-parameter/${val.id}`}>{val.id}</Link>
                            {j === criteria.actionParameters.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td>
                    {criteria.criteriaSet ? <Link to={`/criteria-set/${criteria.criteriaSet.id}`}>{criteria.criteriaSet.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/criteria/${criteria.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/criteria/${criteria.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/criteria/${criteria.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="jhipsterSampleApplicationApp.criteria.home.notFound">No Criteria found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Criteria;
