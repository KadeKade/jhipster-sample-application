import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICriteriaParameter } from 'app/shared/model/criteria-parameter.model';
import { searchEntities, getEntities } from './criteria-parameter.reducer';

export const CriteriaParameter = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');

  const criteriaParameterList = useAppSelector(state => state.criteriaParameter.entities);
  const loading = useAppSelector(state => state.criteriaParameter.loading);

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
      <h2 id="criteria-parameter-heading" data-cy="CriteriaParameterHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.home.title">Criteria Parameters</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/criteria-parameter/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.home.createLabel">
              Create new Criteria Parameter
            </Translate>
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
                  placeholder={translate('jhipsterSampleApplicationApp.criteriaParameter.home.search')}
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
        {criteriaParameterList && criteriaParameterList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.parameterName">Parameter Name</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.parameterValue">Parameter Value</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.criteria">Criteria</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {criteriaParameterList.map((criteriaParameter, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/criteria-parameter/${criteriaParameter.id}`} color="link" size="sm">
                      {criteriaParameter.id}
                    </Button>
                  </td>
                  <td>{criteriaParameter.parameterName}</td>
                  <td>{criteriaParameter.parameterValue}</td>
                  <td>
                    {criteriaParameter.criteria ? (
                      <Link to={`/criteria/${criteriaParameter.criteria.id}`}>{criteriaParameter.criteria.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/criteria-parameter/${criteriaParameter.id}`}
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
                        to={`/criteria-parameter/${criteriaParameter.id}/edit`}
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
                        to={`/criteria-parameter/${criteriaParameter.id}/delete`}
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
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaParameter.home.notFound">No Criteria Parameters found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default CriteriaParameter;
