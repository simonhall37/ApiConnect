import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import {Org} from './org';
import {Bundle} from './bundle';
import {Ticket} from './ticket';
import {BundleApiService} from './bundleApi.service';
import { HttpErrorResponse } from '@angular/common/http';

class Message {
  type: string;
  show: boolean;
  content: string;
}

@Component({
  selector: 'af-bundle',
  templateUrl: './bundle.component.html',
  styleUrls: ['./bundle.component.scss'],

})
export class BundleComponent implements OnInit  {

  message: Message;
  orgs: Org[] = [];
  bundles: Bundle[] = [];
  loadingData: boolean = false;

  constructor(private bundleApiService: BundleApiService) {}

    ngOnInit() {

      this.loadingData = true;
      this.bundleApiService.getAllOrgs(true).subscribe(
        (orgs) => {
          orgs.forEach(
            (o) => {
              this.orgs.push(o);
              o.bundles.forEach( (b) => {
                this.bundles.push(b);
              }
            );
            }
          );
          this.loadingData = false;
        }
      );

      this.message = new Message();
    }

    // general functions
    hideMessage(){
      this.message.show = false;
    }
    isError()  {
      if (this.message.type === "error") return true;
      else return false;
    }
    handleError(err: HttpErrorResponse){
      this.message.type = "error";
      if (err.status === 404) this.message.content = "Error connecting to server";
      else this.message.content = err.message;
      this.message.show = true;
    }


}
