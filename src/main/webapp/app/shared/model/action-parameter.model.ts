import { ICriteria } from 'app/shared/model/criteria.model';

export interface IActionParameter {
  id?: number;
  parameterName?: string | null;
  parameterValue?: string | null;
  criterias?: ICriteria[] | null;
}

export const defaultValue: Readonly<IActionParameter> = {};
