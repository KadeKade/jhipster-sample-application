import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CriteriaParameter from './criteria-parameter';
import CriteriaParameterDetail from './criteria-parameter-detail';
import CriteriaParameterUpdate from './criteria-parameter-update';
import CriteriaParameterDeleteDialog from './criteria-parameter-delete-dialog';

const CriteriaParameterRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CriteriaParameter />} />
    <Route path="new" element={<CriteriaParameterUpdate />} />
    <Route path=":id">
      <Route index element={<CriteriaParameterDetail />} />
      <Route path="edit" element={<CriteriaParameterUpdate />} />
      <Route path="delete" element={<CriteriaParameterDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CriteriaParameterRoutes;
