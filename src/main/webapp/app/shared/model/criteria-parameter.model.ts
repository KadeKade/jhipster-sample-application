import { ICriteria } from 'app/shared/model/criteria.model';

export interface ICriteriaParameter {
  id?: number;
  parameterName?: string | null;
  parameterValue?: string | null;
  criteria?: ICriteria | null;
}

export const defaultValue: Readonly<ICriteriaParameter> = {};
