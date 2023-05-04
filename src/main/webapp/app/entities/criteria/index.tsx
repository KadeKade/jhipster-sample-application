import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Criteria from './criteria';
import CriteriaDetail from './criteria-detail';
import CriteriaUpdate from './criteria-update';
import CriteriaDeleteDialog from './criteria-delete-dialog';

const CriteriaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Criteria />} />
    <Route path="new" element={<CriteriaUpdate />} />
    <Route path=":id">
      <Route index element={<CriteriaDetail />} />
      <Route path="edit" element={<CriteriaUpdate />} />
      <Route path="delete" element={<CriteriaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CriteriaRoutes;
