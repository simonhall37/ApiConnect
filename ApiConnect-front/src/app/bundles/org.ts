import {Bundle} from './bundle';
import {Correction} from './correction';

export class Org {
  id: number;
  orgName: string;
  bundles : Bundle[];
  zendeskId: number;
  corrections: Correction[];
}
