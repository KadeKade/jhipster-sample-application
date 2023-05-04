import { ICriteriaParameter } from 'app/shared/model/criteria-parameter.model';
import { IActionParameter } from 'app/shared/model/action-parameter.model';
import { ICriteriaSet } from 'app/shared/model/criteria-set.model';
import { CriteriaType } from 'app/shared/model/enumerations/criteria-type.model';
import { Operator } from 'app/shared/model/enumerations/operator.model';
import { CriteriaDefinition } from 'app/shared/model/enumerations/criteria-definition.model';

export interface ICriteria {
  id?: number;
  priority?: number | null;
  criteriaActionType?: CriteriaType | null;
  operator?: Operator | null;
  criteriaDefinition?: CriteriaDefinition | null;
  criteriaParameters?: ICriteriaParameter[] | null;
  actionParameters?: IActionParameter[] | null;
  criteriaSet?: ICriteriaSet | null;
}

export const defaultValue: Readonly<ICriteria> = {};
