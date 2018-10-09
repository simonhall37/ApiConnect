import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Profile, ApiConnection} from '../profile/profile';
import {ApiService} from '../api.service';
import { HttpErrorResponse } from '@angular/common/http';

class Message {
  type: string;
  show: boolean;
  content: string;
}

@Component({
  selector: 'af-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],

})
export class ProfileComponent implements OnInit {

  @ViewChildren('cmp') profileNames:QueryList<ElementRef>;

  message: Message;
  profiles: Profile[] = [];
  newName: string;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.apiService.getAllProfiles().subscribe(
      (profiles) => {
        this.profiles = profiles;
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

  editProfile(profile: Profile){
    let index = this.profiles.indexOf(profile);
    if (profile.editMode === true){
      profile.editMode = !profile.editMode;
      this.update(profile);
    }
    else {
      // turn off edit mode for all other profiles (without submitting)
      for (var p of this.profiles){
        p.editMode=false;
      }
      profile.editMode = !profile.editMode;
      for (var er of this.profileNames.toArray()){
        if (er.nativeElement.id == index){
          er.nativeElement.focus();
        }
      }
    }
  }
  editConnection(connection:ApiConnection){
    connection.editMode = !connection.editMode;
  }

  handleError(err: HttpErrorResponse){
    this.message.type = "error";
    if (err.status === 404) this.message.content = "Error connecting to server";
    else this.message.content = err.message;
    this.message.show = true;
  }

  // REST calls
  // PUT
  updateProfile(event, profile:Profile) {
    if (event.keyCode === 13){
      profile.editMode = !profile.editMode;
      this.update(profile);
    }
  }
  updateConnection(event, connection:ApiConnection,profile:Profile) {
    if (event.keyCode === 13){
      connection.editMode = !connection.editMode;
      this.update(profile);
    }
  }

  update(profile: Profile){
    this.apiService.putProfile(profile).subscribe(
      (response) => {
        this.message.type = "info";
        this.message.content = "Profile updated successfully";
        this.message.show = true;
      },
      (err: HttpErrorResponse) => {
        this.handleError(err);
      }
    );
  }

  // DELETE
  removeProfile(element: Profile){
    this.apiService.deleteProfile(element).subscribe(
      (response) => {
        this.profiles.splice(this.profiles.indexOf(element),1);
        this.message.type = "info";
        this.message.content = "Profile deleted successfully";
        this.message.show = true;
      },
      (err: HttpErrorResponse) => {
        this.handleError(err);
      }
    );
  }
  removeConnection(element: Profile,connection: ApiConnection){
    var index = element.connections.indexOf(connection);
    if (index > -1) {
      element.connections.splice(index, 1);
    }
    this.update(element);
  }

  // POST
  addConnection(profile: Profile){
    profile.connections.push(new ApiConnection());
  }
  add(event){
    if (event.keyCode === 13){
      this.addProfile();
    }
  }
  addProfile(){
    let profile: Profile = new Profile();
    profile.name = this.newName;
    profile.connections = [];
    this.apiService.postProfile(profile).subscribe(
      (response) => {
        this.message.type = "info";
        this.message.content = "Profile created successfully";
        this.message.show = true;
        this.profiles.push(profile);
      },
      (err: HttpErrorResponse) => {
        this.handleError(err);
      }
    );
  }

}
