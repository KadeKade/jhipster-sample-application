import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AutomatedAction from './automated-action';
import AutomatedActionDetail from './automated-action-detail';
import AutomatedActionUpdate from './automated-action-update';
import AutomatedActionDeleteDialog from './automated-action-delete-dialog';

const AutomatedActionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AutomatedAction />} />
    <Route path="new" element={<AutomatedActionUpdate />} />
    <Route path=":id">
      <Route index element={<AutomatedActionDetail />} />
      <Route path="edit" element={<AutomatedActionUpdate />} />
      <Route path="delete" element={<AutomatedActionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AutomatedActionRoutes;
