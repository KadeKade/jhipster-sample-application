import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICriteriaSet } from 'app/shared/model/criteria-set.model';
import { searchEntities, getEntities } from './criteria-set.reducer';

export const CriteriaSet = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');

  const criteriaSetList = useAppSelector(state => state.criteriaSet.entities);
  const loading = useAppSelector(state => state.criteriaSet.loading);

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
      <h2 id="criteria-set-heading" data-cy="CriteriaSetHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.home.title">Criteria Sets</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/criteria-set/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.home.createLabel">Create new Criteria Set</Translate>
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
                  placeholder={translate('jhipsterSampleApplicationApp.criteriaSet.home.search')}
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
        {criteriaSetList && criteriaSetList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.priority">Priority</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.insurerId">Insurer Id</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.lobId">Lob Id</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.automatedAction">Automated Action</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.brokerCategories">Broker Categories</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {criteriaSetList.map((criteriaSet, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/criteria-set/${criteriaSet.id}`} color="link" size="sm">
                      {criteriaSet.id}
                    </Button>
                  </td>
                  <td>{criteriaSet.name}</td>
                  <td>{criteriaSet.priority}</td>
                  <td>{criteriaSet.insurerId}</td>
                  <td>{criteriaSet.lobId}</td>
                  <td>
                    {criteriaSet.automatedAction ? (
                      <Link to={`/automated-action/${criteriaSet.automatedAction.id}`}>{criteriaSet.automatedAction.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {criteriaSet.brokerCategories
                      ? criteriaSet.brokerCategories.map((val, j) => (
                          <span key={j}>
                            <Link to={`/broker-category/${val.id}`}>{val.id}</Link>
                            {j === criteriaSet.brokerCategories.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/criteria-set/${criteriaSet.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/criteria-set/${criteriaSet.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/criteria-set/${criteriaSet.id}/delete`}
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
              <Translate contentKey="jhipsterSampleApplicationApp.criteriaSet.home.notFound">No Criteria Sets found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default CriteriaSet;
