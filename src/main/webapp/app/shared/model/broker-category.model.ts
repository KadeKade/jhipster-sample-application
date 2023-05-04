import { ICriteriaSet } from 'app/shared/model/criteria-set.model';

export interface IBrokerCategory {
  id?: number;
  displayName?: string | null;
  criteriaSets?: ICriteriaSet[] | null;
}

export const defaultValue: Readonly<IBrokerCategory> = {};
