import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CriteriaSet from './criteria-set';
import CriteriaSetDetail from './criteria-set-detail';
import CriteriaSetUpdate from './criteria-set-update';
import CriteriaSetDeleteDialog from './criteria-set-delete-dialog';

const CriteriaSetRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CriteriaSet />} />
    <Route path="new" element={<CriteriaSetUpdate />} />
    <Route path=":id">
      <Route index element={<CriteriaSetDetail />} />
      <Route path="edit" element={<CriteriaSetUpdate />} />
      <Route path="delete" element={<CriteriaSetDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CriteriaSetRoutes;
