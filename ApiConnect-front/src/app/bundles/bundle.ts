import {Ticket} from './ticket';

export class Bundle {
  id : number;
  startDate: string;
  endDate: string;
  balance: number;
  bundleNum: number;
  orgName: string;
  active: boolean;
  bundleSize: number;
  tickets: Ticket[];
  visible: boolean = true;
  orgZenId: number;
  selected: boolean;
  firstTicketId: number;
  lastTicketId: number;
}
