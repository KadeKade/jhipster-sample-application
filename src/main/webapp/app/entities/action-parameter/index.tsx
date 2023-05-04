import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ActionParameter from './action-parameter';
import ActionParameterDetail from './action-parameter-detail';
import ActionParameterUpdate from './action-parameter-update';
import ActionParameterDeleteDialog from './action-parameter-delete-dialog';

const ActionParameterRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ActionParameter />} />
    <Route path="new" element={<ActionParameterUpdate />} />
    <Route path=":id">
      <Route index element={<ActionParameterDetail />} />
      <Route path="edit" element={<ActionParameterUpdate />} />
      <Route path="delete" element={<ActionParameterDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ActionParameterRoutes;
