import {Filter} from './filter';

export class Pair {
  key: string;
  value: string;
  editMode: boolean = false;

  constructor(key: string, value: string){
    this.key = key;
    this.value = value;
  }
}

export class CacheSummary {
  name: string;
  path: string;
  profileName: string;
  source: string;
  updatedOn: string;
  size: number;
  filter: Filter;
  params: Pair[];
  lookupSummaries: Pair[];
  editMode: boolean;
  inProgress: boolean;
  disk: boolean = true;
}
