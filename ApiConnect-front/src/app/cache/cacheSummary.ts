import {Filter} from './filter';

export class Pair {
  key: string;
  value: string;
}

export class CacheSummary {
  name: string;
  path: string;
  profileName: string;
  source: string;
  updatedOn: string;
  filters: Filter[];
  params: Pair[];
  lookupSummaries: Pair[];
  editMode: boolean;
}
