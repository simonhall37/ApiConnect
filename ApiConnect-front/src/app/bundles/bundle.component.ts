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
  selectedOrg: Org;
  loadingData: boolean = false;
  reload: boolean = false;
  Math = Math;
  newBundle: Bundle;
  showNewBundle: boolean = false;

  constructor(private bundleApiService: BundleApiService) {}

    ngOnInit() {
      this.newBundle = new Bundle();
      this.loadingData = true;
      this.bundleApiService.getAllOrgs(this.reload).subscribe(
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
    showAddBundleBoxes(){
      this.showNewBundle = !this.showNewBundle;
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
          this.bundles.splice(this.getBundleInsertPoint(this.selectedOrg.zendeskId),0,this.newBundle);
          let index = this.getOrgIndex(this.selectedOrg);
          if (index>-1) {
            this.orgs[index] = this.selectedOrg;
          }
          this.newBundle = new Bundle();
        }
        else {
          this.message.show = true;
          this.message.type = "error";
          this.message.content = "Bundle size must be greater than 0;";
        }
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

    reloadBundles(org: Org){
      org.bundles.forEach(
        (bundle) => {
          var ind = this.getBundleIndex(bundle);
          if (ind !== -1){
            for (let b of this.bundles){
              b.selected = false;
              if (this.bundles.indexOf(b) === ind){
                console.log("Replacing bundle " + bundle.orgZenId + " - " + bundle.bundleNum);
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
          this.bundleApiService.getgetOrgWithTickets(org.id,true).subscribe(
            (owt) => {
              this.updateOrg(owt);
              this.reloadBundles(owt);
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
