import { AutomatedActionType } from 'app/shared/model/enumerations/automated-action-type.model';

export interface IAutomatedAction {
  id?: number;
  type?: AutomatedActionType | null;
  positiveActionDefinition?: string | null;
  negativeActionDefinition?: string | null;
  displayNameDe?: string | null;
  displayNameEn?: string | null;
  displayNameFr?: string | null;
  displayNameIt?: string | null;
}

export const defaultValue: Readonly<IAutomatedAction> = {};
