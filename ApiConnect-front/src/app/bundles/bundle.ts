import {Ticket} from './ticket';

export class Bundle {
  id : number;
  startDate: string;
  endDate: string;
  balance: number;
  bundleNum: number;
  active: boolean;
  bundleSize: number;
  tickets: Ticket[];
}
