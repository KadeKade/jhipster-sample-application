import { ICriteria } from 'app/shared/model/criteria.model';
import { IAutomatedAction } from 'app/shared/model/automated-action.model';
import { IBrokerCategory } from 'app/shared/model/broker-category.model';

export interface ICriteriaSet {
  id?: number;
  name?: string | null;
  priority?: number | null;
  insurerId?: number | null;
  lobId?: number | null;
  criterias?: ICriteria[] | null;
  automatedAction?: IAutomatedAction | null;
  brokerCategories?: IBrokerCategory[] | null;
}

export const defaultValue: Readonly<ICriteriaSet> = {};
