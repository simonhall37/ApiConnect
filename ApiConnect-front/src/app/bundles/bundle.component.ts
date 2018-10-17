import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import {Org} from './org';
import {Bundle} from './bundle';
import {Ticket} from './ticket';
import {BundleApiService} from './bundleApi.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Correction } from './correction';

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
  selectedOrg: Org;
  loadingData: boolean = false;
  reload: boolean = false;
  Math = Math;
  newBundle: Bundle;
  showNewBundle: boolean = false;
  newCorrection: Correction;
  showNewCorrection: boolean;

  constructor(private bundleApiService: BundleApiService) {}

    ngOnInit() {
      this.newBundle = new Bundle();
      this.newCorrection = new Correction();
      this.getAll(this.reload);
      this.message = new Message();
    }

    getAll(reload: boolean){
      this.loadingData = true;
      this.bundles = [];
      this.orgs = [];
      this.bundleApiService.getAllOrgs(reload).subscribe(
        (orgs) => {
          orgs.forEach(
            (o) => {
              this.orgs.push(o);
              o.bundles.forEach( (b) => {
                b.visible = true;
                b.selected = false;
                this.bundles.push(b);
              }
            );
            }
          );
          this.loadingData = false;
        }
      );
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
    showAddBundleBoxes(){
      this.showNewBundle = !this.showNewBundle;
    }
    showAddCorrectionBoxes(){
      this.showNewCorrection = !this.showNewCorrection;
    }
    removeTicket(bundle: Bundle,ticket: Ticket){
      let index = bundle.tickets.indexOf(ticket);
      if (index>-1){
        bundle.tickets.splice(index,1);
        this.bundleApiService.putOrg(this.selectedOrg).subscribe(
          (org: Org) => {
            console.log(org);
            this.updateOrg(org);
            this.reloadBundles(org,true);
            this.selectedOrg = org;
          },
          (err: HttpErrorResponse) => {
            this.handleError(err);
          }
        );
      }
    }
    addCorrection(event){
      if (event.keyCode === 13){
        this.newCorrection.zenOrgId = this.selectedOrg.zendeskId;
        this.selectedOrg.corrections.push(this.newCorrection);
        this.newCorrection = new Correction();
      }
    }
    addBundle(event){
      if (event.keyCode === 13){
        if (this.newBundle.bundleSize > 0){
          this.newBundle.bundleNum = this.selectedOrg.bundles.length+1;
          this.newBundle.balance=0;
          this.newBundle.orgZenId = this.selectedOrg.zendeskId;
          this.newBundle.orgName = this.selectedOrg.orgName;
          this.newBundle.selected = true;
          this.selectedOrg.bundles.push(this.newBundle);
          if (this.newBundle.active){
            for (let bundle of this.selectedOrg.bundles){
              bundle.active=false;
            }
            this.newBundle.active = true;
          } else {
            this.newBundle.active = false;
          }
          this.updateModel(this.selectedOrg);
          this.newBundle = new Bundle();
        }
        else {
          this.message.show = true;
          this.message.type = "error";
          this.message.content = "Bundle size must be greater than 0;";
        }
      }
    }
    updateModel(org: Org){
      this.bundles.splice(this.getBundleInsertPoint(org.zendeskId),0,this.newBundle);
      let index = this.getOrgIndex(org);
      if (index>-1) {
        this.orgs[index] = org;
      }
    }
    getOrgByZenId(zenId: number): Org {
      for (let o of this.orgs){
        if (o.zendeskId === zenId) {
          return o;
        }
      }
      console.log("Couldn't find org with id " + zenId);
      return null;
    }

    getOrgIndex(org:Org) : number{
    for (let o of this.orgs){
        if (o.zendeskId === org.zendeskId){
          return this.orgs.indexOf(o);
        }
      }
      return -1;
    }

    getBundleIndex(bundle: Bundle):number {
      for (let b of this.bundles){
        if (b.id === bundle.id) return this.bundles.indexOf(b);
      }
      return -1;
    }

    getBundleInsertPoint(zenId:number):number {
      var index:number = -1;
      for (let b of this.bundles){
        if (b.orgZenId === zenId) index = this.bundles.indexOf(b)+1;
      }
      return index;
    }

    updateOrg(org:Org){
      var ind = this.getOrgIndex(org);
      if (ind !== -1){
        console.log("Replacing org " + org.zendeskId);
        this.orgs[ind] = org;
      }
      else {
        console.log("Couldn't replace org " + org.zendeskId);
      }
    }

    reloadBundles(org: Org, select:boolean){
      org.bundles.forEach(
        (bundle) => {
          var ind = this.getBundleIndex(bundle);
          if (ind !== -1){
            for (let b of this.bundles){
              if (!select) b.selected = false;
              if (this.bundles.indexOf(b) === ind){
                console.log("Replacing bundle " + bundle.orgZenId + " - " + bundle.bundleNum);
                if (select){
                  bundle.visible = true;
                  bundle.selected = true;
                }
                this.bundles[ind] = bundle;
              }
            }

          }
          else console.log("Can't find bundle " + bundle.orgZenId + " - " + bundle.bundleNum);
        }
      );
    }

    hideAll(){
      this.bundles.forEach(
        (b) => {
          b.visible=false;
          b.selected = false;
        }
      );
    }

    showAll(){
      this.bundles.forEach(
        (b) => {
          b.visible=true;
          b.selected = false;
        }
      );
    }

    // load the tickets for a specific bundle
    loadTickets(bundle: Bundle){
      if (bundle.selected) {
        this.showAll();
        this.selectedOrg = null;
      }
      else {
        var org = this.getOrgByZenId(bundle.orgZenId);
        if (org!=null){
          this.bundleApiService.getOrgWithTickets(org.id,true).subscribe(
            (owt) => {
              this.updateOrg(owt);
              this.reloadBundles(owt,false);
              this.selectedOrg = owt;
              this.hideAll();
              for (let updateSel of owt.bundles){
                updateSel.selected=true;
                updateSel.visible = true;
              }
            }
          );
        }
      }


    }

}
