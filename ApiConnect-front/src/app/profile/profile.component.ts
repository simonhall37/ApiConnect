import { Component, OnInit, ViewChild } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Profile, ApiConnection} from '../profile/profile';
import {ApiService} from '../api.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MatTable } from '@angular/material';

class Message {
  type: string;
  show: boolean;
  content: string;
}

@Component({
  selector: 'af-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ProfileComponent implements OnInit {

  message: Message;
  dataSource = [];
  columnsToDisplay = ['name'];
  expandedElement: Profile;
  counter: number = 0;

  @ViewChild('table') table: MatTable<any>;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.apiService.getAllProfiles().subscribe(
      (profiles) => {
        this.dataSource = profiles;
      }
    );
    this.message = new Message();
  }

  isError()  {
    if (this.message.type === "error") return true;
    else return false;
  }

  updateProfile(profile: Profile){
    // profile.name = profile.name + "  " + this.counter;
    this.apiService.putProfile(profile).subscribe(
      (response) => {
        this.table.renderRows();
        this.message.type = "info";
        this.message.content = "Profile updated successfully";
        this.message.show = true;
      },
      (err: HttpErrorResponse) => {
        this.message.type = "error";
        if (err.status === 404) this.message.content = "Error connecting to server";
        else this.message.content = err.message;
        this.message.show = true;
        console.log(this.message);
      }
    );
  }

  removeProfile(element: Profile){
    this.apiService.deleteProfile(element).subscribe(
      (response) => {
        var index = this.dataSource.indexOf(element);
        if (index > -1) {
          this.dataSource.splice(index, 1);
        }
        this.message.type = "info";
        this.message.content = "Profile deleted successfully";
        this.message.show = true;
      },
      (err: HttpErrorResponse) => {
        this.message.type = "error";
        if (err.status === 404) this.message.content = "Error connecting to server";
        else this.message.content = err.message;
        this.message.show = true;
      }
    );
    console.log(this.table.dataSource);
    this.table.renderRows();
  }

  removeConnection(element: Profile,connection: ApiConnection){
    var index = element.connections.indexOf(connection);
    if (index > -1) {
      element.connections.splice(index, 1);
    }
    this.updateProfile(element);
  }

}
