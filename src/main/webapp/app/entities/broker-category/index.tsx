import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BrokerCategory from './broker-category';
import BrokerCategoryDetail from './broker-category-detail';
import BrokerCategoryUpdate from './broker-category-update';
import BrokerCategoryDeleteDialog from './broker-category-delete-dialog';

const BrokerCategoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<BrokerCategory />} />
    <Route path="new" element={<BrokerCategoryUpdate />} />
    <Route path=":id">
      <Route index element={<BrokerCategoryDetail />} />
      <Route path="edit" element={<BrokerCategoryUpdate />} />
      <Route path="delete" element={<BrokerCategoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BrokerCategoryRoutes;
